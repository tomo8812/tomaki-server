package org.valkyrienskies.core.impl.program

import org.valkyrienskies.core.api.event.SingleEvent
import org.valkyrienskies.core.api.events.CollisionEvent
import org.valkyrienskies.core.api.events.JointDeserializedEvent
import org.valkyrienskies.core.api.events.JointSerializedEvent
import org.valkyrienskies.core.api.events.MergeEvent
import org.valkyrienskies.core.api.events.PhysTickEvent
import org.valkyrienskies.core.api.events.SplitEvent
import org.valkyrienskies.core.impl.shadow.EX
import org.valkyrienskies.core.impl.shadow.Fl
import org.valkyrienskies.core.impl.shadow.Fm
import org.valkyrienskies.core.impl.shadow.FP.a
import org.valkyrienskies.core.internal.VsiCore

public interface VSCoreInternal : VsiCore {
   public val networking: Fl

   public val hooks: EX

   public val configurator: Fm

   public val pipelineComponentFactory: a

   public val collisionStartEvent: SingleEvent<CollisionEvent>

   public val collisionEndEvent: SingleEvent<CollisionEvent>

   public val collisionPersistEvent: SingleEvent<CollisionEvent>

   public val physTickEvent: SingleEvent<PhysTickEvent>

   public val jointSerializedEvent: SingleEvent<JointSerializedEvent>

   public val jointDeserializedEvent: SingleEvent<JointDeserializedEvent>

   public val mergeEvent: SingleEvent<MergeEvent>

   public val splitEvent: SingleEvent<SplitEvent>
}
