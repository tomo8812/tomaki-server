package reloc.org.sat4j.pb;

import java.io.PrintWriter;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.SolutionFoundListener;

public class OptToPBSATAdapter extends PBSolverDecorator {
   private static final long serialVersionUID = 1L;
   IOptimizationProblem problem;
   private final IVecInt assumps = new VecInt();
   private long begin;
   private SolutionFoundListener sfl;

   public OptToPBSATAdapter(IOptimizationProblem problem) {
      this(problem, SolutionFoundListener.VOID);
   }

   public OptToPBSATAdapter(IOptimizationProblem problem, SolutionFoundListener sfl) {
      super((IPBSolver)problem);
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

      boolean satisfiable = false;

      try {
         while (this.problem.admitABetterSolution(myAssumps)) {
            satisfiable = true;
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
         }

         this.expireTimeout();
         this.sfl.onUnsatTermination();
         if (this.isVerbose()) {
            System.out
               .println(
                  this.getLogPrefix()
                     + "Optimal objective function value: "
                     + this.problem.getObjectiveValue()
                     + "("
                     + (System.currentTimeMillis() - this.begin) / 1000.0
                     + "s)"
               );
         }
      } catch (TimeoutException e) {
         if (this.isVerbose()) {
            System.out.println(this.getLogPrefix() + "Solver timed out after " + (System.currentTimeMillis() - this.begin) / 1000.0 + "s)");
         }

         if (!satisfiable) {
            throw e;
         }
      } catch (ContradictionException ce) {
         this.expireTimeout();
         this.sfl.onUnsatTermination();
      }

      return satisfiable;
   }

   @Override
   public int[] findModel() throws TimeoutException {
      return this.isSatisfiable() ? this.problem.model() : null;
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      return this.isSatisfiable(assumps) ? this.problem.model() : null;
   }

   @Override
   public int[] model() {
      return this.model(new PrintWriter(System.out, true));
   }

   public int[] model(PrintWriter out) {
      return this.problem.model();
   }

   @Override
   public boolean model(int var) {
      return this.problem.model(var);
   }

   @Override
   public String toString(String prefix) {
      return prefix + "Optimization to Pseudo Boolean adapter\n" + super.toString(prefix);
   }

   public boolean isOptimal() {
      return this.problem.isOptimal();
   }

   public Number getCurrentObjectiveValue() {
      return this.problem.getObjectiveValue();
   }

   public void setTimeoutForFindingBetterSolution(int seconds) {
      this.problem.setTimeoutForFindingBetterSolution(seconds);
   }

   public void setSolutionFoundListener(SolutionFoundListener sfl) {
      this.sfl = sfl;
   }
}
