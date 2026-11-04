package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public final class ga<E> extends fW<E> implements Serializable {
   private static final long a = -6561115435802554013L;

   public ga() {
      super(new HashMap<>());
   }

   public ga(Collection<? extends E> var1) {
      this();
      this.addAll(var1);
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      super.a(var1);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a(new HashMap<>(), var1);
   }
}
