package reloc.org.sat4j.pb.multiobjective;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IIntegerPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.PseudoOptDecorator;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public abstract class AbstractLinMultiObjOptimizer extends PseudoOptDecorator implements IMultiObjOptimizationProblem {
   private static final long serialVersionUID = 1L;
   protected final transient List<ObjectiveFunction> objs = new ArrayList<>();
   protected boolean initConstraintsSet = false;
   protected final IIntegerPBSolver integerSolver;

   public AbstractLinMultiObjOptimizer(IIntegerPBSolver solver) {
      super(solver);
      this.integerSolver = solver;
   }

   @Override
   public void addObjectiveFunction(ObjectiveFunction obj) {
      this.objs.add(obj);
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction objf) {
      this.addObjectiveFunction(objf);
   }

   @Override
   public BigInteger[] getObjectiveValues() {
      BigInteger[] objValues = new BigInteger[this.objs.size()];

      for (int i = 0; i < this.objs.size(); i++) {
         objValues[i] = this.objs.get(i).calculateDegree(this);
      }

      Arrays.sort(objValues);
      return objValues;
   }

   @Override
   public boolean admitABetterSolution() throws TimeoutException {
      return this.admitABetterSolution(VecInt.EMPTY);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      if (!this.initConstraintsSet) {
         this.setInitConstraints();
         this.setGlobalObj();
         this.initConstraintsSet = true;
      }

      boolean res = super.admitABetterSolution(assumps);
      if (res && this.isVerbose()) {
         System.out.println(this.getLogPrefix() + "Current objective functions values: " + Arrays.toString(this.getObjectiveValues()));
      }

      return res;
   }

   @Override
   public boolean hasNoObjectiveFunction() {
      return false;
   }

   protected abstract void setInitConstraints();

   protected abstract void setGlobalObj();
}
