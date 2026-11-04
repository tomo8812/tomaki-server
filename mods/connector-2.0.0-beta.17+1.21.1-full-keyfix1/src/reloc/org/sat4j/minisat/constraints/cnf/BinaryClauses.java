package reloc.org.sat4j.minisat.constraints.cnf;

import java.io.Serializable;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.Propagatable;
import reloc.org.sat4j.specs.UnitPropagationListener;
import reloc.org.sat4j.specs.VarMapper;

@Feature("constraint")
public class BinaryClauses implements Constr, Propagatable, Serializable {
   private static final long serialVersionUID = 1L;
   private final ILits voc;
   private final IVecInt clauses = new VecInt();
   private final int reason;
   private int conflictindex = -1;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !BinaryClauses.class.desiredAssertionStatus();

   public BinaryClauses(ILits voc, int p) {
      this.voc = voc;
      this.reason = p;
   }

   public void addBinaryClause(int p) {
      this.clauses.push(p);
   }

   public void removeBinaryClause(int p) {
      this.clauses.remove(p);
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean propagate(UnitPropagationListener s, int p) {
      this.voc.watch(p, this);

      for (int i = 0; i < this.clauses.size(); i++) {
         int q = this.clauses.get(i);
         if (!s.enqueue(q, this)) {
            this.conflictindex = i;
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean simplify() {
      return false;
   }

   public void undo(int p) {
   }

   @Override
   public void calcReason(int p, IVecInt outReason) {
      outReason.push(this.reason ^ 1);
      if (p == -1) {
         assert this.conflictindex > -1;
         outReason.push(this.clauses.get(this.conflictindex) ^ 1);
      }
   }

   @Override
   public boolean learnt() {
      return false;
   }

   @Override
   public void incActivity(double claInc) {
   }

   @Override
   public double getActivity() {
      return 0.0;
   }

   @Override
   public boolean locked() {
      return false;
   }

   @Override
   public void setLearnt() {
   }

   @Override
   public void register() {
   }

   @Override
   public void rescaleBy(double d) {
   }

   @Override
   public int size() {
      return this.clauses.size();
   }

   @Override
   public int get(int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void assertConstraint(UnitPropagationListener s) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean canBePropagatedMultipleTimes() {
      return true;
   }

   @Override
   public String toString(VarMapper mapper) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean propagatePI(MandatoryLiteralListener l, int p) {
      for (int i = 0; i < this.clauses.size(); i++) {
         l.isMandatory(this.clauses.get(i));
      }

      return true;
   }

   @Override
   public Constr toConstraint() {
      return this;
   }

   @Override
   public void remove(UnitPropagationListener upl) {
      throw new UnsupportedOperationException("Cannot remove all the binary clauses at once!");
   }

   @Override
   public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void forwardActivity(double claInc) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setActivity(double d) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void assertConstraintIfNeeded(UnitPropagationListener s) {
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
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int getAssertionLevel(IVecInt trail, int decisionLevel) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public String dump() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }
}
