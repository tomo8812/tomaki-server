package org.valkyrienskies.core.impl.hooks

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.Consumer
import java.util.function.Predicate
import org.valkyrienskies.core.api.event.EventConsumer
import org.valkyrienskies.core.api.event.ListenableEvent
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.api.event.SingleEvent
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.world.ClientShipWorld
import org.valkyrienskies.core.api.world.ServerShipWorld

/** @deprecated */
@Deprecated(message = "Use VsCoreApi (generally accessed via org.valkyrienskies.mod.api.vsApi)")
public object VSEvents {
   private final val singleEventConstructor: MethodHandle =
      MethodHandles.lookup().findConstructor(Class.forName("org.valkyrienskies.core.util.events.EventEmitterImpl"), MethodType.methodType(void.class))

   @Deprecated(
      message = "Use vsApi.shipLoadEvent",
      replaceWith = @ReplaceWith(expression = "vsApi.shipLoadEvent", imports = "org.valkyrienskies.mod.api.vsApi")
   )
   public final val shipLoadEvent: SingleEvent<org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEvent> = INSTANCE.newSingleEvent()

   @Deprecated(
      message = "Use vsApi.shipLoadEventClient",
      replaceWith = @ReplaceWith(expression = "vsApi.shipLoadEventClient", imports = "org.valkyrienskies.mod.api.vsApi")
   )
   public final val shipLoadEventClient: SingleEvent<org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEventClient> = INSTANCE.newSingleEvent()

   @Deprecated(
      message = "Use vsApi.shipUnloadEvent",
      replaceWith = @ReplaceWith(expression = "vsApi.shipUnloadEvent", imports = "org.valkyrienskies.mod.api.vsApi")
   )
   public final val shipUnloadEventClient: SingleEvent<org.valkyrienskies.core.impl.hooks.VSEvents.ShipUnloadEventClient> = INSTANCE.newSingleEvent()

   @Deprecated(
      message = "Use vsApi.startUpdateRenderTransformsEvent",
      replaceWith = @ReplaceWith(expression = "vsApi.startUpdateRenderTransformsEvent", imports = "org.valkyrienskies.mod.api.vsApi")
   )
   public final val startUpdateRenderTransformsEvent: SingleEvent<org.valkyrienskies.core.impl.hooks.VSEvents.StartUpdateRenderTransformsEvent> =
      INSTANCE.newSingleEvent()

   @Deprecated(message = "Use vsApi.tickEndEvent", replaceWith = @ReplaceWith(expression = "vsApi.tickEndEvent", imports = "org.valkyrienskies.mod.api.vsApi"))
   public final val tickEndEvent: SingleEvent<org.valkyrienskies.core.impl.hooks.VSEvents.TickEndEvent> = INSTANCE.newSingleEvent()

   private fun <T> newSingleEvent(): SingleEvent<Any> {
      return (SingleEvent)singleEventConstructor.invoke()
   }

   /** @deprecated */
   @Deprecated(message = "Use the interface variant", replaceWith = @ReplaceWith(expression = "ShipLoadEvent", imports = ["org.valkyrienskies.mod.api.events.ShipLoadEvent"]))
   public data class ShipLoadEvent(ship: LoadedServerShip) : org.valkyrienskies.core.api.events.ShipLoadEvent {
      public open val ship: LoadedServerShip

      init {
         this.ship = ship
      }

      public operator fun component1(): LoadedServerShip {
         return this.ship
      }

      public fun copy(ship: LoadedServerShip = this.ship): org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEvent {
         return VSEvents.ShipLoadEvent(ship)
      }

      public override fun toString(): String {
         return "ShipLoadEvent(ship=${this.ship})"
      }

      public override fun hashCode(): Int {
         return this.ship.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is VSEvents.ShipLoadEvent && this.ship == (other as VSEvents.ShipLoadEvent).ship
         }
      }

