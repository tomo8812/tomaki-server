package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import java.util.Iterator;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public class SteppedTimeoutLexicoDecoratorPB extends LexicoDecoratorPB {
   private static final long serialVersionUID = 1L;

   public SteppedTimeoutLexicoDecoratorPB(IPBSolver solver) {
      super(solver);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      this.decorated().setObjectiveFunction(this.objs.get(this.currentCriterion));
      this.isSolutionOptimal = false;

      try {
         if (!this.decorated().isSatisfiable(assumps, true)) {
            this.decorated().expireTimeout();
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
      } catch (TimeoutException te) {
         if (this.currentCriterion == this.numberOfCriteria() - 1) {
            throw te;
         }

         this.mergeCurrentandNextCriteria();
         if (this.prevConstr != null) {
            super.removeConstr(this.prevConstr);
            this.prevConstr = null;
         }

         try {
            this.fixCriterionValue();
         } catch (ContradictionException ce) {
            throw new IllegalStateException(ce);
         }

         if (this.isVerbose()) {
            System.out.println(this.getLogPrefix() + "Partial timeout criterion number " + (this.currentCriterion + 1));
         }

         this.currentCriterion++;
         this.calculateObjective();
         this.decorated().expireTimeout();
         return true;
      }
   }

   private void mergeCurrentandNextCriteria() {
      ObjectiveFunction currentObj = this.objs.get(this.currentCriterion);
      int currentObjSize = currentObj.getVars().size();
      ObjectiveFunction nextObj = this.objs.get(this.currentCriterion + 1);
      int nextObjSize = nextObj.getVars().size();
      IVecInt newLits = new VecInt(currentObjSize + nextObjSize);
      currentObj.getVars().copyTo(newLits);
      nextObj.getVars().copyTo(newLits);
      IVec<BigInteger> newCoeffs = new Vec<>(currentObjSize + nextObjSize);
      BigInteger coeffFactor = BigInteger.valueOf(nextObjSize).add(BigInteger.ONE);
      Iterator<BigInteger> it = currentObj.getCoeffs().iterator();

      while (it.hasNext()) {
         newCoeffs.push(it.next().multiply(coeffFactor));
      }

      nextObj.getCoeffs().copyTo(newCoeffs);
      this.objs.set(this.currentCriterion + 1, new ObjectiveFunction(newLits, newCoeffs));
   }
}
