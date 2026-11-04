package reloc.org.sat4j.minisat.orders;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;

@Feature(value = "phaseheuristics", parent = "expert")
public final class PhaseInLastLearnedClauseSelectionStrategy extends AbstractPhaserecordingSelectionStrategy {
   private static final long serialVersionUID = 1L;

   @Override
   public void updateVar(int p) {
      this.phase[LiteralsUtils.var(p)] = p;
   }

   @Override
   public String toString() {
      return "phase appearing in latest learned clause";
   }

   @Override
   public void assignLiteral(int p) {
   }

   @Override
   public void updateVarAtDecisionLevel(int q) {
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
