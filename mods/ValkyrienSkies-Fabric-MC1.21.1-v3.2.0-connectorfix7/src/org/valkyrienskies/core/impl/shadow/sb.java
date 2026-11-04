package org.valkyrienskies.core.impl.shadow;

public interface sb<S extends qN> {
   sb<S> e();

   rV<S> c();

   boolean b();

   double a();

   @Deprecated
   int b(rV<S> var1);

   sb.a<S> a(rV<S> var1);

   sb<S> a(sb<S> var1);

   class a<U extends qN> {
      public final sb<U> a;
      public final sb<U> b;

      public a(sb<U> var1, sb<U> var2) {
         this.a = var1;
         this.b = var2;
      }

      public final sb<U> a() {
         return this.a;
      }

      public final sb<U> b() {
         return this.b;
      }

      public final int c() {
         if (this.a != null && !this.a.b()) {
            return this.b != null && !this.b.b() ? sa.BOTH$7c70dd6b : sa.PLUS$7c70dd6b;
         } else {
            return this.b != null && !this.b.b() ? sa.MINUS$7c70dd6b : sa.HYPER$7c70dd6b;
         }
      }
   }
}
