package org.embeddedt.modernfix.blockstate;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeStateMap<S> implements Map<Map<Property<?>, Comparable<?>>, S> {
   private final Map<Property<?>, Comparable<?>>[] keys;
   private Map<Map<Property<?>, Comparable<?>>, S> fastLookup;
   private final Object[] values;
   private int usedSlots;

   public FakeStateMap(int numStates) {
      this.keys = new Map[numStates];
      this.values = new Object[numStates];
      this.usedSlots = 0;
   }

   @Override
   public int size() {
      return this.usedSlots;
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public boolean containsKey(Object o) {
      return this.getFastLookup().containsKey(o);
   }

   @Override
   public boolean containsValue(Object o) {
      return this.getFastLookup().containsValue(o);
   }

   private Map<Map<Property<?>, Comparable<?>>, S> getFastLookup() {
      if (this.fastLookup == null) {
         Object2ObjectOpenHashMap<Map<Property<?>, Comparable<?>>, S> map = new Object2ObjectOpenHashMap(this.usedSlots);
         Map<Property<?>, Comparable<?>>[] keys = this.keys;
         Object[] values = this.values;

         for (int i = 0; i < this.usedSlots; i++) {
            map.put(keys[i], values[i]);
         }

         this.fastLookup = map;
      }

      return this.fastLookup;
   }

   @Override
   public S get(Object o) {
      return this.getFastLookup().get(o);
   }

   @Nullable
   public S put(Map<Property<?>, Comparable<?>> propertyComparableMap, S s) {
      if (this.fastLookup != null) {
         throw new IllegalStateException("Cannot populate map after fast lookup is built");
      }

      this.keys[this.usedSlots] = propertyComparableMap;
      this.values[this.usedSlots] = s;
      this.usedSlots++;
      return null;
   }

   @Override
   public S remove(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(@NotNull Map<? extends Map<Property<?>, Comparable<?>>, ? extends S> map) {
      for (Entry<? extends Map<Property<?>, Comparable<?>>, ? extends S> entry : map.entrySet()) {
         this.put((Map<Property<?>, Comparable<?>>)entry.getKey(), (S)entry.getValue());
      }
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.usedSlots; i++) {
         this.keys[i] = null;
         this.values[i] = null;
      }

      this.usedSlots = 0;
   }

   private <T> List<T> asList(T... array) {
      List<T> list = Arrays.asList(array);
      if (this.usedSlots < array.length) {
         list = list.subList(0, this.usedSlots);
      }

      return list;
   }

   @NotNull
   @Override
   public Set<Map<Property<?>, Comparable<?>>> keySet() {
      return new AbstractSet<Map<Property<?>, Comparable<?>>>() {
         @Override
         public Iterator<Map<Property<?>, Comparable<?>>> iterator() {
            return (Iterator<Map<Property<?>, Comparable<?>>>)(FakeStateMap.this.keys.length == FakeStateMap.this.usedSlots
               ? Iterators.forArray(FakeStateMap.this.keys)
               : FakeStateMap.this.asList(FakeStateMap.this.keys).iterator());
         }

         @Override
         public int size() {
            return FakeStateMap.this.usedSlots;
         }
      };
   }

   @NotNull
   @Override
   public Collection<S> values() {
      return this.asList((S[])this.values);
   }

   @NotNull
   @Override
   public Set<Entry<Map<Property<?>, Comparable<?>>, S>> entrySet() {
      return new AbstractSet<Entry<Map<Property<?>, Comparable<?>>, S>>() {
         @Override
         public int size() {
            return FakeStateMap.this.usedSlots;
         }

         @NotNull
         @Override
         public Iterator<Entry<Map<Property<?>, Comparable<?>>, S>> iterator() {
            return new Iterator<Entry<Map<Property<?>, Comparable<?>>, S>>() {
               int currentIdx = 0;

               @Override
               public boolean hasNext() {
                  return this.currentIdx < FakeStateMap.this.usedSlots;
               }

               public Entry<Map<Property<?>, Comparable<?>>, S> next() {
                  if (this.currentIdx >= FakeStateMap.this.usedSlots) {
                     throw new IndexOutOfBoundsException();
                  }

                  Entry<Map<Property<?>, Comparable<?>>, S> entry = new SimpleImmutableEntry<>(
                     FakeStateMap.this.keys[this.currentIdx], (S)FakeStateMap.this.values[this.currentIdx]
                  );
                  this.currentIdx++;
                  return entry;
               }

               // $VF: synthetic method
               // $VF: bridge method
               @Override
               public Object next() {
                  return this.next();
               }
            };
         }
      };
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object put(Object param1, Object nullx) {
      return this.put((Map<Property<?>, Comparable<?>>)var1, (S)nullx);
   }
}
