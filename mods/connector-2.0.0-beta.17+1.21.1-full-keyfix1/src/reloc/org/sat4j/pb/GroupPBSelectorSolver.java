package reloc.org.sat4j.pb;

import java.math.BigInteger;
import java.util.Iterator;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.tools.GroupClauseSelectorSolver;

public class GroupPBSelectorSolver extends GroupClauseSelectorSolver<IPBSolver> implements IGroupPBSolver {
   private static final long serialVersionUID = 1L;

   public GroupPBSelectorSolver(IPBSolver solver) {
      super(solver);
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      return this.decorated().addPseudoBoolean(lits, coeffs, moreThan, d);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return this.decorated().addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
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

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      this.decorated().setObjectiveFunction(obj);
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.decorated().getObjectiveFunction();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree, int groupid) throws ContradictionException {
      IVecInt coeffs = new VecInt(literals.size(), 1);
      Integer newvar = this.getGroupVar(literals, groupid);
      literals.push(newvar);
      coeffs.push(degree - coeffs.size());
      return this.decorated().addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree, int groupid) throws ContradictionException {
      IVecInt coeffs = new VecInt(literals.size(), 1);
      int newvar = this.getGroupVar(literals, groupid);
      literals.push(newvar);
      coeffs.push(degree);
      return this.decorated().addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n, int groupid) throws ContradictionException {
      int newvar = this.getGroupVar(literals, groupid);
      IVecInt coeffs = new VecInt(literals.size(), 1);
      literals.push(newvar);
      coeffs.push(n - coeffs.size());
      IConstr constr1 = this.decorated().addAtMost(literals, coeffs, n);
      coeffs.pop();
      coeffs.push(n);
      IConstr constr2 = this.decorated().addAtLeast(literals, coeffs, n);
      if (constr1 == null && constr2 == null) {
         this.discardLastestVar();
         return null;
      } else {
         ConstrGroup group = new ConstrGroup();
         group.add(constr1);
         group.add(constr2);
         return group;
      }
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree, int groupdId) throws ContradictionException {
      int newvar = this.getGroupVar(literals, groupdId);
      literals.push(newvar);
      BigInteger sum = BigInteger.ZERO;
      Iterator<BigInteger> ite = coeffs.iterator();

      while (ite.hasNext()) {
         sum = sum.add(ite.next());
      }

      sum = sum.subtract(degree);
      coeffs.push(sum.negate());
      return this.decorated().addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree, int groupdId) throws ContradictionException {
      int newvar = this.getGroupVar(literals, groupdId);
      literals.push(newvar);
      int sum = 0;
      IteratorInt ite = coeffs.iterator();

      while (ite.hasNext()) {
         sum += ite.next();
      }

      sum -= degree;
      coeffs.push(-sum);
      return this.decorated().addAtMost(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree, int groupId) throws ContradictionException {
      int newvar = this.getGroupVar(literals, groupId);
      literals.push(newvar);
      if (degree >= 0) {
         coeffs.push(degree);
      } else {
         int sum = 0;
         IteratorInt ite = coeffs.iterator();

         while (ite.hasNext()) {
            sum += ite.next();
         }

         sum -= degree;
         coeffs.push(-sum);
      }

      return this.decorated().addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree, int groupId) throws ContradictionException {
      int newvar = this.getGroupVar(literals, groupId);
      literals.push(newvar);
      if (degree.signum() >= 0) {
         coeffs.push(degree);
      } else {
         BigInteger sum = BigInteger.ZERO;
         Iterator<BigInteger> ite = coeffs.iterator();

         while (ite.hasNext()) {
            sum = sum.add(ite.next());
         }

         sum = sum.subtract(degree);
         coeffs.push(sum.negate());
      }

      return this.decorated().addAtLeast(literals, coeffs, degree);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight, int groupId) throws ContradictionException {
      throw new UnsupportedOperationException("not implemented yet!");
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight, int groupId) throws ContradictionException {
      throw new UnsupportedOperationException("not implemented yet!");
   }
}
