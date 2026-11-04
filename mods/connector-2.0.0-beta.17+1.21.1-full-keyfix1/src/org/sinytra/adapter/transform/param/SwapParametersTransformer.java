package org.sinytra.adapter.transform.param;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.SingleValueHandle;
import org.slf4j.Logger;

public record SwapParametersTransformer(int from, int to) implements ParameterTransformer {
   private static final Logger LOGGER = LogUtils.getLogger();

   @Override
   public PatchResult apply(ClassNode classNode, MethodNode methodNode, MixinContext context, List<Type> parameters, int offset) {
      int from = offset + this.from;
      int to = offset + this.to;
      boolean nonStatic = !context.isStatic();
      ParameterNode fromNode = (ParameterNode)methodNode.parameters.get(from);
      ParameterNode toNode = (ParameterNode)methodNode.parameters.get(to);
      int fromOldLVT = ParamTransformationUtil.calculateLVTIndex(parameters, nonStatic, from);
      int toOldLVT = ParamTransformationUtil.calculateLVTIndex(parameters, nonStatic, to);
      Type fromType = parameters.get(from);
      Type toType = parameters.get(to);
      parameters.set(from, toType);
      parameters.set(to, fromType);
      methodNode.parameters.set(from, toNode);
      methodNode.parameters.set(to, fromNode);
      LOGGER.info(
         AdapterUtil.MIXINPATCH,
         "Swapped parameters at positions {}({}) and {}({}) in {}.{}",
         new Object[]{from, fromNode.name, to, toNode.name, classNode.name, methodNode.name}
      );
      int fromNewLVT = ParamTransformationUtil.calculateLVTIndex(parameters, nonStatic, from);
      int toNewLVT = ParamTransformationUtil.calculateLVTIndex(parameters, nonStatic, to);
      swapLVT(methodNode, fromOldLVT, toNewLVT).andThen(swapLVT(methodNode, toOldLVT, fromNewLVT)).accept(null);
      return PatchResult.COMPUTE_FRAMES;
   }

   public static Consumer<Void> swapLVT(MethodNode methodNode, int from, int to) {
      Consumer<Void> r = v -> {};

      for (LocalVariableNode lvn : methodNode.localVariables) {
         if (lvn.index == from) {
            r = r.andThen(v -> lvn.index = to);
         }
      }

      for (AbstractInsnNode insn : methodNode.instructions) {
         SingleValueHandle<Integer> handle = AdapterUtil.handleLocalVarInsnValue(insn);
         if (handle != null && handle.get() == from) {
            LOGGER.info("Swapping in LVT: {} to {}", from, to);
            r = r.andThen(v -> handle.set(to));
         }
      }

      return r;
   }
}
