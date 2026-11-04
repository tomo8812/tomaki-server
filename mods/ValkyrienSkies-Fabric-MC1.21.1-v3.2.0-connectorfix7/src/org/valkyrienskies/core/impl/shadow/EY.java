package org.valkyrienskies.core.impl.shadow;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.ReplaceWith;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference0;
import kotlin.jvm.internal.MutablePropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KMutableProperty0;

public final class EY {
   private static EX a;

   public static final void a(EX var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      a = var0;
   }

   private static EX a() {
      EX var10000 = a;
      if (a == null) {
         Intrinsics.throwUninitializedPropertyAccessException("");
         var10000 = null;
      }

      return var10000;
   }

   private static void b(EX var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      a = var0;
   }

   // $VF: synthetic method
   @Deprecated(
      message = "Obtain an instance of VSCore and get hooks from there.",
      replaceWith = @ReplaceWith(expression = "vsCore.hooks", imports = {}),
      level = DeprecationLevel.ERROR
   )
   private static void b() {
   }

   private static KMutableProperty0 c() {
      return Reflection.mutableProperty0(
         (MutablePropertyReference0)(
            new MutablePropertyReference0Impl(EY.class, "CoreHooksInstance", "getCoreHooksInstance()Lorg/valkyrienskies/core/impl/hooks_impl/CoreHooksImpl;", 1)
         )
      );
   }
}
