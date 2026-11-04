package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.LexicoDecorator;

public class LexicoDecoratorPB extends LexicoDecorator<IPBSolver> implements IPBSolver {
   private static final long serialVersionUID = 1L;
   protected List<ObjectiveFunction> objs = new ArrayList<>();
   private BigInteger bigCurrentValue;

   public LexicoDecoratorPB(IPBSolver solver) {
      super(solver);
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      return this.decorated().addPseudoBoolean(lits, coeffs, moreThan, d);
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      if (obj != null) {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.objs.get(this.currentCriterion);
   }

   @Override
   public boolean admitABetterSolution(IVecInt assumps) throws TimeoutException {
      this.decorated().setObjectiveFunction(this.objs.get(this.currentCriterion));
      return super.admitABetterSolution(assumps);
   }

   @Override
   public void addCriterion(IVecInt literals) {
      this.addCriterion(new ObjectiveFunction(literals, new Vec<>(literals.size(), BigInteger.ONE)));
   }

   public void addCriterion(IVecInt literals, IVec<BigInteger> coefs) {
      this.addCriterion(new ObjectiveFunction(literals, coefs));
   }

   public void addCriterion(ObjectiveFunction objf) {
      this.objs.add(objf);
      if (this.decorated().getObjectiveFunction() == null) {
         this.decorated().setObjectiveFunction(objf);
      }
   }

   @Override
   protected Number evaluate() {
      this.bigCurrentValue = this.objs.get(this.currentCriterion).calculateDegree(this);
      return this.bigCurrentValue;
   }

   @Override
   protected Number evaluate(int criterion) {
      return this.objs.get(criterion).calculateDegree(this);
   }

   @Override
   protected void fixCriterionValue() throws ContradictionException {
      if (this.bigCurrentValue == null) {
         throw new ContradictionException("no current value computed!");
      }

      this.addExactly(this.objs.get(this.currentCriterion).getVars(), this.objs.get(this.currentCriterion).getCoeffs(), this.bigCurrentValue);
   }

   @Override
   protected IConstr discardSolutionsForOptimizing() throws ContradictionException {
      return this.addAtMost(
         this.objs.get(this.currentCriterion).getVars(), this.objs.get(this.currentCriterion).getCoeffs(), this.bigCurrentValue.subtract(BigInteger.ONE)
      );
   }

   @Override
   public int numberOfCriteria() {
      return this.objs.size();
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
