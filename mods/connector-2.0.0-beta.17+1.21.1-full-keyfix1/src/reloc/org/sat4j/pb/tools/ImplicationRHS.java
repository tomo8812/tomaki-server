package reloc.org.sat4j.pb.tools;

import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class ImplicationRHS<T, C> {
   private final IVecInt clause;
   private final DependencyHelper<T, C> helper;
   private final IVec<IConstr> toName = new Vec<>();

   public ImplicationRHS(DependencyHelper<T, C> helper, IVecInt clause) {
      this.clause = clause;
      this.helper = helper;
   }

   public ImplicationAnd<T, C> implies(T thing) throws ContradictionException {
      ImplicationAnd<T, C> and = new ImplicationAnd<>(this.helper, this.clause);
      and.and(thing);
      return and;
   }

   public ImplicationNamer<T, C> implies(T... things) throws ContradictionException {
      for (T t : things) {
         this.clause.push(this.helper.getIntValue(t));
      }

      this.toName.push(this.helper.solver.addClause(this.clause));
      return new ImplicationNamer<>(this.helper, this.toName);
   }

   public ImplicationAnd<T, C> impliesNot(T thing) throws ContradictionException {
      ImplicationAnd<T, C> and = new ImplicationAnd<>(this.helper, this.clause);
      and.andNot(thing);
      return and;
   }
}
