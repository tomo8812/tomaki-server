package reloc.org.sat4j.minisat.constraints;

import reloc.org.sat4j.minisat.constraints.card.MinWatchCard;
import reloc.org.sat4j.minisat.constraints.cnf.LearntWLClause;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;

public class CardinalityDataStructureYanMin extends AbstractCardinalityDataStructure {
   private static final long serialVersionUID = 1L;

   @Override
   public Constr createClause(IVecInt literals) throws ContradictionException {
      return MinWatchCard.minWatchCardNew(this.solver, this.getVocabulary(), literals, true, 1);
   }

   @Override
   public Constr createUnregisteredClause(IVecInt literals) {
      return new LearntWLClause(literals, this.getVocabulary());
   }

   @Override
   public Constr createCardinalityConstraint(IVecInt literals, int degree) throws ContradictionException {
      return MinWatchCard.minWatchCardNew(this.solver, this.getVocabulary(), literals, true, degree);
   }

   @Override
   public Constr createUnregisteredCardinalityConstraint(IVecInt literals, int degree) {
      return new MinWatchCard(this.getVocabulary(), literals, true, degree);
   }
}
