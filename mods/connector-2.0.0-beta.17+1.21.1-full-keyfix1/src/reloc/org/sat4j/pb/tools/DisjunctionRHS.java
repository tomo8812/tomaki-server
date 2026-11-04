package reloc.org.sat4j.pb.tools;

import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class DisjunctionRHS<T, C> {
   private final IVecInt literals;
   private final DependencyHelper<T, C> helper;
   private final IVec<IConstr> toName = new Vec<>();

   public DisjunctionRHS(DependencyHelper<T, C> helper, IVecInt literals) {
      this.literals = literals;
      this.helper = helper;
   }

   public ImplicationNamer<T, C> implies(T... things) throws ContradictionException {
      IVecInt clause = new VecInt();

      for (T t : things) {
         clause.push(this.helper.getIntValue(t));
      }

      IteratorInt it = this.literals.iterator();

      while (it.hasNext()) {
         int p = it.next();
         clause.push(p);
         IConstr constr = this.helper.solver.addClause(clause);
         if (constr == null) {
            throw new IllegalStateException("Constraints are not supposed to be null when using the helper");
         }

         this.toName.push(constr);
         clause.remove(p);
      }

      return new ImplicationNamer<>(this.helper, this.toName);
   }
}
