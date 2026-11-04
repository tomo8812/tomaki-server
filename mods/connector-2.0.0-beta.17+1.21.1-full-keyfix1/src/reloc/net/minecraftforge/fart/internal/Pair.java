package reloc.net.minecraftforge.fart.internal;

class Pair<L, R> {
   private final L left;
   private final R right;

   Pair(L left, R right) {
      this.left = left;
      this.right = right;
   }

   public L getLeft() {
      return this.left;
   }

   public R getRight() {
      return this.right;
   }
}
