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
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import net.satisfy.foundation.compat.RecipeViewerLayout.AssemblyLayout;
import net.satisfy.foundation.compat.RecipeViewerLayout.Pos;
import net.satisfy.foundation.compat.rei.ReiWidgets;

public class AssemblyCategory implements DisplayCategory<AssemblyDisplay> {
   private static final int PADDING = 5;

   public CategoryIdentifier<? extends AssemblyDisplay> getCategoryIdentifier() {
      return AssemblyDisplay.ID;
   }

   public Component getTitle() {
      return Component.translatable("category.farm_and_charm.assembly");
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ObjectRegistry.CUTTING_BOARD.get());
   }

   public int getDisplayWidth(AssemblyDisplay display) {
      return RecipeViewerLayout.assemblyWidth(3) + 10;
   }

   public int getDisplayHeight() {
      return 64;
   }

   public List<Widget> setupDisplay(AssemblyDisplay display, Rectangle bounds) {
      List<Widget> widgets = new ArrayList<>();
      widgets.add(Widgets.createRecipeBase(bounds));
      List<EntryIngredient> inputs = display.getInputEntries();
      AssemblyLayout layout = RecipeViewerLayout.assembly(inputs.size(), display.isOrdered(), 3);
      int x = bounds.x + 5;
      int y = bounds.y + 5;

      for (int i = 0; i < layout.slots().size(); i++) {
         Pos pos = (Pos)layout.slots().get(i);
         ReiWidgets.inputSlot(widgets, x + pos.x(), y + pos.y(), i < inputs.size() ? inputs.get(i) : EntryIngredient.empty());
      }

      for (Pos pos : layout.arrows()) {
         ReiWidgets.rightArrow(widgets, x + pos.x(), y + pos.y());
      }

      ReiWidgets.outputSlot(widgets, x + layout.output().x(), y + layout.output().y(), (EntryIngredient)display.getOutputEntries().getFirst());
      return widgets;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public int getDisplayWidth(Display param1) {
      return this.getDisplayWidth((AssemblyDisplay)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public List setupDisplay(Display param1, Rectangle nullx) {
      return this.setupDisplay((AssemblyDisplay)var1, nullx);
   }
}
