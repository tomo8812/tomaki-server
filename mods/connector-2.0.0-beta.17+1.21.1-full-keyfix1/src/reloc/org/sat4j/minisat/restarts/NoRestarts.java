package reloc.org.sat4j.minisat.restarts;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.core.SolverStats;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "restarts", parent = "expert")
public final class NoRestarts implements RestartStrategy {
   private static final long serialVersionUID = 1L;

   @Override
   public void init(SearchParams params, SolverStats stats) {
   }

   @Override
   public long nextRestartNumberOfConflict() {
      return Long.MAX_VALUE;
   }

   @Override
   public void onRestart() {
   }

   @Override
   public void reset() {
   }

   @Override
   public void newConflict() {
   }

   @Override
   public boolean shouldRestart() {
      return false;
   }

   @Override
   public void onBackjumpToRootLevel() {
   }

   @Override
   public String toString() {
      return "NoRestarts";
   }

   @Override
   public void newLearnedClause(Constr learned, int trailLevel) {
   }
}
