package reloc.org.sat4j.pb;

import java.util.Collection;
import reloc.org.sat4j.AbstractLauncher;
import reloc.org.sat4j.ExitCode;
import reloc.org.sat4j.pb.reader.OPBEclipseReader2007;
import reloc.org.sat4j.pb.tools.XplainPB;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.TimeoutException;

public class LanceurPseudo2007Eclipse extends LanceurPseudo2007 {
   XplainPB quickxplain;
   private static final long serialVersionUID = 1L;

   @Override
   protected ISolver configureSolver(String[] args) {
      IPBSolver theSolver;
      if (args.length > 1) {
         theSolver = SolverFactory.instance().createSolverByName(args[0]);
      } else {
         theSolver = SolverFactory.newDefault();
      }

      this.quickxplain = new XplainPB(theSolver);
      theSolver = new PseudoOptDecorator(this.quickxplain);
      if (args.length == 3) {
         theSolver.setTimeout(Integer.valueOf(args[1]));
      }

      this.out.println(theSolver.toString("c "));
      return theSolver;
   }

   @Override
   protected Reader createReader(ISolver theSolver, String problemname) {
      return new OPBEclipseReader2007((IPBSolver)theSolver);
   }

   public static void main(String[] args) {
      AbstractLauncher lanceur = new LanceurPseudo2007Eclipse();
      if (args.length != 0 && args.length <= 2) {
         lanceur.run(args);
         System.exit(lanceur.getExitCode().value());
      } else {
         lanceur.usage();
      }
   }

   @Override
   protected void displayResult() {
      super.displayResult();
      ExitCode exitCode = this.getExitCode();
      if (exitCode == ExitCode.UNSATISFIABLE) {
         try {
            Collection<IConstr> explanation = this.quickxplain.explain();
            this.log("Explanation for inconsistency: " + explanation);
         } catch (TimeoutException e) {
            this.log("Timeout ! Need more time to complete");
         }
      }
   }
}
