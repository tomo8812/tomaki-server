package reloc.org.sat4j.tools.encoding;

import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class Binary extends EncodingStrategyAdapter {
   private static final long serialVersionUID = 1L;

   @Override
   public IConstr addAtMostOne(ISolver solver, IVecInt literals) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      int n = literals.size();
      int p = (int)Math.ceil(Math.log(n) / Math.log(2.0));
      int k = (int)Math.pow(2.0, p) - n;
      IVecInt clause = new VecInt();
      String binary = "";
      if (p == 0) {
         return group;
      }

      int[] y = new int[p];

      for (int i = 0; i < p; i++) {
         y[i] = solver.nextFreeVarId(true);
      }

      for (int i = 0; i < k; i++) {
         binary = Integer.toBinaryString(i);

         while (binary.length() != p - 1) {
            binary = "0" + binary;
         }

         for (int j = 0; j < p - 1; j++) {
            clause.push(-literals.get(i));
            if (binary.charAt(j) == '0') {
               clause.push(-y[j]);
            } else {
               clause.push(y[j]);
            }

            group.add(solver.addClause(clause));
            clause.clear();
         }
      }

      for (int i = k; i < n; i++) {
         binary = Integer.toBinaryString(2 * k + i - k);

         while (binary.length() != p) {
            binary = "0" + binary;
         }

         for (int j = 0; j < p; j++) {
            clause.push(-literals.get(i));
            if (binary.charAt(j) == '0') {
               clause.push(-y[j]);
            } else {
               clause.push(y[j]);
            }

            group.add(solver.addClause(clause));
            clause.clear();
         }
      }

      return group;
   }

   @Override
   public IConstr addAtMost(ISolver solver, IVecInt literals, int k) throws ContradictionException {
      int n = literals.size();
      int p = (int)Math.ceil(Math.log(n) / Math.log(2.0));
      ConstrGroup group = new ConstrGroup(false);
      int[][] b = new int[k][p];

      for (int i = 0; i < k; i++) {
         for (int j = 0; j < p; j++) {
            b[i][j] = solver.nextFreeVarId(true);
         }
      }

      int[][] t = new int[k][n];

      for (int i = 0; i < k; i++) {
         for (int j = 0; j < n; j++) {
            t[i][j] = solver.nextFreeVarId(true);
         }
      }

      IVecInt clause1 = new VecInt();
      IVecInt clause2 = new VecInt();
      String binary = "";

      for (int i = 0; i < n; i++) {
         int max = Math.max(1, k - n + i + 1);
         int min = Math.min(i + 1, k);
         clause1.push(-literals.get(i));
         binary = Integer.toBinaryString(i);

         while (binary.length() != p) {
            binary = "0" + binary;
         }

         for (int g = max - 1; g < min; g++) {
            clause1.push(t[g][i]);

            for (int j = 0; j < p; j++) {
               clause2.push(-t[g][i]);
               if (binary.charAt(j) == '0') {
                  clause2.push(-b[g][j]);
               } else {
                  clause2.push(b[g][j]);
               }

               group.add(solver.addClause(clause2));
               clause2.clear();
            }
         }

         group.add(solver.addClause(clause1));
         clause1.clear();
      }

      return group;
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
