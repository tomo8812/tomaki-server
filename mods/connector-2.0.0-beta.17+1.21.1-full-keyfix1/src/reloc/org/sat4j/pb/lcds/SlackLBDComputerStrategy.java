package reloc.org.sat4j.pb.lcds;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class SlackLBDComputerStrategy implements ILBDComputerStrategy {
   @Override
   public void init(int howmany) {
   }

   @Override
   public int computeLBD(ILits voc, PBConstr constr, int propagated) {
      BigInteger slack = constr.getSumCoefs().subtract(constr.getDegree());
      return slack.bitLength() < 32 ? slack.intValue() : Integer.MAX_VALUE;
   }
}
