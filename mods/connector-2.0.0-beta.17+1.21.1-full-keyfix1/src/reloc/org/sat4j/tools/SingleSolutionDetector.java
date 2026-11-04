package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public class SingleSolutionDetector extends SolverDecorator<ISolver> {
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !SingleSolutionDetector.class.desiredAssertionStatus();

   public SingleSolutionDetector(ISolver solver) {
      super(solver);
   }

   public boolean hasASingleSolution() throws TimeoutException {
      return this.hasASingleSolution(new VecInt());
   }

   public boolean hasASingleSolution(IVecInt assumptions) throws TimeoutException {
      int[] firstmodel = this.model();
      assert firstmodel != null;
      IVecInt clause = new VecInt(firstmodel.length);

      for (int q : firstmodel) {
         clause.push(-q);
      }

      boolean result = false;

      try {
         IConstr added = this.addClause(clause);
         result = !this.isSatisfiable(assumptions);
         this.removeConstr(added);
      } catch (ContradictionException e) {
         result = true;
      }

      return result;
   }
}
