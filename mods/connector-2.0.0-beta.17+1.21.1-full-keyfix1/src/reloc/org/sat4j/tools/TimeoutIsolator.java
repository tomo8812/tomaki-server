package reloc.org.sat4j.tools;

import reloc.org.sat4j.specs.ISolver;

public class TimeoutIsolator<T extends ISolver> extends SolverDecorator<T> {
   private static final long serialVersionUID = 1L;

   public TimeoutIsolator(T solver) {
      super(solver);
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
   }

   @Override
   public void setTimeout(int t) {
   }

   @Override
   public void setTimeoutMs(long t) {
   }
}
