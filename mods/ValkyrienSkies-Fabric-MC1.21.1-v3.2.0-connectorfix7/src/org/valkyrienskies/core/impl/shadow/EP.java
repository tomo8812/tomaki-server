package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.ShipObjectClientWorld;
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking;

public final class EP {
   final Provider<Fl> a;
   final Provider<SimplePacketNetworking> b;
   final Provider<Fe> c;

   private EP(Provider<Fl> var1, Provider<SimplePacketNetworking> var2, Provider<Fe> var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   public final EO a(ShipObjectClientWorld var1) {
      Fl var10001 = this.a.get();
      SimplePacketNetworking var10002 = this.b.get();
      Fe var4 = this.c.get();
      SimplePacketNetworking var3 = var10002;
      Fl var2 = var10001;
      var1 = var1;
      return new EO(var1, var2, var3, var4);
   }

   public static EP a(Provider<Fl> var0, Provider<SimplePacketNetworking> var1, Provider<Fe> var2) {
      return new EP(var0, var1, var2);
   }

   private static EO a(ShipObjectClientWorld var0, Fl var1, SimplePacketNetworking var2, Fe var3) {
      return new EO(var0, var1, var2, var3);
   }
}
