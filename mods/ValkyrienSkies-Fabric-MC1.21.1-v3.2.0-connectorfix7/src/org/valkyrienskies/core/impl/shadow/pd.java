package org.valkyrienskies.core.impl.shadow;

public class pd extends oV {
   private static final long b = -8415396756375798143L;
   private final Integer[] c;
   private final Integer[] d;

   private pd(Integer[] var1, Integer[] var2) {
      this(px.DIMENSIONS_MISMATCH, var1, var2);
   }

   public pd(px var1, Integer[] var2, Integer[] var3) {
      super(var1, var2, var3);
      this.c = (Integer[])var2.clone();
      this.d = (Integer[])var3.clone();
   }

   private Integer[] b() {
      return (Integer[])this.c.clone();
   }

   private Integer[] c() {
      return (Integer[])this.d.clone();
   }

   public final int a(int var1) {
      return this.c[var1];
   }

   public final int b(int var1) {
      return this.d[var1];
   }
}
