package reloc.org.sat4j.pb.orders;

import java.math.BigInteger;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class BumperEffective implements IBumper {
   private BigInteger constrDegree;
   private final IVecInt bumpableCandidates = new VecInt();
   private BumpStrategy bumpStrategy;

   @Override
   public void varBumpActivity(ILits voc, BumpStrategy bStrategy, IOrder order, PBConstr constr, int lit, int propagated) {
      if (lit == 0) {
         this.constrDegree = constr.getDegree();
         this.bumpableCandidates.clear();
         this.bumpStrategy = bStrategy;
      }

      if (lit == propagated) {
         bStrategy.varBumpActivity(order, constr, lit);
      } else if (!voc.isFalsified(constr.get(lit))) {
         this.constrDegree = this.constrDegree.subtract(constr.getCoef(lit));
      } else {
         this.bumpableCandidates.push(lit);
      }
   }

   @Override
   public void postBumpActivity(IOrder order, PBConstr constr) {
      IteratorInt it = this.bumpableCandidates.iterator();

      while (it.hasNext()) {
         int v = it.next();
         if (constr.getCoef(v).compareTo(this.constrDegree) < 0) {
            break;
         }

         this.bumpStrategy.varBumpActivity(order, constr, v);
      }
   }

   @Override
   public String toString() {
      return "EFFECTIVE";
   }
}
