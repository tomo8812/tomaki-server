package reloc.org.sat4j;

import java.io.PrintWriter;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ILogAble;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.LexicoDecorator;

@Feature("solutionlistener")
final class OptimizationMode implements ILauncherMode {
   private int nbSolutions;
   private ExitCode exitCode = ExitCode.UNKNOWN;
   private boolean isIncomplete = false;
   private PrintWriter out;
   private long beginTime;
   private IOptimizationProblem problem;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !OptimizationMode.class.desiredAssertionStatus();

   @Override
   public void setIncomplete(boolean isIncomplete) {
      this.isIncomplete = isIncomplete;
   }

   @Override
   public void displayResult(ISolver solver, IProblem problem, ILogAble logger, PrintWriter out, Reader reader, long beginTime, boolean displaySolutionLine) {
      if (solver != null) {
         System.out.flush();
         out.flush();
         solver.printStat(out);
         out.println("s " + this.exitCode);
         if (this.exitCode == ExitCode.SATISFIABLE || this.exitCode == ExitCode.OPTIMUM_FOUND || this.isIncomplete && this.exitCode == ExitCode.UPPER_BOUND) {
            assert this.nbSolutions > 0;
            logger.log("Found " + this.nbSolutions + " solution(s)");
            if (displaySolutionLine) {
               out.print("v ");
               reader.decode(problem.model(), out);
               out.println();
            }

            IOptimizationProblem optproblem = (IOptimizationProblem)problem;
            if (!optproblem.hasNoObjectiveFunction()) {
               String objvalue;
               if (!(optproblem instanceof LexicoDecorator)) {
                  objvalue = optproblem.getObjectiveValue().toString();
               } else {
                  IVec<Number> values = new Vec<>();
                  LexicoDecorator<?> lexico = (LexicoDecorator<?>)optproblem;

                  for (int i = 0; i < lexico.numberOfCriteria(); i++) {
                     values.push(lexico.getObjectiveValue(i));
                  }

                  objvalue = values.toString();
               }

               logger.log("objective function=" + objvalue);
            }
         }

         logger.log("Total wall clock time (in seconds): " + (System.currentTimeMillis() - beginTime) / 1000.0);
         out.flush();
      }
   }

   @Override
   public void solve(IProblem problem, Reader reader, ILogAble logger, PrintWriter out, long beginTime) {
      boolean isSatisfiable = false;
      this.nbSolutions = 0;
      IOptimizationProblem optproblem = (IOptimizationProblem)problem;
      this.exitCode = ExitCode.UNKNOWN;
      this.out = out;
      this.beginTime = beginTime;
      this.problem = optproblem;

      try {
         while (optproblem.admitABetterSolution()) {
            this.nbSolutions++;
            if (!isSatisfiable) {
               if (optproblem.nonOptimalMeansSatisfiable()) {
                  logger.log("SATISFIABLE");
                  this.exitCode = ExitCode.SATISFIABLE;
                  if (optproblem.hasNoObjectiveFunction()) {
                     return;
                  }
               } else if (this.isIncomplete) {
                  this.exitCode = ExitCode.UPPER_BOUND;
               }

               isSatisfiable = true;
               logger.log("OPTIMIZING...");
            }

            logger.log("Got one! Elapsed wall clock time (in seconds):" + (System.currentTimeMillis() - beginTime) / 1000.0);
            out.println("o " + optproblem.getObjectiveValue());
            optproblem.discardCurrentSolution();
         }

         if (isSatisfiable) {
            this.exitCode = ExitCode.OPTIMUM_FOUND;
         } else {
            this.exitCode = ExitCode.UNSATISFIABLE;
         }
      } catch (ContradictionException ex) {
         assert isSatisfiable;
         this.exitCode = ExitCode.OPTIMUM_FOUND;
      } catch (TimeoutException e) {
         logger.log("timeout");
      }
   }

   @Override
   public ExitCode getCurrentExitCode() {
      return this.exitCode;
   }

   @Override
   public void onSolutionFound(int[] solution) {
      this.nbSolutions++;
      this.out.printf("c Found solution #%d  (%.2f)s%n", this.nbSolutions, (System.currentTimeMillis() - this.beginTime) / 1000.0);
      this.out.println("c Value of objective function : " + this.problem.getObjectiveValue());
      if (System.getProperty("printallmodels") != null) {
         this.out.println(new VecInt(solution).toString().replace(",", " ") + " 0");
      }
   }

   @Override
   public void onSolutionFound(IVecInt solution) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void onUnsatTermination() {
   }

   @Override
   public void setExitCode(ExitCode exitCode) {
      this.exitCode = exitCode;
   }
}
