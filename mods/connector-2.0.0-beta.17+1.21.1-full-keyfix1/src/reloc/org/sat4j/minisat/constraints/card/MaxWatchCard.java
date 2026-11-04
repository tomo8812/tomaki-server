package reloc.org.sat4j.minisat.constraints.card;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.constraints.cnf.Lits;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClauses;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.Undoable;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.Propagatable;
import reloc.org.sat4j.specs.UnitPropagationListener;
import reloc.org.sat4j.specs.VarMapper;

@Feature("constraint")
public final class MaxWatchCard implements Propagatable, Constr, Undoable, Serializable {
   private static final long serialVersionUID = 1L;
   private int degree;
   private final int[] lits;
   private boolean moreThan;
   private int watchCumul;
   private final ILits voc;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MaxWatchCard.class.desiredAssertionStatus();

   public MaxWatchCard(ILits voc, IVecInt ps, boolean moreThan, int degree) {
      this.voc = voc;
      this.degree = degree;
      this.moreThan = moreThan;
      int[] index = new int[voc.nVars() * 2 + 2];

      for (int i = 0; i < index.length; i++) {
         index[i] = 0;
      }

      for (int i = 0; i < ps.size(); i++) {
         if (index[ps.get(i) ^ 1] == 0) {
            index[ps.get(i)]++;
         } else {
            index[ps.get(i) ^ 1]--;
         }
      }

      int ind = 0;

      while (ind < ps.size()) {
         if (index[ps.get(ind)] > 0) {
            index[ps.get(ind)]--;
            ind++;
         } else {
            if ((ps.get(ind) & 1) != 0) {
               this.degree--;
            }

            ps.set(ind, ps.last());
            ps.pop();
         }
      }

      this.lits = new int[ps.size()];
      ps.moveTo(this.lits);
      this.normalize();
      this.watchCumul = 0;

      for (int i = 0; i < this.lits.length; i++) {
         if (!voc.isFalsified(this.lits[i])) {
            this.watchCumul++;
            voc.watch(this.lits[i] ^ 1, this);
         }
      }
   }

   @Override
   public void calcReason(int p, IVecInt outReason) {
      for (int lit : this.lits) {
         if (this.voc.isFalsified(lit)) {
            outReason.push(lit ^ 1);
         }
      }
   }

   @Override
   public double getActivity() {
      return 0.0;
   }

   @Override
   public void incActivity(double claInc) {
   }

   @Override
   public void setActivity(double d) {
   }

   @Override
   public boolean learnt() {
      return false;
   }

   @Override
   public boolean locked() {
      return true;
   }

   public static Constr maxWatchCardNew(UnitPropagationListener s, ILits voc, IVecInt ps, boolean moreThan, int degree) throws ContradictionException {
      MaxWatchCard outclause = null;
      if (ps.size() < degree) {
         throw new ContradictionException("Creating trivially inconsistent constraint");
      }

      if (ps.size() == degree) {
         for (int i = 0; i < ps.size(); i++) {
            if (!s.enqueue(ps.get(i))) {
               throw new ContradictionException("Contradiction with implied literal");
            }
         }

         return new UnitClauses(ps);
      } else {
         outclause = new MaxWatchCard(voc, ps, moreThan, degree);
         if (outclause.degree <= 0) {
            return null;
         }

         if (outclause.watchCumul < outclause.degree) {
            throw new ContradictionException();
         }

         if (outclause.watchCumul == outclause.degree) {
            for (int i = 0; i < outclause.lits.length; i++) {
               if (!s.enqueue(outclause.lits[i])) {
                  throw new ContradictionException("Contradiction with implied literal");
               }
            }

            return null;
         } else {
            return outclause;
         }
      }
   }

   public void normalize() {
      if (!this.moreThan) {
         this.degree = 0 - this.degree;

         for (int indLit = 0; indLit < this.lits.length; indLit++) {
            this.lits[indLit] = this.lits[indLit] ^ 1;
            this.degree++;
         }

         this.moreThan = true;
      }
   }

