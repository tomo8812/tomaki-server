package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.UnitPropagationListener;

public final class PuebloMinWatchPb extends MinWatchPb {
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !PuebloMinWatchPb.class.desiredAssertionStatus();

   private PuebloMinWatchPb(ILits voc, int[] lits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs) {
      super(voc, lits, coefs, degree, sumCoefs);
   }

   private PuebloMinWatchPb(ILits voc, IDataStructurePB mpb) {
      super(voc, mpb);
   }

   public static PuebloMinWatchPb normalizedMinWatchPbNew(UnitPropagationListener s, ILits voc, int[] lits, BigInteger[] coefs, BigInteger degree) throws ContradictionException {
      BigInteger sumCoefs = BigInteger.ZERO;

      for (BigInteger c : coefs) {
         sumCoefs = sumCoefs.add(c);
      }

      PuebloMinWatchPb outclause = new PuebloMinWatchPb(voc, lits, coefs, degree, sumCoefs);
      if (outclause.degree.signum() <= 0) {
         return null;
      }

      outclause.computeWatches();
      outclause.computePropagation(s);
      return outclause;
   }

   public static WatchPb normalizedWatchPbNew(ILits voc, IDataStructurePB mpb) {
      return new PuebloMinWatchPb(voc, mpb);
   }

   @Override
   protected BigInteger maximalCoefficient(int pIndice) {
      return this.coefs[0];
   }

   @Override
   protected BigInteger updateWatched(BigInteger mc, int pIndice) {
      BigInteger maxCoef = mc;
      if (this.watchingCount < this.size()) {
         BigInteger upWatchCumul = this.watchCumul.subtract(this.coefs[pIndice]);
         BigInteger borneSup = this.degree.add(maxCoef);

         for (int ind = 0; ind < this.lits.length && upWatchCumul.compareTo(borneSup) < 0; ind++) {
            if (!this.voc.isFalsified(this.lits[ind]) && !this.watched[ind]) {
               upWatchCumul = upWatchCumul.add(this.coefs[ind]);
               this.watched[ind] = true;
               assert this.watchingCount < this.size();
               this.watching[this.watchingCount++] = ind;
               this.voc.watch(this.lits[ind] ^ 1, this);
            }
         }

         this.watchCumul = upWatchCumul.add(this.coefs[pIndice]);
      }

      return maxCoef;
   }
}
