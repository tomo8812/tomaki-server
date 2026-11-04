package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.minisat.core.Pair;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.IPBSolverService;
import reloc.org.sat4j.pb.SolverFactory;
import reloc.org.sat4j.pb.constraints.pb.IConflict;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.pb.constraints.pb.SkipStrategy;
import reloc.org.sat4j.pb.core.PBDataStructureFactory;
import reloc.org.sat4j.pb.core.PBSolverCP;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.SearchListenerAdapter;
import reloc.org.sat4j.specs.TimeoutException;

public class InprocCardConstrLearningSolver extends PBSolverCP {
   private static final long serialVersionUID = 1L;
   private final IPBSolver coSolver;
   private final CardConstrFinder cardFinder;
   private Constr extendedConstr;
   private boolean detectCardFromAllConstraintsInCflAnalysis = false;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !InprocCardConstrLearningSolver.class.desiredAssertionStatus();

   public InprocCardConstrLearningSolver(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow
   ) {
      super(learner, dsf, order, noRemove, skipAllow);
      this.coSolver = SolverFactory.newResolution();
      this.cardFinder = new CardConstrFinder(this.coSolver);
      this.configureSolver();
   }

   public InprocCardConstrLearningSolver(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order) {
      super(learner, dsf, order);
      this.coSolver = SolverFactory.newResolution();
      this.cardFinder = new CardConstrFinder(this.coSolver);
      this.configureSolver();
   }

   public InprocCardConstrLearningSolver(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, boolean noRemove, SkipStrategy skipAllow
   ) {
      super(learner, dsf, params, order, noRemove, skipAllow);
      this.coSolver = SolverFactory.newResolution();
      this.cardFinder = new CardConstrFinder(this.coSolver);
      this.configureSolver();
   }

   public InprocCardConstrLearningSolver(
      LearningStrategy<PBDataStructureFactory> learner,
      PBDataStructureFactory dsf,
      SearchParams params,
      IOrder order,
      RestartStrategy restarter,
      boolean noRemove,
      SkipStrategy skipAllow
   ) {
      super(learner, dsf, params, order, restarter, noRemove, skipAllow);
      this.coSolver = SolverFactory.newResolution();
      this.cardFinder = new CardConstrFinder(this.coSolver);
      this.configureSolver();
   }

   public InprocCardConstrLearningSolver(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, RestartStrategy restarter
   ) {
      super(learner, dsf, params, order, restarter);
      this.coSolver = SolverFactory.newResolution();
      this.cardFinder = new CardConstrFinder(this.coSolver);
      this.configureSolver();
   }

   public InprocCardConstrLearningSolver(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order) {
      super(learner, dsf, params, order);
      this.coSolver = SolverFactory.newResolution();
      this.cardFinder = new CardConstrFinder(this.coSolver);
      this.configureSolver();
   }

   public void setDetectCardFromAllConstraintsInCflAnalysis(boolean value) {
      this.detectCardFromAllConstraintsInCflAnalysis = value;
   }

