package net.satisfy.farm_and_charm.core.compat.rei.doughing;

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

public class CraftingBowlCategory implements DisplayCategory<CraftingBowlDisplay> {
   public static final CategoryIdentifier<CraftingBowlDisplay> CRAFTING_BOWL_DISPLAY = CategoryIdentifier.of("farm_and_charm", "bowl_display");

   public CategoryIdentifier<CraftingBowlDisplay> getCategoryIdentifier() {
      return CRAFTING_BOWL_DISPLAY;
   }

   public Component getTitle() {
      return Component.translatable("rei.farm_and_charm.bowl_category");
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ObjectRegistry.CRAFTING_BOWL.get());
   }

   public int getDisplayHeight() {
      return 64;
   }

   public List<Widget> setupDisplay(CraftingBowlDisplay display, Rectangle bounds) {
      Point startPoint = new Point(bounds.getCenterX() - 18, bounds.getCenterY() - 18);
      List<Widget> widgets = Lists.newArrayList();
      widgets.add(Widgets.createRecipeBase(bounds));
      int baseX = startPoint.x - 40;
      int baseY = startPoint.y;
      int size = 18;
      int arrowXOffset = 50;
      int arrowYOffset = size / 2;
      int outputXOffset = arrowXOffset + 46;
      widgets.add(Widgets.createArrow(new Point(baseX + arrowXOffset, baseY + arrowYOffset)).animationDurationTicks(50.0));
      widgets.add(Widgets.createResultSlotBackground(new Point(baseX + outputXOffset, baseY + arrowYOffset)));
      widgets.add(
         Widgets.createSlot(new Point(baseX + outputXOffset, baseY + arrowYOffset))
            .entries((Collection)display.getOutputEntries().get(0))
            .disableBackground()
            .markOutput()
      );

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            int index = i * 2 + j;
            Point slotPosition = new Point(baseX + j * size, baseY + i * size);
            if (display.getInputEntries().size() > index) {
               widgets.add(Widgets.createSlot(slotPosition).entries((Collection)display.getInputEntries().get(index)).markInput());
            } else {
               widgets.add(Widgets.createSlotBackground(slotPosition));
            }
         }
      }

      return widgets;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((CraftingBowlDisplay)var1, nullx);
   }
}
