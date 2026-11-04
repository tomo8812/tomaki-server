package reloc.org.sat4j.minisat.constraints.cnf;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.UnitPropagationListener;
import reloc.org.sat4j.specs.VarMapper;

@Feature("constraint")
public class UnitClauses implements Constr {
   protected final int[] literals;

   public UnitClauses(IVecInt values) {
      this.literals = new int[values.size()];
      values.copyTo(this.literals);
   }

   @Override
   public void assertConstraint(UnitPropagationListener s) {
      for (int p : this.literals) {
         s.enqueue(p, this);
      }
   }

   @Override
   public void assertConstraintIfNeeded(UnitPropagationListener s) {
      this.assertConstraint(s);
   }

   @Override
   public void calcReason(int p, IVecInt outReason) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double getActivity() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void incActivity(double claInc) {
   }

   @Override
   public void setActivity(double claInc) {
   }

   @Override
   public boolean locked() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void register() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(UnitPropagationListener upl) {
      int oldLevel = upl.getPropagationLevel();

      for (int i = this.literals.length - 1; i >= 0; i--) {
         upl.unset(this.literals[i]);
      }

      if (upl.getPropagationLevel() < oldLevel - this.literals.length) {
         throw new IllegalStateException("removed unit clause which caused propagations");
      }
   }

   @Override
   public void rescaleBy(double d) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setLearnt() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean simplify() {
      return false;
   }

   public boolean propagate(UnitPropagationListener s, int p) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int get(int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean learnt() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int size() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void forwardActivity(double claInc) {
   }

   @Override
   public boolean canBePropagatedMultipleTimes() {
      return false;
   }

   @Override
   public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
      this.calcReason(p, outReason);
   }

   public void propagatePi(MandatoryLiteralListener m) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean canBeSatisfiedByCountingLiterals() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int requiredNumberOfSatisfiedLiterals() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isSatisfied() {
      return true;
   }

   @Override
   public int getAssertionLevel(IVecInt trail, int decisionLevel) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public String toString(VarMapper mapper) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();

      for (int i = 0; i < this.literals.length; i++) {
         stb.append(this.literals[i]);
         stb.append(" 0");
         if (i < this.literals.length - 1) {
            stb.append(System.lineSeparator());
         }
      }

      return stb.toString();
   }
}
