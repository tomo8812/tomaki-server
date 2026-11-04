package org.valkyrienskies.core.impl.shadow;

public final class Io implements Ip {
   private final int a;
   private final int b;
   private final int c;
   private final boolean d;

   public Io(int var1, int var2, int var3, boolean var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
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

   private boolean e() {
      return this.d;
   }

   @Override
   public final It d() {
      return It.EMPTY;
   }

   private int f() {
      return this.a;
   }

   private int g() {
      return this.b;
   }

   private int h() {
      return this.c;
   }

   private boolean i() {
      return this.d;
   }

   private static Io a(int var0, int var1, int var2, boolean var3) {
      return new Io(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   private static Io a(Io var0, int var1, int var2, int var3, boolean var4, int var5) {
      if ((var5 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.c;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.d;
      }

      boolean var10 = var4;
      int var9 = var3;
      int var8 = var2;
      int var7 = var1;
      return new Io(var7, var8, var9, var10);
   }

   @Override
   public final String toString() {
      return "EmptyVoxelShapeUpdate(regionX=" + this.a + ", regionY=" + this.b + ", regionZ=" + this.c + ", overwriteExistingVoxels=" + this.d + ')';
   }

   @Override
   public final int hashCode() {
      int var10000 = ((Integer.hashCode(this.a) * 31 + Integer.hashCode(this.b)) * 31 + Integer.hashCode(this.c)) * 31;
      byte var10001 = this.d;
      if (this.d) {
         var10001 = 1;
      }

      return var10000 + var10001;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Io)) {
         return false;
      } else {
         other = (Io)other;
         if (this.a != ((Io)other).a) {
            return false;
         } else if (this.b != ((Io)other).b) {
            return false;
         } else {
            return this.c != ((Io)other).c ? false : this.d == ((Io)other).d;
         }
      }
   }
}
