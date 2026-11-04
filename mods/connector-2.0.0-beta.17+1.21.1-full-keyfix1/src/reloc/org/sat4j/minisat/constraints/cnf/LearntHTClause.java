package reloc.org.sat4j.minisat.constraints.cnf;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;

@Feature("constraint")
public class LearntHTClause extends HTClause {
   private static final long serialVersionUID = 1L;

   public LearntHTClause(IVecInt ps, ILits voc) {
      super(ps, voc);
   }

   @Override
   public void register() {
      if (this.middleLits.length > 0) {
         int maxi = 0;
         int maxlevel = this.voc.getLevel(this.middleLits[0]);

         for (int i = 1; i < this.middleLits.length; i++) {
            int level = this.voc.getLevel(this.middleLits[i]);
            if (level > maxlevel) {
               maxi = i;
               maxlevel = level;
            }
         }

         if (maxlevel > this.voc.getLevel(this.tail)) {
            int l = this.tail;
            this.tail = this.middleLits[maxi];
            this.middleLits[maxi] = l;
         }
      }

      this.voc.watch(LiteralsUtils.neg(this.head), this);
      this.voc.watch(LiteralsUtils.neg(this.tail), this);
   }

   @Override
   public boolean learnt() {
      return true;
   }

   @Override
   public void setLearnt() {
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

   @Override
   public boolean propagatePI(MandatoryLiteralListener l, int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }
}
