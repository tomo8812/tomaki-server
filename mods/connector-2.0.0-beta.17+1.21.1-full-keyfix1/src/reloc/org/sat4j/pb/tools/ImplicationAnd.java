package reloc.org.sat4j.pb.tools;

import java.util.Iterator;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class ImplicationAnd<T, C> {
   private final DependencyHelper<T, C> helper;
   private final IVecInt clause;
   private final IVec<IConstr> toName = new Vec<>();

   public ImplicationAnd(DependencyHelper<T, C> helper, IVecInt clause) {
      this.clause = clause;
      this.helper = helper;
   }

   public ImplicationAnd<T, C> and(T thing) throws ContradictionException {
      IVecInt tmpClause = new VecInt();
      this.clause.copyTo(tmpClause);
      tmpClause.push(this.helper.getIntValue(thing));
      IConstr constr = this.helper.solver.addClause(tmpClause);
      if (constr != null) {
         this.toName.push(constr);
      }

      return this;
   }

   public ImplicationAnd<T, C> andNot(T thing) throws ContradictionException {
      IVecInt tmpClause = new VecInt();
      this.clause.copyTo(tmpClause);
      tmpClause.push(-this.helper.getIntValue(thing));
      IConstr constr = this.helper.solver.addClause(tmpClause);
      if (constr != null) {
         this.toName.push(constr);
      }

      return this;
   }

   public void named(C name) {
      Iterator<IConstr> it = this.toName.iterator();

      while (it.hasNext()) {
         this.helper.descs.put(it.next(), name);
      }
   }
}
