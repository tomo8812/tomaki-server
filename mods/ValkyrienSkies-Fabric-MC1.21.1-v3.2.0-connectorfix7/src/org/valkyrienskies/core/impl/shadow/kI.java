package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Comparator;

public abstract class kI<K> implements Serializable, Comparator<K> {
   private static final long a = -20497563720380683L;
   private static int b;
   private static int c;
   private static int d;

   static boolean a(int var0) {
      return var0 == -3;
   }

   static boolean b(int var0) {
      return var0 == -2;
   }

   static boolean c(int var0) {
      return var0 == -1;
   }

   static boolean d(int var0) {
      return var0 >= 0;
   }

   public abstract int a();

   public abstract int a(K var1);

   public abstract boolean a(K var1, int var2, int var3);

   public abstract int a(K var1, int var2, int var3, K var4, int var5, int var6);

   public abstract boolean a(K var1, int var2, int var3, K var4);

   @Override
   public int compare(K o1, K o2) {
      if (o1 == null) {
         return o2 == null ? 0 : -1;
      } else {
         return o2 == null ? 1 : ((Comparable)o1).compareTo(o2);
      }
   }
}
