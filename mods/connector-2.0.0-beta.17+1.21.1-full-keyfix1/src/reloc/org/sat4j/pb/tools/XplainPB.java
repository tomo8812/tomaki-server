package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import java.util.Iterator;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.tools.xplain.Xplain;

public class XplainPB extends Xplain<IPBSolver> implements IPBSolver {
   private static final long serialVersionUID = 1L;

   public XplainPB(IPBSolver solver) {
      super(solver);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      IVecInt coeffs = new VecInt(literals.size(), 1);
      int newvar = this.createNewVar(literals);
      literals.push(newvar);
      coeffs.push(degree);
      IConstr constr = this.decorated().addAtLeast(literals, coeffs, degree);
      if (constr == null) {
         this.discardLastestVar();
      } else {
         this.getConstrs().put(newvar, constr);
      }

      return constr;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      IVecInt coeffs = new VecInt(literals.size(), 1);
      int newvar = this.createNewVar(literals);
      literals.push(newvar);
      coeffs.push(degree - coeffs.size());
      IConstr constr = this.decorated().addAtMost(literals, coeffs, degree);
      if (constr == null) {
         this.discardLastestVar();
      } else {
         this.getConstrs().put(newvar, constr);
      }

      return constr;
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      int newvar = this.createNewVar(literals);
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
         this.getConstrs().put(newvar, group);
         return group;
      }
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      int newvar = this.createNewVar(lits);
      lits.push(newvar);
      if (moreThan && d.signum() >= 0) {
         coeffs.push(d);
      } else {
         BigInteger sum = BigInteger.ZERO;
         Iterator<BigInteger> ite = coeffs.iterator();

         while (ite.hasNext()) {
            sum = sum.add(ite.next());
         }

         sum = sum.subtract(d);
         coeffs.push(sum.negate());
      }

      IConstr constr = this.decorated().addPseudoBoolean(lits, coeffs, moreThan, d);
      if (constr == null) {
         this.discardLastestVar();
      } else {
         this.getConstrs().put(newvar, constr);
      }

      return constr;
   }

   private IConstr addPseudoBoolean(IVecInt lits, IVecInt coeffs, boolean moreThan, int d) throws ContradictionException {
      int newvar = this.createNewVar(lits);
      lits.push(newvar);
      if (moreThan && d >= 0) {
         coeffs.push(d);
      } else {
         int sum = 0;
         IteratorInt ite = coeffs.iterator();

         while (ite.hasNext()) {
            sum += ite.next();
         }

         sum -= d;
         coeffs.push(-sum);
      }

      IConstr constr = moreThan ? this.decorated().addAtLeast(lits, coeffs, d) : this.decorated().addAtMost(lits, coeffs, d);
      if (constr == null) {
         this.discardLastestVar();
      } else {
         this.getConstrs().put(newvar, constr);
      }

      return constr;
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
      return this.addPseudoBoolean(literals, coeffs, false, degree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return this.addPseudoBoolean(literals, coeffs, false, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      return this.addPseudoBoolean(literals, coeffs, true, degree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return this.addPseudoBoolean(literals, coeffs, true, degree);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      throw new UnsupportedOperationException();
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      throw new UnsupportedOperationException();
   }
}
