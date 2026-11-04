package reloc.org.sat4j.pb;

import java.math.BigInteger;
import reloc.org.sat4j.pb.core.IntegerVariable;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public interface IIntegerPBSolver extends IPBSolver {
   IntegerVariable newIntegerVar(BigInteger var1);

   BigInteger getIntegerVarValue(IntegerVariable var1);

   IConstr addAtLeast(IntegerVariable var1, int var2) throws ContradictionException;

   IConstr addAtLeast(IVecInt var1, IVec<BigInteger> var2, IVec<IntegerVariable> var3, IVec<BigInteger> var4, BigInteger var5) throws ContradictionException;

   IConstr addAtLeast(IVecInt var1, IVecInt var2, IVec<IntegerVariable> var3, IVec<BigInteger> var4, int var5) throws ContradictionException;

   IConstr addAtMost(IntegerVariable var1, int var2) throws ContradictionException;

   IConstr addAtMost(IVecInt var1, IVec<BigInteger> var2, IVec<IntegerVariable> var3, IVec<BigInteger> var4, BigInteger var5) throws ContradictionException;

   IConstr addAtMost(IVecInt var1, IVecInt var2, IVec<IntegerVariable> var3, IVec<BigInteger> var4, int var5) throws ContradictionException;

   IConstr addExactly(IntegerVariable var1, int var2) throws ContradictionException;

   IConstr addExactly(IVecInt var1, IVec<BigInteger> var2, IVec<IntegerVariable> var3, IVec<BigInteger> var4, BigInteger var5) throws ContradictionException;

   IConstr addExactly(IVecInt var1, IVecInt var2, IVec<IntegerVariable> var3, IVec<BigInteger> var4, int var5) throws ContradictionException;

   IConstr addPseudoBoolean(IVecInt var1, IVec<BigInteger> var2, IVec<IntegerVariable> var3, IVec<BigInteger> var4, boolean var5, BigInteger var6) throws ContradictionException;

   void addIntegerVariableToObjectiveFunction(IntegerVariable var1, BigInteger var2);
}
