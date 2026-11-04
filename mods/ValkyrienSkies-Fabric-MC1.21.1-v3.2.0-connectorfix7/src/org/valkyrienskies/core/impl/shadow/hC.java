package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hC<T> implements Serializable, fQ<T, String> {
   private static final long a = 7511110693171758606L;
   private static final fQ<Object, String> b = new hC<>();

   public static <T> fQ<T, String> a() {
      return (fQ<T, String>)b;
   }

   private hC() {
   }

   private static String b(T var0) {
      return String.valueOf(var0);
   }

   private static fQ b() {
      return b;
   }

   // $VF: synthetic method
   @Override
   public final Object a(Object var1) {
      return String.valueOf(var1);
   }
}
