package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.pb.constraints.pb.MinWatchPb;
import reloc.org.sat4j.pb.constraints.pb.MinWatchPbLong;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class MinLongWatchPBConstructor implements IPBConstructor {
   @Override
   public Constr constructLearntPB(ILits voc, IDataStructurePB dspb) {
      return dspb.isLongSufficient() ? MinWatchPbLong.normalizedWatchPbNew(voc, dspb) : MinWatchPb.normalizedWatchPbNew(voc, dspb);
   }

   @Override
   public Constr constructPB(UnitPropagationListener solver, ILits voc, int[] theLits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs) throws ContradictionException {
      Constr constr;
      if (sumCoefs.bitLength() < 64) {
         constr = MinWatchPbLong.normalizedMinWatchPbNew(solver, voc, theLits, coefs, degree, sumCoefs);
      } else {
         constr = MinWatchPb.normalizedMinWatchPbNew(solver, voc, theLits, coefs, degree, sumCoefs);
      }

      return constr == null ? Constr.TAUTOLOGY : constr;
   }
}
