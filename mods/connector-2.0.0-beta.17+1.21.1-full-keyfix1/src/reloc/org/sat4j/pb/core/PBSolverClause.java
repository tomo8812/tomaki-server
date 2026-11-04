package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import reloc.org.sat4j.pb.constraints.pb.ConflictMapClause;
import reloc.org.sat4j.pb.constraints.pb.IConflict;
import reloc.org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import reloc.org.sat4j.pb.constraints.pb.NoPostProcess;
import reloc.org.sat4j.pb.constraints.pb.NoPreProcess;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class PBSolverClause extends PBSolverCP {
   private static final long serialVersionUID = 1L;

   public PBSolverClause(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order) {
      super(learner, dsf, order);
   }

   @Override
   protected IConflict chooseConflict(PBConstr myconfl, int level) {
      return ConflictMapClause.createConflict(
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
      return super.toString(prefix) + "\n" + prefix + "Simplify during conflict analysis PB constraints to clauses";
   }
}
