package reloc.org.sat4j.minisat.core;

final class MemoryBasedConflictTimer extends ConflictTimerAdapter {
   private static final long NO_BOUND_COMPUTED_YET = -1L;
   private static final long serialVersionUID = 1L;
   private long memorybound = -1L;

   MemoryBasedConflictTimer(Solver<? extends DataStructureFactory> solver, int bound) {
      super(solver, bound);
   }

   private long getMemoryBound() {
      if (this.memorybound == -1L) {
         this.memorybound = Runtime.getRuntime().freeMemory() / 10L;
      }

      return this.memorybound;
   }

   @Override
   public void run() {
      long freemem = Runtime.getRuntime().freeMemory();
      if (freemem < this.getMemoryBound()) {
         this.getSolver().setNeedToReduceDB(true);
      }
   }

   @Override
   public String toString() {
      return "check every " + this.bound() + " if the memory bound " + this.memorybound + " is reached";
   }
}
