package reloc.org.sat4j.opt;

import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.SolverDecorator;

public abstract class AbstractSelectorVariablesDecorator extends SolverDecorator<ISolver> implements IOptimizationProblem {
   private static final long serialVersionUID = 1L;
   private int nbexpectedclauses;
   private int[] prevfullmodel;
   private int[] prevmodel;
   private boolean[] prevboolmodel;
   private boolean isSolutionOptimal;
   private IVecInt prevBlockingClause;

   public AbstractSelectorVariablesDecorator(ISolver solver) {
      super(solver);
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      this.nbexpectedclauses = nb;
   }

   public int getExpectedNumberOfClauses() {
      return this.nbexpectedclauses;
   }

   @Override
   public boolean admitABetterSolution() throws TimeoutException {
      return this.admitABetterSolution(VecInt.EMPTY);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      this.isSolutionOptimal = false;
      boolean result = super.isSatisfiable(assumps, true);
      if (result) {
         this.prevboolmodel = new boolean[this.nVars()];

         for (int i = 0; i < this.nVars(); i++) {
            this.prevboolmodel[i] = this.decorated().model(i + 1);
         }

         this.prevfullmodel = super.modelWithInternalVariables();
         this.prevmodel = super.model();
         this.prevBlockingClause = super.createBlockingClauseForCurrentModel();
         this.calculateObjectiveValue();
      } else {
         this.isSolutionOptimal = true;
      }

      return result;
   }

   abstract void calculateObjectiveValue();

   @Override
   public int[] model() {
      return this.prevmodel;
   }

   @Override
   public boolean model(int var) {
      return this.prevboolmodel[var - 1];
   }

   @Override
   public IConstr discardCurrentModel() throws ContradictionException {
      return this.addBlockingClause(this.prevBlockingClause);
   }

   @Override
   public IVecInt createBlockingClauseForCurrentModel() {
      return this.prevBlockingClause;
   }

   @Override
   public boolean isOptimal() {
      return this.isSolutionOptimal;
   }

   public int getNbexpectedclauses() {
      return this.nbexpectedclauses;
   }

   public void setNbexpectedclauses(int nbexpectedclauses) {
      this.nbexpectedclauses = nbexpectedclauses;
   }

   public int[] getPrevfullmodel() {
      return this.prevfullmodel;
   }

   public void setPrevfullmodel(int[] prevfullmodel) {
      this.prevfullmodel = (int[])prevfullmodel.clone();
   }

   public int[] getPrevmodel() {
      return this.prevmodel;
   }

   public void setPrevmodel(int[] prevmodel) {
      this.prevmodel = (int[])prevmodel.clone();
   }

   public boolean[] getPrevboolmodel() {
      return this.prevboolmodel;
   }

   public void setPrevboolmodel(boolean[] prevboolmodel) {
      this.prevboolmodel = (boolean[])prevboolmodel.clone();
   }

   public boolean isSolutionOptimal() {
      return this.isSolutionOptimal;
   }

   public void setSolutionOptimal(boolean isSolutionOptimal) {
      this.isSolutionOptimal = isSolutionOptimal;
   }
}
