package net.satisfy.bakery.core.compat.rei.jam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.satisfy.bakery.core.registry.ObjectRegistry;

public class JamPotCategory implements DisplayCategory<JamPotDisplay> {
   public CategoryIdentifier<JamPotDisplay> getCategoryIdentifier() {
      return JamPotDisplay.JAM_POT_DISPLAY;
   }

   public Component getTitle() {
      return ((Item)ObjectRegistry.SMALL_COOKING_POT_ITEM.get()).getDescription();
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ObjectRegistry.SMALL_COOKING_POT_ITEM.get());
   }

   public int getDisplayHeight() {
      return 36;
   }

   public List<Widget> setupDisplay(JamPotDisplay display, Rectangle bounds) {
      Point start = new Point(bounds.getCenterX() - 60, bounds.getCenterY() - 8);
      List<Widget> widgets = new ArrayList<>();
      widgets.add(Widgets.createRecipeBase(bounds));

      for (int i = 0; i < 3; i++) {
         widgets.add(Widgets.createSlot(new Point(start.x + i * 18, start.y)).entries((Collection)display.getInputEntries().get(i)).markInput());
      }

      widgets.add(Widgets.createArrow(new Point(start.x + 60, start.y)).animationDurationTicks(display.getCookingTime()));
      widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 98, start.y)));
      widgets.add(Widgets.createSlot(new Point(start.x + 98, start.y)).entries((Collection)display.getOutputEntries().get(0)).disableBackground().markOutput());
      return widgets;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((JamPotDisplay)var1, nullx);
   }
}
