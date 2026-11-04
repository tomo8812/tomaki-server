package reloc.org.sat4j.tools;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.AssignmentOrigin;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IGroupSolver;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.UnitClauseConsumer;

@Feature("solver")
public class DimacsStringSolver extends AbstractOutputSolver implements IGroupSolver {
   private static final long serialVersionUID = 1L;
   private StringBuilder out;
   private int firstCharPos;
   private final int initBuilderSize;
   private int maxvarid = 0;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !DimacsStringSolver.class.desiredAssertionStatus();

   public DimacsStringSolver() {
      this(16);
   }

   public DimacsStringSolver(int initSize) {
      this.out = new StringBuilder(initSize);
      this.initBuilderSize = initSize;
   }

   public StringBuilder getOut() {
      return this.out;
   }

   @Override
   public int newVar() {
      return 0;
   }

   @Override
   public int newVar(int howmany) {
      this.setNbVars(howmany);
      return howmany;
   }

   protected void setNbVars(int howmany) {
      this.nbvars = howmany;
      this.maxvarid = howmany;
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      this.out.append(" ");
      this.out.append(nb);
      this.nbclauses = nb;
      this.fixedNbClauses = true;
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      if (this.firstConstr) {
         if (!this.fixedNbClauses) {
            this.firstCharPos = 0;
            this.out.append("                    ");
            this.out.append("\n");
            this.nbclauses = 0;
         }

         this.firstConstr = false;
      }

      if (!this.fixedNbClauses) {
         this.nbclauses++;
      }

      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         this.out.append(iterator.next()).append(" ");
      }

      this.out.append("0\n");
      return null;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      if (degree > 1) {
         throw new UnsupportedOperationException("Not a clausal problem! degree " + degree);
      }

      assert degree == 1;
      if (this.firstConstr) {
         this.firstCharPos = 0;
         this.out.append("                    ");
         this.out.append("\n");
         this.nbclauses = 0;
         this.firstConstr = false;
      }

      for (int i = 0; i <= literals.size(); i++) {
         for (int j = i + 1; j < literals.size(); j++) {
            if (!this.fixedNbClauses) {
               this.nbclauses++;
            }

            this.out.append(-literals.get(i));
            this.out.append(" ");
            this.out.append(-literals.get(j));
            this.out.append(" 0\n");
         }
      }

      return null;
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
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      if (degree > 1) {
         throw new UnsupportedOperationException("Not a clausal problem! degree " + degree);
      }

      assert degree == 1;
      return this.addClause(literals);
   }

   @Override
   public void reset() {
      this.fixedNbClauses = false;
      this.firstConstr = true;
      this.out = new StringBuilder(this.initBuilderSize);
      this.maxvarid = 0;
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
      return this.maxvarid;
   }

   @Override
   public String toString() {
      this.out.insert(this.firstCharPos, "p cnf " + this.maxvarid + " " + this.nbclauses);
      return this.out.toString();
   }

   @Override
   public int nextFreeVarId(boolean reserve) {
      return reserve ? ++this.maxvarid : this.maxvarid + 1;
   }

   @Override
   public int[] modelWithInternalVariables() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int realNumberOfVariables() {
      return this.maxvarid;
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
      out.println(this.toString());
   }

   @Override
   public IConstr addClause(IVecInt literals, int desc) throws ContradictionException {
      this.out.append(desc + "> ");
      IteratorInt iterator = literals.iterator();

      while (iterator.hasNext()) {
         this.out.append(iterator.next() + " ");
      }

      this.out.append("0\n");
      return null;
   }

   @Override
   public Collection<Integer> getAddedVars() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      Iterator<IVecInt> it = clauses.iterator();

      while (it.hasNext()) {
         this.addClause(it.next());
      }
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
