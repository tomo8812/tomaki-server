package reloc.org.sat4j.minisat.core;

import java.io.Serializable;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "learning", parent = "expert")
public interface LearningStrategy<D extends DataStructureFactory> extends Serializable {
   void init();

   void learns(Constr var1);

   void setVarActivityListener(VarActivityListener var1);

   void setSolver(Solver<D> var1);
}
