package reloc.org.sat4j.minisat.constraints.cnf;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

@Feature("constraint")
public class OriginalBinaryClause extends BinaryClause {
   private static final long serialVersionUID = 1L;

   public OriginalBinaryClause(IVecInt ps, ILits voc) {
      super(ps, voc);
   }

   @Override
   public void setLearnt() {
   }

   @Override
   public boolean learnt() {
      return false;
   }

   public static OriginalBinaryClause brandNewClause(UnitPropagationListener s, ILits voc, IVecInt literals) {
      OriginalBinaryClause c = new OriginalBinaryClause(literals, voc);
      c.register();
      return c;
   }

   @Override
   public void forwardActivity(double claInc) {
      this.activity += claInc;
   }

   @Override
   public void incActivity(double claInc) {
   }

   @Override
   public void setActivity(double claInc) {
   }
}
