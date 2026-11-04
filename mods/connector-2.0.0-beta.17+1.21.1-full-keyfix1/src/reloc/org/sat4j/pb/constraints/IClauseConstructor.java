package reloc.org.sat4j.pb.constraints;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.UnitPropagationListener;

public interface IClauseConstructor {
   Constr constructClause(UnitPropagationListener var1, ILits var2, IVecInt var3);

   Constr constructLearntClause(ILits var1, IVecInt var2);
}
