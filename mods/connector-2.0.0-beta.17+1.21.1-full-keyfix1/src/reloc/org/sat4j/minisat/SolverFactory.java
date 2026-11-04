package reloc.org.sat4j.minisat;

import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.minisat.constraints.MixedDataStructureDanielHT;
import reloc.org.sat4j.minisat.constraints.MixedDataStructureDanielWL;
import reloc.org.sat4j.minisat.constraints.MixedDataStructureDanielWLConciseBinary;
import reloc.org.sat4j.minisat.constraints.MixedDataStructureSingleWL;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.ICDCL;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.minisat.learning.LimitedLearning;
import reloc.org.sat4j.minisat.learning.MiniSATLearning;
import reloc.org.sat4j.minisat.learning.NoLearningButHeuristics;
import reloc.org.sat4j.minisat.learning.PercentLengthLearning;
import reloc.org.sat4j.minisat.orders.PhaseCachingAutoEraseStrategy;
import reloc.org.sat4j.minisat.orders.RSATLastLearnedClausesPhaseSelectionStrategy;
import reloc.org.sat4j.minisat.orders.RSATPhaseSelectionStrategy;
import reloc.org.sat4j.minisat.orders.RandomLiteralSelectionStrategy;
import reloc.org.sat4j.minisat.orders.RandomWalkDecorator;
import reloc.org.sat4j.minisat.orders.VarOrderHeap;
import reloc.org.sat4j.minisat.restarts.ArminRestarts;
import reloc.org.sat4j.minisat.restarts.Glucose21Restarts;
import reloc.org.sat4j.minisat.restarts.LubyRestarts;
import reloc.org.sat4j.minisat.restarts.MiniSATRestarts;
import reloc.org.sat4j.minisat.restarts.NoRestarts;
import reloc.org.sat4j.opt.MinOneDecorator;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.tools.DimacsOutputSolver;
import reloc.org.sat4j.tools.ManyCore;
import reloc.org.sat4j.tools.OptToSatAdapter;
import reloc.org.sat4j.tools.StatisticsSolver;

public final class SolverFactory extends ASolverFactory<ISolver> {
   private static final long serialVersionUID = 1L;
   private static SolverFactory instance;

   private SolverFactory() {
   }

   private static synchronized void createInstance() {
      if (instance == null) {
         instance = new SolverFactory();
      }
   }

   public static SolverFactory instance() {
      if (instance == null) {
         createInstance();
      }

      return instance;
   }

   public static Solver<DataStructureFactory> newMiniLearningHeap() {
      return newMiniLearningHeap(new MixedDataStructureDanielWL());
   }

   public static ICDCL<DataStructureFactory> newMiniLearningHeapEZSimp() {
      Solver<DataStructureFactory> solver = newMiniLearningHeap();
      solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
      return solver;
   }

