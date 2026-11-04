package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking;

public final class Fn implements ek<Fl> {
   private final Provider<Fb> a;
   private final Provider<Fb> b;
   private final Provider<Fe> c;
   private final Provider<SimplePacketNetworking> d;

   private Fn(Provider<Fb> var1, Provider<Fb> var2, Provider<Fe> var3, Provider<SimplePacketNetworking> var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   private Fl a() {
      Fb var10000 = this.a.get();
      Fb var10001 = this.b.get();
      Fe var10002 = this.c.get();
      SimplePacketNetworking var4 = this.d.get();
      Fe var3 = var10002;
      Fb var2 = var10001;
      Fb var1 = var10000;
      return new Fl(var1, var2, var3, var4);
   }

   public static Fn a(Provider<Fb> var0, Provider<Fb> var1, Provider<Fe> var2, Provider<SimplePacketNetworking> var3) {
      return new Fn(var0, var1, var2, var3);
   }

   private static Fl a(Fb var0, Fb var1, Fe var2, SimplePacketNetworking var3) {
      return new Fl(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      Fn var1 = this;
      Fb var10000 = this.a.get();
      Fb var10001 = var1.b.get();
      Fe var10002 = var1.c.get();
      SimplePacketNetworking var4 = var1.d.get();
      Fe var3 = var10002;
      Fb var2 = var10001;
      Fb var5 = var10000;
      return new Fl(var5, var2, var3, var4);
   }
}
