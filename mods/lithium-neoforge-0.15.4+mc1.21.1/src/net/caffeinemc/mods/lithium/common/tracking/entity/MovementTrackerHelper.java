package net.caffeinemc.mods.lithium.common.tracking.entity;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.List;
import net.caffeinemc.mods.lithium.api.inventory.LithiumInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.entity.EntityAccess;

public abstract class MovementTrackerHelper {
   public static final List<Class<?>> MOVEMENT_NOTIFYING_ENTITY_CLASSES;
   public static volatile Reference2IntOpenHashMap<Class<? extends EntityAccess>> CLASS_2_NOTIFY_MASK;
   public static final int NUM_MOVEMENT_NOTIFYING_CLASSES;

   public static int getNotificationMask(Entity entity) {
      int notificationMask = CLASS_2_NOTIFY_MASK.getInt(entity.getClass());
      if (notificationMask == -1) {
         notificationMask = calculateNotificationMask(entity);
      }

      return notificationMask;
   }

   private static int calculateNotificationMask(Entity entity) {
      int mask = 0;
      Class<? extends Entity> entityClass = (Class<? extends Entity>)entity.getClass();

      for (int i = 0; i < MOVEMENT_NOTIFYING_ENTITY_CLASSES.size(); i++) {
         Class<?> superclass = MOVEMENT_NOTIFYING_ENTITY_CLASSES.get(i);
         if (superclass.isAssignableFrom(entityClass)) {
            mask |= 1 << i;
         }
      }

      Reference2IntOpenHashMap<Class<? extends EntityAccess>> copy = CLASS_2_NOTIFY_MASK.clone();
      copy.put(entityClass, mask);
      CLASS_2_NOTIFY_MASK = copy;
      return mask;
   }

   static {
      if (LithiumInventory.class.isAssignableFrom(HopperBlockEntity.class)) {
         MOVEMENT_NOTIFYING_ENTITY_CLASSES = List.of(ItemEntity.class, Container.class);
      } else {
         MOVEMENT_NOTIFYING_ENTITY_CLASSES = List.of();
      }

      CLASS_2_NOTIFY_MASK = new Reference2IntOpenHashMap();
      CLASS_2_NOTIFY_MASK.defaultReturnValue(-1);
      NUM_MOVEMENT_NOTIFYING_CLASSES = MOVEMENT_NOTIFYING_ENTITY_CLASSES.size();
   }
}
