package dev.emi.emi.api.stack;

import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.runtime.EmiTagKey;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface EmiIngredient extends EmiRenderable {
   int RENDER_ICON = 1;
   int RENDER_AMOUNT = 2;
   int RENDER_INGREDIENT = 4;
   int RENDER_REMAINDER = 8;

   List<EmiStack> getEmiStacks();

   default boolean isEmpty() {
      for (EmiStack stack : this.getEmiStacks()) {
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   EmiIngredient copy();

   long getAmount();

   EmiIngredient setAmount(long var1);

   float getChance();

   EmiIngredient setChance(float var1);

   @Override
   default void render(GuiGraphics draw, int x, int y, float delta) {
      this.render(draw, x, y, delta, -1);
   }

   void render(GuiGraphics var1, int var2, int var3, float var4, int var5);

   List<ClientTooltipComponent> getTooltip();

   static boolean areEqual(EmiIngredient a, EmiIngredient b) {
      List<EmiStack> as = a.getEmiStacks();
      List<EmiStack> bs = b.getEmiStacks();
      if (as.size() != bs.size()) {
         return false;
      }

      for (int i = 0; i < as.size(); i++) {
         if (!as.get(i).isEqual(bs.get(i))) {
            return false;
         }
      }

      return true;
   }

   static <T> EmiIngredient of(TagKey<T> key) {
      return of(key, 1L);
   }

   static <T> EmiIngredient of(TagKey<T> key, long amount) {
      return of(EmiTags.getRawValues(EmiTagKey.of(key)), amount);
   }

   static EmiIngredient of(Ingredient ingredient) {
      if (ingredient != null && !ingredient.isEmpty()) {
         ItemStack[] stacks = ingredient.getItems();
         int amount = 1;
         if (stacks.length != 0) {
            amount = stacks[0].getCount();

            for (int i = 1; i < stacks.length; i++) {
               if (stacks[i].getCount() != amount) {
                  amount = 1;
                  break;
               }
            }
         }

         return of(ingredient, amount);
      } else {
         return EmiStack.EMPTY;
      }
   }

   static EmiIngredient of(Ingredient ingredient, long amount) {
      return ingredient != null && !ingredient.isEmpty()
         ? EmiTags.getIngredient(Item.class, Arrays.stream(ingredient.getItems()).map(EmiStack::of).toList(), amount)
         : EmiStack.EMPTY;
   }

   static EmiIngredient of(List<? extends EmiIngredient> list) {
      return of(list, 1L);
   }

   static EmiIngredient of(List<? extends EmiIngredient> list, long amount) {
      if (list.size() == 0) {
         return EmiStack.EMPTY;
      }

      if (list.size() == 1) {
         EmiIngredient stack = list.get(0);
         return stack.getAmount() < amount ? stack.copy().setAmount(amount) : stack;
      }

      long internalAmount = list.get(0).getAmount();

      for (EmiIngredient i : list) {
         if (i.getAmount() != internalAmount) {
            internalAmount = 1L;
         }
      }

      if (internalAmount > 1L) {
         amount = internalAmount;
         list = list.stream().map(st -> st.copy().setAmount(1L)).toList();
      }

      Class<?> tagType = null;

      for (EmiIngredient i : list) {
         for (EmiStack s : i.getEmiStacks()) {
            if (!s.isEmpty()) {
               if (tagType == null) {
                  tagType = EmiTags.ADAPTERS_BY_CLASS.getKey(s.getKey().getClass());
               }

               if (tagType == null || !tagType.isAssignableFrom(s.getKey().getClass())) {
                  return new ListEmiIngredient(list, amount);
               }
            }
         }
      }

      return EmiTags.getIngredient(tagType, list.stream().flatMap(ix -> ix.getEmiStacks().stream()).toList(), amount);
   }
}
