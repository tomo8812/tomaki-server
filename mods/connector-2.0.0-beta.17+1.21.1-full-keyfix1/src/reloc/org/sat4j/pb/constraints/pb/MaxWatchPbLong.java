package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.UnitPropagationListener;
import reloc.org.sat4j.specs.VarMapper;

public final class MaxWatchPbLong extends WatchPbLong {
   private static final long serialVersionUID = 1L;
   private long watchCumul = 0L;
   private final Map<Integer, Long> litToCoeffs;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MaxWatchPbLong.class.desiredAssertionStatus();

   private MaxWatchPbLong(ILits voc, IDataStructurePB mpb) {
      super(mpb);
      this.voc = voc;
      this.activity = 0.0;
      this.watchCumul = 0L;
      if (this.coefs.length > 100) {
         this.litToCoeffs = new HashMap<>(this.coefs.length);

         for (int i = 0; i < this.coefs.length; i++) {
            this.litToCoeffs.put(this.lits[i], this.coefs[i]);
         }
      } else {
         this.litToCoeffs = null;
      }
   }

   private MaxWatchPbLong(ILits voc, int[] lits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs) {
      super(lits, coefs, degree, sumCoefs);
      this.voc = voc;
      this.activity = 0.0;
      this.watchCumul = 0L;
      if (coefs.length > 100) {
         this.litToCoeffs = new HashMap<>(this.coefs.length);

         for (int i = 0; i < this.coefs.length; i++) {
            this.litToCoeffs.put(this.lits[i], this.coefs[i]);
         }
      } else {
         this.litToCoeffs = null;
      }
   }

   @Override
   protected void computeWatches() throws ContradictionException {
      assert this.watchCumul == 0L;

      for (int i = 0; i < this.lits.length; i++) {
         if (this.voc.isFalsified(this.lits[i])) {
            if (this.learnt) {
               this.voc.undos(this.lits[i] ^ 1).push(this);
               this.voc.watch(this.lits[i] ^ 1, this);
            }
         } else {
            this.voc.watch(this.lits[i] ^ 1, this);
            this.watchCumul = this.watchCumul + this.coefs[i];
         }
      }

      assert this.watchCumul >= this.computeLeftSide();
      if (!this.learnt && this.watchCumul < this.degree) {
         for (int i = 0; i < this.lits.length; i++) {
            if (!this.voc.isFalsified(this.lits[i])) {
               this.voc.watches(this.lits[i] ^ 1).remove(this);
            }
         }

         throw new ContradictionException("non satisfiable constraint");
      }
   }

   @Override
   protected void computePropagation(UnitPropagationListener s) throws ContradictionException {
      for (int ind = 0; ind < this.coefs.length && this.watchCumul - this.coefs[ind] < this.degree; ind++) {
         if (this.voc.isUnassigned(this.lits[ind]) && !s.enqueue(this.lits[ind], this)) {
            throw new ContradictionException("non satisfiable constraint");
         }
      }

      assert this.watchCumul >= this.computeLeftSide();
   }

   @Override
   public boolean propagate(UnitPropagationListener s, int p) {
      this.voc.watch(p, this);
      assert this.watchCumul >= this.computeLeftSide() : "" + this.watchCumul + "/" + this.computeLeftSide() + ":" + this.learnt;
      long coefP;
      if (this.litToCoeffs == null) {
         int indiceP = 0;

         while ((this.lits[indiceP] ^ 1) != p) {
            indiceP++;
         }

         coefP = this.coefs[indiceP];
      } else {
         coefP = this.litToCoeffs.get(p ^ 1);
      }

      long newcumul = this.watchCumul - coefP;
      if (newcumul < this.degree) {
         assert !this.isSatisfiable();
         return false;
      }

      this.voc.undos(p).push(this);
      this.watchCumul = newcumul;
      int ind = 0;
      int trailPosition = this.voc.getTrailPosition(p);

      for (long limit = this.watchCumul - this.degree; ind < this.coefs.length && limit < this.coefs[ind]; ind++) {
         int lit = this.lits[ind];
         if (this.voc.isFalsified(lit) && this.voc.getTrailPosition(lit) > trailPosition) {
            assert !this.isSatisfiable();
            return false;
         }

         if (this.voc.isUnassigned(lit)) {
            boolean enqueued = s.enqueue(lit, this);
            assert enqueued;
         }
      }

      assert this.learnt || this.watchCumul >= this.computeLeftSide();
      assert this.watchCumul >= this.computeLeftSide();
      return true;
   }

