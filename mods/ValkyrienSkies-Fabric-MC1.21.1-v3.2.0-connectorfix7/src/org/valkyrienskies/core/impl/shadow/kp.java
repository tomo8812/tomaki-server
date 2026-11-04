package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Set;

public abstract class kp<E> extends kq<E> {
   private static final long a = 1229469966212206107L;

   protected kp(Set<E> var1) {
      super(var1);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(this.c());
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a((Collection<E>)var1.readObject());
   }
}
