package reloc.org.sat4j.pb;

import java.math.BigInteger;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.FakeConstr;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.DimacsStringSolver;

public class LPStringSolver extends DimacsStringSolver implements IPBSolver {
   private static final long serialVersionUID = 1L;
   private int indxConstrObj;
   private int nbOfConstraints;
   private ObjectiveFunction obj;
   private boolean inserted = false;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !LPStringSolver.class.desiredAssertionStatus();

   public LPStringSolver(int initSize) {
      super(initSize);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      IteratorInt it = assumps.iterator();

      while (it.hasNext()) {
         int p = it.next();
         if (p > 0) {
            this.getOut().append("x" + p + " >= 1 \n");
         } else {
            this.getOut().append("- x" + -p + " >= 0 \n");
         }

         this.nbOfConstraints++;
      }

      throw new TimeoutException();
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      return moreThan ? this.addAtLeast(lits, coeffs, d) : this.addAtMost(lits, coeffs, d);
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      this.obj = obj;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      int negationweight = 0;
      boolean first = true;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         int p = iterator.next();
         assert p != 0;
         if (first) {
            if (p > 0) {
               this.getOut().append("x" + p + " ");
            } else {
               this.getOut().append("- x" + -p + " ");
               negationweight++;
            }

            first = false;
         } else if (p > 0) {
            out.append("+ x" + p + " ");
         } else {
            out.append("- x" + -p + " ");
            negationweight++;
         }
      }

      out.append(">= " + (degree - negationweight) + " \n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      int negationweight = 0;
      boolean first = true;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         int p = iterator.next();
         assert p != 0;
         if (first) {
            if (p > 0) {
               this.getOut().append("- x" + p + " ");
            } else {
               this.getOut().append("x" + -p + " ");
               negationweight++;
            }

            first = false;
         } else if (p > 0) {
            out.append("- x" + p + " ");
         } else {
            out.append("+ x" + -p + " ");
            negationweight++;
         }
      }

      out.append(">= " + (-degree + negationweight) + " \n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      boolean first = true;
      int negationweight = 0;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         int lit = iterator.next();
         if (first) {
            if (lit > 0) {
               out.append("x" + lit + " ");
            } else {
               out.append("-x" + -lit + " ");
               negationweight++;
            }

            first = false;
         } else if (lit > 0) {
            out.append("+ x" + lit + " ");
         } else {
            out.append("- x" + -lit + " ");
            negationweight++;
         }
      }

      out.append(">= " + (1 - negationweight) + "\n");
      return FakeConstr.instance();
   }

   public String getExplanation() {
      return null;
   }

   public void setListOfVariablesForExplanation(IVecInt listOfVariables) {
   }

   public void objectiveFunctionToLP(ObjectiveFunction obj, StringBuilder buffer) {
      buffer.append("Minimize \n");
      buffer.append("obj: ");
      IVecInt variables = obj.getVars();
      IVec<BigInteger> coeffs = obj.getCoeffs();
      int n = variables.size();
      if (n > 0) {
         buffer.append(coeffs.get(0));
         buffer.append("x");
         buffer.append(variables.get(0));
         buffer.append(" ");
      }

      for (int i = 1; i < n; i++) {
         BigInteger coeff = coeffs.get(i);
         if (coeff.signum() > 0) {
            buffer.append("+ " + coeff);
         } else {
            buffer.append("- " + coeff.negate());
         }

         buffer.append("x");
         buffer.append(variables.get(i));
         buffer.append(" ");
      }
   }

   @Override
   public String toString() {
      StringBuilder out = this.getOut();
      if (!this.inserted) {
         StringBuilder tmp = new StringBuilder();
         if (this.obj != null) {
            this.objectiveFunctionToLP(this.obj, tmp);
            tmp.append("\n");
            tmp.append("Subject To\n ");
         }

         out.insert(this.indxConstrObj, tmp.toString());
         this.inserted = true;
      }

      out.append("Binary \n");

      for (int i = 1; i <= this.nVars(); i++) {
         out.append("x" + i + "\n");
      }

      out.append("\n");
      out.append("End");
      return out.toString();
   }

   @Override
   public String toString(String prefix) {
      return this.toString();
   }

   @Override
   public int newVar(int howmany) {
      StringBuilder out = this.getOut();
      this.setNbVars(howmany);
      this.indxConstrObj = out.length();
      out.append("\n");
      return howmany;
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.obj;
   }

   @Override
   public int nConstraints() {
      return this.nbOfConstraints;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;
      int n = literals.size();
      if (n > 0) {
         out.append(-coeffs.get(0));
         out.append("x");
         out.append(literals.get(0));
         out.append(" ");
      }

      for (int i = 1; i < n; i++) {
         int coeff = coeffs.get(i);
         if (coeff > 0) {
            out.append("+ " + -coeff);
         } else {
            out.append("- " + coeff);
         }

         out.append("x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append(">= ");
      out.append(-degree);
      out.append(" \n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;
      int n = literals.size();
      if (n > 0) {
         out.append(coeffs.get(0).negate());
         out.append("x");
         out.append(literals.get(0));
         out.append(" ");
      }

      for (int i = 1; i < n; i++) {
         BigInteger coeff = coeffs.get(i);
         if (coeff.signum() < 0) {
            out.append("+ " + coeff.negate());
         } else {
            out.append("- " + coeff);
         }

         out.append("x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append(">= ");
      out.append(degree.negate());
      out.append(" \n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;
      int n = literals.size();
      if (n > 0) {
         out.append(coeffs.get(0));
         out.append("x");
         out.append(literals.get(0));
         out.append(" ");
      }

      for (int i = 1; i < n; i++) {
         int coeff = coeffs.get(i);
         if (coeff > 0) {
            out.append("+ " + coeff);
         } else {
            out.append("- " + coeff * -1);
         }

         out.append("x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append(">= ");
      out.append(degree);
      out.append(" \n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;
      int n = literals.size();
      if (n > 0) {
         out.append(coeffs.get(0));
         out.append("x");
         out.append(literals.get(0));
         out.append(" ");
      }

      for (int i = 1; i < n; i++) {
         BigInteger coeff = coeffs.get(i);
         if (coeff.signum() > 0) {
            out.append("+ " + coeff);
         } else {
            out.append("- " + coeff.negate());
         }

         out.append("x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append(">= ");
      out.append(degree);
      out.append(" \n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;
      int n = literals.size();
      if (n > 0) {
         out.append(coeffs.get(0));
         out.append("x");
         out.append(literals.get(0));
         out.append(" ");
      }

      for (int i = 1; i < n; i++) {
         int coeff = coeffs.get(i);
         if (coeff > 0) {
            out.append("+ " + coeff);
         } else {
            out.append("- " + coeff * -1);
         }

         out.append("x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append("= ");
      out.append(weight);
      out.append(" \n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;
      int n = literals.size();
      if (n > 0) {
         out.append(coeffs.get(0));
         out.append("x");
         out.append(literals.get(0));
         out.append(" ");
      }

      for (int i = 1; i < n; i++) {
         BigInteger coeff = coeffs.get(i);
         if (coeff.signum() > 0) {
            out.append("+ " + coeff);
         } else {
            out.append("- " + coeff.negate());
         }

         out.append("x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append("= ");
      out.append(weight);
      out.append(" \n");
      return FakeConstr.instance();
   }
}
