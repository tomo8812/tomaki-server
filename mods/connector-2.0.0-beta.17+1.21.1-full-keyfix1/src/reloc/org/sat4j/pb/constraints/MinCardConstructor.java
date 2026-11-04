package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.constraints.card.MinWatchCard;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public class MinCardConstructor implements ICardConstructor {
   @Override
   public Constr constructCard(UnitPropagationListener solver, ILits voc, IVecInt theLits, int degree) throws ContradictionException {
      Constr constr = MinWatchCard.minWatchCardNew(solver, voc, theLits, true, degree);
      return constr == null ? Constr.TAUTOLOGY : constr;
   }

   @Override
   public Constr constructLearntCard(ILits voc, IDataStructurePB dspb) {
      IVecInt resLits = new VecInt();
      IVec<BigInteger> resCoefs = new Vec<>();
      dspb.buildConstraintFromConflict(resLits, resCoefs);
      return new MinWatchCard(voc, resLits, true, dspb.getDegree().intValue());
   }
}
