package net.satisfy.bakery.core.item;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.satisfy.bakery.client.util.ClientPlayerName;

public class BakedSweetDoughItem extends Item {
   private static final int ICING_PINK = 14919328;

   public BakedSweetDoughItem(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      if (Platform.getEnvironment() == Env.CLIENT) {
         tooltip.add(
            Component.translatable("tooltip.bakery.baked_sweet_dough", new Object[]{ClientPlayerName.get()})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(14919328)).withItalic(true))
         );
      }
   }
}
