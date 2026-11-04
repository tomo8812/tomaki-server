package reloc.org.sat4j.minisat.orders;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.Heap;

@Feature(value = "varheuristics", parent = "expert")
public class SubsetVarOrder extends VarOrderHeap {
   private final int[] varsToTest;
   private boolean[] inSubset;
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !SubsetVarOrder.class.desiredAssertionStatus();

   public SubsetVarOrder(int[] varsToTest) {
      this.varsToTest = new int[varsToTest.length];
      System.arraycopy(varsToTest, 0, this.varsToTest, 0, varsToTest.length);
   }

   @Override
   public void init() {
      int nlength = this.lits.nVars() + 1;
      if (this.activity == null || this.activity.length < nlength) {
         this.activity = new double[nlength];
      }

      this.inSubset = new boolean[nlength];
      this.phaseStrategy.init(nlength);
      this.activity[0] = -1.0;
      this.heap = new Heap(new ActivityBasedVariableComparator(this.activity));
      this.heap.setBounds(nlength);

      for (int var : this.varsToTest) {
         assert var > 0;
         assert var <= this.lits.nVars() : "" + this.lits.nVars() + "/" + var;
         this.inSubset[var] = true;
         this.activity[var] = 0.0;
         if (this.lits.belongsToPool(var)) {
            this.heap.insert(var);
         }
      }
   }

   @Override
   public void undo(int x) {
      if (this.inSubset[x] && !this.heap.inHeap(x)) {
         this.heap.insert(x);
      }
   }
}
