package reloc.org.sat4j.minisat.constraints.cnf;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.UnitPropagationListener;
import reloc.org.sat4j.specs.VarMapper;

@Feature("constraint")
public class UnitClause implements Constr {
   protected final int literal;
   protected double activity;
   private boolean learnt;

   public UnitClause(int value) {
      this(value, false);
   }

   public UnitClause(int value, boolean learnt) {
      this.literal = value;
      this.learnt = learnt;
   }

   @Override
   public void assertConstraint(UnitPropagationListener s) {
      s.enqueue(this.literal, this);
   }

   @Override
   public void assertConstraintIfNeeded(UnitPropagationListener s) {
      this.assertConstraint(s);
   }

   @Override
   public void calcReason(int p, IVecInt outReason) {
      if (p == -1) {
         outReason.push(LiteralsUtils.neg(this.literal));
      }
   }

   @Override
   public double getActivity() {
      return this.activity;
   }

   @Override
   public void incActivity(double claInc) {
   }

   @Override
   public void setActivity(double claInc) {
      this.activity = claInc;
   }

   @Override
   public boolean locked() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void register() {
   }

   @Override
   public void remove(UnitPropagationListener upl) {
      int oldLevel = upl.getPropagationLevel();
      upl.unset(this.literal);
      if (upl.getPropagationLevel() < oldLevel - 1) {
         throw new IllegalStateException("removed unit clause which caused propagations");
      }
   }

   @Override
   public void rescaleBy(double d) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setLearnt() {
      this.learnt = true;
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
      if (i > 0) {
         throw new IllegalArgumentException();
      } else {
         return this.literal;
      }
   }

   @Override
   public boolean learnt() {
      return this.learnt;
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public void forwardActivity(double claInc) {
   }

   @Override
   public String toString() {
      return Lits.toString(this.literal);
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
      return true;
   }

   @Override
   public int requiredNumberOfSatisfiedLiterals() {
      return 1;
   }

   @Override
   public boolean isSatisfied() {
      return true;
   }

   @Override
   public int getAssertionLevel(IVecInt trail, int decisionLevel) {
      return 0;
   }

   @Override
   public String toString(VarMapper mapper) {
      return mapper == null ? this.toString() : mapper.map(LiteralsUtils.toDimacs(this.literal));
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();
      stb.append(this.literal);
      stb.append(" 0");
      return stb.toString();
   }
}
