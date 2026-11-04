package reloc.org.sat4j.minisat.orders;

import java.util.ArrayList;
import java.util.List;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.Heap;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;
import reloc.org.sat4j.specs.IVecInt;

@Feature(value = "varheuristics", parent = "expert")
public class LevelBasedVarOrderHeap extends VarOrderHeap {
   private static final long serialVersionUID = 1L;
   private int[] level;
   private final List<IVecInt> varsByLevel = new ArrayList<>();

   public LevelBasedVarOrderHeap() {
   }

   public LevelBasedVarOrderHeap(IPhaseSelectionStrategy strategy) {
      super(strategy);
   }

   @Override
   protected Heap createHeap(double[] activity) {
      return new Heap(new LevelAndActivityVariableComparator(activity, this.level));
   }

   public void addLevel(IVecInt vars) {
      this.varsByLevel.add(vars.clone());
   }

   public void addLevel(int[] vars) {
      this.varsByLevel.add(new VecInt(vars));
   }

   @Override
   public void init() {
      int nlength = this.lits.nVars() + 1;
      if (this.level == null || this.level.length < nlength) {
         this.level = new int[nlength];

         for (int i = 0; i < nlength; i++) {
            this.level[i] = Integer.MAX_VALUE;
         }
      }

      for (int i = 1; i <= this.varsByLevel.size(); i++) {
         IVecInt currentLevel = this.varsByLevel.get(i - 1);

         for (int j = 0; j < currentLevel.size(); j++) {
            this.level[currentLevel.get(j)] = i;
         }
      }

      super.init();
   }

   @Override
   public String toString() {
      return "Level and activity based heuristics using a heap " + this.phaseStrategy;
   }
}
