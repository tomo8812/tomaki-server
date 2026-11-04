package dev.emi.emi.api.recipe.handler;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus.Internal;

public class EmiCraftContext<T extends AbstractContainerMenu> {
   private final AbstractContainerScreen<T> screen;
   private final EmiPlayerInventory inventory;
   private final EmiCraftContext.Type type;
   private final EmiCraftContext.Destination destination;
   private final int amount;

   @Internal
   public EmiCraftContext(
      AbstractContainerScreen<T> screen, EmiPlayerInventory inventory, EmiCraftContext.Type type, EmiCraftContext.Destination destination, int amount
   ) {
      this.screen = screen;
      this.inventory = inventory;
      this.type = type;
      this.destination = destination;
      this.amount = amount;
   }

   @Internal
   public EmiCraftContext(AbstractContainerScreen<T> screen, EmiPlayerInventory inventory, EmiCraftContext.Type type) {
      this(screen, inventory, type, EmiCraftContext.Destination.NONE, 1);
   }

   public AbstractContainerScreen<T> getScreen() {
      return this.screen;
   }

   public T getScreenHandler() {
      return (T)this.screen.getMenu();
   }

   public EmiPlayerInventory getInventory() {
      return this.inventory;
   }

   public EmiCraftContext.Type getType() {
      return this.type;
   }

   public EmiCraftContext.Destination getDestination() {
      return this.destination;
   }

   public int getAmount() {
      return this.amount;
   }

   public enum Destination {
      NONE,
      CURSOR,
      INVENTORY;

      // $VF: synthetic method
      private static EmiCraftContext.Destination[] $values() {
         return new EmiCraftContext.Destination[]{NONE, CURSOR, INVENTORY};
      }
   }

   public enum Type {
      FILL_BUTTON,
      CRAFTABLE;

      // $VF: synthetic method
      private static EmiCraftContext.Type[] $values() {
         return new EmiCraftContext.Type[]{FILL_BUTTON, CRAFTABLE};
      }
   }
}
