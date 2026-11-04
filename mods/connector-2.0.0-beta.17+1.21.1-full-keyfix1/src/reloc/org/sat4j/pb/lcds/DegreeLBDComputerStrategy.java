package reloc.org.sat4j.pb.lcds;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class DegreeLBDComputerStrategy implements ILBDComputerStrategy {
   @Override
   public void init(int howmany) {
   }

   @Override
   public int computeLBD(ILits voc, PBConstr constr, int propagated) {
      BigInteger degree = constr.getDegree();
      return degree.bitLength() < 32 ? degree.intValue() : Integer.MAX_VALUE;
   }
}
