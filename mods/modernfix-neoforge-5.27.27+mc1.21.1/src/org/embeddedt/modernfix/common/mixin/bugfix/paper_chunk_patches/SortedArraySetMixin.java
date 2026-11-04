package org.embeddedt.modernfix.common.mixin.bugfix.paper_chunk_patches;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.function.Predicate;
import net.minecraft.util.SortedArraySet;
import org.embeddedt.modernfix.annotation.RequiresMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SortedArraySet.class)
@RequiresMod("!moonrise")
public abstract class SortedArraySetMixin<T> extends AbstractSet<T> {
   @Shadow
   private int size;
   @Shadow
   private T[] contents;

   @Override
   public boolean removeIf(Predicate<? super T> filter) {
      int i = 0;
      int len = this.size;
      T[] backingArray = this.contents;

      while (i < len) {
         if (filter.test(backingArray[i])) {
            int lastIndex = i;

            while (i < len) {
               T curr = backingArray[i];
               if (!filter.test(curr)) {
                  backingArray[lastIndex++] = curr;
               }

               i++;
            }

            Arrays.fill(backingArray, lastIndex, len, null);
            this.size = lastIndex;
            return true;
         }

         i++;
      }

      return false;
   }
}
