package reloc.org.sat4j.tools;

public interface IBackboneProgressListener {
   IBackboneProgressListener VOID = new IBackboneProgressListener() {
      @Override
      public void start(int litsToTest) {
      }

      @Override
      public void inProgress(int processed, int initLitsToTest) {
      }

      @Override
      public void end(int nCallsToSolver) {
      }
   };

   void start(int var1);

   void inProgress(int var1, int var2);

   void end(int var1);
}
