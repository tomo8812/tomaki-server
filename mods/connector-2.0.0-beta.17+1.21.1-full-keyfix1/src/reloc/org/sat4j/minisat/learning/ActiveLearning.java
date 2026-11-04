package reloc.org.sat4j.minisat.learning;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "learning", parent = "expert")
public final class ActiveLearning<D extends DataStructureFactory> extends LimitedLearning<D> {
   private static final long serialVersionUID = 1L;
   private double percent;
   private IOrder order;
   private int maxpercent;

   public ActiveLearning() {
      this(0.95);
   }

   public ActiveLearning(double d) {
      this.percent = d;
   }

   public void setOrder(IOrder order) {
      this.order = order;
   }

   @Override
   public void setSolver(Solver<D> s) {
      super.setSolver(s);
      this.order = s.getOrder();
   }

   public void setActivityPercent(double d) {
      this.percent = d;
   }

   public double getActivityPercent() {
      return this.percent;
   }

   @Override
   protected boolean learningCondition(Constr clause) {
      int nbactivevars = 0;

      for (int i = 0; i < clause.size(); i++) {
         if (this.order.varActivity(clause.get(i)) > 1.0) {
            nbactivevars++;
         }
      }

      return nbactivevars > clause.size() * this.percent;
   }

   @Override
   public String toString() {
      return "Limit learning to clauses containing active literals (" + this.percent * 100.0 + "%)";
   }

   public void setLimit(int percent) {
      this.maxpercent = percent;
   }

   public int getLimit() {
      return this.maxpercent;
   }
}
