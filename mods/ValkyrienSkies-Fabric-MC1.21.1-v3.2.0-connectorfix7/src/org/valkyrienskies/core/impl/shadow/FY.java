package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.JvmInline;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KProperty;
import org.apache.logging.log4j.Logger;

@JvmInline
public final class FY {
   private final Logger a;

   private Logger a() {
      return this.a;
   }

   public static final Logger a(Logger var0, KProperty<?> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return var0;
   }

   private static String b(Logger var0) {
      return "ClassLogger(logger=" + var0 + ")";
   }

   @Override
   public final String toString() {
      Logger var1 = this.a;
      return "ClassLogger(logger=" + this.a + ")";
   }

   private static int c(Logger var0) {
      return var0.hashCode();
   }

   @Override
   public final int hashCode() {
      Logger var1 = this.a;
      return this.a.hashCode();
   }

   private static boolean a(Logger var0, Object var1) {
      FY var2;
      return !(var1 instanceof FY) ? false : Intrinsics.areEqual(var0, (var2 = (FY)var1).a);
   }

   @Override
   public final boolean equals(Object other) {
      Object var2 = other;
      other = this.a;
      FY var4;
      return !(var2 instanceof FY) ? false : Intrinsics.areEqual(other, (var4 = (FY)var2).a);
   }

   // $VF: synthetic method
   private FY(Logger var1) {
      this.a = var1;
   }

   public static Logger a(Logger var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      return var0;
   }

   // $VF: synthetic method
   private static FY d(Logger var0) {
      return new FY(var0);
   }

   // $VF: synthetic method
   private Logger b() {
      return this.a;
   }

   private static boolean a(Logger var0, Logger var1) {
      return Intrinsics.areEqual(var0, var1);
   }
}
