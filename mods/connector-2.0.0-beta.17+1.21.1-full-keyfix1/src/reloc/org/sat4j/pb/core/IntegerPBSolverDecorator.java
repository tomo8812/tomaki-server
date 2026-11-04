package reloc.org.sat4j.pb.core;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IIntegerPBSolver;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.PBSolverDecorator;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class IntegerPBSolverDecorator extends PBSolverDecorator implements IIntegerPBSolver {
   private static final long serialVersionUID = 1L;
   private final IPBSolver decorated;

   public IntegerPBSolverDecorator(IPBSolver solver) {
      super(solver);
      this.decorated = solver;
   }

   @Override
   public IntegerVariable newIntegerVar(BigInteger maxValue) {
      if (maxValue.compareTo(BigInteger.ZERO) <= 0) {
         throw new IllegalArgumentException("the integer variable maximum value must be at least 1");
      }

      int nbVars = maxValue.bitLength();
      IVecInt vars = new VecInt(nbVars);

      for (int i = 0; i < nbVars; i++) {
         vars.push(this.decorated.nextFreeVarId(true));
      }

      return new IntegerVariable(vars);
   }

   @Override
   public IConstr addAtLeast(
      IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger degree
   ) throws ContradictionException {
      literals = new VecInt(Arrays.copyOf(literals.toArray(), literals.size()));
      coeffs = this.copyBigIntVec(coeffs);
      this.pushIntegerVariables(literals, coeffs, integerVars, integerVarsCoeffs);
      return this.decorated.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int degree) throws ContradictionException {
      literals = new VecInt(Arrays.copyOf(literals.toArray(), literals.size()));
      coeffs = new VecInt(coeffs.toArray());
      this.pushIntegerVariables(literals, coeffs, integerVars, integerVarsCoeffs);
      return this.decorated.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger degree) throws ContradictionException {
      literals = new VecInt(Arrays.copyOf(literals.toArray(), literals.size()));
      coeffs = this.copyBigIntVec(coeffs);
      this.pushIntegerVariables(literals, coeffs, integerVars, integerVarsCoeffs);
      return this.decorated.addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int degree) throws ContradictionException {
      literals = new VecInt(Arrays.copyOf(literals.toArray(), literals.size()));
      coeffs = new VecInt(coeffs.toArray());
      this.pushIntegerVariables(literals, coeffs, integerVars, integerVarsCoeffs);
      return this.decorated.addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addExactly(
      IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger weight
   ) throws ContradictionException {
      literals = new VecInt(Arrays.copyOf(literals.toArray(), literals.size()));
      coeffs = this.copyBigIntVec(coeffs);
      this.pushIntegerVariables(literals, coeffs, integerVars, integerVarsCoeffs);
      return this.decorated.addExactly(literals, coeffs, weight);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int weight) throws ContradictionException {
      literals = new VecInt(Arrays.copyOf(literals.toArray(), literals.size()));
      coeffs = new VecInt(coeffs.toArray());
      this.pushIntegerVariables(literals, coeffs, integerVars, integerVarsCoeffs);
      return this.decorated.addExactly(literals, coeffs, weight);
   }

   @Override
   public IConstr addPseudoBoolean(
      IVecInt lits, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, boolean moreThan, BigInteger d
   ) throws ContradictionException {
      lits = new VecInt(Arrays.copyOf(lits.toArray(), lits.size()));
      coeffs = this.copyBigIntVec(coeffs);
      this.pushIntegerVariables(lits, coeffs, integerVars, integerVarsCoeffs);
      return this.decorated.addPseudoBoolean(lits, coeffs, moreThan, d);
   }

   private void pushIntegerVariables(IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs) {
      if (integerVars.size() != integerVarsCoeffs.size()) {
         throw new IllegalArgumentException("different number of integer variables and integer variables coeffs");
      }

      Iterator<IntegerVariable> intVarsIt = integerVars.iterator();

      for (BigInteger factor : integerVarsCoeffs) {
         for (IteratorInt intVarLitsIt = intVarsIt.next().getVars().iterator(); intVarLitsIt.hasNext(); factor = factor.shiftLeft(1)) {
            literals.push(intVarLitsIt.next());
            coeffs.push(factor);
         }
      }
   }

   private void pushIntegerVariables(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs) {
      if (integerVars.size() != integerVarsCoeffs.size()) {
         throw new IllegalArgumentException("different number of integer variables and integer variables coeffs");
      }

      Iterator<IntegerVariable> intVarsIt = integerVars.iterator();

      for (BigInteger factor : integerVarsCoeffs) {
         for (IteratorInt intVarLitsIt = intVarsIt.next().getVars().iterator(); intVarLitsIt.hasNext(); factor = factor.shiftLeft(1)) {
            literals.push(intVarLitsIt.next());
            coeffs.push(factor.intValue());
         }
      }
   }

   private void pushIntegerVariables(IVecInt vars, IVec<BigInteger> coeffs, IntegerVariable var, BigInteger weight) {
      this.pushIntegerVariables(vars, coeffs, new Vec<IntegerVariable>().push(var), new Vec<BigInteger>().push(weight));
   }

   @Override
   public void addIntegerVariableToObjectiveFunction(IntegerVariable var, BigInteger weight) {
      IVecInt varsCopy = new VecInt(this.decorated().getObjectiveFunction().getVars().toArray());
      Object[] coeffsArray = this.decorated().getObjectiveFunction().getCoeffs().toArray();
      IVec<BigInteger> coeffsCopy = new Vec<>();

      for (int i = 0; i < coeffsArray.length; i++) {
         coeffsCopy.push((BigInteger)coeffsArray[i]);
      }

      this.pushIntegerVariables(varsCopy, coeffsCopy, var, weight);
      this.decorated().setObjectiveFunction(new ObjectiveFunction(varsCopy, coeffsCopy));
   }

   @Override
   public IConstr addAtLeast(IntegerVariable var, int degree) throws ContradictionException {
      return this.decorated.addAtLeast(var.getVars(), this.integerVariableCoeffs(var), degree);
   }

   @Override
   public IConstr addAtMost(IntegerVariable var, int degree) throws ContradictionException {
      return this.decorated.addAtMost(var.getVars(), this.integerVariableCoeffs(var), degree);
   }

   @Override
   public IConstr addExactly(IntegerVariable var, int degree) throws ContradictionException {
      return this.decorated.addExactly(var.getVars(), this.integerVariableCoeffs(var), degree);
   }

   private IVecInt integerVariableCoeffs(IntegerVariable var) {
      int nbLits = var.getVars().size();
      IVecInt coeffs = new VecInt(nbLits);
      int factor = 1;

      for (int i = 0; i < nbLits; i++) {
         coeffs.push(factor);
         factor <<= 1;
      }

      return coeffs;
   }

   private IVec<BigInteger> copyBigIntVec(IVec<BigInteger> src) {
      IVec<BigInteger> res = new Vec<>();

      for (int i = 0; i < src.size(); i++) {
         res.push(src.get(i));
      }

      return res;
   }

   @Override
   public BigInteger getIntegerVarValue(IntegerVariable var) {
      BigInteger res = BigInteger.ZERO;
      BigInteger factor = BigInteger.ONE;

      for (IteratorInt it = var.getVars().iterator(); it.hasNext(); factor = factor.shiftLeft(1)) {
         if (this.decorated.model(it.next())) {
            res = res.add(factor);
         }
      }

      return res;
   }
}
