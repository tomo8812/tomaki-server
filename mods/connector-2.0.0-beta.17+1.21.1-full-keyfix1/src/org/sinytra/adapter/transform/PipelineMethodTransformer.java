package org.sinytra.adapter.transform;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ann.SliceData;
import org.sinytra.adapter.env.ctx.AuditTrail;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.key.ControlKeys;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.mixin.MixinType;
import org.sinytra.adapter.patch.mixin.MixinTypes;
import org.sinytra.adapter.patch.processor.Processor;
import org.sinytra.adapter.patch.processor.Processors;
import org.sinytra.adapter.patch.resolver.Resolver;
import org.sinytra.adapter.patch.resolver.Resolvers;
import org.sinytra.adapter.transform.patch.MethodPatch;
import org.sinytra.adapter.transform.patch.MethodPatchResolver;
import org.sinytra.adapter.util.AdapterUtil;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.points.BeforeConstant;

public class PipelineMethodTransformer implements MethodTransformer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MethodPatchResolver patchResolver;
   private final boolean patchesOnly;

   public PipelineMethodTransformer(List<MethodPatch> methodPatches, boolean patchesOnly) {
      this.patchResolver = new MethodPatchResolver(methodPatches);
      this.patchesOnly = patchesOnly;
   }

   @Override
   public PatchResult apply(MixinContext context, Configuration config) {
      TargetPair cleanTarget = context.methods().findOwnMethodPair(context.cleanLookup(), config.getTargetMethod());
      if (cleanTarget == null) {
         return PatchResult.PASS;
      } else {
         TargetPair dirtyTarget = context.methods().findOwnMethodPair(context.dirtyLookup(), config.getTargetMethod());
         if (!this.patchResolver.matches(config)
            && !this.failsDirtyInjectionCheck(context, config, dirtyTarget)
            && this.hasValidSlice(context, config, dirtyTarget)) {
            return PatchResult.PASS;
         } else {
            LOGGER.debug(AdapterUtil.MIXINPATCH, "Considering method {}", context.getMixinId());
            AuditTrail auditTrail = context.environment().auditTrail();
            auditTrail.recordResult(context, config, AuditTrail.Match.NONE);
            PatchResult result = this.execute(context, config);
            if (result != PatchResult.PASS) {
               auditTrail.recordResult(context, config, AuditTrail.Match.FULL);
               return result;
            } else {
               return PatchResult.PASS;
            }
         }
      }
   }

   private PatchResult execute(MixinContext context, Configuration config) {
      String mixinId = context.getMixinId();
      Resolvers resolvers = this.patchesOnly ? new Resolvers(false) : context.getResolvers();
      Processors processors = context.getProcessors();
      resolvers.addFirst(this.patchResolver);
      MutableConfiguration cleanConfig = config.copy();
      MutableConfiguration dirtyConfig = cleanConfig.childConfig();
      dirtyConfig.inheritMixinType();
      dirtyConfig.inheritTargetClass();
      Recipe recipe = new Recipe(cleanConfig, dirtyConfig, resolvers, processors, context);
      resolvers.freeze();

      for (Resolver resolver : resolvers.getAll()) {
         context.pushAudit(resolver);
         Resolver.ResolutionResult res = resolver.resolve(context, recipe);
         context.popAudit();
         Objects.requireNonNull(res, "BUG: Received null from resolver " + resolver.getClass());
         if (res.type() != Resolver.ResultType.SUCCESS && res.type() != Resolver.ResultType.REPLACE) {
            if (res.type() == Resolver.ResultType.FAIL) {
               LOGGER.debug(AdapterUtil.MIXINPATCH, "Skipping mixin {} due to failed RESOLVER {}", mixinId, resolver.getClass().getSimpleName());
               return PatchResult.PASS;
            }
         } else {
            if (res.type() == Resolver.ResultType.REPLACE) {
               dirtyConfig = res.patch().copy();
               break;
            }

            dirtyConfig.mergeFrom(res.patch());
         }
      }

      if (dirtyConfig.hasProperty(ControlKeys.MIXIN_TYPE)) {
         String type = dirtyConfig.getMixinType();
         MixinType lateMixinType = MixinTypes.getMixinType(Type.getType(type).getInternalName());
         if (lateMixinType != null) {
            TxResult postResult = lateMixinType.postProcess(context, cleanConfig, dirtyConfig, recipe);
            if (postResult == TxResult.FAIL) {
               LOGGER.debug(AdapterUtil.MIXINPATCH, "Skipping mixin {} due to failed postProcess", mixinId);
               return PatchResult.PASS;
            }
         }
      }

      if (!dirtyConfig.validate()) {
         LOGGER.debug(AdapterUtil.MIXINPATCH, "Skipping mixin {} due to invalid DIRTY config", mixinId);
         return PatchResult.PASS;
      }

      processors.freeze();

      for (Processor processor : processors.getAll()) {
         context.pushAudit(processor);
         TxResult res = processor.process(context, dirtyConfig, recipe);
         context.popAudit();
         if (res == TxResult.FINALIZE) {
            break;
         }

         if (res == TxResult.FAIL) {
            LOGGER.debug(AdapterUtil.MIXINPATCH, "Skipping mixin {} due to failed PROCESSOR {}", mixinId, processor.getClass().getSimpleName());
            return PatchResult.PASS;
         }
      }

      return PatchResult.APPLY;
   }

   public boolean failsDirtyInjectionCheck(MixinContext context, Configuration config, TargetPair dirtyTarget) {
      return !context.getMixinType().canInject(context, config)
         || dirtyTarget == null
         || !context.methods().hasInjectionTargetInsns(dirtyTarget) && this.computeConstantTargetInsns(context, dirtyTarget).isEmpty();
   }

   public List<AbstractInsnNode> computeConstantTargetInsns(MixinContext context, @Nullable TargetPair target) {
      return context.methods()
         .computeInjectionTargetInsns(
            target,
            () -> context.methodAnnotation().getNested("constant").orElse(null),
            (ctx, h) -> new BeforeConstant(ctx, h.unwrap(), Type.getReturnType(context.methodNode().desc).getDescriptor()),
            false
         );
   }

   public boolean hasValidSlice(MixinContext context, Configuration config, @Nullable TargetPair target) {
      if (target == null) {
         return false;
      }

      SliceData slice = config.getProperty(MixinKeys.SLICE).orElse(null);
      if (slice == null) {
         return true;
      }

      AtData from = slice.from();
      if (from != null && !this.validateAtNode(context, from, target)) {
         return false;
      }

      AtData to = slice.to();
      return to == null || this.validateAtNode(context, to, target);
   }

   private boolean validateAtNode(MixinContext context, AtData at, TargetPair target) {
      List<AbstractInsnNode> insns = context.methods()
         .computeInjectionTargetInsns(
            target,
            context::injectionPointAnnotation,
            (ctx, h) -> InjectionPoint.parse(ctx, context.methodNode(), context.methodAnnotation().unwrap(), at.toAnnotationNode()),
            false,
            true
         );
      return !insns.isEmpty();
   }
}
