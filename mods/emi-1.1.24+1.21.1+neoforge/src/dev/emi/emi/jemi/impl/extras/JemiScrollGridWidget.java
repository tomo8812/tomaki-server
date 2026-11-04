package dev.emi.emi.jemi.impl.extras;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.placement.IPlaceable;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class JemiScrollGridWidget implements IScrollGridWidget {
   private static final int SCROLL_BAR_WIDTH = 18;
   public List<IRecipeSlotDrawable> slots;
   public int x;
   public int y;
   public int gridWidth;
   public int gridHeight;
   public int width;
   public int height;

   public JemiScrollGridWidget(List<IRecipeSlotDrawable> slots, int x, int y, int gridWidth, int gridHeight) {
      this.slots = slots;
      this.x = x;
      this.y = y;
      this.gridWidth = gridWidth;
      this.gridHeight = gridHeight;
      this.width = gridWidth * 18 + 18;
      this.height = gridHeight * 18;
   }

   public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
      return Optional.empty();
   }

   public ScreenPosition getPosition() {
      return new ScreenPosition(this.x, this.y);
   }

   public IScrollGridWidget setPosition(int xPos, int yPos) {
      this.x = xPos;
      this.y = yPos;
      return this;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public ScreenRectangle getScreenRectangle() {
      return new ScreenRectangle(this.getPosition(), this.getWidth(), this.getHeight());
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IPlaceable setPosition(int param1, int nullx) {
      return this.setPosition(var1, nullx);
   }
}
