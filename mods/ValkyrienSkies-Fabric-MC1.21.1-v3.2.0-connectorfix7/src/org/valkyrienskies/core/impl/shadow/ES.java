package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking;

public final class ES implements ek<ER> {
   private final Provider<Et> a;
   private final Provider<Fb> b;
   private final Provider<Fb> c;
   private final Provider<SimplePacketNetworking> d;
   private final Provider<Fe> e;

   private ES(Provider<Et> var1, Provider<Fb> var2, Provider<Fb> var3, Provider<SimplePacketNetworking> var4, Provider<Fe> var5) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
   }

   private ER a() {
      dT var10000 = ej.a(this.a);
      Fb var10001 = this.b.get();
      Fb var10002 = this.c.get();
      SimplePacketNetworking var10003 = this.d.get();
      Fe var5 = this.e.get();
      SimplePacketNetworking var4 = var10003;
      Fb var3 = var10002;
      Fb var2 = var10001;
      dT var1 = var10000;
      return new ER(var1, var2, var3, var4, var5);
   }

   public static ES a(Provider<Et> var0, Provider<Fb> var1, Provider<Fb> var2, Provider<SimplePacketNetworking> var3, Provider<Fe> var4) {
      return new ES(var0, var1, var2, var3, var4);
   }

   private static ER a(dT<Et> var0, Fb var1, Fb var2, SimplePacketNetworking var3, Fe var4) {
      return new ER(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      ES var1 = this;
      dT var10000 = ej.a(this.a);
      Fb var10001 = var1.b.get();
      Fb var10002 = var1.c.get();
      SimplePacketNetworking var10003 = var1.d.get();
      Fe var5 = var1.e.get();
      SimplePacketNetworking var4 = var10003;
      Fb var3 = var10002;
      Fb var2 = var10001;
      dT var6 = var10000;
      return new ER(var6, var2, var3, var4, var5);
   }
}
