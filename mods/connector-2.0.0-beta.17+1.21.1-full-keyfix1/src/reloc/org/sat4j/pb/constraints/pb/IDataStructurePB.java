package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.VarActivityListener;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public interface IDataStructurePB {
   IDataStructurePB TAUTOLOGY = new IDataStructurePB() {
      @Override
      public int size() {
         return 0;
      }

      @Override
      public BigInteger saturation() {
         return BigInteger.ZERO;
      }

      @Override
      public int reduceCoeffsByPower2() {
         return 0;
      }

      @Override
      public boolean isLongSufficient() {
         return true;
      }

      @Override
      public boolean isCardinality() {
         return true;
      }

      @Override
      public BigInteger getDegree() {
         return BigInteger.ZERO;
      }

      @Override
      public BigInteger getCardDegree() {
         return BigInteger.ZERO;
      }

      @Override
      public int getAssertiveLiteral() {
         return -1;
      }

      @Override
      public BigInteger cuttingPlane(int[] lits, BigInteger[] reducedCoefs, BigInteger degreeCons, BigInteger coefMult) {
         throw new UnsupportedOperationException();
      }

      @Override
      public BigInteger cuttingPlane(int[] lits, BigInteger[] reducedCoefs, BigInteger deg) {
         throw new UnsupportedOperationException();
      }

      @Override
      public BigInteger cuttingPlane(PBConstr cpb, BigInteger degreeCons, BigInteger[] reducedCoefs, BigInteger coefMult, VarActivityListener val, int p) {
         throw new UnsupportedOperationException();
      }

      @Override
      public BigInteger cuttingPlane(PBConstr cpb, BigInteger deg, BigInteger[] reducedCoefs, VarActivityListener val, int p) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void buildConstraintFromMapPb(int[] resLits, BigInteger[] resCoefs) {
      }

      @Override
      public void buildConstraintFromConflict(IVecInt resLits, IVec<BigInteger> resCoefs) {
      }
   };

   BigInteger saturation();

   BigInteger cuttingPlane(PBConstr var1, BigInteger var2, BigInteger[] var3, VarActivityListener var4, int var5);

   BigInteger cuttingPlane(PBConstr var1, BigInteger var2, BigInteger[] var3, BigInteger var4, VarActivityListener var5, int var6);

   BigInteger cuttingPlane(int[] var1, BigInteger[] var2, BigInteger var3);

   BigInteger cuttingPlane(int[] var1, BigInteger[] var2, BigInteger var3, BigInteger var4);

   void buildConstraintFromConflict(IVecInt var1, IVec<BigInteger> var2);

   void buildConstraintFromMapPb(int[] var1, BigInteger[] var2);

   BigInteger getDegree();

   BigInteger getCardDegree();

   int size();

   boolean isCardinality();

   int getAssertiveLiteral();

   boolean isLongSufficient();

   int reduceCoeffsByPower2();
}
