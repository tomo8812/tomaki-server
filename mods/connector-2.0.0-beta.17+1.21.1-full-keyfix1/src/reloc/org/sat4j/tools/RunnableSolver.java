package reloc.org.sat4j.tools;

import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

class RunnableSolver implements Runnable {
   private final int index;
   private final ISolver solver;
   private final OutcomeListener ol;
   private final IVecInt assumps;
   private final boolean globalTimeout;

   public RunnableSolver(int i, ISolver solver, IVecInt assumps, boolean globalTimeout, OutcomeListener ol) {
      this.index = i;
      this.solver = solver;
      this.ol = ol;
      this.assumps = assumps;
      this.globalTimeout = globalTimeout;
   }

   @Override
   public void run() {
      try {
         boolean result = this.solver.isSatisfiable(this.assumps, this.globalTimeout);
         this.ol.onFinishWithAnswer(true, result, this.index);
      } catch (Exception e) {
         this.ol.onFinishWithAnswer(false, false, this.index);
      }
   }
}
