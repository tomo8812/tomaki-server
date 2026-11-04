package org.sinytra.adapter.env.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class OrderedRegistry<U> {
   private final List<U> instances = new ArrayList<>();
   private boolean frozen;

   public void freeze() {
      this.frozen = true;
   }

   public List<U> getAll() {
      return this.instances;
   }

   public OrderedRegistry<U> addBefore(Class<? extends U> type, U entry) {
      this.assertNotFrozen();
      this.assertUnique(entry);
      U original = this.getOrThrow((Class<U>)type);
      int index = this.instances.indexOf(original);
      this.instances.add(index, entry);
      return this;
   }

   public OrderedRegistry<U> addAfter(Class<? extends U> type, U entry) {
      this.assertNotFrozen();
      this.assertUnique(entry);
      U original = this.getOrThrow((Class<U>)type);
      int index = this.instances.indexOf(original);
      this.instances.add(index + 1, entry);
      return this;
   }

   public <T extends U> T getOrThrow(Class<T> type) {
      return Objects.requireNonNull(this.get(type), "Entry not found for type %s".formatted(type));
   }

   public void add(U entry) {
      this.assertNotFrozen();
      this.assertUnique(entry);
      this.instances.add(entry);
   }

   public OrderedRegistry<U> addFirst(U entry) {
      this.assertNotFrozen();
      this.assertUnique(entry);
      this.instances.addFirst(entry);
      return this;
   }

   @Nullable
   public <T extends U> T get(Class<T> type) {
      for (U entry : this.instances) {
         if (type == entry.getClass()) {
            return (T)entry;
         }
      }

      return null;
   }

   public void assertUnique(U entry) {
      if (this.get(entry.getClass()) != null) {
         throw new IllegalArgumentException("Duplicate entry for type %s".formatted(entry.getClass()));
      }
   }

   private void assertNotFrozen() {
      if (this.frozen) {
         throw new IllegalStateException("Already frozen");
      }
   }
}
