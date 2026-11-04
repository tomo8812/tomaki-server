package reloc.org.sat4j.pb.orders;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public enum Bumper implements IBumper {
   ANY {
      @Override
      boolean isBumpable(ILits voc, int i, int p) {
         return true;
      }
   },
   ASSIGNED {
      @Override
      boolean isBumpable(ILits voc, int i, int p) {
         return !voc.isUnassigned(i);
      }
   },
   FALSIFIED {
      @Override
      boolean isBumpable(ILits voc, int i, int p) {
         return voc.isFalsified(i);
      }
   },
   FALSIFIED_AND_PROPAGATED {
      @Override
      boolean isBumpable(ILits voc, int i, int p) {
         return voc.isFalsified(i) || voc.getLevel(i) == voc.getLevel(p);
      }
   };

   Bumper() {
   }

   @Override
   public void varBumpActivity(ILits voc, BumpStrategy bumpStrategy, IOrder order, PBConstr constr, int i, int propagated) {
      if (this.isBumpable(voc, constr.get(i), constr.get(propagated))) {
         bumpStrategy.varBumpActivity(order, constr, i);
      }
   }

   @Override
   public void postBumpActivity(IOrder order, PBConstr constr) {
   }

   abstract boolean isBumpable(ILits var1, int var2, int var3);
}
