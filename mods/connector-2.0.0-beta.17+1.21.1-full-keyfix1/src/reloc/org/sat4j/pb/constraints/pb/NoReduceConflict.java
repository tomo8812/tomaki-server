package reloc.org.sat4j.pb.constraints.pb;

public class NoReduceConflict implements IReduceConflictStrategy {
   private static final IReduceConflictStrategy INSTANCE = new NoReduceConflict();

   private NoReduceConflict() {
   }

   public static final IReduceConflictStrategy instance() {
      return INSTANCE;
   }

   @Override
   public void reduceConflict(ConflictMapDivideByPivot conflict, int literal) {
   }
}
