package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.constraints.card.AtLeast;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public final class AtLeastPB extends AtLeast implements PBConstr {
   private static final long serialVersionUID = 1L;
   private final BigInteger degree;
   private boolean learnt = false;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AtLeastPB.class.desiredAssertionStatus();

   private AtLeastPB(ILits voc, IVecInt ps, int degree) {
      super(voc, ps, degree);
      this.degree = BigInteger.valueOf(degree);
   }

   public static PBConstr atLeastNew(UnitPropagationListener s, ILits voc, IVecInt ps, int n) throws ContradictionException {
      int degree = niceParameters(s, voc, ps, n);
      if (degree == 0) {
         return new UnitClausesPB(ps);
      } else if (degree == 1) {
         return ps.size() == 2 ? OriginalBinaryClausePB.brandNewClause(s, voc, ps) : OriginalHTClausePB.brandNewClause(s, voc, ps);
      } else {
         return atLeastNew(voc, ps, degree);
      }
   }

   public static AtLeastPB atLeastNew(ILits voc, IVecInt ps, int n) {
      AtLeastPB atleast = new AtLeastPB(voc, ps, n);
      atleast.register();
      return atleast;
   }

   @Override
   public BigInteger getCoef(int literal) {
      return BigInteger.ONE;
   }

   @Override
   public BigInteger getDegree() {
      return this.degree;
   }

   @Override
   public ILits getVocabulary() {
      return this.voc;
   }

   @Override
   public int[] getLits() {
      int[] tmp = new int[this.size()];
      System.arraycopy(this.lits, 0, tmp, 0, this.size());
      return tmp;
   }

   @Override
   public BigInteger[] getCoefs() {
      BigInteger[] tmp = new BigInteger[this.size()];

      for (int i = 0; i < tmp.length; i++) {
         tmp[i] = BigInteger.ONE;
      }

      return tmp;
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
