package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.pb.core.PBSolverStats;

public final class ConflictMapReduceToClause extends ConflictMap {
   public static final BigInteger MAXVALUE = BigInteger.valueOf(Long.MAX_VALUE);

   public ConflictMapReduceToClause(
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
      return new ConflictMapReduceToClause(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
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
            return ConflictMapReduceToClause.createConflict(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
         }

         @Override
         public String toString() {
            return "Reduce to clause during conflict analysis if necessary";
         }
      };
   }

   @Override
   protected BigInteger reduceUntilConflict(int litImplied, int ind, BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
      BigInteger coefLitImplied = this.weightedLits.get(litImplied ^ 1);
      if (reducedCoefs[0].multiply(coefLitImplied).compareTo(MAXVALUE) <= 0
         && reducedCoefs[ind].multiply(this.weightedLits.getCoef(0)).compareTo(MAXVALUE) <= 0) {
         return super.reduceUntilConflict(litImplied, ind, reducedCoefs, degreeReduced, wpb);
      }

      degreeReduced = this.reduceToClause(ind, wpb, reducedCoefs);
      this.coefMultCons = this.weightedLits.get(litImplied ^ 1);
      this.coefMult = BigInteger.ONE;
      this.numberOfReductions++;
      return degreeReduced;
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
}
