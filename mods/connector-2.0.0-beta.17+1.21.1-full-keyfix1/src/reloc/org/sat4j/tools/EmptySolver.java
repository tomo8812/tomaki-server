package reloc.org.sat4j.tools;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.FakeConstr;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.SearchListener;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.specs.UnitClauseProvider;

public abstract class EmptySolver implements ISolver {
   private static final long serialVersionUID = 1L;
   private final IConstr FAKECONSTR = FakeConstr.instance();
   private int nbVars;
   private int nbClauses;

   @Override
   public int[] model() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int[] primeImplicant() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean primeImplicant(int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean globalTimeout) throws TimeoutException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isSatisfiable(boolean globalTimeout) throws TimeoutException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int[] findModel() throws TimeoutException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int nConstraints() {
      return this.nbClauses;
   }

   @Override
   public int newVar(int howmany) {
      this.nbVars = howmany;
      return howmany;
   }

   @Override
   public int nVars() {
      return this.nbVars;
   }

   @Override
   public void printInfos(PrintWriter out, String prefix) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void printInfos(PrintWriter out) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean model(int var) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int newVar() {
      this.nbVars++;
      return this.nbVars;
   }

   @Override
   public int nextFreeVarId(boolean reserve) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void registerLiteral(int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      this.nbClauses = nb;
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      return this.FAKECONSTR;
   }

   @Override
   public IConstr addBlockingClause(IVecInt literals) throws ContradictionException {
      return this.FAKECONSTR;
   }

   @Override
   public IConstr discardCurrentModel() throws ContradictionException {
      return this.FAKECONSTR;
   }

   @Override
   public IVecInt createBlockingClauseForCurrentModel() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean removeConstr(IConstr c) {
      return false;
   }

   @Override
   public boolean removeSubsumedConstr(IConstr c) {
      return false;
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      return this.FAKECONSTR;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      return this.FAKECONSTR;
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      return this.FAKECONSTR;
   }

   @Override
   public IConstr addConstr(Constr constr) {
      return this.FAKECONSTR;
   }

   @Override
   public void setTimeout(int t) {
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
   }

   @Override
   public void setTimeoutMs(long t) {
   }

   @Override
   public int getTimeout() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public long getTimeoutMs() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void expireTimeout() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void reset() {
   }

   @Override
   public void printStat(PrintStream out, String prefix) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void printStat(PrintWriter out) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public Map<String, Number> getStat() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public String toString(String prefix) {
      return "Empty Solver";
   }

   @Override
   public void clearLearntClauses() {
   }

   @Override
   public void setDBSimplificationAllowed(boolean status) {
   }

   @Override
   public boolean isDBSimplificationAllowed() {
      return false;
   }

   @Override
   public <S extends ISolverService> void setSearchListener(SearchListener<S> sl) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setUnitClauseProvider(UnitClauseProvider ucp) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public <S extends ISolverService> SearchListener<S> getSearchListener() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isVerbose() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setVerbose(boolean value) {
   }

   @Override
   public void setLogPrefix(String prefix) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public String getLogPrefix() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IVecInt unsatExplanation() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int[] modelWithInternalVariables() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int realNumberOfVariables() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isSolverKeptHot() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setKeepSolverHot(boolean keepHot) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public ISolver getSolvingEngine() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }
}
