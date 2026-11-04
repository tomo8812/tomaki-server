package reloc.org.sat4j.minisat.restarts;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.core.SolverStats;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "restarts", parent = "expert")
public final class MiniSATRestarts implements RestartStrategy {
   private static final long serialVersionUID = 1L;
   private double nofConflicts;
   private SearchParams params;
   private int conflictcount;

   @Override
   public void init(SearchParams theParams, SolverStats stats) {
      this.params = theParams;
      this.nofConflicts = theParams.getInitConflictBound();
      this.conflictcount = 0;
   }

   @Override
   public long nextRestartNumberOfConflict() {
      return Math.round(this.nofConflicts);
   }

   @Override
   public void onRestart() {
      this.nofConflicts = this.nofConflicts * this.params.getConflictBoundIncFactor();
   }

   @Override
   public String toString() {
      return "MiniSAT restarts strategy";
   }

   @Override
   public boolean shouldRestart() {
      return this.conflictcount >= this.nofConflicts;
   }

   @Override
   public void onBackjumpToRootLevel() {
      this.conflictcount = 0;
   }

   @Override
   public void reset() {
      this.conflictcount = 0;
   }

   @Override
   public void newConflict() {
      this.conflictcount++;
   }

   @Override
   public void newLearnedClause(Constr learned, int trailLevel) {
   }
}
