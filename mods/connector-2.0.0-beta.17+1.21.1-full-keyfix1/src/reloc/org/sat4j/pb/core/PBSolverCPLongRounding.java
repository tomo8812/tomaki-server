package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import reloc.org.sat4j.pb.constraints.pb.ConflictMapRounding;
import reloc.org.sat4j.pb.constraints.pb.IConflict;
import reloc.org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import reloc.org.sat4j.pb.constraints.pb.NoPostProcess;
import reloc.org.sat4j.pb.constraints.pb.NoPreProcess;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.pb.constraints.pb.SkipStrategy;

public class PBSolverCPLongRounding extends PBSolverCPLong {
   private static final long serialVersionUID = 1L;

   public PBSolverCPLongRounding(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order) {
      super(learner, dsf, order);
   }

   public PBSolverCPLongRounding(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, RestartStrategy restarter
   ) {
      super(learner, dsf, params, order, restarter);
   }

   public PBSolverCPLongRounding(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order) {
      super(learner, dsf, params, order);
   }

   public PBSolverCPLongRounding(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow
   ) {
      super(learner, dsf, order, noRemove, skipAllow);
   }

   public PBSolverCPLongRounding(
      LearningStrategy<PBDataStructureFactory> learner,
      PBDataStructureFactory dsf,
      SearchParams params,
      IOrder order,
      RestartStrategy restarter,
      boolean noRemove,
      SkipStrategy skipAllow
   ) {
      super(learner, dsf, params, order, restarter, noRemove, skipAllow);
   }

   public PBSolverCPLongRounding(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, boolean noRemove, SkipStrategy skipAllow
   ) {
      super(learner, dsf, params, order, noRemove, skipAllow);
   }

   @Override
   protected IConflict chooseConflict(PBConstr myconfl, int level) {
      return ConflictMapRounding.createConflict(
         myconfl,
         level,
         this.isNoRemove(),
         this.isSkipAllow(),
         NoPreProcess.instance(),
         NoPostProcess.instance(),
         IWeakeningStrategy.UNASSIGNED_FIRST,
         AutoDivisionStrategy.ENABLED,
         this.pbStats
      );
   }

   @Override
   public String toString(String prefix) {
      return super.toString(prefix)
         + "\n"
         + prefix
         + "Performs rounding over coefficients during conflict analysis - Jakob Nordstrom's algorithm (ConflictMapRounding)";
   }
}
