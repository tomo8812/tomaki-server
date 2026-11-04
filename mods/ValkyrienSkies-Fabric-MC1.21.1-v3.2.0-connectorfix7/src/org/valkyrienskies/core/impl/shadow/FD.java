package org.valkyrienskies.core.impl.shadow;

import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

sealed interface FD permits FD.a, FD.b, FD.c {
   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0013\u0012\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\u001c\u0010\b\u001a\u00020\u00002\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u0002HÆ\u0001¢\u0006\u0004\b\b\u0010\tJ\u001a\u0010\f\u001a\u00020\u000b2\b\u0010\u0003\u001a\u0004\u0018\u00010\nHÖ\u0003¢\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u000eHÖ\u0001¢\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b\u0011\u0010\u0007R\u0019\u0010\b\u001a\u0004\u0018\u00010\u00028\u0007¢\u0006\f\n\u0004\b\b\u0010\u0012\u001a\u0004\b\b\u0010\u0007",
      d2 = {
            "Lorg/valkyrienskies/core/impl/shadow/FD$a;",
            "Lorg/valkyrienskies/core/impl/shadow/FD;",
            "",
            "p0",
            "<init>",
            "(Ljava/lang/String;)V",
            "b",
            "()Ljava/lang/String;",
            "a",
            "(Ljava/lang/String;)Lorg/valkyrienskies/core/impl/shadow/FD$a;",
            "",
            "",
            "equals",
            "(Ljava/lang/Object;)Z",
            "",
            "hashCode",
            "()I",
            "toString",
            "Ljava/lang/String;"
      }
   )
   final class a implements FD {
      final String a;

      public a(String var1) {
         this.a = var1
      }

      // $VF: synthetic method
      public a(String var1, int var2, DefaultConstructorMarker var3) {
         if ((var2 & 1) != 0) {
            var1 = null
         }

         this(var1)
      }

      @JvmName(name = "a")
      public final String a() {
         return this.a
      }

      private String b() {
         return this.a
      }

      private static FD.a a(String var0) {
         return new FD.a(var0)
      }

      // $VF: synthetic method
      private static FD.a a$default(FD.a var0, String var1, int var2) {
         if ((var2 & 1) != 0) {
            var1 = var0.a
         }

         String var3 = var1
         return new FD.a(var3)
      }

      @Override
      public final String toString() {
         return "Discard(reason=" + this.a + ")"
      }

      @Override
      public final int hashCode() {
         return this.a == null ? 0 : this.a.hashCode()
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true
         } else {
            return !(other instanceof FD.a var2) ? false : Intrinsics.areEqual(this.a, var2.a)
         }
      }

      public a() {
         this(null, 1, null)
      }
   }

   final class b implements FD {
      final String a;

      public b(String var1) {
         Intrinsics.checkNotNullParameter(var1, "")
         super()
         this.a = var1
      }

      public final String a() {
         return this.a
      }

      private String b() {
         return this.a
      }

      private static FD.b a(String var0) {
         Intrinsics.checkNotNullParameter(var0, "")
         return new FD.b(var0)
      }

      // $VF: synthetic method
      private static FD.b a(FD.b var0, String var1, int var2) {
         if ((var2 & 1) != 0) {
            var1 = var0.a
         }

         String var3 = var1
         Intrinsics.checkNotNullParameter(var1, "")
         return new FD.b(var3)
      }

      @Override
      public final String toString() {
         return "Restore(dimensionId=" + this.a + ")"
      }

      @Override
      public final int hashCode() {
         return this.a.hashCode()
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true
         } else {
            return !(other instanceof FD.b var2) ? false : Intrinsics.areEqual(this.a, var2.a)
         }
      }
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003",
      d2 = {"Lorg/valkyrienskies/core/impl/shadow/FD$c;", "Lorg/valkyrienskies/core/impl/shadow/FD;", "<init>", "()V"}
   )
   final class c implements FD {
      public static final FD.c INSTANCE = new FD.c();

      private c() {
      }
   }
}
