package reloc.org.sat4j.pb.constraints;

import reloc.org.sat4j.minisat.constraints.cnf.BinaryClauses;
import reloc.org.sat4j.minisat.constraints.cnf.LearntBinaryClause;
import reloc.org.sat4j.minisat.constraints.cnf.LearntWLClause;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalWLClause;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClause;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class UnitConciseBinaryWLClauseConstructor implements IClauseConstructor {
   private BinaryClauses[] binaryClauses;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !UnitConciseBinaryWLClauseConstructor.class.desiredAssertionStatus();

   @Override
   public Constr constructClause(UnitPropagationListener solver, ILits voc, IVecInt v) {
      if (v == null) {
         return Constr.TAUTOLOGY;
      } else if (v.size() == 1) {
         return new UnitClause(v.last());
      } else {
         return v.size() == 2 ? this.createConciseBinaryClause(v, voc) : OriginalWLClause.brandNewClause(solver, voc, v);
      }
   }

   private Constr createConciseBinaryClause(IVecInt literals, ILits voc) {
      assert literals.size() == 2;
      if (this.binaryClauses == null) {
         this.binaryClauses = new BinaryClauses[voc.nVars() * 2 + 2];
      } else if (this.binaryClauses.length < voc.nVars() * 2 + 1) {
         BinaryClauses[] newBinaryClauses = new BinaryClauses[voc.nVars() * 2 + 2];
         System.arraycopy(this.binaryClauses, 0, newBinaryClauses, 0, this.binaryClauses.length);
         this.binaryClauses = newBinaryClauses;
      }

      BinaryClauses first;
      if ((first = this.binaryClauses[literals.get(0)]) == null) {
         first = new BinaryClauses(voc, literals.get(0));
         voc.watch(literals.get(0) ^ 1, first);
         this.binaryClauses[literals.get(0)] = first;
      }

      BinaryClauses second;
      if ((second = this.binaryClauses[literals.get(1)]) == null) {
         second = new BinaryClauses(voc, literals.get(1));
         voc.watch(literals.get(1) ^ 1, second);
         this.binaryClauses[literals.get(1)] = second;
      }

      first.addBinaryClause(literals.get(1));
      second.addBinaryClause(literals.get(0));
      return first;
   }

   @Override
   public Constr constructLearntClause(ILits voc, IVecInt literals) {
      if (literals.size() == 1) {
         return new UnitClause(literals.last(), true);
      } else {
         return literals.size() == 2 ? new LearntBinaryClause(literals, voc) : new LearntWLClause(literals, voc);
      }
   }
}
