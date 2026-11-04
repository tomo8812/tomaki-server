package org.valkyrienskies.mod.client.audio

import net.minecraft.class_1109
import net.minecraft.class_2960
import net.minecraft.class_3414
import net.minecraft.class_3419
import net.minecraft.class_5819
import net.minecraft.class_1113.class_1114
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.Ship

public class SimpleSoundInstanceOnShip : class_1109, VelocityTickableSoundInstance {
   private final val ship: Ship
   private final val originalPos: Vector3d = Vector3d(this.field_5439, this.field_5450, this.field_5449)

   public open var velocity: Vector3dc = Vector3d() as Vector3dc
      private set

   public constructor(resourceLocation: class_2960,
      soundSource: class_3419,
      volume: Float,
      pitch: Float,
      random: class_5819,
      looping: Boolean,
      delay: Int,
      attenuation: class_1114,
      x: Double,
      y: Double,
      z: Double,
      relative: Boolean,
      ship: Ship
   ) : super(resourceLocation, soundSource, volume, pitch, random, looping, delay, attenuation, x, y, z, relative) {
      this.ship = ship
      val newPos: Vector3d = ship.shipToWorld.transformPosition(this.originalPos, Vector3d())
      this.field_5439 = newPos.x
      this.field_5450 = newPos.y
      this.field_5449 = newPos.z
   }

   public constructor(soundEvent: class_3414,
      soundSource: class_3419,
      volume: Float,
      pitch: Float,
      random: class_5819,
      looping: Boolean,
      delay: Int,
      attenuation: class_1114,
      x: Double,
      y: Double,
      z: Double,
      ship: Ship
   )  {
      val var10001: class_2960 = soundEvent.method_14833()
      this(var10001, soundSource, volume, pitch, random, looping, delay, attenuation, x, y, z, false, ship)
   }

   public constructor(soundEvent: class_3414,
      soundSource: class_3419,
      volume: Float,
      pitch: Float,
      random: class_5819,
      x: Double,
      y: Double,
      z: Double,
      ship: Ship
   ) : this(soundEvent, soundSource, volume, pitch, random, false, 0, class_1114.field_5476, x, y, z, ship)
   fun method_4793(): Boolean {
      false
   }

   fun method_16896() {
      val newPos: Vector3d = this.ship.shipToWorld.transformPosition(this.originalPos, Vector3d())
      this.velocity = newPos.sub(super.field_5439, super.field_5450, super.field_5449, Vector3d())
      super.field_5439 = newPos.x
      super.field_5450 = newPos.y
      super.field_5449 = newPos.z
   }
}
