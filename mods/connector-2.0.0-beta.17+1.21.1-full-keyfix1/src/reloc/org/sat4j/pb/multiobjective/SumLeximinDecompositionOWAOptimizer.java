package reloc.org.sat4j.pb.multiobjective;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IIntegerPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.PseudoOptDecorator;
import reloc.org.sat4j.pb.core.IntegerVariable;
import reloc.org.sat4j.specs.AssignmentOrigin;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.SearchListener;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.specs.UnitClauseConsumer;
import reloc.org.sat4j.specs.UnitClauseProvider;

public class SumLeximinDecompositionOWAOptimizer implements IMultiObjOptimizationProblem, IIntegerPBSolver {
   private final List<ObjectiveFunction> objs = new ArrayList<>();
   private final BigInteger[] weights;
   private final IIntegerPBSolver solver;
   protected final List<IntegerVariable> objBoundVariables = new ArrayList<>();
   private final List<IVecInt> atLeastFlags = new ArrayList<>();
   private boolean initDone = false;
   private boolean sumLexStepDone = false;
   private ObjectiveFunction lexObj;
   private ObjectiveFunction sumObj;
   private ObjectiveFunction owaObj;
   private IConstr additionnalCstr = null;
   private BigInteger bestValue = null;
   private PseudoOptDecorator optPb;
   private int[] lastModel;
   private int[] lastModelWithInternalVars;

   public SumLeximinDecompositionOWAOptimizer(IIntegerPBSolver solver, int[] weights) {
      this.solver = solver;
      this.weights = new BigInteger[weights.length];

      for (int i = 0; i < weights.length; i++) {
         this.weights[i] = BigInteger.valueOf(weights[i]);
      }
   }

   public SumLeximinDecompositionOWAOptimizer(IIntegerPBSolver solver, BigInteger[] weights) {
      this.solver = solver;
      this.weights = weights;
   }

   @Override
   public boolean admitABetterSolution() throws TimeoutException {
      return this.admitABetterSolution(VecInt.EMPTY);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      boolean sat = false;
      if (!this.sumLexStepDone && !this.initDone) {
         this.initDone = true;
         this.setInitConstraints();
         this.createGlobalObj();
         this.optPb = new PseudoOptDecorator(this.solver);
      }

      if (!this.sumLexStepDone) {
         this.solver.setObjectiveFunction(this.sumObj);
         sat = this.launchSubOpt();
         if (!sat) {
            this.sumLexStepDone = true;
            this.initDone = false;
         }
      }

      if (!this.sumLexStepDone) {
         this.solver.setObjectiveFunction(this.lexObj);
         sat = this.launchSubOpt();
         if (!sat) {
            this.sumLexStepDone = true;
            this.initDone = false;
         }
      }

      if (this.sumLexStepDone) {
         boolean optFound = this.checkLeximinSumDecomposition();
         if (optFound) {
            return false;
         }

         if (!this.initDone) {
            System.out.println(this.solver.getLogPrefix() + "Sum-Lex optimization step done");
            this.optPb = new PseudoOptDecorator(this.solver);
            this.optPb.setObjectiveFunction(this.owaObj);
            this.initDone = true;
         }

         sat = this.launchSubOpt();
      }

      return sat;
   }

   private boolean checkLeximinSumDecomposition() {
      BigInteger weightsSum = BigInteger.ZERO;

      for (int i = 0; i < this.weights.length; i++) {
         weightsSum = weightsSum.add(this.weights[i]);
      }

      BigInteger minDiv = this.weights[this.weights.length - 1];

      for (int i = 0; i < this.weights.length - 1; i++) {
         minDiv = minDiv.min(this.weights[i + 1].multiply(weightsSum).divide(this.weights[i]));
      }

      BigInteger objBound = weightsSum.divide(this.weights[this.weights.length - 1]).multiply(this.getObjectiveValues()[this.weights.length - 1]);
      return objBound.compareTo(minDiv) <= 0;
   }

