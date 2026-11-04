package dev.emi.emi.api.stack;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.screen.tooltip.EmiTextTooltipWrapper;
import dev.emi.emi.screen.tooltip.IngredientTooltipComponent;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class ListEmiIngredient implements EmiIngredient {
   private final List<? extends EmiIngredient> ingredients;
   private final List<EmiStack> fullList;
   private long amount;
   private float chance = 1.0F;

   @Internal
   public ListEmiIngredient(List<? extends EmiIngredient> ingredients, long amount) {
      this.ingredients = ingredients;
      this.fullList = ingredients.stream().flatMap(i -> i.getEmiStacks().stream()).toList();
      if (this.fullList.isEmpty()) {
         throw new IllegalArgumentException("ListEmiIngredient cannot be empty");
      }

      this.amount = amount;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof ListEmiIngredient other ? other.getEmiStacks().equals(this.getEmiStacks()) : false;
   }

   @Override
   public int hashCode() {
      return this.fullList.hashCode();
   }

   @Override
   public EmiIngredient copy() {
      EmiIngredient stack = new ListEmiIngredient(this.ingredients, this.amount);
      stack.setChance(this.chance);
      return stack;
   }

   @Override
   public String toString() {
      return "Ingredient" + this.getEmiStacks();
   }

   @Override
   public List<EmiStack> getEmiStacks() {
      return this.fullList;
   }

   @Override
   public long getAmount() {
      return this.amount;
   }

   @Override
   public EmiIngredient setAmount(long amount) {
      this.amount = amount;
      return this;
   }

   @Override
   public float getChance() {
      return this.chance;
   }

   @Override
   public EmiIngredient setChance(float chance) {
      this.chance = chance;
      return this;
   }

   @Override
   public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
      int item = (int)(System.currentTimeMillis() / 1000L % this.ingredients.size());
      EmiIngredient current = this.ingredients.get(item);
      if ((flags & 1) != 0) {
         current.render(draw, x, y, delta, -3);
      }

      if ((flags & 2) != 0) {
         current.copy().setAmount(this.amount).render(draw, x, y, delta, 2);
      }

      if ((flags & 4) != 0) {
         EmiRender.renderIngredientIcon(this, draw, x, y);
      }
   }

   @Override
   public List<ClientTooltipComponent> getTooltip() {
      List<ClientTooltipComponent> tooltip = Lists.newArrayList();
      tooltip.add(new EmiTextTooltipWrapper(this, EmiPort.ordered(EmiPort.translatable("tooltip.emi.accepts"))));
      tooltip.add(new IngredientTooltipComponent(this.ingredients));
      int item = (int)(System.currentTimeMillis() / 1000L % this.ingredients.size());
      tooltip.addAll(this.ingredients.get(item).copy().setAmount(this.amount).getTooltip());
      return tooltip;
   }

   @Internal
   public List<? extends EmiIngredient> getIngredients() {
      return this.ingredients;
   }
}
