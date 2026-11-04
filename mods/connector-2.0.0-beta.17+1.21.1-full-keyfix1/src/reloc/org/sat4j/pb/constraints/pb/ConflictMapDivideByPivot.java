package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.pb.core.PBSolverStats;

public class ConflictMapDivideByPivot extends ConflictMap {
   final boolean divideReason;
   final DivisionStrategy divisionStrategy;
   final IReduceConflictStrategy reduceConflict;

   public ConflictMapDivideByPivot(
      PBConstr cpb,
      int level,
      boolean noRemove,
      SkipStrategy skip,
      IPreProcess preprocess,
      IPostProcess postProcessing,
      IWeakeningStrategy weakeningStrategy,
      AutoDivisionStrategy autoDivisionStrategy,
      PBSolverStats stats,
      DivisionStrategy divisionStrategy,
      IReduceConflictStrategy reduceConflict,
      boolean divideReason
   ) {
      super(cpb, level, noRemove, skip, preprocess, postProcessing, weakeningStrategy, autoDivisionStrategy, stats);
      this.divisionStrategy = divisionStrategy;
      this.reduceConflict = reduceConflict;
      this.divideReason = divideReason;
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
      PBSolverStats stats,
      DivisionStrategy divisionStrategy,
      IReduceConflictStrategy reduceConflict,
      boolean divideReason
   ) {
      return new ConflictMapDivideByPivot(
         cpb, level, noRemove, skip, preprocess, postProcessing, weakeningStrategy, autoDivisionStrategy, stats, divisionStrategy, reduceConflict, divideReason
      );
   }

   public static IConflictFactory fullWeakeningOnReasonFactory() {
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
            return ConflictMapDivideByPivot.createConflict(
               cpb,
               level,
               noRemove,
               skip,
               preprocess,
               postprocess,
               weakeningStrategy,
               autoDivisionStrategy,
               stats,
               DivisionStrategy.FULL_WEAKENING,
               NoReduceConflict.instance(),
               true
            );
         }

         @Override
         public String toString() {
            return "Divide the reason by the coefficient of the pivot when resolving, and weaken away non-divisible coefficient.";
         }
      };
   }

   public static IConflictFactory partialWeakeningOnReasonFactory() {
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
            return ConflictMapDivideByPivot.createConflict(
               cpb,
               level,
               noRemove,
               skip,
               preprocess,
               postprocess,
               weakeningStrategy,
               autoDivisionStrategy,
               stats,
               DivisionStrategy.PARTIAL_WEAKENING,
               NoReduceConflict.instance(),
               true
            );
         }

         @Override
         public String toString() {
            return "Divide the reason by the coefficient of the pivot when resolving, and partially weaken non-divisible coefficient.";
         }
      };
   }

   public static IConflictFactory fullWeakeningOnBothFactory() {
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
            return ConflictMapDivideByPivot.createConflict(
               cpb,
               level,
               noRemove,
               skip,
               preprocess,
               postprocess,
               weakeningStrategy,
               autoDivisionStrategy,
               stats,
               DivisionStrategy.FULL_WEAKENING,
               ReduceConflict.instance(),
               true
            );
         }

         @Override
         public String toString() {
            return "Divide both constraints by the coefficient of the pivot when resolving, and weaken away non-divisible coefficient.";
         }
      };
   }

   public static IConflictFactory partialWeakeningOnBothFactory() {
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
            return ConflictMapDivideByPivot.createConflict(
               cpb,
               level,
               noRemove,
               skip,
               preprocess,
               postprocess,
               weakeningStrategy,
               autoDivisionStrategy,
               stats,
               DivisionStrategy.PARTIAL_WEAKENING,
               ReduceConflict.instance(),
               true
            );
         }

         @Override
         public String toString() {
            return "Divide both constraints by the coefficient of the pivot when resolving, and partially weaken non-divisible coefficient.";
         }
      };
   }

   public static IConflictFactory fullWeakeningOnConflictFactory() {
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
            return ConflictMapDivideByPivot.createConflict(
               cpb,
               level,
               noRemove,
               skip,
               preprocess,
               postprocess,
               weakeningStrategy,
               autoDivisionStrategy,
               stats,
               DivisionStrategy.FULL_WEAKENING,
               ReduceConflict.instance(),
               false
            );
         }

         @Override
         public String toString() {
            return "Divide the conflict by the coefficient of the pivot when resolving, and weaken away non-divisible coefficient.";
         }
      };
   }

   public static IConflictFactory partialWeakeningOnConflictFactory() {
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
            return ConflictMapDivideByPivot.createConflict(
               cpb,
               level,
               noRemove,
               skip,
               preprocess,
               postprocess,
               weakeningStrategy,
               autoDivisionStrategy,
               stats,
               DivisionStrategy.PARTIAL_WEAKENING,
               ReduceConflict.instance(),
               false
            );
         }

         @Override
         public String toString() {
            return "Divide the conflict by the coefficient of the pivot when resolving, and partially weaken non-divisible coefficient.";
         }
      };
   }

   @Override
   protected BigInteger reduceUntilConflict(int litImplied, int ind, BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
      BigInteger coeffImplied = reducedCoefs[ind];
      BigInteger outputDegree = degreeReduced;
      int size = wpb.size();
      if (this.divideReason) {
         for (int i = 0; i < size; i++) {
            if (this.voc.isFalsified(wpb.get(i))) {
               reducedCoefs[i] = ceildiv(reducedCoefs[i], coeffImplied);
            } else {
               BigInteger[] tmp = this.divisionStrategy.divide(reducedCoefs[i], coeffImplied);
               reducedCoefs[i] = tmp[0];
               outputDegree = outputDegree.subtract(tmp[1]);
            }
         }

         outputDegree = this.saturation(reducedCoefs, ceildiv(outputDegree, coeffImplied), wpb);
      }

      this.stats.incNumberOfRoundingOperations();
      this.reduceConflict.reduceConflict(this, litImplied ^ 1);
      this.coefMultCons = this.weightedLits.get(litImplied ^ 1);
      this.coefMult = reducedCoefs[ind];
      return outputDegree;
   }

   static BigInteger ceildiv(BigInteger p, BigInteger q) {
      return p.add(q).subtract(BigInteger.ONE).divide(q);
   }
}
