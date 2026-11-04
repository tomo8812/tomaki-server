package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public class Minimal4CardinalityModel extends AbstractMinimalModel {
   private static final long serialVersionUID = 1L;
   private int[] prevfullmodel;

   public Minimal4CardinalityModel(ISolver solver) {
      super(solver);
   }

   public Minimal4CardinalityModel(ISolver solver, IVecInt p, SolutionFoundListener modelListener) {
      super(solver, p, modelListener);
   }

   public Minimal4CardinalityModel(ISolver solver, IVecInt p) {
      super(solver, p);
   }

   public Minimal4CardinalityModel(ISolver solver, SolutionFoundListener modelListener) {
      super(solver, modelListener);
   }

   @Override
   public int[] model() {
      int[] prevmodel = null;
      IConstr lastOne = null;
      IVecInt literals = new VecInt(this.pLiterals.size());

      for (int p : this.pLiterals) {
         literals.push(p);
      }

      try {
         do {
            this.prevfullmodel = super.modelWithInternalVariables();
            prevmodel = super.model();
            int counter = 0;

            for (int q : this.prevfullmodel) {
               if (this.pLiterals.contains(q)) {
                  counter++;
               }
            }

            lastOne = this.addAtMost(literals, counter - 1);
         } while (this.isSatisfiable());
      } catch (TimeoutException e) {
         throw new IllegalStateException("Solver timed out");
      } catch (ContradictionException var10) {
      }

      if (lastOne != null) {
         this.removeConstr(lastOne);
      }

      return prevmodel;
   }

   @Override
   public int[] modelWithInternalVariables() {
      this.model();
      return this.prevfullmodel;
   }
}
