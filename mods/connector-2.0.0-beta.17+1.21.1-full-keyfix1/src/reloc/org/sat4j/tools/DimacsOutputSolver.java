package reloc.org.sat4j.tools;

import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Collection;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.AssignmentOrigin;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IGroupSolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.UnitClauseConsumer;

@Feature("solver")
public class DimacsOutputSolver extends AbstractOutputSolver implements IGroupSolver {
   private static final long serialVersionUID = 1L;
   private transient PrintWriter out;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !DimacsOutputSolver.class.desiredAssertionStatus();

   public DimacsOutputSolver() {
      this(new PrintWriter(System.out, true));
   }

   public DimacsOutputSolver(PrintWriter pw) {
      this.out = pw;
   }

   private void readObject(ObjectInputStream stream) {
      this.out = new PrintWriter(System.out, true);
   }

   @Override
   public int newVar() {
      return 0;
   }

   @Override
   public int newVar(int howmany) {
      this.out.print("p cnf " + howmany);
      this.nbvars = howmany;
      return 0;
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      this.out.println(" " + nb);
      this.nbclauses = nb;
      this.fixedNbClauses = true;
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      if (this.firstConstr) {
         if (!this.fixedNbClauses) {
            this.out.println(" XXXXXX");
         }

         this.firstConstr = false;
      }

      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         this.out.print(iterator.next() + " ");
      }

      this.out.println("0");
      return null;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      if (degree > 1) {
         throw new UnsupportedOperationException("Not a clausal problem! degree " + degree);
      }

      assert degree == 1;
      if (this.firstConstr) {
         if (!this.fixedNbClauses) {
            this.out.println("XXXXXX");
         }

         this.firstConstr = false;
      }

      for (int i = 0; i <= literals.size(); i++) {
         for (int j = i + 1; j < literals.size(); j++) {
            this.out.println("" + -literals.get(i) + " " + -literals.get(j) + " 0");
         }
      }

      return null;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      if (degree > 1) {
         throw new UnsupportedOperationException("Not a clausal problem! degree " + degree);
      }

      assert degree == 1;
      return this.addClause(literals);
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      if (n > 1) {
         throw new UnsupportedOperationException("Not a clausal problem! degree " + n);
      }

      assert n == 1;
      this.addAtMost(literals, n);
      this.addAtLeast(literals, n);
      return null;
   }

   @Override
   public void reset() {
      this.fixedNbClauses = false;
      this.firstConstr = true;
   }

   @Override
   public String toString(String prefix) {
      return "Dimacs output solver";
   }

   @Override
   public int nConstraints() {
      return this.nbclauses;
   }

   @Override
   public int nVars() {
      return this.nbvars;
   }

   @Override
   public int nextFreeVarId(boolean reserve) {
      return reserve ? ++this.nbvars : this.nbvars + 1;
   }

   @Override
   public int[] modelWithInternalVariables() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int realNumberOfVariables() {
      return this.nbvars;
   }

   @Override
   public void registerLiteral(int p) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean primeImplicant(int p) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void printStat(PrintWriter out) {
   }

   @Override
   public void printInfos(PrintWriter out) {
   }

   @Override
   public IConstr addClause(IVecInt literals, int desc) throws ContradictionException {
      this.out.print(desc + "> ");
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         this.out.print(iterator.next() + " ");
      }

      this.out.println("0");
      return null;
   }

   @Override
   public Collection<Integer> getAddedVars() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addParity(IVecInt literals, boolean even) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public AssignmentOrigin getOriginInModel(int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setUnitClauseConsumer(UnitClauseConsumer ucc) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }
}
