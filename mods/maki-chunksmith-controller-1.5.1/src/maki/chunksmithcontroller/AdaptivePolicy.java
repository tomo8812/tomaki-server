package maki.chunksmithcontroller;

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
   private AdaptivePolicy.Result baseline;

   public int reset(Settings s, String why) {
      this.cap = Math.max(s.min, Math.min(s.max, 2));
      this.reason = why;
      this.phase = "baseline";
      this.window = new AdaptivePolicy.Window();
      this.settle = s.settleSeconds;
      this.cooldown = 0.0;
      this.pressureSamples = 0;
      this.baseline = null;
      this.withoutProgress = 0.0;
      this.failedExperiments = 0;
      return this.cap;
   }

   public int unavailable(Settings s, String why) {
      this.reset(s, why);
      this.cap = 1;
      return this.cap;
   }

   public int warmStart(Settings s, int storedCap, String why) {
      this.reset(s, why);
      this.cap = Math.max(s.min, Math.min(s.max, storedCap));
      this.settle = s.settleSeconds;
      this.reason = why;
      return this.cap;
   }

   public int update(AdaptiveSample x, int players, Settings s) {
      return this.update(x, players, s, 0L, false, false);
   }

   public int update(AdaptiveSample x, int players, Settings s, long pairCompletions, boolean pairAvailable, boolean admissionBlocked) {
      return this.update(x, players, s, pairCompletions, pairAvailable, admissionBlocked, 0L);
   }

   public int update(AdaptiveSample x, int players, Settings s, long pairCompletions, boolean pairAvailable, boolean admissionBlocked, long fastSignature) {
      return this.update(x, players, s, pairCompletions, pairAvailable, admissionBlocked, fastSignature, pairCompletions, 0L, 0L, 0L);
   }

   public int update(
      AdaptiveSample x,
      int players,
      Settings s,
      long pairCompletions,
      boolean pairAvailable,
      boolean admissionBlocked,
      long fastSignature,
      long newPairs,
      long repairPairs,
      long uncertainPairs,
      long oldestWipMillis
   ) {
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
               if (!pairAvailable) {
                  this.window = new AdaptivePolicy.Window();
                  this.withoutProgress = 0.0;
                  this.reason = "pair-proof-unavailable";
                  return this.cap;
               }

               if (pairCompletions > 0L) {
                  this.withoutProgress = 0.0;
               } else if (x.inFlight() <= 0 && x.completions() <= 0L) {
                  this.withoutProgress = 0.0;
               } else {
                  this.withoutProgress += dt;
               }

               if (this.withoutProgress >= s.noProgressSeconds && this.sinceReduction >= 10.0) {
                  this.cap = Math.max(s.min, this.cap - 1);
                  this.sinceReduction = 0.0;
                  this.withoutProgress = 0.0;
                  this.abandon(s, "no-pair-progress", s.retrySeconds);
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

               if (this.phase.equals("probe") && this.candidateCap > this.baselineCap && admissionBlocked) {
                  this.cap = this.baselineCap;
                  this.lastExperiment = "c=" + this.baselineCap + " -> " + this.candidateCap + " inconclusive: downstream WIP constrained";
                  this.experimentNumber++;
                  this.failedExperiments++;
                  this.abandon(s, "trial-downstream-constrained", s.retrySeconds);
                  return this.cap;
               }

               this.window.add(x, pairCompletions, admissionBlocked, fastSignature, newPairs, repairPairs, uncertainPairs, oldestWipMillis);
               if (!this.window.ready(s)) {
                  this.reason = "measure-" + this.phase;
                  return this.cap;
               }

               AdaptivePolicy.Result result = this.window.result();
               this.window = new AdaptivePolicy.Window();
               this.lastWindowRate = result.rate;
               if (result.fastStable && (!this.phase.equals("probe") || this.baseline == null || result.fastSignature == this.baseline.fastSignature)) {
                  if (result.events >= s.minimumEvents && !(result.rate <= 0.0)) {
                     if (this.phase.equals("baseline")) {
                        this.baseline = result;
                        this.baselineRate = result.rate;
                        this.confirmedBaselineRate = result.rate;
                        this.baselineCap = this.cap;
                        boolean storageBound = result.admissionFraction >= 0.25 || result.synchronousWrites > 0L || result.queueGrowth > 16 || x.nativeHeld();
                        boolean capBound = result.width >= Math.max(1.0, this.cap * 0.8);
                        if (storageBound && this.cap > s.min) {
                           this.candidateCap = this.cap - 1;
                        } else {
                           if (storageBound || this.cap >= s.max || !capBound) {
                              this.reason = storageBound ? "storage-bound-at-floor" : "stable-not-cap-bound";
                              this.cooldown = s.retrySeconds;
                              this.baseline = null;
                              return this.cap;
                           }

                           this.candidateCap = this.cap + 1;
                        }

                        this.cap = this.candidateCap;
                        this.phase = "probe";
                        this.settle = s.settleSeconds;
                        this.reason = this.candidateCap > this.baselineCap ? "probe-c-plus-one" : "probe-c-minus-one";
                        return this.cap;
                     } else {
                        this.probeRate = result.rate;
                        boolean lower = this.candidateCap < this.baselineCap;
                        if (Math.abs(result.inFlight - this.baseline.inFlight) < 0.15) {
                           this.cap = this.baselineCap;
                           this.lastExperiment = "c=" + this.baselineCap + " -> " + this.candidateCap + " inconclusive: actual in-flight unchanged";
                           this.experimentNumber++;
                           this.abandon(s, "native-dispatch-unchanged", s.retrySeconds);
                           return this.cap;
                        }

                        if (result.classifiedEvents >= s.minimumEvents
                           && this.baseline.classifiedEvents >= s.minimumEvents
                           && Math.abs(result.newRatio - this.baseline.newRatio) > 0.2) {
                           this.cap = this.baselineCap;
                           this.lastExperiment = "c=" + this.baselineCap + " -> " + this.candidateCap + " inconclusive: NEW/REPAIR mix changed";
                           this.experimentNumber++;
                           this.abandon(s, "work-class-mix-changed", s.retrySeconds);
                           return this.cap;
                        }

                        if (result.sourceProofRate + 0.05 < this.baseline.sourceProofRate) {
                           this.cap = this.baselineCap;
                           this.lastExperiment = "c=" + this.baselineCap + " -> " + this.candidateCap + " rejected: source proof rate regressed";
                           this.experimentNumber++;
                           this.abandon(s, "source-proof-regressed", s.retrySeconds);
                           return this.cap;
                        }

                        boolean tailSafe = result.queueGrowth <= Math.max(16, this.baseline.queueGrowth + 16)
                           && result.synchronousWrites <= this.baseline.synchronousWrites
                           && result.oldestWipMillis <= Math.max(15000L, this.baseline.oldestWipMillis + 5000L);
                        boolean rateSafe = lower
                           ? result.rate >= this.baseline.rate * 0.98
                           : result.rate >= this.baseline.rate * (1.0 + s.minimumGainPercent / 100.0);
                        boolean quotaSafe = result.throttledMicrosPerSecond
                           <= Math.max(this.baseline.throttledMicrosPerSecond * 1.25, this.baseline.throttledMicrosPerSecond + 10000.0);
                        boolean efficiencyBetter = !Double.isFinite(this.baseline.cpuPerPair)
                           || !Double.isFinite(result.cpuPerPair)
                           || result.cpuPerPair <= this.baseline.cpuPerPair
                           || result.queueGrowth < this.baseline.queueGrowth;
                        boolean accept = tailSafe && quotaSafe && rateSafe && (!lower || efficiencyBetter);
                        this.cap = accept ? this.candidateCap : this.baselineCap;
                        this.lastExperiment = "c="
                           + this.baselineCap
                           + " -> "
                           + this.candidateCap
                           + " pair/s="
                           + round(this.baseline.rate)
                           + "/"
                           + round(result.rate)
                           + " tailSafe="
                           + tailSafe
                           + " quotaSafe="
                           + quotaSafe
                           + " accepted="
                           + accept;
                        this.experimentNumber++;
                        if (accept) {
                           this.failedExperiments = 0;
                           this.reason = lower ? "same-goodput-lower-c" : "pair-goodput-gain";
                        } else {
                           this.failedExperiments++;
                           this.reason = !tailSafe ? "tail-debt-regressed" : (!quotaSafe ? "cpu-quota-throttle-regressed" : "no-pair-goodput-gain");
                        }

                        this.phase = "baseline";
                        this.baseline = null;
                        this.settle = s.settleSeconds;
                        this.cooldown = Math.min(600.0, s.retrySeconds * Math.pow(2.0, Math.min(3, this.failedExperiments / 2)));
                        return this.cap;
                     }
                  } else {
                     if (this.phase.equals("probe")) {
                        this.cap = this.baselineCap;
                     }

                     this.lastExperiment = "insufficient confirmed PAIR samples at c=" + this.cap;
                     this.experimentNumber++;
                     this.abandon(s, "insufficient-pair-samples", s.retrySeconds);
                     return this.cap;
                  }
               } else {
                  if (this.phase.equals("probe")) {
                     this.cap = this.baselineCap;
                  }

                  this.lastExperiment = "inconclusive: FAST WIP state changed";
                  this.experimentNumber++;
                  this.abandon(s, "fast-state-changed", s.retrySeconds);
                  return this.cap;
               }
            } else {
               this.pressureSamples++;
               if (hard || this.pressureSamples >= 3 && this.sinceReduction >= 10.0) {
                  int drop = hard ? Math.max(1, this.cap / 2) : 1;
                  this.cap = Math.max(s.min, this.cap - drop);
                  this.sinceReduction = 0.0;
                  this.pressureSamples = 0;
                  this.abandon(s, "pressure-recovery", s.retrySeconds);
               } else if (!this.phase.equals("baseline") && this.candidateCap > this.baselineCap) {
                  this.cap = this.baselineCap;
                  this.phase = "baseline";
                  this.window = new AdaptivePolicy.Window();
                  this.baseline = null;
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

   public double retryRemainingSeconds() {
      return this.cooldown;
   }

   private void abandon(Settings s, String why, double wait) {
      if (!this.phase.equals("baseline") && this.baseline != null) {
         this.cap = Math.min(this.cap, this.baselineCap);
      }

      this.phase = "baseline";
      this.window = new AdaptivePolicy.Window();
      this.baseline = null;
      this.settle = s.settleSeconds;
      this.cooldown = wait;
      this.reason = why;
   }

   private static double round(double v) {
      return Math.rint(v * 1000.0) / 1000.0;
   }

   private record Result(
      double rate,
      long events,
      double width,
      double inFlight,
      double cpu,
      double cpuPerPair,
      int queueGrowth,
      long synchronousWrites,
      double admissionFraction,
      long fastSignature,
      boolean fastStable,
      long classifiedEvents,
      double newRatio,
      double sourceProofRate,
      long oldestWipMillis,
      double throttledMicrosPerSecond
   ) {
   }

   private static final class Window {
      double seconds;
      double widthSeconds;
      double inFlightSeconds;
      double cpuSeconds;
      double cpuKnownSeconds;
      double blockedSeconds;
      long pairs;
      long synchronousWrites;
      long newPairs;
      long repairPairs;
      long uncertainPairs;
      long oldestWipMillis;
      long throttledMicros;
      int firstQueue = -1;
      int lastQueue;
      long fastSignature = Long.MIN_VALUE;
      boolean fastStable = true;

      void add(
         AdaptiveSample x,
         long pairCompletions,
         boolean admissionBlocked,
         long signature,
         long newPairCompletions,
         long repairPairCompletions,
         long uncertain,
         long oldestMillis
      ) {
         this.seconds = this.seconds + x.seconds();
         this.pairs = this.pairs + Math.max(0L, pairCompletions);
         this.widthSeconds = this.widthSeconds + x.nativeWidth() * x.seconds();
         this.inFlightSeconds = this.inFlightSeconds + x.inFlight() * x.seconds();
         this.newPairs = this.newPairs + Math.max(0L, newPairCompletions);
         this.repairPairs = this.repairPairs + Math.max(0L, repairPairCompletions);
         this.uncertainPairs = this.uncertainPairs + Math.max(0L, uncertain);
         this.oldestWipMillis = Math.max(this.oldestWipMillis, Math.max(0L, oldestMillis));
         if (Double.isFinite(x.cpu())) {
            this.cpuSeconds = this.cpuSeconds + x.cpu() * x.seconds();
            this.cpuKnownSeconds = this.cpuKnownSeconds + x.seconds();
         }

         this.synchronousWrites = this.synchronousWrites + x.synchronousWrites();
         this.throttledMicros = this.throttledMicros + Math.max(0L, x.throttledMicros());
         if (this.firstQueue < 0) {
            this.firstQueue = x.lodQueue();
         }

         this.lastQueue = x.lodQueue();
         if (admissionBlocked) {
            this.blockedSeconds = this.blockedSeconds + x.seconds();
         }

         if (this.fastSignature == Long.MIN_VALUE) {
            this.fastSignature = signature;
         } else if (this.fastSignature != signature) {
            this.fastStable = false;
         }
      }

      boolean ready(Settings s) {
         return this.seconds >= s.windowSeconds && (this.pairs >= s.minimumEvents || this.seconds >= 3.0 * s.windowSeconds);
      }

      AdaptivePolicy.Result result() {
         long classified = this.newPairs + this.repairPairs;
         double proofDenominator = this.pairs + this.uncertainPairs;
         return new AdaptivePolicy.Result(
            this.seconds > 0.0 ? this.pairs / this.seconds : 0.0,
            this.pairs,
            this.seconds > 0.0 ? this.widthSeconds / this.seconds : 0.0,
            this.seconds > 0.0 ? this.inFlightSeconds / this.seconds : 0.0,
            this.cpuKnownSeconds > 0.0 ? this.cpuSeconds / this.cpuKnownSeconds : Double.NaN,
            this.pairs > 0L && this.cpuKnownSeconds > 0.0 ? this.cpuSeconds / this.pairs : Double.NaN,
            this.lastQueue - this.firstQueue,
            this.synchronousWrites,
            this.seconds > 0.0 ? this.blockedSeconds / this.seconds : 0.0,
            this.fastSignature == Long.MIN_VALUE ? 0L : this.fastSignature,
            this.fastStable,
            classified,
            classified > 0L ? (double)this.newPairs / classified : 0.0,
            proofDenominator > 0.0 ? this.pairs / proofDenominator : 1.0,
            this.oldestWipMillis,
            this.seconds > 0.0 ? this.throttledMicros / this.seconds : 0.0
         );
      }
   }
}
