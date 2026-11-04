package reloc.org.sat4j.opt;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.SolverDecorator;

@Feature("solver")
public final class MinOneDecorator extends SolverDecorator<ISolver> implements IOptimizationProblem {
   private static final long serialVersionUID = 1L;
   private int[] prevmodel;
   private int[] prevmodelWithInternalVariables;
   private boolean isSolutionOptimal;
   private int counter;
   private final IVecInt literals = new VecInt();
   private IConstr previousConstr;

   public MinOneDecorator(ISolver solver) {
      super(solver);
   }

   @Override
   public boolean admitABetterSolution() throws TimeoutException {
      return this.admitABetterSolution(VecInt.EMPTY);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      this.isSolutionOptimal = false;
      boolean result = this.isSatisfiable(assumps, true);
      if (result) {
         this.prevmodel = super.model();
         this.prevmodelWithInternalVariables = super.modelWithInternalVariables();
         this.calculateObjectiveValue();
      } else {
         this.isSolutionOptimal = true;
      }

      return result;
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
      this.calculateObjectiveValue();
      return this.counter;
   }

   private void calculateObjectiveValue() {
      this.counter = 0;

      for (int p : this.prevmodel) {
         if (p > 0) {
            this.counter++;
         }
      }
   }

   @Override
   public void discardCurrentSolution() throws ContradictionException {
      if (this.literals.isEmpty()) {
         for (int i = 1; i <= this.nVars(); i++) {
            this.literals.push(i);
         }
      }

      if (this.previousConstr != null) {
         super.removeConstr(this.previousConstr);
      }

      this.previousConstr = this.addAtMost(this.literals, this.counter - 1);
   }

   @Override
   public int[] model() {
      return this.prevmodel;
   }

   @Override
   public int[] modelWithInternalVariables() {
      return this.prevmodelWithInternalVariables;
   }

   @Override
   public void reset() {
      this.literals.clear();
      this.previousConstr = null;
      super.reset();
   }

   @Override
   public Number getObjectiveValue() {
      return this.counter;
   }

   @Override
   public void discard() throws ContradictionException {
      this.discardCurrentSolution();
   }

   @Override
   public void forceObjectiveValueTo(Number forcedValue) throws ContradictionException {
      try {
         this.addAtMost(this.literals, forcedValue.intValue());
      } catch (ContradictionException ce) {
         this.isSolutionOptimal = true;
         throw ce;
      }
   }

   @Override
   public boolean isOptimal() {
      return this.isSolutionOptimal;
   }

   @Override
   public void setTimeoutForFindingBetterSolution(int seconds) {
      throw new UnsupportedOperationException("No implemented yet");
   }
}
