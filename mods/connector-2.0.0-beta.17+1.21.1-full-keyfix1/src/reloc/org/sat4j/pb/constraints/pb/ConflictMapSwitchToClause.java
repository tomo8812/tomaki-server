package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.pb.core.PBSolverStats;

public final class ConflictMapSwitchToClause extends ConflictMap {
   private static int upperBound;

   public ConflictMapSwitchToClause(PBConstr cpb, int level) {
      super(cpb, level);
   }

   public ConflictMapSwitchToClause(PBConstr cpb, int level, boolean noRemove, SkipStrategy skip, PBSolverStats stats) {
      super(cpb, level, noRemove, skip, stats);
   }

   @Override
   protected BigInteger reduceUntilConflict(int litImplied, int ind, BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
      BigInteger degreeCons = super.reduceUntilConflict(litImplied, ind, reducedCoefs, degreeReduced, wpb);
      int i = 0;

      while (i < reducedCoefs.length && reducedCoefs[i].equals(BigInteger.ZERO) && i != ind) {
         i++;
      }

      if (i < reducedCoefs.length) {
         BigInteger bigCoef = reducedCoefs[i].multiply(this.coefMultCons);
         if (this.weightedLits.containsKey(wpb.get(i))) {
            bigCoef = bigCoef.add(this.weightedLits.get(wpb.get(i)).multiply(this.coefMult));
         }

         if (bigCoef.toString().length() > upperBound) {
            this.numberOfReductions++;
            this.hasBeenReduced = true;
            degreeCons = this.reduceToClause(ind, wpb, reducedCoefs);
            this.coefMultCons = this.weightedLits.get(litImplied ^ 1);
            this.coefMult = BigInteger.ONE;
         }
      }

      return degreeCons;
   }

   private BigInteger reduceToClause(int ind, IWatchPb wpb, BigInteger[] reducedCoefs) {
      for (int i = 0; i < reducedCoefs.length; i++) {
         if (i != ind && !wpb.getVocabulary().isFalsified(wpb.get(i))) {
            reducedCoefs[i] = BigInteger.ZERO;
         } else {
            reducedCoefs[i] = BigInteger.ONE;
         }
      }

      return BigInteger.ONE;
   }

   public static int getUpperBound() {
      return upperBound;
   }

   public static void setUpperBound(int upperBound) {
      ConflictMapSwitchToClause.upperBound = upperBound;
   }
}
