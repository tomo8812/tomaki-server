package reloc.org.sat4j.tools.encoding;

import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class Sequential extends EncodingStrategyAdapter {
   private static final long serialVersionUID = 1L;

   @Override
   public IConstr addAtMost(ISolver solver, IVecInt literals, int k) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      int n = literals.size();
      if (n == 1) {
         return group;
      }

      int[][] s = new int[n - 1][k];

      for (int j = 0; j < k; j++) {
         for (int i = 0; i < n - 1; i++) {
            s[i][j] = solver.nextFreeVarId(true);
         }
      }

      IVecInt clause = new VecInt();
      clause.push(-literals.get(0));
      clause.push(s[0][0]);
      group.add(solver.addClause(clause));
      clause.clear();

      for (int j = 1; j < k; j++) {
         clause.push(-s[0][j]);
         group.add(solver.addClause(clause));
         clause.clear();
      }

      clause.push(-literals.get(n - 1));
      clause.push(-s[n - 2][k - 1]);
      group.add(solver.addClause(clause));
      clause.clear();

      for (int i = 1; i < n - 1; i++) {
         clause.push(-literals.get(i));
         clause.push(s[i][0]);
         group.add(solver.addClause(clause));
         clause.clear();
         clause.push(-s[i - 1][0]);
         clause.push(s[i][0]);
         group.add(solver.addClause(clause));
         clause.clear();

         for (int j = 1; j < k; j++) {
            clause.push(-literals.get(i));
            clause.push(-s[i - 1][j - 1]);
            clause.push(s[i][j]);
            group.add(solver.addClause(clause));
            clause.clear();
            clause.push(-s[i - 1][j]);
            clause.push(s[i][j]);
            group.add(solver.addClause(clause));
            clause.clear();
         }

         clause.push(-literals.get(i));
         clause.push(-s[i - 1][k - 1]);
         group.add(solver.addClause(clause));
         clause.clear();
      }

      return group;
   }

   @Override
   public IConstr addAtMostOne(ISolver solver, IVecInt literals) throws ContradictionException {
      return this.addAtMost(solver, literals, 1);
   }

   @Override
   public IConstr addExactlyOne(ISolver solver, IVecInt literals) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      group.add(this.addAtLeastOne(solver, literals));
      group.add(this.addAtMostOne(solver, literals));
      return group;
   }

   @Override
   public IConstr addExactly(ISolver solver, IVecInt literals, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      group.add(this.addAtLeast(solver, literals, degree));
      group.add(this.addAtMost(solver, literals, degree));
      return group;
   }
}
