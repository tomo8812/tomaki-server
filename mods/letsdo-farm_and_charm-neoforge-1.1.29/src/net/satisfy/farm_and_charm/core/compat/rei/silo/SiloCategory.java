package net.satisfy.farm_and_charm.core.compat.rei.silo;

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
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.foundation.compat.RecipeViewerLayout;

public class SiloCategory implements DisplayCategory<SiloDisplay> {
   public static final CategoryIdentifier<SiloDisplay> SILO_DISPLAY = CategoryIdentifier.of("farm_and_charm", "silo_display");

   public CategoryIdentifier<SiloDisplay> getCategoryIdentifier() {
      return SILO_DISPLAY;
   }

   public Component getTitle() {
      return Component.translatable("rei.farm_and_charm.silo_category");
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ObjectRegistry.SILO_WOOD.get());
   }

   public int getDisplayWidth(SiloDisplay display) {
      return 64;
   }

   public int getDisplayHeight() {
      return 96;
   }

   public List<Widget> setupDisplay(SiloDisplay display, Rectangle bounds) {
      Point startPoint = new Point(bounds.getCenterX(), bounds.getCenterY());
      List<Widget> widgets = Lists.newArrayList();
      widgets.add(Widgets.createRecipeBase(bounds));
      widgets.add(
         Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> RecipeViewerLayout.drawDownArrow(graphics, startPoint.x - 8, startPoint.y - 19))
      );
      widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x - 8, startPoint.y + 12)));
      widgets.add(
         Widgets.createSlot(new Point(startPoint.x - 8, startPoint.y + 12))
            .entries((Collection)display.getOutputEntries().get(0))
            .disableBackground()
            .markOutput()
      );
      if (display.getInputEntries().isEmpty()) {
         widgets.add(Widgets.createSlotBackground(new Point(startPoint.x - 8, startPoint.y - 40)));
      } else {
         widgets.add(Widgets.createSlot(new Point(startPoint.x - 8, startPoint.y - 40)).entries((Collection)display.getInputEntries().get(0)).markInput());
      }

      return widgets;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public int getDisplayWidth(Display param1) {
      return this.getDisplayWidth((SiloDisplay)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((SiloDisplay)var1, nullx);
   }
}
