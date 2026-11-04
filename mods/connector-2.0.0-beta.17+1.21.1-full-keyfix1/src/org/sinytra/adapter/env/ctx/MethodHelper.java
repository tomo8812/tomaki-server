package org.sinytra.adapter.env.ctx;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.params.EnhancedParamsDiff;
import org.sinytra.adapter.analysis.params.LayeredParamsDiffSnapshot;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;
import org.sinytra.adapter.env.MockMixinRuntime;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.env.param.ParamDiffResolver;
import org.sinytra.adapter.env.param.Parameters;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;
import org.sinytra.adapter.util.provider.ClassLookup;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.code.MethodSlice;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.util.Locals;

public class MethodHelper {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MixinContext context;
   private final MethodFinder methodFinder;
   private final Map<TargetPair, List<AbstractInsnNode>> targetInstructionsCache = new HashMap<>();

   public MethodHelper(MixinContext context, List<Type> targetTypes) {
      this.context = context;
      String singleTargetClass = targetTypes.getFirst().getInternalName();
      this.methodFinder = new MethodFinder(context.environment(), singleTargetClass);
   }

   @Nullable
   public MethodNode findInheritedMethod(ClassLookup lookup, MethodQualifier qualifier) {
      return Optional.ofNullable(this.methodFinder.findInheritedMethod(lookup, qualifier)).map(TargetPair::methodNode).orElse(null);
   }

   @Nullable
   public MethodNode findOwnMethod(ClassLookup lookup, MethodQualifier qualifier) {
      return Optional.ofNullable(this.findOwnMethodPair(lookup, qualifier)).map(TargetPair::methodNode).orElse(null);
   }

   @Nullable
   public TargetPair findMethodPair(ClassLookup lookup, MethodQualifier qualifier) {
      return this.methodFinder.findMethod(lookup, qualifier, 0);
   }

   @Nullable
   public TargetPair findOwnMethodPair(ClassLookup lookup, MethodQualifier qualifier) {
      return this.methodFinder.findMethod(lookup, qualifier, 2);
   }

   @Nullable
   public Pair<ClassNode, List<MethodNode>> findOwnMethodsByName(ClassLookup lookup, MethodQualifier qualifier) {
      return this.methodFinder.findMethods(lookup, qualifier, 3);
   }

   public boolean hasInjectionTargetInsns(@Nullable TargetPair target) {
      return !this.findInjectionTargetInsns(target).isEmpty();
   }

   @Nullable
   public AbstractInsnNode findInjectionTargetInsn(@Nullable TargetPair target) {
      List<AbstractInsnNode> cleanInsns = this.findInjectionTargetInsns(target);
      return cleanInsns.size() != 1 ? null : cleanInsns.getFirst();
   }

   public List<AbstractInsnNode> findInjectionTargetInsns(@Nullable TargetPair target) {
      return this.findInjectionTargetInsns(target, false);
   }

   public List<AbstractInsnNode> findInjectionTargetInsns(@Nullable TargetPair target, boolean ignoreOrdinal) {
      return this.targetInstructionsCache
         .computeIfAbsent(target, t -> this.computeInjectionTargetInsns(target, this.context::injectionPointAnnotation, (ctx, h) -> {
            AnnotationHandle atAnn = h;
            if (ignoreOrdinal) {
               atAnn = atAnn.copy();
               atAnn.removeValues("ordinal");
            }

            return InjectionPoint.parse(ctx, this.context.methodNode(), this.context.methodAnnotation().unwrap(), atAnn.unwrap());
         }, true));
   }

   @Nullable
   public AbstractInsnNode findInjectionTargetInsn(@Nullable TargetPair target, AtData atData) {
      List<AbstractInsnNode> cleanInsns = this.computeInjectionTargetInsns(target, this.context::injectionPointAnnotation, (ctx, h) -> {
         AnnotationNode atCopy = atData.toAnnotationNode();
         AnnotationHandle annCopy = this.context.methodAnnotation().copy();
         annCopy.setOrAppendNonNull("at", atCopy);
         return InjectionPoint.parse(ctx, this.context.methodNode(), annCopy.unwrap(), atCopy);
      }, true);
      return cleanInsns.size() != 1 ? null : cleanInsns.getFirst();
   }

   public List<Type> resolveCapturedMethodParams(Configuration clean, Configuration dirty) {
      List<Type> cleanCaptured = clean.getParameters().getTypes(MethodParameters.ParamGroup.CAPTURED_PARAMS);
      List<Type> dirtyCaptured = new ArrayList<>();
      if (!cleanCaptured.isEmpty()) {
         MethodNode cleanTarget = this.findOwnMethod(this.context.cleanLookup(), clean.getTargetMethod());
         MethodNode dirtyTarget = this.findOwnMethod(this.context.dirtyLookup(), dirty.getTargetMethod());
         if (cleanTarget == null || dirtyTarget == null) {
            return dirtyCaptured;
         }

         LayeredParamsDiffSnapshot diff = EnhancedParamsDiff.compareMethodParameters(cleanTarget, dirtyTarget);
         List<Type> cleanTargetParams = Parameters.getParameterTypes(cleanTarget.desc);
         List<Type> capturedSublist = cleanTargetParams.subList(0, cleanCaptured.size());
         if (cleanCaptured.size() > cleanTargetParams.size() || !cleanCaptured.equals(capturedSublist)) {
            return dirtyCaptured;
         }

         ParamDiffResolver.ParamEvalResult evalResult = ParamDiffResolver.resolve(cleanTargetParams, diff);
         int maxIndex = capturedSublist.stream()
            .map(evalResult::getUpdated)
            .filter(Objects::nonNull)
            .mapToInt(ParamDiffResolver.ParamState::dirtyIndex)
            .max()
            .orElse(-1);
         if (maxIndex != -1) {
            List<Type> dirtyTargetParams = Parameters.getParameterTypes(dirtyTarget.desc);
            dirtyCaptured = List.copyOf(dirtyTargetParams.subList(0, maxIndex + 1));
         }
      }

      return dirtyCaptured;
   }

