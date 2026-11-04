package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public final class Ge {
   public static final void a(Vector3dc var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      if (!var0.isFinite()) {
         String var1 = var0 + " is not finite!";
         throw new IllegalArgumentException(var1.toString());
      }
   }

   private static void a(Vector3dc var0, Function0<String> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      if (!var0.isFinite()) {
         Object var2 = var1.invoke();
         throw new IllegalArgumentException(var2.toString());
      }
   }

   private static void a(Quaterniondc var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      if (!var0.isFinite()) {
         String var1 = var0 + " is not finite!";
         throw new IllegalArgumentException(var1.toString());
      }
   }

   private static void a(Quaterniondc var0, Function0<String> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      if (!var0.isFinite()) {
         Object var2 = var1.invoke();
         throw new IllegalArgumentException(var2.toString());
      }
   }

   private static void b(Quaterniondc var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      if (!(Math.abs(var0.lengthSquared() - 1.0) < 1.0E-12)) {
         String var1 = var0 + " is not normalized!";
         throw new IllegalArgumentException(var1.toString());
      }
   }

   private static void b(Quaterniondc var0, Function0<String> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      if (!(Math.abs(var0.lengthSquared() - 1.0) < 1.0E-12)) {
         Object var2 = var1.invoke();
         throw new IllegalArgumentException(var2.toString());
      }
   }
}
