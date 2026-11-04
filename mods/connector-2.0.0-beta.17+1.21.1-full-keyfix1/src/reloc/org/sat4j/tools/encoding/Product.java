package reloc.org.sat4j.tools.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class Product extends EncodingStrategyAdapter {
   private static final long serialVersionUID = 1L;

   public IConstr addAtMostNonOpt(ISolver solver, IVecInt literals, int k) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      IVecInt clause = new VecInt();
      int n = literals.size();
      if (k > n) {
         return group;
      }

      if (n == k + 1) {
         for (int i = 0; i < n; i++) {
            clause.push(-literals.get(i));
         }

         group.add(solver.addClause(clause));
         clause.clear();
         return group;
      } else {
         if (n < 7) {
            Binomial binomial = new Binomial();
            return binomial.addAtMost(solver, literals, k);
         }

         int p = (int)Math.ceil(Math.pow(n, 1.0 / (k + 1)));
         int[][] a = new int[n][k + 1];
         int[] result = this.decompositionBase10VersBaseP(0, p, k + 1);
         a[0] = result;
         ArrayList<Integer> dejaPris = new ArrayList<>();
         dejaPris.add(0);
         int tmp = 1;

         for (int i = 1; i <= k + 1; i++) {
            a[i] = this.decompositionBase10VersBaseP(tmp, p, k + 1);
            dejaPris.add(tmp);
            tmp *= p;
         }

         tmp = 2;

         for (int i = k + 2; i < n; i++) {
            while (dejaPris.contains(tmp)) {
               tmp++;
            }

            a[i] = this.decompositionBase10VersBaseP(tmp, p, k + 1);
            tmp++;
         }

         ArrayList<Integer>[] hashTupleSetTable = new ArrayList[k + 1];
         int[][][] aWithoutD = new int[n][k + 1][k];
         HashMap<Integer, Integer>[] ady = new HashMap[k + 1];
         VecInt[] adxd = new VecInt[k + 1];

         for (int d = 0; d < k + 1; d++) {
            hashTupleSetTable[d] = new ArrayList<>();
            ady[d] = new HashMap<>();
            adxd[d] = new VecInt();

            for (int i = 0; i < n; i++) {
               for (int j = 0; j < k; j++) {
                  if (j < d) {
                     aWithoutD[i][d][j] = a[i][j];
                  } else {
                     aWithoutD[i][d][j] = a[i][j + 1];
                  }
               }

               int hash = this.recompositionBase10DepuisBaseP(aWithoutD[i][d], p);
               if (!hashTupleSetTable[d].contains(hash)) {
                  hashTupleSetTable[d].add(hash);
                  int varId = solver.nextFreeVarId(true);
                  ady[d].put(hash, varId);
                  adxd[d].push(varId);
               }
            }
         }

         for (int d = 0; d < k + 1; d++) {
            for (int i = 0; i < n; i++) {
               clause.push(-literals.get(i));
               clause.push(ady[d].get(this.recompositionBase10DepuisBaseP(aWithoutD[i][d], p)));
               group.add(solver.addClause(clause));
               clause.clear();
            }

            group.add(this.addAtMost(solver, adxd[d], k));
         }

         return group;
      }
   }

   @Override
   public IConstr addAtMostOne(ISolver solver, IVecInt literals) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      IVecInt clause = new VecInt();
      int n = literals.size();
      if (n < 7) {
         Binomial binomial = new Binomial();
         return binomial.addAtMostOne(solver, literals);
      }

      int p = (int)Math.ceil(Math.sqrt(n));
      int q = (int)Math.ceil((double)n / p);
      int[] u = new int[p];
      int[] v = new int[q];

      for (int i = 0; i < p; i++) {
         u[i] = solver.nextFreeVarId(true);
      }

      for (int i = 0; i < q; i++) {
         v[i] = solver.nextFreeVarId(true);
      }

      int cpt = 0;

      for (int i = 0; i < p; i++) {
         for (int j = 0; j < q; j++) {
            if (cpt < n) {
               clause.push(-literals.get(cpt));
               clause.push(u[i]);
               group.add(solver.addClause(clause));
               clause.clear();
               clause.push(-literals.get(cpt));
               clause.push(v[j]);
               group.add(solver.addClause(clause));
               clause.clear();
               cpt++;
            }
         }
      }

      group.add(this.addAtMostOne(solver, new VecInt(u)));
      group.add(this.addAtMostOne(solver, new VecInt(v)));
      return group;
   }

   private int[] decompositionBase10VersBaseP(int n, int p, int nbBits) {
      int[] result = new int[nbBits];
      int reste = n;

      for (int j = 0; j < nbBits - 1; j++) {
         int pow = (int)Math.pow(p, nbBits - j - 1);
         int quotient = reste / pow;
         result[j] = quotient + 1;
         reste -= quotient * pow;
      }

      result[nbBits - 1] = reste + 1;
      return result;
   }

   private int recompositionBase10DepuisBaseP(int[] tab, int p) {
      int result = 0;

      for (int i = 0; i < tab.length - 1; i++) {
         result = (result + tab[i] - 1) * p;
      }

      return result + (tab[tab.length - 1] - 1);
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
