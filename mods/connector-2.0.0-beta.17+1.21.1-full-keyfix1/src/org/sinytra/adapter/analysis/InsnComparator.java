package org.sinytra.adapter.analysis;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class InsnComparator {
   public static final int IGNORE_VAR_INDEX = 1;
   public static final int IGNORE_LINE_NUMBERS = 16;

   public static boolean insnEqual(AbstractInsnNode a, AbstractInsnNode b) {
      return insnEqual(a, b, 0);
   }

   public static boolean insnEqual(AbstractInsnNode a, AbstractInsnNode b, int flags) {
      if (a == b) {
         return true;
      }

      if (a != null && b != null) {
         if (a.getClass() != b.getClass()) {
            return false;
         } else if (a.getOpcode() != b.getOpcode()) {
            return false;
         } else if (a instanceof FieldInsnNode fa) {
            FieldInsnNode fb = (FieldInsnNode)b;
            return Objects.equals(fa.owner, fb.owner) && Objects.equals(fa.name, fb.name) && Objects.equals(fa.desc, fb.desc);
         } else if (a instanceof IincInsnNode ia) {
            IincInsnNode ib = (IincInsnNode)b;
            return ia.var == ib.var && ia.incr == ib.incr;
         } else if (a instanceof InsnNode) {
            return true;
         } else if (a instanceof IntInsnNode ia) {
            IntInsnNode ib = (IntInsnNode)b;
            return ia.operand == ib.operand;
         } else if (a instanceof InvokeDynamicInsnNode ia) {
            InvokeDynamicInsnNode ib = (InvokeDynamicInsnNode)b;
            return Objects.equals(ia.bsm, ib.bsm)
               && Arrays.equals(ia.bsmArgs, ib.bsmArgs)
               && Objects.equals(ia.name, ib.name)
               && Objects.equals(ia.desc, ib.desc);
         } else if (a instanceof JumpInsnNode || a instanceof LabelNode || a instanceof FrameNode) {
            return true;
         } else if (a instanceof LdcInsnNode la) {
            LdcInsnNode lb = (LdcInsnNode)b;
            return Objects.equals(la.cst, lb.cst);
         } else if (a instanceof LineNumberNode la) {
            LineNumberNode lb = (LineNumberNode)b;
            return (flags & 16) != 0 || la.line == lb.line && insnEqual(la.start, lb.start);
         } else if (a instanceof LookupSwitchInsnNode la) {
            LookupSwitchInsnNode lb = (LookupSwitchInsnNode)b;
            return insnEqual(la.dflt, lb.dflt) && Objects.equals(la.keys, lb.keys) && instructionListsEqual(la.labels, lb.labels);
         } else if (a instanceof MethodInsnNode ma) {
            MethodInsnNode mb = (MethodInsnNode)b;
            return Objects.equals(ma.owner, mb.owner) && Objects.equals(ma.name, mb.name) && Objects.equals(ma.desc, mb.desc) && ma.itf == mb.itf;
         } else if (a instanceof MultiANewArrayInsnNode ma) {
            MultiANewArrayInsnNode mb = (MultiANewArrayInsnNode)b;
            return Objects.equals(ma.desc, mb.desc) && ma.dims == mb.dims;
         } else if (a instanceof TableSwitchInsnNode ta) {
            TableSwitchInsnNode tb = (TableSwitchInsnNode)b;
            return ta.min == tb.min && ta.max == tb.max && insnEqual(ta.dflt, tb.dflt) && instructionListsEqual(ta.labels, tb.labels);
         } else if (a instanceof TypeInsnNode ta) {
            TypeInsnNode tb = (TypeInsnNode)b;
            return Objects.equals(ta.desc, tb.desc);
         } else if (!(a instanceof VarInsnNode va)) {
            throw new IllegalArgumentException("Unknown insn type " + a.getClass().getName());
         } else {
            VarInsnNode vb = (VarInsnNode)b;
            return (flags & 1) != 0 || va.var == vb.var;
         }
      } else {
         return false;
      }
   }

   private static boolean instructionListsEqual(List<? extends AbstractInsnNode> a, List<? extends AbstractInsnNode> b) {
      if (a == b) {
         return true;
      }

      if (a != null && b != null) {
         if (a.size() != b.size()) {
            return false;
         }

         for (int i = 0; i < a.size(); i++) {
            if (!insnEqual(a.get(i), b.get(i))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
