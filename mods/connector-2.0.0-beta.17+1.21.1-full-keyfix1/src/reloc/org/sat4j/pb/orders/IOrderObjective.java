package reloc.org.sat4j.pb.orders;

import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.pb.ObjectiveFunction;

public interface IOrderObjective extends IOrder {
   void setObjectiveFunction(ObjectiveFunction var1);
}
