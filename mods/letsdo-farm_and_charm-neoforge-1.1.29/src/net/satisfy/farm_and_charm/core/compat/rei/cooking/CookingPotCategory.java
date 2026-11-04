package net.satisfy.farm_and_charm.core.compat.rei.cooking;

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
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.core.block.entity.CookingPotBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.joml.Vector2i;

public class CookingPotCategory implements DisplayCategory<CookingPotDisplay> {
   public CategoryIdentifier<CookingPotDisplay> getCategoryIdentifier() {
      return CookingPotDisplay.COOKING_POT_DISPLAY;
   }

   public Component getTitle() {
      return Component.translatable("rei.farm_and_charm.cooking_pot_category");
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ObjectRegistry.COOKING_POT.get());
   }

   public List<Widget> setupDisplay(CookingPotDisplay display, Rectangle bounds) {
      Vector2i startPoint = new Vector2i(bounds.getCenterX() - 55, bounds.getCenterY() - 13);
      ArrayList<Widget> widgets = new ArrayList<>();
      widgets.add(Widgets.createRecipeBase(bounds));
      widgets.add(Widgets.createArrow(new Point(startPoint.x + 54, startPoint.y - 1)).animationDurationTicks(CookingPotBlockEntity.getMaxCookingTime()));
      widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 90, startPoint.y)));
      widgets.add(
         Widgets.createSlot(new Point(startPoint.x + 90, startPoint.y)).entries((Collection)display.getOutputEntries().get(0)).disableBackground().markOutput()
      );
      EntryIngredient containerEntry = (EntryIngredient)display.getInputEntries().get(6);
      if (!containerEntry.isEmpty()) {
         widgets.add(Widgets.createSlot(new Point(startPoint.x + 56, startPoint.y + 23)).entries(containerEntry).markInput());
      }

      for (int i = 0; i < 6; i++) {
         int x = i % 3 * 18 - 8;
         int y = i / 3 * 18 - 4;
         widgets.add(Widgets.createSlot(new Point(startPoint.x + x, startPoint.y + y)).entries((Collection)display.getInputEntries().get(i)).markInput());
      }

      return widgets;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((CookingPotDisplay)var1, nullx);
   }
}
