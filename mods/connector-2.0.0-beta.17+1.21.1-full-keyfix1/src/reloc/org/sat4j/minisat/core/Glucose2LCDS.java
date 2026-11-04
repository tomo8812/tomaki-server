package reloc.org.sat4j.minisat.core;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVec;

@Feature(value = "deletion", parent = "expert")
public class Glucose2LCDS<D extends DataStructureFactory> extends GlucoseLCDS<D> {
   private static final long serialVersionUID = 1L;

   protected Glucose2LCDS(Solver<D> solver, ConflictTimer timer) {
      super(solver, timer);
   }

   @Override
   public String toString() {
      return "Glucose 2 learned constraints deletion strategy (LBD updated on propagation) with timer " + this.getTimer();
   }

   @Override
   public void onPropagation(Constr from, int propagated) {
      if (from.getActivity() > 2.0) {
         int nblevel = this.computeLBD(from, propagated);
         if (nblevel < from.getActivity()) {
            this.getSolver().stats.incUpdateLBD();
            from.setActivity(nblevel);
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void onConflictAnalysis(Constr var1) {
      super.onConflictAnalysis(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void onClauseLearning(Constr var1) {
      super.onClauseLearning(var1);
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
   public ConflictTimer getTimer() {
      return super.getTimer();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void reduce(IVec var1) {
      super.reduce(var1);
   }
}
