package reloc.org.sat4j;

import java.io.PrintWriter;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.AssignmentOrigin;
import reloc.org.sat4j.specs.ILogAble;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.Backbone;

@Feature("solutionlistener")
final class DecisionMode implements ILauncherMode {
   private ExitCode exitCode = ExitCode.UNKNOWN;
   private int nbSolutionFound;
   private PrintWriter out;
   private long beginTime;
   private Reader reader;
   private ISolver solver;

   @Override
   public void displayResult(ISolver solver, IProblem problem, ILogAble logger, PrintWriter out, Reader reader, long beginTime, boolean displaySolutionLine) {
      if (solver != null) {
         out.flush();
         double wallclocktime = (System.currentTimeMillis() - beginTime) / 1000.0;
         solver.printStat(out);
         out.println("s " + this.exitCode);
         if (this.exitCode != ExitCode.UNKNOWN && this.exitCode != ExitCode.UNSATISFIABLE) {
            int[] model = problem.model();
            if (System.getProperty("prime") != null) {
               int initiallength = model.length;
               logger.log("returning a prime implicant ...");
               long beginpi = System.currentTimeMillis();
               model = solver.primeImplicant();
               long endpi = System.currentTimeMillis();
               logger.log("removed " + (initiallength - model.length) + " literals");
               logger.log("pi computation time: " + (endpi - beginpi) + " ms");
            }

            if (System.getProperty("backbone") != null) {
               logger.log("computing the backbone of the formula ...");
               long beginpi = System.currentTimeMillis();
               model = solver.primeImplicant();

               try {
                  IVecInt backbone = Backbone.instance().compute(solver, model);
                  long endpi = System.currentTimeMillis();
                  out.print(solver.getLogPrefix());
                  reader.decode(backbone.toArray(), out);
                  out.println();
                  logger.log("backbone computation time: " + (endpi - beginpi) + " ms");
               } catch (TimeoutException e) {
                  logger.log("timeout, cannot compute the backbone.");
               }
            }

            if (this.nbSolutionFound >= 1) {
               logger.log("Found " + this.nbSolutionFound + " solution(s)");
            } else {
               this.printSolution(solver, out, reader, model);
            }
         }

         logger.log("Total wall clock time (in seconds) : " + wallclocktime);
      }
   }

   private void printSolution(ISolver solver, PrintWriter out, Reader reader, int[] model) {
      out.print("v ");
      if (System.getProperty("color") == null) {
         reader.decode(model, out);
         out.println();
      } else {
         int[] stats = new int[AssignmentOrigin.values().length];

         for (int p : model) {
            AssignmentOrigin origin = solver.getOriginInModel(p);
            out.print(origin.getColor());
            out.print(p);
            out.print("\u001b[0m");
            out.print(" ");
            stats[origin.ordinal()]++;
         }

         out.println("0");
         out.print(solver.getLogPrefix());

         for (AssignmentOrigin ao : AssignmentOrigin.values()) {
            out.printf("%s%s%s: %d ", ao.getColor(), ao, "\u001b[0m", stats[ao.ordinal()]);
         }

         out.println();
      }
   }

   @Override
   public void solve(IProblem problem, Reader reader, ILogAble logger, PrintWriter out, long beginTime) {
      this.exitCode = ExitCode.UNKNOWN;
      this.out = out;
      this.nbSolutionFound = 0;
      this.beginTime = beginTime;
      this.reader = reader;
      this.solver = (ISolver)problem;

      try {
         if (problem.isSatisfiable()) {
            if (this.exitCode == ExitCode.UNKNOWN) {
               this.exitCode = ExitCode.SATISFIABLE;
            }
         } else if (this.exitCode == ExitCode.UNKNOWN) {
            this.exitCode = ExitCode.UNSATISFIABLE;
         }
      } catch (TimeoutException e) {
         logger.log("timeout");
      }
   }

   @Override
   public void setIncomplete(boolean isIncomplete) {
   }

   @Override
   public ExitCode getCurrentExitCode() {
      return this.exitCode;
   }

   @Override
   public void onSolutionFound(int[] solution) {
      this.nbSolutionFound++;
      this.exitCode = ExitCode.SATISFIABLE;
      this.out.printf("c Found solution #%d  (%.2f)s%n", this.nbSolutionFound, (System.currentTimeMillis() - this.beginTime) / 1000.0);
      if (System.getProperty("printallmodels") != null) {
         this.printSolution(this.solver, this.out, this.reader, solution);
      }
   }

   @Override
   public void onSolutionFound(IVecInt solution) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void onUnsatTermination() {
      if (this.exitCode == ExitCode.SATISFIABLE) {
         this.exitCode = ExitCode.OPTIMUM_FOUND;
      }
   }

   @Override
   public void setExitCode(ExitCode exitCode) {
      this.exitCode = exitCode;
   }
}
