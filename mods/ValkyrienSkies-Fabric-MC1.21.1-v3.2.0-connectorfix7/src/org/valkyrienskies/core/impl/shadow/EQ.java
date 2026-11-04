package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.ShipObjectClientWorld;
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking;

public final class EQ implements EO.b {
   private final EP a;

   private EQ(EP var1) {
      this.a = var1;
   }

   @Override
   public final EO a(ShipObjectClientWorld var1) {
      ShipObjectClientWorld var2 = var1;
      EP var5 = this.a;
      Fl var10001 = var5.a.get();
      SimplePacketNetworking var10002 = var5.b.get();
      Fe var4 = var5.c.get();
      SimplePacketNetworking var3 = var10002;
      Fl var7 = var10001;
      var1 = var2;
      return new EO(var1, var7, var3, var4);
   }

   private static Provider<EO.b> b(EP var0) {
      return eo.a(new EQ(var0));
   }

   public static ew<EO.b> a(EP var0) {
      return eo.a(new EQ(var0));
   }
}
