package reloc.org.sat4j.minisat.learning;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.minisat.core.VarActivityListener;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "learning", parent = "expert")
public final class NoLearningButHeuristics<D extends DataStructureFactory> extends AbstractLearning<D> {
   private static final long serialVersionUID = 1L;

   @Override
   public void learns(Constr reason) {
      this.claBumpActivity(reason);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void init() {
      super.init();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void setSolver(Solver var1) {
      super.setSolver(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void setVarActivityListener(VarActivityListener var1) {
      super.setVarActivityListener(var1);
   }
}
