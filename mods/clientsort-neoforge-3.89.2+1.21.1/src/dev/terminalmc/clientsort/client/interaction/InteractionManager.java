package dev.terminalmc.clientsort.client.interaction;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;

public class InteractionManager {
   public static final InteractionManager.Waiter TICK_WAITER = type -> type == InteractionManager.TriggerType.TICK;
   private static final ArrayDeque<InteractionManager.InteractionEvent> eventQueue = new ArrayDeque<>();
   private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
      1, Thread.ofVirtual().name("clientsort-interaction-scheduler").factory()
   );
   private static ScheduledFuture<?> tickFuture;
   private static InteractionManager.Waiter waiter = null;

   public static void push(InteractionManager.InteractionEvent event) {
      synchronized (eventQueue) {
         eventQueue.add(event);
         if (waiter == null) {
            triggerSend(InteractionManager.TriggerType.INITIAL);
         }
      }
   }

   public static void pushAll(Collection<InteractionManager.InteractionEvent> events) {
      synchronized (eventQueue) {
         eventQueue.addAll(events);
         if (waiter == null) {
            triggerSend(InteractionManager.TriggerType.INITIAL);
         }
      }
   }

   public static void clear() {
      synchronized (eventQueue) {
         eventQueue.clear();
         waiter = null;
      }
   }

   public static void triggerSend(InteractionManager.TriggerType type) {
      synchronized (eventQueue) {
         if (waiter == null || waiter.trigger(type)) {
            do {
               InteractionManager.InteractionEvent event = eventQueue.poll();
               if (event == null) {
                  waiter = null;
                  break;
               }

               doSendEvent(event);
            } while (waiter.trigger(InteractionManager.TriggerType.INITIAL));
         }
      }
   }

   private static void doSendEvent(InteractionManager.InteractionEvent event) {
      InteractionManager.Waiter blockingWaiter = tt -> false;
      waiter = blockingWaiter;
      Minecraft.getInstance().execute(() -> {
         synchronized (eventQueue) {
            if (waiter == blockingWaiter) {
               waiter = event.send();
            }
         }
      });
   }

   public static void setTickRate(long intervalMs) {
      if (tickFuture != null) {
         tickFuture.cancel(false);
      }

      tickFuture = executor.scheduleAtFixedRate(InteractionManager::tick, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
   }

   private static void tick() {
      try {
         triggerSend(InteractionManager.TriggerType.TICK);
      } catch (Exception e) {
         LogUtils.getLogger().error("Error while ticking InteractionManager", e);
      }
   }

   public static class CallbackEvent implements InteractionManager.InteractionEvent {
      private final Supplier<InteractionManager.Waiter> callback;

      public CallbackEvent(Supplier<InteractionManager.Waiter> callback) {
         this.callback = callback;
      }

      @Override
      public InteractionManager.Waiter send() {
         return this.callback.get();
      }
   }

   @FunctionalInterface
   public interface InteractionEvent {
      InteractionManager.Waiter send();
   }

   public enum TriggerType {
      INITIAL,
      GUI_CONFIRM,
      TICK;

      // $VF: synthetic method
      private static InteractionManager.TriggerType[] $values() {
         return new InteractionManager.TriggerType[]{INITIAL, GUI_CONFIRM, TICK};
      }
   }

   @FunctionalInterface
   public interface Waiter {
      boolean trigger(InteractionManager.TriggerType var1);

      static InteractionManager.Waiter equal(InteractionManager.TriggerType type) {
         return type::equals;
      }
   }
}
