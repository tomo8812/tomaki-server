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

   public int reset(Settings s, String why) {
      this.cap = Math.max(s.min, Math.min(s.max, 2));
      this.reason = why;
      this.phase = "baseline";
      this.window = new AdaptivePolicy.Window();
      this.settle = s.settleSeconds;
      this.cooldown = 0.0;
      this.pressureSamples = 0;
      this.baseline = this.probe = null;
      this.tryLower = false;
      this.withoutProgress = 0.0;
      this.failedExperiments = 0;
      return this.cap;
   }

   public int unavailable(Settings s, String why) {
      this.reset(s, why);
      this.cap = 1;
      return this.cap;
   }

   public int update(AdaptiveSample x, int players, Settings s) {
      this.cap = Math.max(s.min, Math.min(s.max, this.cap));
      if (players > 0) {
         this.reset(s, "players-online-paused");
         this.cap = 1;
         return this.cap;
      }

      if (Double.isFinite(x.seconds()) && !(x.seconds() <= 0.0)) {
         double dt = x.seconds();
         this.sinceReduction += dt;
         this.cooldown = Math.max(0.0, this.cooldown - dt);
         if (Double.isFinite(x.mspt()) && Double.isFinite(x.heap())) {
            boolean hard = x.mspt() >= s.emergencyMspt || x.heap() >= 90.0 || x.oomKills() > 0L;
            String pressure = x.oomKills() > 0L
               ? "cgroup-oom"
               : (
                  x.mspt() >= s.highMspt
                     ? "tick-pressure"
                     : (
                        x.heap() >= s.highHeap
                           ? "heap-pressure"
                           : (
                              x.memoryHeadroomMb() >= 0.0 && x.memoryHeadroomMb() < s.memoryReserveMb
                                 ? "memory-headroom"
                                 : (
                                    !(x.gc() >= 50.0) && (!(x.gc() >= s.gcPressurePercent) || !(x.heap() >= 55.0))
                                       ? (!x.nativeHeld() && x.synchronousWrites() <= 0L ? "" : "native-backpressure")
                                       : "gc-pressure"
                                 )
                           )
                     )
               );
            if (!hard && pressure.isEmpty()) {
               this.pressureSamples = 0;
               if (x.storeAvailable() ? x.lodWrites() <= 0L : x.completions() <= 0L) {
                  if (x.inFlight() <= 0 && x.completions() <= 0L) {
                     this.withoutProgress = 0.0;
                  } else {
                     this.withoutProgress += dt;
                  }
               } else {
                  this.withoutProgress = 0.0;
               }

               if (this.withoutProgress >= s.noProgressSeconds && this.sinceReduction >= 10.0) {
                  this.cap = Math.max(s.min, this.cap - Math.max(1, this.cap / 2));
                  this.sinceReduction = 0.0;
                  this.withoutProgress = 0.0;
                  this.abandon(s, "no-progress", s.retrySeconds);
                  return this.cap;
               }

               if (this.cooldown > 0.0) {
                  this.reason = "observe-cooldown";
                  return this.cap;
               }

               if (this.settle > 0.0) {
                  this.settle -= dt;
                  this.reason = "settling-" + this.phase;
                  return this.cap;
               }

               if (x.inFlight() > this.cap + 1) {
                  this.reason = "draining-old-width";
                  return this.cap;
               }

               this.window.add(x);
               if (!(this.window.seconds < s.windowSeconds) && (this.window.events() >= s.minimumEvents || !(this.window.seconds < 3 * s.windowSeconds))) {
                  AdaptivePolicy.Result r = this.window.result();
                  this.window = new AdaptivePolicy.Window();
                  this.lastWindowRate = r.rate;
                  if (r.rate <= 0.0) {
                     this.abandon(s, "waiting-for-output", s.retrySeconds);
                     return this.cap;
                  }

                  if (this.phase.equals("baseline")) {
                     this.baseline = r;
                     this.baselineRate = r.rate;
                     this.baselineCap = this.cap;
                     if (this.cap == s.max) {
                        this.tryLower = true;
                     }

                     if (this.cap == s.min) {
                        this.tryLower = false;
                     }

                     int step = Math.max(1, Math.min(s.maxStep, this.cap / 2));
                     if (Double.isFinite(r.cpu) && r.cpu < 70.0 && !this.tryLower) {
                        step = Math.max(step, Math.min(s.maxStep, this.cap));
                     }

                     this.candidateCap = this.tryLower ? Math.max(s.min, this.cap - step) : Math.min(s.max, this.cap + step);
                     if (this.candidateCap == this.cap) {
                        this.abandon(s, "at-configured-bound", s.retrySeconds);
                        return this.cap;
                     }

                     this.cap = this.candidateCap;
                     this.phase = "probe";
                     this.settle = s.settleSeconds;
                     this.reason = "probe-width";
                  } else if (this.phase.equals("probe")) {
                     this.probe = r;
                     this.probeRate = r.rate;
                     this.cap = this.baselineCap;
                     this.phase = "confirm";
                     this.settle = s.settleSeconds;
                     this.reason = "confirm-baseline";
                  } else {
                     boolean repeated = this.phase.equals("repeat");
                     if (!repeated) {
                        this.confirmation = r;
                     }

                     this.confirmedBaselineRate = this.confirmation.rate;
                     double reference = (this.baseline.rate + this.confirmation.rate) / 2.0;
                     boolean terrainStable = Math.max(this.baseline.rate, this.confirmation.rate)
                        <= 1.35 * Math.min(this.baseline.rate, this.confirmation.rate);
                     boolean delivered = this.probe.width >= this.candidateCap * 0.8;
                     boolean queuesStable = this.probe.queueGrowth <= 64 && this.probe.synchronousWrites == 0L;
                     boolean sameBasis = this.baseline.store == this.probe.store && this.probe.store == this.confirmation.store && this.probe.store == r.store;
                     boolean lower = this.candidateCap < this.baselineCap;
                     boolean faster = this.probe.rate >= reference * (lower ? 0.98 : 1.0 + s.minimumGainPercent / 100.0);
                     double strongRate = Math.max(this.baseline.rate, this.confirmation.rate) * (1.0 + s.minimumGainPercent / 100.0);
                     boolean promising = !lower && delivered && queuesStable && sameBasis && this.probe.rate >= strongRate;
                     if (!terrainStable && promising && !repeated) {
                        this.cap = this.candidateCap;
                        this.phase = "repeat";
                        this.settle = s.settleSeconds;
                        this.reason = "repeat-promising-width";
                        return this.cap;
                     }

                     boolean repeatPassed = repeated
                        && promising
                        && r.rate >= strongRate
                        && r.width >= this.candidateCap * 0.8
                        && r.queueGrowth <= 64
                        && r.synchronousWrites == 0L;
                     boolean accept = repeatPassed || terrainStable && delivered && queuesStable && sameBasis && faster;
                     this.cap = accept ? this.candidateCap : this.baselineCap;
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
                        + (repeated ? "/" + round(r.rate) : "")
                        + " accepted="
                        + accept;
                     this.experimentNumber++;
                     if (accept) {
                        this.tryLower = false;
                        this.failedExperiments = 0;
                        this.reason = lower ? "same-speed-lower-width" : "throughput-gain";
                     } else {
                        this.tryLower = !this.tryLower;
                        this.reason = !sameBasis
                           ? "output-source-changed"
                           : (
                              repeated
                                 ? "repeat-not-confirmed"
                                 : (
                                    !terrainStable
                                       ? "baseline-varied"
                                       : (!delivered ? "native-width-limited" : (!queuesStable ? "storage-backlog" : "no-throughput-gain"))
                                 )
                           );
                        this.failedExperiments++;
                        this.cooldown = Math.min(600.0, s.retrySeconds * Math.pow(2.0, Math.min(3, this.failedExperiments / 2)));
                     }

                     this.phase = "baseline";
                     this.baseline = this.probe = null;
                     this.settle = s.settleSeconds;
                  }

                  return this.cap;
               } else {
                  this.reason = "measure-" + this.phase;
                  return this.cap;
               }
            } else {
               this.pressureSamples++;
               if (hard || this.pressureSamples >= 3 && this.sinceReduction >= 10.0) {
                  int drop = hard ? Math.max(1, this.cap / 2) : Math.max(1, this.cap / 4);
                  this.cap = Math.max(s.min, this.cap - drop);
                  this.sinceReduction = 0.0;
                  this.pressureSamples = 0;
                  this.abandon(s, "pressure-recovery", s.retrySeconds);
               } else {
                  if (!this.phase.equals("baseline")) {
                     this.cap = Math.min(this.cap, this.baselineCap);
                  }

                  this.window = new AdaptivePolicy.Window();
                  this.baseline = this.probe = null;
                  this.phase = "baseline";
                  this.settle = s.settleSeconds;
               }

               this.reason = hard ? "emergency-" + (pressure.isEmpty() ? "heap" : pressure) : pressure;
               return this.cap;
            }
         } else {
            return this.unavailable(s, "measurement-unavailable");
         }
      } else {
         return this.cap;
      }
   }

   public int updateSafetyOnly(AdaptiveSample x, int players, Settings s) {
      this.cap = Math.max(s.min, Math.min(s.max, this.cap));
      if (players > 0) {
         this.reset(s, "players-online-paused");
         this.cap = 1;
         return this.cap;
      }

      if (Double.isFinite(x.seconds()) && !(x.seconds() <= 0.0)) {
         this.sinceReduction = this.sinceReduction + x.seconds();
         if (Double.isFinite(x.mspt()) && Double.isFinite(x.heap())) {
            boolean hard = x.mspt() >= s.emergencyMspt || x.heap() >= 90.0 || x.oomKills() > 0L;
            String pressure = x.oomKills() > 0L
               ? "cgroup-oom"
               : (
                  x.mspt() >= s.highMspt
                     ? "tick-pressure"
                     : (
                        x.heap() >= s.highHeap
                           ? "heap-pressure"
                           : (
                              x.memoryHeadroomMb() >= 0.0 && x.memoryHeadroomMb() < s.memoryReserveMb
                                 ? "memory-headroom"
                                 : (
                                    !(x.gc() >= 50.0) && (!(x.gc() >= s.gcPressurePercent) || !(x.heap() >= 55.0))
                                       ? (!x.nativeHeld() && x.synchronousWrites() <= 0L ? "" : "native-backpressure")
                                       : "gc-pressure"
                                 )
                           )
                     )
               );
            if (!hard && pressure.isEmpty()) {
               this.pressureSamples = 0;
               this.withoutProgress = 0.0;
               this.reason = "shared-backfill-pause";
               return this.cap;
            }

            this.pressureSamples++;
            if (hard || this.pressureSamples >= 3 && this.sinceReduction >= 10.0) {
               int drop = hard ? Math.max(1, this.cap / 2) : Math.max(1, this.cap / 4);
               this.cap = Math.max(s.min, this.cap - drop);
               this.sinceReduction = 0.0;
               this.pressureSamples = 0;
               this.abandon(s, "pressure-recovery", s.retrySeconds);
            }

            this.reason = hard ? "emergency-" + (pressure.isEmpty() ? "heap" : pressure) : pressure;
            return this.cap;
         } else {
            this.reason = "shared-backfill-measurement-unavailable";
            return this.cap;
         }
      } else {
         return this.cap;
      }
   }

   public double retryRemainingSeconds() {
      return this.cooldown;
   }

   private void abandon(Settings s, String why, double wait) {
      if (!this.phase.equals("baseline")) {
         this.cap = Math.min(this.cap, this.baselineCap);
      }

      this.phase = "baseline";
      this.window = new AdaptivePolicy.Window();
      this.baseline = this.probe = null;
      this.settle = s.settleSeconds;
      this.cooldown = wait;
      this.reason = why;
   }

   private static double round(double v) {
      return Math.rint(v * 1000.0) / 1000.0;
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

      void add(AdaptiveSample x) {
         this.seconds = this.seconds + x.seconds();
         this.widthSeconds = this.widthSeconds + x.nativeWidth() * x.seconds();
         if (Double.isFinite(x.cpu())) {
            this.cpuSeconds = this.cpuSeconds + x.cpu() * x.seconds();
            this.cpuKnownSeconds = this.cpuKnownSeconds + x.seconds();
         }

         this.complete = this.complete + x.completions();
         this.writes = this.writes + x.lodWrites();
         this.store = this.store & x.storeAvailable();
         this.synchronousWrites = this.synchronousWrites + x.synchronousWrites();
         if (this.firstQueue < 0) {
            this.firstQueue = x.lodQueue();
         }

         this.lastQueue = x.lodQueue();
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
