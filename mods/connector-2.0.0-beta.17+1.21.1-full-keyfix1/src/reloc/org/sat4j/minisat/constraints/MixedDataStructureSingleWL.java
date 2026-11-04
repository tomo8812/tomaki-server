package reloc.org.sat4j.minisat.constraints;

import reloc.org.sat4j.minisat.constraints.card.AtLeast;
import reloc.org.sat4j.minisat.constraints.cnf.Clauses;
import reloc.org.sat4j.minisat.constraints.cnf.LearntWLClause;
import reloc.org.sat4j.minisat.constraints.cnf.Lits;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalBinaryClause;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalWLClause;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClause;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;

public class MixedDataStructureSingleWL extends AbstractDataStructureFactory {
   private static final long serialVersionUID = 1L;

   @Override
   public Constr createCardinalityConstraint(IVecInt literals, int degree) throws ContradictionException {
      return AtLeast.atLeastNew(this.solver, this.getVocabulary(), literals, degree);
   }

   @Override
   public Constr createUnregisteredCardinalityConstraint(IVecInt literals, int degree) {
      return new AtLeast(this.getVocabulary(), literals, degree);
   }

   @Override
   public Constr createClause(IVecInt literals) throws ContradictionException {
      IVecInt v = Clauses.sanityCheck(literals, this.getVocabulary(), this.solver);
      if (v == null) {
         return null;
      } else if (v.size() == 1) {
         return new UnitClause(v.last());
      } else {
         return v.size() == 2
            ? OriginalBinaryClause.brandNewClause(this.solver, this.getVocabulary(), v)
            : OriginalWLClause.brandNewClause(this.solver, this.getVocabulary(), v);
      }
   }

   @Override
   public Constr createUnregisteredClause(IVecInt literals) {
      return new LearntWLClause(literals, this.getVocabulary());
   }

   @Override
   protected ILits createLits() {
      return new Lits();
   }
}
