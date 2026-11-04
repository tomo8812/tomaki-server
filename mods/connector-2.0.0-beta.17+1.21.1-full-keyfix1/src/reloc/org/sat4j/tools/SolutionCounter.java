package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public class SolutionCounter extends SolverDecorator<ISolver> {
   private static final long serialVersionUID = 1L;
   private int lowerBound;

   public SolutionCounter(ISolver solver) {
      super(solver);
   }

   public int lowerBound() {
      return this.lowerBound;
   }

   public long countSolutions() throws TimeoutException {
      this.lowerBound = 0;
      boolean trivialfalsity = false;

      while (!trivialfalsity && this.isSatisfiable(true)) {
         this.lowerBound++;
         int[] last = this.model();
         IVecInt clause = new VecInt(last.length);

         for (int q : last) {
            clause.push(-q);
         }

         try {
            this.addClause(clause);
         } catch (ContradictionException e) {
            trivialfalsity = true;
         }
      }

      return this.lowerBound;
   }
}
