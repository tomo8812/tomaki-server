package reloc.org.sat4j.pb.lcds;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class RatioCoefficientsDegreeLBDComputerStrategy implements ILBDComputerStrategy {
   @Override
   public void init(int howmany) {
   }

   @Override
   public int computeLBD(ILits voc, PBConstr constr, int propagated) {
      BigInteger sum = constr.getSumCoefs();
      BigInteger degree = constr.getDegree();
      BigInteger ratio = sum.divide(degree);
      return ratio.bitLength() < 32 ? ratio.intValue() : Integer.MAX_VALUE;
   }
}
