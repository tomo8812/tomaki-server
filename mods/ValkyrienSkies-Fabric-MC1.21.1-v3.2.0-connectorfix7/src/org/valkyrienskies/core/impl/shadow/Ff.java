package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking;

public final class Ff implements ek<Fe> {
   private final Provider<Fb> a;
   private final Provider<Fb> b;
   private final Provider<SimplePacketNetworking> c;

   private Ff(Provider<Fb> var1, Provider<Fb> var2, Provider<SimplePacketNetworking> var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private Fe a() {
      Fb var10000 = this.a.get();
      Fb var10001 = this.b.get();
      SimplePacketNetworking var3 = this.c.get();
      Fb var2 = var10001;
      Fb var1 = var10000;
      return new Fe(var1, var2, var3);
   }

   public static Ff a(Provider<Fb> var0, Provider<Fb> var1, Provider<SimplePacketNetworking> var2) {
      return new Ff(var0, var1, var2);
   }

   private static Fe a(Fb var0, Fb var1, SimplePacketNetworking var2) {
      return new Fe(var0, var1, var2);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      Ff var1 = this;
      Fb var10000 = this.a.get();
      Fb var10001 = var1.b.get();
      SimplePacketNetworking var3 = var1.c.get();
      Fb var2 = var10001;
      Fb var4 = var10000;
      return new Fe(var4, var2, var3);
   }
}
