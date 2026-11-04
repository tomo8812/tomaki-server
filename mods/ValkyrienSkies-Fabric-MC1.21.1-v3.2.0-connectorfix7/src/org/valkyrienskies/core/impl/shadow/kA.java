package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import java.util.SortedSet;

public class kA<E> extends kz<E> implements SortedSet<E> {
   private static final long b = -1675486811351124386L;

   public static <E> kA<E> a(SortedSet<E> var0, fQ<? super E, ? extends E> var1) {
      return new kA<>(var0, var1);
   }

   private static <E> kA<E> b(SortedSet<E> var0, fQ<? super E, ? extends E> var1) {
      kA var2 = new kA(var0, var1);
      if (var0.size() > 0) {
         Object[] var3 = var0.toArray();
         var0.clear();
         Object[] var6 = var3;
         int var7 = var3.length;

         for (int var4 = 0; var4 < var7; var4++) {
            Object var5 = var6[var4];
            var2.f().add(var1.a(var5));
         }
      }

      return var2;
   }

   protected kA(SortedSet<E> var1, fQ<? super E, ? extends E> var2) {
      super(var1, var2);
   }

   private SortedSet<E> a() {
      return (SortedSet<E>)this.f();
   }

   @Override
   public E first() {
      return ((SortedSet)this.f()).first();
   }

   @Override
   public E last() {
      return ((SortedSet)this.f()).last();
   }

   @Override
   public Comparator<? super E> comparator() {
      return ((SortedSet)this.f()).comparator();
   }

   @Override
   public SortedSet<E> subSet(E fromElement, E toElement) {
      fromElement = (E)((SortedSet)this.f()).subSet(fromElement, toElement);
      return new kA<>(fromElement, super.a);
   }

   @Override
   public SortedSet<E> headSet(E toElement) {
      toElement = (E)((SortedSet)this.f()).headSet(toElement);
      return new kA<>(toElement, super.a);
   }

   @Override
   public SortedSet<E> tailSet(E fromElement) {
      fromElement = (E)((SortedSet)this.f()).tailSet(fromElement);
      return new kA<>(fromElement, super.a);
   }
}
