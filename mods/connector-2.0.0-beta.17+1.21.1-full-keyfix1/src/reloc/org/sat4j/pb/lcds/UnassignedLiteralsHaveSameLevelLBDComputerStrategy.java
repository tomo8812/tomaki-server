package reloc.org.sat4j.pb.lcds;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class UnassignedLiteralsHaveSameLevelLBDComputerStrategy extends AbstractLBDComputerStrategy {
   private boolean unassignedFound;

   @Override
   protected void startComputeLBD(PBConstr constr, int propagated) {
      this.unassignedFound = false;
   }

   @Override
   protected int unassignedLiteral(ILits voc, PBConstr constr, int i) {
      if (this.unassignedFound) {
         return 0;
      }

      this.unassignedFound = true;
      return 1;
   }

   @Override
   protected int assignedLiteral(ILits voc, PBConstr constr, int i) {
      return 1;
   }
}
