package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class PostProcessToClause implements IPostProcess {
   private static final PostProcessToClause INSTANCE = new PostProcessToClause();
   private int assertiveLevel;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !PostProcessToClause.class.desiredAssertionStatus();

   private PostProcessToClause() {
   }

   public static final PostProcessToClause instance() {
      return INSTANCE;
   }

   @Override
   public void postProcess(int dl, ConflictMap conflictMap) {
      if (conflictMap.isAssertive(dl) && !conflictMap.degree.equals(BigInteger.ONE)) {
         conflictMap.setDecisionLevel(dl);
         if (conflictMap.assertiveLiteral != -1) {
            conflictMap.assertiveLiteral = this.chooseAssertiveLiteral(dl, conflictMap);
            int lit = conflictMap.weightedLits.getLit(conflictMap.assertiveLiteral);
            IVecInt toSuppress = new VecInt();

            for (int i = 0; i < conflictMap.size(); i++) {
               int ilit = conflictMap.weightedLits.getLit(i);
               int litLevel = conflictMap.voc.getLevel(ilit);
               if (litLevel < this.assertiveLevel && conflictMap.voc.isFalsified(ilit)) {
                  conflictMap.weightedLits.changeCoef(i, BigInteger.ONE);
               } else if (ilit != lit) {
                  toSuppress.push(ilit);
               }
            }

            conflictMap.weightedLits.changeCoef(conflictMap.assertiveLiteral, BigInteger.ONE);

            for (int i = 0; i < toSuppress.size(); i++) {
               conflictMap.removeCoef(toSuppress.get(i));
            }

            conflictMap.degree = BigInteger.ONE;
            conflictMap.assertiveLiteral = conflictMap.weightedLits.getFromAllLits(lit);
            assert conflictMap.backtrackLevel == conflictMap.oldGetBacktrackLevel(dl);
         }
      }
   }

   public int chooseAssertiveLiteral(int maxLevel, ConflictMap conflictMap) {
      int indStop = ConflictMap.levelToIndex(maxLevel);
      int indStart = ConflictMap.levelToIndex(0);
      BigInteger slack = conflictMap.computeSlack(0).subtract(conflictMap.degree);
      int previous = 0;

      for (int indLevel = indStart; indLevel <= indStop; indLevel++) {
         if (conflictMap.byLevel[indLevel] != null) {
            int level = ConflictMap.indexToLevel(indLevel);
            assert conflictMap.computeSlack(level).subtract(conflictMap.degree).equals(slack);
            if (conflictMap.isImplyingLiteralOrdered(level, slack)) {
               conflictMap.backtrackLevel = previous;
               this.assertiveLevel = level;
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

      assert conflictMap.backtrackLevel == conflictMap.oldGetBacktrackLevel(maxLevel);
      return conflictMap.assertiveLiteral;
   }

   @Override
   public String toString() {
      return "Performs a post-processing after conflict analysis in order to learn clauses";
   }
}
