package reloc.org.sat4j.minisat.learning;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.constraints.cnf.WLClause;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "learning", parent = "expert")
public final class ClauseOnlyLearning<D extends DataStructureFactory> extends LimitedLearning<D> {
   private static final long serialVersionUID = 1L;

   @Override
   protected boolean learningCondition(Constr constr) {
      return constr instanceof WLClause;
   }

   @Override
   public String toString() {
      return "Limit learning to clauses using watched literals only";
   }
}
