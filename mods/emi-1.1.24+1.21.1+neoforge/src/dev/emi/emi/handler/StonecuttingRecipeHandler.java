package dev.emi.emi.handler;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class StonecuttingRecipeHandler implements StandardRecipeHandler<StonecutterMenu> {
   public List<Slot> getInputSources(StonecutterMenu handler) {
      List<Slot> list = Lists.newArrayList();
      list.add(handler.getSlot(0));
      int invStart = 2;

      for (int i = invStart; i < invStart + 36; i++) {
         list.add(handler.getSlot(i));
      }

      return list;
   }

   public List<Slot> getCraftingSlots(StonecutterMenu handler) {
      return List.of((Slot)handler.slots.get(0));
   }

   @Override
   public boolean supportsRecipe(EmiRecipe recipe) {
      return recipe.getCategory() == VanillaEmiRecipeCategories.STONECUTTING;
   }

   @Nullable
   public Slot getOutputSlot(StonecutterMenu handler) {
      return handler.getSlot(1);
   }

   @Override
   public boolean craft(EmiRecipe recipe, EmiCraftContext<StonecutterMenu> context) {
      boolean action = StandardRecipeHandler.super.craft(recipe, context);
      Minecraft client = Minecraft.getInstance();
      Level world = client.level;
      SingleRecipeInput inv = new SingleRecipeInput(recipe.getInputs().get(0).getEmiStacks().get(0).getItemStack());
      List<StonecutterRecipe> recipes = world.getRecipeManager()
         .getRecipesFor(RecipeType.STONECUTTING, inv, world)
         .stream()
         .<StonecutterRecipe>map(RecipeHolder::value)
         .toList();

      for (int i = 0; i < recipes.size(); i++) {
         if (EmiPort.getId((Recipe<?>)recipes.get(i)) != null && EmiPort.getId((Recipe<?>)recipes.get(i)).equals(recipe.getId())) {
            StonecutterMenu sh = (StonecutterMenu)context.getScreenHandler();
            client.gameMode.handleInventoryButtonClick(sh.containerId, i);
            if (context.getDestination() == EmiCraftContext.Destination.CURSOR) {
               client.gameMode.handleInventoryMouseClick(sh.containerId, 1, 0, ClickType.PICKUP, client.player);
            } else if (context.getDestination() == EmiCraftContext.Destination.INVENTORY) {
               client.gameMode.handleInventoryMouseClick(sh.containerId, 1, 0, ClickType.QUICK_MOVE, client.player);
            }
            break;
         }
      }

      return action;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Slot getOutputSlot(AbstractContainerMenu param1) {
      return this.getOutputSlot((StonecutterMenu)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public List getCraftingSlots(AbstractContainerMenu param1) {
      return this.getCraftingSlots((StonecutterMenu)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public List getInputSources(AbstractContainerMenu param1) {
      return this.getInputSources((StonecutterMenu)var1);
   }
}
