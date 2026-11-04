package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import java.util.Collection;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.OptToPBSATAdapter;
import reloc.org.sat4j.pb.multiobjective.IMultiObjOptimizationProblem;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.SolutionFoundListener;

public class LinMultiObjHelper<T, C> extends DependencyHelper<T, C> implements SolutionFoundListener {
   private IMultiObjOptimizationProblem solver;
   private boolean hasASolution;
   private boolean isOptimal = false;

   public LinMultiObjHelper(IMultiObjOptimizationProblem solver) {
      this(solver, false);
   }

   public LinMultiObjHelper(IMultiObjOptimizationProblem solver, boolean explanationEnabled) {
      this(solver, false, false);
   }

   public LinMultiObjHelper(IMultiObjOptimizationProblem solver, boolean explanationEnabled, boolean canonicalOptFunctionEnabled) {
      super(new OptToPBSATAdapter(solver), explanationEnabled, canonicalOptFunctionEnabled);
      this.solver = solver;
      ((OptToPBSATAdapter)this.getSolver()).setSolutionFoundListener(this);
   }

   public void addCriterion(Collection<T> things) {
      IVecInt literals = new VecInt(things.size());

      for (T thing : things) {
         literals.push(this.getIntValue(thing));
      }

      this.solver.addObjectiveFunction(new ObjectiveFunction(literals, new Vec<>(literals.size(), BigInteger.ONE)));
   }

   public void addWeightedCriterion(Collection<WeightedObject<T>> things) {
      IVecInt literals = new VecInt(things.size());
      IVec<BigInteger> coefs = new Vec<>(things.size());

      for (WeightedObject<T> wo : things) {
         literals.push(this.getIntValue(wo.thing));
         coefs.push(wo.getWeight());
      }

      this.solver.addObjectiveFunction(new ObjectiveFunction(literals, coefs));
   }

   @Override
   public boolean hasASolution() throws TimeoutException {
      try {
         return super.hasASolution();
      } catch (TimeoutException e) {
         if (this.hasASolution) {
            return true;
         } else {
            throw e;
         }
      }
   }

   @Override
   public boolean hasASolution(IVec<T> assumps) throws TimeoutException {
      try {
         return super.hasASolution(assumps);
      } catch (TimeoutException e) {
         if (this.hasASolution) {
            return true;
         } else {
            throw e;
         }
      }
   }

   @Override
   public boolean hasASolution(Collection<T> assumps) throws TimeoutException {
      try {
         return super.hasASolution(assumps);
      } catch (TimeoutException e) {
         if (this.hasASolution) {
            return true;
         } else {
            throw e;
         }
      }
   }

   public boolean isOptimal() {
      return this.isOptimal || this.solver.isOptimal();
   }

   @Override
   public void onSolutionFound(int[] solution) {
      this.hasASolution = true;
   }

   @Override
   public void onSolutionFound(IVecInt solution) {
      this.hasASolution = true;
   }

   @Override
   public void onUnsatTermination() {
      if (this.hasASolution) {
         this.isOptimal = true;
      }
   }
}
