package dev.emi.emi.handler;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class InventoryRecipeHandler implements StandardRecipeHandler<InventoryMenu> {
   public static final Component TOO_SMALL = EmiPort.translatable("emi.too_small");

   public List<Slot> getInputSources(InventoryMenu handler) {
      List<Slot> list = Lists.newArrayList();

      for (int i = 1; i < 5; i++) {
         list.add(handler.getSlot(i));
      }

      int invStart = 9;

      for (int i = invStart; i < invStart + 36; i++) {
         list.add(handler.getSlot(i));
      }

      return list;
   }

   public List<Slot> getCraftingSlots(InventoryMenu handler) {
      List<Slot> list = Lists.newArrayList();
      list.add(handler.getSlot(1));
      list.add(handler.getSlot(2));
      list.add(null);
      list.add(handler.getSlot(3));
      list.add(handler.getSlot(4));
      list.add(null);
      list.add(null);
      list.add(null);
      list.add(null);
      return list;
   }

   public List<Slot> getCraftingSlots(EmiRecipe recipe, InventoryMenu handler) {
      if (recipe instanceof EmiCraftingRecipe craf && craf.shapeless) {
         List<Slot> list = Lists.newArrayList();
         list.add(handler.getSlot(1));
         list.add(handler.getSlot(2));
         list.add(handler.getSlot(3));
         list.add(handler.getSlot(4));
         return list;
      } else {
         return this.getCraftingSlots(handler);
      }
   }

   @Nullable
   public Slot getOutputSlot(InventoryMenu handler) {
      return (Slot)handler.slots.get(0);
   }

   @Override
   public boolean supportsRecipe(EmiRecipe recipe) {
      if (recipe.getCategory() != VanillaEmiRecipeCategories.CRAFTING || !recipe.supportsRecipeTree()) {
         return false;
      } else {
         return recipe instanceof EmiCraftingRecipe crafting ? crafting.canFit(2, 2) : true;
      }
   }

   @Override
   public boolean canCraft(EmiRecipe recipe, EmiCraftContext<InventoryMenu> context) {
      return context.getScreenHandler() instanceof RecipeBookMenu<?, ?> arsh && recipe instanceof EmiCraftingRecipe crafting
         ? crafting.canFit(arsh.getGridWidth(), arsh.getGridHeight()) && StandardRecipeHandler.super.canCraft(recipe, context)
         : false;
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<InventoryMenu> context) {
      return !this.canCraft(recipe, context)
            && context.getScreenHandler() instanceof RecipeBookMenu<?, ?> arsh
            && recipe instanceof EmiCraftingRecipe crafting
            && !crafting.canFit(arsh.getGridWidth(), arsh.getGridHeight())
         ? List.of(ClientTooltipComponent.create(EmiPort.ordered(TOO_SMALL)))
         : StandardRecipeHandler.super.getTooltip(recipe, context);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Slot getOutputSlot(AbstractContainerMenu param1) {
      return this.getOutputSlot((InventoryMenu)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public List getCraftingSlots(EmiRecipe param1, AbstractContainerMenu nullx) {
      return this.getCraftingSlots(var1, (InventoryMenu)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public List getCraftingSlots(AbstractContainerMenu param1) {
      return this.getCraftingSlots((InventoryMenu)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public List getInputSources(AbstractContainerMenu param1) {
      return this.getInputSources((InventoryMenu)var1);
   }
}
