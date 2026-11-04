package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.pb.constraints.pb.PuebloMinWatchPb;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class PuebloMinWatchPBConstructor implements IPBConstructor {
   @Override
   public Constr constructLearntPB(ILits voc, IDataStructurePB dspb) {
      return PuebloMinWatchPb.normalizedWatchPbNew(voc, dspb);
   }

   @Override
   public Constr constructPB(UnitPropagationListener solver, ILits voc, int[] theLits, BigInteger[] coefs, BigInteger degree, BigInteger sumCoefs) throws ContradictionException {
      Constr constr = PuebloMinWatchPb.normalizedMinWatchPbNew(solver, voc, theLits, coefs, degree);
      return constr == null ? Constr.TAUTOLOGY : constr;
   }
}
