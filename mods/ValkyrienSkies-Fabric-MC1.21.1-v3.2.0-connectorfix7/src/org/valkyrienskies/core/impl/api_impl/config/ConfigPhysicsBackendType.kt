package org.valkyrienskies.core.impl.api_impl.config

public enum class ConfigPhysicsBackendType {
   KRUNCH_CLASSIC,
   KRUNCH_PHYSX,
   KRUNCH_JOLT;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<ConfigPhysicsBackendType> {
      arrayOf(KRUNCH_CLASSIC, KRUNCH_PHYSX, KRUNCH_JOLT)
   }
}
