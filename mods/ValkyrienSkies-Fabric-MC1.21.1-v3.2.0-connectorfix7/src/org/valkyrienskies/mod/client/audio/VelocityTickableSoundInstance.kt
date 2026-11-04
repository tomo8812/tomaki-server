package org.valkyrienskies.mod.client.audio

import net.minecraft.class_1117
import org.joml.Vector3dc

public interface VelocityTickableSoundInstance : class_1117 {
   public val velocity: Vector3dc

   public open fun updateVelocity() {
   }
}
