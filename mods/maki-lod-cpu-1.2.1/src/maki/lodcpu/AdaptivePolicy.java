package maki.lodcpu;

public final class AdaptivePolicy {
   public int cap = 2;
   public String reason = "warmup";
   public String phase = "baseline";
   public String lastExperiment = "none";
   public double lastWindowRate;
   public double baselineRate;
   public double probeRate;
   public double confirmedBaselineRate;
   public long experimentNumber;
   private AdaptivePolicy.Window window = new AdaptivePolicy.Window();
   private double settle;
   private double cooldown;
   private double sinceReduction = 1000.0;
   private double withoutProgress;
   private int pressureSamples;
   private int baselineCap;
   private int candidateCap;
   private int failedExperiments;
   private boolean tryLower;
   private AdaptivePolicy.Result baseline;
   private AdaptivePolicy.Result probe;
   private AdaptivePolicy.Result confirmation;

   public int reset(Settings var1, String var2) {
      this.cap = Math.max(var1.min, Math.min(var1.max, 2));
      this.reason = var2;
      this.phase = "baseline";
      this.window = new AdaptivePolicy.Window();
      this.settle = var1.settleSeconds;
      this.cooldown = 0.0;
      this.pressureSamples = 0;
      this.baseline = this.probe = null;
      this.tryLower = false;
      this.withoutProgress = 0.0;
      this.failedExperiments = 0;
      return this.cap;
   }

   public int unavailable(Settings var1, String var2) {
      this.reset(var1, var2);
      this.cap = 1;
      return this.cap;
   }

