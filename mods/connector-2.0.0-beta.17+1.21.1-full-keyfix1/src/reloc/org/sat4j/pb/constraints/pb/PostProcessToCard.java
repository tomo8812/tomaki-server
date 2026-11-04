package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class PostProcessToCard implements IPostProcess {
   private static final PostProcessToCard INSTANCE = new PostProcessToCard();
   private int assertiveLevel;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !PostProcessToCard.class.desiredAssertionStatus();

   private PostProcessToCard() {
   }

   public static final PostProcessToCard instance() {
      return INSTANCE;
   }

   @Override
   public void postProcess(int dl, ConflictMap conflictMap) {
      if (conflictMap.isAssertive(dl) && !conflictMap.degree.equals(BigInteger.ONE)) {
         conflictMap.setDecisionLevel(dl);
         if (conflictMap.assertiveLiteral != -1) {
            conflictMap.assertiveLiteral = this.chooseAssertiveLiteral(dl, conflictMap);
            BigInteger coefLit = conflictMap.weightedLits.getCoef(conflictMap.assertiveLiteral);
            BigInteger sumCoefsTmp = BigInteger.ZERO;

            for (int i = 0; i < conflictMap.size(); i++) {
               sumCoefsTmp = sumCoefsTmp.add(conflictMap.weightedLits.getCoef(i));
            }

            if (sumCoefsTmp.compareTo(BigInteger.valueOf(conflictMap.size())) == 0) {
               return;
            }

            IVecInt compLSet = new VecInt();
            BigInteger coefMax = coefLit;
            sumCoefsTmp = sumCoefsTmp.subtract(coefLit);
            conflictMap.changeCoef(conflictMap.assertiveLiteral, BigInteger.ONE);

            for (int i = 0; i < conflictMap.size(); i++) {
               int ilit = conflictMap.weightedLits.getLit(i);
               int lit = conflictMap.weightedLits.getLit(conflictMap.assertiveLiteral);
               int litLevel = conflictMap.voc.getLevel(ilit);
               BigInteger coefTmp = conflictMap.weightedLits.getCoef(i);
               if (ilit != lit) {
                  if (litLevel < this.assertiveLevel && conflictMap.voc.isFalsified(ilit)) {
                     conflictMap.changeCoef(i, BigInteger.ONE);
                     sumCoefsTmp = sumCoefsTmp.subtract(coefTmp);
                     if (coefMax.compareTo(coefTmp) < 0) {
                        coefMax = coefTmp;
                     }
                  } else {
                     compLSet.push(ilit);
                  }
               }
            }

            assert sumCoefsTmp.compareTo(conflictMap.degree) < 0;
            int lit = conflictMap.weightedLits.getLit(conflictMap.assertiveLiteral);
            int degreeCard = 1;

            for (int i = 0; i < compLSet.size(); i++) {
               int ilit = conflictMap.weightedLits.getFromAllLits(compLSet.get(i));
               if (coefMax.compareTo(conflictMap.weightedLits.getCoef(ilit)) <= 0) {
                  conflictMap.changeCoef(ilit, BigInteger.ONE);
                  degreeCard++;
               } else {
                  conflictMap.removeCoef(compLSet.get(i));
               }
            }

            conflictMap.degree = BigInteger.valueOf(degreeCard);
            conflictMap.assertiveLiteral = conflictMap.weightedLits.getFromAllLits(lit);
            assert conflictMap.backtrackLevel == conflictMap.oldGetBacktrackLevel(dl);
         }
      }
   }

   private int chooseAssertiveLiteral(int maxLevel, ConflictMap conflictMap) {
      int indStop = ConflictMap.levelToIndex(maxLevel);
      int indStart = ConflictMap.levelToIndex(0);
      BigInteger slack = conflictMap.computeSlack(0).subtract(conflictMap.degree);
      int previous = 0;
      IVecInt literals = new VecInt();

      for (int indLevel = indStart; indLevel <= indStop; indLevel++) {
         if (conflictMap.byLevel[indLevel] != null) {
            int level = ConflictMap.indexToLevel(indLevel);
            assert conflictMap.computeSlack(level).subtract(conflictMap.degree).equals(slack);
            if (conflictMap.isImplyingLiteralOrdered(level, slack, literals)) {
               this.assertiveLevel = level;
               conflictMap.backtrackLevel = previous;
               break;
            }

            VecInt lits = conflictMap.byLevel[indLevel];
            IteratorInt iterator = lits.iterator();

            while (iterator.hasNext()) {
               int lit = iterator.next();
               if (conflictMap.voc.isFalsified(lit) && conflictMap.voc.getLevel(lit) == ConflictMap.indexToLevel(indLevel)) {
                  slack = slack.subtract(conflictMap.weightedLits.get(lit));
               }
            }

            if (!lits.isEmpty()) {
               previous = level;
            }
         }
      }

      assert literals.size() > 0;
      int maxLit = literals.get(0);
      BigInteger maxCoef = conflictMap.weightedLits.getCoef(maxLit);

      for (int i = 1; i < literals.size(); i++) {
         BigInteger coef = conflictMap.weightedLits.getCoef(literals.get(i));
         if (coef.compareTo(maxCoef) > 0) {
            maxLit = literals.get(i);
            maxCoef = coef;
         }
      }

      assert conflictMap.backtrackLevel == conflictMap.oldGetBacktrackLevel(maxLevel);
      assert literals.size() > 0;
      return maxLit;
   }

   @Override
   public String toString() {
      return "Performs a post-processing after conflict analysis in order to learn only cardinality constraints (Dixon's procedure)";
   }
}
