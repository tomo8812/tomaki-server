package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClause;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVecInt;

public final class UnitClausePB extends UnitClause implements PBConstr {
   private final ILits voc;
   private static final long serialVersionUID = 1L;

   public UnitClausePB(int value, ILits voc) {
      super(value);
      this.voc = voc;
   }

   public UnitClausePB(int value, boolean learnt, ILits voc) {
      super(value, learnt);
      this.voc = voc;
   }

   @Override
   public IVecInt computeAnImpliedClause() {
      return null;
   }

   @Override
   public BigInteger getCoef(int p) {
      return BigInteger.ONE;
   }

   @Override
   public BigInteger[] getCoefs() {
      return new BigInteger[]{BigInteger.ONE};
   }

   @Override
   public BigInteger getDegree() {
      return BigInteger.ONE;
   }

   @Override
   public int[] getLits() {
      return new int[]{this.literal};
   }

   @Override
   public ILits getVocabulary() {
      return this.voc;
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();
      stb.append("+1 ");
      stb.append(LiteralsUtils.toOPB(this.literal));
      stb.append(" >= 1");
      return stb.toString();
   }

   @Override
   public BigInteger getSumCoefs() {
      return BigInteger.valueOf(this.size());
   }
}
