package reloc.org.sat4j.pb.constraints.pb;

public class NoPostProcess implements IPostProcess {
   private static final NoPostProcess INSTANCE = new NoPostProcess();

   private NoPostProcess() {
   }

   public static final NoPostProcess instance() {
      return INSTANCE;
   }

   @Override
   public void postProcess(int dl, ConflictMap conflictMap) {
   }

   @Override
   public String toString() {
      return "No postprocessing";
   }
}