   public int update(AdaptiveSample var1, int var2, Settings var3) {
      this.cap = Math.max(var3.min, Math.min(var3.max, this.cap));
      if (var2 > 0) {
         this.reset(var3, "players-online-paused");
         this.cap = 1;
         return this.cap;
      }

      if (Double.isFinite(var1.seconds()) && !(var1.seconds() <= 0.0)) {
         double var4 = var1.seconds();
         this.sinceReduction += var4;
         this.cooldown = Math.max(0.0, this.cooldown - var4);
         if (Double.isFinite(var1.mspt()) && Double.isFinite(var1.heap())) {
            boolean var6 = var1.mspt() >= var3.emergencyMspt || var1.heap() >= 90.0 || var1.oomKills() > 0L;
            String var7 = var1.oomKills() > 0L
               ? "cgroup-oom"
               : (
                  var1.mspt() >= var3.highMspt
                     ? "tick-pressure"
                     : (
                        var1.heap() >= var3.highHeap
                           ? "heap-pressure"
                           : (
                              var1.memoryHeadroomMb() >= 0.0 && var1.memoryHeadroomMb() < var3.memoryReserveMb
                                 ? "memory-headroom"
                                 : (
                                    !(var1.gc() >= 50.0) && (!(var1.gc() >= var3.gcPressurePercent) || !(var1.heap() >= 55.0))
                                       ? (!var1.nativeHeld() && var1.synchronousWrites() <= 0L ? "" : "native-backpressure")
                                       : "gc-pressure"
                                 )
                           )
                     )
               );
            if (!var6 && var7.isEmpty()) {
               this.pressureSamples = 0;
               if (var1.storeAvailable() ? var1.lodWrites() <= 0L : var1.completions() <= 0L) {
                  if (var1.inFlight() <= 0 && var1.completions() <= 0L) {
                     this.withoutProgress = 0.0;
                  } else {
                     this.withoutProgress += var4;
                  }
               } else {
                  this.withoutProgress = 0.0;
               }

               if (this.withoutProgress >= var3.noProgressSeconds && this.sinceReduction >= 10.0) {
                  this.cap = Math.max(var3.min, this.cap - Math.max(1, this.cap / 2));
                  this.sinceReduction = 0.0;
                  this.withoutProgress = 0.0;
                  this.abandon(var3, "no-progress", var3.retrySeconds);
                  return this.cap;
               }

               if (this.cooldown > 0.0) {
                  this.reason = "observe-cooldown";
                  return this.cap;
               }

               if (this.settle > 0.0) {
                  this.settle -= var4;
                  this.reason = "settling-" + this.phase;
                  return this.cap;
               }

               if (var1.inFlight() > this.cap + 1) {
                  this.reason = "draining-old-width";
                  return this.cap;
               }

               this.window.add(var1);
               if (!(this.window.seconds < var3.windowSeconds)
                  && (this.window.events() >= var3.minimumEvents || !(this.window.seconds < 3 * var3.windowSeconds))) {
                  AdaptivePolicy.Result var23 = this.window.result();
                  this.window = new AdaptivePolicy.Window();
                  this.lastWindowRate = var23.rate;
                  if (var23.rate <= 0.0) {
                     this.abandon(var3, "waiting-for-output", var3.retrySeconds);
                     return this.cap;
                  }

                  if (this.phase.equals("baseline")) {
                     this.baseline = var23;
                     this.baselineRate = var23.rate;
                     this.baselineCap = this.cap;
                     if (this.cap == var3.max) {
                        this.tryLower = true;
                     }

                     if (this.cap == var3.min) {
                        this.tryLower = false;
                     }

                     int var9 = Math.max(1, Math.min(var3.maxStep, this.cap / 2));
                     if (Double.isFinite(var23.cpu) && var23.cpu < 70.0 && !this.tryLower) {
                        var9 = Math.max(var9, Math.min(var3.maxStep, this.cap));
                     }

                     this.candidateCap = this.tryLower ? Math.max(var3.min, this.cap - var9) : Math.min(var3.max, this.cap + var9);
                     if (this.candidateCap == this.cap) {
                        this.abandon(var3, "at-configured-bound", var3.retrySeconds);
                        return this.cap;
                     }

                     this.cap = this.candidateCap;
                     this.phase = "probe";
                     this.settle = var3.settleSeconds;
                     this.reason = "probe-width";
                  } else if (this.phase.equals("probe")) {
                     this.probe = var23;
                     this.probeRate = var23.rate;
                     this.cap = this.baselineCap;
                     this.phase = "confirm";
                     this.settle = var3.settleSeconds;
                     this.reason = "confirm-baseline";
                  } else {
                     boolean var24 = this.phase.equals("repeat");
                     if (!var24) {
                        this.confirmation = var23;
                     }

                     this.confirmedBaselineRate = this.confirmation.rate;
                     double var10 = (this.baseline.rate + this.confirmation.rate) / 2.0;
                     boolean var12 = Math.max(this.baseline.rate, this.confirmation.rate) <= 1.35 * Math.min(this.baseline.rate, this.confirmation.rate);
                     boolean var13 = this.probe.width >= this.candidateCap * 0.8;
                     boolean var14 = this.probe.queueGrowth <= 64 && this.probe.synchronousWrites == 0L;
                     boolean var15 = this.baseline.store == this.probe.store && this.probe.store == this.confirmation.store && this.probe.store == var23.store;
                     boolean var16 = this.candidateCap < this.baselineCap;
                     boolean var17 = this.probe.rate >= var10 * (var16 ? 0.98 : 1.0 + var3.minimumGainPercent / 100.0);
                     double var18 = Math.max(this.baseline.rate, this.confirmation.rate) * (1.0 + var3.minimumGainPercent / 100.0);
                     boolean var20 = !var16 && var13 && var14 && var15 && this.probe.rate >= var18;
                     if (!var12 && var20 && !var24) {
                        this.cap = this.candidateCap;
                        this.phase = "repeat";
                        this.settle = var3.settleSeconds;
                        this.reason = "repeat-promising-width";
                        return this.cap;
                     }

                     boolean var21 = var24
                        && var20
                        && var23.rate >= var18
                        && var23.width >= this.candidateCap * 0.8
                        && var23.queueGrowth <= 64
                        && var23.synchronousWrites == 0L;
                     boolean var22 = var21 || var12 && var13 && var14 && var15 && var17;
                     this.cap = var22 ? this.candidateCap : this.baselineCap;
                     this.lastExperiment = "A="
                        + this.baselineCap
                        + " B="
                        + this.candidateCap
                        + " rates="
                        + round(this.baseline.rate)
                        + "/"
                        + round(this.probe.rate)
                        + "/"
                        + round(this.confirmation.rate)
                        + (var24 ? "/" + round(var23.rate) : "")
                        + " accepted="
                        + var22;
                     this.experimentNumber++;
                     if (var22) {
                        this.tryLower = false;
                        this.failedExperiments = 0;
                        this.reason = var16 ? "same-speed-lower-width" : "throughput-gain";
                     } else {
                        this.tryLower = !this.tryLower;
                        this.reason = !var15
                           ? "output-source-changed"
                           : (
                              var24
                                 ? "repeat-not-confirmed"
                                 : (!var12 ? "baseline-varied" : (!var13 ? "native-width-limited" : (!var14 ? "storage-backlog" : "no-throughput-gain")))
                           );
                        this.failedExperiments++;
                        this.cooldown = Math.min(600.0, var3.retrySeconds * Math.pow(2.0, Math.min(3, this.failedExperiments / 2)));
                     }

                     this.phase = "baseline";
                     this.baseline = this.probe = null;
                     this.settle = var3.settleSeconds;
                  }

                  return this.cap;
               } else {
                  this.reason = "measure-" + this.phase;
                  return this.cap;
               }
            } else {
               this.pressureSamples++;
               if (var6 || this.pressureSamples >= 3 && this.sinceReduction >= 10.0) {
                  int var8 = var6 ? Math.max(1, this.cap / 2) : Math.max(1, this.cap / 4);
                  this.cap = Math.max(var3.min, this.cap - var8);
                  this.sinceReduction = 0.0;
                  this.pressureSamples = 0;
                  this.abandon(var3, "pressure-recovery", var3.retrySeconds);
               } else {
                  if (!this.phase.equals("baseline")) {
                     this.cap = Math.min(this.cap, this.baselineCap);
                  }

                  this.window = new AdaptivePolicy.Window();
                  this.baseline = this.probe = null;
                  this.phase = "baseline";
                  this.settle = var3.settleSeconds;
               }

               this.reason = var6 ? "emergency-" + (var7.isEmpty() ? "heap" : var7) : var7;
               return this.cap;
            }
         } else {
            return this.unavailable(var3, "measurement-unavailable");
         }
      } else {
         return this.cap;
      }
   }

