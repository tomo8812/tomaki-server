package reloc.org.sat4j.tools.xplain;

import java.io.Serializable;
import java.util.Map;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public interface MinimizationStrategy extends Serializable {
   IVecInt explain(ISolver var1, Map<Integer, ?> var2, IVecInt var3) throws TimeoutException;

   void cancelExplanationComputation();
}
