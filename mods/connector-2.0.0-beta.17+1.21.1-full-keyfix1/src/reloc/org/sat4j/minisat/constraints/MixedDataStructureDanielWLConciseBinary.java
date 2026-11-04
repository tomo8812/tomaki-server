package reloc.org.sat4j.minisat.constraints;

import reloc.org.sat4j.minisat.constraints.card.AtLeast;
import reloc.org.sat4j.minisat.constraints.cnf.BinaryClauses;
import reloc.org.sat4j.minisat.constraints.cnf.Clauses;
import reloc.org.sat4j.minisat.constraints.cnf.LearntBinaryClause;
import reloc.org.sat4j.minisat.constraints.cnf.LearntWLClause;
import reloc.org.sat4j.minisat.constraints.cnf.Lits;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalWLClause;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClause;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;

public class MixedDataStructureDanielWLConciseBinary extends AbstractDataStructureFactory {
   private BinaryClauses[] binaryClauses;
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MixedDataStructureDanielWLConciseBinary.class.desiredAssertionStatus();

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
         return v.size() == 2 ? this.createConciseBinaryClause(v) : OriginalWLClause.brandNewClause(this.solver, this.getVocabulary(), v);
      }
   }

   private Constr createConciseBinaryClause(IVecInt literals) {
      assert literals.size() == 2;
      if (this.binaryClauses == null) {
         this.binaryClauses = new BinaryClauses[this.getVocabulary().nVars() * 2 + 2];
      } else if (this.binaryClauses.length < this.getVocabulary().nVars() * 2) {
         BinaryClauses[] newBinaryClauses = new BinaryClauses[this.getVocabulary().nVars() * 2 + 2];
         System.arraycopy(this.binaryClauses, 0, newBinaryClauses, 0, this.binaryClauses.length);
         this.binaryClauses = newBinaryClauses;
      }

      BinaryClauses first;
      if ((first = this.binaryClauses[literals.get(0)]) == null) {
         first = new BinaryClauses(this.getVocabulary(), literals.get(0));
         this.getVocabulary().watch(literals.get(0) ^ 1, first);
         this.binaryClauses[literals.get(0)] = first;
      }

      BinaryClauses second;
      if ((second = this.binaryClauses[literals.get(1)]) == null) {
         second = new BinaryClauses(this.getVocabulary(), literals.get(1));
         this.getVocabulary().watch(literals.get(1) ^ 1, second);
         this.binaryClauses[literals.get(1)] = second;
      }

      first.addBinaryClause(literals.get(1));
      second.addBinaryClause(literals.get(0));
      return first;
   }

   @Override
   public Constr createUnregisteredClause(IVecInt literals) {
      if (literals.size() == 1) {
         return new UnitClause(literals.last(), true);
      } else {
         return literals.size() == 2 ? new LearntBinaryClause(literals, this.getVocabulary()) : new LearntWLClause(literals, this.getVocabulary());
      }
   }

   @Override
   protected ILits createLits() {
      return new Lits();
   }
}
