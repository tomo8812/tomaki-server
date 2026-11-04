package reloc.org.sat4j;

import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.minisat.constraints.MixedDataStructureDanielWL;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.minisat.learning.MiniSATLearning;
import reloc.org.sat4j.minisat.orders.RSATPhaseSelectionStrategy;
import reloc.org.sat4j.minisat.orders.VarOrderHeap;
import reloc.org.sat4j.minisat.restarts.ArminRestarts;
import reloc.org.sat4j.specs.ISolver;

public class LightFactory extends ASolverFactory<ISolver> {
   private static final long serialVersionUID = 1460304168178023681L;
   private static LightFactory instance;

   private static synchronized void createInstance() {
      if (instance == null) {
         instance = new LightFactory();
      }
   }

   public static LightFactory instance() {
      if (instance == null) {
         createInstance();
      }

      return instance;
   }

   @Override
   public ISolver defaultSolver() {
      MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<>();
      Solver<DataStructureFactory> solver = new Solver<>(
         learning, new MixedDataStructureDanielWL(), new VarOrderHeap(new RSATPhaseSelectionStrategy()), new ArminRestarts()
      );
      learning.setSolver(solver);
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      solver.setSearchParams(new SearchParams(1.1, 100));
      return solver;
   }

   @Override
   public ISolver lightSolver() {
      return this.defaultSolver();
   }

   public static void main(String[] args) {
      AbstractLauncher lanceur = new BasicLauncher<>(instance());
      if (args.length != 1) {
         lanceur.usage();
      } else {
         lanceur.run(args);
         System.exit(lanceur.getExitCode().value());
      }
   }
}
