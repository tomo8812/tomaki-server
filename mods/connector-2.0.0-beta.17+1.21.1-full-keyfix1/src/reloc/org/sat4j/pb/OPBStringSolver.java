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

public class OPBStringSolver extends DimacsStringSolver implements IPBSolver {
   private static final long serialVersionUID = 1L;
   private int indxConstrObj;
   private int nbOfConstraints;
   private ObjectiveFunction obj;
   private boolean inserted = false;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !OPBStringSolver.class.desiredAssertionStatus();

   public OPBStringSolver() {
   }

   public OPBStringSolver(int initSize) {
      super(initSize);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      IteratorInt it = assumps.iterator();

      while (it.hasNext()) {
         int p = it.next();
         if (p > 0) {
            this.getOut().append("+1 x" + p + " >= 1 ;\n");
         } else {
            this.getOut().append("-1 x" + -p + " >= 0 ;\n");
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
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         int p = iterator.next();
         assert p != 0;
         if (p > 0) {
            out.append("+1 x" + p + " ");
         } else {
            out.append("-1 x" + -p + " ");
            negationweight++;
         }
      }

      out.append(">= " + (degree - negationweight) + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      int negationweight = 0;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         int p = iterator.next();
         assert p != 0;
         if (p > 0) {
            out.append("-1 x" + p + " ");
         } else {
            out.append("+1 x" + -p + " ");
            negationweight++;
         }
      }

      out.append(">= " + (-degree + negationweight) + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      int negationweight = 0;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         int lit = iterator.next();
         if (lit > 0) {
            out.append("+1 x" + lit + " ");
         } else {
            out.append("-1 x" + -lit + " ");
            negationweight++;
         }
      }

      out.append(">= " + (1 - negationweight) + " ;\n");
      return FakeConstr.instance();
   }

   public String getExplanation() {
      return null;
   }

   public void setListOfVariablesForExplanation(IVecInt listOfVariables) {
   }

   @Override
   public String toString() {
      StringBuilder out = this.getOut();
      if (!this.inserted) {
         StringBuilder tmp = new StringBuilder();
         tmp.append("* #variable= ");
         tmp.append(this.nVars());
         tmp.append(" #constraint= ");
         tmp.append(this.nbOfConstraints);
         if (this.obj != null && !this.obj.getCoeffs().isEmpty()) {
            tmp.append("\n");
            tmp.append("min: ");
            tmp.append(this.obj);
            tmp.append(";");
         }

         out.insert(this.indxConstrObj, tmp.toString());
         this.inserted = true;
      }

      return out.toString();
   }

   @Override
   public String toString(String prefix) {
      return "OPB output solver";
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

      for (int i = 0; i < literals.size(); i++) {
         out.append(-coeffs.get(i));
         out.append(" x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append(">= ");
      out.append(-degree);
      out.append(" ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i).negate());
         out.append(" x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append(">= ");
      out.append(degree.negate());
      out.append(" ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i));
         out.append(" x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append(">= ");
      out.append(degree);
      out.append(" ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i));
         out.append(" x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append(">= ");
      out.append(degree);
      out.append(" ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addExactly(IVecInt literals, int weight) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      int negationweight = 0;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         int p = iterator.next();
         assert p != 0;
         if (p > 0) {
            out.append("+1 x" + p + " ");
         } else {
            out.append("-1 x" + -p + " ");
            negationweight++;
         }
      }

      out.append("= " + (weight - negationweight) + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i));
         out.append(" x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append("= ");
      out.append(weight);
      out.append(" ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i));
         out.append(" x");
         out.append(literals.get(i));
         out.append(" ");
      }

      out.append("= ");
      out.append(weight);
      out.append(" ;\n");
      return FakeConstr.instance();
   }
}
