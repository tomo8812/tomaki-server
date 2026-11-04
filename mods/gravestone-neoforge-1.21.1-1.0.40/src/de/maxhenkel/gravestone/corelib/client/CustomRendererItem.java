package de.maxhenkel.gravestone.corelib.client;

import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class CustomRendererItem extends Item {
   public CustomRendererItem(Properties properties) {
      super(properties);
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      super.initializeClient(consumer);
      final ItemRenderer renderer = this.createItemRenderer();
      if (renderer != null) {
         consumer.accept(new IClientItemExtensions() {
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
               return renderer.getRenderer();
            }
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ItemRenderer createItemRenderer() {
      return null;
   }
}
