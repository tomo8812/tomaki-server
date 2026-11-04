package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class MinWatchPbLong extends WatchPbLong {
   private static final long serialVersionUID = 1L;
   protected long watchCumul = 0L;
   protected boolean[] watched;
   protected int[] watching;
   protected int watchingCount = 0;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MinWatchPbLong.class.desiredAssertionStatus();

   protected MinWatchPbLong(ILits voc, IDataStructurePB mpb) {
      super(mpb);
      this.voc = voc;
      this.watching = new int[this.coefs.length];
      this.watched = new boolean[this.coefs.length];
      this.activity = 0.0;
      this.watchCumul = 0L;
      this.watchingCount = 0;
   }

   protected MinWatchPbLong(ILits voc, int[] lits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs) {
      super(lits, coefs, degree, sumCoefs);
      this.voc = voc;
      this.watching = new int[this.coefs.length];
      this.watched = new boolean[this.coefs.length];
      this.activity = 0.0;
      this.watchCumul = 0L;
      this.watchingCount = 0;
   }

   @Override
   protected void computeWatches() throws ContradictionException {
      assert this.watchCumul == 0L;
      assert this.watchingCount == 0;

      for (int i = 0; i < this.lits.length && this.watchCumul - this.coefs[0] < this.degree; i++) {
         if (!this.voc.isFalsified(this.lits[i])) {
            this.voc.watch(this.lits[i] ^ 1, this);
            this.watching[this.watchingCount++] = i;
            this.watched[i] = true;
            this.watchCumul = this.watchCumul + this.coefs[i];
         }
      }

      if (this.learnt) {
         this.watchMoreForLearntConstraint();
      }

      if (this.watchCumul < this.degree) {
         throw new ContradictionException("non satisfiable constraint");
      }

      assert this.nbOfWatched() == this.watchingCount;
   }

   private void watchMoreForLearntConstraint() {
      int free = 1;

      while (this.watchCumul - this.coefs[0] < this.degree && free > 0) {
         free = 0;
         int maxlevel = -1;
         int maxi = -1;

         for (int i = 0; i < this.lits.length; i++) {
            if (this.voc.isFalsified(this.lits[i]) && !this.watched[i]) {
               free++;
               int level = this.voc.getLevel(this.lits[i]);
               if (level > maxlevel) {
                  maxi = i;
                  maxlevel = level;
               }
            }
         }

         if (free > 0) {
            assert maxi >= 0;
            this.voc.watch(this.lits[maxi] ^ 1, this);
            this.watching[this.watchingCount++] = maxi;
            this.watched[maxi] = true;
            this.watchCumul = this.watchCumul + this.coefs[maxi];
            assert --free >= 0;
         }
      }

      assert this.lits.length == 1 || this.watchingCount > 1;
   }

   @Override
   protected void computePropagation(UnitPropagationListener s) throws ContradictionException {
      for (int ind = 0; ind < this.lits.length && this.watchCumul - this.coefs[this.watching[ind]] < this.degree; ind++) {
         if (this.voc.isUnassigned(this.lits[ind]) && !s.enqueue(this.lits[ind], this)) {
            throw new ContradictionException("non satisfiable constraint");
         }
      }
   }

   public static MinWatchPbLong normalizedMinWatchPbNew(
      UnitPropagationListener s, ILits voc, int[] lits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs
   ) throws ContradictionException {
      MinWatchPbLong outclause = new MinWatchPbLong(voc, lits, coefs, degree, sumCoefs);
      if (outclause.degree <= 0L) {
         return null;
      }

      outclause.computeWatches();
      outclause.computePropagation(s);
      return outclause;
   }

   protected int nbOfWatched() {
      int retour = 0;

      for (int ind = 0; ind < this.watched.length; ind++) {
         for (int i = 0; i < this.watchingCount; i++) {
            if (this.watching[i] == ind) {
               assert this.watched[ind];
            }
         }

         retour += this.watched[ind] ? 1 : 0;
      }

      return retour;
   }

   @Override
   public boolean propagate(UnitPropagationListener s, int p) {
      assert this.nbOfWatched() == this.watchingCount;
      assert this.watchingCount > 1;
      int pIndiceWatching = 0;

      while (pIndiceWatching < this.watchingCount && (this.lits[this.watching[pIndiceWatching]] ^ 1) != p) {
         pIndiceWatching++;
      }

      int pIndice = this.watching[pIndiceWatching];
      assert p == (this.lits[pIndice] ^ 1);
      assert this.watched[pIndice];
      long maxCoef = this.maximalCoefficient(pIndice);
      maxCoef = this.updateWatched(maxCoef, pIndice);
      long upWatchCumul = this.watchCumul - this.coefs[pIndice];
      assert this.nbOfWatched() == this.watchingCount;
      if (upWatchCumul < this.degree) {
         this.voc.watch(p, this);
         assert this.watched[pIndice];
         assert !this.isSatisfiable();
         return false;
      }

      if (upWatchCumul < this.degree + maxCoef) {
         assert this.watchingCount != 0;
         long limit = upWatchCumul - this.degree;

         for (int i = 0; i < this.watchingCount; i++) {
            if (limit < this.coefs[this.watching[i]]
               && i != pIndiceWatching
               && !this.voc.isSatisfied(this.lits[this.watching[i]])
               && !s.enqueue(this.lits[this.watching[i]], this)) {
               this.voc.watch(p, this);
               assert !this.isSatisfiable();
               return false;
            }
         }

         this.voc.undos(p).push(this);
      }

      this.watched[pIndice] = false;
      this.watchCumul = upWatchCumul;
      this.watching[pIndiceWatching] = this.watching[--this.watchingCount];
      assert this.watchingCount != 0;
      assert this.nbOfWatched() == this.watchingCount;
      return true;
   }

   @Override
   public void remove(UnitPropagationListener upl) {
      for (int i = 0; i < this.watchingCount; i++) {
         this.voc.watches(this.lits[this.watching[i]] ^ 1).remove(this);
         this.watched[this.watching[i]] = false;
      }

      this.watchingCount = 0;
      assert this.nbOfWatched() == this.watchingCount;

      for (int ind = 0; ind < this.coefs.length && this.watchCumul - this.coefs[ind] < this.degree; ind++) {
         if (!this.voc.isUnassigned(this.lits[ind]) && this.voc.getReason(this.lits[ind]) == this) {
            upl.unset(this.lits[ind]);
         }
      }
   }

   @Override
   public void undo(int p) {
      this.voc.watch(p, this);
      int pIndice = 0;

      while ((this.lits[pIndice] ^ 1) != p) {
         pIndice++;
      }

      assert pIndice < this.lits.length;
      this.watchCumul = this.watchCumul + this.coefs[pIndice];
      assert this.watchingCount == this.nbOfWatched();
      this.watched[pIndice] = true;
      this.watching[this.watchingCount++] = pIndice;
      assert this.watchingCount == this.nbOfWatched();
   }

   public static WatchPbLong normalizedWatchPbNew(ILits voc, IDataStructurePB mpb) {
      return new MinWatchPbLong(voc, mpb);
   }

   protected long maximalCoefficient(int pIndice) {
      long maxCoef = 0L;

      for (int i = 0; i < this.watchingCount; i++) {
         if (this.coefs[this.watching[i]] > maxCoef && this.watching[i] != pIndice) {
            maxCoef = this.coefs[this.watching[i]];
         }
      }

      assert this.learnt || maxCoef != 0L;
      return maxCoef;
   }

   protected long updateWatched(long mc, int pIndice) {
      long maxCoef = mc;
      if (this.watchingCount < this.size()) {
         long upWatchCumul = this.watchCumul - this.coefs[pIndice];
         long degreePlusMaxCoef = this.degree + maxCoef;

         for (int ind = 0; ind < this.lits.length && upWatchCumul < degreePlusMaxCoef; ind++) {
            if (!this.voc.isFalsified(this.lits[ind]) && !this.watched[ind]) {
               upWatchCumul += this.coefs[ind];
               this.watched[ind] = true;
               assert this.watchingCount < this.size();
               this.watching[this.watchingCount++] = ind;
               this.voc.watch(this.lits[ind] ^ 1, this);
               if (this.coefs[ind] > maxCoef) {
                  maxCoef = this.coefs[ind];
                  degreePlusMaxCoef = this.degree + maxCoef;
               }
            }
         }

         this.watchCumul = upWatchCumul + this.coefs[pIndice];
      }

      return maxCoef;
   }

   @Override
   public boolean propagatePI(MandatoryLiteralListener l, int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int getAssertionLevel(IVecInt trail, int decisionLevel) {
      throw new UnsupportedOperationException("To be done");
   }
}
