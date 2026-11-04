package dev.emi.emi.api.widget;

import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;

public class GeneratedSlotWidget extends SlotWidget {
   private static final int INCREMENT = 1000;
   private final Function<Random, EmiIngredient> stackSupplier;
   private final int unique;
   private long lastGenerate = 0L;
   private EmiIngredient stack = null;

   public GeneratedSlotWidget(Function<Random, EmiIngredient> stackSupplier, int unique, int x, int y) {
      super(EmiStack.EMPTY, x, y);
      this.stackSupplier = stackSupplier;
      this.unique = unique;
   }

   @Override
   public void drawOverlay(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      if (!this.getStack().isEmpty()) {
         int off = 1;
         if (this.output) {
            off = 5;
         }

         EmiRender.renderIngredientIcon(this.getStack(), context.raw(), this.x + off, this.y + off);
      }

      super.drawOverlay(context.raw(), mouseX, mouseY, delta);
   }

   @Override
   public EmiIngredient getStack() {
      long time = System.currentTimeMillis() / 1000L;
      if (this.stack == null || time > this.lastGenerate) {
         this.lastGenerate = time;
         this.stack = this.stackSupplier.apply(this.getRandom(time));
      }

      return this.stack;
   }

   private Random getRandom(long time) {
      return new Random(new Random(time ^ this.unique).nextInt());
   }
}
