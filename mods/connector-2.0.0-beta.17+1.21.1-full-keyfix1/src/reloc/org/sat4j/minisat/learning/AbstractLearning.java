package reloc.org.sat4j.minisat.learning;

import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.minisat.core.VarActivityListener;
import reloc.org.sat4j.specs.Constr;

abstract class AbstractLearning<D extends DataStructureFactory> implements LearningStrategy<D> {
   private static final long serialVersionUID = 1L;
   private VarActivityListener val;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AbstractLearning.class.desiredAssertionStatus();

   @Override
   public void setVarActivityListener(VarActivityListener s) {
      this.val = s;
   }

   @Override
   public void setSolver(Solver<D> s) {
      this.val = s;
   }

   public final void claBumpActivity(Constr reason) {
      for (int i = 0; i < reason.size(); i++) {
         int q = reason.get(i);
         assert q > 1;
         this.val.varBumpActivity(q);
      }
   }

   @Override
   public void init() {
   }
}
