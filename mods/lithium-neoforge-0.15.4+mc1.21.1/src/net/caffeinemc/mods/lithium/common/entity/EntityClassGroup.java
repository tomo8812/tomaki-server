package net.caffeinemc.mods.lithium.common.entity;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceReferenceImmutablePair;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import net.caffeinemc.mods.lithium.common.reflection.ReflectionUtil;
import net.caffeinemc.mods.lithium.common.services.PlatformMappingInformation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.entity.vehicle.Minecart;
import org.jetbrains.annotations.Nullable;

public class EntityClassGroup {
   private static final byte ABSENT_VALUE = 3;
   public static final EntityClassGroup CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE;
   private final BiPredicate<Class<?>, Supplier<EntityType<?>>> classAndTypeFitEvaluator;
   private volatile Reference2ByteOpenHashMap<Class<?>> class2GroupContains;
   @Nullable
   private volatile ObjectOpenHashSet<ReferenceReferenceImmutablePair<Class<?>, EntityType<?>>> containedClassAndTypePairs;

   public EntityClassGroup(BiPredicate<Class<?>, Supplier<EntityType<?>>> classAndTypeFitEvaluator) {
      this.classAndTypeFitEvaluator = classAndTypeFitEvaluator;
      this.clear();
   }

   public void clear() {
      this.class2GroupContains = new Reference2ByteOpenHashMap();
      this.class2GroupContains.defaultReturnValue((byte)3);
      this.containedClassAndTypePairs = null;
   }

   public boolean contains(Entity entity) {
      return this.contains(entity.getClass(), entity.getType());
   }

   public boolean contains(Class<?> entityClass, EntityType<?> entityType) {
      byte contains = this.class2GroupContains.getByte(entityClass);
      return contains < 2 ? contains == 1 : this.checkDetailedContains(entityClass, entityType, contains);
   }

   private boolean checkDetailedContains(Class<?> entityClass, EntityType<?> entityType, byte contains) {
      if (contains == 3) {
         return this.testAndAddClass(entityClass, entityType);
      }

      ObjectOpenHashSet<ReferenceReferenceImmutablePair<Class<?>, EntityType<?>>> containedPairs = this.containedClassAndTypePairs;
      return containedPairs != null && containedPairs.contains(ReferenceReferenceImmutablePair.of(entityClass, entityType));
   }

   boolean testAndAddClass(Class<?> entityClass, EntityType<?> entityType) {
      synchronized (this) {
         if (this.class2GroupContains.containsKey(entityClass)) {
            return this.contains(entityClass, entityType);
         }

         Reference2ByteOpenHashMap<Class<?>> newMap = this.class2GroupContains.clone();
         boolean[] accessedEntityType = new boolean[1];
         Supplier<EntityType<?>> entityTypeSupplier = () -> {
            accessedEntityType[0] = true;
            return entityType;
         };
         boolean contains = this.classAndTypeFitEvaluator.test(entityClass, entityTypeSupplier);
         byte containsInfo = (byte)(contains ? 1 : 0);
         if (accessedEntityType[0]) {
            containsInfo = 2;
            ObjectOpenHashSet<ReferenceReferenceImmutablePair<Class<?>, EntityType<?>>> newPairSet = this.containedClassAndTypePairs;
            newPairSet = newPairSet == null ? new ObjectOpenHashSet() : newPairSet.clone();
            if (contains) {
               newPairSet.add(ReferenceReferenceImmutablePair.of(entityClass, entityType));
               this.containedClassAndTypePairs = newPairSet;
            }
         }

         byte previousContainsInfo = newMap.put(entityClass, containsInfo);
         if (previousContainsInfo != 3 && previousContainsInfo != containsInfo) {
            throw new IllegalStateException(
               "Entity class group class fit evaluator must be a pure function! Class fit for "
                  + entityClass
                  + " changed from "
                  + previousContainsInfo
                  + " to "
                  + containsInfo
                  + " when evaluating for "
                  + entityType
                  + "!"
            );
         }

         this.class2GroupContains = newMap;
         return contains;
      }
   }

   static {
      String remapped_collidesWith = PlatformMappingInformation.INSTANCE
         .mapMethodName("intermediary", "net.minecraft.class_1297", "method_30949", "(Lnet/minecraft/class_1297;)Z", "canCollideWith");
      CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE = new EntityClassGroup(
         (entityClass, entityType) -> ReflectionUtil.hasMethodOverride(entityClass, Entity.class, true, remapped_collidesWith, Entity.class)
      );
      if (!CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE.contains(Minecart.class, EntityType.MINECART)) {
         throw new AssertionError();
      }

      if (CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE.contains(WindCharge.class, EntityType.WIND_CHARGE)
         && CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE.contains(BreezeWindCharge.class, EntityType.BREEZE_WIND_CHARGE)) {
         if (CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE.contains(Shulker.class, EntityType.SHULKER)) {
            Logger.getLogger("Lithium EntityClassGroup")
               .warning("Either Lithium EntityClassGroup is broken or something else gave Shulkers the minecart-like collision behavior.");
         }

         CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE.clear();
      } else {
         throw new AssertionError();
      }
   }

   public static class NoDragonClassGroup extends EntityClassGroup {
      public static final EntityClassGroup.NoDragonClassGroup BOAT_SHULKER_LIKE_COLLISION;

      public NoDragonClassGroup(BiPredicate<Class<?>, Supplier<EntityType<?>>> classAndTypeFitEvaluator) {
         super(classAndTypeFitEvaluator);
         if (classAndTypeFitEvaluator.test(
            EnderDragon.class,
            () -> {
               throw new IllegalArgumentException(
                  "EntityClassGroup.NoDragonClassGroup cannot be initialized: Must exclude EnderDragonEntity without checking entity type!"
               );
            }
         )) {
            throw new IllegalArgumentException("EntityClassGroup.NoDragonClassGroup cannot be initialized: Must exclude EnderDragonEntity!");
         }
      }

      static {
         String remapped_canBeCollidedWith = PlatformMappingInformation.INSTANCE
            .mapMethodName("intermediary", "net.minecraft.class_1297", "method_30948", "()Z", "canBeCollidedWith");
         BOAT_SHULKER_LIKE_COLLISION = new EntityClassGroup.NoDragonClassGroup(
            (entityClass, entityType) -> ReflectionUtil.hasMethodOverride(entityClass, Entity.class, true, remapped_canBeCollidedWith)
         );
         if (!BOAT_SHULKER_LIKE_COLLISION.contains(Shulker.class, EntityType.SHULKER)) {
            throw new AssertionError();
         }

         BOAT_SHULKER_LIKE_COLLISION.clear();
      }
   }
}
