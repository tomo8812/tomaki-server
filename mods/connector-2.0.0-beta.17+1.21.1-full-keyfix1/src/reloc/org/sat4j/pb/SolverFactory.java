package reloc.org.sat4j.pb;

import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;
import reloc.org.sat4j.minisat.core.LearnedConstraintsDeletionStrategy;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.minisat.learning.ClauseOnlyLearning;
import reloc.org.sat4j.minisat.learning.MiniSATLearning;
import reloc.org.sat4j.minisat.learning.NoLearningButHeuristics;
import reloc.org.sat4j.minisat.orders.PhaseInLastLearnedClauseSelectionStrategy;
import reloc.org.sat4j.minisat.orders.RSATPhaseSelectionStrategy;
import reloc.org.sat4j.minisat.orders.UserFixedPhaseSelectionStrategy;
import reloc.org.sat4j.minisat.orders.VarOrderHeap;
import reloc.org.sat4j.minisat.restarts.ArminRestarts;
import reloc.org.sat4j.minisat.restarts.Glucose21Restarts;
import reloc.org.sat4j.minisat.restarts.LubyRestarts;
import reloc.org.sat4j.minisat.restarts.MiniSATRestarts;
import reloc.org.sat4j.minisat.restarts.NoRestarts;
import reloc.org.sat4j.pb.constraints.AbstractPBDataStructureFactory;
import reloc.org.sat4j.pb.constraints.CompetMinHTmixedClauseCardConstrDataStructureFactory;
import reloc.org.sat4j.pb.constraints.CompetResolutionMinPBLongMixedWLClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.CompetResolutionPBLongMixedHTClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.CompetResolutionPBLongMixedWLClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.CompetResolutionPBMixedHTClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.CompetResolutionPBMixedWLClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.PBLongMaxClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.PBLongMinClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.PBMaxClauseAtLeastConstrDataStructure;
import reloc.org.sat4j.pb.constraints.PBMaxClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.PBMaxDataStructure;
import reloc.org.sat4j.pb.constraints.PBMinClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.PBMinDataStructure;
import reloc.org.sat4j.pb.constraints.PuebloPBMinClauseAtLeastConstrDataStructure;
import reloc.org.sat4j.pb.constraints.PuebloPBMinClauseCardConstrDataStructure;
import reloc.org.sat4j.pb.constraints.PuebloPBMinDataStructure;
import reloc.org.sat4j.pb.constraints.pb.ConflictMapDivideByPivot;
import reloc.org.sat4j.pb.constraints.pb.SkipStrategy;
import reloc.org.sat4j.pb.core.PBDataStructureFactory;
import reloc.org.sat4j.pb.core.PBSolver;
import reloc.org.sat4j.pb.core.PBSolverCP;
import reloc.org.sat4j.pb.core.PBSolverCPCardLearning;
import reloc.org.sat4j.pb.core.PBSolverCPClauseLearning;
import reloc.org.sat4j.pb.core.PBSolverCPLong;
import reloc.org.sat4j.pb.core.PBSolverCPLongDivideBy2;
import reloc.org.sat4j.pb.core.PBSolverCPLongDivideByGCD;
import reloc.org.sat4j.pb.core.PBSolverCPLongReduceToCard;
import reloc.org.sat4j.pb.core.PBSolverCPLongRounding;
import reloc.org.sat4j.pb.core.PBSolverCPReduceByGCD;
import reloc.org.sat4j.pb.core.PBSolverCPReduceByPowersOf2;
import reloc.org.sat4j.pb.core.PBSolverCautious;
import reloc.org.sat4j.pb.core.PBSolverClause;
import reloc.org.sat4j.pb.core.PBSolverResCP;
import reloc.org.sat4j.pb.core.PBSolverResolution;
import reloc.org.sat4j.pb.core.PBSolverWithImpliedClause;
import reloc.org.sat4j.pb.lcds.PBGlucoseLCDS;
import reloc.org.sat4j.pb.orders.Bumper;
import reloc.org.sat4j.pb.orders.BumperEffective;
import reloc.org.sat4j.pb.orders.VarOrderHeapObjective;
import reloc.org.sat4j.pb.restarts.GrowingCoefficientRestarts;
import reloc.org.sat4j.pb.tools.InprocCardConstrLearningSolver;
import reloc.org.sat4j.pb.tools.ManyCorePB;
import reloc.org.sat4j.pb.tools.PreprocCardConstrLearningSolver;
import reloc.org.sat4j.specs.ISolver;

