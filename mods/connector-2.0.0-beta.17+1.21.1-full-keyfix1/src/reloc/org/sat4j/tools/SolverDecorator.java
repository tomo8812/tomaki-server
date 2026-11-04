package reloc.org.sat4j.tools;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import reloc.org.sat4j.specs.AssignmentOrigin;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.SearchListener;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.specs.UnitClauseConsumer;
import reloc.org.sat4j.specs.UnitClauseProvider;

public abstract class SolverDecorator<T extends ISolver> implements ISolver {
   private static final long serialVersionUID = 1L;
   private T solver;

   @Override
   public boolean isDBSimplificationAllowed() {
      return this.solver.isDBSimplificationAllowed();
   }

   @Override
   public void setDBSimplificationAllowed(boolean status) {
      this.solver.setDBSimplificationAllowed(status);
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
      this.solver.setTimeoutOnConflicts(count);
   }

   @Override
   public void printInfos(PrintWriter out, String prefix) {
      this.solver.printInfos(out, prefix);
   }

   @Override
   public void printInfos(PrintWriter out) {
      this.solver.printInfos(out);
   }

   @Override
   public boolean isSatisfiable(boolean global) throws TimeoutException {
      return this.solver.isSatisfiable(global);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean global) throws TimeoutException {
      return this.solver.isSatisfiable(assumps, global);
   }

   @Override
   public void clearLearntClauses() {
      this.solver.clearLearntClauses();
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
   public boolean model(int var) {
      return this.solver.model(var);
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      this.solver.setExpectedNumberOfClauses(nb);
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
   public String toString(String prefix) {
      return this.solver.toString(prefix);
   }

   @Override
   public String toString() {
      return this.solver.toString();
   }

   @Deprecated
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

   public SolverDecorator(T solver) {
      this.solver = solver;
   }

   @Deprecated
   @Override
   public int newVar() {
      return this.solver.newVar();
   }

   @Override
   public int newVar(int howmany) {
      return this.solver.newVar(howmany);
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      return this.solver.addClause(literals);
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      this.solver.addAllClauses(clauses);
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
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      return this.solver.addAtMost(literals, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      return this.solver.addAtLeast(literals, degree);
   }

   @Override
   public int[] model() {
      return this.solver.model();
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      return this.solver.isSatisfiable();
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      return this.solver.isSatisfiable(assumps);
   }

   @Override
   public void setTimeout(int t) {
      this.solver.setTimeout(t);
   }

   @Override
   public void setTimeoutMs(long t) {
      this.solver.setTimeoutMs(t);
   }

   @Override
   public void expireTimeout() {
      this.solver.expireTimeout();
   }

   @Override
   public int nConstraints() {
      return this.solver.nConstraints();
   }

   @Override
   public int nVars() {
      return this.solver.nVars();
   }

   @Override
   public void reset() {
      this.solver.reset();
   }

   public T decorated() {
      return this.solver;
   }

   public T clearDecorated() {
      T decorated = this.solver;
      this.solver = null;
      return decorated;
   }

   protected void setDecorated(T solver) {
      this.solver = solver;
   }

   @Override
   public boolean removeConstr(IConstr c) {
      return this.solver.removeConstr(c);
   }

   @Override
   public Map<String, Number> getStat() {
      return this.solver.getStat();
   }

   @Override
   public <S extends ISolverService> void setSearchListener(SearchListener<S> sl) {
      this.solver.setSearchListener(sl);
   }

   @Override
   public int nextFreeVarId(boolean reserve) {
      return this.solver.nextFreeVarId(reserve);
   }

   @Override
   public boolean removeSubsumedConstr(IConstr c) {
      return this.solver.removeSubsumedConstr(c);
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
   public int[] primeImplicant() {
      return this.solver.primeImplicant();
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      return this.solver.addExactly(literals, n);
   }

   @Override
   public int[] modelWithInternalVariables() {
      return this.solver.modelWithInternalVariables();
   }

   @Override
   public int realNumberOfVariables() {
      return this.solver.realNumberOfVariables();
   }

   @Override
   public void registerLiteral(int p) {
      this.solver.registerLiteral(p);
   }

   @Override
   public boolean isSolverKeptHot() {
      return this.solver.isSolverKeptHot();
   }

   @Override
   public void setKeepSolverHot(boolean value) {
      this.solver.setKeepSolverHot(value);
   }

   @Override
   public boolean primeImplicant(int p) {
      return this.solver.primeImplicant(p);
   }

   @Override
   public ISolver getSolvingEngine() {
      return this.solver.getSolvingEngine();
   }

   @Override
   public void setUnitClauseProvider(UnitClauseProvider ucp) {
      this.solver.setUnitClauseProvider(ucp);
   }

   @Override
   public IConstr addConstr(Constr constr) {
      return this.solver.addConstr(constr);
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
