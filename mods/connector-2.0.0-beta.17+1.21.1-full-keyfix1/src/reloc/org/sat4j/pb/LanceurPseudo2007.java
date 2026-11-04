package reloc.org.sat4j.pb;

import reloc.org.sat4j.AbstractLauncher;
import reloc.org.sat4j.ILauncherMode;
import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.pb.reader.OPBReader2012;
import reloc.org.sat4j.pb.tools.OptimalModelIterator;
import reloc.org.sat4j.reader.DimacsReader;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.ISolver;

public class LanceurPseudo2007 extends LanceurPseudo2005 {
   private static final long serialVersionUID = 1L;
   private PBSolverHandle handle;

   public LanceurPseudo2007() {
   }

   public LanceurPseudo2007(ASolverFactory<IPBSolver> factory) {
      super(factory);
   }

   @Override
   protected Reader createReader(ISolver theSolver, String problemname) {
      return problemname.endsWith(".cnf") ? new DimacsReader(theSolver) : new OPBReader2012(this.handle);
   }

   @Override
   protected void configureLauncher() {
      this.handle = new PBSolverHandle((IPBSolver)this.solver);
      String all = System.getProperty("all");
      if (all != null) {
         this.feedWithDecorated = true;
         this.solver = new OptimalModelIterator(new OptToPBSATAdapter(this.handle));
         this.setLauncherMode(ILauncherMode.DECISION);
      }

      super.configureLauncher();
   }

   public static void main(String[] args) {
      AbstractLauncher lanceur = new LanceurPseudo2007();
      if (args.length != 0 && args.length <= 3) {
         lanceur.addHook();
         lanceur.run(args);
         System.exit(lanceur.getExitCode().value());
      } else {
         lanceur.usage();
      }
   }
}
