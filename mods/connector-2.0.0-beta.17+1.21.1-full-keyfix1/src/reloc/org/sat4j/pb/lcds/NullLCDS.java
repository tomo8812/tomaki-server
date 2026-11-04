package reloc.org.sat4j.pb.lcds;

import reloc.org.sat4j.minisat.core.ConflictTimer;
import reloc.org.sat4j.minisat.core.LearnedConstraintsDeletionStrategy;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVec;

public class NullLCDS implements LearnedConstraintsDeletionStrategy {
   private final LearnedConstraintsDeletionStrategy decorated;

   public NullLCDS(LearnedConstraintsDeletionStrategy decorated) {
      this.decorated = decorated;
   }

   @Override
   public void init() {
      this.decorated.init();
   }

   @Override
   public ConflictTimer getTimer() {
      return this.decorated.getTimer();
   }

   @Override
   public void reduce(IVec<Constr> learnedConstrs) {
   }

   @Override
   public void onClauseLearning(Constr outLearnt) {
      this.decorated.onClauseLearning(outLearnt);
   }

   @Override
   public void onConflictAnalysis(Constr reason) {
      this.decorated.onConflictAnalysis(reason);
   }

   @Override
   public void onPropagation(Constr from, int propagated) {
      this.decorated.onPropagation(from, propagated);
   }
}
