package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class hV<E> implements Iterator<E> {
   private Comparator<? super E> a = null;
   private List<Iterator<? extends E>> b = null;
   private List<E> c = null;
   private BitSet d = null;
   private int e = -1;

   public hV() {
      this(null, 2);
   }

   private hV(Comparator<? super E> var1) {
      this(var1, 2);
   }

   private hV(Comparator<? super E> var1, int var2) {
      this.b = new ArrayList<>(var2);
      Comparator var4 = var1;
      hV var3 = this;
      this.e();
      var3.a = var4;
   }

   public hV(Comparator<? super E> var1, Iterator<? extends E> var2, Iterator<? extends E> var3) {
      this(var1, 2);
      this.a(var2);
      this.a(var3);
   }

   public hV(Comparator<? super E> var1, Iterator<? extends E>[] var2) {
      this(var1, var2.length);
      Iterator[] var5 = var2;
      int var6 = var2.length;

      for (int var3 = 0; var3 < var6; var3++) {
         Iterator var4 = var5[var3];
         this.a(var4);
      }
   }

   public hV(Comparator<? super E> var1, Collection<Iterator<? extends E>> var2) {
      this(var1, var2.size());

      for (Iterator var4 : var2) {
         this.a(var4);
      }
   }

   private void a(Iterator<? extends E> var1) {
      this.e();
      if (var1 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      this.b.add(var1);
   }

   private void a(int var1, Iterator<? extends E> var2) {
      this.e();
      if (var2 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      this.b.set(var1, var2);
   }

   private List<Iterator<? extends E>> a() {
      return iY.a(this.b);
   }

   private Comparator<? super E> b() {
      return this.a;
   }

   private void a(Comparator<? super E> var1) {
      this.e();
      this.a = var1;
   }

   @Override
   public final boolean hasNext() {
      hV var1 = this;
      if (this.c == null) {
         var1.c = new ArrayList<>(var1.b.size());
         var1.d = new BitSet(var1.b.size());

         for (int var2 = 0; var2 < var1.b.size(); var2++) {
            var1.c.add(null);
            var1.d.clear(var2);
         }
      }

      BitSet var3 = this.d;
      int var5 = 0;

      boolean var10000;
      while (true) {
         if (var5 >= var3.size()) {
            var10000 = false;
            break;
         }

         if (var3.get(var5)) {
            var10000 = true;
            break;
         }

         var5++;
      }

      if (!var10000) {
         List var4 = this.b;
         Iterator var6 = this.b.iterator();

         while (true) {
            if (!var6.hasNext()) {
               var10000 = false;
               break;
            }

            if (((Iterator)var6.next()).hasNext()) {
               var10000 = true;
               break;
            }
         }

         if (!var10000) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      hV var3 = this;
      int var4 = -1;
      int var1 = null;

      for (int var2 = 0; var2 < var3.c.size(); var2++) {
         if (!var3.d.get(var2)) {
            int var6 = var2;
            hV var5 = var3;
            Iterator var7;
            if ((var7 = var3.b.get(var6)).hasNext()) {
               var5.c.set(var6, (E)var7.next());
               var5.d.set(var6);
               boolean var10000 = true;
            } else {
               var5.c.set(var6, null);
               var5.d.clear(var6);
               boolean var13 = false;
            }
         }

         if (var3.d.get(var2)) {
            if (var4 == -1) {
               var4 = var2;
               var1 = (int)var3.c.get(var2);
            } else {
               Object var12 = var3.c.get(var2);
               if (var3.a == null) {
                  throw new NullPointerException("You must invoke setComparator() to set a comparator first.");
               }

               if (var3.a.compare((E)var12, (E)var1) < 0) {
                  var1 = (int)var12;
                  var4 = var2;
               }
            }
         }
      }

      var1 = var4;
      if (var4 == -1) {
         throw new NoSuchElementException();
      }

      Object var9 = this.c.get(var1);
      var4 = var1;
      var3 = this;
      this.c.set(var4, null);
      var3.d.clear(var4);
      this.e = var1;
      return (E)var9;
   }

   @Override
   public final void remove() {
      if (this.e == -1) {
         throw new IllegalStateException("No value can be removed at present");
      }

      this.b.get(this.e).remove();
   }

   private int c() {
      if (this.e == -1) {
         throw new IllegalStateException("No value has been returned yet");
      } else {
         return this.e;
      }
   }

   private void d() {
      if (this.c == null) {
         this.c = new ArrayList<>(this.b.size());
         this.d = new BitSet(this.b.size());

         for (int var1 = 0; var1 < this.b.size(); var1++) {
            this.c.add(null);
            this.d.clear(var1);
         }
      }
   }

   private boolean a(int var1) {
      Iterator var2;
      if ((var2 = this.b.get(var1)).hasNext()) {
         this.c.set(var1, (E)var2.next());
         this.d.set(var1);
         return true;
      } else {
         this.c.set(var1, null);
         this.d.clear(var1);
         return false;
      }
   }

   private void b(int var1) {
      this.c.set(var1, null);
      this.d.clear(var1);
   }

   private void e() {
      if (this.c != null) {
         throw new IllegalStateException("Can't do that after next or hasNext has been called.");
      }
   }

   private int f() {
      int var1 = -1;
      Object var2 = null;

      for (int var3 = 0; var3 < this.c.size(); var3++) {
         if (!this.d.get(var3)) {
            this.a(var3);
         }

         if (this.d.get(var3)) {
            if (var1 == -1) {
               var1 = var3;
               var2 = this.c.get(var3);
            } else {
               Object var4 = this.c.get(var3);
               if (this.a == null) {
                  throw new NullPointerException("You must invoke setComparator() to set a comparator first.");
               }

               if (this.a.compare((E)var4, (E)var2) < 0) {
                  var2 = var4;
                  var1 = var3;
               }
            }
         }
      }

      return var1;
   }

   private static boolean a(BitSet var0) {
      for (int var1 = 0; var1 < var0.size(); var1++) {
         if (var0.get(var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean a(List<Iterator<? extends E>> var0) {
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         if (((Iterator)var2.next()).hasNext()) {
            return true;
         }
      }

      return false;
   }
}
