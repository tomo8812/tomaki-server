package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.pb.core.PBSolverStats;

public class ConflictMapReduceToCard extends ConflictMap {
   public static final BigInteger MAXVALUE = BigInteger.valueOf(Long.MAX_VALUE);

   public ConflictMapReduceToCard(
      PBConstr cpb,
      int level,
      boolean noRemove,
      SkipStrategy skip,
      IPreProcess preprocess,
      IPostProcess postprocess,
      IWeakeningStrategy weakeningStrategy,
      AutoDivisionStrategy autoDivisionStrategy,
      PBSolverStats stats
   ) {
      super(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
   }

   public static IConflict createConflict(
      PBConstr cpb,
      int level,
      boolean noRemove,
      SkipStrategy skip,
      IPreProcess preprocess,
      IPostProcess postprocess,
      IWeakeningStrategy weakeningStrategy,
      AutoDivisionStrategy autoDivisionStrategy,
      PBSolverStats stats
   ) {
      return new ConflictMapReduceToCard(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
   }

   public static IConflictFactory factory() {
      return new IConflictFactory() {
         @Override
         public IConflict createConflict(
            PBConstr cpb,
            int level,
            boolean noRemove,
            SkipStrategy skip,
            IPreProcess preprocess,
            IPostProcess postprocess,
            IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy,
            PBSolverStats stats
         ) {
            return ConflictMapReduceToCard.createConflict(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
         }

         @Override
         public String toString() {
            return "Reduce to cardinality constraint during conflict analysis if necessary";
         }
      };
   }

   @Override
   protected BigInteger reduceUntilConflict(int litImplied, int ind, BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
      BigInteger degreeFurtherReduced = this.reduceToCard(ind, wpb, reducedCoefs);
      this.coefMultCons = this.weightedLits.get(litImplied ^ 1);
      this.coefMult = BigInteger.ONE;
      this.numberOfReductions++;
      return degreeFurtherReduced;
   }

   private BigInteger reduceToCard(int ind, IWatchPb wpb, BigInteger[] reducedCoefs) {
      BigInteger[] tmpCoefs = new BigInteger[reducedCoefs.length];
      BigInteger maxCoef = BigInteger.ZERO;

      for (int i = 0; i < reducedCoefs.length; i++) {
         if (i != ind && !wpb.getVocabulary().isFalsified(wpb.get(i))) {
            tmpCoefs[i] = BigInteger.ZERO;
         } else {
            tmpCoefs[i] = BigInteger.ONE;
            if (reducedCoefs[i].compareTo(maxCoef) > 0) {
               maxCoef = reducedCoefs[i];
            }
         }
      }

      int cpt = 0;

      for (int i = 0; i < reducedCoefs.length; i++) {
         if (tmpCoefs[i].equals(BigInteger.ZERO) && reducedCoefs[i].compareTo(maxCoef) > 0) {
            reducedCoefs[i] = BigInteger.ONE;
            cpt++;
         } else {
            reducedCoefs[i] = tmpCoefs[i];
         }
      }

      return BigInteger.valueOf(cpt + 1L);
   }
}
