package reloc.org.sat4j.pb.tools;

import reloc.org.sat4j.pb.IPBSolver;

public class SteppedTimeoutLexicoHelper<T, C> extends AbstractLexicoHelper<T, C> {
   public SteppedTimeoutLexicoHelper(IPBSolver solver) {
      super(new SteppedTimeoutLexicoDecoratorPB(solver));
   }

   public SteppedTimeoutLexicoHelper(IPBSolver solver, boolean explanationEnabled, boolean canonicalOptFunctionEnabled) {
      super(new SteppedTimeoutLexicoDecoratorPB(solver), explanationEnabled, canonicalOptFunctionEnabled);
   }

   public SteppedTimeoutLexicoHelper(IPBSolver solver, boolean explanationEnabled) {
      super(new SteppedTimeoutLexicoDecoratorPB(solver), explanationEnabled);
   }
}
