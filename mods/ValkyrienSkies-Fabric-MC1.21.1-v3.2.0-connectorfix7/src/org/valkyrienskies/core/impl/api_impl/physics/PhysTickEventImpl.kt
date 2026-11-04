package org.valkyrienskies.core.impl.api_impl.physics

import org.valkyrienskies.core.api.events.PhysTickEvent
import org.valkyrienskies.core.api.world.PhysLevel

public class PhysTickEventImpl(world: PhysLevel, delta: Double) : PhysTickEvent {
   public open val world: PhysLevel
   public open val delta: Double

   init {
      this.world = world
      this.delta = delta
   }

   public override fun toString(): String {
      return "PhysTick(world=${this.world}, delta=${this.delta})"
   }
}
