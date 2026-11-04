package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.pb.constraints.pb.MapPb;
import reloc.org.sat4j.pb.constraints.pb.MaxWatchPb;
import reloc.org.sat4j.pb.constraints.pb.Pseudos;
import reloc.org.sat4j.pb.constraints.pb.WatchPb;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class PBMaxDataStructure extends AbstractPBDataStructureFactory {
   private static final long serialVersionUID = 1L;

   @Override
   protected Constr constraintFactory(int[] literals, BigInteger[] coefs, BigInteger degree) throws ContradictionException {
      return MaxWatchPb.normalizedMaxWatchPbNew(
         this.solver, this.getVocabulary(), literals, coefs, degree, AbstractPBClauseCardConstrDataStructure.sumOfCoefficients(coefs)
      );
   }

   protected WatchPb learntConstraintFactory(IDataStructurePB dspb) {
      return MaxWatchPb.normalizedWatchPbNew(this.getVocabulary(), dspb);
   }

   private Constr learntConstraintFactory(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree, boolean moreThan) {
      int[] lits = new int[literals.size()];
      literals.copyTo(lits);
      BigInteger[] bc = new BigInteger[coefs.size()];
      coefs.copyTo(bc);
      degree = Pseudos.niceCheckedParametersForCompetition(lits, bc, moreThan, degree);
      return MaxWatchPb.normalizedWatchPbNew(this.getVocabulary(), new MapPb(literals, coefs, degree));
   }

   @Override
   protected Constr learntAtLeastConstraintFactory(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      return this.learntConstraintFactory(literals, coefs, degree, true);
   }

   @Override
   protected Constr learntAtMostConstraintFactory(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      return this.learntConstraintFactory(literals, coefs, degree, false);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Constr learntConstraintFactory(IDataStructurePB var1) {
      return this.learntConstraintFactory(var1);
   }
}
