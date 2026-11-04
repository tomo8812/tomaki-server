package reloc.org.sat4j;

import java.io.PrintWriter;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.TimeoutException;

@Deprecated
public abstract class AbstractOptimizationLauncher extends AbstractLauncher {
   private static final long serialVersionUID = 1L;
   private static final String CURRENT_OPTIMUM_VALUE_PREFIX = "o ";
   private boolean incomplete = false;
   private boolean displaySolutionLine = true;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AbstractOptimizationLauncher.class.desiredAssertionStatus();

   @Override
   protected void setIncomplete(boolean value) {
      this.incomplete = value;
   }

   @Override
   protected void setDisplaySolutionLine(boolean value) {
      this.displaySolutionLine = value;
   }

   @Override
   protected void displayResult() {
      this.displayAnswer();
      this.log("Total wall clock time (in seconds): " + (System.currentTimeMillis() - this.getBeginTime()) / 1000.0);
   }

   protected void displayAnswer() {
      if (this.solver != null) {
         System.out.flush();
         PrintWriter out = this.getLogWriter();
         out.flush();
         this.solver.printStat(out, "c ");
         this.solver.printInfos(out, "c ");
         ExitCode exitCode = this.getExitCode();
         out.println("s " + exitCode);
         if (exitCode == ExitCode.SATISFIABLE || exitCode == ExitCode.OPTIMUM_FOUND || this.incomplete && exitCode == ExitCode.UPPER_BOUND) {
            if (this.displaySolutionLine) {
               out.print("v ");
               this.getReader().decode(this.solver.model(), out);
               out.println();
            }

            IOptimizationProblem optproblem = (IOptimizationProblem)this.solver;
            if (!optproblem.hasNoObjectiveFunction()) {
               this.log("objective function=" + optproblem.getObjectiveValue());
            }
         }
      }
   }

   @Override
   protected void solve(IProblem problem) throws TimeoutException {
      boolean isSatisfiable = false;
      IOptimizationProblem optproblem = (IOptimizationProblem)problem;

      try {
         while (optproblem.admitABetterSolution()) {
            if (!isSatisfiable) {
               if (optproblem.nonOptimalMeansSatisfiable()) {
                  this.setExitCode(ExitCode.SATISFIABLE);
                  if (optproblem.hasNoObjectiveFunction()) {
                     return;
                  }

                  this.log("SATISFIABLE");
               } else if (this.incomplete) {
                  this.setExitCode(ExitCode.UPPER_BOUND);
               }

               isSatisfiable = true;
               this.log("OPTIMIZING...");
            }

            this.log("Got one! Elapsed wall clock time (in seconds):" + (System.currentTimeMillis() - this.getBeginTime()) / 1000.0);
            this.getLogWriter().println("o " + optproblem.getObjectiveValue());
            optproblem.discardCurrentSolution();
         }

         if (isSatisfiable) {
            this.setExitCode(ExitCode.OPTIMUM_FOUND);
         } else {
            this.setExitCode(ExitCode.UNSATISFIABLE);
         }
      } catch (ContradictionException ex) {
         assert isSatisfiable;
         this.setExitCode(ExitCode.OPTIMUM_FOUND);
      }
   }
}
