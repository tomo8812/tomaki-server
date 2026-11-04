package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.constraints.cnf.Clauses;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.pb.constraints.pb.MapPb;
import reloc.org.sat4j.pb.constraints.pb.Pseudos;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public abstract class AbstractPBClauseCardConstrDataStructure extends AbstractPBDataStructureFactory {
   private static final long serialVersionUID = 1L;
   static final BigInteger MAX_INT_VALUE = BigInteger.valueOf(2147483647L);
   private final IPBConstructor ipbc;
   private final ICardConstructor icardc;
   private final IClauseConstructor iclausec;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AbstractPBClauseCardConstrDataStructure.class.desiredAssertionStatus();

   AbstractPBClauseCardConstrDataStructure(IClauseConstructor iclausec, ICardConstructor icardc, IPBConstructor ipbc) {
      this.iclausec = iclausec;
      this.icardc = icardc;
      this.ipbc = ipbc;
   }

   @Override
   public Constr createClause(IVecInt literals) throws ContradictionException {
      IVecInt v = Clauses.sanityCheck(literals, this.getVocabulary(), this.solver);
      return this.constructClause(v);
   }

   @Override
   public Constr createUnregisteredClause(IVecInt literals) {
      return this.constructLearntClause(literals);
   }

   @Override
   public Constr createCardinalityConstraint(IVecInt literals, int degree) throws ContradictionException {
      return this.constructCard(literals, degree);
   }

   @Override
   public Constr createUnregisteredCardinalityConstraint(IVecInt literals, int degree) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Constr constraintFactory(int[] literals, BigInteger[] coefs, BigInteger degree) throws ContradictionException {
      if (literals.length == 0 && degree.signum() <= 0) {
         return Constr.TAUTOLOGY;
      } else if (degree.equals(BigInteger.ONE)) {
         IVecInt v = Clauses.sanityCheck(new VecInt(literals), this.getVocabulary(), this.solver);
         return v == Constr.TAUTOLOGY ? null : this.constructClause(v);
      } else if (coefficientsEqualTo(BigInteger.ONE, coefs)) {
         assert degree.compareTo(MAX_INT_VALUE) < 0;
         return this.constructCard(new VecInt(literals), degree.intValue());
      } else {
         return this.constructPB(literals, coefs, degree);
      }
   }

   @Override
   protected Constr learntConstraintFactory(IDataStructurePB dspb) {
      if (dspb.getDegree().equals(BigInteger.ONE)) {
         IVecInt literals = new VecInt();
         IVec<BigInteger> resCoefs = new Vec<>();
         dspb.buildConstraintFromConflict(literals, resCoefs);
         int indLit = dspb.getAssertiveLiteral();
         if (indLit > -1) {
            int tmp = literals.get(indLit);
            literals.set(indLit, literals.get(0));
            literals.set(0, tmp);
            BigInteger coeftmp = resCoefs.get(indLit);
            resCoefs.set(indLit, resCoefs.get(0));
            resCoefs.set(0, coeftmp);
         }

         return this.constructLearntClause(literals);
      } else {
         return dspb.isCardinality() ? this.constructLearntCard(dspb) : this.constructLearntPB(dspb);
      }
   }

   private Constr learntConstraintFactory(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree, boolean moreThan) {
      int[] lits = new int[literals.size()];
      literals.copyTo(lits);
      BigInteger[] bc = new BigInteger[coefs.size()];
      coefs.copyTo(bc);
      degree = Pseudos.niceCheckedParametersForCompetition(lits, bc, moreThan, degree);
      if (degree.equals(BigInteger.ONE)) {
         return this.constructLearntClause(new VecInt(lits));
      }

      if (coefficientsEqualTo(BigInteger.ONE, bc)) {
         return this.constructLearntCard(new VecInt(lits), new Vec<>(bc), degree);
      }

      if (coefficientsEqualTo(bc[0], bc)) {
         BigInteger[] division = degree.divideAndRemainder(bc[0]);
         if (!division[1].equals(BigInteger.ZERO)) {
            division[0] = division[0].add(BigInteger.ONE);
         }

         return this.constructLearntCard(new VecInt(lits), new Vec<>(bc), division[0]);
      } else {
         return this.constructLearntPB(new VecInt(lits), new Vec<>(bc), degree);
      }
   }

   @Override
   protected Constr learntAtLeastConstraintFactory(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      return this.learntConstraintFactory(literals, coefs, degree, true);
   }

   @Override
   protected Constr learntAtMostConstraintFactory(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      return this.learntConstraintFactory(literals, coefs, degree, false);
   }

   static boolean coefficientsEqualTo(BigInteger value, BigInteger[] coefs) {
      for (int i = 0; i < coefs.length; i++) {
         if (!coefs[i].equals(value)) {
            return false;
         }
      }

      return true;
   }

   protected Constr constructClause(IVecInt v) {
      return this.iclausec.constructClause(this.solver, this.getVocabulary(), v);
   }

   protected Constr constructCard(IVecInt theLits, int degree) throws ContradictionException {
      return this.icardc.constructCard(this.solver, this.getVocabulary(), theLits, degree);
   }

   protected Constr constructPB(int[] theLits, BigInteger[] coefs, BigInteger degree) throws ContradictionException {
      return this.ipbc.constructPB(this.solver, this.getVocabulary(), theLits, coefs, degree, sumOfCoefficients(coefs));
   }

   protected Constr constructLearntClause(IVecInt literals) {
      return this.iclausec.constructLearntClause(this.getVocabulary(), literals);
   }

   protected Constr constructLearntCard(IDataStructurePB dspb) {
      return this.icardc.constructLearntCard(this.getVocabulary(), dspb);
   }

   protected Constr constructLearntCard(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      return this.icardc.constructLearntCard(this.getVocabulary(), new MapPb(literals, coefs, degree));
   }

   protected Constr constructLearntPB(IDataStructurePB dspb) {
      return this.ipbc.constructLearntPB(this.getVocabulary(), dspb);
   }

   protected Constr constructLearntPB(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      return this.ipbc.constructLearntPB(this.getVocabulary(), new MapPb(literals, coefs, degree));
   }

   public static final BigInteger sumOfCoefficients(BigInteger[] coefs) {
      BigInteger sumCoefs = BigInteger.ZERO;

      for (BigInteger c : coefs) {
         sumCoefs = sumCoefs.add(c);
      }

      return sumCoefs;
   }
}
