package reloc.org.sat4j.minisat.orders;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;

@Feature(value = "phaseheuristics", parent = "expert")
public final class PositiveLiteralSelectionStrategy implements IPhaseSelectionStrategy {
   private static final long serialVersionUID = 1L;

   @Override
   public void assignLiteral(int p) {
   }

   @Override
   public void init(int nlength) {
   }

   @Override
   public void init(int var, int p) {
   }

   @Override
   public int select(int var) {
      return LiteralsUtils.posLit(var);
   }

   @Override
   public void updateVar(int p) {
   }

   @Override
   public String toString() {
      return "positive phase selection";
   }

   @Override
   public void updateVarAtDecisionLevel(int q) {
   }
}
