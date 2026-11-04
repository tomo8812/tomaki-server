package reloc.org.sat4j.pb.constraints;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.UnitPropagationListener;

public interface IPBConstructor {
   Constr constructLearntPB(ILits var1, IDataStructurePB var2);

   Constr constructPB(UnitPropagationListener var1, ILits var2, int[] var3, BigInteger[] var4, BigInteger var5, BigInteger var6) throws ContradictionException;
}
