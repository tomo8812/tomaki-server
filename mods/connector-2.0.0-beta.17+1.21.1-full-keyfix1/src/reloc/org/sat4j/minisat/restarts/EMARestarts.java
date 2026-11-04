package reloc.org.sat4j.minisat.restarts;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.core.SolverStats;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "restarts", parent = "expert")
public class EMARestarts implements RestartStrategy {
   private static final long serialVersionUID = 1L;
   private long fast;
   private long slow;
   private SolverStats stats;
   private long limit;

   @Override
   public void reset() {
      this.fast = 0L;
      this.slow = 0L;
      this.limit = 50L;
   }

   @Override
   public void newConflict() {
   }

   @Override
   public void init(SearchParams params, SolverStats stats) {
      this.stats = stats;
      this.reset();
   }

   @Override
   public long nextRestartNumberOfConflict() {
      return 0L;
   }

   @Override
   public boolean shouldRestart() {
      return this.stats.getConflicts() > this.limit && this.fast / 125L > this.slow / 100L;
   }

   @Override
   public void onRestart() {
      this.limit = this.stats.getConflicts() + 50L;
   }

   @Override
   public void onBackjumpToRootLevel() {
   }

   @Override
   public void newLearnedClause(Constr learned, int trailLevel) {
      int lbd = (int)learned.getActivity();
      this.fast = this.fast - (this.fast >> 5);
      this.fast += lbd << 27;
      this.slow = this.slow - (this.slow >> 14);
      this.slow += lbd << 18;
   }

   @Override
   public String toString() {
      return "Exponential Moving Average (EMA, Biere) restarts strategy";
   }
}
