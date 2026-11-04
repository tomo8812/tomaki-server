package reloc.org.sat4j.specs;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;
import reloc.org.sat4j.annotations.Feature;

@Feature("solver")
public interface ISolver extends IProblem, Serializable {
   @Deprecated
   int newVar();

   int nextFreeVarId(boolean var1);

   void registerLiteral(int var1);

   void setExpectedNumberOfClauses(int var1);

   IConstr addClause(IVecInt var1) throws ContradictionException;

   IConstr addBlockingClause(IVecInt var1) throws ContradictionException;

   IConstr discardCurrentModel() throws ContradictionException;

   IVecInt createBlockingClauseForCurrentModel();

   boolean removeConstr(IConstr var1);

   boolean removeSubsumedConstr(IConstr var1);

   void addAllClauses(IVec<IVecInt> var1) throws ContradictionException;

   IConstr addAtMost(IVecInt var1, int var2) throws ContradictionException;

   IConstr addAtLeast(IVecInt var1, int var2) throws ContradictionException;

   IConstr addExactly(IVecInt var1, int var2) throws ContradictionException;

   IConstr addParity(IVecInt var1, boolean var2);

   IConstr addConstr(Constr var1);

   void setTimeout(int var1);

   void setTimeoutOnConflicts(int var1);

   void setTimeoutMs(long var1);

   int getTimeout();

   long getTimeoutMs();

   void expireTimeout();

   void reset();

   @Deprecated
   void printStat(PrintStream var1, String var2);

   @Deprecated
   void printStat(PrintWriter var1, String var2);

   void printStat(PrintWriter var1);

   Map<String, Number> getStat();

   String toString(String var1);

   void clearLearntClauses();

   void setDBSimplificationAllowed(boolean var1);

   boolean isDBSimplificationAllowed();

   <S extends ISolverService> void setSearchListener(SearchListener<S> var1);

   void setUnitClauseProvider(UnitClauseProvider var1);

   void setUnitClauseConsumer(UnitClauseConsumer var1);

   <S extends ISolverService> SearchListener<S> getSearchListener();

   boolean isVerbose();

   void setVerbose(boolean var1);

   void setLogPrefix(String var1);

   String getLogPrefix();

   IVecInt unsatExplanation();

   int[] modelWithInternalVariables();

   int realNumberOfVariables();

   boolean isSolverKeptHot();

   void setKeepSolverHot(boolean var1);

   ISolver getSolvingEngine();

   AssignmentOrigin getOriginInModel(int var1);
}