   @Nullable
   public List<AbstractInsnNode> computeInjectionTargetInsns(
      @Nullable TargetPair target,
      Supplier<AnnotationHandle> atNodeSupplier,
      BiFunction<IMixinContext, AnnotationHandle, InjectionPoint> injectionPointParser,
      boolean ignoreShift
   ) {
      return this.computeInjectionTargetInsns(target, atNodeSupplier, injectionPointParser, ignoreShift, false);
   }

   @Nullable
   public List<AbstractInsnNode> computeInjectionTargetInsns(
      @Nullable TargetPair target,
      Supplier<AnnotationHandle> atNodeSupplier,
      BiFunction<IMixinContext, AnnotationHandle, InjectionPoint> injectionPointParser,
      boolean ignoreShift,
      boolean ignoreSlice
   ) {
      if (target == null) {
         return List.of();
      }

      AnnotationHandle atNode = atNodeSupplier.get();
      if (atNode == null) {
         return List.of();
      }

      AnnotationHandle atNodeCopy = atNode.copy();
      if (ignoreShift) {
         atNodeCopy.removeValues("shift");
      }

      PatchContext patchContext = this.context.patchContext();
      IMixinContext mixinContext = MockMixinRuntime.forClass(this.context.classNode().name, target.classNode().name, patchContext.environment());
      InjectionPoint injectionPoint = injectionPointParser.apply(mixinContext, atNodeCopy);
      Target mixinTarget = MockMixinRuntime.createMixinTarget(target);
      InsnList instructions = ignoreSlice
         ? target.methodNode().instructions
         : this.getSlicedInsns(
            this.context.methodAnnotation(),
            this.context.classNode(),
            this.context.methodNode(),
            target.classNode(),
            target.methodNode(),
            patchContext,
            mixinTarget
         );
      List<AbstractInsnNode> targetInsns = new ArrayList<>();

      try {
         if (MockMixinRuntime.injectionPointNeedsSpecialCare(injectionPoint)) {
            MockMixinRuntime.findModifyVariableInjectionInsns(injectionPoint, mixinContext, target.methodNode().instructions, targetInsns, mixinTarget);
         } else {
            injectionPoint.find(target.methodNode().desc, instructions, targetInsns);
         }

         return targetInsns;
      } catch (Throwable e) {
         LOGGER.error("Error finding injection insns", e);
         return List.of();
      }
   }

   private InsnList getSlicedInsns(
      AnnotationHandle parentAnnotation,
      ClassNode classNode,
      MethodNode injectorMethod,
      ClassNode targetClass,
      MethodNode targetMethod,
      PatchContext context,
      Target mixinTarget
   ) {
      return parentAnnotation.getValue("slice").map(handle -> {
         Object value = handle.get();
         return value instanceof List<?> list ? (AnnotationNode)list.getFirst() : (AnnotationNode)value;
      }).map(sliceAnn -> {
         IMixinContext mixinContext = MockMixinRuntime.forClass(classNode.name, targetClass.name, context.environment());
         ISliceContext sliceContext = MockMixinRuntime.forSlice(mixinContext, injectorMethod);
         return this.computeSlicedInsns(sliceContext, sliceAnn, mixinTarget);
      }).orElse(targetMethod.instructions);
   }

   private InsnList computeSlicedInsns(ISliceContext context, AnnotationNode annotation, Target mixinTarget) {
      MethodSlice slice = MethodSlice.parse(context, annotation);
      return slice.getSlice(mixinTarget);
   }

   @Nullable
   public List<LocalVariable> getTargetMethodLocals(TargetPair target) {
      Type[] targetParams = Type.getArgumentTypes(target.methodNode().desc);
      boolean isStatic = isStatic(this.context.methodNode());
      int lvtOffset = isStatic ? 0 : 1;
      int targetLocalPos = targetParams.length + lvtOffset;
      return this.getTargetMethodLocals(target, targetLocalPos);
   }

   @Nullable
   public List<LocalVariable> getTargetMethodLocals(TargetPair target, int startPos) {
      return this.getTargetMethodLocals(target, startPos, this.context.environment().fabricLVTCompatibility());
   }

   @Nullable
   public List<LocalVariable> getTargetMethodLocals(TargetPair target, int startPos, int lvtCompatLevel) {
      List<AbstractInsnNode> targetInsns = this.findInjectionTargetInsns(target);
      if (targetInsns.isEmpty()) {
         LOGGER.debug("Skipping LVT patch, no target instructions found");
         return null;
      }

      LocalVariableNode[] localVariables;
      synchronized (this) {
         localVariables = Locals.getLocalsAt(target.classNode(), target.methodNode(), targetInsns.getFirst(), lvtCompatLevel);
      }

      LocalVariable[] locals = Stream.of(localVariables)
         .filter(Objects::nonNull)
         .map(lv -> new LocalVariable(lv.index, Type.getType(lv.desc)))
         .toArray(LocalVariable[]::new);
      return AdapterUtil.summariseLocals(locals, startPos);
   }

   public static boolean isStatic(MethodNode node) {
      return (node.access & 8) != 0;
   }
}
