package reloc.org.sat4j.minisat.learning;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.minisat.core.VarActivityListener;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "learning", parent = "expert")
public final class MiniSATLearning<D extends DataStructureFactory> extends AbstractLearning<D> {
   private static final long serialVersionUID = 1L;
   private DataStructureFactory dsf;

   public void setDataStructureFactory(DataStructureFactory dsf) {
      this.dsf = dsf;
   }

   @Override
   public void setSolver(Solver<D> s) {
      super.setSolver(s);
      if (s != null) {
         this.dsf = s.getDSFactory();
      }
   }

   @Override
   public void learns(Constr constr) {
      this.claBumpActivity(constr);
      this.dsf.learnConstraint(constr);
   }

   @Override
   public String toString() {
      return "Learn all clauses as in MiniSAT";
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
   public void setVarActivityListener(VarActivityListener var1) {
      super.setVarActivityListener(var1);
   }
}
