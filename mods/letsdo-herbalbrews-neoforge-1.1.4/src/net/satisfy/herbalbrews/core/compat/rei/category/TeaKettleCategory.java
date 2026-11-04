package net.satisfy.herbalbrews.core.compat.rei.category;

import com.google.common.collect.Lists;
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
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.satisfy.herbalbrews.core.compat.rei.display.TeaKettleDisplay;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;

public class TeaKettleCategory implements DisplayCategory<TeaKettleDisplay> {
   public static final CategoryIdentifier<TeaKettleDisplay> TEA_KETTLE_DISPLAY = CategoryIdentifier.of("herbalbrews", "tea_kettle_display");

   public CategoryIdentifier<TeaKettleDisplay> getCategoryIdentifier() {
      return TEA_KETTLE_DISPLAY;
   }

   public Component getTitle() {
      return ((Block)ObjectRegistry.TEA_KETTLE.get()).getName();
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ObjectRegistry.TEA_KETTLE.get());
   }

   public int getDisplayHeight() {
      return 70;
   }

   public List<Widget> setupDisplay(TeaKettleDisplay display, Rectangle bounds) {
      Point startPoint = new Point(bounds.getX() + 10, bounds.getY() + 10);
      List<Widget> widgets = Lists.newArrayList();
      widgets.add(Widgets.createArrow(new Point(startPoint.x + 54, startPoint.y + 22)).animationDurationTicks(200.0));
      widgets.add(Widgets.createSlot(new Point(startPoint.x + 85, startPoint.y + 22)).entries((Collection)display.getOutputEntries().get(0)).markOutput());
      widgets.add(Widgets.createSlot(new Point(startPoint.x + 31, startPoint.y + 30)).entries((Collection)display.getInputEntries().get(0)).markInput());

      for (int slot = 1; slot < 4; slot++) {
         int x = 13 + (slot - 1) % 2 * 18;
         int y = 12 + (slot - 1) / 2 * 18;
         widgets.add(Widgets.createSlot(new Point(startPoint.x + x, startPoint.y + y)).entries((Collection)display.getInputEntries().get(slot)).markInput());
      }

      return widgets;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((TeaKettleDisplay)var1, nullx);
   }
}
