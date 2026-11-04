package org.sinytra.adapter.patch;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.MixinGroups;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;
import org.sinytra.adapter.env.ann.ClassTarget;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchContext;
import org.sinytra.adapter.env.ctx.PatchContextImpl;
import org.sinytra.adapter.env.ctx.PatchEnvironment;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.PropertyContainerTemplate;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.mixin.MixinFlag;
import org.sinytra.adapter.patch.mixin.MixinType;
import org.sinytra.adapter.transform.ClassTransformer;
import org.sinytra.adapter.transform.MethodTransformer;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;
import org.slf4j.Logger;

public class Patcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PatchEnvironment environment;
   private final List<ClassTransformer> classPatches;
   private final Multimap<TxPhase, MethodTransformer> methodTransformers;

   private Patcher(PatchEnvironment environment, List<ClassTransformer> classPatches, Multimap<TxPhase, MethodTransformer> methodTransformers) {
      this.environment = environment;
      this.classPatches = classPatches;
      this.methodTransformers = methodTransformers;
   }

   public PatchResult process(ClassNode classNode) {
      ClassTarget classTarget = MixinParser.prepareMixinClass(classNode, this.environment);
      PatchResult result = PatchResult.PASS;
      PatchContextImpl context = new PatchContextImpl(classNode, classTarget.getTypes(), this.environment);

      for (ClassTransformer patch : this.classPatches) {
         result = result.or(patch.apply(classNode, classTarget, context));
      }

      MixinParser.MixinClassHandle mixinClass = MixinParser.parseMixins(classTarget, classNode, this.environment);
      if (mixinClass == null) {
         return result;
      }

      for (MixinParser.MixinMethodHandle mixin : mixinClass.mixins()) {
         PatchResult subResult = this.processMixin(classNode, classTarget, context, mixin);
         result = result.or(subResult);
      }

      context.run();
      MixinGroups groups = MixinGroups.create(mixinClass.mixins().stream().map(MixinParser.MixinMethodHandle::methodNode).toList());
      this.environment.auditTrail().processGroups(classNode, groups);
      return result;
   }

   private PatchResult processMixin(ClassNode classNode, ClassTarget classTarget, PatchContext patchContext, MixinParser.MixinMethodHandle mixin) {
      MethodQualifier target = mixin.properties().getProperty(MixinKeys.TARGET_METHOD).orElse(null);
      if (target == null) {
         return PatchResult.PASS;
      }

      AnnotationHandle atHandle = mixin.methodAnnotation().getNested("at").orElse(null);
      MixinType mixinType = mixin.mixinType();
      Set<MixinFlag> flags = mixinType.getFlags();
      MixinContext mixinContext = new MixinContext(
         mixinType, patchContext, classTarget, classNode, mixin.methodNode(), mixin.methodAnnotation(), atHandle, flags
      );
      String mixinId = mixinContext.getMixinId();
      PropertyContainerTemplate template = mixin.mixinType().getConfigurationTemplate();
      MutableConfiguration configuration = MutableConfiguration.create(template);
      configuration.mergeFrom(mixin.properties());
      this.environment.auditTrail().prepareMethod(mixinContext);
      PatchResult result = PatchResult.PASS;

      for (MethodTransformer transformer : this.getTransformers(TxPhase.EARLY)) {
         mixinContext.pushAudit(transformer);
         PatchResult txResult = transformer.apply(mixinContext, configuration);
         mixinContext.popAudit();
         result = result.or(txResult);
      }

      TxResult preResult = mixinType.preProcess(mixinContext, configuration, mixinContext.getResolvers(), mixinContext.getProcessors());
      if (preResult == TxResult.FAIL) {
         LOGGER.debug(AdapterUtil.MIXINPATCH, "Skipping mixin {} due to failed preProcess", mixinId);
         return PatchResult.PASS;
      }

      for (MethodTransformer transformer : this.getTransformers(TxPhase.LOADED)) {
         mixinContext.pushAudit(transformer);
         PatchResult txResult = transformer.apply(mixinContext, configuration);
         mixinContext.popAudit();
         result = result.or(txResult);
      }

      if (!configuration.validate()) {
         LOGGER.debug(AdapterUtil.MIXINPATCH, "Skipping mixin {} due to invalid CLEAN config", mixinId);
         return PatchResult.PASS;
      }

      MethodNode methodNode = mixinContext.methodNode();
      methodNode.maxLocals = methodNode.maxStack = 999;

      for (MethodTransformer transformer : this.getTransformers(TxPhase.VALIDATED)) {
         mixinContext.pushAudit(transformer);
         PatchResult txResult = transformer.apply(mixinContext, configuration);
         mixinContext.popAudit();
         result = result.or(txResult);
      }

      return result;
   }

   private Collection<MethodTransformer> getTransformers(TxPhase phase) {
      return this.methodTransformers.get(phase);
   }

   public static Patcher.Builder builder(PatchEnvironment environment) {
      return new Patcher.Builder(environment);
   }

   public static class Builder {
      private final PatchEnvironment environment;
      private final List<ClassTransformer> classTransformers = new ArrayList<>();
      private final Multimap<TxPhase, MethodTransformer> methodTransformers = HashMultimap.create();

      public Builder(PatchEnvironment environment) {
         this.environment = environment;
      }

      public Patcher.Builder classTransformer(ClassTransformer transformer) {
         this.classTransformers.add(transformer);
         return this;
      }

      public Patcher.Builder classTransformers(List<ClassTransformer> transformers) {
         this.classTransformers.addAll(transformers);
         return this;
      }

      public Patcher.Builder methodTransformer(TxPhase phase, MethodTransformer transformer) {
         this.methodTransformers.put(phase, transformer);
         return this;
      }

      public Patcher.Builder methodTransformers(Multimap<TxPhase, MethodTransformer> transformers) {
         this.methodTransformers.putAll(transformers);
         return this;
      }

      public Patcher build() {
         return new Patcher(this.environment, this.classTransformers, this.methodTransformers);
      }
   }
}
