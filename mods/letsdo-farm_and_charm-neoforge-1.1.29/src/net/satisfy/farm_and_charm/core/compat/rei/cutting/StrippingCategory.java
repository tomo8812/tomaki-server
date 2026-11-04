package net.satisfy.farm_and_charm.core.compat.rei.cutting;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.satisfy.foundation.compat.rei.ReiWidgets;

public class StrippingCategory implements DisplayCategory<StrippingDisplay> {
   private static final int PADDING = 5;

   public CategoryIdentifier<? extends StrippingDisplay> getCategoryIdentifier() {
      return StrippingDisplay.ID;
   }

   public Component getTitle() {
      return Component.translatable("category.farm_and_charm.log_stripping");
   }

   public Renderer getIcon() {
      return EntryStacks.of(Items.IRON_AXE);
   }

   public int getDisplayWidth(StrippingDisplay display) {
      return 140;
   }

   public int getDisplayHeight() {
      return 46;
   }

   public List<Widget> setupDisplay(StrippingDisplay display, Rectangle bounds) {
      List<Widget> widgets = new ArrayList<>();
      widgets.add(Widgets.createRecipeBase(bounds));
      int x = bounds.x + 5;
      int y = bounds.y + 5 + 6;
      ReiWidgets.inputSlot(widgets, x + 10, y, (EntryIngredient)display.getInputEntries().getFirst());
      ReiWidgets.rightArrow(widgets, x + 34, y + 1);
      ReiWidgets.outputSlot(widgets, x + 64, y, (EntryIngredient)display.getOutputEntries().getFirst());
      return widgets;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public int getDisplayWidth(Display param1) {
      return this.getDisplayWidth((StrippingDisplay)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((StrippingDisplay)var1, nullx);
   }
}
