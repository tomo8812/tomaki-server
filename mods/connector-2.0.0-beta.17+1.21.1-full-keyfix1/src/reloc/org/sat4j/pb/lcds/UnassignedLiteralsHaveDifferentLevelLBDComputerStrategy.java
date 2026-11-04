package reloc.org.sat4j.pb.lcds;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class UnassignedLiteralsHaveDifferentLevelLBDComputerStrategy extends AbstractLBDComputerStrategy {
   @Override
   protected int unassignedLiteral(ILits voc, PBConstr constr, int i) {
      return 1;
   }

   @Override
   protected int assignedLiteral(ILits voc, PBConstr constr, int i) {
      return 1;
   }
}