   private boolean launchSubOpt() throws TimeoutException {
      boolean sat = false;

      while (this.optPb.admitABetterSolution()) {
         sat = true;
         this.lastModel = this.solver.model();
         this.lastModelWithInternalVars = this.solver.modelWithInternalVariables();
         if (this.additionnalCstr != null) {
            System.out.println(this.solver.getLogPrefix() + "Current objectives values: " + Arrays.toString(this.getObjectiveValues()));
            System.out
               .println(
                  this.solver.getLogPrefix()
                     + "Current owa function value: owa="
                     + this.bestValue
                     + ", sum="
                     + this.sumObj.calculateDegree(this.solver)
                     + ", lex="
                     + this.lexObj.calculateDegree(this.solver)
               );
            this.solver.removeSubsumedConstr(this.additionnalCstr);
         }

         try {
            this.calculateObjective();
            this.optPb.discardCurrentSolution();
            this.additionnalCstr = this.solver.addAtMost(this.owaObj.getVars(), this.owaObj.getCoeffs(), this.bestValue.subtract(BigInteger.ONE));
         } catch (ContradictionException e) {
            break;
         }
      }

      return sat;
   }

   protected void setInitConstraints() {
      String owaWeightsProperty = System.getProperty("_owaWeights");
      if (owaWeightsProperty != null) {
         String[] weights = owaWeightsProperty.split(",");

         for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] = BigInteger.valueOf(Long.valueOf(weights[i]));
         }
      }

      if (this.solver.isVerbose()) {
         System.out.println(this.solver.getLogPrefix() + "OWA weights : " + Arrays.toString(this.weights));
      }

      BigInteger minObjValuesBound = this.minObjValuesBound();

      for (int i = 0; i < this.objs.size(); i++) {
         IntegerVariable boundVar = this.solver.newIntegerVar(minObjValuesBound);
         this.objBoundVariables.add(boundVar);
         this.atLeastFlags.add(new VecInt());

         for (int j = 0; j < this.objs.size(); j++) {
            this.addBoundConstraint(i, boundVar, j);
         }

         this.addFlagsCardinalityConstraint(i);
      }

      this.createSumAndLexObjs();
   }

   private void createSumAndLexObjs() {
      IVecInt auxObjsVars = new VecInt();
      IVec<BigInteger> sumObjCoeffs = new Vec<>();
      IVec<BigInteger> lexObjCoeffs = new Vec<>();
      BigInteger lexFactor = BigInteger.ONE;
      Iterator<IntegerVariable> intVarIt = this.objBoundVariables.iterator();

      while (intVarIt.hasNext()) {
         BigInteger sumFactor = BigInteger.ONE;
         IntegerVariable nextBoundVar = intVarIt.next();

         for (IteratorInt nextBoundVarLitsIt = nextBoundVar.getVars().iterator(); nextBoundVarLitsIt.hasNext(); lexFactor = lexFactor.shiftLeft(1)) {
            auxObjsVars.push(nextBoundVarLitsIt.next());
            sumObjCoeffs.push(sumFactor);
            sumFactor = sumFactor.shiftLeft(1);
            lexObjCoeffs.push(lexFactor);
         }
      }

      this.sumObj = new ObjectiveFunction(auxObjsVars, sumObjCoeffs);
      this.lexObj = new ObjectiveFunction(auxObjsVars, lexObjCoeffs);
   }

   private void addBoundConstraint(int boundVarIndex, IntegerVariable boundVar, int objIndex) {
      IVecInt literals = new VecInt();
      IVec<BigInteger> coeffs = new Vec<>();
      this.objs.get(objIndex).getVars().copyTo(literals);
      this.objs.get(objIndex).getCoeffs().copyTo(coeffs);
      int flagLit = this.solver.nextFreeVarId(true);
      this.atLeastFlags.get(boundVarIndex).push(flagLit);
      literals.push(flagLit);
      coeffs.push(this.minObjValuesBound().negate());

      try {
         this.solver
            .addAtMost(literals, coeffs, new Vec<IntegerVariable>().push(boundVar), new Vec<BigInteger>().push(BigInteger.ONE.negate()), BigInteger.ZERO);
      } catch (ContradictionException e) {
         throw new RuntimeException(e);
      }
   }

   private void addFlagsCardinalityConstraint(int card) {
      try {
         this.solver.addAtMost(this.atLeastFlags.get(card), card);
      } catch (ContradictionException e) {
         throw new RuntimeException(e);
      }
   }

   protected BigInteger minObjValuesBound() {
      BigInteger maxValue = BigInteger.ZERO;

      for (ObjectiveFunction nextObj : this.objs) {
         BigInteger maxObjValue = this.maxObjValue(nextObj);
         if (maxValue.compareTo(maxObjValue) < 0) {
            maxValue = maxObjValue;
         }
      }

      return maxValue.add(BigInteger.ONE);
   }

   private BigInteger maxObjValue(ObjectiveFunction obj) {
      IVec<BigInteger> objCoeffs = obj.getCoeffs();
      BigInteger coeffsSum = BigInteger.ZERO;
      Iterator<BigInteger> objCoeffsIt = objCoeffs.iterator();

      while (objCoeffsIt.hasNext()) {
         coeffsSum = coeffsSum.add(objCoeffsIt.next());
      }

      return coeffsSum;
   }

   private void createGlobalObj() {
      ObjectiveFunction oldObj = this.getObjectiveFunction();
      this.solver.setObjectiveFunction(new ObjectiveFunction(new VecInt(), new Vec<>()));

      for (int i = 0; i < this.objBoundVariables.size(); i++) {
         this.solver.addIntegerVariableToObjectiveFunction(this.objBoundVariables.get(i), this.weights[this.weights.length - i - 1]);
      }

      this.owaObj = this.solver.getObjectiveFunction();
      this.solver.setObjectiveFunction(oldObj);
   }

   @Override
   public boolean hasNoObjectiveFunction() {
      return false;
   }

   @Override
   public boolean nonOptimalMeansSatisfiable() {
      return true;
   }

   @Override
   public Number calculateObjective() {
      this.bestValue = BigInteger.ZERO;
      BigInteger[] values = this.getObjectiveValues();

      for (int i = 0; i < this.objs.size(); i++) {
         this.bestValue = this.bestValue.add(values[i].multiply(this.weights[i]));
      }

      return this.bestValue;
   }

   @Override
   public Number getObjectiveValue() {
      return this.bestValue;
   }

   @Override
   public void forceObjectiveValueTo(Number forcedValue) throws ContradictionException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void discard() throws ContradictionException {
      this.discardCurrentSolution();
   }

   @Override
   public void discardCurrentSolution() throws ContradictionException {
   }

   @Override
   public boolean isOptimal() {
      return false;
   }

   @Override
   public void setTimeoutForFindingBetterSolution(int seconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addObjectiveFunction(ObjectiveFunction obj) {
      this.objs.add(obj);
   }

   @Override
   public BigInteger[] getObjectiveValues() {
      BigInteger[] res = new BigInteger[this.objs.size()];

      for (int i = 0; i < this.objs.size(); i++) {
         res[i] = this.objs.get(i).calculateDegree(this.solver);
      }

      Arrays.sort(res);
      return res;
   }

   @Override
   public boolean model(int var) {
      return this.lastModelWithInternalVars[var - 1] > 0;
   }

   @Override
   public int[] model() {
      return this.lastModel;
   }

   @Override
   public int[] modelWithInternalVariables() {
      return this.lastModelWithInternalVars;
   }

   @Override
   public int[] primeImplicant() {
      return this.solver.primeImplicant();
   }

   @Override
   public boolean primeImplicant(int p) {
      return this.solver.primeImplicant(p);
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      return this.solver.isSatisfiable();
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean globalTimeout) throws TimeoutException {
      return this.solver.isSatisfiable(assumps, globalTimeout);
   }

   @Override
   public boolean isSatisfiable(boolean globalTimeout) throws TimeoutException {
      return this.solver.isSatisfiable(globalTimeout);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      return this.solver.isSatisfiable(assumps);
   }

   @Override
   public int[] findModel() throws TimeoutException {
      return this.solver.findModel();
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      return this.solver.findModel(assumps);
   }

   @Override
   public int nConstraints() {
      return this.solver.nConstraints();
   }

   @Override
   public int newVar(int howmany) {
      return this.solver.newVar(howmany);
   }

   @Override
   public int nVars() {
      return this.solver.nVars();
   }

   @Override
   public void printStat(PrintStream out, String prefix) {
      this.solver.printStat(out, prefix);
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
      this.solver.printStat(out, prefix);
   }

   @Override
   public void printStat(PrintWriter out) {
      this.solver.printStat(out);
   }

   @Override
   public Map<String, Number> getStat() {
      return this.solver.getStat();
   }

   @Override
   public String toString(String prefix) {
      return this.solver.toString(prefix);
   }

   @Override
   public void clearLearntClauses() {
      this.solver.clearLearntClauses();
   }

   @Override
   public void setDBSimplificationAllowed(boolean status) {
      this.solver.setDBSimplificationAllowed(status);
   }

   @Override
   public boolean isDBSimplificationAllowed() {
      return this.solver.isDBSimplificationAllowed();
   }

   @Override
   public <S extends ISolverService> void setSearchListener(SearchListener<S> sl) {
      this.solver.setSearchListener(sl);
   }

   @Override
   public void setUnitClauseProvider(UnitClauseProvider ucp) {
      this.solver.setUnitClauseProvider(ucp);
   }

   @Override
   public <S extends ISolverService> SearchListener<S> getSearchListener() {
      return this.solver.getSearchListener();
   }

   @Override
   public boolean isVerbose() {
      return this.solver.isVerbose();
   }

   @Override
   public void setVerbose(boolean value) {
      this.solver.setVerbose(value);
   }

   @Override
   public void setLogPrefix(String prefix) {
      this.solver.setLogPrefix(prefix);
   }

   @Override
   public String getLogPrefix() {
      return this.solver.getLogPrefix();
   }

   @Override
   public IVecInt unsatExplanation() {
      return this.solver.unsatExplanation();
   }

   @Override
   public int realNumberOfVariables() {
      return this.solver.realNumberOfVariables();
   }

   @Override
   public boolean isSolverKeptHot() {
      return this.solver.isSolverKeptHot();
   }

   @Override
   public void setKeepSolverHot(boolean keepHot) {
      this.solver.setKeepSolverHot(keepHot);
   }

   @Override
   public ISolver getSolvingEngine() {
      return this.solver.getSolvingEngine();
   }

   @Override
   public IntegerVariable newIntegerVar(BigInteger maxValue) {
      return this.solver.newIntegerVar(maxValue);
   }

   @Override
   public BigInteger getIntegerVarValue(IntegerVariable var) {
      return this.solver.getIntegerVarValue(var);
   }

   @Override
   public IConstr addAtLeast(IntegerVariable var, int degree) throws ContradictionException {
      return this.solver.addAtLeast(var, degree);
   }

   @Override
   public IConstr addAtLeast(
      IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger degree
   ) throws ContradictionException {
      return this.solver.addAtLeast(literals, coeffs, integerVars, integerVarsCoeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int degree) throws ContradictionException {
      return this.solver.addAtLeast(literals, coeffs, integerVars, integerVarsCoeffs, degree);
   }

   @Override
   public IConstr addAtMost(IntegerVariable var, int degree) throws ContradictionException {
      return this.solver.addAtMost(var, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger degree) throws ContradictionException {
      return this.solver.addAtMost(literals, coeffs, integerVars, integerVarsCoeffs, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int degree) throws ContradictionException {
      return this.solver.addAtMost(literals, coeffs, integerVars, integerVarsCoeffs, degree);
   }

   @Override
   public IConstr addExactly(IntegerVariable var, int degree) throws ContradictionException {
      return this.solver.addExactly(var, degree);
   }

   @Override
   public int newVar() {
      return this.solver.newVar();
   }

   @Override
   public IConstr addExactly(
      IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger weight
   ) throws ContradictionException {
      return this.solver.addExactly(literals, coeffs, integerVars, integerVarsCoeffs, weight);
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      return this.solver.addPseudoBoolean(lits, coeffs, moreThan, d);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int weight) throws ContradictionException {
      return this.solver.addExactly(literals, coeffs, integerVars, integerVarsCoeffs, weight);
   }

   @Override
   public IConstr addPseudoBoolean(
      IVecInt lits, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, boolean moreThan, BigInteger d
   ) throws ContradictionException {
      return this.solver.addPseudoBoolean(lits, coeffs, integerVars, integerVarsCoeffs, moreThan, d);
   }

   @Override
   public int nextFreeVarId(boolean reserve) {
      return this.solver.nextFreeVarId(reserve);
   }

   @Override
   public void addIntegerVariableToObjectiveFunction(IntegerVariable var, BigInteger weight) {
      this.solver.addIntegerVariableToObjectiveFunction(var, weight);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      return this.solver.addAtMost(literals, coeffs, degree);
   }

   @Override
   public void registerLiteral(int p) {
      this.solver.registerLiteral(p);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return this.solver.addAtMost(literals, coeffs, degree);
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      this.solver.setExpectedNumberOfClauses(nb);
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      return this.solver.addClause(literals);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      return this.solver.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addBlockingClause(IVecInt literals) throws ContradictionException {
      return this.solver.addBlockingClause(literals);
   }

   @Override
   public IConstr discardCurrentModel() throws ContradictionException {
      return this.solver.discardCurrentModel();
   }

   @Override
   public IVecInt createBlockingClauseForCurrentModel() {
      return this.solver.createBlockingClauseForCurrentModel();
   }

   @Override
   public boolean removeConstr(IConstr c) {
      return this.solver.removeConstr(c);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return this.solver.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public boolean removeSubsumedConstr(IConstr c) {
      return this.solver.removeSubsumedConstr(c);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      return this.solver.addExactly(literals, coeffs, weight);
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      this.solver.addAllClauses(clauses);
   }

   @Override
   public void printInfos(PrintWriter out, String prefix) {
      this.solver.printInfos(out, prefix);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      return this.solver.addExactly(literals, coeffs, weight);
   }

   @Override
   public void printInfos(PrintWriter out) {
      this.solver.printInfos(out);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      return this.solver.addAtMost(literals, degree);
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      this.solver.setObjectiveFunction(obj);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      return this.solver.addAtLeast(literals, degree);
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.solver.getObjectiveFunction();
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      return this.solver.addExactly(literals, n);
   }

   @Override
   public IConstr addConstr(Constr constr) {
      return this.solver.addConstr(constr);
   }

   @Override
   public void setTimeout(int t) {
      this.solver.setTimeout(t);
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
      this.solver.setTimeoutOnConflicts(count);
   }

   @Override
   public void setTimeoutMs(long t) {
      this.solver.setTimeoutMs(t);
   }

   @Override
   public int getTimeout() {
      return this.solver.getTimeout();
   }

   @Override
   public long getTimeoutMs() {
      return this.solver.getTimeoutMs();
   }

   @Override
   public void expireTimeout() {
      this.solver.expireTimeout();
   }

   @Override
   public void reset() {
      this.solver.reset();
   }

   @Override
   public IConstr addParity(IVecInt literals, boolean even) {
      return this.solver.addParity(literals, even);
   }

   @Override
   public AssignmentOrigin getOriginInModel(int p) {
      return this.solver.getOriginInModel(p);
   }

   @Override
   public void setUnitClauseConsumer(UnitClauseConsumer ucc) {
      this.solver.setUnitClauseConsumer(ucc);
   }
}
