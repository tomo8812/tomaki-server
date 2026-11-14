package maki.chunksmithfixes;

final class LodChangeCounter {
   private long changed;
   private long saved;
   private long lastChanged;

   void changed(long now) {
      this.changed++;
      this.lastChanged = now;
   }

   boolean due(int threshold) {
      return this.changed - this.saved >= threshold;
   }

   boolean ready(int threshold, long now, long quietNanos) {
      return this.due(threshold) && now - this.lastChanged >= quietNanos;
   }

   long snapshot() {
      return this.changed;
   }

   void saved(long snapshot) {
      this.saved = Math.max(this.saved, snapshot);
   }

   boolean clean() {
      return this.changed == this.saved;
   }

   void restore(int threshold, long now) {
      this.changed = Math.max(this.changed, this.saved + threshold);
      this.lastChanged = now;
   }
}
