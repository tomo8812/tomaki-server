package net.satisfy.candlelight.core.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.foundation.armor.Wearing;

public final class Wearables {
   private Wearables() {
   }

   public static boolean isWearing(LivingEntity entity, Item item) {
      return Wearing.isWearing(entity, item);
   }

   public static boolean hasChefSet(LivingEntity entity) {
      return PlatformHelper.enableChefSetBonus()
         && isWearing(entity, (Item)ObjectRegistry.COOKING_HAT.get())
         && isWearing(entity, (Item)ObjectRegistry.CHEFS_JACKET.get())
         && isWearing(entity, (Item)ObjectRegistry.CHEFS_PANTS.get())
         && isWearing(entity, (Item)ObjectRegistry.CHEFS_BOOTS.get());
   }

   public static boolean isElegantlyDressed(LivingEntity entity) {
      if (!PlatformHelper.enableElegantSetBonus()) {
         return false;
      }

      boolean suit = (isWearing(entity, (Item)ObjectRegistry.SHIRT.get()) || isWearing(entity, (Item)ObjectRegistry.FORMAL_SHIRT.get()))
         && isWearing(entity, (Item)ObjectRegistry.TROUSERS_AND_VEST.get())
         && isWearing(entity, (Item)ObjectRegistry.NECKTIE.get());
      boolean dress = isWearing(entity, (Item)ObjectRegistry.DRESS.get()) && isWearing(entity, (Item)ObjectRegistry.FLOWER_CROWN.get());
      return suit || dress;
   }
}
