package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.IVecInt;

@Feature("solutionlistener")
public interface SolutionFoundListener {
   SolutionFoundListener VOID = new SolutionFoundListener() {
      @Override
      public void onSolutionFound(int[] model) {
      }

      @Override
      public void onSolutionFound(IVecInt solution) {
      }

      @Override
      public void onUnsatTermination() {
      }
   };

   void onSolutionFound(int[] var1);

   void onSolutionFound(IVecInt var1);

   void onUnsatTermination();
}
