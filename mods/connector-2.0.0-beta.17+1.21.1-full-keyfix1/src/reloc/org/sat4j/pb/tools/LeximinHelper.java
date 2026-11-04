package reloc.org.sat4j.pb.tools;

import reloc.org.sat4j.pb.IIntegerPBSolver;
import reloc.org.sat4j.pb.multiobjective.LeximinDecorator;

public class LeximinHelper<T, C> extends AbstractLexicoHelper<T, C> {
   public LeximinHelper(IIntegerPBSolver solver) {
      super(new LeximinDecorator(solver));
   }

   public LeximinHelper(IIntegerPBSolver solver, boolean explanationEnabled, boolean canonicalOptFunctionEnabled) {
      super(new LeximinDecorator(solver), explanationEnabled, canonicalOptFunctionEnabled);
   }

   public LeximinHelper(IIntegerPBSolver solver, boolean explanationEnabled) {
      super(new LeximinDecorator(solver), explanationEnabled);
   }
}
