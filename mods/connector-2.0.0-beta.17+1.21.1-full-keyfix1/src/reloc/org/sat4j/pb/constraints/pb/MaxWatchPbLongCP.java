package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public final class MaxWatchPbLongCP extends WatchPbLongCP {
   private static final long serialVersionUID = 1L;
   private long watchCumul = 0L;
   private final Map<Integer, Long> litToCoeffs;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MaxWatchPbLongCP.class.desiredAssertionStatus();

   private MaxWatchPbLongCP(ILits voc, IDataStructurePB mpb) {
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

   private MaxWatchPbLongCP(ILits voc, int[] lits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs) {
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
            coefP = this.litToCoeffs.get(p ^ 1);
         } else {
            coefP = 0L;
         }
      }

      this.watchCumul += coefP;
   }

   public static MaxWatchPbLongCP normalizedMaxWatchPbNew(
      UnitPropagationListener s, ILits voc, int[] lits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs
   ) throws ContradictionException {
      MaxWatchPbLongCP outclause = new MaxWatchPbLongCP(voc, lits, coefs, degree, sumCoefs);
      if (outclause.degree <= 0L) {
         return null;
      }

      outclause.computeWatches();
      outclause.computePropagation(s);
      return outclause;
   }

   public static WatchPbLongCP normalizedWatchPbNew(ILits voc, IDataStructurePB mpb) {
      return new MaxWatchPbLongCP(voc, mpb);
   }

   @Override
   public int getAssertionLevel(IVecInt trail, int decisionLevel) {
      throw new UnsupportedOperationException("To be done");
   }
}
