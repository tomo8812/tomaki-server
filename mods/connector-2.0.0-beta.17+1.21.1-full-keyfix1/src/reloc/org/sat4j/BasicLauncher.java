package reloc.org.sat4j;

import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.minisat.SolverFactory;
import reloc.org.sat4j.reader.InstanceReader;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.ISolver;

public class BasicLauncher<T extends ISolver> extends AbstractLauncher {
   private static final long serialVersionUID = 1L;
   private final ASolverFactory<T> factory;

   public BasicLauncher(ASolverFactory<T> factory) {
      this.factory = factory;
   }

   public static void main(String[] args) {
      BasicLauncher<ISolver> lanceur = new BasicLauncher<>(SolverFactory.instance());
      if (args.length != 0 && args.length <= 3) {
         lanceur.addHook();
         lanceur.run(args);
         System.exit(lanceur.getExitCode().value());
      } else {
         lanceur.usage();
      }
   }

   @Override
   protected ISolver configureSolver(String[] args) {
      ISolver asolver;
      if (args.length >= 2) {
         asolver = this.factory.createSolverByName(args[0]);
      } else {
         asolver = this.factory.defaultSolver();
      }

      if (args.length == 3) {
         asolver.setTimeout(Integer.valueOf(args[1]));
      } else {
         asolver.setTimeout(Integer.MAX_VALUE);
      }

      if (!"BRESIL".equals(System.getProperty("prime")) && System.getProperty("all") == null) {
         asolver.setDBSimplificationAllowed(true);
      }

      this.getLogWriter().println(asolver.toString("c "));
      return asolver;
   }

   @Override
   protected Reader createReader(ISolver theSolver, String problemname) {
      return new InstanceReader(theSolver);
   }

   @Override
   public void usage() {
      this.log("java -jar org.sat4j.core.jar <cnffile>");
   }

   @Override
   protected String getInstanceName(String[] args) {
      return args.length == 0 ? null : args[args.length - 1];
   }
}
