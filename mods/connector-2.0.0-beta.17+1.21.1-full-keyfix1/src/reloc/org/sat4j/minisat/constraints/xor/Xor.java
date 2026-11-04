package reloc.org.sat4j.minisat.constraints.xor;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.Propagatable;
import reloc.org.sat4j.specs.UnitPropagationListener;
import reloc.org.sat4j.specs.VarMapper;

@Feature("constraint")
public class Xor implements Constr, Propagatable {
   private final int[] lits;
   private final boolean parity;
   private final ILits voc;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !Xor.class.desiredAssertionStatus();

   public static Xor createParityConstraint(IVecInt lits, boolean parity, ILits voc) {
      Xor xor = new Xor(lits, parity, voc);
      xor.register();
      return xor;
   }

   public Xor(IVecInt lits, boolean parity, ILits voc) {
      this.lits = new int[lits.size()];
      lits.copyTo(this.lits);
      this.parity = parity;
      this.voc = voc;
   }

   @Override
   public boolean learnt() {
      return false;
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
   public double getActivity() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean canBePropagatedMultipleTimes() {
      return false;
   }

   @Override
   public String toString(VarMapper mapper) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean propagate(UnitPropagationListener s, int p) {
      int nbSatisfied = 0;
      if (p == this.lits[0] || LiteralsUtils.neg(p) == this.lits[0]) {
         int tmp = this.lits[1];
         this.lits[1] = this.lits[0];
         this.lits[0] = tmp;
      }

      if (this.voc.isSatisfied(this.lits[1])) {
         nbSatisfied = 1;
      }

      for (int i = 2; i < this.lits.length; i++) {
         if (this.voc.isSatisfied(this.lits[i])) {
            nbSatisfied++;
         } else if (this.voc.isUnassigned(this.lits[i])) {
            int tmp = this.lits[1];
            this.lits[1] = this.lits[i];
            this.lits[i] = tmp;
            this.voc.watch(this.lits[1] ^ 1, this);
            this.voc.watch(this.lits[1], this);
            this.voc.watches(p ^ 1).remove(this);
            return true;
         }
      }

      this.voc.watch(p, this);
      int toPropagate = (nbSatisfied & 1) == 1 == this.parity ? this.lits[0] : LiteralsUtils.neg(this.lits[0]);
      return s.enqueue(toPropagate, this);
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
   public void remove(UnitPropagationListener upl) {
      this.voc.watches(this.lits[0]).remove(this);
      this.voc.watches(this.lits[0] ^ 1).remove(this);
      this.voc.watches(this.lits[1]).remove(this);
      this.voc.watches(this.lits[1] ^ 1).remove(this);
   }

   @Override
   public boolean simplify() {
      return false;
   }

   @Override
   public void calcReason(int p, IVecInt outReason) {
      int nbUnassigned = 0;

      for (int i = 0; i < this.lits.length; i++) {
         if (this.voc.isFalsified(this.lits[i])) {
            outReason.push(this.lits[i] ^ 1);
         } else if (this.voc.isSatisfied(this.lits[i])) {
            outReason.push(this.lits[i]);
         } else {
            nbUnassigned++;
         }
      }

      assert nbUnassigned == (p == -1 ? 0 : 1);
   }

   @Override
   public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void incActivity(double claInc) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void forwardActivity(double claInc) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean locked() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setLearnt() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void register() {
      this.voc.watch(this.lits[0], this);
      this.voc.watch(this.lits[0] ^ 1, this);
      this.voc.watch(this.lits[1], this);
      this.voc.watch(this.lits[1] ^ 1, this);
   }

   @Override
   public void rescaleBy(double d) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setActivity(double d) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void assertConstraint(UnitPropagationListener s) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void assertConstraintIfNeeded(UnitPropagationListener s) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean canBeSatisfiedByCountingLiterals() {
      return true;
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

   @Override
   public String toString() {
      StringBuilder stb = new StringBuilder();

      for (int l : this.lits) {
         stb.append(LiteralsUtils.toDimacs(l));
         stb.append(" ");
         stb.append(this.voc.isUnassigned(l) ? "U" : (this.voc.isFalsified(l) ? "F" : "T"));
         stb.append(" x ");
      }

      stb.append(this.parity);
      return stb.toString();
   }

   public void activate() {
      this.register();
   }

   public void deactivate() {
      this.remove(null);
   }
}
