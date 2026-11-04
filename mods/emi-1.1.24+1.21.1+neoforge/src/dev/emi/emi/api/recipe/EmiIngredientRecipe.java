package dev.emi.emi.api.recipe;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ListEmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;

public abstract class EmiIngredientRecipe implements EmiRecipe {
   protected abstract EmiIngredient getIngredient();

   protected abstract List<EmiStack> getStacks();

   protected abstract EmiRecipe getRecipeContext(EmiStack var1, int var2);

   @Override
   public List<EmiIngredient> getInputs() {
      return List.of(new ListEmiIngredient(this.getStacks(), 1L));
   }

   @Override
   public List<EmiStack> getOutputs() {
      return List.of();
   }

   @Override
   public int getDisplayHeight() {
      return ((this.getStacks().size() - 1) / 8 + 1) * 18 + 24;
   }

   @Override
   public int getDisplayWidth() {
      return 144;
   }

   @Override
   public boolean supportsRecipeTree() {
      return false;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      List<EmiStack> stacks = this.getStacks();
      widgets.addSlot(this.getIngredient(), 63, 0);
      int ph = (widgets.getHeight() - 42) / 18;
      int pageSize = (ph + 1) * 8;
      EmiIngredientRecipe.PageManager manager = new EmiIngredientRecipe.PageManager(stacks, pageSize);
      if (pageSize < stacks.size()) {
         widgets.addButton(2, 2, 12, 12, 0, 0, () -> true, (mouseX, mouseY, button) -> manager.scroll(-1));
         widgets.addButton(widgets.getWidth() - 14, 2, 12, 12, 12, 0, () -> true, (mouseX, mouseY, button) -> manager.scroll(1));
      }

      for (int i = 0; i < stacks.size() && i / 8 <= ph; i++) {
         widgets.add(new EmiIngredientRecipe.PageSlotWidget(manager, i, i % 8 * 18, i / 8 * 18 + 24));
      }
   }

   private class PageManager {
      public final List<EmiStack> stacks;
      public final int pageSize;
      public int currentPage;

      public PageManager(List<EmiStack> stacks, int pageSize) {
         this.stacks = stacks;
         this.pageSize = pageSize;
      }

      public void scroll(int delta) {
         this.currentPage += delta;
         int totalPages = (this.stacks.size() - 1) / this.pageSize + 1;
         if (this.currentPage < 0) {
            this.currentPage = totalPages - 1;
         }

         if (this.currentPage >= totalPages) {
            this.currentPage = 0;
         }
      }

      public EmiStack getStack(int offset) {
         offset += this.pageSize * this.currentPage;
         return offset < this.stacks.size() ? this.stacks.get(offset) : EmiStack.EMPTY;
      }

      public EmiRecipe getRecipe(int offset) {
         offset += this.pageSize * this.currentPage;
         return offset < this.stacks.size() ? EmiIngredientRecipe.this.getRecipeContext(this.stacks.get(offset), offset) : null;
      }
   }

   private class PageSlotWidget extends SlotWidget {
      public final EmiIngredientRecipe.PageManager manager;
      public final int offset;

      public PageSlotWidget(EmiIngredientRecipe.PageManager manager, int offset, int x, int y) {
         super(EmiStack.EMPTY, x, y);
         this.manager = manager;
         this.offset = offset;
      }

      @Override
      public EmiIngredient getStack() {
         return this.manager.getStack(this.offset);
      }

      @Override
      public EmiRecipe getRecipe() {
         return this.manager.getRecipe(this.offset);
      }

      @Override
      public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
         if (!this.getStack().isEmpty()) {
            super.render(draw, mouseX, mouseY, delta);
         }
      }

      @Override
      public void drawBackground(GuiGraphics draw, int mouseX, int mouseY, float delta) {
         super.drawBackground(draw, mouseX, mouseY, delta);
         EmiDrawContext context = EmiDrawContext.wrap(draw);
         if (BoM.getRecipe(EmiIngredientRecipe.this.getIngredient()) instanceof EmiResolutionRecipe err && err.stack.equals(this.getStack())) {
            context.drawTexture(EmiRenderHelper.WIDGETS, this.x, this.y, 36, 128, 18, 18);
         }
      }
   }
}
