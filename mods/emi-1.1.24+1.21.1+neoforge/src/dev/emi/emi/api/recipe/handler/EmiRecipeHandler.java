package dev.emi.emi.api.recipe.handler;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.Widget;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface EmiRecipeHandler<T extends AbstractContainerMenu> {
   Component NOT_ENOUGH_INGREDIENTS = EmiPort.translatable("emi.not_enough_ingredients");

   EmiPlayerInventory getInventory(AbstractContainerScreen<T> var1);

   boolean supportsRecipe(EmiRecipe var1);

   default boolean alwaysDisplaySupport(EmiRecipe recipe) {
      return true;
   }

   boolean canCraft(EmiRecipe var1, EmiCraftContext<T> var2);

   boolean craft(EmiRecipe var1, EmiCraftContext<T> var2);

   default List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<T> context) {
      return this.canCraft(recipe, context) ? List.of() : List.of(ClientTooltipComponent.create(EmiPort.ordered(NOT_ENOUGH_INGREDIENTS)));
   }

   default void render(EmiRecipe recipe, EmiCraftContext<T> context, List<Widget> widgets, GuiGraphics draw) {
   }
}
