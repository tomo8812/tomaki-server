package reloc.org.sat4j.minisat.constraints.cnf;

import java.io.Serializable;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.Undoable;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.Propagatable;
import reloc.org.sat4j.specs.UnitPropagationListener;
import reloc.org.sat4j.specs.VarMapper;

@Feature("constraint")
public class CBClause implements Constr, Undoable, Propagatable, Serializable {
   private static final long serialVersionUID = 1L;
   protected int falsified;
   private boolean learnt;
   protected final int[] lits;
   protected final ILits voc;
   private double activity;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !CBClause.class.desiredAssertionStatus();

   public static CBClause brandNewClause(UnitPropagationListener s, ILits voc, IVecInt literals) {
      CBClause c = new CBClause(literals, voc);
      c.register();
      return c;
   }

   public CBClause(IVecInt ps, ILits voc, boolean learnt) {
      this.learnt = learnt;
      this.lits = new int[ps.size()];
      this.voc = voc;
      ps.moveTo(this.lits);
   }

   public CBClause(IVecInt ps, ILits voc) {
      this(ps, voc, false);
   }

   public void remove() {
      for (int i = 0; i < this.lits.length; i++) {
         this.voc.watches(this.lits[i] ^ 1).remove(this);
      }
   }

   @Override
   public boolean propagate(UnitPropagationListener s, int p) {
      this.voc.undos(p).push(this);
      this.falsified++;
      assert this.falsified != this.lits.length;
      if (this.falsified == this.lits.length - 1) {
         for (int i = 0; i < this.lits.length; i++) {
            if (!this.voc.isFalsified(this.lits[i])) {
               return s.enqueue(this.lits[i], this);
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean simplify() {
      for (int p : this.lits) {
         if (this.voc.isSatisfied(p)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void undo(int p) {
      this.falsified--;
   }

   @Override
   public void calcReason(int p, IVecInt outReason) {
      assert outReason.size() == 0;

      for (int q : this.lits) {
         assert this.voc.isFalsified(q) || q == p;
         if (this.voc.isFalsified(q)) {
            outReason.push(q ^ 1);
         }
      }

      assert p == -1 || outReason.size() == this.lits.length - 1;
   }

   @Override
   public boolean learnt() {
      return this.learnt;
   }

   @Override
   public void incActivity(double claInc) {
      this.activity += claInc;
   }

   @Override
   public double getActivity() {
      return this.activity;
   }

   @Override
   public boolean locked() {
      return this.voc.getReason(this.lits[0]) == this;
   }

   @Override
   public void setLearnt() {
      this.learnt = true;
   }

   @Override
   public void register() {
      for (int p : this.lits) {
         this.voc.watch(p ^ 1, this);
      }

      if (this.learnt) {
         for (int p : this.lits) {
            if (this.voc.isFalsified(p)) {
               this.voc.undos(p ^ 1).push(this);
               this.falsified++;
            }
         }

         assert this.falsified == this.lits.length - 1;
      }
   }

   @Override
   public void rescaleBy(double d) {
      this.activity *= d;
   }

   @Override
   public int size() {
      return this.lits.length;
   }

   @Override
   public int get(int i) {
      return this.lits[i];
   }

   @Override
   public void assertConstraint(UnitPropagationListener s) {
      assert this.voc.isUnassigned(this.lits[0]);
      boolean ret = s.enqueue(this.lits[0], this);
      assert ret;
   }

   @Override
   public String toString() {
      StringBuilder stb = new StringBuilder();

      for (int i = 0; i < this.lits.length; i++) {
         stb.append(this.lits[i]);
         stb.append("[");
         stb.append(this.voc.valueToString(this.lits[i]));
         stb.append("]");
         stb.append(" ");
      }

      return stb.toString();
   }

   @Override
   public boolean canBePropagatedMultipleTimes() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public String toString(VarMapper mapper) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void remove(UnitPropagationListener upl) {
      throw new UnsupportedOperationException("Not implemented yet!");
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
   public boolean propagatePI(MandatoryLiteralListener l, int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public Constr toConstraint() {
      return this;
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();

      for (int p : this.lits) {
         stb.append(LiteralsUtils.toDimacs(p));
         stb.append(' ');
      }

      stb.append('0');
      return stb.toString();
   }
}
