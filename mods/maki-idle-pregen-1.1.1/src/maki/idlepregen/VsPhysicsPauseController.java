package maki.idlepregen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class VsPhysicsPauseController {
   private static final Logger LOG = System.getLogger("MakiIdlePregen");
   private static boolean sessionActive;
   private static Object sessionPipeline;
   private static Method getRunningMethod;
   private static Method setRunningMethod;
   private static Field synchronizedPhysicsField;
   private static boolean accessResolved;
   private static boolean ownsAsyncPause;
   private static boolean asyncOwnershipBlocked;
   private static boolean pulseInFlight;

   private VsPhysicsPauseController() {
   }

   static synchronized void beginSession() {
      restoreOwnedPause();
      resetSession();
      sessionActive = true;
   }

   static synchronized void tick(Object server) {
      if (sessionActive) {
         if (sessionPipeline == null) {
            try {
               Method getPipeline = server.getClass().getMethod("getVsPipeline");
               Object pipeline = getPipeline.invoke(server);
               if (pipeline == null) {
                  return;
               }

               sessionPipeline = pipeline;
               resolveAccess(pipeline);
            } catch (NoSuchMethodException ex) {
               accessResolved = true;
               asyncOwnershipBlocked = true;
               LOG.log(Level.INFO, "Valkyrien Skies pipeline API is unavailable; zero-delta guard remains active");
               return;
            } catch (ReflectiveOperationException | RuntimeException ex) {
               accessResolved = true;
               asyncOwnershipBlocked = true;
               LOG.log(Level.WARNING, "Failed to resolve Valkyrien Skies pipeline; zero-delta guard remains active", ex);
               return;
            }
         }

         if (accessResolved && sessionPipeline != null) {
            try {
               if (isSynchronizedPhysics(sessionPipeline)) {
                  if (ownsAsyncPause) {
                     restoreOwnedPause();
                     asyncOwnershipBlocked = false;
                     LOG.log(Level.INFO, "Valkyrien Skies switched to synchronized physics; restored owned async pause");
                  }

                  return;
               }

               if (ownsAsyncPause) {
                  if (!pulseInFlight && readRunning(sessionPipeline)) {
                     ownsAsyncPause = false;
                     asyncOwnershipBlocked = true;
                     LOG.log(Level.INFO, "Valkyrien Skies async physics was resumed externally; pause ownership released");
                  }

                  return;
               }

               if (asyncOwnershipBlocked) {
                  return;
               }

               if (!readRunning(sessionPipeline)) {
                  asyncOwnershipBlocked = true;
                  LOG.log(Level.INFO, "Valkyrien Skies async physics was already paused; idle pregen will not take ownership");
                  return;
               }

               writeRunning(sessionPipeline, false);
               if (readRunning(sessionPipeline)) {
                  asyncOwnershipBlocked = true;
                  LOG.log(Level.WARNING, "Valkyrien Skies async physics did not enter the paused state; zero-delta guard only");
                  return;
               }

               ownsAsyncPause = true;
               LOG.log(Level.INFO, "Paused Valkyrien Skies async physics for idle pregen");
            } catch (ReflectiveOperationException | RuntimeException ex) {
               failOpen("Failed to coordinate Valkyrien Skies async physics", ex);
            }
         }
      }
   }

   static synchronized double adjustPhysicsDelta(Object pipeline, double deltaSeconds) {
      if (!sessionActive) {
         return deltaSeconds;
      }

      if (sessionPipeline == null) {
         return 0.0;
      }

      if (pipeline != sessionPipeline) {
         return deltaSeconds;
      }

      if (accessResolved && ownsAsyncPause && !pulseInFlight) {
         try {
            if (isSynchronizedPhysics(pipeline)) {
               restoreOwnedPause();
               asyncOwnershipBlocked = false;
            } else if (readRunning(pipeline)) {
               ownsAsyncPause = false;
               asyncOwnershipBlocked = true;
               LOG.log(Level.INFO, "Valkyrien Skies async physics resumed outside an idle state-sync pulse; ownership released");
            }
         } catch (ReflectiveOperationException | RuntimeException ex) {
            failOpen("Failed to observe Valkyrien Skies physics step ownership", ex);
         }
      }

      return 0.0;
   }

   static synchronized void afterGameFrame(Object pipeline) {
      if (sessionActive && pipeline == sessionPipeline && ownsAsyncPause && !pulseInFlight) {
         try {
            if (isSynchronizedPhysics(pipeline)) {
               restoreOwnedPause();
               asyncOwnershipBlocked = false;
               return;
            }

            if (readRunning(pipeline)) {
               ownsAsyncPause = false;
               asyncOwnershipBlocked = true;
               LOG.log(Level.INFO, "Valkyrien Skies async physics was already running before state-sync pulse; ownership released");
               return;
            }

            pulseInFlight = true;
            writeRunning(pipeline, true);
         } catch (ReflectiveOperationException | RuntimeException ex) {
            pulseInFlight = false;
            failOpen("Failed to wake Valkyrien Skies for idle state synchronization", ex);
         }
      }
   }

   static synchronized void completePhysicsStep(Object pipeline) {
      if (sessionActive && pipeline == sessionPipeline && ownsAsyncPause && pulseInFlight) {
         try {
            pulseInFlight = false;
            if (isSynchronizedPhysics(pipeline)) {
               restoreOwnedPause();
               asyncOwnershipBlocked = false;
               return;
            }

            if (readRunning(pipeline)) {
               writeRunning(pipeline, false);
            }
         } catch (ReflectiveOperationException | RuntimeException ex) {
            pulseInFlight = false;
            failOpen("Failed to re-pause Valkyrien Skies after idle state synchronization", ex);
         }
      }
   }

   static synchronized boolean ownsAsyncPause() {
      return sessionActive && ownsAsyncPause;
   }

   static synchronized void leaveSession() {
      sessionActive = false;
      restoreOwnedPause();
      resetSession();
   }

   private static void resolveAccess(Object pipeline) throws ReflectiveOperationException {
      getRunningMethod = pipeline.getClass().getMethod("getArePhysicsRunning");
      setRunningMethod = pipeline.getClass().getMethod("setArePhysicsRunning", boolean.class);
      synchronizedPhysicsField = pipeline.getClass().getDeclaredField("c");
      synchronizedPhysicsField.setAccessible(true);
      if (synchronizedPhysicsField.getType() != boolean.class) {
         throw new NoSuchFieldException("FQ.c is no longer the synchronized-physics boolean");
      }

      accessResolved = true;
   }

   private static boolean isSynchronizedPhysics(Object pipeline) throws IllegalAccessException {
      return synchronizedPhysicsField.getBoolean(pipeline);
   }

   private static boolean readRunning(Object pipeline) throws ReflectiveOperationException {
      return (Boolean)getRunningMethod.invoke(pipeline);
   }

   private static void writeRunning(Object pipeline, boolean running) throws ReflectiveOperationException {
      setRunningMethod.invoke(pipeline, running);
   }

   private static void restoreOwnedPause() {
      if (ownsAsyncPause && sessionPipeline != null && accessResolved) {
         Object pipeline = sessionPipeline;
         ownsAsyncPause = false;
         pulseInFlight = false;

         try {
            if (!readRunning(pipeline)) {
               writeRunning(pipeline, true);
               LOG.log(Level.INFO, "Resumed Valkyrien Skies async physics owned by idle pregen");
            }
         } catch (ReflectiveOperationException | RuntimeException ex) {
            LOG.log(Level.WARNING, "Failed to resume Valkyrien Skies async physics owned by idle pregen", ex);
         }
      } else {
         ownsAsyncPause = false;
         pulseInFlight = false;
      }
   }

   private static void failOpen(String message, Throwable error) {
      LOG.log(Level.WARNING, message + "; releasing pause ownership", error);
      if (ownsAsyncPause && sessionPipeline != null && accessResolved) {
         try {
            if (!readRunning(sessionPipeline)) {
               writeRunning(sessionPipeline, true);
            }
         } catch (ReflectiveOperationException | RuntimeException restoreError) {
            LOG.log(Level.WARNING, "Failed to restore Valkyrien Skies physics while releasing ownership", restoreError);
         }
      }

      ownsAsyncPause = false;
      pulseInFlight = false;
      asyncOwnershipBlocked = true;
   }

   private static void resetSession() {
      sessionPipeline = null;
      getRunningMethod = null;
      setRunningMethod = null;
      synchronizedPhysicsField = null;
      accessResolved = false;
      ownsAsyncPause = false;
      asyncOwnershipBlocked = false;
      pulseInFlight = false;
   }
}
