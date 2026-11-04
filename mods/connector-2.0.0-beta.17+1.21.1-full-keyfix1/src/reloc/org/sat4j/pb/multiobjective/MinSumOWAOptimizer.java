package reloc.org.sat4j.pb.multiobjective;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IIntegerPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.core.IntegerVariable;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class MinSumOWAOptimizer extends AbstractLinMultiObjOptimizer {
   private static final long serialVersionUID = 1L;
   private final BigInteger[] weights;
   private IntegerVariable objBoundVar;

   public MinSumOWAOptimizer(IIntegerPBSolver solver, int[] weights) {
      super(solver);
      this.weights = new BigInteger[weights.length];

      for (int i = 0; i < weights.length; i++) {
         this.weights[i] = BigInteger.valueOf(weights[i]);
      }
   }

   public MinSumOWAOptimizer(IIntegerPBSolver solver, BigInteger[] weights) {
      super(solver);
      this.weights = weights;
   }

   @Override
   protected void setInitConstraints() {
      try {
         this.objBoundVar = this.integerSolver.newIntegerVar(this.globalObjBound());

         for (List<Integer> permutation : new MinSumOWAOptimizer.PermutationComputer(super.objs.size())) {
            this.addConstraint(this.objBoundVar, permutation);
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private void addConstraint(IntegerVariable boundVar, List<Integer> permutation) {
      IVecInt vars = new VecInt();
      IVec<BigInteger> weights = new Vec<>();

      for (int i = 0; i < super.objs.size(); i++) {
         ObjectiveFunction currentObj = super.objs.get(permutation.get(i));
         BigInteger curWeight = this.weights[i];
         currentObj.getVars().copyTo(vars);
         Iterator<BigInteger> coeffIt = currentObj.getCoeffs().iterator();

         while (coeffIt.hasNext()) {
            weights.push(coeffIt.next().multiply(curWeight));
         }
      }

      BigInteger fact = BigInteger.ONE;

      for (IteratorInt litsIt = boundVar.getVars().iterator(); litsIt.hasNext(); fact = fact.shiftLeft(1)) {
         int nextLit = litsIt.next() * -1;
         vars.push(nextLit);
         weights.push(fact);
      }

      try {
         this.decorated().addAtMost(vars, weights, fact.subtract(BigInteger.ONE));
      } catch (ContradictionException e) {
         throw new UnsupportedOperationException(e);
      }
   }

   private BigInteger globalObjBound() {
      BigInteger bound = BigInteger.ONE;

      for (ObjectiveFunction obj : super.objs) {
         Iterator<BigInteger> coeffIt = obj.getCoeffs().iterator();

         while (coeffIt.hasNext()) {
            bound = bound.add(coeffIt.next());
         }
      }

      return bound;
   }

   @Override
   protected void setGlobalObj() {
      this.decorated().setObjectiveFunction(new ObjectiveFunction(new VecInt(), new Vec<>()));
      this.integerSolver.addIntegerVariableToObjectiveFunction(this.objBoundVar, BigInteger.ONE);
   }

   @Override
   public Number calculateObjective() {
      super.objectiveValue = BigInteger.ZERO;
      BigInteger[] objValues = this.getObjectiveValues();

      for (int i = 0; i < objValues.length; i++) {
         super.objectiveValue = super.objectiveValue.add(objValues[i].multiply(this.weights[i]));
      }

      return this.getObjectiveValue();
   }

   protected BigInteger minObjValuesBound() {
      BigInteger maxValue = BigInteger.ZERO;

      for (ObjectiveFunction nextObj : this.objs) {
         BigInteger maxObjValue = this.maxObjValue(nextObj);
         if (maxValue.compareTo(maxObjValue) < 0) {
            maxValue = maxObjValue;
         }
      }

      return maxValue.add(BigInteger.ONE);
   }

   private BigInteger maxObjValue(ObjectiveFunction obj) {
      IVec<BigInteger> objCoeffs = obj.getCoeffs();
      BigInteger coeffsSum = BigInteger.ZERO;
      Iterator<BigInteger> objCoeffsIt = objCoeffs.iterator();

      while (objCoeffsIt.hasNext()) {
         coeffsSum = coeffsSum.add(objCoeffsIt.next());
      }

      return coeffsSum;
   }

   static class PermutationComputer implements Iterator<List<Integer>>, Iterable<List<Integer>> {
      private int currentElement;
      private final SortedSet<Integer> elements;
      private MinSumOWAOptimizer.PermutationComputer end = null;
      private boolean soleIntegerReturned = false;

      public PermutationComputer(int nbElem) {
         this.elements = new TreeSet<>();

         for (int i = 0; i < nbElem; i++) {
            this.elements.add(i);
         }
      }

      public PermutationComputer(Set<Integer> elements) {
         this.elements = new TreeSet<>(elements);
      }

      @Override
      public Iterator<List<Integer>> iterator() {
         return this;
      }

      @Override
      public boolean hasNext() {
         return !this.soleIntegerReturned && (this.end == null || this.end.hasNext() || this.currentElement != this.elements.last());
      }

      public List<Integer> next() {
         List<Integer> res = new ArrayList<>();
         if (this.elements.size() == 1) {
            return this.addSoleInteger(res);
         }

         if (this.end == null) {
            this.createChildPermutation();
         }

         if (!this.end.hasNext()) {
            this.changeCurrentAndChild();
         }

         return this.addCurrentAndNextInChild(res);
      }

      private void changeCurrentAndChild() {
         this.currentElement = this.elements.tailSet(this.currentElement + 1).first();
         SortedSet<Integer> endElements = new TreeSet<>();

         for (Integer element : this.elements) {
            if (!element.equals(this.currentElement)) {
               endElements.add(element);
            }
         }

         this.end = new MinSumOWAOptimizer.PermutationComputer(endElements);
      }

      private List<Integer> addCurrentAndNextInChild(List<Integer> res) {
         res.add(this.currentElement);
         res.addAll(this.end.next());
         return res;
      }

      private void createChildPermutation() {
         this.currentElement = this.elements.first();
         SortedSet<Integer> endElements = this.elements.tailSet(this.currentElement + 1);
         this.end = new MinSumOWAOptimizer.PermutationComputer(endElements);
      }

      private List<Integer> addSoleInteger(List<Integer> res) {
         this.soleIntegerReturned = true;
         res.add(this.elements.first());
         return res;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object next() {
         return this.next();
      }
   }
}
