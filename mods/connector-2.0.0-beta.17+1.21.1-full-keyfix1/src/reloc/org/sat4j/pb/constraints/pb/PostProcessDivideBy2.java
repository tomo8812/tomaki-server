package reloc.org.sat4j.pb.constraints.pb;

public class PostProcessDivideBy2 implements IPostProcess {
   private static final PostProcessDivideBy2 INSTANCE = new PostProcessDivideBy2();

   private PostProcessDivideBy2() {
   }

   public static final PostProcessDivideBy2 instance() {
      return INSTANCE;
   }

   @Override
   public void postProcess(int dl, ConflictMap conflictMap) {
      int nbBits = conflictMap.reduceCoeffsByPower2();
      if (nbBits > 0) {
         conflictMap.stats.incNumberOfReductionsByPower2();
         conflictMap.stats.incNumberOfRightShiftsForCoeffs(nbBits);
      }
   }

   @Override
   public String toString() {
      return "Performs a post-processing after conflict analysis in order to divide by 2 as much as possible coefficients of learned constraints";
   }
}
