package org.valkyrienskies.core.impl.shadow;

public final class DD extends DH<DD> {
   public static final DD a = new DD(null, null);
   public final DE b;
   public int c;
   public boolean d;
   public boolean e;
   public final Dy f;
   public Object g;
   public boolean h;

   public DD(DE var1, Dy var2) {
      this.b = var1;
      this.f = var2;
   }

   @Override
   public final boolean a() {
      boolean var1 = ((DD)super.j).d || ((DD)super.k).d || this.b.c == this && this.b.d != null;
      boolean var2 = ((DD)super.j).e || ((DD)super.k).e || this.b.c == this && this.b.e != null;
      if (var1 == this.d && var2 == this.e) {
         return false;
      }

      this.d = var1;
      this.e = var2;
      return true;
   }

   @Override
   public final boolean b() {
      int var1 = ((DD)super.j).c + ((DD)super.k).c + 1;
      boolean var2 = this.a();
      Object var3 = null;
      boolean var4 = false;
      if (this.f != null) {
         if (((DD)super.j).h) {
            var3 = ((DD)super.j).g;
            var4 = true;
         }

         if (this.b.g && this.b.c == this) {
            if (var4) {
               var3 = this.f.a(var3, this.b.f);
            } else {
               var3 = this.b.f;
               var4 = true;
            }
         }

         if (((DD)super.k).h) {
            if (var4) {
               var3 = this.f.a(var3, ((DD)super.k).g);
            } else {
               var3 = ((DD)super.k).g;
               var4 = true;
            }
         }
      }

      if (var1 != this.c || var2 || this.h != var4 || (var3 != null ? !var3.equals(this.g) : this.g != null)) {
         this.c = var1;
         this.g = var3;
         this.h = var4;
         return true;
      } else {
         return false;
      }
   }
}
