package net.caffeinemc.mods.lithium.common.entity.pushable;

import net.caffeinemc.mods.lithium.common.entity.EntityClassGroup;
import net.caffeinemc.mods.lithium.common.reflection.ReflectionUtil;
import net.caffeinemc.mods.lithium.common.services.PlatformMappingInformation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

public class PushableEntityClassGroup {
   public static final EntityClassGroup CACHABLE_UNPUSHABILITY;
   public static final EntityClassGroup MAYBE_PUSHABLE;

   static {
      String remapped_isClimbing = PlatformMappingInformation.INSTANCE
         .mapMethodName("intermediary", "net.minecraft.class_1309", "method_6101", "()Z", "onClimbable");
      String remapped_isPushable = PlatformMappingInformation.INSTANCE
         .mapMethodName("intermediary", "net.minecraft.class_1297", "method_5810", "()Z", "isPushable");
      CACHABLE_UNPUSHABILITY = new EntityClassGroup(
         (entityClass, entityType) -> LivingEntity.class.isAssignableFrom(entityClass)
            && !Player.class.isAssignableFrom(entityClass)
            && !ReflectionUtil.hasMethodOverride(entityClass, LivingEntity.class, true, remapped_isPushable)
            && !ReflectionUtil.hasMethodOverride(entityClass, LivingEntity.class, true, remapped_isClimbing)
      );
      MAYBE_PUSHABLE = new EntityClassGroup((entityClass, entityType) -> {
         if (ReflectionUtil.hasMethodOverride(entityClass, Entity.class, true, remapped_isPushable)) {
            if (EnderDragon.class.isAssignableFrom(entityClass)) {
               return false;
            } else if (ArmorStand.class.isAssignableFrom(entityClass)) {
               return ReflectionUtil.hasMethodOverride(entityClass, ArmorStand.class, true, remapped_isPushable);
            } else {
               return Bat.class.isAssignableFrom(entityClass) ? ReflectionUtil.hasMethodOverride(entityClass, Bat.class, true, remapped_isPushable) : true;
            }
         } else {
            return Player.class.isAssignableFrom(entityClass);
         }
      });
   }
}
