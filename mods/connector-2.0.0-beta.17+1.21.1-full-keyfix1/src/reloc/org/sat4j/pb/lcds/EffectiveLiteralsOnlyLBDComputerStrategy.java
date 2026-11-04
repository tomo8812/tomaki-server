package reloc.org.sat4j.pb.lcds;

import java.math.BigInteger;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class EffectiveLiteralsOnlyLBDComputerStrategy extends AbstractLBDComputerStrategy {
   private int currentPropagated;
   private BigInteger degree;
   private final IVecInt lbdCandidates = new VecInt();

   @Override
   protected void startComputeLBD(PBConstr constr, int propagated) {
      this.degree = constr.getDegree();
      this.currentPropagated = propagated;
      this.lbdCandidates.clear();
   }

   @Override
   protected int unassignedLiteral(ILits voc, PBConstr constr, int i) {
      this.degree = this.degree.subtract(constr.getCoef(i));
      return 0;
   }

   @Override
   protected int assignedLiteral(ILits voc, PBConstr constr, int i) {
      if (constr.get(i) == this.currentPropagated) {
         return 0;
      } else if (voc.isSatisfied(constr.get(i))) {
         this.degree = this.degree.subtract(constr.getCoef(i));
         return 0;
      } else {
         this.lbdCandidates.push(i);
         return 1;
      }
   }

   @Override
   protected int fixLbd(PBConstr constr, int lbd) {
      int newLbd = 0;

      for (IteratorInt it = this.lbdCandidates.iterator(); it.hasNext(); newLbd++) {
         int v = it.next();
         if (constr.getCoef(v).compareTo(this.degree) < 0) {
            break;
         }
      }

      return newLbd;
   }
}