   @Override
   public boolean propagate(UnitPropagationListener s, int p) {
      this.voc.watch(p, this);
      assert !this.voc.isFalsified(p);
      if (this.watchCumul == this.degree) {
         return false;
      }

      this.voc.undos(p).push(this);
      this.watchCumul--;
      if (this.watchCumul == this.degree) {
         for (int q : this.lits) {
            if (this.voc.isUnassigned(q) && !s.enqueue(q, this)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public void remove(UnitPropagationListener upl) {
      for (int q : this.lits) {
         this.voc.watches(q ^ 1).remove(this);
      }
   }

   @Override
   public void rescaleBy(double d) {
   }

   @Override
   public boolean simplify() {
      int i = 0;
      int curr = this.watchCumul;

      while (i < this.lits.length) {
         if (this.voc.isUnassigned(this.lits[i++])) {
            if (--curr < this.degree) {
               return false;
            }
         }
      }

      return false;
   }

   @Override
   public String toString() {
      StringBuilder stb = new StringBuilder();
      if (this.lits.length > 0) {
         if (this.voc.isUnassigned(this.lits[0])) {
            stb.append(Lits.toString(this.lits[0]));
            stb.append(" ");
         }

         for (int i = 1; i < this.lits.length; i++) {
            if (this.voc.isUnassigned(this.lits[i])) {
               stb.append(" + ");
               stb.append(Lits.toString(this.lits[i]));
               stb.append(" ");
            }
         }

         stb.append(">= ");
         stb.append(this.degree);
      }

      return stb.toString();
   }

   @Override
   public void undo(int p) {
      this.watchCumul++;
   }

   @Override
   public void setLearnt() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void register() {
      throw new UnsupportedOperationException();
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
      boolean ret = true;
      int[] var3 = this.lits;
      int var4 = var3.length;

      for (int var5 = 0; var5 < var4; var5++) {
         Integer lit = var3[var5];
         if (this.voc.isUnassigned(lit)) {
            ret &= s.enqueue(lit, this);
         }
      }

      assert ret;
   }

   @Override
   public void assertConstraintIfNeeded(UnitPropagationListener s) {
      throw new UnsupportedOperationException();
   }

   public BigInteger getCoef(int literal) {
      return BigInteger.ONE;
   }

   public BigInteger getDegree() {
      return BigInteger.valueOf(this.degree);
   }

   public ILits getVocabulary() {
      return this.voc;
   }

   @Override
   public void forwardActivity(double claInc) {
   }

   @Override
   public boolean canBePropagatedMultipleTimes() {
      return false;
   }

   @Override
   public Constr toConstraint() {
      return this;
   }

   @Override
   public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean propagatePI(MandatoryLiteralListener l, int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean canBeSatisfiedByCountingLiterals() {
      return true;
   }

   @Override
   public int requiredNumberOfSatisfiedLiterals() {
      return this.degree;
   }

   @Override
   public boolean isSatisfied() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int getAssertionLevel(IVecInt trail, int decisionLevel) {
      int nUnsat = 0;
      Set<Integer> litsSet = new HashSet<>();
      int[] i = this.lits;
      int var6 = i.length;

      for (int var7 = 0; var7 < var6; var7++) {
         Integer ix = i[var7];
         litsSet.add(ix);
      }

      for (int ix = 0; ix < trail.size(); ix++) {
         if (litsSet.contains(trail.get(ix) ^ 1)) {
            if (++nUnsat == this.lits.length - this.degree) {
               return ix;
            }
         }
      }

      return -1;
   }

   @Override
   public String toString(VarMapper mapper) {
      StringBuilder stb = new StringBuilder();
      if (this.lits.length > 0) {
         if (this.voc.isUnassigned(this.lits[0])) {
            stb.append(mapper.map(LiteralsUtils.toDimacs(this.lits[0])));
            stb.append(" ");
         }

         for (int i = 1; i < this.lits.length; i++) {
            if (this.voc.isUnassigned(this.lits[i])) {
               stb.append(" + ");
               stb.append(mapper.map(LiteralsUtils.toDimacs(this.lits[i])));
               stb.append(" ");
            }
         }

         stb.append(">= ");
         stb.append(this.degree);
      }

      return stb.toString();
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();
      stb.append(LiteralsUtils.toOPB(this.lits[0]));
      int i = 1;

      while (i < this.lits.length) {
         stb.append(" + ");
         stb.append(LiteralsUtils.toOPB(this.lits[i++]));
      }

      stb.append(" >= ");
      stb.append(this.degree);
      return stb.toString();
   }
}
