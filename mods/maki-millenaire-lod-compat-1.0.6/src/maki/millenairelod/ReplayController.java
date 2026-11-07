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

   public static void replay(Object level, Object queue) {
      if (level != null && queue != null) {
         ReplayController.RuntimeAccess rt = runtime(level);
         if (rt != null) {
            long tick = rt.gameTime(level);
            if (tick >= 0L && tick % 20L == 0L) {
               if (generationEnabled()) {
                  ReplayController.DeferredAccess da = deferred(level);
                  if (da != null) {
                     Object data = da.get(level);
                     if (data != null) {
                        int budget = computeBudget(level, queue, rt);
                        if (budget > 0) {
                           Object server = rt.server(level);
                           if (server != null) {
                              int radius = Math.max(1, rt.viewDistance(server) + 1);
                              List<?> players = rt.players(level);
                              if (players != null && !players.isEmpty()) {
                                 int admitted = 0;

                                 for (Object player : players) {
                                    Object at = rt.playerChunk(player);
                                    if (at != null) {
                                       int px = rt.chunkX(at);
                                       int pz = rt.chunkZ(at);
                                       if (px != Integer.MIN_VALUE && pz != Integer.MIN_VALUE) {
                                          for (int dx = -radius; dx <= radius; dx++) {
                                             for (int dz = -radius; dz <= radius; dz++) {
                                                int x = px + dx;
                                                int z = pz + dz;
                                                long key = packChunk(x, z);
                                                if (da.pendingContains(data, key)
                                                   && !da.queuedContains(data, key)
                                                   && rt.hasChunk(level, x, z)
                                                   && readyForReplay(level, key, tick, rt)) {
                                                   Object chunkPos = rt.newChunkPos(x, z);
                                                   if (chunkPos != null && !rt.horizonDrop(level, chunkPos)) {
                                                      Object blockPos = rt.newBlockPos((x << 4) + 8, 64, (z << 4) + 8);
                                                      if (blockPos != null) {
                                                         da.remember(data, key, true);
                                                         if (!rt.enqueue(queue, blockPos)) {
                                                            da.release(data, key);
                                                         } else {
                                                            noteAdmission(queue);
                                                            if (++admitted >= budget) {
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

   public static boolean beginCandidate(Object level, Object pos) {
      if (level != null && pos != null) {
         ReplayController.RuntimeAccess rt = runtime(level);
         ReplayController.DeferredAccess da = deferred(level);
         if (rt != null && da != null) {
            Object data = da.get(level);
            if (data == null) {
               return false;
            }

            int bx = rt.blockX(pos);
            int bz = rt.blockZ(pos);
            if (bx != Integer.MIN_VALUE && bz != Integer.MIN_VALUE) {
               int cx = bx >> 4;
               int cz = bz >> 4;
               long key = packChunk(cx, cz);
               da.release(data, key);
               long tick = rt.gameTime(level);
               ReplayController.LevelState ls = levelState(level);
               ReplayController.DeferredState wait;
               synchronized (ls) {
                  wait = ls.deferred.get(key);
               }

               if (wait != null) {
                  boolean coverageReady = !wait.hasBounds || rt.hasChunksAt(level, wait.minBlockX, wait.minBlockZ, wait.maxBlockX, wait.maxBlockZ);
                  if (!coverageReady) {
                     da.remember(data, key, false);
                     synchronized (ls) {
                        wait.touchedTick = tick;
                        return false;
                     }
                  }

                  if (!wait.hasBounds && tick >= 0L && tick < wait.nextRetryTick) {
                     da.remember(data, key, false);
                     return false;
                  }
               }

               Object chunkPos = rt.newChunkPos(cx, cz);
               if (rt.hasChunk(level, cx, cz) && chunkPos != null && !rt.horizonDrop(level, chunkPos)) {
                  da.consume(data, key);
                  synchronized (ls) {
                     ls.deferred.remove(key);
                     return true;
                  }
               } else {
                  da.remember(data, key, false);
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

   public static boolean coverageCheck(Object level, Object from, Object to) {
      ReplayController.RuntimeAccess rt = runtime(level);
      if (rt != null && level != null && from != null && to != null) {
         boolean ready = rt.hasChunksAt(level, from, to);
         if (!ready) {
            coverageMiss(level, from, to, rt);
         }

         return ready;
      } else {
         return false;
      }
   }

   public static void deferred(Object level, Object pos, boolean queued) {
      if (level != null && pos != null) {
         ReplayController.RuntimeAccess rt = runtime(level);
         ReplayController.DeferredAccess da = deferred(level);
         if (rt != null && da != null) {
            Object data = da.get(level);
            if (data != null) {
               int bx = rt.blockX(pos);
               int bz = rt.blockZ(pos);
               if (bx != Integer.MIN_VALUE && bz != Integer.MIN_VALUE) {
                  long key = packChunk(bx >> 4, bz >> 4);
                  da.remember(data, key, queued);
                  if (queued) {
                     long tick = rt.gameTime(level);
                     ReplayController.LevelState ls = levelState(level);
                     synchronized (ls) {
                        ReplayController.DeferredState state = ls.deferred.computeIfAbsent(key, ignored -> new ReplayController.DeferredState());
                        int index = Math.min(state.deferrals, DEFER_BACKOFF_TICKS.length - 1);
                        state.deferrals++;
                        if (tick >= 0L) {
                           state.nextRetryTick = Math.max(state.nextRetryTick, tick + DEFER_BACKOFF_TICKS[index]);
                           state.touchedTick = tick;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void coverageMiss(Object level, Object from, Object to, ReplayController.RuntimeAccess rt) {
      int fx = rt.blockX(from);
      int fz = rt.blockZ(from);
      int tx = rt.blockX(to);
      int tz = rt.blockZ(to);
      if (fx != Integer.MIN_VALUE && fz != Integer.MIN_VALUE && tx != Integer.MIN_VALUE && tz != Integer.MIN_VALUE) {
         int minX = Math.min(fx, tx);
         int maxX = Math.max(fx, tx);
         int minZ = Math.min(fz, tz);
         int maxZ = Math.max(fz, tz);
         int centerX = (int)(((long)minX + maxX) / 2L);
         int centerZ = (int)(((long)minZ + maxZ) / 2L);
         long key = packChunk(centerX >> 4, centerZ >> 4);
         long tick = rt.gameTime(level);
         ReplayController.LevelState ls = levelState(level);
         synchronized (ls) {
            ReplayController.DeferredState state = ls.deferred.computeIfAbsent(key, ignored -> new ReplayController.DeferredState());
            state.hasBounds = true;
            state.minBlockX = minX;
            state.maxBlockX = maxX;
            state.minBlockZ = minZ;
            state.maxBlockZ = maxZ;
            state.touchedTick = tick;
         }
      }
   }

   private static boolean readyForReplay(Object level, long key, long tick, ReplayController.RuntimeAccess rt) {
      ReplayController.LevelState ls = levelState(level);
      ReplayController.DeferredState wait;
      synchronized (ls) {
         wait = ls.deferred.get(key);
      }

      if (wait == null) {
         return true;
      } else {
         return wait.hasBounds
            ? rt.hasChunksAt(level, wait.minBlockX, wait.minBlockZ, wait.maxBlockX, wait.maxBlockZ)
            : tick < 0L || tick >= wait.nextRetryTick;
      }
   }

   private static int computeBudget(Object level, Object queue, ReplayController.RuntimeAccess rt) {
      ReplayController.QueueSnapshot q = rt.queueSnapshot(queue);
      boolean queueKnown = q != null;
      if (!queueKnown) {
         q = new ReplayController.QueueSnapshot(0, 0);
      }

      ReplayController.QueueState qs = queueState(queue);
      ReplayController.QueueObservation observation;
      synchronized (qs) {
         observation = qs.observe(q.main, q.lone);
      }

      ReplayController.SignalSnapshot s = sampleSignals(level, rt, qs);
      if (q.main < 72 && q.lone < 64 && (observation.mainStallSamples < 2 || q.main < 8) && !s.hardStop) {
         double headroom = 1.0;
         if (s.mspt >= 0.0) {
            if (s.mspt >= 50.0) {
               return 0;
            }

            if (s.mspt > 25.0) {
               headroom *= clamp((50.0 - s.mspt) / 25.0, 0.0, 1.0);
            }
         }

         if (s.csDispatchRatio >= 0.0) {
            if (s.csDispatchRatio <= 0.12) {
               return 0;
            }

            headroom *= Math.sqrt(clamp(s.csDispatchRatio, 0.0, 1.0));
         }

         if (s.heapPercent >= 0.0) {
            if (s.heapPercent >= 93.0) {
               return 0;
            }

            if (s.heapPercent > 78.0) {
               headroom *= clamp((93.0 - s.heapPercent) / 15.0, 0.0, 1.0);
            }
         }

         if (s.gcFraction >= 0.0) {
            if (s.gcFraction >= 0.15) {
               return 0;
            }

            if (s.gcFraction > 0.02) {
               headroom *= clamp((0.15 - s.gcFraction) / 0.13, 0.0, 1.0);
            }
         }

         if (s.sinkQueueGrowth > 0 && s.sinkQueue >= 16) {
            headroom *= s.sinkQueueGrowth >= 16 ? 0.35 : 0.65;
         }

         if (observation.externalMainGrowth > 0) {
            headroom *= observation.externalMainGrowth >= 8 ? 0.35 : 0.65;
         }

         if (observation.loneGrowth > 0 && q.lone > 0) {
            headroom *= observation.loneGrowth >= 8 ? 0.5 : 0.75;
         }

         headroom = clamp(headroom, 0.0, 1.0);
         if (headroom < 0.06) {
            return 0;
         }

         int target = 12 + (int)Math.round(60.0 * headroom);
         int room = Math.max(0, target - q.main);
         if (room == 0) {
            return 0;
         }

         int loadCap = (int)Math.floor(64.0 * Math.pow(headroom, 1.35));
         if (loadCap <= 0) {
            return 0;
         }

         int budget = Math.min(64, Math.min(room, loadCap));
         if (!queueKnown) {
            budget = Math.min(budget, 8);
         }

         if (q.main > 0 && observation.mainDrainEma >= 0.0) {
            int sustainable = (int)Math.floor(observation.mainDrainEma * 0.85);
            budget = Math.min(budget, Math.max(0, sustainable));
         }

         if (q.lone > 0) {
            double loneFactor = 1.0 - 0.8 * clamp(q.lone / 64.0, 0.0, 1.0);
            budget = (int)Math.floor(budget * loneFactor);
         }

         return Math.max(0, budget);
      } else {
         return 0;
      }
   }

   private static ReplayController.SignalSnapshot sampleSignals(Object level, ReplayController.RuntimeAccess rt, ReplayController.QueueState qs) {
      ReplayController.SignalSnapshot out = new ReplayController.SignalSnapshot();
      Object server = rt.server(level);
      double rawMspt = server == null ? -1.0 : rt.averageMspt(server);
      synchronized (qs) {
         if (rawMspt >= 0.0 && Double.isFinite(rawMspt)) {
            qs.msptEma = qs.msptEma < 0.0 ? rawMspt : qs.msptEma * 0.75 + rawMspt * 0.25;
         }

         out.mspt = qs.msptEma;
      }

      ReplayController.ChunksmithSignals cs = chunksmith().sample();
      if (cs != null) {
         out.hardStop = cs.probing || cs.heapHolding || cs.residencyDraining || cs.generationHeld || cs.atCeiling;
         if (cs.limit > 0 && cs.ceiling > 0) {
            out.csDispatchRatio = clamp((double)cs.limit / cs.ceiling, 0.0, 1.0);
         }

         if (cs.heapLivePercent >= 0.0) {
            out.heapPercent = cs.heapLivePercent;
         }

         synchronized (qs) {
            if (cs.sinkQueue >= 0) {
               out.sinkQueue = cs.sinkQueue;
               out.sinkQueueGrowth = qs.lastSinkQueue < 0 ? 0 : cs.sinkQueue - qs.lastSinkQueue;
               qs.lastSinkQueue = cs.sinkQueue;
            }
         }
      }

      if (out.heapPercent < 0.0) {
         out.heapPercent = fallbackHeapPercent();
      }

      out.gcFraction = ReplayController.GcSampler.sample();
      return out;
   }

   private static double fallbackHeapPercent() {
      try {
         MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
         MemoryUsage heap = bean.getHeapMemoryUsage();
         long max = heap.getMax();
         return max > 0L ? 100.0 * heap.getUsed() / max : -1.0;
      } catch (RuntimeException | LinkageError ignored) {
         return -1.0;
      }
   }

   private static void noteAdmission(Object queue) {
      ReplayController.QueueState state = queueState(queue);
      synchronized (state) {
         state.admittedSinceObservation++;
      }
   }

   private static ReplayController.LevelState levelState(Object level) {
      synchronized (LEVEL_STATES) {
         return LEVEL_STATES.computeIfAbsent(level, ignored -> new ReplayController.LevelState());
      }
   }

   private static ReplayController.QueueState queueState(Object queue) {
      synchronized (QUEUE_STATES) {
         return QUEUE_STATES.computeIfAbsent(queue, ignored -> new ReplayController.QueueState());
      }
   }

   private static ReplayController.DeferredAccess deferred(Object level) {
      ReplayController.DeferredAccess local = deferredAccess;
      if (local != null) {
         return local.usable ? local : null;
      }

      synchronized (ReplayController.class) {
         local = deferredAccess;
         if (local == null) {
            local = new ReplayController.DeferredAccess(level);
            deferredAccess = local;
         }
      }

      return local.usable ? local : null;
   }

   private static ReplayController.RuntimeAccess runtime(Object level) {
      ReplayController.RuntimeAccess local = runtimeAccess;
      if (local != null) {
         return local.usable ? local : null;
      }

      synchronized (ReplayController.class) {
         local = runtimeAccess;
         if (local == null) {
            local = new ReplayController.RuntimeAccess(level);
            runtimeAccess = local;
         }
      }

      return local.usable ? local : null;
   }

   private static ReplayController.ChunksmithAccess chunksmith() {
      ReplayController.ChunksmithAccess local = chunksmithAccess;
      if (local != null) {
         return local;
      }

      synchronized (ReplayController.class) {
         local = chunksmithAccess;
         if (local == null) {
            local = new ReplayController.ChunksmithAccess();
            chunksmithAccess = local;
         }

         return local;
      }
   }

   private static boolean generationEnabled() {
      ReplayController.ConfigAccess local = configAccess;
      if (local == null) {
         synchronized (ReplayController.class) {
            local = configAccess;
            if (local == null) {
               local = new ReplayController.ConfigAccess();
               configAccess = local;
            }
         }
      }

      return local.enabled();
   }

   private static long packChunk(int x, int z) {
      return x & 4294967295L | (z & 4294967295L) << 32;
   }

   private static double clamp(double value, double min, double max) {
      return Math.max(min, Math.min(max, value));
   }

   private static Method findMethod(Class<?> type, String name, int parameterCount) {
      for (Class<?> c = type; c != null; c = c.getSuperclass()) {
         for (Method method : c.getDeclaredMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
               try {
                  method.setAccessible(true);
               } catch (RuntimeException var9) {
               }

               return method;
            }
         }
      }

      for (Method method : type.getMethods()) {
         if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
            return method;
         }
      }

      return null;
   }

   private static Method findAnyMethod(Class<?> type, int parameterCount, String... names) {
      for (String name : names) {
         Method method = findMethod(type, name, parameterCount);
         if (method != null) {
            return method;
         }
      }

      return null;
   }

   private static Field findField(Class<?> type, String name) {
      for (Class<?> c = type; c != null; c = c.getSuperclass()) {
         try {
            Field field = c.getDeclaredField(name);

            try {
               field.setAccessible(true);
            } catch (RuntimeException var5) {
            }

            return field;
         } catch (NoSuchFieldException var6) {
         }
      }

      return null;
   }

   private static Object invoke(Method method, Object receiver, Object... args) {
      if (method == null) {
         return null;
      }

      try {
         return method.invoke(receiver, args);
      } catch (ReflectiveOperationException | RuntimeException ignored) {
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
         Method probing = null;
         Method atCeiling = null;
         Method holding = null;
         Method live = null;
         Method draining = null;
         Method held = null;
         Field inflightF = null;
         Field limitF = null;
         Field ceilingF = null;
         Field sinkF = null;

         try {
            ClassLoader cl = ReplayController.class.getClassLoader();
            Class<?> tick = Class.forName("com.kishku7.chunksmith.util.TickBudget", false, cl);
            probing = ReplayController.findAnyMethod(tick, 0, "isProbing");
            atCeiling = ReplayController.findAnyMethod(tick, 0, "atCeiling");
         } catch (ClassNotFoundException | LinkageError var16) {
         }

         try {
            Class<?> heap = Class.forName("com.kishku7.chunksmith.util.HeapPressure", false, ReplayController.class.getClassLoader());
            holding = ReplayController.findAnyMethod(heap, 0, "isHolding");
            live = ReplayController.findAnyMethod(heap, 0, "liveEstimatePercent", "usedPercent");
         } catch (ClassNotFoundException | LinkageError var15) {
         }

         try {
            Class<?> residency = Class.forName("com.kishku7.chunksmith.util.ChunkResidency", false, ReplayController.class.getClassLoader());
            draining = ReplayController.findAnyMethod(residency, 0, "isDraining");
            held = ReplayController.findAnyMethod(residency, 0, "isGenerationHeld");
         } catch (ClassNotFoundException | LinkageError var14) {
         }

         try {
            Class<?> stats = Class.forName("com.kishku7.chunksmith.util.DispatchStats", false, ReplayController.class.getClassLoader());
            inflightF = ReplayController.findField(stats, "inFlight");
            limitF = ReplayController.findField(stats, "limit");
            ceilingF = ReplayController.findField(stats, "ceiling");
            sinkF = ReplayController.findField(stats, "sinkQueue");
         } catch (ClassNotFoundException | LinkageError var13) {
         }

         this.tickProbing = probing;
         this.tickAtCeiling = atCeiling;
         this.heapHolding = holding;
         this.heapLive = live;
         this.residencyDraining = draining;
         this.generationHeld = held;
         this.inFlight = inflightF;
         this.limit = limitF;
         this.ceiling = ceilingF;
         this.sinkQueue = sinkF;
      }

      ReplayController.ChunksmithSignals sample() {
         ReplayController.ChunksmithSignals out = new ReplayController.ChunksmithSignals();
         out.probing = this.bool(ReplayController.invoke(this.tickProbing, null));
         out.atCeiling = this.bool(ReplayController.invoke(this.tickAtCeiling, null));
         out.heapHolding = this.bool(ReplayController.invoke(this.heapHolding, null));
         out.residencyDraining = this.bool(ReplayController.invoke(this.residencyDraining, null));
         out.generationHeld = this.bool(ReplayController.invoke(this.generationHeld, null));
         Object live = ReplayController.invoke(this.heapLive, null);
         if (live instanceof Number) {
            out.heapLivePercent = ((Number)live).doubleValue();
         }

         out.inFlight = this.intField(this.inFlight);
         out.limit = this.intField(this.limit);
         out.ceiling = this.intField(this.ceiling);
         out.sinkQueue = this.intField(this.sinkQueue);
         return out;
      }

      private boolean bool(Object value) {
         return value instanceof Boolean && (Boolean)value;
      }

      private int intField(Field field) {
         if (field != null && Modifier.isStatic(field.getModifiers())) {
            try {
               return field.getInt(null);
            } catch (IllegalAccessException | RuntimeException ignored) {
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
         Object sc = null;
         Field villages = null;
         Field lone = null;
         boolean ok = false;

         try {
            Class<?> type = Class.forName("org.millenaire.config.MillenaireServerConfig", false, ReplayController.class.getClassLoader());
            Field server = ReplayController.findField(type, "SERVER");
            sc = server == null ? null : server.get(null);
            if (sc != null) {
               villages = ReplayController.findField(sc.getClass(), "generateVillages");
               lone = ReplayController.findField(sc.getClass(), "generateLoneBuildings");
               ok = villages != null && lone != null;
            }
         } catch (ReflectiveOperationException | RuntimeException | LinkageError var7) {
         }

         this.serverConfig = sc;
         this.generateVillages = villages;
         this.generateLoneBuildings = lone;
         this.usable = ok;
      }

      boolean enabled() {
         return !this.usable ? true : this.read(this.generateVillages) || this.read(this.generateLoneBuildings);
      }

      private boolean read(Field field) {
         try {
            Object holder = field.get(this.serverConfig);
            if (holder == null) {
               return false;
            }

            Method get = this.valueGet;
            if (get == null || !get.getDeclaringClass().isAssignableFrom(holder.getClass())) {
               get = ReplayController.findAnyMethod(holder.getClass(), 0, "get");
               this.valueGet = get;
            }

            Object value = ReplayController.invoke(get, holder);
            return value instanceof Boolean && (Boolean)value;
         } catch (IllegalAccessException | RuntimeException ignored) {
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

      DeferredAccess(Object level) {
         Method get0 = null;
         Method remember0 = null;
         Method release0 = null;
         Method consume0 = null;
         Field pending0 = null;
         Field queued0 = null;
         boolean ok = false;

         try {
            Class<?> type = Class.forName("maki.millenairelod.DeferredData", false, ReplayController.class.getClassLoader());
            get0 = ReplayController.findMethod(type, "get", 1);
            remember0 = ReplayController.findMethod(type, "remember", 2);
            release0 = ReplayController.findMethod(type, "release", 1);
            consume0 = ReplayController.findMethod(type, "consume", 1);
            pending0 = ReplayController.findField(type, "pending");
            queued0 = ReplayController.findField(type, "queued");
            ok = get0 != null && remember0 != null && release0 != null && consume0 != null && pending0 != null && queued0 != null;
         } catch (ClassNotFoundException | LinkageError var10) {
         }

         this.get = get0;
         this.remember = remember0;
         this.release = release0;
         this.consume = consume0;
         this.pending = pending0;
         this.queued = queued0;
         this.usable = ok;
      }

      Object get(Object level) {
         return ReplayController.invoke(this.get, null, level);
      }

      void remember(Object data, long key, boolean claimed) {
         ReplayController.invoke(this.remember, data, key, claimed);
      }

      void release(Object data, long key) {
         ReplayController.invoke(this.release, data, key);
      }

      void consume(Object data, long key) {
         ReplayController.invoke(this.consume, data, key);
      }

      boolean pendingContains(Object data, long key) {
         return this.contains(this.set(this.pending, data), key);
      }

      boolean queuedContains(Object data, long key) {
         return this.contains(this.set(this.queued, data), key);
      }

      private Object set(Field field, Object data) {
         try {
            return field.get(data);
         } catch (IllegalAccessException | RuntimeException ignored) {
            return null;
         }
      }

      private boolean contains(Object set, long key) {
         if (set == null) {
            return false;
         }

         Method local = this.contains;
         if (local == null || local.getDeclaringClass() != set.getClass()) {
            local = ReplayController.findMethod(set.getClass(), "contains", 1);
            this.contains = local;
         }

         Object value = ReplayController.invoke(local, set, key);
         return value instanceof Boolean && (Boolean)value;
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
         long now = System.nanoTime();
         long collection = 0L;
         boolean known = false;

         try {
            for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
               long time = bean.getCollectionTime();
               if (time >= 0L) {
                  collection += time;
                  known = true;
               }
            }
         } catch (RuntimeException | LinkageError ignored) {
            return ema;
         }

         if (!known) {
            return ema;
         }

         if (lastWallNanos != 0L && lastCollectionMillis >= 0L) {
            double wallMillis = (now - lastWallNanos) / 1000000.0;
            if (wallMillis < 500.0) {
               return ema;
            }

            long delta = Math.max(0L, collection - lastCollectionMillis);
            double fraction = ReplayController.clamp(delta / wallMillis, 0.0, 1.0);
            ema = ema < 0.0 ? fraction : ema * 0.7 + fraction * 0.3;
            lastWallNanos = now;
            lastCollectionMillis = collection;
            return ema;
         } else {
            lastWallNanos = now;
            lastCollectionMillis = collection;
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

      QueueSnapshot(int main, int lone) {
         this.main = Math.max(0, main);
         this.lone = Math.max(0, lone);
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

      ReplayController.QueueObservation observe(int main, int lone) {
         ReplayController.QueueObservation result = new ReplayController.QueueObservation();
         if (this.lastMain >= 0) {
            int expected = Math.max(0, this.lastMain + this.admittedSinceObservation);
            int drained = Math.max(0, expected - main);
            result.externalMainGrowth = Math.max(0, main - expected);
            this.mainDrainEma = this.mainDrainEma < 0.0 ? drained : this.mainDrainEma * 0.7 + drained * 0.3;
            if (expected > 0 && main > 0 && drained == 0) {
               this.mainStallSamples++;
            } else {
               this.mainStallSamples = 0;
            }
         }

         if (this.lastLone >= 0) {
            result.loneGrowth = Math.max(0, lone - this.lastLone);
            if (this.lastLone > 0 && lone >= this.lastLone) {
               this.loneStallSamples++;
            } else {
               this.loneStallSamples = 0;
            }
         }

         this.lastMain = main;
         this.lastLone = lone;
         this.admittedSinceObservation = 0;
         result.mainStallSamples = this.mainStallSamples;
         result.loneStallSamples = this.loneStallSamples;
         result.mainDrainEma = this.mainDrainEma;
         return result;
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

      RuntimeAccess(Object level) {
         Method game = null;
         Method server = null;
         Method chunk = null;
         Method chunksAt = null;
         Method playerList = null;
         Constructor<?> bpCtor = null;
         Constructor<?> cpCtor = null;
         Method bx = null;
         Method bz = null;
         Method hdrop = null;
         Field cx = null;
         Field cz = null;
         boolean ok = false;

         try {
            Class<?> levelClass = level.getClass();
            game = ReplayController.findAnyMethod(levelClass, 0, "getGameTime");
            server = ReplayController.findAnyMethod(levelClass, 0, "getServer");
            chunk = ReplayController.findAnyMethod(levelClass, 2, "hasChunk");
            Class<?> blockPos = Class.forName("net.minecraft.core.BlockPos", false, ReplayController.class.getClassLoader());
            Class<?> chunkPos = Class.forName("net.minecraft.world.level.ChunkPos", false, ReplayController.class.getClassLoader());
            chunksAt = ReplayController.findAnyMethod(levelClass, 2, "hasChunksAt");
            playerList = ReplayController.findAnyMethod(levelClass, 0, "players");
            bpCtor = blockPos.getConstructor(int.class, int.class, int.class);
            cpCtor = chunkPos.getConstructor(int.class, int.class);
            bx = ReplayController.findAnyMethod(blockPos, 0, "getX");
            bz = ReplayController.findAnyMethod(blockPos, 0, "getZ");
            cx = ReplayController.findField(chunkPos, "x");
            cz = ReplayController.findField(chunkPos, "z");
            Class<?> guard = Class.forName("com.kishku7.chunksmith.lod.HorizonGuard", false, ReplayController.class.getClassLoader());
            hdrop = ReplayController.findMethod(guard, "drop", 2);
            ok = game != null
               && server != null
               && chunk != null
               && chunksAt != null
               && playerList != null
               && bpCtor != null
               && cpCtor != null
               && bx != null
               && bz != null
               && cx != null
               && cz != null
               && hdrop != null;
         } catch (ReflectiveOperationException | LinkageError var19) {
         }

         this.getGameTime = game;
         this.getServer = server;
         this.hasChunk = chunk;
         this.hasChunksAt = chunksAt;
         this.players = playerList;
         this.blockPosCtor = bpCtor;
         this.chunkPosCtor = cpCtor;
         this.blockGetX = bx;
         this.blockGetZ = bz;
         this.chunkX = cx;
         this.chunkZ = cz;
         this.horizonDrop = hdrop;
         this.usable = ok;
      }

      long gameTime(Object level) {
         Object value = ReplayController.invoke(this.getGameTime, level);
         return value instanceof Number ? ((Number)value).longValue() : -1L;
      }

      Object server(Object level) {
         return ReplayController.invoke(this.getServer, level);
      }

      boolean hasChunk(Object level, int x, int z) {
         Object value = ReplayController.invoke(this.hasChunk, level, x, z);
         return value instanceof Boolean && (Boolean)value;
      }

      boolean hasChunksAt(Object level, Object from, Object to) {
         Object value = ReplayController.invoke(this.hasChunksAt, level, from, to);
         return value instanceof Boolean && (Boolean)value;
      }

      boolean hasChunksAt(Object level, int minX, int minZ, int maxX, int maxZ) {
         Object from = this.newBlockPos(minX, 0, minZ);
         Object to = this.newBlockPos(maxX, 0, maxZ);
         return from != null && to != null && this.hasChunksAt(level, from, to);
      }

      List<?> players(Object level) {
         Object value = ReplayController.invoke(this.players, level);
         return value instanceof List ? (List)value : List.of();
      }

      Object newBlockPos(int x, int y, int z) {
         try {
            return this.blockPosCtor.newInstance(x, y, z);
         } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
         }
      }

      Object newChunkPos(int x, int z) {
         try {
            return this.chunkPosCtor.newInstance(x, z);
         } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
         }
      }

      int blockX(Object pos) {
         Object value = ReplayController.invoke(this.blockGetX, pos);
         return value instanceof Number ? ((Number)value).intValue() : Integer.MIN_VALUE;
      }

      int blockZ(Object pos) {
         Object value = ReplayController.invoke(this.blockGetZ, pos);
         return value instanceof Number ? ((Number)value).intValue() : Integer.MIN_VALUE;
      }

      int chunkX(Object pos) {
         try {
            return this.chunkX.getInt(pos);
         } catch (IllegalAccessException | RuntimeException ignored) {
            return Integer.MIN_VALUE;
         }
      }

      int chunkZ(Object pos) {
         try {
            return this.chunkZ.getInt(pos);
         } catch (IllegalAccessException | RuntimeException ignored) {
            return Integer.MIN_VALUE;
         }
      }

      boolean horizonDrop(Object level, Object chunkPos) {
         Object value = ReplayController.invoke(this.horizonDrop, null, level, chunkPos);
         return value instanceof Boolean && (Boolean)value;
      }

      double averageMspt(Object server) {
         Method local = this.serverAverage;
         if (local == null) {
            local = ReplayController.findAnyMethod(server.getClass(), 0, "getAverageTickTimeNanos", "getAverageNanosPerTick", "getAverageTickTime");
            this.serverAverage = local;
         }

         Object value = ReplayController.invoke(local, server);
         if (!(value instanceof Number)) {
            return -1.0;
         }

         double raw = ((Number)value).doubleValue();
         if (raw < 0.0) {
            return -1.0;
         }

         String name = local == null ? "" : local.getName().toLowerCase();
         return name.contains("nano") ? raw / 1000000.0 : raw;
      }

      int viewDistance(Object server) {
         Method spl = this.serverPlayerList;
         if (spl == null) {
            spl = ReplayController.findAnyMethod(server.getClass(), 0, "getPlayerList");
            this.serverPlayerList = spl;
         }

         Object list = ReplayController.invoke(spl, server);
         if (list == null) {
            return 10;
         }

         Method vd = this.viewDistance;
         if (vd == null) {
            vd = ReplayController.findAnyMethod(list.getClass(), 0, "getViewDistance");
            this.viewDistance = vd;
         }

         Object value = ReplayController.invoke(vd, list);
         return value instanceof Number ? ((Number)value).intValue() : 10;
      }

      Object playerChunk(Object player) {
         Method method = this.playerChunkPosition;
         if (method == null) {
            method = ReplayController.findAnyMethod(player.getClass(), 0, "chunkPosition");
            this.playerChunkPosition = method;
         }

         return ReplayController.invoke(method, player);
      }

      ReplayController.QueueSnapshot queueSnapshot(Object queue) {
         Method method = this.queueGetStats;
         if (method == null || !method.getDeclaringClass().isAssignableFrom(queue.getClass())) {
            method = ReplayController.findAnyMethod(queue.getClass(), 0, "getStats");
            this.queueGetStats = method;
         }

         return ReplayController.invoke(method, queue) instanceof Map<?, ?> map
            ? new ReplayController.QueueSnapshot(this.number(map.get("mainQueueSize")), this.number(map.get("lbQueueSize")))
            : null;
      }

      boolean enqueue(Object queue, Object blockPos) {
         Method method = this.queueEnqueue;
         if (method == null || !method.getDeclaringClass().isAssignableFrom(queue.getClass())) {
            method = ReplayController.findAnyMethod(queue.getClass(), 1, "enqueue");
            this.queueEnqueue = method;
         }

         if (method == null) {
            return false;
         }

         try {
            method.invoke(queue, blockPos);
            return true;
         } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
         }
      }

      private int number(Object value) {
         return value instanceof Number ? ((Number)value).intValue() : 0;
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
