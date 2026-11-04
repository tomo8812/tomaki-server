package reloc.org.sat4j.minisat.constraints;

import reloc.org.sat4j.minisat.constraints.card.AtLeast;
import reloc.org.sat4j.minisat.constraints.cnf.LearntWLClause;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;

public class CardinalityDataStructure extends AbstractCardinalityDataStructure {
   private static final long serialVersionUID = 1L;

   @Override
   public Constr createUnregisteredClause(IVecInt literals) {
      return new LearntWLClause(literals, this.getVocabulary());
   }

   @Override
   public Constr createClause(IVecInt literals) throws ContradictionException {
      return AtLeast.atLeastNew(this.solver, this.getVocabulary(), literals, 1);
   }

   @Override
   public Constr createCardinalityConstraint(IVecInt literals, int degree) throws ContradictionException {
      return AtLeast.atLeastNew(this.solver, this.getVocabulary(), literals, degree);
   }

   @Override
   public Constr createUnregisteredCardinalityConstraint(IVecInt literals, int degree) {
      return new AtLeast(this.getVocabulary(), literals, degree);
   }
}
