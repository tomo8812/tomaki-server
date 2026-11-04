package reloc.org.sat4j.minisat.core;

import java.io.Serializable;
import reloc.org.sat4j.specs.Constr;

public interface VarActivityListener extends Serializable {
   void varBumpActivity(int var1);

   void varBumpActivity(Constr var1, int var2, int var3);

   void postBumpActivity(Constr var1);
}
