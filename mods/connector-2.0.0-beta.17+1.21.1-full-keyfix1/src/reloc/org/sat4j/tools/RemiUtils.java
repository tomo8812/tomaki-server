package reloc.org.sat4j.tools;

import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public final class RemiUtils {
   private RemiUtils() {
   }

   public static IVecInt backbone(ISolver s) throws TimeoutException {
      IVecInt backbone = new VecInt();
      int nvars = s.nVars();

      for (int i = 1; i <= nvars; i++) {
         backbone.push(i);
         if (s.isSatisfiable(backbone)) {
            backbone.pop().push(-i);
            if (s.isSatisfiable(backbone)) {
               backbone.pop();
            } else {
               backbone.pop().push(i);
            }
         } else {
            backbone.pop().push(-i);
         }
      }

      return backbone;
   }
}