   public static Solver<DataStructureFactory> newMiniLearningHeapExpSimp() {
      Solver<DataStructureFactory> solver = newMiniLearningHeap();
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static Solver<DataStructureFactory> newMiniLearningHeapRsatExpSimp() {
      Solver<DataStructureFactory> solver = newMiniLearningHeapExpSimp();
      solver.setOrder(new VarOrderHeap(new RSATPhaseSelectionStrategy()));
      return solver;
   }

   public static Solver<DataStructureFactory> newMiniLearningHeapRsatExpSimpBiere() {
      Solver<DataStructureFactory> solver = newMiniLearningHeapRsatExpSimp();
      solver.setRestartStrategy(new ArminRestarts());
      solver.setSearchParams(new SearchParams(1.1, 100));
      return solver;
   }

   public static ICDCL<DataStructureFactory> newMiniLearningHeapRsatExpSimpLuby() {
      ICDCL<DataStructureFactory> solver = newMiniLearningHeapRsatExpSimp();
      solver.setRestartStrategy(new LubyRestarts(512));
      return solver;
   }

   public static ICDCL<DataStructureFactory> newGlucose21() {
      Solver<DataStructureFactory> solver = newMiniLearningHeapRsatExpSimp();
      solver.setRestartStrategy(new Glucose21Restarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.lbd_based);
      return solver;
   }

   private static Solver<DataStructureFactory> newBestCurrentSolverConfiguration(DataStructureFactory dsf) {
      MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<>();
      Solver<DataStructureFactory> solver = new Solver<>(learning, dsf, new VarOrderHeap(new RSATPhaseSelectionStrategy()), new ArminRestarts());
      solver.setSearchParams(new SearchParams(1.1, 100));
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static ICDCL<DataStructureFactory> newGreedySolver() {
      MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<>();
      Solver<DataStructureFactory> solver = new Solver<>(
         learning, new MixedDataStructureDanielWL(), new RandomWalkDecorator(new VarOrderHeap(new RSATPhaseSelectionStrategy())), new NoRestarts()
      );
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static ICDCL<DataStructureFactory> newRandomSolver() {
      MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<>();
      Solver<DataStructureFactory> solver = new Solver<>(
         learning, new MixedDataStructureDanielWL(), new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1.0), new NoRestarts()
      );
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static ICDCL<DataStructureFactory> newDefaultAutoErasePhaseSaving() {
      ICDCL<DataStructureFactory> solver = newBestWL();
      solver.setOrder(new VarOrderHeap(new PhaseCachingAutoEraseStrategy()));
      return solver;
   }

   public static ICDCL<DataStructureFactory> newDefaultMS21PhaseSaving() {
      ICDCL<DataStructureFactory> solver = newBestWL();
      solver.setOrder(new VarOrderHeap(new RSATLastLearnedClausesPhaseSelectionStrategy()));
      return solver;
   }

   public static Solver<DataStructureFactory> newBestWL() {
      return newBestCurrentSolverConfiguration(new MixedDataStructureDanielWL());
   }

   public static ICDCL<DataStructureFactory> newBestHT() {
      return newBestCurrentSolverConfiguration(new MixedDataStructureDanielHT());
   }

   public static ICDCL<DataStructureFactory> newBest17() {
      Solver<DataStructureFactory> solver = newBestCurrentSolverConfiguration(new MixedDataStructureSingleWL());
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION_WLONLY);
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based_low_memory);
      LimitedLearning<DataStructureFactory> learning = new PercentLengthLearning<>(10);
      solver.setLearningStrategy(learning);
      return solver;
   }

   public static Solver<DataStructureFactory> newGlucose() {
      Solver<DataStructureFactory> solver = newBestWL();
      solver.setLearnedConstraintsDeletionStrategy(solver.lbd_based);
      solver.setRestartStrategy(new LubyRestarts(512));
      return solver;
   }

   public static Solver<DataStructureFactory> newMiniLearningHeap(DataStructureFactory dsf) {
      return newMiniLearning(dsf, new VarOrderHeap());
   }

   public static Solver<DataStructureFactory> newMiniLearning(DataStructureFactory dsf, IOrder order) {
      MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<>();
      return new Solver<>(learning, dsf, order, new MiniSATRestarts());
   }

   public static ICDCL<DataStructureFactory> newMiniLearningHeapEZSimpNoRestarts() {
      LimitedLearning<DataStructureFactory> learning = new PercentLengthLearning<>(10);
      Solver<DataStructureFactory> solver = new Solver<>(
         learning, new MixedDataStructureDanielWL(), new SearchParams(Integer.MAX_VALUE), new VarOrderHeap(), new MiniSATRestarts()
      );
      learning.setSolver(solver);
      solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
      return solver;
   }

   public static ICDCL<DataStructureFactory> newMiniLearningHeapEZSimpLongRestarts() {
      LimitedLearning<DataStructureFactory> learning = new PercentLengthLearning<>(10);
      Solver<DataStructureFactory> solver = new Solver<>(
         learning, new MixedDataStructureDanielWL(), new SearchParams(1000), new VarOrderHeap(), new MiniSATRestarts()
      );
      learning.setSolver(solver);
      solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
      return solver;
   }

   public static Solver<DataStructureFactory> newMiniSATHeap() {
      return newMiniSATHeap(new MixedDataStructureDanielWL());
   }

   public static ICDCL<DataStructureFactory> newMiniSATHeapEZSimp() {
      Solver<DataStructureFactory> solver = newMiniSATHeap();
      solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
      return solver;
   }

   public static ICDCL<DataStructureFactory> newMiniSATHeapExpSimp() {
      Solver<DataStructureFactory> solver = newMiniSATHeap();
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static Solver<DataStructureFactory> newMiniSATHeap(DataStructureFactory dsf) {
      MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<>();
      Solver<DataStructureFactory> solver = new Solver<>(learning, dsf, new VarOrderHeap(), new MiniSATRestarts());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static ICDCL<MixedDataStructureDanielWL> newBackjumping() {
      NoLearningButHeuristics<MixedDataStructureDanielWL> learning = new NoLearningButHeuristics<>();
      Solver<MixedDataStructureDanielWL> solver = new Solver<>(learning, new MixedDataStructureDanielWL(), new VarOrderHeap(), new MiniSATRestarts());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static ISolver newMinOneSolver() {
      return new OptToSatAdapter(new MinOneDecorator(newDefault()));
   }

   public static ISolver newAgeLCDS() {
      Solver<?> solver = (Solver<?>)newGlucose21();
      solver.setLearnedConstraintsDeletionStrategy(solver.age_based);
      return solver;
   }

   public static ISolver newActivityLCDS() {
      Solver<?> solver = (Solver<?>)newGlucose21();
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   public static ISolver newSizeLCDS() {
      Solver<?> solver = (Solver<?>)newGlucose21();
      solver.setLearnedConstraintsDeletionStrategy(solver.size_based);
      return solver;
   }

   public static ISolver newDefault() {
      return newGlucose21();
   }

   @Override
   public ISolver defaultSolver() {
      return newDefault();
   }

   public static ISolver newLight() {
      return newMiniLearningHeap();
   }

   @Override
   public ISolver lightSolver() {
      return newLight();
   }

   public static ISolver newDimacsOutput() {
      return new DimacsOutputSolver();
   }

   public static ISolver newStatistics() {
      return new StatisticsSolver();
   }

   public static ISolver newParallel() {
      return new ManyCore<>(
         newSAT(),
         newUNSAT(),
         newMiniLearningHeapRsatExpSimpLuby(),
         newMiniLearningHeapRsatExpSimp(),
         newDefaultAutoErasePhaseSaving(),
         newMiniLearningHeap(),
         newMiniSATHeapExpSimp(),
         newMiniSATHeapEZSimp()
      );
   }

   public static ISolver newSATUNSAT() {
      return new ManyCore<>(newSAT(), newUNSAT());
   }

   public static Solver<DataStructureFactory> newSAT() {
      Solver<DataStructureFactory> solver = (Solver<DataStructureFactory>)newGlucose21();
      solver.setRestartStrategy(new LubyRestarts(100));
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based_low_memory);
      return solver;
   }

   public static Solver<DataStructureFactory> newUNSAT() {
      Solver<DataStructureFactory> solver = (Solver<DataStructureFactory>)newGlucose21();
      solver.setRestartStrategy(new NoRestarts());
      solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
      return solver;
   }

   public static Solver<DataStructureFactory> newConcise() {
      Solver<DataStructureFactory> solver = (Solver<DataStructureFactory>)newGlucose21();
      solver.setDataStructureFactory(new MixedDataStructureDanielWLConciseBinary());
      solver.setSimplifier(Solver.NO_SIMPLIFICATION);
      return solver;
   }

   public static Solver<DataStructureFactory> newNoSimplification() {
      Solver<DataStructureFactory> solver = (Solver<DataStructureFactory>)newGlucose21();
      solver.setSimplifier(Solver.NO_SIMPLIFICATION);
      return solver;
   }
}
