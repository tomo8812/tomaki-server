package reloc.org.sat4j.pb.constraints;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.IDataStructurePB;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public interface ICardConstructor {
   Constr constructCard(UnitPropagationListener var1, ILits var2, IVecInt var3, int var4) throws ContradictionException;

   Constr constructLearntCard(ILits var1, IDataStructurePB var2);
}
