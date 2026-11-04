package reloc.org.sat4j.pb.core;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class PBSolverWithImpliedClause extends PBSolverCP {
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !PBSolverWithImpliedClause.class.desiredAssertionStatus();

   public PBSolverWithImpliedClause(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order) {
      super(learner, dsf, order);
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt literals, IVec<BigInteger> coeffs, boolean moreThan, BigInteger degree) throws ContradictionException {
      IVecInt vlits = this.dimacs2internal(literals);
      assert vlits.size() == literals.size();
      assert literals.size() == coeffs.size();
      Constr result = this.dsfactory.createPseudoBooleanConstraint(vlits, coeffs, moreThan, degree);
      if (result != null && result != Constr.TAUTOLOGY) {
         IVecInt clits = ((PBConstr)result).computeAnImpliedClause();
         if (clits != null) {
            this.addConstr(this.dsfactory.createClause(clits));
         }
      }

      return this.addConstr(result);
   }

   @Override
   public String toString(String prefix) {
      return super.toString(prefix) + "\n" + prefix + "Add implied clauses in preprocessing";
   }
}
