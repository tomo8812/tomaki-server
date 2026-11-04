package org.sinytra.adapter.analysis.selector;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.sinytra.adapter.analysis.method.AdvancedSourceInterpreter;

public class FrameUtil {
   public static Frame<SourceValue>[] getFrames(MethodNode methodNode) {
      try {
         Analyzer<SourceValue> analyzer = new Analyzer(new SourceInterpreter());
         return analyzer.analyze(Object.class.getName(), methodNode);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static Frame<SourceValue>[] getStableFrames(MethodNode methodNode) {
      try {
         Analyzer<SourceValue> analyzer = new Analyzer(new AdvancedSourceInterpreter(true));
         return analyzer.analyze(Object.class.getName(), methodNode);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static int getPopCount(AbstractInsnNode insn) {
      int op = insn.getOpcode();
      if (insn instanceof MethodInsnNode) {
         int args = Type.getArgumentsAndReturnSizes(((MethodInsnNode)insn).desc) >> 2;
         boolean isStatic = op == 184;
         if (op == 186) {
            isStatic = true;
         }

         return isStatic ? args : args + 1;
      } else {
         if (insn instanceof FieldInsnNode) {
            if (op == 178) {
               return 0;
            }

            if (op == 180) {
               return 1;
            }

            boolean isLongOrDouble = ((FieldInsnNode)insn).desc.matches("[JD]");
            int valSize = isLongOrDouble ? 2 : 1;
            if (op == 179) {
               return valSize;
            }

            if (op == 181) {
               return valSize + 1;
            }
         }
         return switch (op) {
            case 87 -> 1;
            case 88 -> 2;
            case 89 -> 1;
            case 96, 100, 104, 108 -> 2;
            case 116, 133 -> 1;
            default -> 0;
         };
      }
   }
}
