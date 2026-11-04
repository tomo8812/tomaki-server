package reloc.org.sat4j.pb.constraints;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.LearntBinaryClausePB;
import reloc.org.sat4j.pb.constraints.pb.LearntHTClausePB;
import reloc.org.sat4j.pb.constraints.pb.OriginalBinaryClausePB;
import reloc.org.sat4j.pb.constraints.pb.OriginalHTClausePB;
import reloc.org.sat4j.pb.constraints.pb.UnitClausePB;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class UnitBinaryHTClausePBConstructor implements IClauseConstructor {
   @Override
   public Constr constructClause(UnitPropagationListener solver, ILits voc, IVecInt v) {
      if (v == null) {
         return Constr.TAUTOLOGY;
      } else if (v.size() == 1) {
         return new UnitClausePB(v.last(), voc);
      } else {
         return v.size() == 2 ? OriginalBinaryClausePB.brandNewClause(solver, voc, v) : OriginalHTClausePB.brandNewClause(solver, voc, v);
      }
   }

   @Override
   public Constr constructLearntClause(ILits voc, IVecInt literals) {
      if (literals.size() == 1) {
         return new UnitClausePB(literals.last(), true, voc);
      } else {
         return literals.size() == 2 ? new LearntBinaryClausePB(literals, voc) : new LearntHTClausePB(literals, voc);
      }
   }
}
