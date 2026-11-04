package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public final class jU<E> extends jR<E> implements Serializable {
   private static final long a = 20150610L;

   public jU() {
      super(new HashMap<>());
   }

   private jU(Collection<? extends E> var1) {
      this();
      this.addAll(var1);
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      super.a(var1);
   }

   private void b(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a(new HashMap<>());
      super.a(var1);
   }
}
