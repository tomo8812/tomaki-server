package maki.millenairelod;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class ReplayController {
   private static final int MAX_BURST = 64;
   private static final int QUEUE_TARGET_AT_FULL_HEADROOM = 72;
   private static final int QUEUE_TARGET_AT_LOW_HEADROOM = 12;
   private static final int MAIN_HARD_STOP = 72;
   private static final int LONE_HARD_STOP = 64;
   private static final int[] DEFER_BACKOFF_TICKS = new int[]{20, 40, 80, 160, 200};
   private static final Map<Object, ReplayController.LevelState> LEVEL_STATES = new WeakHashMap<>();
   private static final Map<Object, ReplayController.QueueState> QUEUE_STATES = new WeakHashMap<>();
   private static volatile ReplayController.DeferredAccess deferredAccess;
   private static volatile ReplayController.RuntimeAccess runtimeAccess;
   private static volatile ReplayController.ChunksmithAccess chunksmithAccess;
   private static volatile ReplayController.ConfigAccess configAccess;

   private ReplayController() {
   }

   public static void replay(Object var0, Object var1) {
      if (var0 != null && var1 != null) {
         ReplayController.RuntimeAccess var2 = runtime(var0);
         if (var2 != null) {
            long var3 = var2.gameTime(var0);
            if (var3 >= 0L && var3 % 20L == 0L) {
               if (generationEnabled()) {
                  ReplayController.DeferredAccess var5 = deferred(var0);
                  if (var5 != null) {
                     Object var6 = var5.get(var0);
                     if (var6 != null) {
                        int var7 = computeBudget(var0, var1, var2);
                        if (var7 > 0) {
                           Object var8 = var2.server(var0);
                           if (var8 != null) {
                              int var9 = Math.max(1, var2.viewDistance(var8) + 1);
                              List var10 = var2.players(var0);
                              if (var10 != null && !var10.isEmpty()) {
                                 int var11 = 0;

                                 for (Object var13 : var10) {
                                    Object var14 = var2.playerChunk(var13);
                                    if (var14 != null) {
                                       int var15 = var2.chunkX(var14);
                                       int var16 = var2.chunkZ(var14);
                                       if (var15 != Integer.MIN_VALUE && var16 != Integer.MIN_VALUE) {
                                          for (int var17 = -var9; var17 <= var9; var17++) {
                                             for (int var18 = -var9; var18 <= var9; var18++) {
                                                int var19 = var15 + var17;
                                                int var20 = var16 + var18;
                                                long var21 = packChunk(var19, var20);
                                                if (var5.pendingContains(var6, var21)
                                                   && !var5.queuedContains(var6, var21)
                                                   && var2.hasChunk(var0, var19, var20)
                                                   && readyForReplay(var0, var21, var3, var2)) {
                                                   Object var23 = var2.newChunkPos(var19, var20);
                                                   if (var23 != null && !var2.horizonDrop(var0, var23)) {
                                                      Object var24 = var2.newBlockPos((var19 << 4) + 8, 64, (var20 << 4) + 8);
                                                      if (var24 != null) {
                                                         var5.remember(var6, var21, true);
                                                         if (!var2.enqueue(var1, var24)) {
                                                            var5.release(var6, var21);
                                                         } else {
                                                            noteAdmission(var1);
                                                            if (++var11 >= var7) {
                                                               return;
                                                            }
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean beginCandidate(Object var0, Object var1) {
      if (var0 != null && var1 != null) {
         ReplayController.RuntimeAccess var2 = runtime(var0);
         ReplayController.DeferredAccess var3 = deferred(var0);
         if (var2 != null && var3 != null) {
            Object var4 = var3.get(var0);
            if (var4 == null) {
               return false;
            }

            int var5 = var2.blockX(var1);
            int var6 = var2.blockZ(var1);
            if (var5 != Integer.MIN_VALUE && var6 != Integer.MIN_VALUE) {
               int var7 = var5 >> 4;
               int var8 = var6 >> 4;
               long var9 = packChunk(var7, var8);
               var3.release(var4, var9);
               long var11 = var2.gameTime(var0);
               ReplayController.LevelState var13 = levelState(var0);
               ReplayController.DeferredState var14;
               synchronized (var13) {
                  var14 = var13.deferred.get(var9);
               }

               if (var14 != null) {
                  boolean var15 = !var14.hasBounds || var2.hasChunksAt(var0, var14.minBlockX, var14.minBlockZ, var14.maxBlockX, var14.maxBlockZ);
                  if (!var15) {
                     var3.remember(var4, var9, false);
                     synchronized (var13) {
                        var14.touchedTick = var11;
                        return false;
                     }
                  }

                  if (!var14.hasBounds && var11 >= 0L && var11 < var14.nextRetryTick) {
                     var3.remember(var4, var9, false);
                     return false;
                  }
               }

               Object var22 = var2.newChunkPos(var7, var8);
               if (var2.hasChunk(var0, var7, var8) && var22 != null && !var2.horizonDrop(var0, var22)) {
                  var3.consume(var4, var9);
                  synchronized (var13) {
                     var13.deferred.remove(var9);
                     return true;
                  }
               } else {
                  var3.remember(var4, var9, false);
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean coverageCheck(Object var0, Object var1, Object var2) {
      ReplayController.RuntimeAccess var3 = runtime(var0);
      if (var3 != null && var0 != null && var1 != null && var2 != null) {
         boolean var4 = var3.hasChunksAt(var0, var1, var2);
         if (!var4) {
            coverageMiss(var0, var1, var2, var3);
         }

         return var4;
      } else {
         return false;
      }
   }

   public static void deferred(Object var0, Object var1, boolean var2) {
      if (var0 != null && var1 != null) {
         ReplayController.RuntimeAccess var3 = runtime(var0);
         ReplayController.DeferredAccess var4 = deferred(var0);
         if (var3 != null && var4 != null) {
            Object var5 = var4.get(var0);
            if (var5 != null) {
               int var6 = var3.blockX(var1);
               int var7 = var3.blockZ(var1);
               if (var6 != Integer.MIN_VALUE && var7 != Integer.MIN_VALUE) {
                  long var8 = packChunk(var6 >> 4, var7 >> 4);
                  var4.remember(var5, var8, var2);
                  if (var2) {
                     long var10 = var3.gameTime(var0);
                     ReplayController.LevelState var12 = levelState(var0);
                     synchronized (var12) {
                        ReplayController.DeferredState var14 = var12.deferred.computeIfAbsent(var8, var0x -> new ReplayController.DeferredState());
                        int var15 = Math.min(var14.deferrals, DEFER_BACKOFF_TICKS.length - 1);
                        var14.deferrals++;
                        if (var10 >= 0L) {
                           var14.nextRetryTick = Math.max(var14.nextRetryTick, var10 + DEFER_BACKOFF_TICKS[var15]);
                           var14.touchedTick = var10;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void coverageMiss(Object var0, Object var1, Object var2, ReplayController.RuntimeAccess var3) {
      int var4 = var3.blockX(var1);
      int var5 = var3.blockZ(var1);
      int var6 = var3.blockX(var2);
      int var7 = var3.blockZ(var2);
      if (var4 != Integer.MIN_VALUE && var5 != Integer.MIN_VALUE && var6 != Integer.MIN_VALUE && var7 != Integer.MIN_VALUE) {
         int var8 = Math.min(var4, var6);
         int var9 = Math.max(var4, var6);
         int var10 = Math.min(var5, var7);
         int var11 = Math.max(var5, var7);
         int var12 = (int)(((long)var8 + var9) / 2L);
         int var13 = (int)(((long)var10 + var11) / 2L);
         long var14 = packChunk(var12 >> 4, var13 >> 4);
         long var16 = var3.gameTime(var0);
         ReplayController.LevelState var18 = levelState(var0);
         synchronized (var18) {
            ReplayController.DeferredState var20 = var18.deferred.computeIfAbsent(var14, var0x -> new ReplayController.DeferredState());
            var20.hasBounds = true;
            var20.minBlockX = var8;
            var20.maxBlockX = var9;
            var20.minBlockZ = var10;
            var20.maxBlockZ = var11;
            var20.touchedTick = var16;
         }
      }
   }

   private static boolean readyForReplay(Object var0, long var1, long var3, ReplayController.RuntimeAccess var5) {
      ReplayController.LevelState var6 = levelState(var0);
      ReplayController.DeferredState var7;
      synchronized (var6) {
         var7 = var6.deferred.get(var1);
      }

      if (var7 == null) {
         return true;
      } else {
         return var7.hasBounds
            ? var5.hasChunksAt(var0, var7.minBlockX, var7.minBlockZ, var7.maxBlockX, var7.maxBlockZ)
            : var3 < 0L || var3 >= var7.nextRetryTick;
      }
   }

   private static int computeBudget(Object var0, Object var1, ReplayController.RuntimeAccess var2) {
      ReplayController.QueueSnapshot var3 = var2.queueSnapshot(var1);
      boolean var4 = var3 != null;
      if (!var4) {
         var3 = new ReplayController.QueueSnapshot(0, 0);
      }

      ReplayController.QueueState var5 = queueState(var1);
      ReplayController.QueueObservation var6;
      synchronized (var5) {
         var6 = var5.observe(var3.main, var3.lone);
      }

      ReplayController.SignalSnapshot var7 = sampleSignals(var0, var2, var5);
      if (var3.main < 72 && var3.lone < 64 && (var6.mainStallSamples < 2 || var3.main < 8) && !var7.hardStop) {
         double var8 = 1.0;
         if (var7.mspt >= 0.0) {
            if (var7.mspt >= 50.0) {
               return 0;
            }

            if (var7.mspt > 25.0) {
               var8 *= clamp((50.0 - var7.mspt) / 25.0, 0.0, 1.0);
            }
         }

         if (var7.csDispatchRatio >= 0.0) {
            if (var7.csDispatchRatio <= 0.12) {
               return 0;
            }

            var8 *= Math.sqrt(clamp(var7.csDispatchRatio, 0.0, 1.0));
         }

         if (var7.heapPercent >= 0.0) {
            if (var7.heapPercent >= 93.0) {
               return 0;
            }

            if (var7.heapPercent > 78.0) {
               var8 *= clamp((93.0 - var7.heapPercent) / 15.0, 0.0, 1.0);
            }
         }

         if (var7.gcFraction >= 0.0) {
            if (var7.gcFraction >= 0.15) {
               return 0;
            }

            if (var7.gcFraction > 0.02) {
               var8 *= clamp((0.15 - var7.gcFraction) / 0.13, 0.0, 1.0);
            }
         }

         if (var7.sinkQueueGrowth > 0 && var7.sinkQueue >= 16) {
            var8 *= var7.sinkQueueGrowth >= 16 ? 0.35 : 0.65;
         }

         if (var6.externalMainGrowth > 0) {
            var8 *= var6.externalMainGrowth >= 8 ? 0.35 : 0.65;
         }

         if (var6.loneGrowth > 0 && var3.lone > 0) {
            var8 *= var6.loneGrowth >= 8 ? 0.5 : 0.75;
         }

         var8 = clamp(var8, 0.0, 1.0);
         if (var8 < 0.06) {
            return 0;
         }

         int var10 = 12 + (int)Math.round(60.0 * var8);
         int var11 = Math.max(0, var10 - var3.main);
         if (var11 == 0) {
            return 0;
         }

         int var12 = (int)Math.floor(64.0 * Math.pow(var8, 1.35));
         if (var12 <= 0) {
            return 0;
         }

         int var13 = Math.min(64, Math.min(var11, var12));
         if (!var4) {
            var13 = Math.min(var13, 8);
         }

         if (var3.main > 0 && var6.mainDrainEma >= 0.0) {
            int var14 = (int)Math.floor(var6.mainDrainEma * 0.85);
            var13 = Math.min(var13, Math.max(0, var14));
         }

         if (var3.lone > 0) {
            double var18 = 1.0 - 0.8 * clamp(var3.lone / 64.0, 0.0, 1.0);
            var13 = (int)Math.floor(var13 * var18);
         }

         return Math.max(0, var13);
      } else {
         return 0;
      }
   }

   private static ReplayController.SignalSnapshot sampleSignals(Object var0, ReplayController.RuntimeAccess var1, ReplayController.QueueState var2) {
      ReplayController.SignalSnapshot var3 = new ReplayController.SignalSnapshot();
      Object var4 = var1.server(var0);
      double var5 = var4 == null ? -1.0 : var1.averageMspt(var4);
      synchronized (var2) {
         if (var5 >= 0.0 && Double.isFinite(var5)) {
            var2.msptEma = var2.msptEma < 0.0 ? var5 : var2.msptEma * 0.75 + var5 * 0.25;
         }

         var3.mspt = var2.msptEma;
      }

      ReplayController.ChunksmithSignals var7 = chunksmith().sample();
      if (var7 != null) {
         var3.hardStop = var7.probing || var7.heapHolding || var7.residencyDraining || var7.generationHeld || var7.atCeiling;
         if (var7.limit > 0 && var7.ceiling > 0) {
            var3.csDispatchRatio = clamp((double)var7.limit / var7.ceiling, 0.0, 1.0);
         }

         if (var7.heapLivePercent >= 0.0) {
            var3.heapPercent = var7.heapLivePercent;
         }

         synchronized (var2) {
            if (var7.sinkQueue >= 0) {
               var3.sinkQueue = var7.sinkQueue;
               var3.sinkQueueGrowth = var2.lastSinkQueue < 0 ? 0 : var7.sinkQueue - var2.lastSinkQueue;
               var2.lastSinkQueue = var7.sinkQueue;
            }
         }
      }

      if (var3.heapPercent < 0.0) {
         var3.heapPercent = fallbackHeapPercent();
      }

      var3.gcFraction = ReplayController.GcSampler.sample();
      return var3;
   }

   private static double fallbackHeapPercent() {
      try {
         MemoryMXBean var0 = ManagementFactory.getMemoryMXBean();
         MemoryUsage var1 = var0.getHeapMemoryUsage();
         long var2 = var1.getMax();
         return var2 > 0L ? 100.0 * var1.getUsed() / var2 : -1.0;
      } catch (RuntimeException | LinkageError var4) {
         return -1.0;
      }
   }

   private static void noteAdmission(Object var0) {
      ReplayController.QueueState var1 = queueState(var0);
      synchronized (var1) {
         var1.admittedSinceObservation++;
      }
   }

   private static ReplayController.LevelState levelState(Object var0) {
      synchronized (LEVEL_STATES) {
         return LEVEL_STATES.computeIfAbsent(var0, var0x -> new ReplayController.LevelState());
      }
   }

   private static ReplayController.QueueState queueState(Object var0) {
      synchronized (QUEUE_STATES) {
         return QUEUE_STATES.computeIfAbsent(var0, var0x -> new ReplayController.QueueState());
      }
   }

   private static ReplayController.DeferredAccess deferred(Object var0) {
      ReplayController.DeferredAccess var1 = deferredAccess;
      if (var1 != null) {
         return var1.usable ? var1 : null;
      }

      synchronized (ReplayController.class) {
         var1 = deferredAccess;
         if (var1 == null) {
            var1 = new ReplayController.DeferredAccess(var0);
            deferredAccess = var1;
         }
      }

      return var1.usable ? var1 : null;
   }

   private static ReplayController.RuntimeAccess runtime(Object var0) {
      ReplayController.RuntimeAccess var1 = runtimeAccess;
      if (var1 != null) {
         return var1.usable ? var1 : null;
      }

      synchronized (ReplayController.class) {
         var1 = runtimeAccess;
         if (var1 == null) {
            var1 = new ReplayController.RuntimeAccess(var0);
            runtimeAccess = var1;
         }
      }

      return var1.usable ? var1 : null;
   }

   private static ReplayController.ChunksmithAccess chunksmith() {
      ReplayController.ChunksmithAccess var0 = chunksmithAccess;
      if (var0 != null) {
         return var0;
      }

      synchronized (ReplayController.class) {
         var0 = chunksmithAccess;
         if (var0 == null) {
            var0 = new ReplayController.ChunksmithAccess();
            chunksmithAccess = var0;
         }

         return var0;
      }
   }

   private static boolean generationEnabled() {
      ReplayController.ConfigAccess var0 = configAccess;
      if (var0 == null) {
         synchronized (ReplayController.class) {
            var0 = configAccess;
            if (var0 == null) {
               var0 = new ReplayController.ConfigAccess();
               configAccess = var0;
            }
         }
      }

      return var0.enabled();
   }

   private static long packChunk(int var0, int var1) {
      return var0 & 4294967295L | (var1 & 4294967295L) << 32;
   }

   private static double clamp(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }

   private static Method findMethod(Class<?> var0, String var1, int var2) {
      for (Class var3 = var0; var3 != null; var3 = var3.getSuperclass()) {
         for (Method var7 : var3.getDeclaredMethods()) {
            if (var7.getName().equals(var1) && var7.getParameterCount() == var2) {
               try {
                  var7.setAccessible(true);
               } catch (RuntimeException var9) {
               }

               return var7;
            }
         }
      }

      for (Method var13 : var0.getMethods()) {
         if (var13.getName().equals(var1) && var13.getParameterCount() == var2) {
            return var13;
         }
      }

      return null;
   }

   private static Method findAnyMethod(Class<?> var0, int var1, String... var2) {
      for (String var6 : var2) {
         Method var7 = findMethod(var0, var6, var1);
         if (var7 != null) {
            return var7;
         }
      }

      return null;
   }

   private static Field findField(Class<?> var0, String var1) {
      for (Class var2 = var0; var2 != null; var2 = var2.getSuperclass()) {
         try {
            Field var3 = var2.getDeclaredField(var1);

            try {
               var3.setAccessible(true);
            } catch (RuntimeException var5) {
            }

            return var3;
         } catch (NoSuchFieldException var6) {
         }
      }

      return null;
   }

   private static Object invoke(Method var0, Object var1, Object... var2) {
      if (var0 == null) {
         return null;
      }

      try {
         return var0.invoke(var1, var2);
      } catch (ReflectiveOperationException | RuntimeException var4) {
         return null;
      }
   }

   private static final class ChunksmithAccess {
      final Method tickProbing;
      final Method tickAtCeiling;
      final Method heapHolding;
      final Method heapLive;
      final Method residencyDraining;
      final Method generationHeld;
      final Field inFlight;
      final Field limit;
      final Field ceiling;
      final Field sinkQueue;

      ChunksmithAccess() {
         Method var1 = null;
         Method var2 = null;
         Method var3 = null;
         Method var4 = null;
         Method var5 = null;
         Method var6 = null;
         Field var7 = null;
         Field var8 = null;
         Field var9 = null;
         Field var10 = null;

         try {
            ClassLoader var11 = ReplayController.class.getClassLoader();
            Class var12 = Class.forName("com.kishku7.chunksmith.util.TickBudget", false, var11);
            var1 = ReplayController.findAnyMethod(var12, 0, "isProbing");
            var2 = ReplayController.findAnyMethod(var12, 0, "atCeiling");
         } catch (ClassNotFoundException | LinkageError var16) {
         }

         try {
            Class var17 = Class.forName("com.kishku7.chunksmith.util.HeapPressure", false, ReplayController.class.getClassLoader());
            var3 = ReplayController.findAnyMethod(var17, 0, "isHolding");
            var4 = ReplayController.findAnyMethod(var17, 0, "liveEstimatePercent", "usedPercent");
         } catch (ClassNotFoundException | LinkageError var15) {
         }

         try {
            Class var18 = Class.forName("com.kishku7.chunksmith.util.ChunkResidency", false, ReplayController.class.getClassLoader());
            var5 = ReplayController.findAnyMethod(var18, 0, "isDraining");
            var6 = ReplayController.findAnyMethod(var18, 0, "isGenerationHeld");
         } catch (ClassNotFoundException | LinkageError var14) {
         }

         try {
            Class var19 = Class.forName("com.kishku7.chunksmith.util.DispatchStats", false, ReplayController.class.getClassLoader());
            var7 = ReplayController.findField(var19, "inFlight");
            var8 = ReplayController.findField(var19, "limit");
            var9 = ReplayController.findField(var19, "ceiling");
            var10 = ReplayController.findField(var19, "sinkQueue");
         } catch (ClassNotFoundException | LinkageError var13) {
         }

         this.tickProbing = var1;
         this.tickAtCeiling = var2;
         this.heapHolding = var3;
         this.heapLive = var4;
         this.residencyDraining = var5;
         this.generationHeld = var6;
         this.inFlight = var7;
         this.limit = var8;
         this.ceiling = var9;
         this.sinkQueue = var10;
      }

      ReplayController.ChunksmithSignals sample() {
         ReplayController.ChunksmithSignals var1 = new ReplayController.ChunksmithSignals();
         var1.probing = this.bool(ReplayController.invoke(this.tickProbing, null));
         var1.atCeiling = this.bool(ReplayController.invoke(this.tickAtCeiling, null));
         var1.heapHolding = this.bool(ReplayController.invoke(this.heapHolding, null));
         var1.residencyDraining = this.bool(ReplayController.invoke(this.residencyDraining, null));
         var1.generationHeld = this.bool(ReplayController.invoke(this.generationHeld, null));
         Object var2 = ReplayController.invoke(this.heapLive, null);
         if (var2 instanceof Number) {
            var1.heapLivePercent = ((Number)var2).doubleValue();
         }

         var1.inFlight = this.intField(this.inFlight);
         var1.limit = this.intField(this.limit);
         var1.ceiling = this.intField(this.ceiling);
         var1.sinkQueue = this.intField(this.sinkQueue);
         return var1;
      }

      private boolean bool(Object var1) {
         return var1 instanceof Boolean && (Boolean)var1;
      }

      private int intField(Field var1) {
         if (var1 != null && Modifier.isStatic(var1.getModifiers())) {
            try {
               return var1.getInt(null);
            } catch (IllegalAccessException | RuntimeException var3) {
               return -1;
            }
         } else {
            return -1;
         }
      }
   }

   private static final class ChunksmithSignals {
      boolean probing;
      boolean heapHolding;
      boolean residencyDraining;
      boolean generationHeld;
      boolean atCeiling;
      double heapLivePercent = -1.0;
      int inFlight = -1;
      int limit = -1;
      int ceiling = -1;
      int sinkQueue = -1;
   }

   private static final class ConfigAccess {
      final Object serverConfig;
      final Field generateVillages;
      final Field generateLoneBuildings;
      volatile Method valueGet;
      final boolean usable;

      ConfigAccess() {
         Object var1 = null;
         Field var2 = null;
         Field var3 = null;
         boolean var4 = false;

         try {
            Class var5 = Class.forName("org.millenaire.config.MillenaireServerConfig", false, ReplayController.class.getClassLoader());
            Field var6 = ReplayController.findField(var5, "SERVER");
            var1 = var6 == null ? null : var6.get(null);
            if (var1 != null) {
               var2 = ReplayController.findField(var1.getClass(), "generateVillages");
               var3 = ReplayController.findField(var1.getClass(), "generateLoneBuildings");
               var4 = var2 != null && var3 != null;
            }
         } catch (ReflectiveOperationException | RuntimeException | LinkageError var7) {
         }

         this.serverConfig = var1;
         this.generateVillages = var2;
         this.generateLoneBuildings = var3;
         this.usable = var4;
      }

      boolean enabled() {
         return !this.usable ? true : this.read(this.generateVillages) || this.read(this.generateLoneBuildings);
      }

      private boolean read(Field var1) {
         try {
            Object var2 = var1.get(this.serverConfig);
            if (var2 == null) {
               return false;
            }

            Method var3 = this.valueGet;
            if (var3 == null || !var3.getDeclaringClass().isAssignableFrom(var2.getClass())) {
               var3 = ReplayController.findAnyMethod(var2.getClass(), 0, "get");
               this.valueGet = var3;
            }

            Object var4 = ReplayController.invoke(var3, var2);
            return var4 instanceof Boolean && (Boolean)var4;
         } catch (IllegalAccessException | RuntimeException var5) {
            return false;
         }
      }
   }

   private static final class DeferredAccess {
      final boolean usable;
      final Method get;
      final Method remember;
      final Method release;
      final Method consume;
      final Field pending;
      final Field queued;
      Method contains;

      DeferredAccess(Object var1) {
         Method var2 = null;
         Method var3 = null;
         Method var4 = null;
         Method var5 = null;
         Field var6 = null;
         Field var7 = null;
         boolean var8 = false;

         try {
            Class var9 = Class.forName("maki.millenairelod.DeferredData", false, ReplayController.class.getClassLoader());
            var2 = ReplayController.findMethod(var9, "get", 1);
            var3 = ReplayController.findMethod(var9, "remember", 2);
            var4 = ReplayController.findMethod(var9, "release", 1);
            var5 = ReplayController.findMethod(var9, "consume", 1);
            var6 = ReplayController.findField(var9, "pending");
            var7 = ReplayController.findField(var9, "queued");
            var8 = var2 != null && var3 != null && var4 != null && var5 != null && var6 != null && var7 != null;
         } catch (ClassNotFoundException | LinkageError var10) {
         }

         this.get = var2;
         this.remember = var3;
         this.release = var4;
         this.consume = var5;
         this.pending = var6;
         this.queued = var7;
         this.usable = var8;
      }

      Object get(Object var1) {
         return ReplayController.invoke(this.get, null, var1);
      }

      void remember(Object var1, long var2, boolean var4) {
         ReplayController.invoke(this.remember, var1, var2, var4);
      }

      void release(Object var1, long var2) {
         ReplayController.invoke(this.release, var1, var2);
      }

      void consume(Object var1, long var2) {
         ReplayController.invoke(this.consume, var1, var2);
      }

      boolean pendingContains(Object var1, long var2) {
         return this.contains(this.set(this.pending, var1), var2);
      }

      boolean queuedContains(Object var1, long var2) {
         return this.contains(this.set(this.queued, var1), var2);
      }

      private Object set(Field var1, Object var2) {
         try {
            return var1.get(var2);
         } catch (IllegalAccessException | RuntimeException var4) {
            return null;
         }
      }

      private boolean contains(Object var1, long var2) {
         if (var1 == null) {
            return false;
         }

         Method var4 = this.contains;
         if (var4 == null || var4.getDeclaringClass() != var1.getClass()) {
            var4 = ReplayController.findMethod(var1.getClass(), "contains", 1);
            this.contains = var4;
         }

         Object var5 = ReplayController.invoke(var4, var1, var2);
         return var5 instanceof Boolean && (Boolean)var5;
      }
   }

   private static final class DeferredState {
      boolean hasBounds;
      int minBlockX;
      int maxBlockX;
      int minBlockZ;
      int maxBlockZ;
      int deferrals;
      long nextRetryTick;
      long touchedTick;
   }

   private static final class GcSampler {
      private static long lastWallNanos;
      private static long lastCollectionMillis = -1L;
      private static double ema = -1.0;

      static synchronized double sample() {
         long var0 = System.nanoTime();
         long var2 = 0L;
         boolean var4 = false;

         try {
            for (GarbageCollectorMXBean var6 : ManagementFactory.getGarbageCollectorMXBeans()) {
               long var7 = var6.getCollectionTime();
               if (var7 >= 0L) {
                  var2 += var7;
                  var4 = true;
               }
            }
         } catch (RuntimeException | LinkageError var11) {
            return ema;
         }

         if (!var4) {
            return ema;
         }

         if (lastWallNanos != 0L && lastCollectionMillis >= 0L) {
            double var12 = (var0 - lastWallNanos) / 1000000.0;
            if (var12 < 500.0) {
               return ema;
            }

            long var13 = Math.max(0L, var2 - lastCollectionMillis);
            double var9 = ReplayController.clamp(var13 / var12, 0.0, 1.0);
            ema = ema < 0.0 ? var9 : ema * 0.7 + var9 * 0.3;
            lastWallNanos = var0;
            lastCollectionMillis = var2;
            return ema;
         } else {
            lastWallNanos = var0;
            lastCollectionMillis = var2;
            return ema;
         }
      }
   }

   private static final class LevelState {
      final Map<Long, ReplayController.DeferredState> deferred = new HashMap<>();
   }

   private static final class QueueObservation {
      int externalMainGrowth;
      int loneGrowth;
      int mainStallSamples;
      int loneStallSamples;
      double mainDrainEma = -1.0;
   }

   private static final class QueueSnapshot {
      final int main;
      final int lone;

      QueueSnapshot(int var1, int var2) {
         this.main = Math.max(0, var1);
         this.lone = Math.max(0, var2);
      }
   }

   private static final class QueueState {
      int lastMain = -1;
      int lastLone = -1;
      int admittedSinceObservation;
      int mainStallSamples;
      int loneStallSamples;
      double mainDrainEma = -1.0;
      double msptEma = -1.0;
      int lastSinkQueue = -1;

      ReplayController.QueueObservation observe(int var1, int var2) {
         ReplayController.QueueObservation var3 = new ReplayController.QueueObservation();
         if (this.lastMain >= 0) {
            int var4 = Math.max(0, this.lastMain + this.admittedSinceObservation);
            int var5 = Math.max(0, var4 - var1);
            var3.externalMainGrowth = Math.max(0, var1 - var4);
            this.mainDrainEma = this.mainDrainEma < 0.0 ? var5 : this.mainDrainEma * 0.7 + var5 * 0.3;
            if (var4 > 0 && var1 > 0 && var5 == 0) {
               this.mainStallSamples++;
            } else {
               this.mainStallSamples = 0;
            }
         }

         if (this.lastLone >= 0) {
            var3.loneGrowth = Math.max(0, var2 - this.lastLone);
            if (this.lastLone > 0 && var2 >= this.lastLone) {
               this.loneStallSamples++;
            } else {
               this.loneStallSamples = 0;
            }
         }

         this.lastMain = var1;
         this.lastLone = var2;
         this.admittedSinceObservation = 0;
         var3.mainStallSamples = this.mainStallSamples;
         var3.loneStallSamples = this.loneStallSamples;
         var3.mainDrainEma = this.mainDrainEma;
         return var3;
      }
   }

   private static final class RuntimeAccess {
      final boolean usable;
      final Method getGameTime;
      final Method getServer;
      final Method hasChunk;
      final Method hasChunksAt;
      final Method players;
      final Constructor<?> blockPosCtor;
      final Constructor<?> chunkPosCtor;
      final Method blockGetX;
      final Method blockGetZ;
      final Field chunkX;
      final Field chunkZ;
      final Method horizonDrop;
      volatile Method serverAverage;
      volatile Method serverPlayerList;
      volatile Method viewDistance;
      volatile Method playerChunkPosition;
      volatile Method queueGetStats;
      volatile Method queueEnqueue;

      RuntimeAccess(Object var1) {
         Method var2 = null;
         Method var3 = null;
         Method var4 = null;
         Method var5 = null;
         Method var6 = null;
         Constructor var7 = null;
         Constructor var8 = null;
         Method var9 = null;
         Method var10 = null;
         Method var11 = null;
         Field var12 = null;
         Field var13 = null;
         boolean var14 = false;

         try {
            Class var15 = var1.getClass();
            var2 = ReplayController.findAnyMethod(var15, 0, "getGameTime");
            var3 = ReplayController.findAnyMethod(var15, 0, "getServer");
            var4 = ReplayController.findAnyMethod(var15, 2, "hasChunk");
            Class var16 = Class.forName("net.minecraft.core.BlockPos", false, ReplayController.class.getClassLoader());
            Class var17 = Class.forName("net.minecraft.world.level.ChunkPos", false, ReplayController.class.getClassLoader());
            var5 = ReplayController.findAnyMethod(var15, 2, "hasChunksAt");
            var6 = ReplayController.findAnyMethod(var15, 0, "players");
            var7 = var16.getConstructor(int.class, int.class, int.class);
            var8 = var17.getConstructor(int.class, int.class);
            var9 = ReplayController.findAnyMethod(var16, 0, "getX");
            var10 = ReplayController.findAnyMethod(var16, 0, "getZ");
            var12 = ReplayController.findField(var17, "x");
            var13 = ReplayController.findField(var17, "z");
            Class var18 = Class.forName("com.kishku7.chunksmith.lod.HorizonGuard", false, ReplayController.class.getClassLoader());
            var11 = ReplayController.findMethod(var18, "drop", 2);
            var14 = var2 != null
               && var3 != null
               && var4 != null
               && var5 != null
               && var6 != null
               && var7 != null
               && var8 != null
               && var9 != null
               && var10 != null
               && var12 != null
               && var13 != null
               && var11 != null;
         } catch (ReflectiveOperationException | LinkageError var19) {
         }

         this.getGameTime = var2;
         this.getServer = var3;
         this.hasChunk = var4;
         this.hasChunksAt = var5;
         this.players = var6;
         this.blockPosCtor = var7;
         this.chunkPosCtor = var8;
         this.blockGetX = var9;
         this.blockGetZ = var10;
         this.chunkX = var12;
         this.chunkZ = var13;
         this.horizonDrop = var11;
         this.usable = var14;
      }

      long gameTime(Object var1) {
         Object var2 = ReplayController.invoke(this.getGameTime, var1);
         return var2 instanceof Number ? ((Number)var2).longValue() : -1L;
      }

      Object server(Object var1) {
         return ReplayController.invoke(this.getServer, var1);
      }

      boolean hasChunk(Object var1, int var2, int var3) {
         Object var4 = ReplayController.invoke(this.hasChunk, var1, var2, var3);
         return var4 instanceof Boolean && (Boolean)var4;
      }

      boolean hasChunksAt(Object var1, Object var2, Object var3) {
         Object var4 = ReplayController.invoke(this.hasChunksAt, var1, var2, var3);
         return var4 instanceof Boolean && (Boolean)var4;
      }

      boolean hasChunksAt(Object var1, int var2, int var3, int var4, int var5) {
         Object var6 = this.newBlockPos(var2, 0, var3);
         Object var7 = this.newBlockPos(var4, 0, var5);
         return var6 != null && var7 != null && this.hasChunksAt(var1, var6, var7);
      }

      List<?> players(Object var1) {
         Object var2 = ReplayController.invoke(this.players, var1);
         return var2 instanceof List ? (List)var2 : List.of();
      }

      Object newBlockPos(int var1, int var2, int var3) {
         try {
            return this.blockPosCtor.newInstance(var1, var2, var3);
         } catch (ReflectiveOperationException | RuntimeException var5) {
            return null;
         }
      }

      Object newChunkPos(int var1, int var2) {
         try {
            return this.chunkPosCtor.newInstance(var1, var2);
         } catch (ReflectiveOperationException | RuntimeException var4) {
            return null;
         }
      }

      int blockX(Object var1) {
         Object var2 = ReplayController.invoke(this.blockGetX, var1);
         return var2 instanceof Number ? ((Number)var2).intValue() : Integer.MIN_VALUE;
      }

      int blockZ(Object var1) {
         Object var2 = ReplayController.invoke(this.blockGetZ, var1);
         return var2 instanceof Number ? ((Number)var2).intValue() : Integer.MIN_VALUE;
      }

      int chunkX(Object var1) {
         try {
            return this.chunkX.getInt(var1);
         } catch (IllegalAccessException | RuntimeException var3) {
            return Integer.MIN_VALUE;
         }
      }

      int chunkZ(Object var1) {
         try {
            return this.chunkZ.getInt(var1);
         } catch (IllegalAccessException | RuntimeException var3) {
            return Integer.MIN_VALUE;
         }
      }

      boolean horizonDrop(Object var1, Object var2) {
         Object var3 = ReplayController.invoke(this.horizonDrop, null, var1, var2);
         return var3 instanceof Boolean && (Boolean)var3;
      }

      double averageMspt(Object var1) {
         Method var2 = this.serverAverage;
         if (var2 == null) {
            var2 = ReplayController.findAnyMethod(var1.getClass(), 0, "getAverageTickTimeNanos", "getAverageNanosPerTick", "getAverageTickTime");
            this.serverAverage = var2;
         }

         Object var3 = ReplayController.invoke(var2, var1);
         if (!(var3 instanceof Number)) {
            return -1.0;
         }

         double var4 = ((Number)var3).doubleValue();
         if (var4 < 0.0) {
            return -1.0;
         }

         String var6 = var2 == null ? "" : var2.getName().toLowerCase();
         return var6.contains("nano") ? var4 / 1000000.0 : var4;
      }

      int viewDistance(Object var1) {
         Method var2 = this.serverPlayerList;
         if (var2 == null) {
            var2 = ReplayController.findAnyMethod(var1.getClass(), 0, "getPlayerList");
            this.serverPlayerList = var2;
         }

         Object var3 = ReplayController.invoke(var2, var1);
         if (var3 == null) {
            return 10;
         }

         Method var4 = this.viewDistance;
         if (var4 == null) {
            var4 = ReplayController.findAnyMethod(var3.getClass(), 0, "getViewDistance");
            this.viewDistance = var4;
         }

         Object var5 = ReplayController.invoke(var4, var3);
         return var5 instanceof Number ? ((Number)var5).intValue() : 10;
      }

      Object playerChunk(Object var1) {
         Method var2 = this.playerChunkPosition;
         if (var2 == null) {
            var2 = ReplayController.findAnyMethod(var1.getClass(), 0, "chunkPosition");
            this.playerChunkPosition = var2;
         }

         return ReplayController.invoke(var2, var1);
      }

      ReplayController.QueueSnapshot queueSnapshot(Object var1) {
         Method var2 = this.queueGetStats;
         if (var2 == null || !var2.getDeclaringClass().isAssignableFrom(var1.getClass())) {
            var2 = ReplayController.findAnyMethod(var1.getClass(), 0, "getStats");
            this.queueGetStats = var2;
         }

         return ReplayController.invoke(var2, var1) instanceof Map var4
            ? new ReplayController.QueueSnapshot(this.number(var4.get("mainQueueSize")), this.number(var4.get("lbQueueSize")))
            : null;
      }

      boolean enqueue(Object var1, Object var2) {
         Method var3 = this.queueEnqueue;
         if (var3 == null || !var3.getDeclaringClass().isAssignableFrom(var1.getClass())) {
            var3 = ReplayController.findAnyMethod(var1.getClass(), 1, "enqueue");
            this.queueEnqueue = var3;
         }

         if (var3 == null) {
            return false;
         }

         try {
            var3.invoke(var1, var2);
            return true;
         } catch (ReflectiveOperationException | RuntimeException var5) {
            return false;
         }
      }

      private int number(Object var1) {
         return var1 instanceof Number ? ((Number)var1).intValue() : 0;
      }
   }

   private static final class SignalSnapshot {
      double mspt = -1.0;
      double csDispatchRatio = -1.0;
      double heapPercent = -1.0;
      double gcFraction = -1.0;
      boolean hardStop;
      int sinkQueue = -1;
      int sinkQueueGrowth;
   }
}