   public double retryRemainingSeconds() {
      return this.cooldown;
   }

   private void abandon(Settings var1, String var2, double var3) {
      if (!this.phase.equals("baseline")) {
         this.cap = Math.min(this.cap, this.baselineCap);
      }

      this.phase = "baseline";
      this.window = new AdaptivePolicy.Window();
      this.baseline = this.probe = null;
      this.settle = var1.settleSeconds;
      this.cooldown = var3;
      this.reason = var2;
   }

   private static double round(double var0) {
      return Math.rint(var0 * 1000.0) / 1000.0;
   }

   private record Result(double rate, double width, double cpu, boolean store, int queueGrowth, long synchronousWrites) {
   }

   private static final class Window {
      double seconds;
      double widthSeconds;
      double cpuSeconds;
      double cpuKnownSeconds;
      long complete;
      long writes;
      long synchronousWrites;
      boolean store = true;
      int firstQueue = -1;
      int lastQueue;

      void add(AdaptiveSample var1) {
         this.seconds = this.seconds + var1.seconds();
         this.widthSeconds = this.widthSeconds + var1.nativeWidth() * var1.seconds();
         if (Double.isFinite(var1.cpu())) {
            this.cpuSeconds = this.cpuSeconds + var1.cpu() * var1.seconds();
            this.cpuKnownSeconds = this.cpuKnownSeconds + var1.seconds();
         }

         this.complete = this.complete + var1.completions();
         this.writes = this.writes + var1.lodWrites();
         this.store = this.store & var1.storeAvailable();
         this.synchronousWrites = this.synchronousWrites + var1.synchronousWrites();
         if (this.firstQueue < 0) {
            this.firstQueue = var1.lodQueue();
         }

         this.lastQueue = var1.lodQueue();
      }

      long events() {
         return this.store ? Math.min(this.writes, this.complete) : this.complete;
      }

      AdaptivePolicy.Result result() {
         return new AdaptivePolicy.Result(
            this.events() / this.seconds,
            this.widthSeconds / this.seconds,
            this.cpuKnownSeconds > 0.0 ? this.cpuSeconds / this.cpuKnownSeconds : Double.NaN,
            this.store,
            this.lastQueue - this.firstQueue,
            this.synchronousWrites
         );
      }
   }
}
