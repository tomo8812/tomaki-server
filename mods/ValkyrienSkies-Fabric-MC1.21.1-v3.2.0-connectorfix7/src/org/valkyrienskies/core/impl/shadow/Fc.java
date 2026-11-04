package org.valkyrienskies.core.impl.shadow;

import io.netty.buffer.ByteBuf;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.internal.world.VsiPlayer;

public final class Fc {
   final Fd a;
   public final ByteBuf b;

   public Fc(Fd var1, ByteBuf var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
   }

   public final Fd a() {
      return this.a;
   }

   public final ByteBuf b() {
      return this.b;
   }

   private void c() {
      this.a.b(this.b);
   }

   private void a(VsiPlayer var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.a(this.b, var1);
   }

   private Fd d() {
      return this.a;
   }

   private ByteBuf e() {
      return this.b;
   }

   private static Fc a(Fd var0, ByteBuf var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new Fc(var0, var1);
   }

   // $VF: synthetic method
   private static Fc a(Fc var0, Fd var1, ByteBuf var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      ByteBuf var5 = var2;
      Fd var4 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var5, "");
      return new Fc(var4, var5);
   }

   @Override
   public final String toString() {
      return "Packet(type=" + this.a + ", data=" + this.b + ")";
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode() * 31 + this.b.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Fc var2)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.a, var2.a) ? false : Intrinsics.areEqual(this.b, var2.b);
      }
   }
}
