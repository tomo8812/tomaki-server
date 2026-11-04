package org.embeddedt.modernfix.common.mixin.perf.faster_ingredients;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.embeddedt.modernfix.neoforge.load.MinecraftServerReloadTracker;
import org.embeddedt.modernfix.neoforge.recipe.ExtendedIngredient;
import org.embeddedt.modernfix.neoforge.recipe.IngredientItemStacksSoftReference;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Ingredient.class, priority = 700)
public abstract class IngredientMixin implements ExtendedIngredient {
   @Shadow
   @Final
   private Value[] values;
   @Shadow
   @Nullable
   private IntList stackingIds;
   @Shadow
   @Nullable
   private ItemStack[] itemStacks;
   @Shadow
   private ICustomIngredient customIngredient;
   private volatile IngredientItemStacksSoftReference mfix$cachedItemStacks;

   @Shadow
   public abstract boolean isCustom();

   @Unique
   private boolean isVanilla() {
      return !this.isCustom();
   }

   private boolean mfix$areTagsAvailable() {
      return !MinecraftServerReloadTracker.isReloadActive();
   }

   @Inject(
      method = "test(Lnet/minecraft/world/item/ItemStack;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;getItems()[Lnet/minecraft/world/item/ItemStack;"),
      cancellable = true
   )
   private void modernfix$fasterTagIngredientTest(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      if (this.isVanilla() && this.values.length == 1 && this.values[0] instanceof TagValue tagValue && this.mfix$areTagsAvailable()) {
         cir.setReturnValue(stack.getItemHolder().is(tagValue.tag()));
      }
   }

   @Inject(method = "hasNoItems", at = @At("HEAD"), cancellable = true, remap = false)
   public void hasNoItems(CallbackInfoReturnable<Boolean> cir) {
      if (this.isVanilla()) {
         cir.setReturnValue(!this.containsItems());
      }
   }

   @Unique
   private boolean isEmptyTagStack(ItemStack item) {
      return item.getItem() == Items.BARRIER && item.getHoverName() instanceof MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
   }

   @Unique
   private boolean containsItems() {
      for (Value value : this.values) {
         if (value instanceof ItemValue) {
            return true;
         }

         if (value instanceof TagValue tagValue && this.mfix$areTagsAvailable()) {
            Optional<Named<Item>> holderSetOpt = BuiltInRegistries.ITEM.getTag(tagValue.tag());
            if (holderSetOpt.isPresent() && holderSetOpt.get().size() > 0) {
               return true;
            }
         } else {
            Collection<ItemStack> items = value.getItems();
            if (!items.isEmpty() && !this.isEmptyTagStack(items.iterator().next())) {
               return true;
            }
         }
      }

      return false;
   }

   @Inject(
      method = "getStackingIds",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;getItems()[Lnet/minecraft/world/item/ItemStack;"),
      cancellable = true
   )
   private void modernfix$fasterTagIngredientStacking(CallbackInfoReturnable<IntList> cir) {
      if (this.isVanilla() && this.values.length == 1 && this.values[0] instanceof TagValue tagValue && this.mfix$areTagsAvailable()) {
         Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(tagValue.tag());
         if (!tag.isPresent() || tag.get().size() == 0) {
            return;
         }

         IntArrayList list = new IntArrayList(tag.get().stream().mapToInt(h -> BuiltInRegistries.ITEM.getId((Item)h.value())).toArray());
         list.sort(IntComparators.NATURAL_COMPARATOR);
         this.stackingIds = list;
         cir.setReturnValue(list);
      }
   }

   @Overwrite
   public ItemStack[] getItems() {
      if (this.itemStacks != null) {
         return this.itemStacks;
      }

      if (this.customIngredient != null) {
         this.itemStacks = this.customIngredient
            .getItems()
            .collect(Collectors.toCollection(ItemStackLinkedSet::createTypeAndComponentsSet))
            .toArray(ItemStack[]::new);
         return this.itemStacks;
      }

      IngredientItemStacksSoftReference cache = this.mfix$cachedItemStacks;
      if (cache != null) {
         ItemStack[] stacks = cache.get();
         if (stacks != null) {
            return stacks;
         }
      }

      IngredientItemStacksSoftReference.clearReferences();
      ItemStack[] result = this.computeItemsArray();
      this.mfix$cachedItemStacks = new IngredientItemStacksSoftReference((Ingredient)this, result);
      return result;
   }

   private ItemStack[] computeItemsArray() {
      if (this.values.length == 1 && this.values[0] instanceof TagValue tagValue && this.mfix$areTagsAvailable()) {
         Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(tagValue.tag());
         if (tag.isPresent() && tag.get().size() > 0) {
            Named<Item> holderSet = tag.get();
            ItemStack[] result = new ItemStack[holderSet.size()];

            for (int i = 0; i < result.length; i++) {
               result[i] = new ItemStack(holderSet.get(i));
            }

            return result;
         }
      }

      ArrayList<ItemStack> itemList = new ArrayList<>(2);

      for (Value value : this.values) {
         Collection<ItemStack> collection = value.getItems();
         itemList.ensureCapacity(collection.size() + itemList.size());

         for (ItemStack item : collection) {
            itemList.add(item);
         }
      }

      return itemList.toArray(ItemStack[]::new);
   }

   @Override
   public void mfix$clearReference() {
      this.mfix$cachedItemStacks = null;
   }
}
