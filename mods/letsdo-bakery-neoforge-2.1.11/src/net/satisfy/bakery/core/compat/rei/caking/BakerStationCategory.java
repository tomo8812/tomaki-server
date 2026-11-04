package net.satisfy.bakery.core.compat.rei.caking;

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
import net.satisfy.bakery.core.registry.ObjectRegistry;

public class BakerStationCategory implements DisplayCategory<BakerStationDisplay> {
   public static final CategoryIdentifier<BakerStationDisplay> BAKER_STATION_DISPLAY = CategoryIdentifier.of("bakery", "baking_display");

   public CategoryIdentifier<BakerStationDisplay> getCategoryIdentifier() {
      return BAKER_STATION_DISPLAY;
   }

   public Component getTitle() {
      return ((Block)ObjectRegistry.BAKER_STATION.get()).getName();
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ObjectRegistry.BAKER_STATION.get());
   }

   public int getDisplayHeight() {
      return 64;
   }

   public List<Widget> setupDisplay(BakerStationDisplay display, Rectangle bounds) {
      Point startPoint = new Point(bounds.getCenterX() - 36, bounds.getCenterY() - 9);
      List<Widget> widgets = Lists.newArrayList();
      widgets.add(Widgets.createRecipeBase(bounds));
      int baseX = startPoint.x - 30;
      int baseY = startPoint.y;
      int size = 18;
      int arrowXOffset = 3 * size;
      int arrowYOffset = 0;
      int outputXOffset = arrowXOffset + 58;
      widgets.add(Widgets.createArrow(new Point(baseX + arrowXOffset, baseY + arrowYOffset)).animationDurationTicks(50.0));
      widgets.add(Widgets.createResultSlotBackground(new Point(baseX + outputXOffset, baseY)));
      widgets.add(
         Widgets.createSlot(new Point(baseX + outputXOffset, baseY)).entries((Collection)display.getOutputEntries().get(0)).disableBackground().markOutput()
      );
      widgets.add(Widgets.createSlot(new Point(baseX, baseY)).entries((Collection)display.getInputEntries().get(0)).markInput());

      for (int i = 1; i <= 2; i++) {
         if (display.getInputEntries().size() > i) {
            widgets.add(Widgets.createSlot(new Point(baseX + i * size, baseY)).entries((Collection)display.getInputEntries().get(i)).markInput());
         }
      }

      return widgets;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((BakerStationDisplay)var1, nullx);
   }
}
