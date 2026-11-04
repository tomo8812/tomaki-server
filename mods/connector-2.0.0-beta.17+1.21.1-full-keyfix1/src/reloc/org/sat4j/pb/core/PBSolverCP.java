package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearnedConstraintsEvaluationType;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.minisat.core.Pair;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.restarts.MiniSATRestarts;
import reloc.org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import reloc.org.sat4j.pb.constraints.pb.ConflictMap;
import reloc.org.sat4j.pb.constraints.pb.IConflict;
import reloc.org.sat4j.pb.constraints.pb.IConflictFactory;
import reloc.org.sat4j.pb.constraints.pb.IPostProcess;
import reloc.org.sat4j.pb.constraints.pb.IPreProcess;
import reloc.org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import reloc.org.sat4j.pb.constraints.pb.NoPostProcess;
import reloc.org.sat4j.pb.constraints.pb.NoPreProcess;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.pb.constraints.pb.SkipStrategy;
import reloc.org.sat4j.pb.orders.BumpStrategy;
import reloc.org.sat4j.pb.orders.Bumper;
import reloc.org.sat4j.pb.orders.IBumper;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.TimeoutException;

public class PBSolverCP extends PBSolver {
   private static final long serialVersionUID = 1L;
   private boolean noRemove = true;
   private SkipStrategy skipAllow = SkipStrategy.SKIP;
   private IConflictFactory conflictFactory = ConflictMap.factory();
   private IPreProcess preprocess = NoPreProcess.instance();
   private IPostProcess postprocess = NoPostProcess.instance();
   private IWeakeningStrategy weakeningStrategy = IWeakeningStrategy.UNASSIGNED_FIRST;
   private AutoDivisionStrategy autoDivisionStrategy = AutoDivisionStrategy.DISABLED;
   private BumpStrategy bumpStrategy = BumpStrategy.ALWAYS_ONE;
   private IBumper bumper = Bumper.ANY;
   private final IVec<String> conflictVariables = new Vec<>();
   private final IVec<String> conflictConstraints = new Vec<>();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !PBSolverCP.class.desiredAssertionStatus();

