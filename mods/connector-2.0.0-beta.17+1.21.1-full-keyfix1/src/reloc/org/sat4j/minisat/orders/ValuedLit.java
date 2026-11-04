package reloc.org.sat4j.minisat.orders;

final class ValuedLit implements Comparable<ValuedLit> {
   final int id;
   final int count;

   ValuedLit(int id, int count) {
      this.id = id;
      this.count = count;
   }

   public int compareTo(ValuedLit t) {
      if (this.count == 0) {
         return Integer.MAX_VALUE;
      } else {
         return t.count == 0 ? -1 : this.count - t.count;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else {
         return o instanceof ValuedLit ? ((ValuedLit)o).count == this.count : false;
      }
   }

   @Override
   public int hashCode() {
      return this.id;
   }

   @Override
   public String toString() {
      return "" + this.id + "(" + this.count + ")";
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compareTo(Object var1) {
      return this.compareTo((ValuedLit)var1);
   }
}
