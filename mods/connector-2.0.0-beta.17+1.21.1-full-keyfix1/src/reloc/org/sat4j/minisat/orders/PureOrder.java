package reloc.org.sat4j.minisat.orders;

import reloc.org.sat4j.annotations.Feature;

@Feature(value = "varheuristics", parent = "expert")
public final class PureOrder extends VarOrderHeap {
   private static final long serialVersionUID = 1L;
   private int period;
   private int cpt;

   public PureOrder() {
      this(20);
   }

   public PureOrder(int p) {
      this.setPeriod(p);
   }

   public void setPeriod(int p) {
      this.period = p;
      this.cpt = this.period;
   }

   public int getPeriod() {
      return this.period;
   }

   @Override
   public int select() {
      if (this.cpt < this.period) {
         this.cpt++;
      } else {
         this.cpt = 0;
         int nblits = 2 * this.lits.nVars();

         for (int i = 2; i <= nblits; i++) {
            if (this.lits.isUnassigned(i) && this.lits.watches(i).size() > 0 && this.lits.watches(i ^ 1).size() == 0) {
               return i;
            }
         }
      }

      return super.select();
   }

   @Override
   public String toString() {
      return "tries to first branch on a single phase watched unassigned variable (pure literal if using a CB data structure) else VSIDS from MiniSAT";
   }
}
