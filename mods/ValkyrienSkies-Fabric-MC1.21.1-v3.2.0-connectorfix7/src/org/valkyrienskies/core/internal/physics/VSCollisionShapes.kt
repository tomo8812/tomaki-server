package org.valkyrienskies.core.internal.physics

public enum class VSCollisionShapes {
   VOXEL,
   SPHERE,
   WHEEL,
   BOX,
   CAPSULE;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<VSCollisionShapes> {
      arrayOf(VOXEL, SPHERE, WHEEL, BOX, CAPSULE)
   }
}
