package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public class OptToSatAdapter extends SolverDecorator<ISolver> {
   private static final long serialVersionUID = 1L;
   private final IOptimizationProblem problem;
   private final IVecInt assumps = new VecInt();
   private long begin;
   private final SolutionFoundListener sfl;

   public OptToSatAdapter(IOptimizationProblem problem) {
      this(problem, SolutionFoundListener.VOID);
   }

   public OptToSatAdapter(IOptimizationProblem problem, SolutionFoundListener sfl) {
      super((ISolver)problem);
      this.problem = problem;
      this.sfl = sfl;
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      return this.isSatisfiable(VecInt.EMPTY);
   }

   @Override
   public boolean isSatisfiable(boolean global) throws TimeoutException {
      return this.isSatisfiable();
   }

   @Override
   public boolean isSatisfiable(IVecInt myAssumps, boolean global) throws TimeoutException {
      return this.isSatisfiable(myAssumps);
   }

   @Override
   public boolean isSatisfiable(IVecInt myAssumps) throws TimeoutException {
      this.assumps.clear();
      myAssumps.copyTo(this.assumps);
      this.begin = System.currentTimeMillis();
      if (this.problem.hasNoObjectiveFunction()) {
         return this.problem.isSatisfiable(myAssumps);
      }

      if (!this.problem.admitABetterSolution(myAssumps)) {
         return false;
      }

      try {
         while (true) {
            this.sfl.onSolutionFound(this.problem.model());
            this.problem.discardCurrentSolution();
            if (this.isVerbose()) {
               System.out
                  .println(
                     this.getLogPrefix()
                        + "Current objective function value: "
                        + this.problem.getObjectiveValue()
                        + "("
                        + (System.currentTimeMillis() - this.begin) / 1000.0
                        + "s)"
                  );
            }

            if (!this.problem.admitABetterSolution(myAssumps)) {
               this.expireTimeout();
               this.sfl.onUnsatTermination();
               break;
            }
         }
      } catch (TimeoutException e) {
         if (this.isVerbose()) {
            System.out.println(this.getLogPrefix() + "Solver timed out after " + (System.currentTimeMillis() - this.begin) / 1000.0 + "s)");
         }
      } catch (ContradictionException ce) {
         this.expireTimeout();
         this.sfl.onUnsatTermination();
      }

      return true;
   }

   @Override
   public int[] model() {
      return this.problem.model();
   }

   @Override
   public boolean model(int var) {
      return this.problem.model(var);
   }

   @Override
   public int[] modelWithInternalVariables() {
      return this.decorated().modelWithInternalVariables();
   }

   @Override
   public int[] findModel() throws TimeoutException {
      return this.isSatisfiable() ? this.model() : null;
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      return this.isSatisfiable(assumps) ? this.model() : null;
   }

   @Override
   public String toString(String prefix) {
      return prefix + "Optimization to SAT adapter\n" + super.toString(prefix);
   }

   public boolean isOptimal() {
      return this.problem.isOptimal();
   }
}
