package reloc.org.sat4j.pb;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.pb.reader.OPBReader2012;
import reloc.org.sat4j.pb.tools.PreprocCardConstrLearningSolver;
import reloc.org.sat4j.reader.DimacsReader;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.TimeoutException;

public class CardConstrLearningSolverLauncher {
   private final PreprocCardConstrLearningSolver<IPBSolver> solver;
   private final long solverStart;
   private final boolean verbose = false;

   @Deprecated
   public static void main(String[] args) throws Exception {
      new CardConstrLearningSolverLauncher(args[0]);
   }

   public CardConstrLearningSolverLauncher(String instance) {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
         }
      });
      this.solver = SolverFactory.newDetectCards();
      Reader reader;
      if (!instance.endsWith(".opb") && !instance.endsWith(".opb.bz2")) {
         reader = new DimacsReader(this.solver);
      } else {
         reader = new OPBReader2012(new PBSolverHandle(this.solver));
      }

      try {
         reader.parseInstance(instance);
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }

      this.solver.setVerbose(false);
      if (System.getProperties().getProperty("nopreprocessing") != null) {
         this.solver.setPreprocessing(false);
      } else if (System.getProperties().getProperty("riss") != null) {
         this.solver.setPreprocessing(false);
         this.solver.setRissLocation(System.getProperties().getProperty("riss"));
         this.solver.setInstance(instance);
      } else {
         this.solver.setPreprocessing(true);
      }

      if (System.getProperties().getProperty("printcards") != null) {
         this.solver.setPrintCards(true);
      } else {
         this.solver.setPrintCards(false);
      }

      this.solverStart = System.currentTimeMillis();
      if (System.getProperties().getProperty("printcards") != null) {
         this.solver.setPrintCards(true);
         this.preproc(reader);
      } else {
         this.solver.setPrintCards(false);
         this.solve(reader);
      }
   }

   private void solve(Reader reader) {
      try {
         if (this.solver.isSatisfiable()) {
            System.out.println("s SATISFIABLE");
            System.out.print("v ");
            PrintWriter out = new PrintWriter(System.out, true);
            int[] model = this.solver.model();
            reader.decode(model, out);
            out.flush();
            System.out.println();
         } else {
            System.out.println("s UNSATISFIABLE");
         }
      } catch (TimeoutException e) {
         Logger.getLogger("reloc.org.sat4j.pb").log(Level.INFO, "Timeout", e);
      }
   }

   private void preproc(Reader reader) {
      this.solver.init();
   }

   // $VF: synthetic method
   static long access$000(CardConstrLearningSolverLauncher x0) {
      return x0.solverStart;
   }

   // $VF: synthetic method
   static PreprocCardConstrLearningSolver access$100(CardConstrLearningSolverLauncher x0) {
      return x0.solver;
   }
}
