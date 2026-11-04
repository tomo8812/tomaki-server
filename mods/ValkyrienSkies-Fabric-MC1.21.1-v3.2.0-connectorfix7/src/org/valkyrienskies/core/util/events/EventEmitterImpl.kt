package org.valkyrienskies.core.util.events

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentHashMap.KeySetView
import kotlin.jvm.internal.SourceDebugExtension
import org.valkyrienskies.core.api.event.EventConsumer
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.api.event.SingleEvent

@SourceDebugExtension(["SMAP\nEventEmitterImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventEmitterImpl.kt\norg/valkyrienskies/core/util/events/EventEmitterImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,31:1\n1863#2,2:32\n*S KotlinDebug\n*F\n+ 1 EventEmitterImpl.kt\norg/valkyrienskies/core/util/events/EventEmitterImpl\n*L\n13#1:32,2\n*E\n"])
public class EventEmitterImpl<T> : SingleEvent<T> {
   private final val listeners: KeySetView<org.valkyrienskies.core.util.events.EventEmitterImpl.EventListener<Any>, Boolean> = ConcurrentHashMap.newKeySet()

   public override fun emit(value: Any) {
      val var10000: KeySetView = this.listeners

      for (`element$iv` in var10000) {
         (`element$iv` as EventEmitterImpl.EventListener).accept(value)
      }
   }

   public override fun on(cb: EventConsumer<Any>): RegisteredListener {
      val listener: EventEmitterImpl.EventListener = EventEmitterImpl.EventListener(cb)
      listener.handler = { 
         `this$0`.listeners.remove(`$listener`)
      }
      this.listeners.add(listener)
      return listener.handler
   }

   private data class EventListener<T>(cb: EventConsumer<Any>) {
      public final val cb: EventConsumer<Any>
      public final lateinit var handler: RegisteredListener

      init {
         this.cb = cb
      }

      public fun accept(event: Any) {
         this.cb.accept((T)event, this.handler)
      }

      public operator fun component1(): EventConsumer<Any> {
         return this.cb
      }

      public fun copy(cb: EventConsumer<Any> = this.cb): org.valkyrienskies.core.util.events.EventEmitterImpl.EventListener<Any> {
         return EventEmitterImpl.EventListener<>(cb)
      }

      public override fun toString(): String {
         return "EventListener(cb=${this.cb})"
      }

      public override fun hashCode(): Int {
         return this.cb.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is EventEmitterImpl.EventListener && this.cb == (other as EventEmitterImpl.EventListener).cb
         }
      }
   }
}
