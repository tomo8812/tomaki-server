package reloc.org.sat4j.minisat.orders;

public class LevelAndActivityVariableComparator implements VariableComparator {
   private final double[] activity;
   private final int[] level;

   public LevelAndActivityVariableComparator(double[] activity, int[] level) {
      this.activity = activity;
      this.level = level;
   }

   @Override
   public boolean preferredTo(int a, int b) {
      return this.level[a] < this.level[b] || this.level[a] == this.level[b] && this.activity[a] > this.activity[b];
   }

   @Override
   public String toString() {
      return "Level and activity based variable heuristic";
   }
}
