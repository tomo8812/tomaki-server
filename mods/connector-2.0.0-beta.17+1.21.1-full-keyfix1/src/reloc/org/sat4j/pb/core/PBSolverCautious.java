package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.pb.constraints.pb.ConflictMapSwitchToClause;
import reloc.org.sat4j.pb.constraints.pb.IConflict;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class PBSolverCautious extends PBSolverCP {
   private static final long serialVersionUID = 1L;
   public static final int BOUND = 10;

   public PBSolverCautious(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order) {
      super(learner, dsf, order);
      ConflictMapSwitchToClause.setUpperBound(10);
   }

   public PBSolverCautious(LearningStrategy<PBDataStructureFactory> learner, PBDataStructureFactory dsf, IOrder order, int bound) {
      super(learner, dsf, order);
      ConflictMapSwitchToClause.setUpperBound(bound);
   }

   @Override
   protected IConflict chooseConflict(PBConstr myconfl, int level) {
      return new ConflictMapSwitchToClause(myconfl, level, this.isNoRemove(), this.isSkipAllow(), this.pbStats);
   }

   @Override
   public String toString(String prefix) {
      return super.toString(prefix) + "\n" + prefix + "When dealing with too large coefficients, simplify asserted PB constraints to clauses";
   }

   @Override
   protected void updateNumberOfReductions(IConflict confl) {
      this.pbStats.incNumberOfReductions(((ConflictMapSwitchToClause)confl).getNumberOfReductions());
   }

   @Override
   protected void updateNumberOfReducedLearnedConstraints(IConflict confl) {
      if (((ConflictMapSwitchToClause)confl).hasBeenReduced()) {
         this.pbStats.incNumberOfLearnedConstraintsReduced();
      }
   }
}
