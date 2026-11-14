package com.kishku7.chunksmith.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class EventBus {
   private static final MethodHandle accept;
   private final Map<Class<?>, Set<Consumer<?>>> subscribers = new HashMap<>();

   public <T> void subscribe(Class<T> eventClass, Consumer<T> subscriber) {
      this.subscribers.computeIfAbsent(eventClass, x -> new HashSet<>());
      this.subscribers.get(eventClass).add(subscriber);
   }

   public <T> void unsubscribe(Class<T> eventClass, Consumer<T> subscriber) {
      this.subscribers.computeIfAbsent(eventClass, x -> new HashSet<>());
      this.subscribers.get(eventClass).remove(subscriber);
   }

   public void unsubscribeAll() {
      this.subscribers.clear();
   }

   public void unsubscribeAll(Class<?> eventClass) {
      this.subscribers.remove(eventClass);
   }

   public void call(Object event) {
      Class<?> eventClass = event.getClass();
      if (accept != null && this.subscribers.containsKey(eventClass)) {
         this.subscribers.get(eventClass).forEach(subscriber -> {
            try {
               accept.invoke((Consumer)subscriber, (Object)event);
            } catch (Throwable e) {
               e.printStackTrace();
            }
         });
      }
   }

   static {
      MethodHandle acceptMethodHandle = null;

      try {
         acceptMethodHandle = MethodHandles.publicLookup().findVirtual(Consumer.class, "accept", MethodType.methodType(void.class, Object.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
         e.printStackTrace();
      }

      accept = acceptMethodHandle;
   }
}
