package org.valkyrienskies.core.api.events

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.PhysTickOnly

@VsBeta
@PhysTickOnly
public interface JointDeserializedEvent {
   public val jointId: Int

   public val shipId0: Long?

   public val shipId1: Long?

   public val jointType: String

   public val dimensionId: String?

   public val outcome: org.valkyrienskies.core.api.events.JointDeserializedEvent.Outcome

   public val reason: String?

   public enum class Outcome {
      RESTORED,
      DISCARDED;

      @JvmStatic
      @JvmSynthetic
      fun `$values`(): Array<JointDeserializedEvent.Outcome> {
         arrayOf(RESTORED, DISCARDED)
      }
   }
}
