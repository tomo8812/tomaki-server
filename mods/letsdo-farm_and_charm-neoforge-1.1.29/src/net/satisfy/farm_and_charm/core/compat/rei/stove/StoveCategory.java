package net.satisfy.farm_and_charm.core.compat.rei.stove;

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.DisplayRenderer;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.SimpleDisplayRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class StoveCategory implements DisplayCategory<StoveDisplay> {
   public List<Widget> setupDisplay(StoveDisplay display, Rectangle bounds) {
      Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);
      int cookingTime = 240;
      List<Widget> widgets = Lists.newArrayList();
      widgets.add(Widgets.createRecipeBase(bounds));
      DecimalFormat df = new DecimalFormat("###.##");
      widgets.add(
         Widgets.createLabel(
               new Point(bounds.x + bounds.width - 5, bounds.y + 5),
               Component.translatable("category.rei.cooking.time&xp", new Object[]{df.format(display.getXp()), df.format(cookingTime / 20.0)})
            )
            .noShadow()
            .rightAligned()
            .color(-12566464, -4473925)
      );
      int move = 20;
      int moveDown = 10;
      widgets.add(Widgets.createArrow(new Point(startPoint.x + 24 + move, startPoint.y + 8 + moveDown)).animationDurationTicks(cookingTime));
      widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61 + move, startPoint.y + 9 + moveDown)));
      widgets.add(
         Widgets.createSlot(new Point(startPoint.x + 61 + move, startPoint.y + 9 + moveDown))
            .entries((Collection)display.getOutputEntries().get(0))
            .disableBackground()
            .markOutput()
      );
      widgets.add(Widgets.createBurningFire(new Point(startPoint.x + 1, startPoint.y + 20 + moveDown)).animationDurationMS(10000.0));
      this.addSlot(widgets, display, startPoint, 0, moveDown, 0);
      if (display.getInputEntries().size() < 2) {
         this.addBackground(widgets, startPoint, 18, moveDown);
      } else {
         this.addSlot(widgets, display, startPoint, 18, moveDown, 1);
      }

      if (display.getInputEntries().size() < 3) {
         this.addBackground(widgets, startPoint, 36, moveDown);
      } else {
         this.addSlot(widgets, display, startPoint, 36, moveDown, 2);
      }

      return widgets;
   }

   public void addSlot(List<Widget> widgets, StoveDisplay display, Point startPoint, int x, int y, int index) {
      widgets.add(
         Widgets.createSlot(new Point(startPoint.x + x - 17, startPoint.y + 1 + y)).entries((Collection)display.getInputEntries().get(index)).markInput()
      );
   }

   public void addBackground(List<Widget> widgets, Point startPoint, int x, int y) {
      widgets.add(Widgets.createSlotBackground(new Point(startPoint.x + x - 17, startPoint.y + 1 + y)));
   }

   public DisplayRenderer getDisplayRenderer(StoveDisplay display) {
      return SimpleDisplayRenderer.from(Collections.singletonList((EntryIngredient)display.getInputEntries().get(0)), display.getOutputEntries());
   }

   public int getDisplayHeight() {
      return 65;
   }

   public CategoryIdentifier<? extends StoveDisplay> getCategoryIdentifier() {
      return StoveDisplay.STOVE_DISPLAY;
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ObjectRegistry.STOVE.get());
   }

   public Component getTitle() {
      return Component.translatable("rei.farm_and_charm.stove_category");
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((StoveDisplay)var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public DisplayRenderer getDisplayRenderer(Display param1) {
      return this.getDisplayRenderer((StoveDisplay)var1);
   }
}
