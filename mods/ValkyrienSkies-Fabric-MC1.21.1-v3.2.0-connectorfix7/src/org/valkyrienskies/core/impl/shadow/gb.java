package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Set;

public class gb<E> extends gy<E> implements eW<E> {
   private static final long b = -2575833140344736876L;

   public static <E> gb<E> a(eW<E> var0, fF<? super E> var1) {
      return new gb<>(var0, var1);
   }

   protected gb(eW<E> var1, fF<? super E> var2) {
      super(var1, var2);
   }

   protected eW<E> e() {
      return (eW<E>)super.f();
   }

   @Override
   public boolean equals(Object object) {
      return object == this || this.e().equals(object);
   }

   @Override
   public int hashCode() {
      return this.e().hashCode();
   }

   @Override
   public final boolean a(E var1, int var2) {
      this.b((E)var1);
      return this.e().a((E)var1, var2);
   }

   @Override
   public final boolean b(Object var1, int var2) {
      return this.e().b(var1, var2);
   }

   @Override
   public final Set<E> a() {
      return this.e().a();
   }

   @Override
   public final int a(Object var1) {
      return this.e().a(var1);
   }

   // $VF: synthetic method
   @Override
   public Collection f() {
      return this.e();
   }
}
