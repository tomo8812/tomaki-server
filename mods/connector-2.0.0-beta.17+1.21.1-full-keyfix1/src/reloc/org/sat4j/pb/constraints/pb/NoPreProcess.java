package reloc.org.sat4j.pb.constraints.pb;

public class NoPreProcess implements IPreProcess {
   private static final IPreProcess INSTANCE = new NoPreProcess();

   private NoPreProcess() {
   }

   public static final IPreProcess instance() {
      return INSTANCE;
   }

   @Override
   public void preProcess(int dl, ConflictMap conflictMap) {
   }

   @Override
   public String toString() {
      return "Conflict is considered as is when resolving";
   }
}
