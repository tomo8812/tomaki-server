package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;

public final class fY<E> extends fV<E> {
   private static final long a = -2560033712679053143L;

   public static <E> eW<E> a(eW<E> var0) {
      return new fY<>(var0);
   }

   private fY(eW<E> var1) {
      super(var1);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(this.e());
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a((Collection<E>)var1.readObject());
   }

   @Override
   public final boolean containsAll(Collection<?> coll) {
      Iterator var2 = coll.iterator();

      while (var2.hasNext()) {
         if (!this.contains(var2.next())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final boolean add(E object) {
      return this.a(object, 1);
   }

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      boolean var2 = false;
      Iterator var4 = coll.iterator();

      while (var4.hasNext()) {
         boolean var3 = this.a((E)var4.next(), 1);
         var2 = var2 || var3;
      }

      return var2;
   }

   @Override
   public final boolean remove(Object object) {
      return this.b(object, 1);
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      if (coll == null) {
         return this.e().removeAll(null);
      }

      boolean var2 = false;

      for (boolean var3 : coll) {
         var3 = this.b(var3, this.a(var3));
         var2 = var2 || var3;
      }

      return var2;
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      if (coll != null) {
         boolean var2 = false;
         Iterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!coll.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      } else {
         return this.e().retainAll(null);
      }
   }

   @Override
   public final boolean a(E var1, int var2) {
      this.e().a((E)var1, var2);
      return true;
   }
}
