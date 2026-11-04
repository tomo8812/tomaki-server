package reloc.org.sat4j;

import reloc.org.sat4j.minisat.SolverFactory;
import reloc.org.sat4j.reader.GroupedCNFReader;
import reloc.org.sat4j.reader.LecteurDimacs;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.AllMUSes;
import reloc.org.sat4j.tools.GroupClauseSelectorSolver;
import reloc.org.sat4j.tools.SolutionFoundListener;
import reloc.org.sat4j.tools.xplain.Explainer;
import reloc.org.sat4j.tools.xplain.HighLevelXplain;
import reloc.org.sat4j.tools.xplain.MinimizationStrategy;
import reloc.org.sat4j.tools.xplain.Xplain;

public class MUSLauncher extends AbstractLauncher {
   private static final long serialVersionUID = 1L;
   private int[] mus;
   private Explainer xplain;
   private boolean highLevel = false;
   private AllMUSes allMuses;
   private int muscount = 0;

   @Override
   public void usage() {
      this.log("java -jar sat4j-mus.jar [Insertion|Deletion|QuickXplain|all] <cnffile>|<gcnffile>");
   }

   @Override
   protected Reader createReader(ISolver theSolver, String problemname) {
      return this.highLevel ? new GroupedCNFReader((GroupClauseSelectorSolver)theSolver) : new LecteurDimacs(theSolver);
   }

   @Override
   protected String getInstanceName(String[] args) {
      return args.length == 0 ? null : args[args.length - 1];
   }

   @Override
   protected ISolver configureSolver(String[] args) {
      String problemName = args[args.length - 1];
      if (problemName.endsWith(".gcnf")) {
         this.highLevel = true;
      }

      ISolver solver;
      if (this.highLevel) {
         HighLevelXplain<ISolver> hlxp = new HighLevelXplain<>(SolverFactory.newDefault());
         this.xplain = hlxp;
         solver = hlxp;
      } else {
         Xplain<ISolver> xp = new Xplain<>(SolverFactory.newDefault(), false);
         this.xplain = xp;
         solver = xp;
      }

      solver.setDBSimplificationAllowed(true);
      if (args.length == 2) {
         if ("all".equals(args[0])) {
            this.allMuses = new AllMUSes(this.highLevel, SolverFactory.instance());
            solver = this.allMuses.getSolverInstance();
         } else {
            String className = "reloc.org.sat4j.tools.xplain." + args[0] + "Strategy";

            try {
               this.xplain.setMinimizationStrategy((MinimizationStrategy)Class.forName(className).newInstance());
            } catch (Exception e) {
               this.log(e.getMessage());
            }
         }
      }

      solver.setTimeout(Integer.MAX_VALUE);
      this.getLogWriter().println(solver.toString("c "));
      return solver;
   }

   @Override
   protected void displayResult() {
      if (this.solver != null) {
         double wallclocktime = (System.currentTimeMillis() - this.beginTime) / 1000.0;
         this.solver.printStat(this.out);
         this.solver.printInfos(this.out);
         this.out.println("s " + this.getExitCode());
         if (this.getExitCode() == ExitCode.SATISFIABLE) {
            int[] model = this.solver.model();
            this.out.print("v ");
            this.reader.decode(model, this.out);
            this.out.println();
         } else if (this.getExitCode() == ExitCode.UNSATISFIABLE && this.mus != null) {
            this.out.print("v ");
            this.reader.decode(this.mus, this.out);
            this.out.println();
         }

         this.log("Total wall clock time (in seconds) : " + wallclocktime);
      }
   }

   @Override
   public void run(String[] args) {
      this.mus = null;
      super.run(args);
      double wallclocktime = (System.currentTimeMillis() - this.beginTime) / 1000.0;
      if (this.getExitCode() == ExitCode.UNSATISFIABLE) {
         try {
            this.log("Unsat detection wall clock time (in seconds) : " + wallclocktime);
            double beginmus = System.currentTimeMillis();
            if (this.allMuses != null) {
               SolutionFoundListener mssListener = new SolutionFoundListener() {
                  private int msscount = 0;

                  @Override
                  public void onUnsatTermination() {
                     throw new UnsupportedOperationException("Not implemented yet!");
                  }

                  @Override
                  public void onSolutionFound(IVecInt solution) {
                     System.out.print("\r" + MUSLauncher.this.solver.getLogPrefix() + "found mss number " + ++this.msscount);
                  }

                  @Override
                  public void onSolutionFound(int[] solution) {
                     throw new UnsupportedOperationException("Not implemented yet!");
                  }
               };
               SolutionFoundListener musListener = new SolutionFoundListener() {
                  @Override
                  public void onSolutionFound(int[] solution) {
                  }

                  @Override
                  public void onSolutionFound(IVecInt solution) {
                     System.out.println(MUSLauncher.this.solver.getLogPrefix() + "found mus number " + ++MUSLauncher.this.muscount);
                     MUSLauncher.this.out.print("v ");
                     int[] localMus = new int[solution.size()];
                     solution.copyTo(localMus);
                     MUSLauncher.this.reader.decode(localMus, MUSLauncher.this.out);
                     MUSLauncher.this.out.println();
                  }

                  @Override
                  public void onUnsatTermination() {
                  }
               };
               this.allMuses.computeAllMSS(mssListener);
               if ("card".equals(System.getProperty("min"))) {
                  this.allMuses.computeAllMUSesOrdered(musListener);
               } else {
                  this.allMuses.computeAllMUSes(musListener);
               }

               this.log("All MUSes computation wall clock time (in seconds) : " + (System.currentTimeMillis() - beginmus) / 1000.0);
            } else {
               this.log("Size of initial " + (this.highLevel ? "high level " : "") + "unsat subformula: " + this.solver.unsatExplanation().size());
               this.log("Computing " + (this.highLevel ? "high level " : "") + "MUS ...");
               this.mus = this.xplain.minimalExplanation();
               this.log("Size of the " + (this.highLevel ? "high level " : "") + "MUS: " + this.mus.length);
               this.log("Unsat core  computation wall clock time (in seconds) : " + (System.currentTimeMillis() - beginmus) / 1000.0);
            }
         } catch (TimeoutException e) {
            this.log("Cannot compute " + (this.highLevel ? "high level " : "") + "MUS within the timeout.");
         }
      }
   }

   public static void main(String[] args) {
      MUSLauncher lanceur = new MUSLauncher();
      if (args.length >= 1 && args.length <= 2) {
         lanceur.addHook();
         lanceur.run(args);
         System.exit(lanceur.getExitCode().value());
      } else {
         lanceur.usage();
      }
   }
}
