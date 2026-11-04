package reloc.org.sat4j.pb.multiobjective;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IIntegerPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.core.IntegerVariable;
import reloc.org.sat4j.pb.tools.LexicoDecoratorPB;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public class LeximinDecorator extends LexicoDecoratorPB implements IMultiObjOptimizationProblem {
   private static final long serialVersionUID = 1L;
   private final List<ObjectiveFunction> initObjs = new ArrayList<>();
   private boolean initDone = false;
   private final List<IntegerVariable> objBoundVariables = new ArrayList<>();
   private final IIntegerPBSolver integerSolver;
   private final List<IVecInt> atLeastFlags = new ArrayList<>();

   public LeximinDecorator(IIntegerPBSolver solver) {
      super(solver);
      this.integerSolver = solver;
   }

   @Override
   public boolean admitABetterSolution() throws TimeoutException {
      return this.admitABetterSolution(VecInt.EMPTY);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      if (!this.initDone) {
         this.init();
         this.initDone = true;
      }

      return super.admitABetterSolution(assumps);
   }

   private void init() {
      this.setInitConstraints();
      super.objs = new ArrayList<>();

      for (IntegerVariable boundVar : this.objBoundVariables) {
         IVec<BigInteger> weights = new Vec<>();
         BigInteger factor = BigInteger.ONE;

         for (int i = 0; i < boundVar.getVars().size(); i++) {
            weights.push(factor);
            factor = factor.shiftLeft(1);
         }

         super.objs.add(new ObjectiveFunction(boundVar.getVars(), weights));
      }
   }

   protected void setInitConstraints() {
      BigInteger minObjValuesBound = this.minObjValuesBound();

      for (int i = 0; i < this.initObjs.size(); i++) {
         IntegerVariable boundVar = this.integerSolver.newIntegerVar(minObjValuesBound);
         this.objBoundVariables.add(boundVar);
         this.atLeastFlags.add(new VecInt());

         for (int j = 0; j < this.initObjs.size(); j++) {
            this.addBoundConstraint(i, boundVar, j);
         }

         this.addFlagsCardinalityConstraint(i);
      }
   }

   private void addBoundConstraint(int boundVarIndex, IntegerVariable boundVar, int objIndex) {
      IVecInt literals = new VecInt();
      IVec<BigInteger> coeffs = new Vec<>();
      this.initObjs.get(objIndex).getVars().copyTo(literals);
      this.initObjs.get(objIndex).getCoeffs().copyTo(coeffs);
      int flagLit = this.decorated().nextFreeVarId(true);
      this.atLeastFlags.get(boundVarIndex).push(flagLit);
      literals.push(flagLit);
      coeffs.push(this.minObjValuesBound().negate());

      try {
         this.integerSolver
            .addAtMost(literals, coeffs, new Vec<IntegerVariable>().push(boundVar), new Vec<BigInteger>().push(BigInteger.ONE.negate()), BigInteger.ZERO);
      } catch (ContradictionException e) {
         throw new RuntimeException(e);
      }
   }

   private void addFlagsCardinalityConstraint(int card) {
      try {
         this.decorated().addAtMost(this.atLeastFlags.get(card), card);
      } catch (ContradictionException e) {
         throw new RuntimeException(e);
      }
   }

   protected BigInteger minObjValuesBound() {
      BigInteger maxValue = BigInteger.ZERO;

      for (ObjectiveFunction nextObj : this.initObjs) {
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

   @Override
   public void addCriterion(IVecInt literals) {
      this.initObjs.add(new ObjectiveFunction(literals, new Vec<>(literals.size(), BigInteger.ONE)));
   }

   @Override
   public void addCriterion(IVecInt literals, IVec<BigInteger> coefs) {
      this.initObjs.add(new ObjectiveFunction(literals, coefs));
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
   public void addObjectiveFunction(ObjectiveFunction obj) {
      this.addCriterion(obj);
   }
}
