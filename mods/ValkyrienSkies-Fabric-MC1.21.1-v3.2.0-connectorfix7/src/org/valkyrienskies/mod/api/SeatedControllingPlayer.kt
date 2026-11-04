package org.valkyrienskies.mod.api

import net.minecraft.class_2350

/** @deprecated */
@Deprecated(message = "This API will be removed at a later date. Addons should make their own seat entities and handle their own inputs. ")
public class SeatedControllingPlayer(seatInDirection: class_2350) {
   public final val seatInDirection: class_2350
   public final var forwardImpulse: Float
   public final var leftImpulse: Float
   public final var upImpulse: Float
   public final var sprintOn: Boolean
   public final var cruise: Boolean

   init {
      this.seatInDirection = seatInDirection
   }
}
