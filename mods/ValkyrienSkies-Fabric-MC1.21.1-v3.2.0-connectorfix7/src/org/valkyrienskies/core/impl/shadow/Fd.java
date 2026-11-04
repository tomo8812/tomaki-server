package org.valkyrienskies.core.impl.shadow;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.event.RegisteredListener;
import org.valkyrienskies.core.internal.world.VsiPlayer;

public final class Fd {
   public final Fb a;
   final int b;
   final String c;

   public Fd(Fb var1, int var2, String var3) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var3, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private Fb c() {
      return this.a;
   }

   public final int a() {
      return this.b;
   }

   public final String b() {
      return this.c;
   }

   public final Fc a(ByteBuf var1) {
      return new Fc(this, var1);
   }

   public final void b(ByteBuf var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.a(this.a(var1));
   }

   public final void a(ByteBuf var1, VsiPlayer var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      this.a.a(this.a(var1), var2);
   }

   public final void a(ByteBuf var1, VsiPlayer... var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Fb var10000 = this.a;
      Fc var10001 = this.a(var1);
      VsiPlayer[] var3 = Arrays.copyOf(var2, var2.length);
      Fc var8 = var10001;
      Fb var7 = var10000;
      Intrinsics.checkNotNullParameter(var8, "");
      Intrinsics.checkNotNullParameter(var3, "");
      var3 = var3;
      int var4 = 0;

      for (int var5 = var3.length; var4 < var5; var4++) {
         VsiPlayer var6;
         var6 = var6 = var3[var4];
         var7.a(var8, var6);
      }
   }

   public final void c(ByteBuf var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.b(this.a(var1));
   }

   public final RegisteredListener a(Fh var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return this.a.a(this, var1);
   }

   public final RegisteredListener a(Fa var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return this.a.a(this, var1);
   }

   private Fb d() {
      return this.a;
   }

   private int e() {
      return this.b;
   }

   private String f() {
      return this.c;
   }

   private static Fd a(Fb var0, int var1, String var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var2, "");
      return new Fd(var0, var1, var2);
   }

   // $VF: synthetic method
   private static Fd a(Fd var0, Fb var1, int var2, String var3, int var4) {
      if ((var4 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.c;
      }

      String var7 = var3;
      int var6 = var2;
      Fb var5 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var7, "");
      return new Fd(var5, var6, var7);
   }

   @Override
   public final String toString() {
      return "PacketType(channel=" + this.a + ", id=" + this.b + ", name=" + this.c + ")";
   }

   @Override
   public final int hashCode() {
      return (this.a.hashCode() * 31 + Integer.hashCode(this.b)) * 31 + this.c.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Fd var2)) {
         return false;
      } else if (!Intrinsics.areEqual(this.a, var2.a)) {
         return false;
      } else {
         return this.b != var2.b ? false : Intrinsics.areEqual(this.c, var2.c);
      }
   }
}
