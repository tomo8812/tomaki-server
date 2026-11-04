package maki.lodcpu;

public final class Decision {
   public int cap = 2;
   private int healthy;
   private int overloaded;
   public String reason = "warmup";

   public int idle(Settings var1) {
      this.healthy = this.overloaded = 0;
      this.cap = Math.max(var1.min, Math.min(var1.max, 2));
      this.reason = "generation-idle";
      return this.cap;
   }

   public int update(double var1, double var3, double var5, int var7, Settings var8) {
      this.cap = Math.max(var8.min, Math.min(var8.max, this.cap));
      if (var7 > 0) {
         this.healthy = this.overloaded = 0;
         boolean var9 = !Double.isFinite(var1) || !Double.isFinite(var3) || var1 >= var8.highCpu || var3 >= 30.0 || var5 >= var8.highHeap - 5.0;
         this.cap = var9 ? 1 : Math.max(1, Math.min(var8.onlineMax, var8.max));
         this.reason = var9 ? "online-busy" : "online-two-slots";
         if (!var8.generateWhileOnline) {
            this.cap = 1;
            this.reason = "players-online-paused";
         }
      } else if (!Double.isFinite(var1) || var1 < 0.0 || !Double.isFinite(var3)) {
         this.healthy = this.overloaded = 0;
         this.cap = var8.min;
         this.reason = "measurement-unavailable";
      } else if (var1 >= var8.emergencyCpu || var3 >= var8.emergencyMspt || var5 >= var8.highHeap) {
         this.healthy = this.overloaded = 0;
         this.cap = Math.max(var8.min, this.cap - 2);
         this.reason = "overload";
      } else if (var1 >= var8.highCpu || var3 >= var8.highMspt) {
         this.healthy = 0;
         if (++this.overloaded >= var8.downSamples) {
            this.cap = Math.max(var8.min, this.cap - 1);
            this.overloaded = 0;
         }

         this.reason = "busy";
      } else if (var1 < var8.lowCpu && var3 < var8.lowMspt && var5 < var8.highHeap - 5.0) {
         this.overloaded = 0;
         if (++this.healthy >= var8.upSamples) {
            this.cap = Math.min(var8.max, this.cap + 1);
            this.healthy = 0;
         }

         this.reason = "headroom";
      } else {
         this.healthy = this.overloaded = 0;
         this.reason = "steady";
      }

      return this.cap;
   }
}
