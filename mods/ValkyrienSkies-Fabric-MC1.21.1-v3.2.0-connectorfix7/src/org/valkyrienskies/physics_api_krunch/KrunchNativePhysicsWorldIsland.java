package org.valkyrienskies.physics_api_krunch;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.joml.Vector3dc;
import org.valkyrienskies.core.impl.shadow.He;

public class KrunchNativePhysicsWorldIsland implements He.c {
   private final int a;
   private final IntList b;
   private final KrunchNativePhysicsWorldReference c;

   public KrunchNativePhysicsWorldIsland(int islandId, IntArrayList bodies, KrunchNativePhysicsWorldReference krunchPhysicsWorld) {
      this.a = islandId;
      this.b = bodies;
      this.c = krunchPhysicsWorld;
   }

   @Override
   public final IntList a() {
      return this.b;
   }

   @Override
   public final void a(Vector3dc var1, double var2) {
      this.c.a();
      KrunchNativePhysicsWorldReference var4 = this.c;
      preStep(this.c.b, this.a, var1.x(), var1.y(), var1.z(), var2);
   }

   @Override
   public final void b(Vector3dc var1, double var2) {
      this.c.a();
      KrunchNativePhysicsWorldReference var4 = this.c;
      subStep(this.c.b, this.a, var1.x(), var1.y(), var1.z(), var2);
   }

   @Override
   public final void b() {
      this.c.a();
      KrunchNativePhysicsWorldReference var1 = this.c;
      postStep(this.c.b, this.a);
   }

   private static native void preStep(long var0, int var2, double var3, double var5, double var7, double var9);

   private static native void subStep(long var0, int var2, double var3, double var5, double var7, double var9);

   private static native void postStep(long var0, int var2);
}
