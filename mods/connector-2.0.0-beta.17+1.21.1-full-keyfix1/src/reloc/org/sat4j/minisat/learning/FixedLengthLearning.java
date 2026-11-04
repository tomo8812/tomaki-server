package reloc.org.sat4j.minisat.learning;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "learning", parent = "expert")
public final class FixedLengthLearning<D extends DataStructureFactory> extends LimitedLearning<D> {
   private static final long serialVersionUID = 1L;
   private int maxlength;
   private int bound;

   public FixedLengthLearning() {
      this(3);
   }

   public FixedLengthLearning(int maxlength) {
      this.maxlength = maxlength;
   }

   @Override
   public void init() {
      this.setBound(this.maxlength);
   }

   public void setMaxLength(int v) {
      this.maxlength = v;
   }

   public int getMaxLength() {
      return this.maxlength;
   }

   @Override
   public String toString() {
      return "Limit learning to clauses of size smaller or equal to " + this.maxlength;
   }

   protected void setBound(int newbound) {
      this.bound = newbound;
   }

   @Override
   protected boolean learningCondition(Constr constr) {
      return constr.size() <= this.bound;
   }
}
