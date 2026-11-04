package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import reloc.org.sat4j.pb.constraints.pb.ConflictMapReduceToClause;
import reloc.org.sat4j.pb.constraints.pb.IConflict;
import reloc.org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import reloc.org.sat4j.pb.constraints.pb.NoPostProcess;
import reloc.org.sat4j.pb.constraints.pb.NoPreProcess;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.pb.constraints.pb.SkipStrategy;

public class PBSolverCPLong extends PBSolverCP {
   private static final long serialVersionUID = 1L;

   public PBSolverCPLong(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order) {
      super(learner, dsf, order);
   }

   public PBSolverCPLong(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, RestartStrategy restarter
   ) {
      super(learner, dsf, params, order, restarter);
   }

   public PBSolverCPLong(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order) {
      super(learner, dsf, params, order);
   }

   public PBSolverCPLong(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order, boolean noRemove, SkipStrategy skipAllow) {
      super(learner, dsf, order, noRemove, skipAllow);
   }

   public PBSolverCPLong(
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

   public PBSolverCPLong(
      LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, SearchParams params, IOrder order, boolean noRemove, SkipStrategy skipAllow
   ) {
      super(learner, dsf, params, order, noRemove, skipAllow);
   }

   @Override
   protected IConflict chooseConflict(PBConstr myconfl, int level) {
      return ConflictMapReduceToClause.createConflict(
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
         + "Reduce coefficients of asserted PB constraints from BigInteger to Long and reduce to clauses during conflict analysis if necessary (ConflictMapReduceToClause) ";
   }
}
