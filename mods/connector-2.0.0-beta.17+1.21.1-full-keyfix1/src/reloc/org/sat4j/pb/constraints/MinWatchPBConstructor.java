package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.pb.constraints.pb.MinWatchPb;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class MinWatchPBConstructor implements IPBConstructor {
   @Override
   public Constr constructLearntPB(ILits voc, IDataStructurePB dspb) {
      Constr constr = MinWatchPb.normalizedWatchPbNew(voc, dspb);
      return constr == null ? Constr.TAUTOLOGY : constr;
   }

   @Override
   public Constr constructPB(UnitPropagationListener solver, ILits voc, int[] theLits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs) throws ContradictionException {
      return MinWatchPb.normalizedMinWatchPbNew(solver, voc, theLits, coefs, degree, sumCoefs);
   }
}
