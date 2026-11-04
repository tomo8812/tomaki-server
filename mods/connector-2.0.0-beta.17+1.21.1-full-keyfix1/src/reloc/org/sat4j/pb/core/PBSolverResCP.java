package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;

public class PBSolverResCP extends PBSolverCP {
   private static final long serialVersionUID = 1L;
   public static final long MAXCONFLICTS = 100000L;
   private long bound;

   public PBSolverResCP(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order) {
      this(learner, dsf, order, 100000L);
   }

   public PBSolverResCP(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order, long bound) {
      super(learner, dsf, order);
      this.bound = bound;
   }

   public PBSolverResCP(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, RestartStrategy restarter
   ) {
      super(learner, dsf, params, order, restarter);
   }

   public PBSolverResCP(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order) {
      super(learner, dsf, params, order);
   }

   @Override
   boolean someCriteria() {
      if (this.pbStats.getConflicts() == this.bound) {
         this.setSimplifier(NO_SIMPLIFICATION);
         this.reduceDB();
         this.pbStats.incNumberOfCP();
         return true;
      } else if (this.pbStats.getConflicts() > this.bound) {
         this.pbStats.incNumberOfCP();
         return true;
      } else {
         this.pbStats.incNumberOfResolution();
         return false;
      }
   }
}