      /** @deprecated */
      @Deprecated(message = "Use vsApi.shipLoadEvent", replaceWith = @ReplaceWith(expression = "vsApi.shipLoadEvent", imports = ["org.valkyrienskies.mod.api.vsApi"]))
      public companion object : ListenableEvent<VSEvents.ShipLoadEvent> {
         @JvmSynthetic
         private SingleEvent<VSEvents.ShipLoadEvent> $$delegate_0 = VSEvents.INSTANCE.shipLoadEvent;

         public override fun on(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEvent>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun on(cb: EventConsumer<org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEvent>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun once(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEvent>): RegisteredListener {
            return this.$$delegate_0.once(cb)
         }

         public override fun once(
            predicate: Predicate<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEvent>,
            cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEvent>
         ): RegisteredListener {
            return this.$$delegate_0.once(predicate, cb)
         }
      }
   }

   /** @deprecated */
   @Deprecated(message = "Use the interface variant", replaceWith = @ReplaceWith(expression = "ShipLoadEventClient", imports = ["org.valkyrienskies.mod.api.events.ShipLoadEventClient"]))
   public data class ShipLoadEventClient(ship: ClientShip) : org.valkyrienskies.core.api.events.ShipLoadEventClient {
      public open val ship: ClientShip

      init {
         this.ship = ship
      }

      public operator fun component1(): ClientShip {
         return this.ship
      }

      public fun copy(ship: ClientShip = this.ship): org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEventClient {
         return VSEvents.ShipLoadEventClient(ship)
      }

      public override fun toString(): String {
         return "ShipLoadEventClient(ship=${this.ship})"
      }

      public override fun hashCode(): Int {
         return this.ship.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is VSEvents.ShipLoadEventClient && this.ship == (other as VSEvents.ShipLoadEventClient).ship
         }
      }

      /** @deprecated */
      @Deprecated(message = "Use vsApi.shipLoadEventClient", replaceWith = @ReplaceWith(expression = "vsApi.shipLoadEventClient", imports = ["org.valkyrienskies.mod.api.vsApi"]))
      public companion object : ListenableEvent<VSEvents.ShipLoadEventClient> {
         @JvmSynthetic
         private SingleEvent<VSEvents.ShipLoadEventClient> $$delegate_0 = VSEvents.INSTANCE.shipLoadEventClient;

         public override fun on(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEventClient>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun on(cb: EventConsumer<org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEventClient>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun once(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEventClient>): RegisteredListener {
            return this.$$delegate_0.once(cb)
         }

         public override fun once(
            predicate: Predicate<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEventClient>,
            cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEventClient>
         ): RegisteredListener {
            return this.$$delegate_0.once(predicate, cb)
         }
      }
   }

   /** @deprecated */
   @Deprecated(message = "Use the interface variant", replaceWith = @ReplaceWith(expression = "ShipUnloadEventClient", imports = ["org.valkyrienskies.mod.api.events.ShipUnloadEventClient"]))
   public data class ShipUnloadEventClient(ship: ClientShip) : org.valkyrienskies.core.api.events.ShipUnloadEventClient {
      public open val ship: ClientShip

      init {
         this.ship = ship
      }

      public operator fun component1(): ClientShip {
         return this.ship
      }

      public fun copy(ship: ClientShip = this.ship): org.valkyrienskies.core.impl.hooks.VSEvents.ShipUnloadEventClient {
         return VSEvents.ShipUnloadEventClient(ship)
      }

      public override fun toString(): String {
         return "ShipUnloadEventClient(ship=${this.ship})"
      }

      public override fun hashCode(): Int {
         return this.ship.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is VSEvents.ShipUnloadEventClient && this.ship == (other as VSEvents.ShipUnloadEventClient).ship
         }
      }

      /** @deprecated */
      @Deprecated(message = "Use vsApi.shipUnloadEvent", replaceWith = @ReplaceWith(expression = "vsApi.shipUnloadEvent", imports = ["org.valkyrienskies.mod.api.vsApi"]))
      public companion object : ListenableEvent<VSEvents.ShipUnloadEventClient> {
         @JvmSynthetic
         private SingleEvent<VSEvents.ShipUnloadEventClient> $$delegate_0 = VSEvents.INSTANCE.shipUnloadEventClient;

         public override fun on(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipUnloadEventClient>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun on(cb: EventConsumer<org.valkyrienskies.core.impl.hooks.VSEvents.ShipUnloadEventClient>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun once(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipUnloadEventClient>): RegisteredListener {
            return this.$$delegate_0.once(cb)
         }

         public override fun once(
            predicate: Predicate<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipUnloadEventClient>,
            cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.ShipUnloadEventClient>
         ): RegisteredListener {
            return this.$$delegate_0.once(predicate, cb)
         }
      }
   }

   /** @deprecated */
   @Deprecated(message = "Use the interface variant", replaceWith = @ReplaceWith(expression = "StartUpdateRenderTransformsEvent", imports = ["org.valkyrienskies.mod.api.events.StartUpdateRenderTransformsEvent"]))
   public data class StartUpdateRenderTransformsEvent(shipWorld: ClientShipWorld) : org.valkyrienskies.core.api.events.StartUpdateRenderTransformsEvent {
      public open val shipWorld: ClientShipWorld

      init {
         this.shipWorld = shipWorld
      }

      public operator fun component1(): ClientShipWorld {
         return this.shipWorld
      }

      public fun copy(shipWorld: ClientShipWorld = this.shipWorld): org.valkyrienskies.core.impl.hooks.VSEvents.StartUpdateRenderTransformsEvent {
         return VSEvents.StartUpdateRenderTransformsEvent(shipWorld)
      }

      public override fun toString(): String {
         return "StartUpdateRenderTransformsEvent(shipWorld=${this.shipWorld})"
      }

      public override fun hashCode(): Int {
         return this.shipWorld.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is VSEvents.StartUpdateRenderTransformsEvent && this.shipWorld == (other as VSEvents.StartUpdateRenderTransformsEvent).shipWorld
         }
      }

      /** @deprecated */
      @Deprecated(message = "Use vsApi.startUpdateRenderTransformsEvent", replaceWith = @ReplaceWith(expression = "vsApi.startUpdateRenderTransformsEvent", imports = ["org.valkyrienskies.mod.api.vsApi"]))
      public companion object : ListenableEvent<VSEvents.StartUpdateRenderTransformsEvent> {
         @JvmSynthetic
         private SingleEvent<VSEvents.StartUpdateRenderTransformsEvent> $$delegate_0 = VSEvents.INSTANCE.startUpdateRenderTransformsEvent;

         public override fun on(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.StartUpdateRenderTransformsEvent>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun on(cb: EventConsumer<org.valkyrienskies.core.impl.hooks.VSEvents.StartUpdateRenderTransformsEvent>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun once(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.StartUpdateRenderTransformsEvent>): RegisteredListener {
            return this.$$delegate_0.once(cb)
         }

         public override fun once(
            predicate: Predicate<in org.valkyrienskies.core.impl.hooks.VSEvents.StartUpdateRenderTransformsEvent>,
            cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.StartUpdateRenderTransformsEvent>
         ): RegisteredListener {
            return this.$$delegate_0.once(predicate, cb)
         }
      }
   }

   /** @deprecated */
   @Deprecated(message = "Use the interface variant", replaceWith = @ReplaceWith(expression = "TickEndEvent", imports = ["org.valkyrienskies.mod.api.events.TickEndEvent"]))
   public data class TickEndEvent(world: ServerShipWorld) : org.valkyrienskies.core.api.events.TickEndEvent {
      public open val world: ServerShipWorld

      init {
         this.world = world
      }

      public operator fun component1(): ServerShipWorld {
         return this.world
      }

      public fun copy(world: ServerShipWorld = this.world): org.valkyrienskies.core.impl.hooks.VSEvents.TickEndEvent {
         return VSEvents.TickEndEvent(world)
      }

      public override fun toString(): String {
         return "TickEndEvent(world=${this.world})"
      }

      public override fun hashCode(): Int {
         return this.world.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is VSEvents.TickEndEvent && this.world == (other as VSEvents.TickEndEvent).world
         }
      }

      /** @deprecated */
      @Deprecated(message = "Use vsApi.tickEndEvent", replaceWith = @ReplaceWith(expression = "vsApi.tickEndEvent", imports = ["org.valkyrienskies.mod.api.vsApi"]))
      public companion object : ListenableEvent<VSEvents.TickEndEvent> {
         @JvmSynthetic
         private SingleEvent<VSEvents.TickEndEvent> $$delegate_0 = VSEvents.INSTANCE.tickEndEvent;

         public override fun on(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.TickEndEvent>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun on(cb: EventConsumer<org.valkyrienskies.core.impl.hooks.VSEvents.TickEndEvent>): RegisteredListener {
            return this.$$delegate_0.on(cb)
         }

         public override fun once(cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.TickEndEvent>): RegisteredListener {
            return this.$$delegate_0.once(cb)
         }

         public override fun once(
            predicate: Predicate<in org.valkyrienskies.core.impl.hooks.VSEvents.TickEndEvent>,
            cb: Consumer<in org.valkyrienskies.core.impl.hooks.VSEvents.TickEndEvent>
         ): RegisteredListener {
            return this.$$delegate_0.once(predicate, cb)
         }
      }
   }
}
