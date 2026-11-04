package dev.emi.emi.api.recipe.handler;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.platform.EmiClient;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface StandardRecipeHandler<T extends AbstractContainerMenu> extends EmiRecipeHandler<T> {
   List<Slot> getInputSources(T var1);

   List<Slot> getCraftingSlots(T var1);

   default List<Slot> getCraftingSlots(EmiRecipe recipe, T handler) {
      return this.getCraftingSlots(handler);
   }

   @Nullable
   default Slot getOutputSlot(T handler) {
      return null;
   }

   @Override
   default EmiPlayerInventory getInventory(AbstractContainerScreen<T> screen) {
      return new EmiPlayerInventory(this.getInputSources((T)screen.getMenu()).stream().<ItemStack>map(Slot::getItem).map(EmiStack::of).toList());
   }

   @Override
   default boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
      return context.getInventory().canCraft(recipe);
   }

   @Override
   default boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
      List<ItemStack> stacks = EmiRecipeFiller.getStacks(this, recipe, context.getScreen(), context.getAmount());
      if (stacks != null && stacks != null) {
         Minecraft.getInstance().setScreen(context.getScreen());
         if (!EmiClient.onServer) {
            return EmiRecipeFiller.clientFill(this, recipe, context.getScreen(), stacks, context.getDestination());
         }

         EmiClient.sendFillRecipe(this, context.getScreen(), context.getScreenHandler().containerId, switch (context.getDestination()) {
            case NONE -> 0;
            case CURSOR -> 1;
            case INVENTORY -> 2;
         }, stacks, recipe);
         return true;
      } else {
         return false;
      }
   }

   @Override
   default void render(EmiRecipe recipe, EmiCraftContext<T> context, List<Widget> widgets, GuiGraphics draw) {
      renderMissing(recipe, context.getInventory(), widgets, draw);
   }

   @Internal
   static void renderMissing(EmiRecipe recipe, EmiPlayerInventory inv, List<Widget> widgets, GuiGraphics draw) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      context.enableDepthTest();
      Map<EmiIngredient, Boolean> availableForCrafting = getAvailable(recipe, inv);

      for (Widget w : widgets) {
         if (w instanceof SlotWidget sw) {
            EmiIngredient stack = sw.getStack();
            Bounds bounds = sw.getBounds();
            if (sw.getRecipe() == null && availableForCrafting.containsKey(stack) && !stack.isEmpty() && !availableForCrafting.get(stack)) {
               context.fill(bounds.x(), bounds.y(), bounds.width(), bounds.height(), 1157562368);
            }
         }
      }
   }

   private static Map<EmiIngredient, Boolean> getAvailable(EmiRecipe recipe, EmiPlayerInventory inventory) {
      Map<EmiIngredient, Boolean> availableForCrafting = new IdentityHashMap<>();
      List<Boolean> list = inventory.getCraftAvailability(recipe);
      List<EmiIngredient> inputs = recipe.getInputs();
      if (list.size() != inputs.size()) {
         return Map.of();
      }

      for (int i = 0; i < list.size(); i++) {
         availableForCrafting.put(inputs.get(i), list.get(i));
      }

      return availableForCrafting;
   }
}
