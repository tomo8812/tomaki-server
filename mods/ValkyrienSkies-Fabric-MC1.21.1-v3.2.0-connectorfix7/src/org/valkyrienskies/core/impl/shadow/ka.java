package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public final class ka<E> extends AbstractCollection<E> implements Serializable, Queue<E>, eZ<E> {
   private static final long a = -8423413834657610406L;
   private transient E[] b;
   private transient int c = 0;
   private transient int d = 0;
   private transient boolean e = false;
   private final int f;

   public ka() {
      this(32);
   }

   private ka(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("The size must be greater than 0");
      }

      this.b = (E[])(new Object[var1]);
      this.f = this.b.length;
   }

   private ka(Collection<? extends E> var1) {
      this(var1.size());
      this.addAll(var1);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeInt(this.size());

      for (Object var3 : this) {
         var1.writeObject(var3);
      }
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.b = (E[])(new Object[this.f]);
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         this.b[var3] = (E)var1.readObject();
      }

      this.c = 0;
      this.e = var2 == this.f;
      if (this.e) {
         this.d = 0;
      } else {
         this.d = var2;
      }
   }

   @Override
   public final int size() {
      int var1;
      if (this.d < this.c) {
         var1 = this.f - this.c + this.d;
      } else if (this.d == this.c) {
         var1 = this.e ? this.f : 0;
      } else {
         var1 = this.d - this.c;
      }

      return var1;
   }

   @Override
   public final boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public final boolean a() {
      return false;
   }

   private boolean c() {
      return this.size() == this.f;
   }

   @Override
   public final int b() {
      return this.f;
   }

   @Override
   public final void clear() {
      this.e = false;
      this.c = 0;
      this.d = 0;
      Arrays.fill(this.b, null);
   }

   @Override
   public final boolean add(E element) {
      if (element == null) {
         throw new NullPointerException("Attempted to add null object to queue");
      }

      ka var2 = this;
      if (this.size() == var2.f) {
         this.remove();
      }

      this.b[this.d++] = element;
      if (this.d >= this.f) {
         this.d = 0;
      }

      if (this.d == this.c) {
         this.e = true;
      }

      return true;
   }

   private E a(int var1) {
      int var2 = this.size();
      if (var1 >= 0 && var1 < var2) {
         var1 = (this.c + var1) % this.f;
         return this.b[var1];
      } else {
         throw new NoSuchElementException(String.format("The specified index (%1$d) is outside the available range [0, %2$d)", var1, var2));
      }
   }

   @Override
   public final boolean offer(E element) {
      return this.add(element);
   }

   @Override
   public final E poll() {
      return this.isEmpty() ? null : this.remove();
   }

   @Override
   public final E element() {
      if (this.isEmpty()) {
         throw new NoSuchElementException("queue is empty");
      } else {
         return this.peek();
      }
   }

   @Override
   public final E peek() {
      return this.isEmpty() ? null : this.b[this.c];
   }

   @Override
   public final E remove() {
      if (this.isEmpty()) {
         throw new NoSuchElementException("queue is empty");
      }

      Object var1 = this.b[this.c];
      if (var1 != null) {
         this.b[this.c++] = null;
         if (this.c >= this.f) {
            this.c = 0;
         }

         this.e = false;
      }

      return (E)var1;
   }

   private int b(int var1) {
      if (++var1 >= this.f) {
         var1 = 0;
      }

      return var1;
   }

   private int c(int var1) {
      if (--var1 < 0) {
         var1 = this.f - 1;
      }

      return var1;
   }

   @Override
   public final Iterator<E> iterator() {
      return new Iterator<E>() {
         private int a = ka.this.c;
         private int b = -1;
         private boolean c = ka.this.e;

         @Override
         public final boolean hasNext() {
            return this.c || this.a != ka.this.d;
         }

         @Override
         public final E next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            }

            this.c = false;
            this.b = this.a;
            this.a = ka.a(ka.this, this.a);
            return ka.this.b[this.b];
         }

         @Override
         public final void remove() {
            if (this.b == -1) {
               throw new IllegalStateException();
            }

            if (this.b == ka.this.c) {
               ka.this.remove();
               this.b = -1;
            } else {
               int var1 = this.b + 1;
               if (ka.this.c < this.b && var1 < ka.this.d) {
                  System.arraycopy(ka.this.b, var1, ka.this.b, this.b, ka.this.d - var1);
               } else {
                  while (var1 != ka.this.d) {
                     if (var1 >= ka.this.f) {
                        ka.this.b[var1 - 1] = ka.this.b[0];
                        var1 = 0;
                     } else {
                        ka.this.b[ka.b(ka.this, var1)] = ka.this.b[var1];
                        var1 = ka.a(ka.this, var1);
                     }
                  }
               }

               this.b = -1;
               ka.this.d = ka.b(ka.this, ka.this.d);
               ka.this.b[ka.this.d] = null;
               ka.a(ka.this, false);
               this.a = ka.b(ka.this, this.a);
            }
         }
      };
   }

   // $VF: synthetic method
   static int a(ka var0, int var1) {
      var1 = var1;
      var0 = var0;
      if (++var1 >= var0.f) {
         var1 = 0;
      }

      return var1;
   }

   // $VF: synthetic method
   static int b(ka var0, int var1) {
      var1 = var1;
      var0 = var0;
      if (--var1 < 0) {
         var1 = var0.f - 1;
      }

      return var1;
   }

   // $VF: synthetic method
   static boolean a(ka var0, boolean var1) {
      return var0.e = false;
   }
}
