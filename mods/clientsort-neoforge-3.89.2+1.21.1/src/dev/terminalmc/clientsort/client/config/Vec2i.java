package dev.terminalmc.clientsort.client.config;

public record Vec2i(int x, int y) {
   public Vec2i add(Vec2i other) {
      return new Vec2i(this.x + other.x, this.y + other.y);
   }

   public Vec2i subtract(Vec2i other) {
      return new Vec2i(this.x - other.x, this.y - other.y);
   }

   public boolean equals(Vec2i other) {
      return this.x == other.x && this.y == other.y;
   }
}
