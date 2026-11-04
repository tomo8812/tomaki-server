package org.valkyrienskies.core.impl.shadow;

public final class Im implements Ip {
   private final int a;
   private final int b;
   private final int c;

   public Im(int var1, int var2, int var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   @Override
   public final int a() {
      return this.a;
   }

   @Override
   public final int b() {
      return this.b;
   }

   @Override
   public final int c() {
      return this.c;
   }

   @Override
   public final It d() {
      return It.DELETE;
   }

   private int e() {
      return this.a;
   }

   private int f() {
      return this.b;
   }

   private int g() {
      return this.c;
   }

   private static Im a(int var0, int var1, int var2) {
      return new Im(var0, var1, var2);
   }

   // $VF: synthetic method
   private static Im a(Im var0, int var1, int var2, int var3, int var4) {
      if ((var4 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.c;
      }

      int var8 = var3;
      int var7 = var2;
      int var6 = var1;
      return new Im(var6, var7, var8);
   }

   @Override
   public final String toString() {
      return "DeleteVoxelShapeUpdate(regionX=" + this.a + ", regionY=" + this.b + ", regionZ=" + this.c + ')';
   }

   @Override
   public final int hashCode() {
      return (Integer.hashCode(this.a) * 31 + Integer.hashCode(this.b)) * 31 + Integer.hashCode(this.c);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Im)) {
         return false;
      } else {
         other = (Im)other;
         if (this.a != ((Im)other).a) {
            return false;
         } else {
            return this.b != ((Im)other).b ? false : this.c == ((Im)other).c;
         }
      }
   }
}
