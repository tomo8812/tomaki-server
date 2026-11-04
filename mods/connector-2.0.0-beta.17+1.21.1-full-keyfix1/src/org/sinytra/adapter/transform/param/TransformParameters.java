package org.sinytra.adapter.transform.param;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.patch.mixin.MixinFlag;

public record TransformParameters(List<ParameterTransformer> transformers, boolean withOffset) {
   public PatchResult apply(MixinContext context) {
      ClassNode classNode = context.classNode();
      MethodNode methodNode = context.methodNode();
      Type[] params = Type.getArgumentTypes(methodNode.desc);
      List<Type> newParameterTypes = new ArrayList<>(Arrays.asList(params));
      PatchResult result = PatchResult.PASS;
      int offset = this.calculateOffset(context);

      for (ParameterTransformer transform : this.transformers) {
         result = result.or(transform.apply(classNode, methodNode, context, newParameterTypes, offset));
      }

      if (result != PatchResult.PASS) {
         this.updateDescription(context, methodNode, newParameterTypes);
      }

      return result;
   }

   public boolean isEmpty() {
      return this.transformers.isEmpty();
   }

   private void updateDescription(MixinContext context, MethodNode methodNode, List<Type> parameters) {
      Type returnType = Type.getReturnType(methodNode.desc);
      methodNode.desc = Type.getMethodDescriptor(returnType, parameters.toArray(Type[]::new));
      methodNode.signature = null;
      context.recordCtxAudit("Change descriptor to %s", methodNode.desc);
   }

   private int calculateOffset(MixinContext context) {
      boolean needsLocalOffset = context.hasFlag(MixinFlag.ACCEPTS_INSTANCE);
      return !context.isStatic() && this.withOffset && needsLocalOffset ? 1 : 0;
   }

   public static TransformParameters.Builder builder() {
      return new TransformParameters.Builder();
   }

   @CanIgnoreReturnValue
   public static class Builder {
      private final List<ParameterTransformer> transformers = new ArrayList<>();
      private boolean offset = false;

      public TransformParameters.Builder transform(ParameterTransformer transformer) {
         this.transformers.add(transformer);
         return this;
      }

      public TransformParameters.Builder transform(List<ParameterTransformer> transformers) {
         this.transformers.addAll(transformers);
         return this;
      }

      public TransformParameters.Builder inject(int parameterIndex, Type type) {
         return this.transform(new InjectParameterTransform(parameterIndex, type));
      }

      public TransformParameters.Builder replace(int index, Type type) {
         return this.transform(new ReplaceParametersTransformer(index, type));
      }

      public TransformParameters.Builder replacements(List<Pair<Integer, Type>> replacements) {
         replacements.forEach(p -> this.replace((Integer)p.getFirst(), (Type)p.getSecond()));
         return this;
      }

      public TransformParameters.Builder swap(int from, int to) {
         return this.transform(new SwapParametersTransformer(from, to));
      }

      public TransformParameters.Builder swaps(List<Pair<Integer, Integer>> swaps) {
         swaps.forEach(p -> this.swap((Integer)p.getFirst(), (Integer)p.getSecond()));
         return this;
      }

      public TransformParameters.Builder substitute(int target, int substitute) {
         return this.transform(new SubstituteParameterTransformer(target, substitute));
      }

      public TransformParameters.Builder substitutes(List<Pair<Integer, Integer>> substitutes) {
         substitutes.forEach(p -> this.swap((Integer)p.getFirst(), (Integer)p.getSecond()));
         return this;
      }

      public TransformParameters.Builder inline(int target, Consumer<InstructionAdapter> adapter) {
         return this.transform(new InlineParameterTransformer(target, adapter));
      }

      public TransformParameters.Builder remove(int index) {
         return this.transform(new RemoveParameterTransformer(index));
      }

      public TransformParameters.Builder removals(List<Integer> removals) {
         removals.forEach(this::remove);
         return this;
      }

      public TransformParameters.Builder withOffset() {
         this.offset = true;
         return this;
      }

      public TransformParameters.Builder withOffset(boolean offset) {
         this.offset = offset;
         return this;
      }

      public TransformParameters.Builder chain(Consumer<TransformParameters.Builder> consumer) {
         consumer.accept(this);
         return this;
      }

      @CheckReturnValue
      public TransformParameters build() {
         return new TransformParameters(this.transformers, this.offset);
      }
   }
}
