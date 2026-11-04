package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;

public interface PBConstr extends Constr {
   BigInteger getCoef(int var1);

   BigInteger getDegree();

   ILits getVocabulary();

   int[] getLits();

   BigInteger[] getCoefs();

   IVecInt computeAnImpliedClause();

   BigInteger getSumCoefs();
}
