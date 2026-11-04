package org.valkyrienskies.core.util.datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;
import org.joml.Vector3ic;
import org.valkyrienskies.core.api.util.functions.IntTernaryConsumer;

public interface IBlockPosSet extends Set<Vector3ic> {
   boolean add(int var1, int var2, int var3) throws IllegalArgumentException;

   boolean remove(int var1, int var2, int var3);

   boolean contains(int var1, int var2, int var3);

   boolean canStore(int var1, int var2, int var3);

   @Override
   void clear();

   default void forEach(@Nonnull IntTernaryConsumer action) {
      this.forEach(blockPos -> action.accept(blockPos.x(), blockPos.y(), blockPos.z()));
   }

   default void forEachUnsafe(@Nonnull IntTernaryConsumer action) {
      throw new UnsupportedOperationException();
   }

   default boolean add(@Nonnull Vector3ic pos) throws IllegalArgumentException {
      return this.add(pos.x(), pos.y(), pos.z());
   }

   default boolean remove(@Nonnull Vector3ic pos) {
      return this.remove(pos.x(), pos.y(), pos.z());
   }

   default boolean contains(@Nonnull Vector3ic pos) {
      return this.contains(pos.x(), pos.y(), pos.z());
   }

   default boolean canStore(@Nonnull Vector3ic pos) {
      return this.canStore(pos.x(), pos.y(), pos.z());
   }

   @Override
   default boolean containsAll(@Nonnull Collection<?> c) {
      for (Object o : c) {
         if (!this.contains(o)) {
            return false;
         }
      }

      return true;
   }

   @Override
   default boolean addAll(@Nonnull Collection<? extends Vector3ic> c) throws IllegalArgumentException {
      boolean modified = false;

      for (Vector3ic pos : c) {
         modified |= this.add(pos);
      }

      return modified;
   }

   @Override
   default boolean removeAll(@Nonnull Collection<?> c) {
      boolean modified = false;

      for (Object o : c) {
         modified |= this.remove(o);
      }

      return modified;
   }

   @Override
   default boolean retainAll(@Nonnull Collection<?> c) {
      boolean modified = false;

      for (Vector3ic pos : this) {
         if (!c.contains(pos)) {
            this.remove(pos);
            modified = true;
         }
      }

      return modified;
   }

   @Override
   default boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   default boolean remove(@Nonnull Object o) throws IllegalArgumentException {
      if (o instanceof Vector3ic) {
         return this.remove((Vector3ic)o);
      } else {
         throw new IllegalArgumentException("Object " + o + " is not a BlockPos!");
      }
   }

   @Override
   default boolean contains(@Nonnull Object o) {
      return o instanceof Vector3ic ? this.contains((Vector3ic)o) : false;
   }

   @Nonnull
   @Override
   default Object[] toArray() {
      Vector3ic[] arr = new Vector3ic[this.size()];
      Iterator<Vector3ic> iter = this.iterator();

      for (int i = 0; i < this.size(); i++) {
         arr[i] = iter.next();
      }

      return arr;
   }

   @Nonnull
   @Override
   default <T> T[] toArray(@Nonnull T[] a) {
      return (T[])this.toArray();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default boolean add(@Nonnull Object var1) {
      return this.add((Vector3ic)var1);
   }
}
