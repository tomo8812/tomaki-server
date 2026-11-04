package org.sinytra.adapter.patch.processor.wrapop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.sinytra.adapter.analysis.selector.FrameUtil;
import org.sinytra.adapter.util.AdapterUtil;

public class WrapOpAnalyzer {
   public static List<List<AbstractInsnNode>> groupArrayInitializers(MethodNode methodNode, AbstractInsnNode newArrayInsn) {
      Frame<SourceValue>[] frames = FrameUtil.getStableFrames(methodNode);
      Map<Integer, AbstractInsnNode> indexToStoreNode = new HashMap<>();
      InsnList instructions = methodNode.instructions;

      for (int i = 0; i < instructions.size(); i++) {
         AbstractInsnNode insn = instructions.get(i);
         if (insn.getOpcode() == 83) {
            Frame<SourceValue> frame = frames[i];
            SourceValue arrayRefSource = (SourceValue)frame.getStack(frame.getStackSize() - 4);
            if (arrayRefSource.insns.contains(newArrayInsn)) {
               SourceValue indexSource = (SourceValue)frame.getStack(frame.getStackSize() - 2);
               Integer constantIndex = resolveConstantIndex(indexSource);
               if (constantIndex != null) {
                  indexToStoreNode.put(constantIndex, insn);
               }
            }
         }
      }

      List<List<AbstractInsnNode>> groups = new ArrayList<>();
      AbstractInsnNode currentStart = newArrayInsn.getNext();
      int maxIndex = indexToStoreNode.keySet().stream().max(Integer::compare).orElse(-1);

      for (int i = 0; i <= maxIndex; i++) {
         AbstractInsnNode endNode = indexToStoreNode.get(i);
         List<AbstractInsnNode> currentGroup = new ArrayList<>();
         if (endNode != null) {
            for (AbstractInsnNode ptr = currentStart; ptr != null; ptr = ptr.getNext()) {
               currentGroup.add(ptr);
               if (ptr == endNode) {
                  break;
               }
            }

            currentStart = endNode.getNext();
         }

         groups.add(currentGroup);
      }

      return groups;
   }

   private static Integer resolveConstantIndex(SourceValue source) {
      for (AbstractInsnNode insn : source.insns) {
         int index = AdapterUtil.getIntConstValue(insn).orElse(-1);
         if (index != -1) {
            return index;
         }
      }

      return null;
   }
}
