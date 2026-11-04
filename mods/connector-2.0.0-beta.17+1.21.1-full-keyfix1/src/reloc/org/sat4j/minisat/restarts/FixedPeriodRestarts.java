package reloc.org.sat4j.minisat.restarts;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.core.SolverStats;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "restarts", parent = "expert")
public class FixedPeriodRestarts implements RestartStrategy {
   private static final long serialVersionUID = 1L;
   private long conflictCount;
   private long period;

   @Override
   public void reset() {
      this.conflictCount = 0L;
   }

   @Override
   public void newConflict() {
      this.conflictCount++;
   }

   @Override
   public void init(SearchParams params, SolverStats stats) {
      this.conflictCount = 0L;
   }

   @Deprecated
   @Override
   public long nextRestartNumberOfConflict() {
      return this.period;
   }

   @Override
   public boolean shouldRestart() {
      return this.conflictCount >= this.period;
   }

   @Override
   public void onRestart() {
      this.conflictCount = 0L;
   }

   @Override
   public void onBackjumpToRootLevel() {
   }

   @Override
   public void newLearnedClause(Constr learned, int trailLevel) {
   }

   public long getPeriod() {
      return this.period;
   }

   public void setPeriod(long period) {
      this.period = period;
   }

   @Override
   public String toString() {
      return "constant restarts strategy every " + this.period + " conflicts";
   }
}
