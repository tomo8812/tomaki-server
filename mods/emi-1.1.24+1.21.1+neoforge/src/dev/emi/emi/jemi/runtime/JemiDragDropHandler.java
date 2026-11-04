package dev.emi.emi.jemi.runtime;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.jemi.JemiPlugin;
import dev.emi.emi.jemi.JemiUtil;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler.Target;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;

public class JemiDragDropHandler implements EmiDragDropHandler<Screen> {
   @Override
   public boolean dropStack(Screen screen, EmiIngredient stack, int x, int y) {
      try {
         return this.drop(screen, JemiUtil.getTyped(stack.getEmiStacks().get(0)), x, y);
      } catch (Exception e) {
         return false;
      }
   }

   @Override
   public void render(Screen screen, EmiIngredient dragged, GuiGraphics raw, int mouseX, int mouseY, float delta) {
      try {
         this.render(screen, EmiDrawContext.wrap(raw), JemiUtil.getTyped(dragged.getEmiStacks().get(0)));
      } catch (Exception var8) {
      }
   }

   private <I> boolean drop(Screen screen, Optional<ITypedIngredient<I>> optional, int x, int y) {
      if (optional.isPresent()) {
         for (Target<I> target : this.getTargets(screen, optional.get())) {
            if (target.getArea().contains(x, y)) {
               target.accept(optional.get().getIngredient());
               return true;
            }
         }
      }

      return false;
   }

   private <I> void render(Screen screen, EmiDrawContext context, Optional<ITypedIngredient<I>> optional) {
      if (optional.isPresent()) {
         for (Target<I> target : this.getTargets(screen, optional.get())) {
            Rect2i r = target.getArea();
            context.fill(r.getX(), r.getY(), r.getWidth(), r.getHeight(), -2010989773);
         }
      }
   }

   private <I> List<Target<I>> getTargets(Screen screen, ITypedIngredient<I> typed) {
      Optional<IGhostIngredientHandler<Screen>> optGhost = JemiPlugin.runtime.getScreenHelper().getGhostIngredientHandler(screen);
      if (optGhost.isPresent()) {
         IGhostIngredientHandler<Screen> ghost = optGhost.get();
         return ghost.getTargetsTyped(screen, typed, false);
      } else {
         return List.of();
      }
   }
}
