package dev.emi.emi.api.widget;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiResolutionRecipe;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.HelpLevel;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorites;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.tooltip.EmiTooltip;
import dev.emi.emi.screen.tooltip.RecipeCostTooltipComponent;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SlotWidget extends Widget {
   protected final EmiIngredient stack;
   protected final int x;
   protected final int y;
   protected ResourceLocation textureId;
   protected int u;
   protected int v;
   protected int customWidth;
   protected int customHeight;
   protected boolean drawBack = true;
   protected boolean output = false;
   protected boolean catalyst = false;
   protected boolean custom = false;
   protected List<Supplier<ClientTooltipComponent>> tooltipSuppliers = Lists.newArrayList();
   protected Bounds bounds;
   private EmiRecipe recipe;

   public SlotWidget(EmiIngredient stack, int x, int y) {
      this.stack = stack;
      this.x = x;
      this.y = y;
   }

   public EmiIngredient getStack() {
      return this.stack;
   }

   public EmiRecipe getRecipe() {
      return this.recipe;
   }

   public SlotWidget drawBack(boolean drawBack) {
      this.drawBack = drawBack;
      return this;
   }

   public SlotWidget large(boolean large) {
      this.output = large;
      return this;
   }

   public SlotWidget catalyst(boolean catalyst) {
      this.catalyst = catalyst;
      return this;
   }

   public SlotWidget appendTooltip(Function<EmiIngredient, ClientTooltipComponent> function) {
      return this.appendTooltip(() -> function.apply(this.getStack()));
   }

   public SlotWidget appendTooltip(Supplier<ClientTooltipComponent> supplier) {
      this.tooltipSuppliers.add(supplier);
      return this;
   }

   public SlotWidget appendTooltip(Component text) {
      this.tooltipSuppliers.add(() -> ClientTooltipComponent.create(EmiPort.ordered(text)));
      return this;
   }

   public SlotWidget recipeContext(EmiRecipe recipe) {
      this.recipe = recipe;
      return this;
   }

   public SlotWidget backgroundTexture(ResourceLocation id, int u, int v) {
      this.textureId = id;
      this.u = u;
      this.v = v;
      return this;
   }

   public SlotWidget customBackground(ResourceLocation id, int u, int v, int width, int height) {
      this.backgroundTexture(id, u, v);
      this.custom = true;
      this.customWidth = width;
      this.customHeight = height;
      return this;
   }

   @Override
   public Bounds getBounds() {
      if (this.custom) {
         return new Bounds(this.x, this.y, this.customWidth, this.customHeight);
      } else {
         return this.output ? new Bounds(this.x, this.y, 26, 26) : new Bounds(this.x, this.y, 18, 18);
      }
   }

   @Override
   public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      EmiPort.setPositionTexShader();
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawBackground(draw, mouseX, mouseY, delta);
      this.drawStack(draw, mouseX, mouseY, delta);
      context.disableDepthTest();
      this.drawOverlay(draw, mouseX, mouseY, delta);
   }

   public void drawBackground(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      Bounds bounds = this.getBounds();
      int width = bounds.width();
      int height = bounds.height();
      if (this.drawBack) {
         if (this.textureId != null) {
            context.drawTexture(this.textureId, bounds.x(), bounds.y(), width, height, this.u, this.v, width, height, 256, 256);
         } else {
            int v = this.getStack().getChance() != 1.0F ? bounds.height() : 0;
            if (this.output) {
               context.drawTexture(EmiRenderHelper.WIDGETS, bounds.x(), bounds.y(), 26, 26, 18.0F, v, 26, 26, 256, 256);
            } else {
               context.drawTexture(EmiRenderHelper.WIDGETS, bounds.x(), bounds.y(), 18, 18, 0.0F, v, 18, 18, 256, 256);
            }
         }
      }
   }

   public void drawStack(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      Bounds bounds = this.getBounds();
      int xOff = (bounds.width() - 16) / 2;
      int yOff = (bounds.height() - 16) / 2;
      this.getStack().render(draw, bounds.x() + xOff, bounds.y() + yOff, delta);
   }

   public void drawOverlay(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      Bounds bounds = this.getBounds();
      int width = bounds.width();
      int height = bounds.height();
      int xOff = (width - 16) / 2;
      int yOff = (height - 16) / 2;
      if (this.catalyst) {
         EmiRender.renderCatalystIcon(this.getStack(), draw, this.x + xOff, this.y + yOff);
      }

      if (this.shouldDrawSlotHighlight(mouseX, mouseY)) {
         this.drawSlotHighlight(draw, bounds);
      }
   }

   public boolean shouldDrawSlotHighlight(int mouseX, int mouseY) {
      return this.getBounds().contains(mouseX, mouseY) && EmiConfig.showHoverOverlay;
   }

   public void drawSlotHighlight(GuiGraphics draw, Bounds bounds) {
      EmiRenderHelper.drawSlotHightlight(EmiDrawContext.wrap(draw), bounds.x() + 1, bounds.y() + 1, bounds.width() - 2, bounds.height() - 2, 200);
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      if (this.getStack().isEmpty()) {
         return list;
      }

      list.addAll(this.getStack().getTooltip());
      this.addSlotTooltip(list);
      return list;
   }

   protected void addSlotTooltip(List<ClientTooltipComponent> list) {
      for (Supplier<ClientTooltipComponent> supplier : this.tooltipSuppliers) {
         list.add(supplier.get());
      }

      if (this.getStack().getChance() != 1.0F) {
         list.add(EmiTooltip.chance(this.recipe != null ? "produce" : "consume", this.getStack().getChance()));
      }

      EmiRecipe recipe = this.getRecipe();
      if (recipe != null) {
         if (recipe.getId() != null && EmiConfig.showRecipeIds) {
            list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.literal(recipe.getId().toString(), ChatFormatting.GRAY))));
         }

         if (this.canResolve() && EmiConfig.helpLevel.has(HelpLevel.NORMAL)) {
            if (EmiConfig.viewRecipes.isBound()) {
               list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.resolve.resolve", EmiConfig.viewRecipes.getBindText()))));
            }

            if (EmiConfig.defaultStack.isBound()) {
               list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.resolve.default", EmiConfig.defaultStack.getBindText()))));
            }
         } else if (EmiConfig.favorite.isBound() && EmiConfig.helpLevel.has(HelpLevel.NORMAL) && EmiFavorites.canFavorite(this.getStack(), this.getRecipe())) {
            list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.favorite_recipe", EmiConfig.favorite.getBindText()))));
         }

         if (EmiConfig.showCostPerBatch && recipe.supportsRecipeTree() && !(recipe instanceof EmiResolutionRecipe)) {
            RecipeCostTooltipComponent rctc = new RecipeCostTooltipComponent(recipe);
            if (rctc.shouldDisplay()) {
               list.add(rctc);
            }
         }
      }
   }

   @Override
   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      return this.slotInteraction(bind -> bind.matchesMouse(button))
         ? true
         : EmiScreenManager.stackInteraction(new EmiStackInteraction(this.getStack(), this.getRecipe(), true), bind -> bind.matchesMouse(button));
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.slotInteraction(bind -> bind.matchesKey(keyCode, scanCode))
         ? true
         : EmiScreenManager.stackInteraction(new EmiStackInteraction(this.getStack(), this.getRecipe(), true), bind -> bind.matchesKey(keyCode, scanCode));
   }

   private boolean canResolve() {
      EmiRecipe recipe = this.getRecipe();
      return recipe != null && recipe.supportsRecipeTree() && RecipeScreen.resolve != null;
   }

   private boolean slotInteraction(Function<EmiBind, Boolean> function) {
      EmiRecipe recipe = this.getRecipe();
      if (this.canResolve()) {
         if (function.apply(EmiConfig.defaultStack)) {
            if (BoM.isDefaultRecipe(this.getStack(), recipe)) {
               BoM.removeRecipe(this.getStack(), recipe);
            } else {
               BoM.addRecipe(RecipeScreen.resolve, recipe);
            }

            EmiHistory.pop();
            return true;
         }

         if (function.apply(EmiConfig.viewRecipes)) {
            BoM.addResolution(RecipeScreen.resolve, recipe);
            EmiHistory.pop();
            return true;
         }
      } else if (recipe != null && recipe.supportsRecipeTree() && function.apply(EmiConfig.defaultStack)) {
         if (BoM.isDefaultRecipe(this.getStack(), recipe)) {
            BoM.removeRecipe(this.getStack(), recipe);
         } else {
            BoM.addRecipe(this.getStack(), recipe);
         }

         return true;
      }

      return false;
   }
}
