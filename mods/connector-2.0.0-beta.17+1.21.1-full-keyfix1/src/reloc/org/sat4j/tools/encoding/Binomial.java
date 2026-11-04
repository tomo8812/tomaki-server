package reloc.org.sat4j.tools.encoding;

import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class Binomial extends EncodingStrategyAdapter {
   private static final long serialVersionUID = 1L;

   @Override
   public IConstr addAtMost(ISolver solver, IVecInt literals, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      IVecInt clause = new VecInt();
      if (degree == 1) {
         return this.addAtMostOne(solver, literals);
      }

      for (IVecInt vec : literals.subset(degree + 1)) {
         for (int i = 0; i < vec.size(); i++) {
            clause.push(-vec.get(i));
         }

         group.add(solver.addClause(clause));
         clause.clear();
      }

      return group;
   }

   @Override
   public IConstr addAtMostOne(ISolver solver, IVecInt literals) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      IVecInt clause = new VecInt();

      for (int i = 0; i < literals.size() - 1; i++) {
         for (int j = i + 1; j < literals.size(); j++) {
            clause.push(-literals.get(i));
            clause.push(-literals.get(j));
            group.add(solver.addClause(clause));
            clause.clear();
         }
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
