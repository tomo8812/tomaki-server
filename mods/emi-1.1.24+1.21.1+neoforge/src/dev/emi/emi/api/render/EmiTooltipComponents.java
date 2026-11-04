package dev.emi.emi.api.render;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.screen.tooltip.IngredientTooltipComponent;
import dev.emi.emi.screen.tooltip.RecipeCostTooltipComponent;
import dev.emi.emi.screen.tooltip.RecipeTooltipComponent;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class EmiTooltipComponents {
   public static ClientTooltipComponent getRecipeTooltipComponent(EmiRecipe recipe) {
      return new RecipeTooltipComponent(recipe);
   }

   public static ClientTooltipComponent getRemainderTooltipComponent(EmiIngredient ingredient) {
      return new RemainderTooltipComponent(ingredient);
   }

   public static ClientTooltipComponent getRecipeCostTooltipComponent(EmiRecipe recipe) {
      return new RecipeCostTooltipComponent(recipe);
   }

   public static ClientTooltipComponent getIngredientTooltipComponent(List<? extends EmiIngredient> stacks) {
      return new IngredientTooltipComponent(stacks);
   }

   public static ClientTooltipComponent getAmount(EmiIngredient ingredient) {
      return of(EmiRenderHelper.getAmountText(ingredient, ingredient.getAmount()).copy().withStyle(ChatFormatting.GRAY));
   }

   public static ClientTooltipComponent of(Component text) {
      return ClientTooltipComponent.create(text.getVisualOrderText());
   }

   public static void appendModName(List<ClientTooltipComponent> components, String namespace) {
      if (EmiConfig.appendModId) {
         String mod = EmiUtil.getModName(namespace);
         components.add(of(EmiPort.literal(mod, ChatFormatting.BLUE, ChatFormatting.ITALIC)));
      }
   }
}