   @Override
   public void remove(UnitPropagationListener upl) {
      for (int i = 0; i < this.lits.length; i++) {
         if (!this.voc.isFalsified(this.lits[i])) {
            this.voc.watches(this.lits[i] ^ 1).remove(this);
         }
      }

      for (int ind = 0; ind < this.coefs.length && this.watchCumul - this.coefs[ind] < this.degree; ind++) {
         if (!this.voc.isUnassigned(this.lits[ind]) && this.voc.getReason(this.lits[ind]) == this) {
            upl.unset(this.lits[ind]);
         }
      }
   }

   @Override
   public void undo(int p) {
      long coefP;
      if (this.litToCoeffs == null) {
         int indiceP = 0;

         while (indiceP < this.lits.length && (this.lits[indiceP] ^ 1) != p) {
            indiceP++;
         }

         coefP = indiceP == this.lits.length ? 0L : this.coefs[indiceP];
      } else {
         Long coefL = this.litToCoeffs.get(p ^ 1);
         if (coefL != null) {
            coefP = coefL;
         } else {
            coefP = 0L;
         }
      }

      this.watchCumul += coefP;
   }

   public static MaxWatchPbLong normalizedMaxWatchPbNew(
      UnitPropagationListener s, ILits voc, int[] lits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs
   ) throws ContradictionException {
      MaxWatchPbLong outclause = new MaxWatchPbLong(voc, lits, coefs, degree, sumCoefs);
      if (outclause.degree <= 0L) {
         return null;
      }

      outclause.computeWatches();
      outclause.computePropagation(s);
      return outclause;
   }

   public static WatchPbLong normalizedWatchPbNew(ILits voc, IDataStructurePB mpb) {
      return new MaxWatchPbLong(voc, mpb);
   }

   @Override
   public boolean propagatePI(MandatoryLiteralListener l, int p) {
      this.voc.watch(p, this);
      long coefP;
      if (this.litToCoeffs == null) {
         int indiceP = 0;

         while ((this.lits[indiceP] ^ 1) != p) {
            indiceP++;
         }

         coefP = this.coefs[indiceP];
      } else {
         coefP = this.litToCoeffs.get(p ^ 1);
      }

      long newcumul = this.watchCumul - coefP;
      this.voc.undos(p).push(this);
      this.watchCumul = newcumul;
      int ind = 0;

      for (long limit = this.watchCumul - this.degree; ind < this.coefs.length && limit < this.coefs[ind]; ind++) {
         if (!this.voc.isFalsified(this.lits[ind])) {
            l.isMandatory(this.lits[ind]);
         }
      }

      return true;
   }

   @Override
   public int getAssertionLevel(IVecInt trail, int decisionLevel) {
      Set<Integer> litsSet = new HashSet<>();
      int[] i = this.lits;
      int var5 = i.length;

      for (int var6 = 0; var6 < var5; var6++) {
         Integer ix = i[var6];
         litsSet.add(ix);
      }

      for (int ix = 0; ix < trail.size(); ix++) {
         if (litsSet.contains(trail.get(ix) ^ 1)) {
            return ix;
         }
      }

      return -1;
   }

   private static class WatchPbLongPbConstrDecorator implements PBConstr {
      private final WatchPbLong cstr;

      public WatchPbLongPbConstrDecorator(WatchPbLong cstr) {
         this.cstr = cstr;
      }

      public boolean propagatePI(MandatoryLiteralListener l, int p) {
         return this.cstr.propagatePI(l, p);
      }

      public boolean isAssertive(int dl) {
         return this.cstr.isAssertive(dl);
      }

      @Override
      public void calcReason(int p, IVecInt outReason) {
         this.cstr.calcReason(p, outReason);
      }

