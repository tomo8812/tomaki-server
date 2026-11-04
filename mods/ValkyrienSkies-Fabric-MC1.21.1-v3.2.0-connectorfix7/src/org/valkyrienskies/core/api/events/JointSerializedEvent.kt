package org.valkyrienskies.core.api.events

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.PhysTickOnly

@VsBeta
@PhysTickOnly
public interface JointSerializedEvent {
   public val jointId: Int

   public val shipId0: Long?

   public val shipId1: Long?

   public val jointType: String

   public val dimensionId: String

   public val operation: org.valkyrienskies.core.api.events.JointSerializedEvent.Operation

   public enum class Operation {
      CREATED,
      UPDATED,
      RESTORED,
      REMOVED;

      @JvmStatic
      @JvmSynthetic
      fun `$values`(): Array<JointSerializedEvent.Operation> {
         arrayOf(CREATED, UPDATED, RESTORED, REMOVED)
      }
   }
}
