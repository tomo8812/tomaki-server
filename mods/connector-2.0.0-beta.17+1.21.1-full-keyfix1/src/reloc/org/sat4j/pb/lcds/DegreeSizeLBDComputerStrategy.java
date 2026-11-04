package reloc.org.sat4j.pb.lcds;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class DegreeSizeLBDComputerStrategy implements ILBDComputerStrategy {
   @Override
   public void init(int howmany) {
   }

   @Override
   public int computeLBD(ILits voc, PBConstr constr, int propagated) {
      return constr.getDegree().bitLength();
   }
}
