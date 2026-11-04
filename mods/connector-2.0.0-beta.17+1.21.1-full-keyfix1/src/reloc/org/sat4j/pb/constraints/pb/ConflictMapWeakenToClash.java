package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.core.PBSolverStats;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class ConflictMapWeakenToClash extends ConflictMap {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ConflictMapWeakenToClash.class.desiredAssertionStatus();

   public ConflictMapWeakenToClash(
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
      return new ConflictMapWeakenToClash(cpb, level, noRemove, skip, preprocess, postProcessing, weakeningStrategy, autoDivisionStrategy, stats);
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
            return ConflictMapWeakenToClash.createConflict(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
         }

         @Override
         public String toString() {
            return "Weaken reason to find a multiple of the pivot";
         }
      };
   }

   @Override
   protected BigInteger reduceUntilConflict(int litImplied, int ind, BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
      BigInteger slackResolve = BigInteger.ONE.negate();
      BigInteger slackThis = BigInteger.ZERO;
      BigInteger slackConflict = this.slackConflict();
      BigInteger reducedDegree = degreeReduced;
      BigInteger previousCoefLitImplied = BigInteger.ZERO;
      BigInteger[] mult = new BigInteger[]{BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO};
      BigInteger coefLitImplied = this.weightedLits.get(litImplied ^ 1);
      this.possReducedCoefs = this.possConstraint(wpb, reducedCoefs);

      do {
         if (slackResolve.signum() >= 0) {
            assert slackThis.signum() > 0;
            BigInteger tmp = this.reduceInConstraint(wpb, reducedCoefs, ind, reducedDegree, slackResolve);
            assert tmp.compareTo(reducedDegree) < 0 && tmp.compareTo(BigInteger.ONE) >= 0;
            reducedDegree = tmp;
         }

         assert this.weightedLits.get(litImplied ^ 1).signum() > 0;
         assert reducedCoefs[ind].signum() > 0;
         if (!reducedCoefs[ind].equals(previousCoefLitImplied)) {
            assert coefLitImplied.equals(this.weightedLits.get(litImplied ^ 1));
            mult = this.findMult(coefLitImplied, reducedCoefs[ind]);
            assert mult[0].signum() > 0;
            assert mult[1].signum() > 0;
            this.coefMult = mult[0];
            this.coefMultCons = mult[1];
            assert this.coefMult.multiply(coefLitImplied).equals(this.coefMultCons.multiply(reducedCoefs[ind]).subtract(mult[2]));
            previousCoefLitImplied = reducedCoefs[ind];
         }

         slackThis = this.possReducedCoefs.subtract(reducedDegree).multiply(this.coefMultCons);
         assert slackThis.equals(wpb.slackConstraint(reducedCoefs, reducedDegree).multiply(this.coefMultCons));
         assert slackConflict.equals(this.slackConflict());
         BigInteger slackIndex = slackConflict.multiply(this.coefMult);
         assert slackIndex.signum() <= 0;
         slackResolve = slackThis.add(slackIndex);
      } while (slackResolve.signum() >= 0 || this.isUnsat());

      Map<BigInteger, IVecInt> coefsSat = new TreeMap<>();
      Map<BigInteger, IVecInt> coefsFals = new TreeMap<>();

      for (int i = 0; i < reducedCoefs.length; i++) {
         BigInteger coef = reducedCoefs[i].multiply(this.coefMultCons);
         reducedCoefs[i] = coef;
         if (!this.voc.isFalsified(wpb.get(i)) && i != ind) {
            IVecInt lits = coefsSat.get(coef);
            if (lits == null) {
               lits = new VecInt();
               coefsSat.put(coef, lits);
            }

            lits.push(i);
         } else if (this.voc.isFalsified(wpb.get(i))) {
            IVecInt lits = coefsFals.get(coef);
            if (lits == null) {
               lits = new VecInt();
               coefsFals.put(coef, lits);
            }

            lits.push(i);
         }
      }

      reducedDegree = reducedDegree.multiply(this.coefMultCons);
      this.coefMultCons = BigInteger.ONE;
      if (mult[2].signum() == 0) {
         reducedDegree = this.saturation(reducedCoefs, reducedDegree, wpb);
         if (reducedDegree.equals(BigInteger.ONE)) {
            this.coefMultCons = coefLitImplied;
         }

         return reducedDegree;
      }

      BigInteger expected = this.coefMult.multiply(coefLitImplied);
      BigInteger toWeaken = reducedDegree.subtract(expected);
      assert toWeaken.signum() >= 0;

      for (Entry<BigInteger, IVecInt> entry : coefsSat.entrySet()) {
         if (toWeaken.signum() == 0) {
            break;
         }

         BigInteger coef = entry.getKey();

         for (IteratorInt it = entry.getValue().iterator(); it.hasNext(); reducedDegree = reducedDegree.subtract(coef)) {
            int index = it.next();
            if (coef.compareTo(toWeaken) > 0) {
               reducedCoefs[index] = coef.subtract(toWeaken);
               reducedDegree = reducedDegree.subtract(toWeaken);
               toWeaken = BigInteger.ZERO;
               break;
            }

            reducedCoefs[index] = BigInteger.ZERO;
            toWeaken = toWeaken.subtract(coef);
         }
      }

      if (toWeaken.signum() > 0 && reducedCoefs[ind].compareTo(reducedDegree) < 0) {
         for (Entry<BigInteger, IVecInt> entry : coefsFals.entrySet()) {
            if (toWeaken.signum() == 0) {
               break;
            }

            BigInteger coef = entry.getKey();
            IteratorInt it = entry.getValue().iterator();

            while (it.hasNext()) {
               int index = it.next();
               if (!this.weightedLits.containsKey(wpb.get(index) ^ 1)) {
                  this.stats.incFalsifiedLiteralsRemovedFromReason();
                  if (coef.compareTo(toWeaken) > 0) {
                     reducedCoefs[index] = coef.subtract(toWeaken);
                     reducedDegree = reducedDegree.subtract(toWeaken);
                     toWeaken = BigInteger.ZERO;
                     break;
                  }

                  reducedCoefs[index] = BigInteger.ZERO;
                  toWeaken = toWeaken.subtract(coef);
                  reducedDegree = reducedDegree.subtract(coef);
               }
            }
         }
      }

      if (toWeaken.signum() > 0) {
         toWeaken = toWeaken.min(reducedCoefs[ind].subtract(expected));
         reducedCoefs[ind] = reducedCoefs[ind].subtract(toWeaken);
         reducedDegree = reducedDegree.subtract(toWeaken);
      }

      reducedDegree = this.saturation(reducedCoefs, reducedDegree, wpb);
      if (reducedDegree.equals(BigInteger.ONE)) {
         this.coefMultCons = coefLitImplied;
         this.coefMult = BigInteger.ONE;
      }

      return reducedDegree;

      throw new AssertionError();
   }

   protected BigInteger[] findMult(BigInteger conflict, BigInteger reason) {
      int cmp = conflict.compareTo(reason);
      if (cmp == 0) {
         return new BigInteger[]{BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO};
      } else if (cmp < 0) {
         BigInteger[] divMod = reason.divideAndRemainder(conflict);
         return new BigInteger[]{divMod[0], BigInteger.ONE, divMod[1]};
      } else {
         BigInteger[] divMod = conflict.divideAndRemainder(reason);
         return divMod[1].signum() == 0
            ? new BigInteger[]{BigInteger.ONE, divMod[0], BigInteger.ZERO}
            : new BigInteger[]{BigInteger.ONE, divMod[0].add(BigInteger.ONE), reason.subtract(divMod[1])};
      }
   }
}
