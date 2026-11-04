package reloc.org.sat4j.pb;

import java.math.BigInteger;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public interface IPBSolverService extends ISolverService {
   IConstr addAtMostOnTheFly(IVecInt var1, IVec<BigInteger> var2, BigInteger var3);

   IConstr addAtMostOnTheFly(IVecInt var1, IVecInt var2, int var3);

   ObjectiveFunction getObjectiveFunction();
}
