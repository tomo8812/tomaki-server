package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public class Minimal4InclusionModel extends AbstractMinimalModel {
   private static final long serialVersionUID = 1L;
   private int[] prevfullmodel;
   private IVecInt assumps = VecInt.EMPTY;

   public Minimal4InclusionModel(ISolver solver, IVecInt p, SolutionFoundListener modelListener) {
      super(solver, p, modelListener);
   }

   public Minimal4InclusionModel(ISolver solver, IVecInt p) {
      this(solver, p, SolutionFoundListener.VOID);
   }

   public Minimal4InclusionModel(ISolver solver) {
      this(solver, negativeLiterals(solver), SolutionFoundListener.VOID);
   }

   @Override
   public int[] model() {
      int[] prevmodel = null;
      IVecInt vec = new VecInt();
      IVecInt cube = new VecInt();
      IConstr prevConstr = null;

      try {
         do {
            this.prevfullmodel = super.modelWithInternalVariables();
            prevmodel = super.model();
            this.modelListener.onSolutionFound(prevmodel);
            vec.clear();
            cube.clear();
            this.assumps.copyTo(cube);

            for (int q : this.prevfullmodel) {
               if (this.pLiterals.contains(q)) {
                  vec.push(-q);
               } else if (this.pLiterals.contains(-q)) {
                  cube.push(q);
               }
            }

            if (prevConstr != null) {
               this.removeSubsumedConstr(prevConstr);
            }

            try {
               prevConstr = this.addBlockingClause(vec);
            } catch (ContradictionException e) {
               break;
            }
         } while (this.isSatisfiable(cube));

         return prevmodel;
      } catch (TimeoutException e) {
         throw new IllegalStateException("Solver timed out");
      }
   }

   @Override
   public int[] modelWithInternalVariables() {
      this.model();
      return this.prevfullmodel;
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      this.assumps = assumps;
      return super.isSatisfiable(assumps);
   }
}