   private void configureSolver() {
      this.setSearchListener(new SearchListenerAdapter<IPBSolverService>() {
         private static final long serialVersionUID = 1L;

         @Override
         public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
            InprocCardConstrLearningSolver.this.handleConflict(confl);
         }
      });
   }

   protected void handleConflict(IConstr confl) {
      this.extendedConstr = null;
      if (this.constraintIsAdmissibleForExtension(confl)) {
         this.tryToExtendConstraint((PBConstr)confl);
      }
   }

   private void tryToExtendConstraint(PBConstr confl) {
      IVecInt atMostLits = new VecInt(confl.getLits().length);

      for (int lit : confl.getLits()) {
         atMostLits.push((lit >> 1) * ((lit & 1) == 1 ? -1 : 1));
      }

      IVecInt discovered = this.cardFinder.searchCardFromAtMostCard(atMostLits, atMostLits.size() - 1);
      if (discovered != null) {
         IConstr constr = this.addAtMostOnTheFly(discovered, new VecInt(discovered.size(), 1), atMostLits.size() - 1);
         this.sharedConflict = null;
         this.extendedConstr = (Constr)constr;
      }
   }

   private boolean constraintIsAdmissibleForExtension(IConstr confl) {
      return confl instanceof PBConstr && ((PBConstr)confl).canBeSatisfiedByCountingLiterals() && ((PBConstr)confl).requiredNumberOfSatisfiedLiterals() == 1;
   }

   @Override
   public void analyzeCP(Constr myconfl, Pair results) throws TimeoutException {
      if (this.detectCardFromAllConstraintsInCflAnalysis) {
         if (this.extendedConstr == null) {
            this.cardDetectionAnalyzeCP(myconfl, results);
         } else {
            this.cardDetectionAnalyzeCP(this.extendedConstr, results);
         }
      } else if (this.extendedConstr == null) {
         super.analyzeCP(myconfl, results);
      } else {
         super.analyzeCP(this.extendedConstr, results);
      }
   }

   public void cardDetectionAnalyzeCP(Constr myconfl, Pair results) throws TimeoutException {
      int litImplied = this.trail.last();
      int currentLevel = this.voc.getLevel(litImplied);
      IConflict confl = this.chooseConflict((PBConstr)myconfl, currentLevel);
      confl.setDecisionLevel(currentLevel);
      assert confl.slackConflict().signum() < 0;

      while (!confl.isAssertive(currentLevel)) {
         if (!this.undertimeout) {
            throw new TimeoutException();
         }

         PBConstr constraint = (PBConstr)this.voc.getReason(litImplied);
         this.extendedConstr = null;
         if (this.constraintIsAdmissibleForExtension(constraint)) {
            this.tryToExtendConstraint(constraint);
         }

         if (this.extendedConstr != null) {
            constraint = (PBConstr)this.extendedConstr;
         }

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

      assert confl.isAssertive(currentLevel) || this.trail.size() == 1 || this.decisionLevel() == 0;
      assert currentLevel == this.decisionLevel();
      confl.setDecisionLevel(currentLevel);
      this.undoOne();
      this.qhead = this.trail.size();
      this.updateNumberOfReducedLearnedConstraints(confl);
      if (confl.size() != 0 && (this.decisionLevel() != 0 && this.trail.size() != 0 || confl.slackConflict().signum() >= 0)) {
         PBConstr resConstr = (PBConstr)this.dsfactory.createUnregisteredPseudoBooleanConstraint(confl);
         results.setReason(resConstr);
         if (this.decisionLevel() != 0 && this.trail.size() != 0) {
            results.setBacktrackLevel(confl.getBacktrackLevel(currentLevel));
         } else {
            results.setBacktrackLevel(-1);
            results.setReason(null);
         }
      } else {
         results.setReason(null);
         results.setBacktrackLevel(-1);
      }
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      this.coSolver.addPseudoBoolean(lits, coeffs, moreThan, d);
      return super.addPseudoBoolean(lits, coeffs, moreThan, d);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      this.coSolver.addAtMost(literals, coeffs, degree);
      return super.addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      this.coSolver.addAtMost(literals, coeffs, degree);
      return super.addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      this.coSolver.addClause(literals);
      return super.addClause(literals);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      this.coSolver.addAtLeast(literals, coeffs, degree);
      return super.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      this.coSolver.addAtLeast(literals, coeffs, degree);
      return super.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      this.coSolver.addExactly(literals, coeffs, weight);
      return super.addExactly(literals, coeffs, weight);
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      this.coSolver.addAllClauses(clauses);
      super.addAllClauses(clauses);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      this.coSolver.addExactly(literals, coeffs, weight);
      return super.addExactly(literals, coeffs, weight);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      this.coSolver.addAtMost(literals, degree);
      return super.addAtMost(literals, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      this.coSolver.addAtLeast(literals, degree);
      return super.addAtLeast(literals, degree);
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      this.coSolver.addExactly(literals, n);
      return super.addExactly(literals, n);
   }

   @Override
   public IConstr addConstr(Constr constr) {
      this.coSolver.addConstr(constr);
      return super.addConstr(constr);
   }
}
