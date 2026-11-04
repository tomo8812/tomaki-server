package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.constraints.card.MinWatchCard;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public final class MinWatchCardPB extends MinWatchCard implements PBConstr {
   private static final long serialVersionUID = 1L;
   private final BigInteger bigDegree;
   private boolean learnt = false;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MinWatchCardPB.class.desiredAssertionStatus();

   public MinWatchCardPB(ILits voc, IVecInt ps, boolean moreThan, int degree) {
      super(voc, ps, moreThan, degree);
      this.bigDegree = BigInteger.valueOf(this.degree);
   }

   public MinWatchCardPB(ILits voc, IVecInt ps, int degree) {
      super(voc, ps, degree);
      this.bigDegree = BigInteger.valueOf(this.degree);
   }

   @Override
   public BigInteger getCoef(int literal) {
      return BigInteger.ONE;
   }

   @Override
   public BigInteger getDegree() {
      return this.bigDegree;
   }

   @Override
   public BigInteger[] getCoefs() {
      BigInteger[] tmp = new BigInteger[this.size()];

      for (int i = 0; i < tmp.length; i++) {
         tmp[i] = BigInteger.ONE;
      }

      return tmp;
   }

   public static PBConstr normalizedMinWatchCardPBNew(UnitPropagationListener s, ILits voc, IVecInt ps, int degree) throws ContradictionException {
      return minWatchCardPBNew(s, voc, ps, true, degree, true);
   }

   public static PBConstr minWatchCardPBNew(UnitPropagationListener s, ILits voc, IVecInt ps, boolean moreThan, int degree) throws ContradictionException {
      return minWatchCardPBNew(s, voc, ps, moreThan, degree, false);
   }

   private static PBConstr minWatchCardPBNew(UnitPropagationListener s, ILits voc, IVecInt ps, boolean moreThan, int degree, boolean normalized) throws ContradictionException {
      int mydegree = degree + linearisation(voc, ps);
      if (ps.size() < mydegree) {
         throw new ContradictionException();
      }

      if (ps.size() == 0 && mydegree > 0) {
         throw new ContradictionException();
      }

      if (ps.size() != mydegree && ps.size() > 0) {
         MinWatchCardPB retour = null;
         if (normalized) {
            retour = new MinWatchCardPB(voc, ps, mydegree);
         } else {
            retour = new MinWatchCardPB(voc, ps, moreThan, mydegree);
         }

         if (retour.bigDegree.signum() <= 0) {
            return null;
         }

         retour.computeWatches();
         return (MinWatchCardPB)retour.computePropagation(s);
      } else {
         for (int i = 0; i < ps.size(); i++) {
            if (!s.enqueue(ps.get(i))) {
               throw new ContradictionException();
            }
         }

         return new UnitClausesPB(ps);
      }
   }

   @Override
   public boolean learnt() {
      return this.learnt;
   }

   @Override
   public void setLearnt() {
      this.learnt = true;
   }

   @Override
   public void register() {
      assert this.learnt;
      this.computeWatches();
   }

   @Override
   public void assertConstraint(UnitPropagationListener s) {
      for (int i = 0; i < this.size(); i++) {
         if (this.getVocabulary().isUnassigned(this.get(i))) {
            boolean ret = s.enqueue(this.get(i), this);
            assert ret;
         }
      }
   }

   @Override
   public IVecInt computeAnImpliedClause() {
      return null;
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();
      stb.append("+1 ");
      stb.append(LiteralsUtils.toOPB(this.lits[0]));
      int i = 1;

      while (i < this.lits.length) {
         stb.append(" +1 ");
         stb.append(LiteralsUtils.toOPB(this.lits[i++]));
      }

      stb.append(" >= ");
      stb.append(this.size() - this.maxUnsatisfied);
      return stb.toString();
   }

   @Override
   public BigInteger getSumCoefs() {
      return BigInteger.valueOf(this.size());
   }
}
