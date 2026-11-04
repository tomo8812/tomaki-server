package reloc.org.sat4j.opt;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public final class MaxSatDecorator extends AbstractSelectorVariablesDecorator {
   private static final long serialVersionUID = 1L;
   private final boolean equivalence;
   private final IVecInt lits = new VecInt();
   private int counter;
   private IConstr prevConstr;

   public MaxSatDecorator(ISolver solver) {
      this(solver, false);
   }

   public MaxSatDecorator(ISolver solver, boolean equivalence) {
      super(solver);
      this.equivalence = equivalence;
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      super.setExpectedNumberOfClauses(nb);
      this.lits.ensure(nb);
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      int newvar = this.nextFreeVarId(true);
      this.lits.push(newvar);
      literals.push(newvar);
      if (!this.equivalence) {
         return super.addClause(literals);
      }

      ConstrGroup constrs = new ConstrGroup();
      constrs.add(super.addClause(literals));
      IVecInt clause = new VecInt(2);
      clause.push(-newvar);

      for (int i = 0; i < literals.size() - 1; i++) {
         clause.push(-literals.get(i));
         constrs.add(super.addClause(clause));
      }

      clause.pop();
      return constrs;
   }

   @Override
   public void reset() {
      this.lits.clear();
      super.reset();
      this.prevConstr = null;
   }

   @Override
   public boolean hasNoObjectiveFunction() {
      return false;
   }

   @Override
   public boolean nonOptimalMeansSatisfiable() {
      return false;
   }

   @Override
   public Number calculateObjective() {
      this.calculateObjectiveValue();
      return this.counter;
   }

   @Override
   public void discardCurrentSolution() throws ContradictionException {
      if (this.prevConstr != null) {
         super.removeSubsumedConstr(this.prevConstr);
      }

      try {
         this.prevConstr = super.addAtMost(this.lits, this.counter - 1);
      } catch (ContradictionException ce) {
         this.setSolutionOptimal(true);
         throw ce;
      }
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      boolean result = super.admitABetterSolution(assumps);
      if (!result && this.prevConstr != null) {
         super.removeConstr(this.prevConstr);
         this.prevConstr = null;
      }

      return result;
   }

   @Override
   public void discard() throws ContradictionException {
      this.discardCurrentSolution();
   }

   @Override
   public Number getObjectiveValue() {
      return this.counter;
   }

   @Override
   void calculateObjectiveValue() {
      this.counter = 0;

      for (int q : this.getPrevfullmodel()) {
         if (q > this.nVars()) {
            this.counter++;
         }
      }
   }

   @Override
   public void forceObjectiveValueTo(Number forcedValue) throws ContradictionException {
      super.addAtMost(this.lits, forcedValue.intValue());
   }

   @Override
   public void setTimeoutForFindingBetterSolution(int seconds) {
      throw new UnsupportedOperationException("No implemented yet");
   }
}
