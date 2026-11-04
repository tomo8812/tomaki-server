package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.pb.core.PBSolverStats;

public class ConflictMapWeakenReason extends ConflictMap {
   public ConflictMapWeakenReason(
      PBConstr cpb,
      int level,
      boolean noRemove,
      SkipStrategy skip,
      IPreProcess preprocess,
      IPostProcess postProcessing,
      IWeakeningStrategy weakeningStrategy,
      AutoDivisionStrategy autoDivisionStrategy,
      PBSolverStats stats
   ) {
      super(cpb, level, noRemove, skip, preprocess, postProcessing, weakeningStrategy, autoDivisionStrategy, stats);
   }

   public static IConflict createConflict(
      PBConstr cpb,
      int level,
      boolean noRemove,
      SkipStrategy skip,
      IPreProcess preprocess,
      IPostProcess postProcessing,
      IWeakeningStrategy weakeningStrategy,
      AutoDivisionStrategy autoDivisionStrategy,
      PBSolverStats stats
   ) {
      return new ConflictMapWeakenReason(cpb, level, noRemove, skip, preprocess, postProcessing, weakeningStrategy, autoDivisionStrategy, stats);
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
            return ConflictMapWeakenReason.createConflict(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
         }

         @Override
         public String toString() {
            return "Weaken the reasons to as few literals as possible";
         }
      };
   }

   @Override
   protected BigInteger reduceUntilConflict(int litImplied, int ind, BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
      BigInteger degree = degreeReduced;

      for (int i = 0; i < wpb.size(); i++) {
         if (i != ind && !this.voc.isFalsified(wpb.get(i))) {
            degree = degree.subtract(reducedCoefs[i]);
            reducedCoefs[i] = BigInteger.ZERO;
         }
      }

      for (int i = 0; i < wpb.size() && degree.compareTo(BigInteger.ONE) > 0; i++) {
         if (this.voc.isFalsified(wpb.get(i)) && reducedCoefs[i].compareTo(degree) < 0) {
            degree = degree.subtract(reducedCoefs[i]);
            reducedCoefs[i] = BigInteger.ZERO;
            this.stats.incFalsifiedLiteralsRemovedFromReason();
         }
      }

      degree = this.saturation(reducedCoefs, degree, wpb);
      return super.reduceUntilConflict(litImplied, ind, reducedCoefs, degree, wpb);
   }
}
