package reloc.org.sat4j.tools;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.SearchListener;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.specs.UnitClauseProvider;

public abstract class AbstractOutputSolver implements ISolver {
   protected int nbvars;
   protected int nbclauses;
   protected boolean fixedNbClauses = false;
   protected boolean firstConstr = true;
   private boolean verbose;
   private static final long serialVersionUID = 1L;

   @Override
   public boolean removeConstr(IConstr c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setTimeout(int t) {
   }

   @Override
   public void setTimeoutMs(long t) {
   }

   @Override
   public int getTimeout() {
      return 0;
   }

   @Override
   public long getTimeoutMs() {
      return 0L;
   }

   @Override
   public void expireTimeout() {
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean global) throws TimeoutException {
      throw new TimeoutException("There is no real solver behind!");
   }

   @Override
   public boolean isSatisfiable(boolean global) throws TimeoutException {
      throw new TimeoutException("There is no real solver behind!");
   }

   @Override
   public void printInfos(PrintWriter output, String prefix) {
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
   }

   @Override
   public boolean isDBSimplificationAllowed() {
      return false;
   }

   @Override
   public void setDBSimplificationAllowed(boolean status) {
   }

   @Override
   public void printStat(PrintStream output, String prefix) {
   }

   @Override
   public void printStat(PrintWriter output, String prefix) {
   }

   @Override
   public Map<String, Number> getStat() {
      return null;
   }

   @Override
   public void clearLearntClauses() {
   }

   @Override
   public int[] model() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean model(int var) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      throw new TimeoutException("There is no real solver behind!");
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      throw new TimeoutException("There is no real solver behind!");
   }

   @Override
   public int[] findModel() throws TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeSubsumedConstr(IConstr c) {
      return false;
   }

   @Override
   public IConstr addBlockingClause(IVecInt literals) throws ContradictionException {
      throw new UnsupportedOperationException();
   }

   @Override
   public IConstr discardCurrentModel() throws ContradictionException {
      throw new UnsupportedOperationException();
   }

   @Override
   public IVecInt createBlockingClauseForCurrentModel() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <S extends ISolverService> SearchListener<S> getSearchListener() {
      return null;
   }

   @Override
   public <S extends ISolverService> void setSearchListener(SearchListener<S> sl) {
   }

   @Override
   public boolean isVerbose() {
      return this.verbose;
   }

   @Override
   public void setVerbose(boolean value) {
      this.verbose = value;
   }

   @Override
   public void setLogPrefix(String prefix) {
   }

   @Override
   public String getLogPrefix() {
      return "";
   }

   @Override
   public IVecInt unsatExplanation() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int[] primeImplicant() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int nConstraints() {
      return 0;
   }

   @Override
   public int newVar(int howmany) {
      return 0;
   }

   @Override
   public int nVars() {
      return 0;
   }

   @Override
   public boolean isSolverKeptHot() {
      return false;
   }

   @Override
   public void setKeepSolverHot(boolean value) {
   }

   @Override
   public ISolver getSolvingEngine() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setUnitClauseProvider(UnitClauseProvider upl) {
      throw new UnsupportedOperationException();
   }

   @Override
   public IConstr addConstr(Constr constr) {
      throw new UnsupportedOperationException();
   }
}
