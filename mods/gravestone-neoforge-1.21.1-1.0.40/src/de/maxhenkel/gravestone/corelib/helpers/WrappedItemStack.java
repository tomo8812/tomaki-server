package de.maxhenkel.gravestone.corelib.helpers;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class WrappedItemStack extends AbstractStack<ItemStack> {
   public WrappedItemStack(ItemStack stack) {
      super(stack);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void render(GuiGraphics guiGraphics, int x, int y) {
      guiGraphics.renderItem(this.stack, x, y, 0);
      ItemStack var10002 = this.stack;
      guiGraphics.renderItemDecorations(Minecraft.getInstance().font, var10002, x, y);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public List<Component> getTooltip() {
      return Screen.getTooltipFromItem(Minecraft.getInstance(), this.stack);
   }

   @Override
   public Component getDisplayName() {
      return this.stack.getHoverName();
   }

   @Override
   public boolean isEmpty() {
      return this.stack.isEmpty();
   }
}
