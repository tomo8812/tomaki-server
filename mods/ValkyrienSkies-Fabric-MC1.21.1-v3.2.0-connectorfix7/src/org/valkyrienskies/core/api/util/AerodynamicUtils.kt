package org.valkyrienskies.core.api.util

import org.valkyrienskies.core.api.VsBeta

@VsBeta
public interface AerodynamicUtils {
   @JvmStatic
   AerodynamicUtils.Companion Companion = AerodynamicUtils.Companion.$$INSTANCE;

   public abstract fun registerDimension(dimensionId: String, maxY: Double, seaLevel: Double, gravity: Double) {
   }

   public abstract fun updateGravityForDimension(dimensionId: String, gravity: Double) {
   }

   public abstract fun updateAerodynamicInfoForDimension(dimensionId: String, maxY: Double?, seaLevel: Double?) {
   }

   public abstract fun getAtmosphereForDimension(id: String): Triple<Double, Double, Double> {
   }

   public abstract fun getAirDensityForY(y: Double, dimension: String): Double {
   }

   public abstract fun getAirPressureForY(y: Double, dimension: String): Double {
   }

   public abstract fun getAirTemperatureForY(y: Double, dimension: String): Double {
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      AerodynamicUtils.Companion $$INSTANCE = AerodynamicUtils.Companion();
      public const val DEFAULT_MAX: Double = 962.0
      public const val DEFAULT_SEA_LEVEL: Double = 62.0
      public const val GRAVITATIONAL_ACCELERATION: Double = 10.0
      public const val UNIVERSAL_GAS_CONSTANT: Double = 8.314
      public const val AIR_MOLAR_MASS: Double = 0.0289644
   }
}
