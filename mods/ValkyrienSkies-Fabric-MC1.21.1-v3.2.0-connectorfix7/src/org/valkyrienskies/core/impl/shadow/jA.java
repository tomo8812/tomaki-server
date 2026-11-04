package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class jA<K, V> extends jf<K, V> implements Serializable {
   private static final long j = 1555089888138299607L;

   public jA() {
      super(jf.h.HARD, jf.h.SOFT, 16, 0.75F, false);
   }

   private jA(jf.h var1, jf.h var2) {
      super(var1, var2, 16, 0.75F, false);
   }

   private jA(jf.h var1, jf.h var2, boolean var3) {
      super(var1, var2, 16, 0.75F, var3);
   }

   private jA(jf.h var1, jf.h var2, int var3, float var4) {
      super(var1, var2, var3, var4, false);
   }

   private jA(jf.h var1, jf.h var2, int var3, float var4, boolean var5) {
      super(var1, var2, var3, var4, var5);
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      this.a(var1);
   }

   private void b(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a(var1);
   }
}
