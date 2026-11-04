package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;

public abstract class iO<E> extends iN<E> {
   private static final long a = 2684959196747496299L;

   protected iO(List<E> var1) {
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
