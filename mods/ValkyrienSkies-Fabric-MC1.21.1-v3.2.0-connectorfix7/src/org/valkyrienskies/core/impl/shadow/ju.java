package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public final class ju<K, V> extends jd<iJ<? extends K>, V> implements Serializable, Cloneable {
   private static final long b = -1788199231038721040L;

   private static <K, V> ju<K, V> a(iZ<iJ<? extends K>, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("Map must not be null");
      } else if (var0.size() > 0) {
         throw new IllegalArgumentException("Map must be empty");
      } else {
         return new ju<>(var0);
      }
   }

   public ju() {
      this(new jo<>());
   }

   private ju(iZ<iJ<? extends K>, V> var1) {
      super(var1);
      super.a = var1;
   }

   private V a(Object var1, Object var2) {
      int var3 = d(var1, var2);
      iZ.c[] var10000 = ((iZ)super.i()).d;
      iZ var10001 = (iZ)super.i();

      for (iZ.c var4 = var10000[iZ.a(var3, ((iZ)super.i()).d.length)]; var4 != null; var4 = var4.a) {
         if (var4.b == var3 && a(var4, var1, var2)) {
            return (V)var4.getValue();
         }
      }

      return null;
   }

   private boolean b(Object var1, Object var2) {
      int var3 = d(var1, var2);
      iZ.c[] var10000 = ((iZ)super.i()).d;
      iZ var10001 = (iZ)super.i();

      for (iZ.c var4 = var10000[iZ.a(var3, ((iZ)super.i()).d.length)]; var4 != null; var4 = var4.a) {
         if (var4.b == var3 && a(var4, var1, var2)) {
            return true;
         }
      }

      return false;
   }

   private V a(K var1, K var2, V var3) {
      int var4 = d(var1, var2);
      iZ var10000 = (iZ)super.i();
      int var5 = iZ.a(var4, ((iZ)super.i()).d.length);

      for (iZ.c var6 = ((iZ)super.i()).d[var5]; var6 != null; var6 = var6.a) {
         if (var6.b == var4 && a(var6, var1, var2)) {
            var1 = var6.getValue();
            ((iZ)super.i()).a(var6, (V)var3);
            return (V)var1;
         }
      }

      ((iZ)super.i()).a(var5, var4, (K)(new iJ<Object>(var1, var2)), (V)var3);
      return null;
   }

   private V c(Object var1, Object var2) {
      int var3 = d(var1, var2);
      iZ var10000 = (iZ)super.i();
      int var4 = iZ.a(var3, ((iZ)super.i()).d.length);
      iZ.c var5 = ((iZ)super.i()).d[var4];
      iZ.c var6 = null;

      while (var5 != null) {
         if (var5.b == var3 && a(var5, var1, var2)) {
            var1 = var5.getValue();
            ((iZ)super.i()).a(var5, var4, var6);
            return (V)var1;
         }

         var6 = var5;
         var5 = var5.a;
      }

      return null;
   }

   private static int d(Object var0, Object var1) {
      int var2 = 0;
      if (var0 != null) {
         var2 = 0 ^ var0.hashCode();
      }

      if (var1 != null) {
         var2 ^= var1.hashCode();
      }

      int var3;
      int var4;
      int var5;
      return (var5 = (var4 = (var3 = var2 + ~(var2 << 9)) ^ var3 >>> 14) + (var4 << 4)) ^ var5 >>> 10;
   }

   private static boolean a(iZ.c<iJ<? extends K>, V> var0, Object var1, Object var2) {
      iJ var5;
      if ((var5 = (iJ)var0.getKey()).a.length == 2) {
         byte var4 = 0;
         if (var1 != var5.a[var4]) {
            if (var1 == null) {
               return false;
            }

            var4 = 0;
            if (!var1.equals(var5.a[var4])) {
               return false;
            }
         }

         var4 = 1;
         if (var2 == var5.a[var4]) {
            return true;
         }

         if (var2 != null) {
            var4 = 1;
            if (var2.equals(var5.a[var4])) {
               return true;
            }
         }
      }

      return false;
   }

   private V b(Object var1, Object var2, Object var3) {
      int var4 = e(var1, var2, var3);
      iZ.c[] var10000 = ((iZ)super.i()).d;
      iZ var10001 = (iZ)super.i();

      for (iZ.c var5 = var10000[iZ.a(var4, ((iZ)super.i()).d.length)]; var5 != null; var5 = var5.a) {
         if (var5.b == var4 && a(var5, var1, var2, var3)) {
            return (V)var5.getValue();
         }
      }

      return null;
   }

   private boolean c(Object var1, Object var2, Object var3) {
      int var4 = e(var1, var2, var3);
      iZ.c[] var10000 = ((iZ)super.i()).d;
      iZ var10001 = (iZ)super.i();

      for (iZ.c var5 = var10000[iZ.a(var4, ((iZ)super.i()).d.length)]; var5 != null; var5 = var5.a) {
         if (var5.b == var4 && a(var5, var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   private V a(K var1, K var2, K var3, V var4) {
      int var5 = e(var1, var2, var3);
      iZ var10000 = (iZ)super.i();
      int var6 = iZ.a(var5, ((iZ)super.i()).d.length);

      for (iZ.c var7 = ((iZ)super.i()).d[var6]; var7 != null; var7 = var7.a) {
         if (var7.b == var5 && a(var7, var1, var2, var3)) {
            var1 = var7.getValue();
            ((iZ)super.i()).a(var7, (V)var4);
            return (V)var1;
         }
      }

      ((iZ)super.i()).a(var6, var5, (K)(new iJ<Object>(var1, var2, var3)), (V)var4);
      return null;
   }

   private V d(Object var1, Object var2, Object var3) {
      int var4 = e(var1, var2, var3);
      iZ var10000 = (iZ)super.i();
      int var5 = iZ.a(var4, ((iZ)super.i()).d.length);
      iZ.c var6 = ((iZ)super.i()).d[var5];
      iZ.c var7 = null;

      while (var6 != null) {
         if (var6.b == var4 && a(var6, var1, var2, var3)) {
            var1 = var6.getValue();
            ((iZ)super.i()).a(var6, var5, var7);
            return (V)var1;
         }

         var7 = var6;
         var6 = var6.a;
      }

      return null;
   }

   private static int e(Object var0, Object var1, Object var2) {
      int var3 = 0;
      if (var0 != null) {
         var3 = 0 ^ var0.hashCode();
      }

      if (var1 != null) {
         var3 ^= var1.hashCode();
      }

      if (var2 != null) {
         var3 ^= var2.hashCode();
      }

      int var4;
      int var5;
      int var6;
      return (var6 = (var5 = (var4 = var3 + ~(var3 << 9)) ^ var4 >>> 14) + (var5 << 4)) ^ var6 >>> 10;
   }

   private static boolean a(iZ.c<iJ<? extends K>, V> var0, Object var1, Object var2, Object var3) {
      iJ var6;
      if ((var6 = (iJ)var0.getKey()).a.length == 3) {
         byte var5 = 0;
         if (var1 != var6.a[var5]) {
            if (var1 == null) {
               return false;
            }

            var5 = 0;
            if (!var1.equals(var6.a[var5])) {
               return false;
            }
         }

         var5 = 1;
         if (var2 != var6.a[var5]) {
            if (var2 == null) {
               return false;
            }

            var5 = 1;
            if (!var2.equals(var6.a[var5])) {
               return false;
            }
         }

         var5 = 2;
         if (var3 == var6.a[var5]) {
            return true;
         }

         if (var3 != null) {
            var5 = 2;
            if (var3.equals(var6.a[var5])) {
               return true;
            }
         }
      }

      return false;
   }

   private V b(Object var1, Object var2, Object var3, Object var4) {
      int var5 = e(var1, var2, var3, var4);
      iZ.c[] var10000 = ((iZ)super.i()).d;
      iZ var10001 = (iZ)super.i();

      for (iZ.c var6 = var10000[iZ.a(var5, ((iZ)super.i()).d.length)]; var6 != null; var6 = var6.a) {
         if (var6.b == var5 && a(var6, var1, var2, var3, var4)) {
            return (V)var6.getValue();
         }
      }

      return null;
   }

   private boolean c(Object var1, Object var2, Object var3, Object var4) {
      int var5 = e(var1, var2, var3, var4);
      iZ.c[] var10000 = ((iZ)super.i()).d;
      iZ var10001 = (iZ)super.i();

      for (iZ.c var6 = var10000[iZ.a(var5, ((iZ)super.i()).d.length)]; var6 != null; var6 = var6.a) {
         if (var6.b == var5 && a(var6, var1, var2, var3, var4)) {
            return true;
         }
      }

      return false;
   }

   private V a(K var1, K var2, K var3, K var4, V var5) {
      int var6 = e(var1, var2, var3, var4);
      iZ var10000 = (iZ)super.i();
      int var7 = iZ.a(var6, ((iZ)super.i()).d.length);

      for (iZ.c var8 = ((iZ)super.i()).d[var7]; var8 != null; var8 = var8.a) {
         if (var8.b == var6 && a(var8, var1, var2, var3, var4)) {
            var1 = var8.getValue();
            ((iZ)super.i()).a(var8, (V)var5);
            return (V)var1;
         }
      }

      ((iZ)super.i()).a(var7, var6, (K)(new iJ<Object>(var1, var2, var3, var4)), (V)var5);
      return null;
   }

   private V d(Object var1, Object var2, Object var3, Object var4) {
      int var5 = e(var1, var2, var3, var4);
      iZ var10000 = (iZ)super.i();
      int var6 = iZ.a(var5, ((iZ)super.i()).d.length);
      iZ.c var7 = ((iZ)super.i()).d[var6];
      iZ.c var8 = null;

      while (var7 != null) {
         if (var7.b == var5 && a(var7, var1, var2, var3, var4)) {
            var1 = var7.getValue();
            ((iZ)super.i()).a(var7, var6, var8);
            return (V)var1;
         }

         var8 = var7;
         var7 = var7.a;
      }

      return null;
   }

   private static int e(Object var0, Object var1, Object var2, Object var3) {
      int var4 = 0;
      if (var0 != null) {
         var4 = 0 ^ var0.hashCode();
      }

      if (var1 != null) {
         var4 ^= var1.hashCode();
      }

      if (var2 != null) {
         var4 ^= var2.hashCode();
      }

      if (var3 != null) {
         var4 ^= var3.hashCode();
      }

      int var5;
      int var6;
      int var7;
      return (var7 = (var6 = (var5 = var4 + ~(var4 << 9)) ^ var5 >>> 14) + (var6 << 4)) ^ var7 >>> 10;
   }

   private static boolean a(iZ.c<iJ<? extends K>, V> var0, Object var1, Object var2, Object var3, Object var4) {
      iJ var7;
      if ((var7 = (iJ)var0.getKey()).a.length == 4) {
         byte var6 = 0;
         if (var1 != var7.a[var6]) {
            if (var1 == null) {
               return false;
            }

            var6 = 0;
            if (!var1.equals(var7.a[var6])) {
               return false;
            }
         }

         var6 = 1;
         if (var2 != var7.a[var6]) {
            if (var2 == null) {
               return false;
            }

            var6 = 1;
            if (!var2.equals(var7.a[var6])) {
               return false;
            }
         }

         var6 = 2;
         if (var3 != var7.a[var6]) {
            if (var3 == null) {
               return false;
            }

            var6 = 2;
            if (!var3.equals(var7.a[var6])) {
               return false;
            }
         }

         var6 = 3;
         if (var4 == var7.a[var6]) {
            return true;
         }

         if (var4 != null) {
            var6 = 3;
            if (var4.equals(var7.a[var6])) {
               return true;
            }
         }
      }

      return false;
   }

   private V b(Object var1, Object var2, Object var3, Object var4, Object var5) {
      int var6 = e(var1, var2, var3, var4, var5);
      iZ.c[] var10000 = ((iZ)super.i()).d;
      iZ var10001 = (iZ)super.i();

      for (iZ.c var7 = var10000[iZ.a(var6, ((iZ)super.i()).d.length)]; var7 != null; var7 = var7.a) {
         if (var7.b == var6 && a(var7, var1, var2, var3, var4, var5)) {
            return (V)var7.getValue();
         }
      }

      return null;
   }

   private boolean c(Object var1, Object var2, Object var3, Object var4, Object var5) {
      int var6 = e(var1, var2, var3, var4, var5);
      iZ.c[] var10000 = ((iZ)super.i()).d;
      iZ var10001 = (iZ)super.i();

      for (iZ.c var7 = var10000[iZ.a(var6, ((iZ)super.i()).d.length)]; var7 != null; var7 = var7.a) {
         if (var7.b == var6 && a(var7, var1, var2, var3, var4, var5)) {
            return true;
         }
      }

      return false;
   }

   private V a(K var1, K var2, K var3, K var4, K var5, V var6) {
      int var7 = e(var1, var2, var3, var4, var5);
      iZ var10000 = (iZ)super.i();
      int var8 = iZ.a(var7, ((iZ)super.i()).d.length);

      for (iZ.c var9 = ((iZ)super.i()).d[var8]; var9 != null; var9 = var9.a) {
         if (var9.b == var7 && a(var9, var1, var2, var3, var4, var5)) {
            var1 = var9.getValue();
            ((iZ)super.i()).a(var9, (V)var6);
            return (V)var1;
         }
      }

      ((iZ)super.i()).a(var8, var7, (K)(new iJ<Object>(var1, var2, var3, var4, var5)), (V)var6);
      return null;
   }

   private V d(Object var1, Object var2, Object var3, Object var4, Object var5) {
      int var6 = e(var1, var2, var3, var4, var5);
      iZ var10000 = (iZ)super.i();
      int var7 = iZ.a(var6, ((iZ)super.i()).d.length);
      iZ.c var8 = ((iZ)super.i()).d[var7];
      iZ.c var9 = null;

      while (var8 != null) {
         if (var8.b == var6 && a(var8, var1, var2, var3, var4, var5)) {
            var1 = var8.getValue();
            ((iZ)super.i()).a(var8, var7, var9);
            return (V)var1;
         }

         var9 = var8;
         var8 = var8.a;
      }

      return null;
   }

   private static int e(Object var0, Object var1, Object var2, Object var3, Object var4) {
      int var5 = 0;
      if (var0 != null) {
         var5 = 0 ^ var0.hashCode();
      }

      if (var1 != null) {
         var5 ^= var1.hashCode();
      }

      if (var2 != null) {
         var5 ^= var2.hashCode();
      }

      if (var3 != null) {
         var5 ^= var3.hashCode();
      }

      if (var4 != null) {
         var5 ^= var4.hashCode();
      }

      int var6;
      int var7;
      int var8;
      return (var8 = (var7 = (var6 = var5 + ~(var5 << 9)) ^ var6 >>> 14) + (var7 << 4)) ^ var8 >>> 10;
   }

   private static boolean a(iZ.c<iJ<? extends K>, V> var0, Object var1, Object var2, Object var3, Object var4, Object var5) {
      iJ var8;
      if ((var8 = (iJ)var0.getKey()).a.length == 5) {
         byte var7 = 0;
         if (var1 != var8.a[var7]) {
            if (var1 == null) {
               return false;
            }

            var7 = 0;
            if (!var1.equals(var8.a[var7])) {
               return false;
            }
         }

         var7 = 1;
         if (var2 != var8.a[var7]) {
            if (var2 == null) {
               return false;
            }

            var7 = 1;
            if (!var2.equals(var8.a[var7])) {
               return false;
            }
         }

         var7 = 2;
         if (var3 != var8.a[var7]) {
            if (var3 == null) {
               return false;
            }

            var7 = 2;
            if (!var3.equals(var8.a[var7])) {
               return false;
            }
         }

         var7 = 3;
         if (var4 != var8.a[var7]) {
            if (var4 == null) {
               return false;
            }

            var7 = 3;
            if (!var4.equals(var8.a[var7])) {
               return false;
            }
         }

         var7 = 4;
         if (var5 == var8.a[var7]) {
            return true;
         }

         if (var5 != null) {
            var7 = 4;
            if (var5.equals(var8.a[var7])) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean a(Object var1) {
      boolean var2 = false;
      fu var3 = ((iZ)super.i()).c();

      while (var3.hasNext()) {
         iJ var4;
         if ((var4 = (iJ)var3.next()).a.length > 0) {
            if (var1 == null) {
               byte var6 = 0;
               if (var4.a[var6] != null) {
                  continue;
               }
            } else {
               byte var7 = 0;
               if (!var1.equals(var4.a[var7])) {
                  continue;
               }
            }

            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   private boolean e(Object var1, Object var2) {
      boolean var3 = false;
      fu var4 = ((iZ)super.i()).c();

      while (var4.hasNext()) {
         iJ var5;
         if ((var5 = (iJ)var4.next()).a.length >= 2) {
            if (var1 == null) {
               byte var7 = 0;
               if (var5.a[var7] != null) {
                  continue;
               }
            } else {
               byte var8 = 0;
               if (!var1.equals(var5.a[var8])) {
                  continue;
               }
            }

            if (var2 == null) {
               byte var9 = 1;
               if (var5.a[var9] != null) {
                  continue;
               }
            } else {
               byte var10 = 1;
               if (!var2.equals(var5.a[var10])) {
                  continue;
               }
            }

            var4.remove();
            var3 = true;
         }
      }

      return var3;
   }

   private boolean f(Object var1, Object var2, Object var3) {
      boolean var4 = false;
      fu var5 = ((iZ)super.i()).c();

      while (var5.hasNext()) {
         iJ var6;
         if ((var6 = (iJ)var5.next()).a.length >= 3) {
            if (var1 == null) {
               byte var8 = 0;
               if (var6.a[var8] != null) {
                  continue;
               }
            } else {
               byte var9 = 0;
               if (!var1.equals(var6.a[var9])) {
                  continue;
               }
            }

            if (var2 == null) {
               byte var10 = 1;
               if (var6.a[var10] != null) {
                  continue;
               }
            } else {
               byte var11 = 1;
               if (!var2.equals(var6.a[var11])) {
                  continue;
               }
            }

            if (var3 == null) {
               byte var12 = 2;
               if (var6.a[var12] != null) {
                  continue;
               }
            } else {
               byte var13 = 2;
               if (!var3.equals(var6.a[var13])) {
                  continue;
               }
            }

            var5.remove();
            var4 = true;
         }
      }

      return var4;
   }

   private boolean f(Object var1, Object var2, Object var3, Object var4) {
      boolean var5 = false;
      fu var6 = ((iZ)super.i()).c();

      while (var6.hasNext()) {
         iJ var7;
         if ((var7 = (iJ)var6.next()).a.length >= 4) {
            if (var1 == null) {
               byte var9 = 0;
               if (var7.a[var9] != null) {
                  continue;
               }
            } else {
               byte var10 = 0;
               if (!var1.equals(var7.a[var10])) {
                  continue;
               }
            }

            if (var2 == null) {
               byte var11 = 1;
               if (var7.a[var11] != null) {
                  continue;
               }
            } else {
               byte var12 = 1;
               if (!var2.equals(var7.a[var12])) {
                  continue;
               }
            }

            if (var3 == null) {
               byte var13 = 2;
               if (var7.a[var13] != null) {
                  continue;
               }
            } else {
               byte var14 = 2;
               if (!var3.equals(var7.a[var14])) {
                  continue;
               }
            }

            if (var4 == null) {
               byte var15 = 3;
               if (var7.a[var15] != null) {
                  continue;
               }
            } else {
               byte var16 = 3;
               if (!var4.equals(var7.a[var16])) {
                  continue;
               }
            }

            var6.remove();
            var5 = true;
         }
      }

      return var5;
   }

   private static void a(iJ<?> var0) {
      if (var0 == null) {
         throw new NullPointerException("Key must not be null");
      }
   }

   private ju<K, V> a() {
      try {
         return (ju<K, V>)super.clone();
      } catch (CloneNotSupportedException var1) {
         throw new InternalError();
      }
   }

   private V a(iJ<? extends K> var1, V var2) {
      a(var1);
      return super.put((K)var1, (V)var2);
   }

   @Override
   public final void putAll(Map<? extends iJ<? extends K>, ? extends V> mapToCopy) {
      Iterator var2 = mapToCopy.keySet().iterator();

      while (var2.hasNext()) {
         a((iJ<?>)var2.next());
      }

      super.putAll(mapToCopy);
   }

   @Override
   public final fu<iJ<? extends K>, V> c() {
      return ((iZ)super.i()).c();
   }

   private iZ<iJ<? extends K>, V> b() {
      return (iZ<iJ<? extends K>, V>)super.i();
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = (Map<K, V>)var1.readObject();
   }

   // $VF: synthetic method
   @Override
   public final Object put(Object var1, Object var2) {
      iJ var10001 = (iJ)var1;
      Object var3 = var2;
      var2 = var10001;
      ju var4 = this;
      a(var2);
      return var4.put(var2, var3);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final Map i() {
      return super.i();
   }

   // $VF: synthetic method
   @Override
   public final Object clone() {
      return this.a();
   }
}
