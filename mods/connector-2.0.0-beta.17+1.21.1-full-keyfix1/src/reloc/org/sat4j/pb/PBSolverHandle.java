package reloc.org.sat4j.pb;

import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public class PBSolverHandle extends PBSolverDecorator implements IOptimizationProblem {
   private static final long serialVersionUID = 1L;

   public PBSolverHandle(IPBSolver solver) {
      super(solver);
      if (!(solver instanceof IOptimizationProblem)) {
         throw new IllegalArgumentException("We need also optimization problem here!");
      }
   }

   public void changeDecorated(IPBSolver solver) {
      this.setDecorated(solver);
   }

   @Override
   public boolean admitABetterSolution() throws TimeoutException {
      return ((IOptimizationProblem)this.decorated()).admitABetterSolution();
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      return ((IOptimizationProblem)this.decorated()).admitABetterSolution(assumps);
   }

   @Override
   public boolean hasNoObjectiveFunction() {
      return ((IOptimizationProblem)this.decorated()).hasNoObjectiveFunction();
   }

   @Override
   public boolean nonOptimalMeansSatisfiable() {
      return ((IOptimizationProblem)this.decorated()).nonOptimalMeansSatisfiable();
   }

   @Override
   public Number calculateObjective() {
      return ((IOptimizationProblem)this.decorated()).calculateObjective();
   }

   @Override
   public Number getObjectiveValue() {
      return ((IOptimizationProblem)this.decorated()).getObjectiveValue();
   }

   @Override
   public void forceObjectiveValueTo(Number forcedValue) throws ContradictionException {
      ((IOptimizationProblem)this.decorated()).forceObjectiveValueTo(forcedValue);
   }

   @Override
   public void discard() throws ContradictionException {
      ((IOptimizationProblem)this.decorated()).discard();
   }

   @Override
   public void discardCurrentSolution() throws ContradictionException {
      ((IOptimizationProblem)this.decorated()).discardCurrentSolution();
   }

   @Override
   public boolean isOptimal() {
      return ((IOptimizationProblem)this.decorated()).isOptimal();
   }

   @Override
   public void setTimeoutForFindingBetterSolution(int seconds) {
      ((IOptimizationProblem)this.decorated()).setTimeoutForFindingBetterSolution(seconds);
   }
}
