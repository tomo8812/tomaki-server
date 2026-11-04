package reloc.org.sat4j.pb;

import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;

public class PseudoIteratorDecorator extends PseudoOptDecorator {
   private static final long serialVersionUID = 1L;

   public PseudoIteratorDecorator(IPBSolver solver) {
      super(solver);
   }

   @Override
   public void discardCurrentSolution() throws ContradictionException {
      int[] last = super.model();
      IVecInt clause = new VecInt(last.length);

      for (int q : last) {
         clause.push(-q);
      }

      this.addClause(clause);
   }
}
