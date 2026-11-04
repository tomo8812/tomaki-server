package dev.emi.emi.api.widget;

public record Bounds(int x, int y, int width, int height) {
   public static final Bounds EMPTY = new Bounds(0, 0, 0, 0);

   public int left() {
      return this.x;
   }

   public int right() {
      return this.x + this.width;
   }

   public int top() {
      return this.y;
   }

   public int bottom() {
      return this.y + this.height;
   }

   public boolean contains(int x, int y) {
      return x >= this.x() && x < this.x() + this.width() && y >= this.y() && y < this.y() + this.height();
   }

   public boolean empty() {
      return this.width <= 0 || this.height <= 0;
   }

   public Bounds overlap(Bounds another) {
      int left = Math.max(this.left(), another.left());
      int top = Math.max(this.top(), another.top());
      return new Bounds(left, top, Math.min(this.right(), another.right()) - left, Math.min(this.bottom(), another.bottom()) - top);
   }
}
