package reloc.org.sat4j.pb.constraints;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.constraints.AbstractDataStructureFactory;
import reloc.org.sat4j.minisat.constraints.card.AtLeast;
import reloc.org.sat4j.minisat.constraints.cnf.Clauses;
import reloc.org.sat4j.minisat.constraints.cnf.LearntBinaryClause;
import reloc.org.sat4j.minisat.constraints.cnf.LearntHTClause;
import reloc.org.sat4j.minisat.constraints.cnf.Lits;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalBinaryClause;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalHTClause;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClause;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.AtLeastPB;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.pb.constraints.pb.Pseudos;
import reloc.org.sat4j.pb.core.PBDataStructureFactory;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public abstract class AbstractPBDataStructureFactory extends AbstractDataStructureFactory implements PBDataStructureFactory {
   public static final AbstractPBDataStructureFactory.INormalizer FOR_COMPETITION = new AbstractPBDataStructureFactory.INormalizer() {
      @Override
      public PBContainer nice(IVecInt literals, IVec<BigInteger> coefs, boolean moreThan, BigInteger degree, ILits voc) throws ContradictionException {
         if (literals.size() != coefs.size()) {
            throw new IllegalArgumentException("Number of coeff and literals are different!!!");
         }

         IVecInt cliterals = new VecInt(literals.size());
         literals.copyTo(cliterals);
         IVec<BigInteger> ccoefs = new Vec<>(literals.size());
         coefs.copyTo(ccoefs);
         int i = 0;

         while (i < cliterals.size()) {
            if (ccoefs.get(i).equals(BigInteger.ZERO)) {
               cliterals.delete(i);
               ccoefs.delete(i);
            } else if (voc.isSatisfied(cliterals.get(i))) {
               degree = degree.subtract(ccoefs.get(i));
               cliterals.delete(i);
               ccoefs.delete(i);
            } else if (voc.isFalsified(cliterals.get(i))) {
               cliterals.delete(i);
               ccoefs.delete(i);
            } else {
               i++;
            }
         }

         int[] theLits = new int[cliterals.size()];
         cliterals.copyTo(theLits);
         BigInteger[] normCoefs = new BigInteger[ccoefs.size()];
         ccoefs.copyTo(normCoefs);
         BigInteger degRes = Pseudos.niceParametersForCompetition(theLits, normCoefs, moreThan, degree);
         return new PBContainer(theLits, normCoefs, degRes);
      }
   };
   public static final AbstractPBDataStructureFactory.INormalizer NO_COMPETITION = new AbstractPBDataStructureFactory.INormalizer() {
      @Override
      public PBContainer nice(IVecInt literals, IVec<BigInteger> coefs, boolean moreThan, BigInteger degree, ILits voc) throws ContradictionException {
         IVecInt cliterals = new VecInt(literals.size());
         literals.copyTo(cliterals);
         IVec<BigInteger> ccoefs = new Vec<>(literals.size());
         coefs.copyTo(ccoefs);
         int i = 0;

         while (i < cliterals.size()) {
            if (voc.isSatisfied(cliterals.get(i))) {
               degree = degree.subtract(ccoefs.get(i));
               cliterals.delete(i);
               ccoefs.delete(i);
            } else if (voc.isFalsified(cliterals.get(i))) {
               cliterals.delete(i);
               ccoefs.delete(i);
            } else {
               i++;
            }
         }

         IDataStructurePB res = Pseudos.niceParameters(cliterals, ccoefs, moreThan, degree, voc);
         int size = res.size();
         int[] theLits = new int[size];
         BigInteger[] theCoefs = new BigInteger[size];
         res.buildConstraintFromMapPb(theLits, theCoefs);
         BigInteger theDegree = res.getDegree();
         return new PBContainer(theLits, theCoefs, theDegree);
      }
   };
   private AbstractPBDataStructureFactory.INormalizer norm = FOR_COMPETITION;
   private static final long serialVersionUID = 1L;

   protected AbstractPBDataStructureFactory.INormalizer getNormalizer() {
      return this.norm;
   }

   public void setNormalizer(String simp) {
      try {
         Field f = AbstractPBDataStructureFactory.class.getDeclaredField(simp);
         this.norm = (AbstractPBDataStructureFactory.INormalizer)f.get(this);
      } catch (Exception e) {
         Logger.getLogger("reloc.org.sat4j.pb").log(Level.INFO, "Issue when setting normalizer, using competition one", e);
         this.norm = FOR_COMPETITION;
      }
   }

   public void setNormalizer(AbstractPBDataStructureFactory.INormalizer normalizer) {
      this.norm = normalizer;
   }

   @Override
   public Constr createClause(IVecInt literals) throws ContradictionException {
      IVecInt v = Clauses.sanityCheck(literals, this.getVocabulary(), this.solver);
      if (v == null) {
         return null;
      } else if (v.size() == 1) {
         return new UnitClause(v.last());
      } else {
         return v.size() == 2
            ? OriginalBinaryClause.brandNewClause(this.solver, this.getVocabulary(), v)
            : OriginalHTClause.brandNewClause(this.solver, this.getVocabulary(), v);
      }
   }

   @Override
   public Constr createUnregisteredClause(IVecInt literals) {
      if (literals.size() == 1) {
         return new UnitClause(literals.last(), true);
      } else {
         return literals.size() == 2 ? new LearntBinaryClause(literals, this.getVocabulary()) : new LearntHTClause(literals, this.getVocabulary());
      }
   }

   @Override
   public Constr createCardinalityConstraint(IVecInt literals, int degree) throws ContradictionException {
      return AtLeastPB.atLeastNew(this.solver, this.getVocabulary(), literals, degree);
   }

   @Override
   public Constr createPseudoBooleanConstraint(IVecInt literals, IVec<BigInteger> coefs, boolean moreThan, BigInteger degree) throws ContradictionException {
      PBContainer res = this.getNormalizer().nice(literals, coefs, moreThan, degree, this.getVocabulary());
      return this.constraintFactory(res.lits, res.coefs, res.degree);
   }

   @Override
   public Constr createAtMostPBConstraint(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) throws ContradictionException {
      return this.createPseudoBooleanConstraint(literals, coefs, false, degree);
   }

   @Override
   public Constr createAtLeastPBConstraint(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) throws ContradictionException {
      return this.createPseudoBooleanConstraint(literals, coefs, true, degree);
   }

   @Override
   public Constr createUnregisteredPseudoBooleanConstraint(IDataStructurePB dspb) {
      return this.learntConstraintFactory(dspb);
   }

   @Override
   public Constr createUnregisteredAtLeastConstraint(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      return this.learntAtLeastConstraintFactory(literals, coefs, degree);
   }

   @Override
   public Constr createUnregisteredAtMostConstraint(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      return this.learntAtMostConstraintFactory(literals, coefs, degree);
   }

   protected abstract Constr constraintFactory(int[] var1, BigInteger[] var2, BigInteger var3) throws ContradictionException;

   protected abstract Constr learntConstraintFactory(IDataStructurePB var1);

   protected abstract Constr learntAtLeastConstraintFactory(IVecInt var1, IVec<BigInteger> var2, BigInteger var3);

   protected abstract Constr learntAtMostConstraintFactory(IVecInt var1, IVec<BigInteger> var2, BigInteger var3);

   @Override
   protected ILits createLits() {
      return new Lits();
   }

   @Override
   public Constr createUnregisteredCardinalityConstraint(IVecInt literals, int degree) {
      return new AtLeast(this.getVocabulary(), literals, degree);
   }

   interface INormalizer {
      PBContainer nice(IVecInt var1, IVec<BigInteger> var2, boolean var3, BigInteger var4, ILits var5) throws ContradictionException;
   }
}
