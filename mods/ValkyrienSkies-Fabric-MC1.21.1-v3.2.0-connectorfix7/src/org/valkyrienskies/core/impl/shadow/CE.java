package org.valkyrienskies.core.impl.shadow;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public sealed interface CE permits CE.a, CE.b, CE.c, CE.d, CE.e, CE.f {
   final class a implements CE {
      final String a;

      public a(String var1) {
         Intrinsics.checkNotNullParameter(var1, "");
         super();
         this.a = var1;
      }

      public final String a() {
         return this.a;
      }
   }

   final class b implements CE {
      final String a;
      final String b;

      public b(String var1, String var2) {
         Intrinsics.checkNotNullParameter(var1, "");
         Intrinsics.checkNotNullParameter(var2, "");
         super();
         this.a = var1;
         this.b = var2;
      }

      public final String a() {
         return this.a;
      }

      public final String b() {
         return this.b;
      }
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003",
      d2 = {"Lorg/valkyrienskies/core/impl/shadow/CE$c;", "Lorg/valkyrienskies/core/impl/shadow/CE;", "<init>", "()V"}
   )
   final class c implements CE {
      public static final CE.c INSTANCE = new CE.c();

      private c() {
      }
   }

   final class d implements CE {
      final Object a;

      public d(Object var1) {
         Intrinsics.checkNotNullParameter(var1, "");
         super();
         this.a = var1;
      }

      public final Object a() {
         return this.a;
      }
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003",
      d2 = {"Lorg/valkyrienskies/core/impl/shadow/CE$e;", "Lorg/valkyrienskies/core/impl/shadow/CE;", "<init>", "()V"}
   )
   final class e implements CE {
      public static final CE.e INSTANCE = new CE.e();

      private e() {
      }
   }

   final class f implements CE {
      final Object a;

      public f(Object var1) {
         Intrinsics.checkNotNullParameter(var1, "");
         super();
         this.a = var1;
      }

      public final Object a() {
         return this.a;
      }
   }
}
