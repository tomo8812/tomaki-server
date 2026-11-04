package reloc.org.sat4j.pb.lcds;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class WeightedLBDComputerStrategy extends LBDComputerStrategyDecorator {
   public WeightedLBDComputerStrategy(ILBDComputerStrategy decorated) {
      super(decorated);
   }

   @Override
   public int computeLBD(ILits voc, PBConstr constr, int propagated) {
      BigInteger lbd = BigInteger.valueOf(super.computeLBD(voc, constr, propagated));
      BigInteger weighted = lbd.multiply(constr.getDegree().multiply(lbd));
      return weighted.bitLength() < 32 ? weighted.intValue() : Integer.MAX_VALUE;
   }
}
