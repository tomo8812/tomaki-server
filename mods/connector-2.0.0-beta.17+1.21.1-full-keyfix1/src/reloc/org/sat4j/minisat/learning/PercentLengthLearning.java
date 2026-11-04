package reloc.org.sat4j.minisat.learning;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "learning", parent = "expert")
public final class PercentLengthLearning<D extends DataStructureFactory> extends LimitedLearning<D> {
   private static final long serialVersionUID = 1L;
   private int maxpercent;
   private int bound;

   public PercentLengthLearning() {
      this(10);
   }

   public PercentLengthLearning(int percent) {
      this.maxpercent = percent;
   }

   public void setLimit(int percent) {
      this.maxpercent = percent;
   }

   public int getLimit() {
      return this.maxpercent;
   }

   @Override
   public void init() {
      super.init();
      this.setBound(this.lits.realnVars() * this.maxpercent / 100);
   }

   @Override
   public String toString() {
      return "Limit learning to clauses of size smaller or equal to " + this.maxpercent + "% of the number of variables";
   }

   protected void setBound(int newbound) {
      this.bound = newbound;
   }

   @Override
   protected boolean learningCondition(Constr constr) {
      return constr.size() <= this.bound;
   }
}
