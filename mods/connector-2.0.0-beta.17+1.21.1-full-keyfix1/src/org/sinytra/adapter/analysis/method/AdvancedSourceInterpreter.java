package org.sinytra.adapter.analysis.method;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

public class AdvancedSourceInterpreter extends SourceInterpreter {
   private final boolean stable;

   public AdvancedSourceInterpreter(boolean stable) {
      super(589824);
      this.stable = stable;
   }

   private static boolean isStackOp(int opcode) {
      return opcode == 89 || opcode == 90 || opcode == 91 || opcode == 92 || opcode == 93 || opcode == 94 || opcode == 95;
   }

   private SourceValue copyWithoutStackOps(SourceValue v) {
      if (this.stable && v.insns != null && !v.insns.isEmpty()) {
         Set<AbstractInsnNode> filtered = new HashSet<>();

         for (AbstractInsnNode insn : v.insns) {
            if (!isStackOp(insn.getOpcode())) {
               filtered.add(insn);
            }
         }

         return filtered.isEmpty() ? v : new SourceValue(v.size, filtered);
      } else {
         return v;
      }
   }

   public SourceValue copyOperation(AbstractInsnNode insn, SourceValue value) {
      return this.stable && !value.insns.isEmpty() ? this.copyWithoutStackOps(value) : super.copyOperation(insn, value);
   }

   public SourceValue unaryOperation(AbstractInsnNode insn, SourceValue value) {
      return super.unaryOperation(insn, this.copyWithoutStackOps(value));
   }

   public SourceValue binaryOperation(AbstractInsnNode insn, SourceValue v1, SourceValue v2) {
      return super.binaryOperation(insn, this.copyWithoutStackOps(v1), this.copyWithoutStackOps(v2));
   }

   public SourceValue ternaryOperation(AbstractInsnNode insn, SourceValue v1, SourceValue v2, SourceValue v3) {
      return super.ternaryOperation(insn, this.copyWithoutStackOps(v1), this.copyWithoutStackOps(v2), this.copyWithoutStackOps(v3));
   }

   public SourceValue naryOperation(AbstractInsnNode insn, List<? extends SourceValue> values) {
      List<SourceValue> cleaned = new ArrayList<>(values.size());

      for (SourceValue v : values) {
         cleaned.add(this.copyWithoutStackOps(v));
      }

      return super.naryOperation(insn, cleaned);
   }

   public SourceValue merge(SourceValue v1, SourceValue v2) {
      if (!this.stable) {
         return super.merge(v1, v2);
      } else if (v1 == v2) {
         return v1;
      } else {
         SourceValue c1 = this.copyWithoutStackOps(v1);
         SourceValue c2 = this.copyWithoutStackOps(v2);
         if (c1.insns != v1.insns && c2.insns == v2.insns) {
            return c1;
         } else {
            return c2.insns != v2.insns && c1.insns == v1.insns ? c2 : super.merge(c1, c2);
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Value merge(Value param1, Value nullx) {
      return this.merge((SourceValue)var1, (SourceValue)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Value naryOperation(AbstractInsnNode param1, List nullx) throws AnalyzerException {
      return this.naryOperation(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Value ternaryOperation(AbstractInsnNode param1, Value nullx, Value nullxx, Value nullxxx) throws AnalyzerException {
      return this.ternaryOperation(var1, (SourceValue)nullx, (SourceValue)nullxx, (SourceValue)nullxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Value binaryOperation(AbstractInsnNode param1, Value nullx, Value nullxx) throws AnalyzerException {
      return this.binaryOperation(var1, (SourceValue)nullx, (SourceValue)nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Value unaryOperation(AbstractInsnNode param1, Value nullx) throws AnalyzerException {
      return this.unaryOperation(var1, (SourceValue)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Value copyOperation(AbstractInsnNode param1, Value nullx) throws AnalyzerException {
      return this.copyOperation(var1, (SourceValue)nullx);
   }
}
