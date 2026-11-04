package reloc.org.sat4j.pb.core;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public interface PBDataStructureFactory extends DataStructureFactory {
   Constr createPseudoBooleanConstraint(IVecInt var1, IVec<BigInteger> var2, boolean var3, BigInteger var4) throws ContradictionException;

   Constr createAtMostPBConstraint(IVecInt var1, IVec<BigInteger> var2, BigInteger var3) throws ContradictionException;

   Constr createAtLeastPBConstraint(IVecInt var1, IVec<BigInteger> var2, BigInteger var3) throws ContradictionException;

   Constr createUnregisteredPseudoBooleanConstraint(IDataStructurePB var1);

   Constr createUnregisteredAtMostConstraint(IVecInt var1, IVec<BigInteger> var2, BigInteger var3);

   Constr createUnregisteredAtLeastConstraint(IVecInt var1, IVec<BigInteger> var2, BigInteger var3);
}