public final class SolverFactory extends ASolverFactory<IPBSolver> {
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

   public static PBSolverResolution newPBResAllPB() {
      return newPBRes(new PBMaxDataStructure());
   }

   public static PBSolverCP newPBCPAllPB() {
      return newPBCP(new PBMaxDataStructure());
   }

   public static IPBSolver newOPBStringSolver() {
      return new OPBStringSolver();
   }

   public static PBSolverCP newPBCPMixedConstraints() {
      return newPBCP(new PBMaxClauseCardConstrDataStructure());
   }

   public static PBSolverCP newPBCPMixedConstraintsObjective() {
      return newPBCP(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective());
   }

   public static PBSolverCP newCompetPBCPMixedConstraintsObjective() {
      return newPBCP(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective());
   }

   public static PBSolverCP newCompetPBCPMixedConstraintsMinObjective() {
      return newPBCP(new PBMinClauseCardConstrDataStructure(), new VarOrderHeapObjective());
   }

   public static PBSolverCP newCompetPBCPMixedConstraintsLongMaxObjective() {
      return newPBCP(new PBLongMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective());
   }

   public static PBSolverCP newCompetPBCPRemoveSatisfiedMixedConstraintsLongMaxObjective() {
      return newPBCP(new PBLongMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), false, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCompetPBCPMixedConstraintsLongMinObjective() {
      return newPBCP(new PBLongMinClauseCardConstrDataStructure(), new VarOrderHeapObjective());
   }

   public static PBSolverCP newPBCPMixedConstraintsObjectiveLearnJustClauses() {
      ClauseOnlyLearning<PBDataStructureFactory> learning = new ClauseOnlyLearning<>();
      return new PBSolverCP(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective());
   }

   public static PBSolverCP newCompetPBCPMixedConstraintsObjectiveLearnJustClauses() {
      ClauseOnlyLearning<PBDataStructureFactory> learning = new ClauseOnlyLearning<>();
      return new PBSolverCP(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective());
   }

   private static PBSolverCP newPBKiller(IPhaseSelectionStrategy phase) {
      ClauseOnlyLearning<PBDataStructureFactory> learning = new ClauseOnlyLearning<>();
      return new PBSolverCP(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(phase));
   }

   public static PBSolverCP newPBKillerRSAT() {
      return newPBKiller(new RSATPhaseSelectionStrategy());
   }

   public static PBSolverCP newPBKillerClassic() {
      return newPBKiller(new PhaseInLastLearnedClauseSelectionStrategy());
   }

   public static PBSolverCP newPBKillerFixed() {
      return newPBKiller(new UserFixedPhaseSelectionStrategy());
   }

   private static PBSolverCP newCompetPBKiller(IPhaseSelectionStrategy phase) {
      ClauseOnlyLearning<PBDataStructureFactory> learning = new ClauseOnlyLearning<>();
      return new PBSolverCP(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(phase));
   }

   public static PBSolverCP newCompetPBKillerRSAT() {
      return newCompetPBKiller(new RSATPhaseSelectionStrategy());
   }

   public static PBSolverCP newCompetPBKillerClassic() {
      return newCompetPBKiller(new PhaseInLastLearnedClauseSelectionStrategy());
   }

   public static PBSolverCP newCompetPBKillerFixed() {
      return newCompetPBKiller(new UserFixedPhaseSelectionStrategy());
   }

   public static PBSolverCP newMiniLearningOPBClauseCardConstrMaxSpecificOrderIncrementalReductionToClause() {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverClause(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverCP newPBCPMixedConstraintsObjectiveNoLearning() {
      NoLearningButHeuristics<PBDataStructureFactory> learning = new NoLearningButHeuristics<>();
      PBSolverCP solver = new PBSolverCP(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverResolution newPBResMixedConstraintsObjective() {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverResolution solver = new PBSolverResolution(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), new MiniSATRestarts());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverResolution newCompetPBResWLMixedConstraintsObjectiveExpSimp() {
      return newCompetPBResMixedConstraintsObjectiveExpSimp(new CompetResolutionPBMixedWLClauseCardConstrDataStructure());
   }

   public static PBSolverResolution newCompetPBResHTMixedConstraintsObjectiveExpSimp() {
      return newCompetPBResMixedConstraintsObjectiveExpSimp(new CompetResolutionPBMixedHTClauseCardConstrDataStructure());
   }

   public static PBSolverResolution newCompetPBResLongHTMixedConstraintsObjectiveExpSimp() {
      return newCompetPBResMixedConstraintsObjectiveExpSimp(new CompetResolutionPBLongMixedHTClauseCardConstrDataStructure());
   }

   public static PBSolverResolution newCompetPBResLongWLMixedConstraintsObjectiveExpSimp() {
      return newCompetPBResMixedConstraintsObjectiveExpSimp(new CompetResolutionPBLongMixedWLClauseCardConstrDataStructure());
   }

   public static PBSolverResolution newCompetMinPBResLongWLMixedConstraintsObjectiveExpSimp() {
      return newCompetPBResMixedConstraintsObjectiveExpSimp(new CompetResolutionMinPBLongMixedWLClauseCardConstrDataStructure());
   }

   public static PBSolverResolution newCompetPBResMixedConstraintsObjectiveExpSimp(PBDataStructureFactory dsf) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverResolution solver = new PBSolverResolution(learning, dsf, new VarOrderHeapObjective(new RSATPhaseSelectionStrategy()), new ArminRestarts());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static PBSolverResolution newPBResHTMixedConstraintsObjective() {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      AbstractPBDataStructureFactory ds = new CompetResolutionPBMixedHTClauseCardConstrDataStructure();
      ds.setNormalizer(AbstractPBDataStructureFactory.NO_COMPETITION);
      PBSolverResolution solver = new PBSolverResolution(learning, ds, new VarOrderHeapObjective(), new MiniSATRestarts());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverResolution newCompetPBResMinHTMixedConstraintsObjective() {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverResolution solver = new PBSolverResolution(
         learning, new CompetMinHTmixedClauseCardConstrDataStructureFactory(), new VarOrderHeapObjective(), new MiniSATRestarts()
      );
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverResolution newPBResMinHTMixedConstraintsObjective() {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      AbstractPBDataStructureFactory ds = new CompetMinHTmixedClauseCardConstrDataStructureFactory();
      ds.setNormalizer(AbstractPBDataStructureFactory.NO_COMPETITION);
      PBSolverResolution solver = new PBSolverResolution(learning, ds, new VarOrderHeapObjective(), new MiniSATRestarts());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverResolution newCompetPBResMixedConstraintsObjectiveExpSimp() {
      PBSolverResolution solver = newPBResMixedConstraintsObjective();
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static PBSolverResolution newPBResHTMixedConstraintsObjectiveExpSimp() {
      PBSolverResolution solver = newPBResHTMixedConstraintsObjective();
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static PBSolverResolution newCompetPBResMinHTMixedConstraintsObjectiveExpSimp() {
      PBSolverResolution solver = newCompetPBResMinHTMixedConstraintsObjective();
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static PBSolverClause newPBCPMixedConstraintsReduceToClause() {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverClause solver = new PBSolverClause(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeap());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverCautious newPBCPMixedConstraintsCautious(int bound) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCautious solver = new PBSolverCautious(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), bound);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverCautious newPBCPMixedConstraintsCautious() {
      return newPBCPMixedConstraintsCautious(10);
   }

   public static PBSolverResCP newPBCPMixedConstraintsResCP(long bound) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverResCP solver = new PBSolverResCP(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), bound);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static PBSolverResCP newPBCPMixedConstraintsResCP() {
      return newPBCPMixedConstraintsResCP(100000L);
   }

   public static PBSolverWithImpliedClause newPBCPMixedConstrainsImplied() {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverWithImpliedClause solver = new PBSolverWithImpliedClause(learning, new PBMaxClauseCardConstrDataStructure(), new VarOrderHeap());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverCP newMiniOPBClauseAtLeastConstrMax() {
      return newPBCP(new PBMaxClauseAtLeastConstrDataStructure());
   }

   public static PBSolverResolution newPBResAllPBWL() {
      return newPBRes(new PBMinDataStructure());
   }

   public static PBSolverCP newPBCPAllPBWL() {
      return newPBCP(new PBMinDataStructure());
   }

   public static PBSolverResolution newPBResAllPBWLPueblo() {
      return newPBRes(new PuebloPBMinDataStructure());
   }

   private static PBSolverResolution newPBRes(PBDataStructureFactory dsf) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverResolution solver = new PBSolverResolution(learning, dsf, new VarOrderHeap(), new MiniSATRestarts());
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      return solver;
   }

   public static PBSolverCP newPBCPAllPBWLPueblo() {
      return newPBCP(new PuebloPBMinDataStructure());
   }

   public static PBSolverCP newMiniOPBClauseCardMinPueblo() {
      return newPBCP(new PuebloPBMinClauseCardConstrDataStructure());
   }

   public static PBSolverCP newMiniOPBClauseCardMin() {
      return newPBCP(new PBMinClauseCardConstrDataStructure());
   }

   public static PBSolverCP newMiniOPBClauseAtLeastMinPueblo() {
      return newPBCP(new PuebloPBMinClauseAtLeastConstrDataStructure());
   }

   private static PBSolverCP newPBCP(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCP(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPReduceByPowersOf2(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPReduceByPowersOf2(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPReduceByGCD(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPReduceByGCD(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPStar(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPLong(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPStarClauseLearning(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPClauseLearning(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPStarCardLearning(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPCardLearning(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPStarDivideBy2(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPLongDivideBy2(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPStarDivideByGCD(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPLongDivideByGCD(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPStarRounding(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPLongRounding(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   private static PBSolverCP newPBCPStarReduceToCard(PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverCP solver = new PBSolverCPLongReduceToCard(learning, dsf, order, noRemove, skipAllow);
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setRestartStrategy(new ArminRestarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based);
      return solver;
   }

   public static PBSolverCP newPBCP(PBDataStructureFactory dsf, IOrder order) {
      return newPBCP(dsf, order, true, SkipStrategy.NO_SKIP);
   }

   private static PBSolverCP newPBCP(PBDataStructureFactory dsf) {
      return newPBCP(dsf, new VarOrderHeap());
   }

   public static PBSolverCP newCuttingPlanes() {
      return newCompetPBCPMixedConstraintsObjective();
   }

   public static IPBSolver newCuttingPlanesWatched() {
      return newCompetPBCPMixedConstraintsMinObjective();
   }

   public static PBSolverCP newCuttingPlanesReduceByPowersOf2() {
      return newPBCPReduceByPowersOf2(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCuttingPlanesReduceByGCD() {
      return newPBCPReduceByGCD(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCuttingPlanesStar() {
      return newPBCPStar(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCuttingPlanesStarRounding() {
      return newPBCPStarRounding(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCuttingPlanesStarReduceToCard() {
      return newPBCPStarReduceToCard(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCuttingPlanesStarClauseLearning() {
      return newPBCPStarClauseLearning(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCuttingPlanesStarCardLearning() {
      return newPBCPStarCardLearning(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCuttingPlanesStarDivideBy2() {
      return newPBCPStarDivideBy2(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static PBSolverCP newCuttingPlanesStarDivideByGCD() {
      return newPBCPStarDivideByGCD(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.NO_SKIP);
   }

   public static IPBSolver newCuttingPlanesReduceByPowersOf2Skip() {
      return newPBCPReduceByPowersOf2(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesReduceByGCDSkip() {
      return newPBCPReduceByGCD(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesStarSkip() {
      return newPBCPStar(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesStarRoundingSkip() {
      return newPBCPStarRounding(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesStarReduceToCardSkip() {
      return newPBCPStarReduceToCard(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesStarClauseLearningSkip() {
      return newPBCPStarClauseLearning(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesStarCardLearningSkip() {
      return newPBCPStarCardLearning(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesStarDivideBy2Skip() {
      return newPBCPStarDivideBy2(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesStarDivideByGCDSkip() {
      return newPBCPStarDivideByGCD(new PBMaxClauseCardConstrDataStructure(), new VarOrderHeapObjective(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newCuttingPlanesAggressiveCleanup() {
      PBSolverCP solver = newCompetPBCPMixedConstraintsObjective();
      solver.setLearnedConstraintsDeletionStrategy(solver.fixedSize(100));
      return solver;
   }

   public static IPBSolver newResolution() {
      return newResolutionGlucoseExpSimp();
   }

   public static IPBSolver newResolutionWL() {
      PBSolverResolution solver = newCompetPBResMixedConstraintsObjectiveExpSimp(new PuebloPBMinDataStructure());
      solver.setSimplifier(Solver.NO_SIMPLIFICATION);
      solver.setRestartStrategy(new Glucose21Restarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.lbd_based);
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static IPBSolver newBoth() {
      return new ManyCorePB<>(newResolution(), newCuttingPlanes());
   }

   public static IPBSolver newSoberBoth() {
      PBSolverCP cp = newCuttingPlanes();
      cp.setTimeout(60);
      return new ManyCorePB<>(newResolution(), new PBTimeoutIsolator(cp));
   }

   public static IPBSolver newBothStar() {
      return new ManyCorePB<>(newResolution(), newCuttingPlanesStar());
   }

   public static IPBSolver newSATUNSAT() {
      return new ManyCorePB<>(newSAT(), newUNSAT());
   }

   public static PBSolverResolution newSAT() {
      PBSolverResolution solver = newResolutionGlucose();
      solver.setRestartStrategy(new LubyRestarts(100));
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based_low_memory);
      return solver;
   }

   public static PBSolverResolution newUNSAT() {
      PBSolverResolution solver = newResolutionGlucose();
      solver.setRestartStrategy(new NoRestarts());
      solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
      return solver;
   }

   public static PBSolverResolution newResolutionGlucose() {
      PBSolverResolution solver = newCompetPBResLongWLMixedConstraintsObjectiveExpSimp();
      solver.setSimplifier(Solver.NO_SIMPLIFICATION);
      solver.setRestartStrategy(new Glucose21Restarts());
      solver.setLearnedConstraintsDeletionStrategy(solver.lbd_based);
      return solver;
   }

   public static PBSolverResolution newResolutionGlucose21() {
      PBSolverResolution solver = newResolutionGlucose();
      solver.setRestartStrategy(new Glucose21Restarts());
      return solver;
   }

   public static PBSolverResolution newResolutionGlucoseSimpleSimp() {
      PBSolverResolution solver = newResolutionGlucose();
      solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
      return solver;
   }

   public static PBSolverResolution newResolutionGlucoseExpSimp() {
      PBSolverResolution solver = newResolutionGlucose();
      solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
      return solver;
   }

   public static IPBSolver newSimpleSimplification() {
      PBSolverResolution solver = newCompetPBResWLMixedConstraintsObjectiveExpSimp();
      solver.setLearnedConstraintsDeletionStrategy(solver.lbd_based);
      solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
      return solver;
   }

   public static IPBSolver newResolutionSimpleRestarts() {
      PBSolverResolution solver = newCompetPBResLongWLMixedConstraintsObjectiveExpSimp();
      solver.setLearnedConstraintsDeletionStrategy(solver.lbd_based);
      solver.setRestartStrategy(new MiniSATRestarts());
      return solver;
   }

   public static IPBSolver newResolutionMaxMemory() {
      PBSolverResolution solver = newCompetPBResLongWLMixedConstraintsObjectiveExpSimp();
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based_low_memory);
      return solver;
   }

   public static PBSolver newDefault() {
      return newResolutionGlucose21();
   }

   public static IPBSolver newDefaultNonNormalized() {
      PBSolver solver = newDefault();
      CompetResolutionPBLongMixedWLClauseCardConstrDataStructure ds = new CompetResolutionPBLongMixedWLClauseCardConstrDataStructure();
      ds.setNormalizer(AbstractPBDataStructureFactory.NO_COMPETITION);
      solver.setDataStructureFactory(ds);
      return solver;
   }

   public PBSolver defaultSolver() {
      return newDefault();
   }

   public static IPBSolver newDefaultOptimizer() {
      return new OptToPBSATAdapter(new PseudoOptDecorator(newDefault()));
   }

   public static IPBSolver newLight() {
      return newCompetPBResMixedConstraintsObjectiveExpSimp();
   }

   public IPBSolver lightSolver() {
      return newLight();
   }

   public static IPBSolver newEclipseP2() {
      MiniSATLearning<PBDataStructureFactory> learning = new MiniSATLearning<>();
      PBSolverResolution solver = new PBSolverResolution(
         learning,
         new CompetResolutionPBMixedHTClauseCardConstrDataStructure(),
         new VarOrderHeapObjective(new RSATPhaseSelectionStrategy()),
         new ArminRestarts()
      );
      learning.setDataStructureFactory(solver.getDSFactory());
      learning.setVarActivityListener(solver);
      solver.setTimeoutOnConflicts(300);
      solver.setVerbose(false);
      solver.setLearnedConstraintsDeletionStrategy(solver.activity_based_low_memory);
      return new OptToPBSATAdapter(new PseudoOptDecorator(solver));
   }

   public static PreprocCardConstrLearningSolver<IPBSolver> newDetectCards() {
      return new PreprocCardConstrLearningSolver<>(newCuttingPlanes());
   }

   public static PreprocCardConstrLearningSolver<IPBSolver> newDetectCardsAndExit() {
      return new PreprocCardConstrLearningSolver<>(newDefault(), true);
   }

   public static IPBSolver newInprocDetectCards() {
      InprocCardConstrLearningSolver solver = (InprocCardConstrLearningSolver)newLazyInprocDetectCards();
      solver.setDetectCardFromAllConstraintsInCflAnalysis(true);
      return solver;
   }

   public static IPBSolver newInprocDetectCardsSkip() {
      InprocCardConstrLearningSolver solver = (InprocCardConstrLearningSolver)newLazyInprocDetectCardsSkip();
      solver.setDetectCardFromAllConstraintsInCflAnalysis(true);
      return solver;
   }

   public static IPBSolver newLazyInprocDetectCards() {
      return new InprocCardConstrLearningSolver(
         new MiniSATLearning<>(), new PBMaxClauseCardConstrDataStructure(), new VarOrderHeap(), true, SkipStrategy.NO_SKIP
      );
   }

   public static IPBSolver newLazyInprocDetectCardsSkip() {
      return new InprocCardConstrLearningSolver(new MiniSATLearning<>(), new PBMaxClauseCardConstrDataStructure(), new VarOrderHeap(), true, SkipStrategy.SKIP);
   }

   public static IPBSolver newPartialRoundingSat() {
      PBSolverCP solver = newCuttingPlanes();
      solver.setConflictFactory(ConflictMapDivideByPivot.partialWeakeningOnBothFactory());
      return solver;
   }

   public static IPBSolver newRoundingSat() {
      PBSolverCP solver = newCuttingPlanes();
      solver.setConflictFactory(ConflictMapDivideByPivot.fullWeakeningOnBothFactory());
      return solver;
   }

   public static IPBSolver newCuttingPlanesPOS2020() {
      PBSolverCP solver = newCuttingPlanes();
      solver.setBumper(new BumperEffective());
      LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS.newUnassignedSame(solver, solver.lbd_based.getTimer());
      solver.setLearnedConstraintsDeletionStrategy(lcds);
      solver.setRestartStrategy(new GrowingCoefficientRestarts());
      return solver;
   }

   public static IPBSolver newPartialRoundingSatPOS2020() {
      PBSolverCP solver = (PBSolverCP)newPartialRoundingSat();
      solver.setBumper(Bumper.ASSIGNED);
      LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS.newDegreeSize(solver, solver.lbd_based.getTimer());
      solver.setLearnedConstraintsDeletionStrategy(lcds);
      return solver;
   }

   public static IPBSolver newRoundingSatPOS2020() {
      PBSolverCP solver = (PBSolverCP)newRoundingSat();
      solver.setBumper(Bumper.ASSIGNED);
      LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS.newSlack(solver, solver.lbd_based.getTimer());
      solver.setLearnedConstraintsDeletionStrategy(lcds);
      return solver;
   }

   public static IPBSolver newCuttingPlanesPOS2020WL() {
      PBSolverCP solver = (PBSolverCP)newCuttingPlanesPOS2020();
      solver.setDataStructureFactory(new PuebloPBMinDataStructure());
      return solver;
   }

   public static IPBSolver newPartialRoundingSatPOS2020WL() {
      PBSolverCP solver = (PBSolverCP)newPartialRoundingSatPOS2020();
      solver.setDataStructureFactory(new PuebloPBMinDataStructure());
      return solver;
   }

   public static IPBSolver newRoundingSatPOS2020WL() {
      PBSolverCP solver = (PBSolverCP)newRoundingSatPOS2020();
      solver.setDataStructureFactory(new PuebloPBMinDataStructure());
      return solver;
   }

   public static IPBSolver newBothPOS2020() {
      return new ManyCorePB<>(newResolution(), newCuttingPlanesPOS2020());
   }

   public static IPBSolver newBothPOS2020WL() {
      return new ManyCorePB<>(newResolutionWL(), newCuttingPlanesPOS2020WL());
   }

   public static IPBSolver newBothPartialRoundingSatPOS2020WL() {
      return new ManyCorePB<>(newResolutionWL(), newPartialRoundingSatPOS2020WL());
   }

   public static IPBSolver newBothPartialRoundingSatPOS2020() {
      return new ManyCorePB<>(newResolution(), newPartialRoundingSatPOS2020());
   }

   public static IPBSolver newBothRoundingSatPOS2020WL() {
      return new ManyCorePB<>(newResolutionWL(), newRoundingSatPOS2020WL());
   }

   public static IPBSolver newBothRoundingSatPOS2020() {
      return new ManyCorePB<>(newResolution(), newRoundingSatPOS2020());
   }

   public static IPBSolver newAllPOS2020WL() {
      return new ManyCorePB<>(newResolutionWL(), newRoundingSatPOS2020WL(), newPartialRoundingSatPOS2020WL(), newCuttingPlanesPOS2020WL());
   }

   public static IPBSolver newAllPOS2020() {
      return new ManyCorePB<>(newResolution(), newRoundingSatPOS2020(), newPartialRoundingSatPOS2020(), newCuttingPlanesPOS2020());
   }

   public static IPBSolver newAllSAT2020() {
      return new ManyCorePB<>(newResolution(), newRoundingSat(), newPartialRoundingSat(), newCuttingPlanes());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public ISolver lightSolver() {
      return this.lightSolver();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public ISolver defaultSolver() {
      return this.defaultSolver();
   }
}
