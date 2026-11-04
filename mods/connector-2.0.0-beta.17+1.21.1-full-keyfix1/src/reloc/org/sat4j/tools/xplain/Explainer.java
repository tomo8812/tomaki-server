package reloc.org.sat4j.tools.xplain;

import reloc.org.sat4j.specs.TimeoutException;

public interface Explainer {
   int[] minimalExplanation() throws TimeoutException;

   void setMinimizationStrategy(MinimizationStrategy var1);
}
