package reloc.org.sat4j.minisat.constraints.cnf;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;

@Feature("constraint")
public final class LearntWLClause extends WLClause {
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !LearntWLClause.class.desiredAssertionStatus();

   public LearntWLClause(IVecInt ps, ILits voc) {
      super(ps, voc);
   }

   @Override
   public void register() {
      if (this.lits.length != 0) {
         assert this.lits.length > 1;
         int maxi = 1;
         int maxlevel = this.voc.getLevel(this.lits[1]);

         for (int i = 2; i < this.lits.length; i++) {
            int level = this.voc.getLevel(this.lits[i]);
            if (level > maxlevel) {
               maxi = i;
               maxlevel = level;
            }
         }

         int l = this.lits[1];
         this.lits[1] = this.lits[maxi];
         this.lits[maxi] = l;
         this.voc.watch(this.lits[0] ^ 1, this);
         this.voc.watch(this.lits[1] ^ 1, this);
      }
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
   public boolean propagatePI(MandatoryLiteralListener s, int p) {
      this.voc.watch(p, this);
      return true;
   }
}
