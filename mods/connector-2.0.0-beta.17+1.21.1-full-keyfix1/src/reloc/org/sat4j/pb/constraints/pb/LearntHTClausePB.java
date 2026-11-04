package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.constraints.cnf.LearntHTClause;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public final class LearntHTClausePB extends LearntHTClause implements PBConstr {
   private static final long serialVersionUID = 1L;

   public LearntHTClausePB(IVecInt ps, ILits voc) {
      super(ps, voc);
   }

   @Override
   public void assertConstraint(UnitPropagationListener s) {
      if (this.getVocabulary().isUnassigned(this.head)) {
         s.enqueue(this.head, this);
      } else if (this.getVocabulary().isUnassigned(this.tail)) {
         s.enqueue(this.tail, this);
      } else {
         for (int i = 0; i < this.middleLits.length; i++) {
            if (this.getVocabulary().isUnassigned(this.middleLits[i])) {
               int temp = this.middleLits[i];
               this.middleLits[i] = this.head;
               this.head = temp;
               s.enqueue(temp, this);
               break;
            }
         }
      }
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
