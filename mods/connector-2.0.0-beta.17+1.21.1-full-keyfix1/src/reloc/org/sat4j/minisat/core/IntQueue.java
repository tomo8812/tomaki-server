package reloc.org.sat4j.minisat.core;

import java.io.Serializable;

public final class IntQueue implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final int INITIAL_QUEUE_CAPACITY = 10;
   private int[] myarray = new int[10];
   private int size = 0;
   private int first = 0;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !IntQueue.class.desiredAssertionStatus();

   public void insert(int x) {
      assert this.size < this.myarray.length;
      this.myarray[this.size++] = x;
   }

   public int dequeue() {
      assert this.first < this.size;
      return this.myarray[this.first++];
   }

   public void clear() {
      this.size = 0;
      this.first = 0;
   }

   public int size() {
      return this.size - this.first;
   }

   public void ensure(int nsize) {
      if (nsize >= this.myarray.length) {
         int[] narray = new int[Math.max(nsize, this.size * 2)];
         System.arraycopy(this.myarray, 0, narray, 0, this.size);
         this.myarray = narray;
      }
   }

   @Override
   public String toString() {
      StringBuilder stb = new StringBuilder();
      stb.append(">");

      for (int i = this.first; i < this.size - 1; i++) {
         stb.append(this.myarray[i]);
         stb.append(" ");
      }

      if (this.first != this.size) {
         stb.append(this.myarray[this.size - 1]);
      }

      stb.append("<");
      return stb.toString();
   }
}
