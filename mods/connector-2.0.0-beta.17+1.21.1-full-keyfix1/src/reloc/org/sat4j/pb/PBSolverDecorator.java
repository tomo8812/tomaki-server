package reloc.org.sat4j.pb;

import java.math.BigInteger;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.tools.SolverDecorator;

public class PBSolverDecorator extends SolverDecorator<IPBSolver> implements IPBSolver {
   private static final long serialVersionUID = 1L;

   public PBSolverDecorator(IPBSolver solver) {
      super(solver);
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      return this.decorated().addPseudoBoolean(lits, coeffs, moreThan, d);
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      this.decorated().setObjectiveFunction(obj);
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.decorated().getObjectiveFunction();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      return this.decorated().addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return this.decorated().addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      return this.decorated().addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return this.decorated().addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      return this.decorated().addExactly(literals, coeffs, weight);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      return this.decorated().addExactly(literals, coeffs, weight);
   }
}
