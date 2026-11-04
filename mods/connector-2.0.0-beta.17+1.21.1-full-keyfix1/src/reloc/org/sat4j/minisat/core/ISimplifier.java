package reloc.org.sat4j.minisat.core;

import java.io.Serializable;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.IVecInt;

@Feature(value = "simplifications", parent = "expert")
public interface ISimplifier extends Serializable {
   void simplify(IVecInt var1);
}
