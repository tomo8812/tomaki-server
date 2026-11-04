package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public final class jp<K, V> extends jc<K, V> implements Serializable, Cloneable, fa<K, V> {
   private static final long k = -612114643488955218L;
   private static int l;
   private transient int m;
   private boolean n;

   public jp() {
      this(100, 0.75F, false);
   }

   private jp(int var1) {
      this(var1, 0.75F);
   }

   private jp(int var1, int var2) {
      this(var1, var2, 0.75F);
   }

   private jp(int var1, boolean var2) {
      this(var1, 0.75F, var2);
   }

   private jp(int var1, float var2) {
      this(var1, 0.75F, false);
   }

   private jp(int var1, int var2, float var3) {
      this(var1, var2, 0.75F, false);
   }

   private jp(int var1, float var2, boolean var3) {
      this(var1, var1, var2, var3);
   }

   private jp(int var1, int var2, float var3, boolean var4) {
      super(var2, var3);
      if (var1 <= 0) {
         throw new IllegalArgumentException("LRUMap max size must be greater than 0");
      }

      if (var2 > var1) {
         throw new IllegalArgumentException("LRUMap initial size must not be greather than max size");
      }

      this.m = var1;
      this.n = var4;
   }

   private jp(Map<? extends K, ? extends V> var1) {
      this(var1, false);
   }

   private jp(Map<? extends K, ? extends V> var1, boolean var2) {
      this(var1.size(), 0.75F, false);
      this.putAll(var1);
   }

   @Override
   public final V get(Object key) {
      boolean var3 = true;
      jc.c var2 = (jc.c)key;
      key = this;
      if ((var2 = this.f(var2)) == null) {
         return null;
      }

      ((jp)key).a(var2);
      return (V)var2.getValue();
   }

   private V a(Object var1, boolean var2) {
      if ((var1 = this.f(var1)) == null) {
         return null;
      }

      this.a(var1);
      return (V)var1.getValue();
   }

   private void a(jc.c<K, V> var1) {
      if (var1.f != super.j) {
         super.f++;
         if (var1.e == null) {
            throw new IllegalStateException(
               "Entry.before is null. Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug."
            );
         }

         var1.e.f = var1.f;
         var1.f.e = var1.e;
         var1.f = super.j;
         var1.e = super.j.e;
         super.j.e.f = var1;
         super.j.e = var1;
      } else {
         if (var1 == super.j) {
            throw new IllegalStateException("Can't move header to MRU (please report this to dev@commons.apache.org)");
         }
      }
   }

   @Override
   protected final void a(iZ.c<K, V> var1, V var2) {
      this.a((jc.c<K, V>)var1);
      var1.setValue(var2);
   }

   @Override
   protected final void a(int var1, int var2, K var3, V var4) {
      jp var5 = this;
      if (super.c >= var5.m) {
         jc.c var15 = super.j.f;
         boolean var6 = false;
         if (this.n) {
            if (var15 != super.j && var15 != null) {
               var6 = true;
            }

            if (var15 == null) {
               throw new IllegalStateException(
                  "Entry.after=null, header.after"
                     + super.j.f
                     + " header.before"
                     + super.j.e
                     + " key="
                     + var3
                     + " value="
                     + var4
                     + " size="
                     + super.c
                     + " maxSize="
                     + this.m
                     + " Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug."
               );
            }
         } else {
            var6 = true;
         }

         if (var6) {
            if (var15 == null) {
               throw new IllegalStateException(
                  "reuse=null, header.after="
                     + super.j.f
                     + " header.before"
                     + super.j.e
                     + " key="
                     + var3
                     + " value="
                     + var4
                     + " size="
                     + super.c
                     + " maxSize="
                     + this.m
                     + " Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug."
               );
            }

            Object var17 = var4;
            var4 = var3;
            var3 = var2;
            var2 = var1;
            jc.c var11 = var15;
            var5 = this;

            try {
               int var8 = var5.d.length;
               int var7 = var11.b;
               var7 = var11.b & var8 - 1;
               iZ.c[] var19 = var5.d;
               iZ.c var20 = var5.d[var7];
               iZ.c var9 = null;

               while (var20 != var11 && var20 != null) {
                  var9 = var20;
                  var20 = var20.a;
               }

               if (var20 == null) {
                  throw new IllegalStateException(
                     "Entry.next=null, data[removeIndex]="
                        + var5.d[var7]
                        + " previous="
                        + var9
                        + " key="
                        + var4
                        + " value="
                        + var17
                        + " size="
                        + var5.c
                        + " maxSize="
                        + var5.m
                        + " Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug."
                  );
               }

               var5.f++;
               var5.b(var11, var7, var9);
               var5.a(var11, var2, var3, (K)var4, (V)var17);
               var5.a(var11, var2);
            } catch (NullPointerException var10) {
               throw new IllegalStateException(
                  "NPE, entry="
                     + var11
                     + " entryIsHeader="
                     + (var11 == var5.j)
                     + " key="
                     + var4
                     + " value="
                     + var17
                     + " size="
                     + var5.c
                     + " maxSize="
                     + var5.m
                     + " Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug."
               );
            }
         } else {
            super.a(var1, var2, (K)var3, (V)var4);
         }
      } else {
         super.a(var1, var2, (K)var3, (V)var4);
      }
   }

   private void b(jc.c<K, V> var1, int var2, int var3, K var4, V var5) {
      try {
         int var7 = super.d.length;
         int var6 = var1.b;
         var6 = var1.b & var7 - 1;
         iZ.c[] var11 = super.d;
         iZ.c var12 = super.d[var6];
         iZ.c var8 = null;

         while (var12 != var1 && var12 != null) {
            var8 = var12;
            var12 = var12.a;
         }

         if (var12 == null) {
            throw new IllegalStateException(
               "Entry.next=null, data[removeIndex]="
                  + super.d[var6]
                  + " previous="
                  + var8
                  + " key="
                  + var4
                  + " value="
                  + var5
                  + " size="
                  + super.c
                  + " maxSize="
                  + this.m
                  + " Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug."
            );
         }

         super.f++;
         this.b(var1, var6, var8);
         this.a(var1, var2, var3, (K)var4, (V)var5);
         this.a(var1, var2);
      } catch (NullPointerException var9) {
         throw new IllegalStateException(
            "NPE, entry="
               + var1
               + " entryIsHeader="
               + (var1 == super.j)
               + " key="
               + var4
               + " value="
               + var5
               + " size="
               + super.c
               + " maxSize="
               + this.m
               + " Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug."
         );
      }
   }

   private static boolean j() {
      return true;
   }

   @Override
   public final boolean a() {
      return super.c >= this.m;
   }

   @Override
   public final int b() {
      return this.m;
   }

   private boolean k() {
      return this.n;
   }

   private jp<K, V> l() {
      return (jp<K, V>)super.i();
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      this.a(var1);
   }

   private void b(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a(var1);
   }

   @Override
   protected final void a(ObjectOutputStream var1) {
      var1.writeInt(this.m);
      super.a(var1);
   }

   @Override
   protected final void a(ObjectInputStream var1) {
      this.m = var1.readInt();
      super.a(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final iZ i() {
      return super.i();
   }

   // $VF: synthetic method
   @Override
   public final Object clone() {
      return super.i();
   }
}
