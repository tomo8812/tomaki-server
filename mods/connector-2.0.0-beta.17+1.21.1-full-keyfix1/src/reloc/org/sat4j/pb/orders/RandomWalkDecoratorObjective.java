package reloc.org.sat4j.pb.orders;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.orders.RandomWalkDecorator;
import reloc.org.sat4j.pb.ObjectiveFunction;

@Feature(value = "varheuristics", parent = "expert")
public class RandomWalkDecoratorObjective extends RandomWalkDecorator implements IOrderObjective {
   private static final long serialVersionUID = 1L;
   private final IOrderObjective objorder;

   public RandomWalkDecoratorObjective(VarOrderHeapObjective order, double p) {
      super(order, p);
      this.objorder = order;
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      this.objorder.setObjectiveFunction(obj);
   }
}
