package net.caffeinemc.mods.lithium.common.ai;

import java.util.Iterator;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.entity.ai.behavior.ShufflingList.WeightedEntry;

public interface WeightedListIterable<U> extends Iterable<U> {
   @Override
   Iterator<U> iterator();

   static <T> Iterable<? extends T> cast(ShufflingList<T> list) {
      return (WeightedListIterable)list;
   }

   class ListIterator<U> implements Iterator<U> {
      private final Iterator<WeightedEntry<? extends U>> inner;

      public ListIterator(Iterator<WeightedEntry<? extends U>> inner) {
         this.inner = inner;
      }

      @Override
      public boolean hasNext() {
         return this.inner.hasNext();
      }

      @Override
      public U next() {
         return (U)this.inner.next().getData();
      }
   }
}
