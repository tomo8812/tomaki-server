package reloc.org.sat4j.minisat.restarts;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.core.SolverStats;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "restarts", parent = "expert")
public final class LubyRestarts implements RestartStrategy {
   public static final int DEFAULT_LUBY_FACTOR = 32;
   private static final long serialVersionUID = 1L;
   private int un = 1;
   private int vn = 1;
   private int factor;
   private int bound;
   private int conflictcount;

   public int luby() {
      return this.vn;
   }

   public int nextLuby() {
      if ((this.un & -this.un) == this.vn) {
         this.un++;
         this.vn = 1;
      } else {
         this.vn <<= 1;
      }

      return this.vn;
   }

   public LubyRestarts() {
      this(32);
   }

   public LubyRestarts(int factor) {
      this.setFactor(factor);
   }

   public void setFactor(int factor) {
      this.factor = factor;
   }

   public int getFactor() {
      return this.factor;
   }

   @Override
   public void init(SearchParams params, SolverStats stats) {
      this.un = 1;
      this.vn = 1;
      this.bound = this.luby() * this.factor;
   }

   @Override
   public long nextRestartNumberOfConflict() {
      return this.bound;
   }

   @Override
   public void onRestart() {
      this.bound = this.nextLuby() * this.factor;
      this.conflictcount = 0;
   }

   @Override
   public String toString() {
      return "luby style (SATZ_rand, TiniSAT) restarts strategy with factor " + this.factor;
   }

   @Override
   public boolean shouldRestart() {
      return this.conflictcount >= this.bound;
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
