package reloc.org.sat4j.specs;

public interface ISolverService {
   void stop();

   void backtrack(int[] var1);

   IConstr addClauseOnTheFly(int[] var1);

   IConstr addAtMostOnTheFly(int[] var1, int var2);

   IVecInt createBlockingClauseForCurrentModel();

   Lbool truthValue(int var1);

   int currentDecisionLevel();

   int[] getLiteralsPropagatedAt(int var1);

   void suggestNextLiteralToBranchOn(int var1);

   double[] getVariableHeuristics();

   IVec<? extends IConstr> getLearnedConstraints();

   int nVars();

   boolean removeSubsumedConstr(IConstr var1);

   String getLogPrefix();
}
