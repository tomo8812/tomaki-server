package reloc.org.sat4j.tools.counting;

import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IteratorInt;

public final class AllVariablesSamplingSet implements SamplingSet {
   private final ISolver solver;

   private AllVariablesSamplingSet(ISolver solver) {
      this.solver = solver;
   }

   public static SamplingSet of(ISolver solver) {
      return new AllVariablesSamplingSet(solver);
   }

   @Override
   public int nVars() {
      return this.solver.nVars();
   }

   @Override
   public IteratorInt variables() {
      return new IteratorInt() {
         int currentVariable = 1;

         @Override
         public int next() {
            return this.currentVariable++;
         }

         @Override
         public boolean hasNext() {
            return this.currentVariable <= AllVariablesSamplingSet.this.solver.nVars();
         }
      };
   }
}
