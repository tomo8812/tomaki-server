package fuzs.puzzleslib.api.client.gui.v2.screens.inventory;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractWidgetsContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
   public AbstractWidgetsContainerScreen(T menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   public AbstractWidgetsContainerScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
      super(menu, inventory, title);
      this.imageWidth = imageWidth;
      this.imageHeight = imageHeight;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
         ? true
         : this.getChildAt(mouseX, mouseY).filter(listener -> listener.mouseScrolled(mouseX, mouseY, scrollX, scrollY)).isPresent();
   }
}
