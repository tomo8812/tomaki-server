package org.embeddedt.modernfix.neoforge.recipe;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import org.embeddedt.modernfix.common.mixin.perf.ingredient_item_deduplication.PatchedDataComponentMapAccessor;

public class IngredientValueDeduplicator {
   private static final ObjectOpenCustomHashSet<ItemValue> VALUES = new ObjectOpenCustomHashSet(new Strategy<ItemValue>() {
      public int hashCode(ItemValue o) {
         if (o == null) {
            return 0;
         }

         ItemStack stack = o.item();
         int hash = 31 * stack.getItem().hashCode();
         if (stack.getComponents() instanceof PatchedDataComponentMapAccessor comps) {
            Reference2ObjectMap<DataComponentType<?>, Optional<?>> patch = comps.mfix$getPatch();
            ObjectIterator var6 = Reference2ObjectMaps.fastIterable(patch).iterator();

            while (var6.hasNext()) {
               Entry<DataComponentType<?>, Optional<?>> entry = (Entry<DataComponentType<?>, Optional<?>>)var6.next();
               int keyHash = System.identityHashCode(entry.getKey()) & 0xFF;
               Optional<?> value = (Optional<?>)entry.getValue();
               int valueHash = value.isPresent() ? System.identityHashCode(value.get()) : 0;
               hash += IntMath.pow(31, keyHash) * valueHash;
            }
         } else {
            hash += System.identityHashCode(stack.getComponents());
         }

         return hash;
      }

      private boolean areComponentsSame(ItemStack a, ItemStack b) {
         if (!(a.getComponents() instanceof PatchedDataComponentMapAccessor aComps && b.getComponents() instanceof PatchedDataComponentMapAccessor bComps)) {
            return a.getComponents() == b.getComponents();
         } else {
            if (!aComps.mfix$getPrototype().equals(bComps.mfix$getPrototype())) {
               return false;
            }

            Reference2ObjectMap<DataComponentType<?>, Optional<?>> aPatch = aComps.mfix$getPatch();
            Reference2ObjectMap<DataComponentType<?>, Optional<?>> bPatch = bComps.mfix$getPatch();
            if (aPatch != bPatch) {
               if (aPatch.size() != bPatch.size()) {
                  return false;
               }

               ObjectIterator var7 = Reference2ObjectMaps.fastIterable(aPatch).iterator();

               while (var7.hasNext()) {
                  Entry<DataComponentType<?>, Optional<?>> entry = (Entry<DataComponentType<?>, Optional<?>>)var7.next();
                  Optional<?> value = (Optional<?>)bPatch.get(entry.getKey());
                  if (value == null) {
                     return false;
                  }

                  if (value.isPresent() != ((Optional)entry.getValue()).isPresent()) {
                     return false;
                  }

                  if (value.isPresent() && value.get() != ((Optional)entry.getValue()).get()) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      private boolean areStacksSame(ItemStack a, ItemStack b) {
         if (!a.is(b.getItem())) {
            return false;
         } else if (a.isEmpty() != b.isEmpty()) {
            return false;
         } else {
            return !this.areComponentsSame(a, b) ? false : a.getCount() == b.getCount();
         }
      }

      public boolean equals(ItemValue a, ItemValue b) {
         return a == b || a != null && b != null && this.areStacksSame(a.item(), b.item());
      }

      // $VF: synthetic method
      // $VF: bridge method
      public boolean equals(Object param1, Object nullx) {
         return this.equals((ItemValue)var1, (ItemValue)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public int hashCode(Object param1) {
         return this.hashCode((ItemValue)var1);
      }
   });

   public static Value deduplicate(Value value) {
      if (value != null && value.getClass() == ItemValue.class) {
         synchronized (VALUES) {
            return (Value)VALUES.addOrGet((ItemValue)value);
         }
      } else {
         return value;
      }
   }
}
