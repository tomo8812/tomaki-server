package reloc.org.sat4j.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IOptimizationProblem;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public class ConstraintRelaxingPseudoOptDecorator extends PBSolverDecorator implements IOptimizationProblem {
   private static final long serialVersionUID = 1L;
   private IConstr previousPBConstr;
   private IConstr addedConstr = null;
   private Number objectiveValue;
   private boolean optimumFound = false;

   public ConstraintRelaxingPseudoOptDecorator(IPBSolver solver) {
      super(solver);
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      return this.isSatisfiable(VecInt.EMPTY);
   }

   @Override
   public boolean isSatisfiable(boolean global) throws TimeoutException {
      return this.isSatisfiable(VecInt.EMPTY, global);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean global) throws TimeoutException {
      boolean result = super.isSatisfiable(assumps, true);
      if (result) {
         this.calculateObjective();
      } else if (this.previousPBConstr != null) {
         this.decorated().removeConstr(this.previousPBConstr);
         this.previousPBConstr = null;
      }

      return result;
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      return this.isSatisfiable(assumps, true);
   }

   @Override
   public boolean admitABetterSolution() throws TimeoutException {
      return this.admitABetterSolution(VecInt.EMPTY);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      if (this.optimumFound) {
         return false;
      }

      int maxValue = this.getObjectiveFunction().minValue().intValue();

      while (true) {
         while (true) {
            if (this.addedConstr != null) {
               this.decorated().removeConstr(this.addedConstr);
            }

            try {
               this.forceObjectiveValueTo(maxValue++);
               break;
            } catch (ContradictionException e) {
               if (this.isVerbose()) {
                  System.out.println(this.decorated().getLogPrefix() + "no solution for objective value " + (maxValue - 1));
               }
            }
         }

         boolean isSatisfiable = super.isSatisfiable(assumps, true);
         if (isSatisfiable) {
            this.optimumFound = true;
            if (this.getObjectiveFunction() != null) {
               this.calculateObjective();
            }

            this.decorated().removeConstr(this.addedConstr);
            return true;
         }

         if (this.isVerbose()) {
            System.out.println(this.decorated().getLogPrefix() + "no solution for objective value " + (maxValue - 1));
         }
      }
   }

   @Override
   public boolean hasNoObjectiveFunction() {
      return this.getObjectiveFunction() == null;
   }

   @Override
   public boolean nonOptimalMeansSatisfiable() {
      return false;
   }

   @Deprecated
   @Override
   public Number calculateObjective() {
      if (this.getObjectiveFunction() == null) {
         throw new UnsupportedOperationException("The problem does not contain an objective function");
      }

      this.objectiveValue = this.getObjectiveFunction().calculateDegree(this);
      return this.objectiveValue;
   }

   @Override
   public Number getObjectiveValue() {
      return this.objectiveValue;
   }

   @Override
   public void forceObjectiveValueTo(Number forcedValue) throws ContradictionException {
      this.addedConstr = super.addPseudoBoolean(
         this.getObjectiveFunction().getVars(), this.getObjectiveFunction().getCoeffs(), false, BigInteger.valueOf(forcedValue.longValue())
      );
   }

   @Deprecated
   @Override
   public void discard() {
      this.discardCurrentSolution();
   }

   @Override
   public void discardCurrentSolution() {
   }

   @Override
   public boolean isOptimal() {
      return this.optimumFound;
   }

   @Override
   public String toString(String prefix) {
      return prefix + "Pseudo Boolean Optimization by lower bound\n" + super.toString(prefix);
   }

   @Override
   public void setTimeoutForFindingBetterSolution(int seconds) {
      throw new UnsupportedOperationException("Does not make sense here");
   }
}
