package net.caffeinemc.mods.lithium.common.ai.brain;

import net.caffeinemc.mods.lithium.mixin.ai.useless_sensors.BrainAccessor;
import net.caffeinemc.mods.lithium.mixin.ai.useless_sensors.SensorAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class SensorHelper {
   public static void disableSensor(LivingEntity brainedEntity, SensorType<?> sensorType) {
      if (!brainedEntity.level().isClientSide()) {
         Brain<?> brain = brainedEntity.getBrain();
         Sensor<?> sensor = (Sensor<?>)((BrainAccessor)brain).getSensors().get(sensorType);
         if (sensor instanceof SensorAccessor sensorAccessor) {
            long lastSenseTime = sensorAccessor.getLastSenseTime();
            int senseInterval = sensorAccessor.getSenseInterval();
            long maxMultipleOfSenseInterval = Long.MAX_VALUE - Long.MAX_VALUE % senseInterval;
            maxMultipleOfSenseInterval -= senseInterval;
            maxMultipleOfSenseInterval += lastSenseTime;
            sensorAccessor.setLastSenseTime(maxMultipleOfSenseInterval);
         }
      }
   }

   public static <T extends LivingEntity, U extends Sensor<T>> void enableSensor(T brainedEntity, SensorType<U> sensorType) {
      enableSensor(brainedEntity, sensorType, false);
   }

   public static <T extends LivingEntity, U extends Sensor<T>> void enableSensor(T brainedEntity, SensorType<U> sensorType, boolean extraTick) {
      if (!brainedEntity.level().isClientSide()) {
         Brain<?> brain = brainedEntity.getBrain();
         U sensor = (U)((BrainAccessor)brain).getSensors().get(sensorType);
         if (sensor instanceof SensorAccessor sensorAccessor) {
            long lastSenseTime = sensorAccessor.getLastSenseTime();
            int senseInterval = sensorAccessor.getSenseInterval();
            if (lastSenseTime > senseInterval) {
               lastSenseTime %= senseInterval;
               if (extraTick) {
                  ((SensorAccessor)sensor).setLastSenseTime(0L);
                  sensor.tick((ServerLevel)brainedEntity.level(), brainedEntity);
               }
            }

            sensorAccessor.setLastSenseTime(lastSenseTime);
         }
      }
   }
}
