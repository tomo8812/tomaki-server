package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;

public class PBSolverResolution extends PBSolver {
   private static final long serialVersionUID = 1L;

   public PBSolverResolution(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, RestartStrategy restarter
   ) {
      super(learner, dsf, params, order, restarter);
   }

   public PBSolverResolution(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order, RestartStrategy restarter) {
      super(learner, dsf, order, restarter);
   }
}
