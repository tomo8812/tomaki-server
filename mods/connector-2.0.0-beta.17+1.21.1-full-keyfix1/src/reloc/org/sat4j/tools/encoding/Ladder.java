package reloc.org.sat4j.tools.encoding;

import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class Ladder extends EncodingStrategyAdapter {
   private static final long serialVersionUID = 1L;

   @Override
   public IConstr addAtMostOne(ISolver solver, IVecInt literals) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      int n = literals.size() + 1;
      int xN = solver.nextFreeVarId(true);
      int[] y = new int[n - 1];

      for (int i = 0; i < n - 1; i++) {
         y[i] = solver.nextFreeVarId(true);
      }

      IVecInt clause = new VecInt();

      for (int i = 1; i <= n - 2; i++) {
         clause.push(-y[i]);
         clause.push(y[i - 1]);
         group.add(solver.addClause(clause));
         clause.clear();
      }

      for (int i = 2; i <= n - 1; i++) {
         clause.push(-y[i - 2]);
         clause.push(y[i - 1]);
         clause.push(literals.get(i - 1));
         group.add(solver.addClause(clause));
         clause.clear();
      }

      for (int i = 2; i <= n - 1; i++) {
         clause.push(-literals.get(i - 1));
         clause.push(y[i - 2]);
         group.add(solver.addClause(clause));
         clause.clear();
      }

      for (int i = 2; i <= n - 1; i++) {
         clause.push(-literals.get(i - 1));
         clause.push(-y[i - 1]);
         group.add(solver.addClause(clause));
         clause.clear();
      }

      clause.push(y[0]);
      clause.push(literals.get(0));
      group.add(solver.addClause(clause));
      clause.clear();
      clause.push(-y[0]);
      clause.push(-literals.get(0));
      group.add(solver.addClause(clause));
      clause.clear();
      clause.push(-y[n - 2]);
      clause.push(xN);
      group.add(solver.addClause(clause));
      clause.clear();
      clause.push(y[n - 2]);
      clause.push(-xN);
      group.add(solver.addClause(clause));
      clause.clear();
      return group;
   }

   @Override
   public IConstr addExactlyOne(ISolver solver, IVecInt literals) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      int n = literals.size();
      IVecInt clause = new VecInt();
      if (n == 1) {
         clause.push(literals.get(0));
         group.add(solver.addClause(clause));
         return group;
      }

      int[] y = new int[n - 1];

      for (int i = 0; i < n - 1; i++) {
         y[i] = solver.nextFreeVarId(true);
      }

      for (int i = 1; i <= n - 2; i++) {
         clause.push(-y[i]);
         clause.push(y[i - 1]);
         group.add(solver.addClause(clause));
         clause.clear();
      }

      for (int i = 2; i <= n - 1; i++) {
         clause.push(-y[i - 2]);
         clause.push(y[i - 1]);
         clause.push(literals.get(i - 1));
         group.add(solver.addClause(clause));
         clause.clear();
      }

      for (int i = 2; i <= n - 1; i++) {
         clause.push(-literals.get(i - 1));
         clause.push(y[i - 2]);
         group.add(solver.addClause(clause));
         clause.clear();
      }

      for (int i = 2; i <= n - 1; i++) {
         clause.push(-literals.get(i - 1));
         clause.push(-y[i - 1]);
         group.add(solver.addClause(clause));
         clause.clear();
      }

      clause.push(y[0]);
      clause.push(literals.get(0));
      group.add(solver.addClause(clause));
      clause.clear();
      clause.push(-y[0]);
      clause.push(-literals.get(0));
      group.add(solver.addClause(clause));
      clause.clear();
      clause.push(-y[n - 2]);
      clause.push(literals.get(n - 1));
      group.add(solver.addClause(clause));
      clause.clear();
      clause.push(y[n - 2]);
      clause.push(-literals.get(n - 1));
      group.add(solver.addClause(clause));
      clause.clear();
      return group;
   }
}
