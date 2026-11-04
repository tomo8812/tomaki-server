package reloc.org.sat4j.pb;

import java.math.BigInteger;
import java.util.Map;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.FakeConstr;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.tools.DimacsStringSolver;

public class UserFriendlyPBStringSolver<T> extends DimacsStringSolver implements IPBSolver {
   private static final long serialVersionUID = 1L;
   private int indxConstrObj;
   private int nbOfConstraints;
   private ObjectiveFunction obj;
   private boolean inserted = false;
   private Map<Integer, T> mapping;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !UserFriendlyPBStringSolver.class.desiredAssertionStatus();

   public UserFriendlyPBStringSolver() {
   }

   public UserFriendlyPBStringSolver(int initSize) {
      super(initSize);
   }

   public void setMapping(Map<Integer, T> mapping) {
      this.mapping = mapping;
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert lits.size() == coeffs.size();
      this.nbOfConstraints++;
      if (moreThan) {
         for (int i = 0; i < lits.size(); i++) {
            out.append(coeffs.get(i) + " " + this.mapping.get(lits.get(i)) + " + ");
         }

         out.append(">= " + d + " ;\n");
      } else {
         for (int i = 0; i < lits.size(); i++) {
            out.append(coeffs.get(i) + " " + this.mapping.get(lits.get(i)) + " + ");
         }

         out.append("<= " + d + " ;\n");
      }

      return FakeConstr.instance();
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      this.obj = obj;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         out.append(this.mapping.get(iterator.next()));
         out.append(" ");
         if (iterator.hasNext()) {
            out.append("+ ");
         }
      }

      out.append(">= " + degree + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         out.append(this.mapping.get(iterator.next()));
         out.append(" ");
         if (iterator.hasNext()) {
            out.append("+ ");
         }
      }

      out.append("<= " + degree + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      StringBuilder out = this.getOut();
      this.nbOfConstraints++;
      boolean beginning = true;
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         int lit = iterator.next();
         if (lit > 0) {
            if (beginning) {
               out.append("-> ");
               beginning = false;
            }

            out.append(this.mapping.get(lit));
         } else {
            if (!beginning) {
               out.append(" NOT ");
            }

            out.append(this.mapping.get(-lit));
         }

         out.append(" ");
         if (iterator.hasNext() && !beginning) {
            out.append("OR ");
         }
      }

      if (beginning) {
         out.append("-> false");
      }

      out.append(" ;\n");
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
         tmp.append(" \n");
         if (this.obj != null) {
            tmp.append("min: ");
            tmp.append(this.decode(this.obj));
            tmp.append(" ;\n");
         }

         out.insert(this.indxConstrObj, tmp.toString());
         this.inserted = true;
      }

      return out.toString();
   }

   private Object decode(ObjectiveFunction obj2) {
      StringBuilder stb = new StringBuilder();
      IVecInt vars = obj2.getVars();
      IVec<BigInteger> coeffs = obj2.getCoeffs();

      for (int i = 0; i < vars.size(); i++) {
         stb.append(coeffs.get(i));
         stb.append(" ");
         int lit = vars.get(i);
         if (lit > 0) {
            stb.append(this.mapping.get(lit));
         } else {
            stb.append("~");
            stb.append(this.mapping.get(-lit));
         }

         stb.append(" ");
      }

      return stb.toString();
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
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i) + " " + this.mapping.get(literals.get(i)) + " + ");
      }

      out.append("<= " + degree + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i) + " " + this.mapping.get(literals.get(i)) + " + ");
      }

      out.append("<= " + degree + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i) + " " + this.mapping.get(literals.get(i)) + " + ");
      }

      out.append(">= " + degree + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i) + " " + this.mapping.get(literals.get(i)) + " + ");
      }

      out.append(">= " + degree + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i) + " " + this.mapping.get(literals.get(i)) + " + ");
      }

      out.append("= " + weight + " ;\n");
      return FakeConstr.instance();
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      StringBuilder out = this.getOut();
      assert literals.size() == coeffs.size();
      this.nbOfConstraints++;

      for (int i = 0; i < literals.size(); i++) {
         out.append(coeffs.get(i) + " " + this.mapping.get(literals.get(i)) + " + ");
      }

      out.append("= " + weight + " ;\n");
      return FakeConstr.instance();
   }
}
