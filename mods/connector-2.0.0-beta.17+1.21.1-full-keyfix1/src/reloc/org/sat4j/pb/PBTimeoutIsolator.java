package reloc.org.sat4j.pb;

public class PBTimeoutIsolator extends PBSolverDecorator {
   private static final long serialVersionUID = 1L;

   public PBTimeoutIsolator(IPBSolver solver) {
      super(solver);
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
   }

   @Override
   public void setTimeout(int t) {
   }

   @Override
   public void setTimeoutMs(long t) {
   }
}
