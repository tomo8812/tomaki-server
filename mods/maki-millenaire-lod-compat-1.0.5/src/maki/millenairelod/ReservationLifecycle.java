package maki.millenairelod;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class ReservationLifecycle {
   private static final Map<Object, ReservationLifecycle.State> ACTIVE = new WeakHashMap<>();

   private ReservationLifecycle() {
   }

   public static synchronized void enter(Object var0, Object var1, int var2) {
      if (var0 != null && var1 != null) {
         expire(var0);
         ReservationLifecycle.State var3 = ACTIVE.computeIfAbsent(var0, var0x -> new ReservationLifecycle.State());
         var3.tick = Compat.maki$gameTime(var0);
         var3.attempts.addLast(new ReservationLifecycle.Attempt(Compat.maki$blockX(var1) >> 4, Compat.maki$blockZ(var1) >> 4, radiusChunks(var2)));
      }
   }

   public static synchronized void begin(Object var0, Object var1, int var2) {
      if (var0 != null && var1 != null) {
         expire(var0);
         ReservationLifecycle.State var3 = ACTIVE.get(var0);
         if (var3 != null) {
            ReservationLifecycle.Attempt var4 = var3.attempts.peekLast();
            int var5 = Compat.maki$blockX(var1) >> 4;
            int var6 = Compat.maki$blockZ(var1) >> 4;
            int var7 = radiusChunks(var2);
            if (var4 != null && var4.matches(var5, var6, var7) && !var4.begun) {
               Object var8 = Compat.maki$protection(var0);
               if (var8 != null) {
                  Object var9 = Compat.maki$reservations(var8);

                  for (int var10 = var5 - var7; var10 <= var5 + var7; var10++) {
                     for (int var11 = var6 - var7; var11 <= var6 + var7; var11++) {
                        long var12 = pack(var10, var11);
                        if (Compat.maki$containsReservation(var9, var12)) {
                           var3.counts.merge(var12, 1, Integer::sum);
                           var4.keys.add(var12);
                        }
                     }
                  }

                  var4.begun = true;
               }
            }
         }
      }
   }

   public static synchronized void end(Object var0, Object var1, int var2) {
      if (var0 != null && var1 != null) {
         ReservationLifecycle.State var3 = ACTIVE.get(var0);
         if (var3 != null) {
            ReservationLifecycle.Attempt var4 = var3.attempts.peekLast();
            int var5 = Compat.maki$blockX(var1) >> 4;
            int var6 = Compat.maki$blockZ(var1) >> 4;
            int var7 = radiusChunks(var2);
            if (var4 != null && var4.matches(var5, var6, var7)) {
               var3.attempts.removeLast();
               if (var4.begun) {
                  Object var8 = Compat.maki$protection(var0);
                  if (var8 == null) {
                     var3.counts.clear();
                  } else {
                     Object var9 = Compat.maki$reservations(var8);

                     for (long var11 : var4.keys) {
                        Integer var13 = var3.counts.get(var11);
                        if (var13 != null) {
                           if (var13 <= 1) {
                              var3.counts.remove(var11);
                              Compat.maki$removeReservation(var9, var11);
                           } else {
                              var3.counts.put(var11, var13 - 1);
                           }
                        }
                     }

                     Compat.maki$invalidateVillageCache(var8);
                  }
               }

               if (var3.attempts.isEmpty() && var3.counts.isEmpty()) {
                  ACTIVE.remove(var0);
               }
            }
         }
      }
   }

   public static synchronized void expire(Object var0) {
      if (var0 != null) {
         ReservationLifecycle.State var1 = ACTIVE.get(var0);
         if (var1 != null && var1.tick != Compat.maki$gameTime(var0)) {
            Object var2 = Compat.maki$protection(var0);
            if (var2 != null && !var1.counts.isEmpty()) {
               Object var3 = Compat.maki$reservations(var2);

               for (long var5 : var1.counts.keySet()) {
                  Compat.maki$removeReservation(var3, var5);
               }

               Compat.maki$invalidateVillageCache(var2);
            }

            ACTIVE.remove(var0);
         }
      }
   }

   public static void ignoreKeepAround(String var0, int var1, int var2, int var3) {
   }

   private static int radiusChunks(int var0) {
      return Math.max(0, (var0 + 15) / 16) + 4;
   }

   private static long pack(int var0, int var1) {
      return var0 & 4294967295L | (var1 & 4294967295L) << 32;
   }

   private static final class Attempt {
      final int centerX;
      final int centerZ;
      final int radius;
      final List<Long> keys = new ArrayList<>();
      boolean begun;

      Attempt(int var1, int var2, int var3) {
         this.centerX = var1;
         this.centerZ = var2;
         this.radius = var3;
      }

      boolean matches(int var1, int var2, int var3) {
         return this.centerX == var1 && this.centerZ == var2 && this.radius == var3;
      }
   }

   private static final class State {
      long tick = Long.MIN_VALUE;
      final Map<Long, Integer> counts = new HashMap<>();
      final Deque<ReservationLifecycle.Attempt> attempts = new ArrayDeque<>();
   }
}
