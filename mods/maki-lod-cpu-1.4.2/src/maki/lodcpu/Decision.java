package maki.lodcpu;

public final class Decision {
   public int cap = 2;
   private int healthy;
   private int overloaded;
   public String reason = "warmup";

   public int idle(Settings s) {
      this.healthy = this.overloaded = 0;
      this.cap = Math.max(s.min, Math.min(s.max, 2));
      this.reason = "generation-idle";
      return this.cap;
   }

   public int update(double cpu, double mspt, double heap, int players, Settings s) {
      this.cap = Math.max(s.min, Math.min(s.max, this.cap));
      if (players > 0) {
         this.healthy = this.overloaded = 0;
         boolean busy = !Double.isFinite(cpu) || !Double.isFinite(mspt) || cpu >= s.highCpu || mspt >= 30.0 || heap >= s.highHeap - 5.0;
         this.cap = busy ? 1 : Math.max(1, Math.min(s.onlineMax, s.max));
         this.reason = busy ? "online-busy" : "online-two-slots";
         if (!s.generateWhileOnline) {
            this.cap = 1;
            this.reason = "players-online-paused";
         }
      } else if (!Double.isFinite(cpu) || cpu < 0.0 || !Double.isFinite(mspt)) {
         this.healthy = this.overloaded = 0;
         this.cap = s.min;
         this.reason = "measurement-unavailable";
      } else if (cpu >= s.emergencyCpu || mspt >= s.emergencyMspt || heap >= s.highHeap) {
         this.healthy = this.overloaded = 0;
         this.cap = Math.max(s.min, this.cap - 2);
         this.reason = "overload";
      } else if (cpu >= s.highCpu || mspt >= s.highMspt) {
         this.healthy = 0;
         if (++this.overloaded >= s.downSamples) {
            this.cap = Math.max(s.min, this.cap - 1);
            this.overloaded = 0;
         }

         this.reason = "busy";
      } else if (cpu < s.lowCpu && mspt < s.lowMspt && heap < s.highHeap - 5.0) {
         this.overloaded = 0;
         if (++this.healthy >= s.upSamples) {
            this.cap = Math.min(s.max, this.cap + 1);
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
