package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClauses;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVecInt;

public class UnitClausesPB extends UnitClauses implements PBConstr {
   public UnitClausesPB(IVecInt values) {
      super(values);
   }

   @Override
   public BigInteger getCoef(int literal) {
      return BigInteger.ONE;
   }

   @Override
   public BigInteger getDegree() {
      return BigInteger.ONE;
   }

   @Override
   public ILits getVocabulary() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int[] getLits() {
      throw new UnsupportedOperationException();
   }

   @Override
   public BigInteger[] getCoefs() {
      throw new UnsupportedOperationException();
   }

   @Override
   public IVecInt computeAnImpliedClause() {
      throw new UnsupportedOperationException();
   }

   @Override
   public BigInteger getSumCoefs() {
      return BigInteger.ONE;
   }
}
