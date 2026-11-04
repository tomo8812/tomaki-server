package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

public class PreProcessReduceConflict implements IPreProcess {
   private static final IPreProcess INSTANCE_WITH_FALSIFIED = new PreProcessReduceConflict(true);
   private static final IPreProcess INSTANCE_WITHOUT_FALSIFIED = new PreProcessReduceConflict(false);
   private final boolean considerFalsified;

   private PreProcessReduceConflict(boolean considerFalsified) {
      this.considerFalsified = considerFalsified;
   }

   public static final IPreProcess instanceWithFalsified() {
      return INSTANCE_WITH_FALSIFIED;
   }

   public static final IPreProcess instanceWithoutFalsified() {
      return INSTANCE_WITHOUT_FALSIFIED;
   }

   @Override
   public void preProcess(int dl, ConflictMap conflictMap) {
      BigInteger slack = conflictMap.slackConflict();
      BigInteger degree = conflictMap.degree;

      for (int i = 0; i < conflictMap.size(); i++) {
         int lit = conflictMap.weightedLits.getLit(i);
         BigInteger coef = conflictMap.weightedLits.getCoef(i);
         if (coef.compareTo(degree) < 0) {
            if (!conflictMap.voc.isFalsified(lit)) {
               conflictMap.removeCoef(lit);
               degree = degree.subtract(coef);
            } else if (this.considerFalsified && coef.negate().compareTo(slack) > 0) {
               conflictMap.removeCoef(lit);
               degree = degree.subtract(coef);
               slack = slack.add(coef);
               conflictMap.stats.incFalsifiedLiteralsRemovedFromConflict();
            }
         }
      }

      conflictMap.degree = degree;
      conflictMap.saturation();
   }

   @Override
   public String toString() {
      return "Literals which do not impact the conflict are removed before resolving";
   }
}
