package net.satisfy.farm_and_charm.core.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class PlowCartItem extends AbstractCartItem {
   public PlowCartItem(Properties properties) {
      super(properties);
   }

   @Override
   protected Entity createCartEntity(Level level) {
      return ((EntityType)EntityTypeRegistry.PLOW.get()).create(level);
   }

   @Override
   protected String getCartTitleKey() {
      return "tooltip.farm_and_charm.plow_cart.title";
   }

   @Override
   protected void appendShiftDetails(List<Component> tooltipComponents, int earthy) {
      tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.cart.attach_horse").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
      tooltipComponents.add(
         Component.translatable("tooltip.farm_and_charm.plow_cart.action_toggle").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy)))
      );
   }
}
