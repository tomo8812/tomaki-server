package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.pb.constraints.pb.MaxWatchPb;
import reloc.org.sat4j.pb.constraints.pb.MaxWatchPbLong;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class MaxLongWatchPBConstructor implements IPBConstructor {
   @Override
   public Constr constructLearntPB(ILits voc, IDataStructurePB dspb) {
      return dspb.isLongSufficient() ? MaxWatchPbLong.normalizedWatchPbNew(voc, dspb) : MaxWatchPb.normalizedWatchPbNew(voc, dspb);
   }

   @Override
   public Constr constructPB(UnitPropagationListener solver, ILits voc, int[] theLits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs) throws ContradictionException {
      Constr constr;
      if (sumCoefs.bitLength() < 64) {
         constr = MaxWatchPbLong.normalizedMaxWatchPbNew(solver, voc, theLits, coefs, degree, sumCoefs);
      } else {
         constr = MaxWatchPb.normalizedMaxWatchPbNew(solver, voc, theLits, coefs, degree, sumCoefs);
      }

      return constr == null ? Constr.TAUTOLOGY : constr;
   }
}
