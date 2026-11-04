package dev.emi.emi.screen;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeDecorator;
import dev.emi.emi.api.widget.RecipeFillButtonWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.registry.EmiRecipes;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.widget.RecipeDefaultButtonWidget;
import dev.emi.emi.widget.RecipeScreenshotButtonWidget;
import dev.emi.emi.widget.RecipeTreeButtonWidget;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class RecipeDisplay {
   public static final int DISPLAY_PADDING = 8;
   public final EmiRecipe recipe;
   private final int width;
   private final int height;
   private List<RecipeDisplay.ButtonType> rightButtons = Lists.newArrayList();
   private List<RecipeDisplay.ButtonType> leftButtons = Lists.newArrayList();
   private int leftWidth = 0;
   private int rightWidth = 0;
   private int rows = 0;
   public Throwable exception;

   public RecipeDisplay(EmiRecipe recipe) {
      this.recipe = recipe;
      this.width = recipe.getDisplayWidth();
      this.height = recipe.getDisplayHeight();
      if (EmiRecipeFiller.isSupported(recipe) && EmiConfig.recipeFillButton) {
         this.rightButtons.add(RecipeDisplay.ButtonType.FILL);
      }

      if (recipe.supportsRecipeTree()) {
         if (EmiConfig.recipeTreeButton) {
            this.rightButtons.add(RecipeDisplay.ButtonType.TREE);
         }

         if (EmiConfig.recipeDefaultButton) {
            this.rightButtons.add(RecipeDisplay.ButtonType.DEFAULT);
         }
      }

      if (EmiConfig.recipeScreenshotButton) {
         this.leftButtons.add(RecipeDisplay.ButtonType.SCREENSHOT);
      }

      this.rows = Math.max(1, (this.height + 8 + 2) / 14);
      this.leftWidth = Math.max(0, (this.leftButtons.size() + this.rows - 1) / this.rows * 14 - 1);
      this.rightWidth = Math.max(0, (this.rightButtons.size() + this.rows - 1) / this.rows * 14 - 1);
   }

   public RecipeDisplay(Throwable exception) {
      this.recipe = null;
      this.width = 128;
      this.height = 64;
      this.exception = exception;
   }

   public WidgetGroup getWidgets(int x, int y, int availableWidth, int availableHeight) {
      int wx = x + (availableWidth - this.width) / 2;
      wx = Math.max(x + this.getLeftWidth(), Math.min(x + availableWidth - this.width - this.getRightWidth(), wx));
      int wy = y;
      int wWidth = this.width;
      int wHeight = Math.min(availableHeight, this.height);
      WidgetGroup widgets = new WidgetGroup(this.recipe, wx, wy, wWidth, wHeight);
      if (this.recipe != null) {
         try {
            this.recipe.addWidgets(widgets);
            if (EmiConfig.showRecipeDecorators) {
               for (EmiRecipeDecorator decorator : EmiRecipes.decorators) {
                  decorator.decorateRecipe(this.recipe, widgets);
               }
            }

            if (EmiConfig.devMode) {
               widgets.decorateDevMode();
            }

            this.addButtons(widgets, this.leftButtons, -17, -14);
            this.addButtons(widgets, this.rightButtons, this.width + 5, 14);
         } catch (Throwable t) {
            EmiLog.error("Error constructing recipe widgets", t);
            widgets = new WidgetGroup(this.recipe, wx, wy, wWidth, wHeight);
            widgets.add(
               new TextWidget(
                     EmiPort.ordered(EmiPort.translatable("emi.error.recipe.render")), wWidth / 2, wHeight / 2 - 5, ChatFormatting.RED.getColor(), true
                  )
                  .horizontalAlign(TextWidget.Alignment.CENTER)
            );
            if (this.exception != null) {
               List<Component> text = EmiUtil.getStackTrace(this.exception).stream().map(s -> EmiPort.literal(s)).toList();
               widgets.addTooltipText(text, 0, 0, wWidth, wHeight);
            }
         }
      } else {
         widgets.add(
            new TextWidget(
                  EmiPort.ordered(EmiPort.translatable("emi.error.recipe.initialize")), wWidth / 2, wHeight / 2 - 5, ChatFormatting.RED.getColor(), true
               )
               .horizontalAlign(TextWidget.Alignment.CENTER)
         );
         if (this.exception != null) {
            List<Component> text = EmiUtil.getStackTrace(this.exception).stream().map(s -> EmiPort.literal(s)).toList();
            widgets.addTooltipText(text, 0, 0, wWidth, wHeight);
         }
      }

      return widgets;
   }

   private void addButtons(WidgetGroup widgets, List<RecipeDisplay.ButtonType> types, int x, int xOff) {
      int space = Math.min(8, this.height + 8 - (Math.min(this.rows, types.size()) * 14 - 2));
      int bottom = this.height + 4 - 12 - space / 2;
      int size = types.size();

      while (size > 0) {
         int used = Math.min(this.rows, size);
         List<RecipeDisplay.ButtonType> current = types.subList(size - used, size);
         int yOff = 0;

         for (RecipeDisplay.ButtonType type : current) {
            int bx = x;
            int by = bottom - yOff;

            widgets.add(switch (type) {
               case FILL -> new RecipeFillButtonWidget(bx, by, this.recipe);
               case TREE -> new RecipeTreeButtonWidget(bx, by, this.recipe);
               case DEFAULT -> new RecipeDefaultButtonWidget(bx, by, this.recipe);
               case SCREENSHOT -> new RecipeScreenshotButtonWidget(bx, by, this.recipe);
            });
            yOff += 14;
         }

         size -= used;
         x += xOff;
      }
   }

   public int getLeftWidth() {
      return this.leftWidth;
   }

   public int getRightWidth() {
      return this.rightWidth;
   }

   public int getWidth() {
      return this.leftWidth + this.rightWidth + this.width;
   }

   public int getHeight() {
      return this.height;
   }

   private enum ButtonType {
      FILL,
      TREE,
      DEFAULT,
      SCREENSHOT;

      // $VF: synthetic method
      private static RecipeDisplay.ButtonType[] $values() {
         return new RecipeDisplay.ButtonType[]{FILL, TREE, DEFAULT, SCREENSHOT};
      }
   }
}
