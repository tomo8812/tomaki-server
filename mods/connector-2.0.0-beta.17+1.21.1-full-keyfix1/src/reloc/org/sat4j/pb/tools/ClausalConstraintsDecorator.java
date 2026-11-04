package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.tools.ClausalCardinalitiesDecorator;
import reloc.org.sat4j.tools.encoding.EncodingStrategyAdapter;
import reloc.org.sat4j.tools.encoding.Policy;

public class ClausalConstraintsDecorator extends ClausalCardinalitiesDecorator<IPBSolver> implements IPBSolver {
   private static final long serialVersionUID = 1L;
   private final IPBSolver solver;

   public ClausalConstraintsDecorator(IPBSolver solver, EncodingStrategyAdapter encodingAd) {
      super(solver, encodingAd);
      this.solver = solver;
   }

   public ClausalConstraintsDecorator(IPBSolver solver) {
      super(solver, new Policy());
      this.solver = solver;
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      if (isCardinality(coeffs)) {
         return moreThan ? super.addAtLeast(lits, d.intValue()) : super.addAtMost(lits, d.intValue());
      } else {
         return this.solver.addPseudoBoolean(lits, coeffs, moreThan, d);
      }
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      this.solver.setObjectiveFunction(obj);
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.solver.getObjectiveFunction();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      return isCardinality(coeffs) ? super.addAtMost(literals, degree) : this.solver.addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return isCardinality(coeffs) ? super.addAtMost(literals, degree.intValue()) : this.solver.addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      return isCardinality(coeffs) ? super.addAtLeast(literals, degree) : this.solver.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return isCardinality(coeffs) ? super.addAtLeast(literals, degree.intValue()) : this.solver.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      return isCardinality(coeffs) ? super.addExactly(literals, weight) : this.solver.addExactly(literals, coeffs, weight);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      return isCardinality(coeffs) ? super.addExactly(literals, weight.intValue()) : this.solver.addExactly(literals, coeffs, weight);
   }

   public static boolean isCardinality(IVecInt coeffs) {
      boolean result = true;

      for (int i = 0; result && i < coeffs.size(); i++) {
         result = coeffs.get(i) == 1;
      }

      return result;
   }

   public static boolean isCardinality(IVec<BigInteger> coeffs) {
      boolean result = true;

      for (int i = 0; result && i < coeffs.size(); i++) {
         result = coeffs.get(i).equals(BigInteger.ONE);
      }

      return result;
   }
}
