package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.pb.core.PBSolverStats;

public class ConflictMapRounding extends ConflictMap {
   public ConflictMapRounding(
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
      return new ConflictMapRounding(cpb, level, noRemove, skip, preprocess, postProcessing, weakeningStrategy, autoDivisionStrategy, stats);
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
            return ConflictMapRounding.createConflict(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
         }

         @Override
         public String toString() {
            return "Always round the coefficient of the propagating literal to 1 during conflict analysis.";
         }
      };
   }

   static BigInteger ceildiv(BigInteger p, BigInteger q) {
      return p.add(q).subtract(BigInteger.ONE).divide(q);
   }

   @Override
   protected BigInteger reduceUntilConflict(int x, int xindex, BigInteger[] abc, BigInteger t, IWatchPb xyz) {
      BigInteger a = abc[xindex];
      int n = xyz.size();
      BigInteger sprime = BigInteger.ZERO;

      for (int k = 0; k < n; k++) {
         if (!this.voc.isFalsified(xyz.get(k))) {
            sprime = sprime.add(ceildiv(abc[k], a));
         }
      }

      BigInteger tprime = ceildiv(t, a);
      sprime = sprime.subtract(tprime);
      BigInteger bigt = t;
      boolean easyRounding = true;

      for (int k = 0; k < n; k++) {
         if (!sprime.equals(BigInteger.ZERO) && !this.voc.isFalsified(xyz.get(k)) && !abc[k].mod(a).equals(BigInteger.ZERO)) {
            bigt = bigt.subtract(abc[k]);
            BigInteger tnewprime = ceildiv(bigt, a);
            sprime = sprime.add(tprime).subtract(ceildiv(abc[k], a)).subtract(tnewprime);
            tprime = tnewprime;
            abc[k] = BigInteger.ZERO;
            easyRounding = false;
         } else {
            abc[k] = ceildiv(abc[k], a);
         }
      }

      tprime = this.saturation(abc, tprime, xyz);
      this.coefMultCons = this.weightedLits.get(x ^ 1);
      this.coefMult = BigInteger.ONE;
      this.stats.incNumberOfRoundingOperations();
      if (easyRounding) {
         this.stats.incNumberOfEasyRoundingOperations();
      }

      return tprime;
   }
}
