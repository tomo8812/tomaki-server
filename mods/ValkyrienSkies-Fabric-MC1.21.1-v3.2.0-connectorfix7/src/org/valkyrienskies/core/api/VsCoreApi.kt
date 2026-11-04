package org.valkyrienskies.core.api

import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.attachment.AttachmentRegistration
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder
import org.valkyrienskies.core.api.bodies.properties.BodyTransform.Factory
import org.valkyrienskies.core.api.event.ListenableEvent
import org.valkyrienskies.core.api.events.CollisionEvent
import org.valkyrienskies.core.api.events.JointDeserializedEvent
import org.valkyrienskies.core.api.events.JointSerializedEvent
import org.valkyrienskies.core.api.events.MergeEvent
import org.valkyrienskies.core.api.events.PhysTickEvent
import org.valkyrienskies.core.api.events.ShipLoadEvent
import org.valkyrienskies.core.api.events.ShipLoadEventClient
import org.valkyrienskies.core.api.events.ShipUnloadEventClient
import org.valkyrienskies.core.api.events.SplitEvent
import org.valkyrienskies.core.api.events.StartUpdateRenderTransformsEvent
import org.valkyrienskies.core.api.events.TickEndEvent

public interface VsCoreApi {
   @VsBeta
   public val transformFactory: Factory

   @VsBeta
   public val shipLoadEvent: ListenableEvent<ShipLoadEvent>

   @VsBeta
   public val shipLoadEventClient: ListenableEvent<ShipLoadEventClient>

   @VsBeta
   public val shipUnloadEventClient: ListenableEvent<ShipUnloadEventClient>

   @VsBeta
   public val startUpdateRenderTransformsEvent: ListenableEvent<StartUpdateRenderTransformsEvent>

   @VsBeta
   public val tickEndEvent: ListenableEvent<TickEndEvent>

   @VsBeta
   public val collisionStartEvent: ListenableEvent<CollisionEvent>

   @VsBeta
   public val collisionEndEvent: ListenableEvent<CollisionEvent>

   @VsBeta
   public val collisionPersistEvent: ListenableEvent<CollisionEvent>

   @VsBeta
   public val mergeEvent: ListenableEvent<MergeEvent>

   @VsBeta
   public val splitEvent: ListenableEvent<SplitEvent>

   @VsBeta
   public val physTickEvent: ListenableEvent<PhysTickEvent>

   @VsBeta
   public val jointSerializedEvent: ListenableEvent<JointSerializedEvent>

   @VsBeta
   public val jointDeserializedEvent: ListenableEvent<JointDeserializedEvent>

   @VsBeta
   public abstract fun newBodyKinematics(velocity: Vector3dc, angularVelocity: Vector3dc, transform: BodyTransform): BodyKinematics {
   }

   @VsBeta
   public abstract fun newBodyKinematics(
      velocity: Vector3dc,
      angularVelocity: Vector3dc,
      position: Vector3dc,
      rotation: Quaterniondc,
      scaling: Vector3dc,
      positionInModel: Vector3dc
   ): BodyKinematics {
   }

   @VsBeta
   public abstract fun newBodyKinematicsBuilder(): Builder {
   }

   public abstract fun newBodyTransform(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc, positionInModel: Vector3dc): BodyTransform {
   }

   @VsBeta
   public abstract fun newBodyTransformBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
   }

   @VsBeta
   public abstract fun <T> newAttachmentRegistrationBuilder(attachmentClass: Class<Any>): org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<
         Any
      > {
   }

   @VsBeta
   public abstract fun <T> registerAttachment(registration: AttachmentRegistration<Any>) {
   }

   @VsBeta
   public open fun <T> registerAttachment(attachmentClass: Class<Any>) {
      this.registerAttachment(attachmentClass, {
      })
   }

   @VsBeta
   public abstract fun <T> registerAttachment(
      attachmentClass: Class<Any>,
      registrationBuilder: (org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<Any>) -> Unit
   ) {
   }

   @VsBeta
   public abstract fun registerAttachmentForRemoval(attachmentKey: String) {
   }
}
