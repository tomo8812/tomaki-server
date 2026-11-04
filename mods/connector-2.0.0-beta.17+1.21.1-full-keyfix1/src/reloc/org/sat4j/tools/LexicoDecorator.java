package reloc.org.sat4j.tools;

import java.util.ArrayList;
import java.util.List;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public class LexicoDecorator<T extends ISolver> extends SolverDecorator<T> implements IOptimizationProblem {
   protected final List<IVecInt> criteria = new ArrayList<>();
   protected int currentCriterion = 0;
   protected IConstr prevConstr;
   private Number currentValue = -1;
   protected int[] prevfullmodel;
   protected int[] prevmodelwithinternalvars;
   protected boolean[] prevboolmodel;
   protected boolean isSolutionOptimal;
   private static final long serialVersionUID = 1L;

   public LexicoDecorator(T solver) {
      super(solver);
   }

   public void addCriterion(IVecInt literals) {
      IVecInt copy = new VecInt(literals.size());
      literals.copyTo(copy);
      this.criteria.add(copy);
   }

   @Override
   public boolean admitABetterSolution() throws TimeoutException {
      return this.admitABetterSolution(VecInt.EMPTY);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      this.isSolutionOptimal = false;
      if (!this.decorated().isSatisfiable(assumps, true)) {
         return this.manageUnsatCase();
      }

      this.prevboolmodel = new boolean[this.nVars()];

      for (int i = 0; i < this.nVars(); i++) {
         this.prevboolmodel[i] = this.decorated().model(i + 1);
      }

      this.prevfullmodel = this.decorated().model();
      this.prevmodelwithinternalvars = this.decorated().modelWithInternalVariables();
      this.calculateObjective();
      return true;
   }

   protected boolean manageUnsatCase() {
      if (this.prevfullmodel == null) {
         return false;
      }

      if (this.currentCriterion < this.numberOfCriteria() - 1) {
         if (this.prevConstr != null) {
            super.removeConstr(this.prevConstr);
            this.prevConstr = null;
         }

         try {
            this.fixCriterionValue();
         } catch (ContradictionException e) {
            throw new IllegalStateException(e);
         }

         if (this.isVerbose()) {
            System.out.println(this.getLogPrefix() + "Found optimal criterion number " + (this.currentCriterion + 1));
         }

         this.currentCriterion++;
         this.calculateObjective();
         return true;
      } else {
         if (this.isVerbose()) {
            System.out.println(this.getLogPrefix() + "Found optimal solution for the last criterion ");
         }

         this.isSolutionOptimal = true;
         if (this.prevConstr != null) {
            super.removeConstr(this.prevConstr);
            this.prevConstr = null;
         }

         return false;
      }
   }

   public int numberOfCriteria() {
      return this.criteria.size();
   }

   protected void fixCriterionValue() throws ContradictionException {
      super.addExactly(this.criteria.get(this.currentCriterion), this.currentValue.intValue());
   }

   @Override
   public int[] model() {
      return this.prevfullmodel;
   }

   @Override
   public boolean model(int var) {
      return this.prevboolmodel[var - 1];
   }

   @Override
   public int[] modelWithInternalVariables() {
      return this.prevmodelwithinternalvars;
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
      this.currentValue = this.evaluate();
      return this.currentValue;
   }

   @Override
   public Number getObjectiveValue() {
      return this.currentValue;
   }

   public Number getObjectiveValue(int criterion) {
      return this.evaluate(criterion);
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
      if (this.prevConstr != null) {
         super.removeSubsumedConstr(this.prevConstr);
      }

      try {
         this.prevConstr = this.discardSolutionsForOptimizing();
      } catch (ContradictionException c) {
         this.prevConstr = null;
         if (!this.manageUnsatCase()) {
            throw c;
         }
      }
   }

   protected IConstr discardSolutionsForOptimizing() throws ContradictionException {
      return super.addAtMost(this.criteria.get(this.currentCriterion), this.currentValue.intValue() - 1);
   }

   protected Number evaluate() {
      return this.evaluate(this.currentCriterion);
   }

   protected Number evaluate(int criterion) {
      int value = 0;
      IteratorInt it = this.criteria.get(this.currentCriterion).iterator();

      while (it.hasNext()) {
         int lit = it.next();
         if (lit > 0 && this.prevboolmodel[lit - 1] || lit < 0 && !this.prevboolmodel[-lit - 1]) {
            value++;
         }
      }

      return value;
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
