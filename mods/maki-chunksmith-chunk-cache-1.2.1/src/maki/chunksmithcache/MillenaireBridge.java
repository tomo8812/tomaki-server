package maki.chunksmithcache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

final class MillenaireBridge {
   private static volatile boolean resolved;
   private static Class<?> millenaire;
   private static boolean millenairePresent;
   private static Field suppressed;
   private static Method setSuppressed;
   private static Class<?> deferredData;
   private static Method deferredGet;
   private static Method deferredRemember;

   static boolean isLoaded() {
      resolve();
      return millenairePresent;
   }

   static boolean canBackfillSafely() {
      resolve();
      return !millenairePresent ? true : suppressed != null && setSuppressed != null && deferredData != null && deferredGet != null && deferredRemember != null;
   }

   private MillenaireBridge() {
   }

   static Boolean suppressNaturalSpawns(boolean var0) {
      resolve();
      if (millenaire != null && suppressed != null && setSuppressed != null) {
         try {
            boolean var1 = suppressed.getBoolean(null);
            setSuppressed.invoke(null, var0);
            return var1;
         } catch (ReflectiveOperationException | RuntimeException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   static void restore(Boolean var0) {
      if (var0 != null) {
         try {
            setSuppressed.invoke(null, var0);
         } catch (ReflectiveOperationException | RuntimeException var2) {
         }
      }
   }

   static boolean rememberPending(ServerLevel var0, ChunkPos var1) {
      if (!"minecraft:overworld".equals(var0.dimension().location().toString())) {
         return true;
      }

      resolve();
      if (!millenairePresent) {
         return true;
      }

      if (deferredGet != null && deferredRemember != null) {
         try {
            Object var2 = deferredGet.invoke(null, var0);
            if (var2 == null) {
               return false;
            }

            deferredRemember.invoke(var2, var1.toLong(), false);
            return true;
         } catch (ReflectiveOperationException | RuntimeException var3) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static synchronized void resolve() {
      if (!resolved) {
         resolved = true;
         ClassLoader var0 = MillenaireBridge.class.getClassLoader();

         try {
            millenaire = Class.forName("org.millenaire.Millenaire", false, var0);
            millenairePresent = true;

            try {
               suppressed = millenaire.getDeclaredField("naturalSpawnSuppressed");
               suppressed.setAccessible(true);
               setSuppressed = millenaire.getMethod("setNaturalSpawnSuppressed", boolean.class);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError var3) {
               suppressed = null;
               setSuppressed = null;
            }
         } catch (ClassNotFoundException | LinkageError var4) {
            millenaire = null;
            millenairePresent = false;
         }

         try {
            deferredData = Class.forName("maki.millenairelod.DeferredData", false, var0);
            deferredGet = deferredData.getMethod("get", ServerLevel.class);
            deferredRemember = deferredData.getMethod("remember", long.class, boolean.class);
         } catch (ReflectiveOperationException | LinkageError var2) {
            deferredData = null;
         }
      }
   }
}
