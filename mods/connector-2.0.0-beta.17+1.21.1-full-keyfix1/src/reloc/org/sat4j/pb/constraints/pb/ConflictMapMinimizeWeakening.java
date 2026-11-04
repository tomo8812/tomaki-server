package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.VarActivityListener;
import reloc.org.sat4j.pb.core.PBSolverStats;
import reloc.org.sat4j.specs.IVecInt;

public class ConflictMapMinimizeWeakening extends ConflictMap {
   private final Map<BigInteger, IVecInt> map = new TreeMap<>();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ConflictMapMinimizeWeakening.class.desiredAssertionStatus();

   public ConflictMapMinimizeWeakening(
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
      return new ConflictMapMinimizeWeakening(cpb, level, noRemove, skip, preprocess, postProcessing, weakeningStrategy, autoDivisionStrategy, stats);
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
            return ConflictMapMinimizeWeakening.createConflict(
               cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats
            );
         }

         @Override
         public String toString() {
            return "Weaken as few literals as possible in the reason";
         }
      };
   }

   @Override
   public BigInteger reduceInConstraint(IWatchPb wpb, BigInteger[] coefsBis, int indLitImplied, BigInteger degreeBis, BigInteger slackResolve) {
      assert degreeBis.compareTo(BigInteger.ONE) > 0;
      int lit = this.findLiteralToRemove(wpb, coefsBis, indLitImplied, degreeBis);
      assert lit != -1;
      assert lit != indLitImplied;
      BigInteger degUpdate = degreeBis.subtract(coefsBis[lit]);
      this.possReducedCoefs = this.possReducedCoefs.subtract(coefsBis[lit]);
      coefsBis[lit] = BigInteger.ZERO;
      assert this.possReducedCoefs.equals(this.possConstraint(wpb, coefsBis));
      degUpdate = this.saturation(coefsBis, degUpdate, wpb);
      assert coefsBis[indLitImplied].signum() > 0;
      assert degreeBis.compareTo(degUpdate) > 0;
      assert this.possReducedCoefs.equals(this.possConstraint(wpb, coefsBis));
      return degUpdate;
   }

   private int findLiteralToRemove(IWatchPb wpb, BigInteger[] coefsBis, int indLitImplied, BigInteger degreeBis) {
      if (this.map.isEmpty()) {
         for (int i = 0; i < coefsBis.length; i++) {
            BigInteger coef = coefsBis[i];
            int lit = wpb.get(i);
            if (i != indLitImplied && !this.voc.isFalsified(lit)) {
               IVecInt vec = this.map.get(coef);
               if (vec == null) {
                  vec = new VecInt();
                  this.map.put(coef, vec);
               }

               vec.push(i);
            }
         }
      }

      IVecInt vec = this.map.entrySet().stream().findFirst().get().getValue();
      int lit = vec.last();
      vec.pop();
      return lit;
   }

   @Override
   public BigInteger resolve(PBConstr cpb, int litImplied, VarActivityListener val) {
      BigInteger res = super.resolve(cpb, litImplied, val);
      this.map.clear();
      return res;
   }
}
