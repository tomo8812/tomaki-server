package org.sinytra.adapter.patch.resolver.special;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.MockMixinRuntime;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ann.SliceData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.resolver.Resolver;
import org.sinytra.adapter.util.MethodQualifier;
import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.code.MethodSlice;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.refmap.IMixinContext;

public class SliceBoundaryResolver implements Resolver {
   @Override
   public Resolver.ResolutionResult resolve(MixinContext context, Recipe recipe) {
      TargetPair dirtyTarget = recipe.getDirtyTarget();
      SliceData slice = recipe.clean().getProperty(MixinKeys.SLICE).orElse(null);
      if (slice != null && !passesSliceCheck(slice, context, dirtyTarget)) {
         SliceData fixedSlice = fixSliceData(slice, dirtyTarget.methodNode());
         if (fixedSlice == null) {
            return Resolver.ResolutionResult.pass();
         }

         Configuration patch = MutableConfiguration.create().setProperty(MixinKeys.SLICE, fixedSlice);
         return Resolver.ResolutionResult.success(patch);
      } else {
         return Resolver.ResolutionResult.pass();
      }
   }

   @Nullable
   private static SliceData fixSliceData(SliceData slice, MethodNode dirtyMethod) {
      AtData from = slice.from();
      if (from != null) {
         from = fixSlideInjectionPoint(from, dirtyMethod).orElse(null);
         if (from == null) {
            return null;
         }
      }

      AtData to = slice.to();
      if (to != null) {
         to = fixSlideInjectionPoint(to, dirtyMethod).orElse(null);
         if (to == null) {
            return null;
         }
      }

      return new SliceData(from, to);
   }

   private static Optional<AtData> fixSlideInjectionPoint(AtData boundary, MethodNode dirtyMethod) {
      if (!"INVOKE".equals(boundary.getValue())) {
         return Optional.empty();
      }

      MethodQualifier target = boundary.getTarget().flatMap(MethodQualifier::parse).orElse(null);
      if (target == null) {
         return Optional.empty();
      }

      List<MethodInsnNode> candidates = new ArrayList<>();

      for (AbstractInsnNode insn : dirtyMethod.instructions) {
         if (insn instanceof MethodInsnNode minsn && minsn.name.equals(target.name())) {
            candidates.add(minsn);
         }
      }

      if (candidates.size() != 1) {
         return Optional.empty();
      }

      MethodInsnNode insn = candidates.getFirst();
      return Optional.of(boundary.withTarget(insn));
   }

   private static boolean passesSliceCheck(SliceData slice, MixinContext context, TargetPair dirtyTarget) {
      Target mixinTarget = MockMixinRuntime.createMixinTarget(dirtyTarget);
      IMixinContext mixinContext = MockMixinRuntime.forClass(context.classNode().name, dirtyTarget.classNode().name, context.patchContext().environment());
      ISliceContext sliceContext = MockMixinRuntime.forSlice(mixinContext, context.methodNode());
      MethodSlice methodSlice = MethodSlice.parse(sliceContext, slice.toAnnotationNode());
      InsnList insns = methodSlice.getSlice(mixinTarget);
      return insns.size() != dirtyTarget.methodNode().instructions.size();
   }
}
