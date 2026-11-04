package org.valkyrienskies.core.api.events

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.physics.ContactPoint
import org.valkyrienskies.core.api.util.PhysTickOnly
import org.valkyrienskies.core.api.world.PhysLevel

@VsBeta
@PhysTickOnly
public interface CollisionEvent {
   public val dimensionId: String

   public val physLevel: PhysLevel

   public val shipIdA: Long

   public val shipIdB: Long

   public val contactPoints: Collection<ContactPoint>
}
