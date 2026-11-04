package org.valkyrienskies.core.impl.shadow;

public final class rm {
   public static final rm a = new rm("XYZ", rs.b, rs.d, rs.f);
   public static final rm b = new rm("XZY", rs.b, rs.f, rs.d);
   public static final rm c = new rm("YXZ", rs.d, rs.b, rs.f);
   public static final rm d = new rm("YZX", rs.d, rs.f, rs.b);
   public static final rm e = new rm("ZXY", rs.f, rs.b, rs.d);
   public static final rm f = new rm("ZYX", rs.f, rs.d, rs.b);
   public static final rm g = new rm("XYX", rs.b, rs.d, rs.b);
   public static final rm h = new rm("XZX", rs.b, rs.f, rs.b);
   public static final rm i = new rm("YXY", rs.d, rs.b, rs.d);
   public static final rm j = new rm("YZY", rs.d, rs.f, rs.d);
   public static final rm k = new rm("ZXZ", rs.f, rs.b, rs.f);
   private static rm o;
   private final String p;
   final rs l;
   final rs m;
   final rs n;

   private rm(String var1, rs var2, rs var3, rs var4) {
      this.p = var1;
      this.l = var2;
      this.m = var3;
      this.n = var4;
   }

   @Override
   public final String toString() {
      return this.p;
   }

   public final rs a() {
      return this.l;
   }

   public final rs b() {
      return this.m;
   }

   public final rs c() {
      return this.n;
   }

   static {
      new rm("ZYZ", rs.f, rs.d, rs.f);
   }
}
