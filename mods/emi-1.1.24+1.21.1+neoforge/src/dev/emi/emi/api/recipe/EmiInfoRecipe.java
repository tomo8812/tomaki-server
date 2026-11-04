package dev.emi.emi.api.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public class EmiInfoRecipe implements EmiRecipe {
   private static final int STACK_WIDTH = 6;
   private static final int MAX_STACKS = 18;
   private static final int PADDING = 4;
   private static final Minecraft CLIENT = Minecraft.getInstance();
   private final List<EmiIngredient> stacks;
   private final List<FormattedCharSequence> text;
   private final ResourceLocation id;

   public EmiInfoRecipe(List<EmiIngredient> stacks, List<Component> text, @Nullable ResourceLocation id) {
      this.stacks = stacks;
      this.text = text.stream().flatMap(t -> CLIENT.font.split(t, this.getDisplayWidth() - 4).stream()).toList();
      this.id = id;
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return VanillaEmiRecipeCategories.INFO;
   }

   @Nullable
   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return this.stacks;
   }

   @Override
   public List<EmiStack> getOutputs() {
      return this.stacks.stream().flatMap(ing -> ing.getEmiStacks().stream()).toList();
   }

   @Override
   public boolean supportsRecipeTree() {
      return false;
   }

   @Override
   public int getDisplayWidth() {
      return 144;
   }

   @Override
   public int getDisplayHeight() {
      int stackHeight = ((Math.min(this.stacks.size(), 18) - 1) / 6 + 1) * 18;
      return stackHeight + 9 * this.text.size() + 4;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      int stackCount = Math.min(this.stacks.size(), 18);
      int stackHeight = (stackCount - 1) / 6 + 1;
      int xOff = 54 - stackCount % 6 * 9;

      for (int i = 0; i < stackCount; i++) {
         int y = i / 6 * 18;
         int x = i % 6 * 18;
         if (y / 18 + 1 == stackHeight && stackCount % 6 != 0) {
            x += xOff;
         }

         if (i + 1 == stackCount && this.stacks.size() > stackCount) {
            widgets.addSlot(EmiIngredient.of(this.stacks.subList(i, this.stacks.size())), x + 18, y);
         } else {
            widgets.addSlot(this.stacks.get(i), x + 18, y);
         }
      }

      int y = stackHeight * 18 + 4;
      int lineCount = (widgets.getHeight() - y) / 9;
      EmiInfoRecipe.PageManager manager = new EmiInfoRecipe.PageManager(this.text, lineCount);
      if (lineCount < this.text.size()) {
         widgets.addButton(2, 2, 12, 12, 0, 0, () -> true, (mouseX, mouseY, button) -> manager.scroll(-1));
         widgets.addButton(widgets.getWidth() - 14, 2, 12, 12, 12, 0, () -> true, (mouseX, mouseY, button) -> manager.scroll(1));
      }

      widgets.addDrawable(0, y, 0, 0, (raw, mouseX, mouseY, delta) -> {
         EmiDrawContext context = EmiDrawContext.wrap(raw);
         int lo = manager.start();

         for (int i = 0; i < lineCount; i++) {
            int l = lo + i;
            if (l >= manager.lines.size()) {
               return;
            }

            FormattedCharSequence text = manager.lines.get(l);
            context.drawText(text, 0, y - y + i * 9, 0);
         }
      });
   }

   private static class PageManager {
      public final List<FormattedCharSequence> lines;
      public final int pageSize;
      public int currentPage;

      public PageManager(List<FormattedCharSequence> lines, int pageSize) {
         this.lines = lines;
         this.pageSize = pageSize;
      }

      public void scroll(int delta) {
         this.currentPage += delta;
         int totalPages = (this.lines.size() - 1) / this.pageSize + 1;
         if (this.currentPage < 0) {
            this.currentPage = totalPages - 1;
         }

         if (this.currentPage >= totalPages) {
            this.currentPage = 0;
         }
      }

      public int start() {
         return this.currentPage * this.pageSize;
      }
   }
}
