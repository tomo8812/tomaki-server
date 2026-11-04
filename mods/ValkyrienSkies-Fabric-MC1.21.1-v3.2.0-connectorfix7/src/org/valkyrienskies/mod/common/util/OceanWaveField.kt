package org.valkyrienskies.mod.common.util

import org.valkyrienskies.mod.common.config.VSGameConfig

public object OceanWaveField {
   private const val DRAG_MULT: Double = 0.048
   private const val XZ_SCALE: Double = 0.035
   private const val TIME_MULT: Double = 0.45
   private const val FREQUENCY: Double = 6.0
   private const val SPEED: Double = 2.0
   private const val WEIGHT: Double = 0.8
   private const val FREQUENCY_MULT: Double = 1.18
   private const val SPEED_MULT: Double = 1.07
   private const val ITER_INC: Double = 12.0
   private const val MAX_ITERATIONS: Int = 40
   private const val DRIVER_TIMEOUT_NANOS: Long = 1000000000L
   private final val DIR_X: DoubleArray = DoubleArray(40)
   private final val DIR_Z: DoubleArray = DoubleArray(40)
   private final val FREQ: DoubleArray = DoubleArray(40)
   private final val SPD: DoubleArray = DoubleArray(40)
   private final val WGT: DoubleArray = DoubleArray(40)
   private final val WAVE_SUM: DoubleArray = DoubleArray(41)
   private final var time: Double
   private final var params: org.valkyrienskies.mod.common.util.OceanWaveField.Params
   private final var driverDimension: String?
   private final var lastAdvanceNanos: Long

   public fun advanceTime(dimensionId: String, deltaSeconds: Double) {
      val now: Long = System.nanoTime()
      val driver: java.lang.String = driverDimension
      if (!(driverDimension == dimensionId)) {
         if (driver != null && now - lastAdvanceNanos < 1000000000L) {
            return
         }

         driverDimension = dimensionId
      }

      lastAdvanceNanos = now
      if (0.0 <= deltaSeconds && deltaSeconds <= 1.0) {
         time += deltaSeconds
      }

      params = this.snapshot(time)
   }

   private fun snapshot(time: Double): org.valkyrienskies.mod.common.util.OceanWaveField.Params {
      val cfg: VSGameConfig.Server.OCEANWAVES = VSGameConfig.SERVER.OceanWaves
      return OceanWaveField.Params(
         cfg.waveHeight,
         0.035 * cfg.horizontalScale,
         cfg.offsetX,
         cfg.offsetZ,
         RangesKt.coerceIn(cfg.iterations, 1, 40),
         (time + cfg.phaseOffset) * cfg.waveSpeed * 0.45
      )
   }

   public fun height(worldX: Double, worldZ: Double): Double {
      if (params.waveHeight <= 0.0) {
         return 0.0
      } else {
         var px: Double = (worldX - params.offsetX) * params.xzScale
         var pz: Double = (worldZ - params.offsetZ) * params.xzScale
         val modifiedTime: Double = params.modifiedTime
         val iterations: Int = params.iterations
         var heightSum: Double = 0.0

         repeat(iterations) { i ->
            val dirX: Double = DIR_X[i]
            val dirZ: Double = DIR_Z[i]
            val weight: Double = WGT[i]
            val x: Double = (dirX * px + dirZ * pz) * FREQ[i] + modifiedTime * SPD[i]
            val wave: Double = Math.exp(Math.sin((dirX * px + dirZ * pz) * FREQ[i] + modifiedTime * SPD[i]) - 1.0)
            val forceMag: Double = wave * Math.cos(x) * weight
            px -= forceMag * dirX * 0.048
            pz -= forceMag * dirZ * 0.048
            heightSum += wave * weight
         }

         return heightSum / WAVE_SUM[iterations] * params.waveHeight - params.waveHeight * 0.5
      }
   }

   @JvmStatic
   fun {
      var iter: Double = 0.0
      var frequency: Double = 6.0
      var speed: Double = 2.0
      var weight: Double = 1.0

      repeat(39) { i ->
         DIR_X[i] = Math.sin(iter)
         DIR_Z[i] = Math.cos(iter)
         FREQ[i] = frequency
         SPD[i] = speed
         WGT[i] = weight
         WAVE_SUM[i + 1] = WAVE_SUM[i] + weight
         iter += 12.0
         weight *= 0.8
         frequency *= 1.18
         speed *= 1.07
      }

      params = INSTANCE.snapshot(0.0)
   }

   private class Params(waveHeight: Double, xzScale: Double, offsetX: Double, offsetZ: Double, iterations: Int, modifiedTime: Double) {
      @JvmField
      public final val waveHeight: Double

      @JvmField
      public final val xzScale: Double

      @JvmField
      public final val offsetX: Double

      @JvmField
      public final val offsetZ: Double

      @JvmField
      public final val iterations: Int

      @JvmField
      public final val modifiedTime: Double

      init {
         this.waveHeight = waveHeight
         this.xzScale = xzScale
         this.offsetX = offsetX
         this.offsetZ = offsetZ
         this.iterations = iterations
         this.modifiedTime = modifiedTime
      }
   }
}
