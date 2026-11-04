package reloc.org.sat4j.pb;

import java.math.BigInteger;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IGroupSolver;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public interface IGroupPBSolver extends IGroupSolver, IPBSolver {
   IConstr addAtMost(IVecInt var1, int var2, int var3) throws ContradictionException;

   IConstr addAtLeast(IVecInt var1, int var2, int var3) throws ContradictionException;

   IConstr addExactly(IVecInt var1, int var2, int var3) throws ContradictionException;

   @Override
   IConstr addAtMost(IVecInt var1, IVecInt var2, int var3) throws ContradictionException;

   IConstr addAtMost(IVecInt var1, IVec<BigInteger> var2, BigInteger var3, int var4) throws ContradictionException;

   IConstr addAtMost(IVecInt var1, IVecInt var2, int var3, int var4) throws ContradictionException;

   IConstr addAtLeast(IVecInt var1, IVecInt var2, int var3, int var4) throws ContradictionException;

   IConstr addAtLeast(IVecInt var1, IVec<BigInteger> var2, BigInteger var3, int var4) throws ContradictionException;

   IConstr addExactly(IVecInt var1, IVecInt var2, int var3, int var4) throws ContradictionException;

   IConstr addExactly(IVecInt var1, IVec<BigInteger> var2, BigInteger var3, int var4) throws ContradictionException;
}
