package dev.terminalmc.clientsort.client.order;

import com.google.common.base.Objects;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StackMatcher {
   @NotNull
   public final Item item;
   @Nullable
   private final DataComponentMap components;

   private StackMatcher(@NotNull ItemStack stack, @Nullable DataComponentMap components) {
      this.item = stack.isEmpty() ? Items.AIR : stack.getItem();
      this.components = components;
   }

   public static StackMatcher of(@NotNull ItemStack stack) {
      return new StackMatcher(stack, stack.getComponents());
   }

   public static StackMatcher plain(@NotNull ItemStack stack) {
      return new StackMatcher(stack, null);
   }

   @Override
   public boolean equals(Object pObj) {
      if (pObj instanceof StackMatcher pMatcher) {
         return this.isSameItem(pMatcher.item) && this.hasAllEqualComponents(pMatcher.components);
      } else if (!(pObj instanceof ItemStack pStack)) {
         return pObj instanceof Item pItem ? this.isSameItem(pItem) : false;
      } else {
         return this.isSameItem(pStack) && this.hasAllEqualComponents(pStack);
      }
   }

   private boolean isSameItem(ItemStack stack) {
      return this.isSameItem(stack.getItem()) || this.item == Items.AIR && stack.isEmpty();
   }

   private boolean isSameItem(Item item) {
      return this.item == item;
   }

   private boolean hasAllEqualComponents(ItemStack stack) {
      if (this.components == null) {
         return true;
      } else {
         return this.item == Items.AIR && stack.isEmpty() ? true : this.hasAllEqualComponents(stack.getComponents());
      }
   }

   private boolean hasAllEqualComponents(DataComponentMap components) {
      if (this.components == null) {
         return true;
      }

      for (TypedDataComponent<?> component : this.components) {
         Object corresponding = components.get(component.type());
         if (!component.value().equals(corresponding)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(new Object[]{this.item});
   }
}
