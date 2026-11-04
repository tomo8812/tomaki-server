package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalHTClause;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public final class OriginalHTClausePB extends OriginalHTClause implements PBConstr {
   private static final long serialVersionUID = 1L;

   public OriginalHTClausePB(IVecInt ps, ILits voc) {
      super(ps, voc);
   }

   @Override
   public IVecInt computeAnImpliedClause() {
      return null;
   }

   @Override
   public BigInteger getCoef(int literal) {
      return BigInteger.ONE;
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
   public BigInteger getDegree() {
      return BigInteger.ONE;
   }

   public static OriginalHTClausePB brandNewClause(UnitPropagationListener s, ILits voc, IVecInt literals) {
      OriginalHTClausePB c = new OriginalHTClausePB(literals, voc);
      c.register();
      return c;
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();
      stb.append("+1 ");
      stb.append(LiteralsUtils.toOPB(this.head));

      for (int p : this.middleLits) {
         stb.append(" +1 ");
         stb.append(LiteralsUtils.toOPB(p));
      }

      stb.append(" +1 ");
      stb.append(LiteralsUtils.toOPB(this.tail));
      stb.append(" >= 1");
      return stb.toString();
   }

   @Override
   public BigInteger getSumCoefs() {
      return BigInteger.valueOf(this.size());
   }
}
