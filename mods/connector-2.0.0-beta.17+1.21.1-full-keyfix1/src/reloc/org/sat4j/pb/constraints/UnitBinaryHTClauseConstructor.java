package reloc.org.sat4j.pb.constraints;

import reloc.org.sat4j.minisat.constraints.cnf.LearntBinaryClause;
import reloc.org.sat4j.minisat.constraints.cnf.LearntHTClause;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalBinaryClause;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalHTClause;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClause;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class UnitBinaryHTClauseConstructor implements IClauseConstructor {
   @Override
   public Constr constructClause(UnitPropagationListener solver, ILits voc, IVecInt v) {
      if (v == null) {
         return Constr.TAUTOLOGY;
      } else if (v.size() == 1) {
         return new UnitClause(v.last());
      } else {
         return v.size() == 2 ? OriginalBinaryClause.brandNewClause(solver, voc, v) : OriginalHTClause.brandNewClause(solver, voc, v);
      }
   }

   @Override
   public Constr constructLearntClause(ILits voc, IVecInt literals) {
      if (literals.size() == 1) {
         return new UnitClause(literals.last(), true);
      } else {
         return literals.size() == 2 ? new LearntBinaryClause(literals, voc) : new LearntHTClause(literals, voc);
      }
   }
}
