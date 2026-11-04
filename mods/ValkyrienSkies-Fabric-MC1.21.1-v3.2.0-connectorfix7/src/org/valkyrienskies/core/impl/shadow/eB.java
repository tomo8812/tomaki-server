package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class eB<T> {
   private static final String a = "Set contributions cannot be null";
   private final List<T> b;

   private eB(int estimatedSize) {
      this.b = new ArrayList<>(estimatedSize);
   }

   private static <T> eB<T> a(int estimatedSize) {
      return new eB<>(estimatedSize);
   }

   private eB<T> a(T t) {
      this.b.add(ev.a((T)t, "Set contributions cannot be null"));
      return this;
   }

   private eB<T> a(Collection<? extends T> collection) {
      Iterator var2 = collection.iterator();

      while (var2.hasNext()) {
         ev.a(var2.next(), "Set contributions cannot be null");
      }

      this.b.addAll(collection);
      return this;
   }

   private Set<T> a() {
      if (this.b.isEmpty()) {
         return Collections.emptySet();
      } else {
         return this.b.size() == 1 ? Collections.singleton(this.b.get(0)) : Collections.unmodifiableSet(new HashSet<>(this.b));
      }
   }
}
