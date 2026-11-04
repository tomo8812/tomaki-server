package reloc.org.sat4j.pb.orders;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class DoubleBumpClashingLiteralsDecorator implements IBumper {
   private final IBumper decorated;

   public DoubleBumpClashingLiteralsDecorator(IBumper decorated) {
      this.decorated = decorated;
   }

   @Override
   public void varBumpActivity(ILits voc, BumpStrategy bumpStrategy, IOrder order, PBConstr constr, int i, int propagated) {
      this.decorated.varBumpActivity(voc, bumpStrategy, order, constr, i, propagated);
      bumpStrategy.varBumpActivity(order, constr, i);
   }

   @Override
   public void postBumpActivity(IOrder order, PBConstr constr) {
      this.decorated.postBumpActivity(order, constr);
   }
}
