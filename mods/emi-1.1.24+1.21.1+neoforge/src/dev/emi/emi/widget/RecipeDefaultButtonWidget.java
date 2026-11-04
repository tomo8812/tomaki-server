package dev.emi.emi.widget;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.HelpLevel;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.tooltip.IngredientTooltipComponent;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class RecipeDefaultButtonWidget extends RecipeButtonWidget {
   public RecipeDefaultButtonWidget(int x, int y, EmiRecipe recipe) {
      super(x, y, 48, 0, recipe);
   }

   @Override
   public int getTextureOffset(int mouseX, int mouseY) {
      int v = super.getTextureOffset(mouseX, mouseY);

      return v + switch (BoM.getRecipeStatus(this.recipe)) {
         case EMPTY -> 0;
         case PARTIAL -> 60;
         case FULL -> 36;
      };
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      switch (BoM.getRecipeStatus(this.recipe)) {
         case PARTIAL:
            List<EmiStack> stacks = Lists.newArrayList();

            for (EmiStack stack : this.recipe.getOutputs()) {
               if (BoM.getRecipe(stack) == this.recipe) {
                  stacks.add(stack);
               }
            }

            if (stacks.size() > 0) {
               list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("tooltip.emi.set_default"))));
               list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("tooltip.emi.current_defaults"))));
               list.add(new IngredientTooltipComponent(stacks));
               break;
            }
         case EMPTY:
            list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("tooltip.emi.set_default"))));
            break;
         case FULL:
            list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("tooltip.emi.unset_default"))));
      }

      if (this.recipe.getOutputs().size() > 1 && EmiConfig.helpLevel.has(HelpLevel.NORMAL) && EmiConfig.defaultStack.isBound()) {
         list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("tooltip.emi.set_default_stack", EmiConfig.defaultStack.getBindText()))));
      }

      return list;
   }

   @Override
   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      if (BoM.getRecipeStatus(this.recipe) == BoM.DefaultStatus.FULL) {
         BoM.removeRecipe(this.recipe);
      } else {
         BoM.addRecipe(this.recipe);
      }

      this.playButtonSound();
      if (RecipeScreen.resolve != null) {
         EmiHistory.pop();
      }

      return true;
   }
}