   public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order) {
      super(learner, dsf, new SearchParams(1.5, 100), order, new MiniSATRestarts());
      this.setLearnedConstraintsDeletionStrategy(LearnedConstraintsEvaluationType.ACTIVITY);
   }

   public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, RestartStrategy restarter) {
      super(learner, dsf, params, order, restarter);
      this.setLearnedConstraintsDeletionStrategy(LearnedConstraintsEvaluationType.ACTIVITY);
   }

   public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order) {
      super(learner, dsf, params, order, new MiniSATRestarts());
      this.setLearnedConstraintsDeletionStrategy(LearnedConstraintsEvaluationType.ACTIVITY);
   }

   public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      this(learner, dsf, order);
      this.noRemove = noRemove;
      this.skipAllow = skipAllow;
   }

   public PBSolverCP(
      LearningStrategy<PBDataStructureFactory> learner,
      PBDataStructureFactory dsf,
      SearchParams params,
      IOrder order,
      RestartStrategy restarter,
      boolean noRemove,
      SkipStrategy skipAllow
   ) {
      this(learner, dsf, params, order, restarter);
      this.noRemove = noRemove;
      this.skipAllow = skipAllow;
   }

   public PBSolverCP(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, boolean noRemove, SkipStrategy skipAllow
   ) {
      this(learner, dsf, params, order);
      this.noRemove = noRemove;
      this.skipAllow = skipAllow;
   }

   @Override
   public void analyze(Constr myconfl, Pair results) throws TimeoutException {
      if (this.someCriteria()) {
         this.analyzeCP(myconfl, results);
      } else {
         super.analyze(myconfl, results);
      }
   }

   public void analyzeCP(Constr myconfl, Pair results) throws TimeoutException {
      int litImplied = this.trail.last();
      int currentLevel = this.voc.getLevel(litImplied);
      IConflict confl = this.chooseConflict((PBConstr)myconfl, currentLevel);
      confl.setDecisionLevel(currentLevel);
      assert confl.slackConflict().signum() < 0;

      while (!confl.isUnsat() && !confl.isAssertive(currentLevel)) {
         if (!this.undertimeout) {
            throw new TimeoutException();
         }

         PBConstr constraint = (PBConstr)this.voc.getReason(litImplied);
         confl.resolve(constraint, litImplied, this);
         this.updateNumberOfReductions(confl);
         assert confl.slackConflict().signum() < 0;
         if (this.trail.size() != 1) {
            this.undoOne();
            if (this.decisionLevel() != 0) {
               litImplied = this.trail.last();
               if (this.voc.getLevel(litImplied) != currentLevel) {
                  this.trailLim.pop();
                  this.slistener.backtracking(LiteralsUtils.toDimacs(litImplied));
               }

               assert this.voc.getLevel(litImplied) <= currentLevel;
               currentLevel = this.voc.getLevel(litImplied);
               confl.setDecisionLevel(currentLevel);
               assert confl.slackIsCorrect(currentLevel);
               assert currentLevel == this.decisionLevel();
               assert litImplied > 1;
               continue;
            }
         }
         break;
      }

      assert confl.isAssertive(currentLevel) || this.trail.size() == 1 || this.decisionLevel() == 0 || confl.isUnsat();
      assert currentLevel == this.decisionLevel();
      confl.setDecisionLevel(currentLevel);
      confl.undoOne(this.trail.last());
      this.undoOne();
      this.qhead = this.trail.size();
      this.updateNumberOfReducedLearnedConstraints(confl);
      if (!confl.isUnsat() && confl.size() != 0 && this.decisionLevel() != 0 && (this.trail.size() != 0 || confl.slackConflict().signum() >= 0)) {
         confl.postProcess(currentLevel);
         PBConstr resConstr = (PBConstr)this.dsfactory.createUnregisteredPseudoBooleanConstraint(confl);
         this.getLearnedConstraintsDeletionStrategy().onClauseLearning(resConstr);
         results.setReason(resConstr);
         int backtrackLevel = confl.getBacktrackLevel(currentLevel);
         results.setBacktrackLevel(backtrackLevel);
      } else {
         results.setReason(null);
         results.setBacktrackLevel(-1);
      }
   }

   protected IConflict chooseConflict(PBConstr myconfl, int level) {
      return this.conflictFactory
         .createConflict(
            myconfl, level, this.noRemove, this.skipAllow, this.preprocess, this.postprocess, this.weakeningStrategy, this.autoDivisionStrategy, this.pbStats
         );
   }

   @Override
   public String toString(String prefix) {
      return prefix
         + "Cutting planes based inference ("
         + this.getClass().getName()
         + ")\n"
         + (this.noRemove ? "" : prefix + " - Removing satisfied literals at a higher level before CP \n")
         + prefix
         + " - "
         + this.skipAllow.getDescription()
         + "\n"
         + prefix
         + " - "
         + this.autoDivisionStrategy
         + "\n"
         + prefix
         + " - "
         + this.postprocess
         + "\n"
         + prefix
         + " - "
         + this.preprocess
         + "\n"
         + prefix
         + " - "
         + this.conflictFactory
         + "\n"
         + prefix
         + " - "
         + this.weakeningStrategy
         + "\n"
         + prefix
         + " - Bump strategy: "
         + this.bumpStrategy
         + "\n"
         + prefix
         + " - Bumper: "
         + this.bumper
         + "\n"
         + super.toString(prefix);
   }

   void initExplanation() {
      this.conflictVariables.clear();
      this.conflictConstraints.clear();
   }

   boolean someCriteria() {
      return true;
   }

   protected void updateNumberOfReductions(IConflict confl) {
   }

   protected void updateNumberOfReducedLearnedConstraints(IConflict confl) {
   }

   public void setBumpStrategy(BumpStrategy bumpStrategy) {
      this.bumpStrategy = bumpStrategy;
   }

   public void setBumper(IBumper bumperEffective) {
      this.bumper = bumperEffective;
   }

   public SkipStrategy isSkipAllow() {
      return this.skipAllow;
   }

   public void setSkipAllow(SkipStrategy skipAllow) {
      this.skipAllow = skipAllow;
   }

   public boolean isNoRemove() {
      return this.noRemove;
   }

   public void setNoRemove(boolean noRemove) {
      this.noRemove = noRemove;
   }

   public IConflictFactory getConflictFactory() {
      return this.conflictFactory;
   }

   public void setConflictFactory(IConflictFactory conflictFactory) {
      this.conflictFactory = conflictFactory;
   }

   public void setPreprocess(IPreProcess preprocess) {
      this.preprocess = preprocess;
   }

   public IPostProcess getPostprocess() {
      return this.postprocess;
   }

   public void setPostprocess(IPostProcess postprocess) {
      this.postprocess = postprocess;
   }

   public IWeakeningStrategy getWeakeningStrategy() {
      return this.weakeningStrategy;
   }

   public void setWeakeningStrategy(IWeakeningStrategy weakeningStrategy) {
      this.weakeningStrategy = weakeningStrategy;
   }

   public void setAutoDivisionStrategy(AutoDivisionStrategy autoDivisionStrategy) {
      this.autoDivisionStrategy = autoDivisionStrategy;
   }

   @Override
   public void varBumpActivity(Constr constr, int i, int p) {
      this.bumper.varBumpActivity(this.voc, this.bumpStrategy, this.getOrder(), (PBConstr)constr, i, p);
   }

   @Override
   public void postBumpActivity(Constr constr) {
      this.bumper.postBumpActivity(this.getOrder(), (PBConstr)constr);
   }
}
