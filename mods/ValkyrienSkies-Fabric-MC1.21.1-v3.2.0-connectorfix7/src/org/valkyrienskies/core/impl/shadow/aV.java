package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;

public class aV {
   public final ResolvedType d;
   public final aU e;

   protected aV(ResolvedType var1, aU var2) {
      this.d = var1;
      this.e = var2;
   }

   public final aU t() {
      return this.e;
   }

   public final ResolvedType u() {
      return this.d;
   }

   public final ResolvedType a(Class<?> var1, int var2) {
      return this.e.a(this.d, var1, var2);
   }

   public final boolean v() {
      ResolvedType var1 = this.d;
      return this.d != null && aU.c(var1);
   }

   public final ResolvedType w() {
      ResolvedType var1 = this.d;
      return this.d == null ? null : this.e.d(var1);
   }

   public final String x() {
      ResolvedType var1 = this.d;
      return this.d == null ? "void" : this.e.e(var1);
   }

   private String a() {
      ResolvedType var1 = this.d;
      return this.d == null ? "void" : this.e.f(var1);
   }

   @Override
   public String toString() {
      return this.x();
   }
}
