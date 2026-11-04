package reloc.org.sat4j.pb.multiobjective;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IIntegerPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.core.IntegerVariable;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class OrderedObjsOWAOptimizer extends AbstractLinMultiObjOptimizer {
   private static final long serialVersionUID = 1L;
   protected final List<IntegerVariable> objBoundVariables = new ArrayList<>();
   private final List<IVecInt> atLeastFlags = new ArrayList<>();
   protected final BigInteger[] weights;
   private ObjectiveFunction sumObj;
   private ObjectiveFunction lexObj;
   private IConstr lexCstr;
   private IConstr sumCstr;

   public OrderedObjsOWAOptimizer(IIntegerPBSolver solver, int[] weights) {
      super(solver);
      this.weights = new BigInteger[weights.length];

      for (int i = 0; i < weights.length; i++) {
         this.weights[i] = BigInteger.valueOf(weights[i]);
      }
   }

   public OrderedObjsOWAOptimizer(IIntegerPBSolver solver, BigInteger[] weights) {
      super(solver);
      this.weights = weights;
   }

   @Override
   protected void setInitConstraints() {
      String owaWeightsProperty = System.getProperty("_owaWeights");
      if (owaWeightsProperty != null) {
         String[] weights = owaWeightsProperty.split(",");

         for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] = BigInteger.valueOf(Long.valueOf(weights[i]));
         }
      }

      if (this.decorated().isVerbose()) {
         System.out.println(this.getLogPrefix() + "OWA weights : " + Arrays.toString(this.weights));
      }

      BigInteger minObjValuesBound = this.minObjValuesBound();

      for (int i = 0; i < super.objs.size(); i++) {
         IntegerVariable boundVar = this.integerSolver.newIntegerVar(minObjValuesBound);
         this.objBoundVariables.add(boundVar);
         this.atLeastFlags.add(new VecInt());

         for (int j = 0; j < super.objs.size(); j++) {
            this.addBoundConstraint(i, boundVar, j);
         }

         this.addFlagsCardinalityConstraint(i);
      }

      this.createSumAndLexObjs();
   }

   private void createSumAndLexObjs() {
      IVecInt auxObjsVars = new VecInt();
      IVec<BigInteger> sumObjCoeffs = new Vec<>();
      IVec<BigInteger> lexObjCoeffs = new Vec<>();
      BigInteger lexFactor = BigInteger.ONE;
      Iterator<IntegerVariable> intVarIt = this.objBoundVariables.iterator();

      while (intVarIt.hasNext()) {
         BigInteger sumFactor = BigInteger.ONE;
         IntegerVariable nextBoundVar = intVarIt.next();

         for (IteratorInt nextBoundVarLitsIt = nextBoundVar.getVars().iterator(); nextBoundVarLitsIt.hasNext(); lexFactor = lexFactor.shiftLeft(1)) {
            auxObjsVars.push(nextBoundVarLitsIt.next());
            sumObjCoeffs.push(sumFactor);
            sumFactor = sumFactor.shiftLeft(1);
            lexObjCoeffs.push(lexFactor);
         }
      }

      this.sumObj = new ObjectiveFunction(auxObjsVars, sumObjCoeffs);
      this.lexObj = new ObjectiveFunction(auxObjsVars, lexObjCoeffs);
   }

   private void addBoundConstraint(int boundVarIndex, IntegerVariable boundVar, int objIndex) {
      IVecInt literals = new VecInt();
      IVec<BigInteger> coeffs = new Vec<>();
      super.objs.get(objIndex).getVars().copyTo(literals);
      super.objs.get(objIndex).getCoeffs().copyTo(coeffs);
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

   @Override
   public Number calculateObjective() {
      super.objectiveValue = BigInteger.ZERO;
      BigInteger[] objValues = this.getObjectiveValues();

      for (int i = 0; i < objValues.length; i++) {
         super.objectiveValue = super.objectiveValue.add(objValues[i].multiply(this.weights[i]));
      }

      return super.objectiveValue;
   }

   @Override
   public void discardCurrentSolution() throws ContradictionException {
      if (this.sumCstr != null) {
         this.decorated().removeSubsumedConstr(this.sumCstr);
      }

      if (this.lexCstr != null) {
         this.decorated().removeSubsumedConstr(this.lexCstr);
      }

      super.discardCurrentSolution();
      this.lexCstr = this.integerSolver.addAtMost(this.objBoundVariables.get(0), this.objectiveValue.divide(this.weights[this.weights.length - 1]).intValue());
      this.sumCstr = this.decorated().addAtMost(this.sumObj.getVars(), this.sumObj.getCoeffs(), this.maxSumBound());
   }

   private BigInteger maxSumBound() {
      BigInteger weigthsSum = BigInteger.ZERO;

      for (BigInteger weight : this.weights) {
         weigthsSum = weigthsSum.add(weight);
      }

      BigInteger res = super.objectiveValue.divide(weigthsSum);
      res = res.add(BigInteger.ONE);
      return res.multiply(BigInteger.valueOf(super.objs.size()));
   }

   private BigInteger maxLexBound() {
      BigInteger maxObjValue = super.objectiveValue.divide(BigInteger.valueOf(super.objs.size())).add(BigInteger.ONE);
      return maxObjValue.multiply(BigInteger.valueOf(1 << this.objBoundVariables.get(0).getVars().size()).multiply(BigInteger.valueOf(super.objs.size() - 1L)));
   }

   @Override
   public void discard() throws ContradictionException {
      this.discardCurrentSolution();
   }

   @Override
   protected void setGlobalObj() {
      try {
         this.decorated().setObjectiveFunction(new ObjectiveFunction(new VecInt(), new Vec<>()));

         for (int i = 0; i < this.objBoundVariables.size(); i++) {
            this.integerSolver.addIntegerVariableToObjectiveFunction(this.objBoundVariables.get(i), this.weights[this.weights.length - i - 1]);
         }
      } catch (Exception e) {
         Logger.getLogger("reloc.org.sat4j.pb").log(Level.INFO, "Exception", e);
         throw new RuntimeException(e);
      }
   }
}
