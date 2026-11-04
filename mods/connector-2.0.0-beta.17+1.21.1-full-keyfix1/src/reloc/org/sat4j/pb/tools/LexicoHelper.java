package reloc.org.sat4j.pb.tools;

import reloc.org.sat4j.pb.IPBSolver;

public class LexicoHelper<T, C> extends AbstractLexicoHelper<T, C> {
   public LexicoHelper(IPBSolver solver) {
      super(new LexicoDecoratorPB(solver));
   }

   public LexicoHelper(IPBSolver solver, boolean explanationEnabled, boolean canonicalOptFunctionEnabled) {
      super(new LexicoDecoratorPB(solver), explanationEnabled, canonicalOptFunctionEnabled);
   }

   public LexicoHelper(IPBSolver solver, boolean explanationEnabled) {
      super(new LexicoDecoratorPB(solver), explanationEnabled);
   }
}
