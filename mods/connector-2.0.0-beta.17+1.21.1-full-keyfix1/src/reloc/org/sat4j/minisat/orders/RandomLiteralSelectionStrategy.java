package reloc.org.sat4j.minisat.orders;

import java.util.Random;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;

@Feature(value = "phaseheuristics", parent = "expert")
public final class RandomLiteralSelectionStrategy implements IPhaseSelectionStrategy {
   private static final long serialVersionUID = 1L;
   public static final Random RAND = System.getProperty("NONDETERMINISTIC") == null ? new Random(123456789L) : new Random();

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
      return RAND.nextBoolean() ? LiteralsUtils.posLit(var) : LiteralsUtils.negLit(var);
   }

   @Override
   public void updateVar(int p) {
   }

   @Override
   public void updateVarAtDecisionLevel(int q) {
   }

   @Override
   public String toString() {
      return "random phase selection";
   }
}
