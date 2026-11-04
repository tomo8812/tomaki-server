package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.pb.constraints.pb.MinWatchCardPB;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class MinCardPBConstructor implements ICardConstructor {
   @Override
   public Constr constructCard(UnitPropagationListener solver, ILits voc, IVecInt theLits, int degree) throws ContradictionException {
      Constr constr = MinWatchCardPB.minWatchCardPBNew(solver, voc, theLits, true, degree);
      return constr == null ? Constr.TAUTOLOGY : constr;
   }

   @Override
   public Constr constructLearntCard(ILits voc, IDataStructurePB dspb) {
      IVecInt resLits = new VecInt();
      IVec<BigInteger> resCoefs = new Vec<>();
      dspb.buildConstraintFromConflict(resLits, resCoefs);
      dspb.isCardinality();
      return new MinWatchCardPB(voc, resLits, true, dspb.getCardDegree().intValue());
   }
}
