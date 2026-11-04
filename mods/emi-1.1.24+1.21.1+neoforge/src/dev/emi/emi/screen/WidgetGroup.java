package dev.emi.emi.screen;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.DrawableWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.dev.EmiDev;
import dev.emi.emi.runtime.dev.RecipeError;
import dev.emi.emi.screen.tooltip.EmiTooltip;
import dev.emi.emi.widget.RecipeBackground;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

public class WidgetGroup implements WidgetHolder {
   public final EmiRecipe recipe;
   public final int x;
   public final int y;
   public final int width;
   public final int height;
   public final List<Widget> widgets = Lists.newArrayList();

   public WidgetGroup(EmiRecipe recipe, int x, int y, int width, int height) {
      this.recipe = recipe;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      if (recipe != null) {
         this.widgets.add(new RecipeBackground(-4, -4, width + 8, height + 8));
      }
   }

   public void error(Throwable e) {
      this.widgets.clear();
      this.widgets.add(new RecipeBackground(-4, -4, this.width + 8, this.height + 8));
      this.widgets
         .add(
            new TextWidget(
                  EmiPort.ordered(EmiPort.translatable("emi.error.recipe.render")), this.width / 2, this.height / 2 - 5, ChatFormatting.RED.getColor(), true
               )
               .horizontalAlign(TextWidget.Alignment.CENTER)
         );
      this.widgets
         .add(
            new DrawableWidget(0, 0, this.width, this.height, (raw, mouseX, mouseY, delta) -> {})
               .tooltip(
                  (i, j) -> EmiUtil.getStackTrace(e)
                     .stream()
                     .map(EmiPort::literal)
                     .map(EmiPort::ordered)
                     .<ClientTooltipComponent>map(ClientTooltipComponent::create)
                     .toList()
               )
         );
   }

   public void decorateDevMode() {
      EmiRecipeCategory category = this.recipe.getCategory();
      ResourceLocation cid = category == null ? null : category.getId();
      ResourceLocation id = this.recipe.getId();
      List<RecipeError> errors = Lists.newArrayList();
      if (id == null) {
         errors.add(new RecipeError(RecipeError.Severity.WARNING, EmiTooltip.splitTranslate("emi.dev.null_recipe_id")));
      } else if (EmiDev.duplicateRecipeIds.contains(id)) {
         List<ClientTooltipComponent> tooltip = Lists.newArrayList();
         if (!Objects.equals(id.getNamespace(), "minecraft") && !Objects.equals(id.getNamespace(), "emi")) {
            tooltip.addAll(EmiTooltip.splitTranslate("emi.dev.duplicate_recipe_id", id));
         } else {
            tooltip.addAll(EmiTooltip.splitTranslate("emi.dev.duplicate_vanilla_recipe_id", id));
         }

         if (cid != null) {
            String suggestedPath = cid.getPath() + "/" + id.getNamespace() + "/" + id.getPath();
            if (EmiApi.getRecipeManager().getRecipe(EmiPort.id(cid.getNamespace(), suggestedPath)) == null) {
               tooltip.addAll(EmiTooltip.splitTranslate("emi.dev.suggest_id", cid.getNamespace(), suggestedPath));
            } else {
               tooltip.addAll(EmiTooltip.splitTranslate("emi.dev.synthetic_id"));
            }
         } else {
            tooltip.addAll(EmiTooltip.splitTranslate("emi.dev.synthetic_id"));
         }

         errors.add(new RecipeError(RecipeError.Severity.ERROR, tooltip));
      } else if (EmiDev.incorrectRecipeIds.contains(id)) {
         List<ClientTooltipComponent> tooltip = Lists.newArrayList();
         tooltip.addAll(EmiTooltip.splitTranslate("emi.dev.synthetic_nag_explanation", id));
         tooltip.addAll(EmiTooltip.splitTranslate("emi.dev.synthetic_id"));
         errors.add(new RecipeError(RecipeError.Severity.ERROR, tooltip));
      }

      if (this.recipe.getInputs() != null && this.recipe.getOutputs() != null && this.recipe.getCatalysts() != null) {
         int nullStacks = 0;

         for (EmiIngredient stack : this.recipe.getInputs()) {
            if (stack == null) {
               nullStacks++;
            }
         }

         for (EmiIngredient stack : this.recipe.getCatalysts()) {
            if (stack == null) {
               nullStacks++;
            }
         }

         for (EmiStack stack : this.recipe.getOutputs()) {
            if (stack == null) {
               nullStacks++;
            }
         }

         if (nullStacks > 0) {
            errors.add(new RecipeError(RecipeError.Severity.ERROR, EmiTooltip.splitTranslate("emi.dev.null_input_or_output")));
         }

         if (!this.recipe.getOutputs().isEmpty() && this.recipe.supportsRecipeTree()) {
            int recipeContexts = 0;

            for (Widget widget : this.widgets) {
               if (widget instanceof SlotWidget sw && sw.getRecipe() != null) {
                  recipeContexts++;
               }
            }

            if (recipeContexts == 0) {
               errors.add(new RecipeError(RecipeError.Severity.WARNING, EmiTooltip.splitTranslate("emi.dev.no_output_slots")));
            }
         }
      } else {
         errors.add(new RecipeError(RecipeError.Severity.ERROR, EmiTooltip.splitTranslate("emi.dev.null_input_or_output")));
      }

      if (!errors.isEmpty()) {
         List<ClientTooltipComponent> tooltip = Lists.newArrayList();
         RecipeError.Severity severity = RecipeError.Severity.WARNING;

         for (RecipeError error : errors) {
            if (error.severity() == RecipeError.Severity.ERROR) {
               severity = RecipeError.Severity.ERROR;
            }
            tooltip.add(switch (error.severity()) {
               case ERROR -> EmiTooltipComponents.of(EmiPort.translatable("emi.dev.severity.error", ChatFormatting.RED));
               case WARNING -> EmiTooltipComponents.of(EmiPort.translatable("emi.dev.severity.warning", ChatFormatting.YELLOW));
            });
            tooltip.addAll(error.tooltip());
         }
         int errorColor = switch (severity) {
            case ERROR -> -859045888;
            case WARNING -> -858993664;
         };
         this.addDrawable(0, 0, this.width, this.height, (raw, mouseX, mouseY, delta) -> {
            EmiDrawContext draw = EmiDrawContext.wrap(raw);
            draw.fill(-2, -3, this.width, 2, errorColor);
            draw.fill(-2, this.height + 1, this.width + 4, 2, errorColor);
         });
         this.addText(EmiPort.literal("!", ChatFormatting.BOLD), this.width, -2, 0xFF000000 | errorColor, true);
         this.addTooltip(tooltip, this.width - 2, -4, 8, 16);
      }
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   @Override
   public <T extends Widget> T add(T widget) {
      this.widgets.add(widget);
      return widget;
   }
}
