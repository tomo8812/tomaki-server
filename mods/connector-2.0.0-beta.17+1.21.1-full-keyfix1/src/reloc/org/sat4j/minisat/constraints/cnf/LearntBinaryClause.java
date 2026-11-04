package reloc.org.sat4j.minisat.constraints.cnf;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVecInt;

@Feature("constraint")
public class LearntBinaryClause extends BinaryClause {
   private static final long serialVersionUID = 1L;

   public LearntBinaryClause(IVecInt ps, ILits voc) {
      super(ps, voc);
   }

   @Override
   public void setLearnt() {
   }

   @Override
   public boolean learnt() {
      return true;
   }

   @Override
   public void forwardActivity(double claInc) {
   }

   @Override
   public void incActivity(double claInc) {
      this.activity += claInc;
   }

   @Override
   public void setActivity(double d) {
      this.activity = d;
   }
}
