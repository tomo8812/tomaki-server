package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;

public final class jz<K, V> extends jf<K, V> implements Serializable {
   private static final long j = -1266190134568365852L;

   public jz() {
      super(jf.h.HARD, jf.h.SOFT, 16, 0.75F, false);
   }

   private jz(jf.h var1, jf.h var2) {
      super(var1, var2, 16, 0.75F, false);
   }

   private jz(jf.h var1, jf.h var2, boolean var3) {
      super(var1, var2, 16, 0.75F, var3);
   }

   private jz(jf.h var1, jf.h var2, int var3, float var4) {
      super(var1, var2, var3, var4, false);
   }

   private jz(jf.h var1, jf.h var2, int var3, float var4, boolean var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   protected final int b(Object var1) {
      return System.identityHashCode(var1);
   }

   @Override
   protected final int c(Object var1, Object var2) {
      return System.identityHashCode(var1) ^ System.identityHashCode(var2);
   }

   @Override
   protected final boolean a(Object var1, Object var2) {
      var2 = this.a(jf.h.HARD) ? var2 : ((Reference)var2).get();
      return var1 == var2;
   }

   @Override
   protected final boolean b(Object var1, Object var2) {
      return var1 == var2;
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