      @Override
      public int get(int i) {
         return this.cstr.get(i);
      }

      @Override
      public double getActivity() {
         return this.cstr.getActivity();
      }

      @Override
      public int getAssertionLevel(IVecInt trail, int decisionLevel) {
         return this.cstr.getAssertionLevel(trail, decisionLevel);
      }

      @Override
      public void incActivity(double claInc) {
         this.cstr.incActivity(claInc);
      }

      @Override
      public void setActivity(double d) {
         this.cstr.setActivity(d);
      }

      public long slackConstraint() {
         return this.cstr.slackConstraint();
      }

      public long slackConstraint(long[] theCoefs, long theDegree) {
         return this.cstr.slackConstraint(theCoefs, theDegree);
      }

      public long computeLeftSide(long[] theCoefs) {
         return this.cstr.computeLeftSide(theCoefs);
      }

      public long computeLeftSide() {
         return this.cstr.computeLeftSide();
      }

      @Override
      public boolean learnt() {
         return this.cstr.learnt();
      }

      @Override
      public boolean locked() {
         return this.cstr.locked();
      }

      @Override
      public void rescaleBy(double d) {
         this.cstr.rescaleBy(d);
      }

      @Override
      public void setLearnt() {
         this.cstr.setLearnt();
      }

      @Override
      public boolean simplify() {
         return this.cstr.simplify();
      }

      @Override
      public final int size() {
         return this.cstr.size();
      }

      @Override
      public String toString() {
         return this.cstr.toString();
      }

      @Override
      public void assertConstraint(UnitPropagationListener s) {
         this.cstr.assertConstraint(s);
      }

      @Override
      public void assertConstraintIfNeeded(UnitPropagationListener s) {
         this.cstr.assertConstraintIfNeeded(s);
      }

      @Override
      public void register() {
         this.cstr.register();
      }

      @Override
      public int[] getLits() {
         return this.cstr.getLits();
      }

      @Override
      public ILits getVocabulary() {
         return this.cstr.getVocabulary();
      }

      @Override
      public IVecInt computeAnImpliedClause() {
         return this.cstr.computeAnImpliedClause();
      }

      public boolean coefficientsEqualToOne() {
         return this.cstr.coefficientsEqualToOne();
      }

      @Override
      public boolean equals(Object pb) {
         return this.cstr.equals(pb);
      }

      @Override
      public int hashCode() {
         return this.cstr.hashCode();
      }

      @Override
      public void forwardActivity(double claInc) {
         this.cstr.forwardActivity(claInc);
      }

      @Override
      public void remove(UnitPropagationListener upl) {
         this.cstr.remove(upl);
      }

      public boolean propagate(UnitPropagationListener s, int p) {
         return this.cstr.propagate(s, p);
      }

      public void undo(int p) {
         this.cstr.undo(p);
      }

      @Override
      public boolean canBePropagatedMultipleTimes() {
         return this.cstr.canBePropagatedMultipleTimes();
      }

      public Constr toConstraint() {
         return this.cstr.toConstraint();
      }

      @Override
      public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
         this.cstr.calcReasonOnTheFly(p, trail, outReason);
      }

      @Override
      public boolean canBeSatisfiedByCountingLiterals() {
         return this.cstr.canBeSatisfiedByCountingLiterals();
      }

      @Override
      public int requiredNumberOfSatisfiedLiterals() {
         return this.cstr.requiredNumberOfSatisfiedLiterals();
      }

      @Override
      public boolean isSatisfied() {
         return this.cstr.isSatisfied();
      }

      @Override
      public String toString(VarMapper mapper) {
         return this.cstr.toString(mapper);
      }

      @Override
      public BigInteger getCoef(int literal) {
         return this.cstr.getCoef(literal);
      }

      @Override
      public BigInteger getDegree() {
         return this.cstr.getDegree();
      }

      @Override
      public BigInteger[] getCoefs() {
         return this.cstr.getCoefs();
      }

      @Override
      public String dump() {
         return this.cstr.dump();
      }

      @Override
      public BigInteger getSumCoefs() {
         return this.cstr.getSumCoefs();
      }
   }
}
