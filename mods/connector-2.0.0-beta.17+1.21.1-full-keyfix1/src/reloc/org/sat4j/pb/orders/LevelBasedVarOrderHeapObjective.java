package reloc.org.sat4j.pb.orders;

import java.math.BigInteger;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;
import reloc.org.sat4j.minisat.orders.LevelBasedVarOrderHeap;
import reloc.org.sat4j.minisat.orders.PhaseInLastLearnedClauseSelectionStrategy;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class LevelBasedVarOrderHeapObjective extends LevelBasedVarOrderHeap implements IOrderObjective {
   private static final long serialVersionUID = 1L;
   private ObjectiveFunction obj;

   public LevelBasedVarOrderHeapObjective() {
      this(new PhaseInLastLearnedClauseSelectionStrategy());
   }

   public LevelBasedVarOrderHeapObjective(IPhaseSelectionStrategy strategy) {
      super(strategy);
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      this.obj = obj;
   }

   @Override
   public void init() {
      super.init();
      if (this.obj != null) {
         IVecInt vars = this.obj.getVars();
         IVec<BigInteger> coefs = this.obj.getCoeffs();

         for (int i = 0; i < vars.size(); i++) {
            int dimacsLiteral = vars.get(i);
            if (this.lits.belongsToPool(Math.abs(dimacsLiteral))) {
               int p = this.lits.getFromPool(dimacsLiteral);
               BigInteger c = coefs.get(i);
               if (c.signum() < 0) {
                  p = LiteralsUtils.neg(p);
               }

               int var = LiteralsUtils.var(p);
               this.activity[var] = c.bitLength() < 64 ? c.abs().longValue() : 9.223372E18F;
               if (this.heap.inHeap(var)) {
                  this.heap.increase(var);
               } else {
                  this.heap.insert(var);
               }

               this.phaseStrategy.init(var, LiteralsUtils.neg(p));
            }
         }
      }
   }

   @Override
   public String toString() {
      return super.toString() + " taking into account the objective function";
   }
}
