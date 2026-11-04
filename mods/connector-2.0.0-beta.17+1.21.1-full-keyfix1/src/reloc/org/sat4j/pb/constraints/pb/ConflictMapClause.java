package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

public final class ConflictMapClause extends ConflictMap {
   public ConflictMapClause(PBConstr cpb, int level) {
      super(cpb, level);
   }

   public ConflictMapClause(PBConstr cpb, int level, boolean noRemove) {
      super(cpb, level, noRemove);
   }

   public static IConflict createConflict(PBConstr cpb, int level) {
      return new ConflictMapClause(cpb, level);
   }

   public static IConflict createConflict(PBConstr cpb, int level, boolean noRemove) {
      return new ConflictMapClause(cpb, level, noRemove);
   }

   @Override
   protected BigInteger reduceUntilConflict(int litImplied, int ind, BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
      for (int i = 0; i < reducedCoefs.length; i++) {
         if (i != ind && !wpb.getVocabulary().isFalsified(wpb.get(i))) {
            reducedCoefs[i] = BigInteger.ZERO;
         } else {
            reducedCoefs[i] = BigInteger.ONE;
         }
      }

      this.coefMultCons = this.weightedLits.get(litImplied ^ 1);
      this.coefMult = BigInteger.ONE;
      return BigInteger.ONE;
   }
}
