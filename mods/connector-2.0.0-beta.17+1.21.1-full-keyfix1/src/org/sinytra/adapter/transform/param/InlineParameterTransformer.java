package org.sinytra.adapter.transform.param;

import com.mojang.logging.LogUtils;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.locals.LVTSnapshot;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.util.AdapterUtil;
import org.slf4j.Logger;

public record InlineParameterTransformer(int target, Consumer<InstructionAdapter> adapter) implements ParameterTransformer {
   private static final Logger LOGGER = LogUtils.getLogger();

   @Override
   public PatchResult apply(ClassNode classNode, MethodNode methodNode, MixinContext context, List<Type> parameters, int offset) {
      int index = this.target + offset;
      LOGGER.info(AdapterUtil.MIXINPATCH, "Inlining parameter {} of method {}.{}", new Object[]{index, classNode.name, methodNode.name});
      int replaceIndex = -999 + index;
      LVTSnapshot.with(methodNode, () -> {
         if (index < methodNode.parameters.size()) {
            methodNode.parameters.remove(index);
         }

         methodNode.localVariables.sort(Comparator.comparingInt(lvnx -> lvnx.index));
         LocalVariableNode lvn = (LocalVariableNode)methodNode.localVariables.remove(index + (context.isStatic() ? 0 : 1));
         AdapterUtil.replaceLVT(methodNode, idx -> idx == lvn.index ? replaceIndex : idx);
      });
      parameters.remove(index);

      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof VarInsnNode varInsn && varInsn.var == replaceIndex) {
            InsnList replacementInsns = AdapterUtil.insnsWithAdapter(this.adapter);
            methodNode.instructions.insert(varInsn, replacementInsns);
            methodNode.instructions.remove(varInsn);
         }
      }

      return PatchResult.COMPUTE_FRAMES;
   }
}
