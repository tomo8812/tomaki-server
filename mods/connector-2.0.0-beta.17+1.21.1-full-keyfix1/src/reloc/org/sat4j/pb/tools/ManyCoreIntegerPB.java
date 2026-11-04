package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.pb.IIntegerPBSolver;
import reloc.org.sat4j.pb.core.IntegerVariable;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class ManyCoreIntegerPB<S extends IIntegerPBSolver> extends ManyCorePB<S> implements IIntegerPBSolver {
   private static final long serialVersionUID = 1L;

   public ManyCoreIntegerPB(ASolverFactory<S> factory, String... solverNames) {
      super(factory, solverNames);
   }

   public ManyCoreIntegerPB(S... iSolver) {
      super(iSolver);
   }

   @Override
   public IntegerVariable newIntegerVar(BigInteger maxValue) {
      IntegerVariable res = this.solvers.get(0).newIntegerVar(maxValue);

      for (int i = 1; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).newIntegerVar(maxValue);
      }

      return res;
   }

   @Override
   public IConstr addAtLeast(IntegerVariable var, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtLeast(var, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtLeast(
      IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger degree
   ) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtLeast(literals, coeffs, integerVars, integerVarsCoeffs, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtLeast(literals, coeffs, integerVars, integerVarsCoeffs, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtMost(IntegerVariable var, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtMost(var, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtMost(literals, coeffs, integerVars, integerVarsCoeffs, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtMost(literals, coeffs, integerVars, integerVarsCoeffs, degree));
      }

      return group;
   }

   @Override
   public IConstr addExactly(IntegerVariable var, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addExactly(var, degree));
      }

      return group;
   }

   @Override
   public IConstr addExactly(
      IVecInt literals, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, BigInteger weight
   ) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addExactly(literals, coeffs, integerVars, integerVarsCoeffs, weight));
      }

      return group;
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, int weight) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addExactly(literals, coeffs, integerVars, integerVarsCoeffs, weight));
      }

      return group;
   }

   @Override
   public IConstr addPseudoBoolean(
      IVecInt lits, IVec<BigInteger> coeffs, IVec<IntegerVariable> integerVars, IVec<BigInteger> integerVarsCoeffs, boolean moreThan, BigInteger d
   ) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addPseudoBoolean(lits, coeffs, integerVars, integerVarsCoeffs, moreThan, d));
      }

      return group;
   }

   @Override
   public void addIntegerVariableToObjectiveFunction(IntegerVariable var, BigInteger weight) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).addIntegerVariableToObjectiveFunction(var, weight);
      }
   }

   @Override
   public BigInteger getIntegerVarValue(IntegerVariable var) {
      return this.solvers.get(this.getWinnerId()).getIntegerVarValue(var);
   }
}
