package reloc.org.sat4j.minisat.orders;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;

@Feature(value = "phaseheuristics", parent = "expert")
public final class RSATLastLearnedClausesPhaseSelectionStrategy extends AbstractPhaserecordingSelectionStrategy {
   private static final long serialVersionUID = 1L;

   @Override
   public void assignLiteral(int p) {
      this.phase[LiteralsUtils.var(p)] = p;
   }

   @Override
   public String toString() {
      return "lightweight component caching from RSAT inverting phase for variables at conflict decision level";
   }

   @Override
   public void updateVar(int p) {
   }

   @Override
   public void updateVarAtDecisionLevel(int p) {
      this.phase[LiteralsUtils.var(p)] = p;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int select(int var1) {
      return super.select(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void init(int var1, int var2) {
      super.init(var1, var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void init(int var1) {
      super.init(var1);
   }
}
