package reloc.org.sat4j.tools.encoding;

import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class Commander extends EncodingStrategyAdapter {
   private static final long serialVersionUID = 1L;

   @Override
   public IConstr addAtMostOne(ISolver solver, IVecInt literals) throws ContradictionException {
      return this.addAtMostOne(solver, literals, 3);
   }

   private IConstr addAtMostOne(ISolver solver, IVecInt literals, int groupSize) throws ContradictionException {
      ConstrGroup constrGroup = new ConstrGroup(false);
      IVecInt clause = new VecInt();
      IVecInt clause1 = new VecInt();
      int n = literals.size();
      int nbGroup = (int)Math.ceil((double)literals.size() / groupSize);
      if (nbGroup == 1) {
         for (int i = 0; i < literals.size() - 1; i++) {
            for (int j = i + 1; j < literals.size(); j++) {
               clause.push(-literals.get(i));
               clause.push(-literals.get(j));
               constrGroup.add(solver.addClause(clause));
               clause.clear();
            }
         }

         return constrGroup;
      } else {
         int[] c = new int[nbGroup];

         for (int i = 0; i < nbGroup; i++) {
            c[i] = solver.nextFreeVarId(true);
         }

         int nbVarLastGroup = n - (nbGroup - 1) * groupSize;

         for (int i = 0; i < nbGroup; i++) {
            int size = 0;
            if (i == nbGroup - 1) {
               size = nbVarLastGroup;
            } else {
               size = groupSize;
            }

            for (int j = 0; j < size - 1; j++) {
               for (int k = j + 1; k < size; k++) {
                  clause.push(-literals.get(i * groupSize + j));
                  clause.push(-literals.get(i * groupSize + k));
                  constrGroup.add(solver.addClause(clause));
                  clause.clear();
               }
            }

            clause1.push(-c[i]);

            for (int j = 0; j < size; j++) {
               clause1.push(literals.get(i * groupSize + j));
               clause.push(c[i]);
               clause.push(-literals.get(i * groupSize + j));
               constrGroup.add(solver.addClause(clause));
               clause.clear();
            }

            constrGroup.add(solver.addClause(clause1));
            clause1.clear();
         }

         constrGroup.add(this.addAtMostOne(solver, new VecInt(c), groupSize));
         return constrGroup;
      }
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
