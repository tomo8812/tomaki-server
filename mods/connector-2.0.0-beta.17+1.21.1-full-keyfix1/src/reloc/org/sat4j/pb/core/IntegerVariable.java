package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.core.ReadOnlyVecInt;
import reloc.org.sat4j.specs.IVecInt;

public class IntegerVariable {
   private final IVecInt vars;

   public IntegerVariable(IVecInt vars) {
      this.vars = new ReadOnlyVecInt(vars);
   }

   public IVecInt getVars() {
      return this.vars;
   }

   public int nVars() {
      return this.vars.size();
   }
}
