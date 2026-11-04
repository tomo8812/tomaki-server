package reloc.org.sat4j.specs;

public interface IOptimizationProblem extends IProblem {
   boolean admitABetterSolution() throws TimeoutException;

   boolean admitABetterSolution(IVecInt var1) throws TimeoutException;

   boolean hasNoObjectiveFunction();

   boolean nonOptimalMeansSatisfiable();

   @Deprecated
   Number calculateObjective();

   Number getObjectiveValue();

   void forceObjectiveValueTo(Number var1) throws ContradictionException;

   @Deprecated
   void discard() throws ContradictionException;

   void discardCurrentSolution() throws ContradictionException;

   boolean isOptimal();

   void setTimeoutForFindingBetterSolution(int var1);
}
