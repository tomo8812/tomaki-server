package org.valkyrienskies.physics_api_krunch;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.impl.shadow.GW;
import org.valkyrienskies.core.impl.shadow.HM;
import org.valkyrienskies.core.impl.shadow.HU;
import org.valkyrienskies.core.impl.shadow.HV;
import org.valkyrienskies.core.impl.shadow.HW;
import org.valkyrienskies.core.impl.shadow.Hc;
import org.valkyrienskies.core.impl.shadow.He;
import org.valkyrienskies.core.impl.shadow.Hh;
import org.valkyrienskies.core.impl.shadow.Hi;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.IA;
import org.valkyrienskies.core.impl.shadow.IB;
import org.valkyrienskies.core.impl.shadow.IN;
import org.valkyrienskies.core.impl.shadow.IR;
import org.valkyrienskies.core.impl.shadow.Iv;
import org.valkyrienskies.core.impl.shadow.Iy;

public class KrunchNativePhysicsWorldReference implements He {
   private static final ThreadLocal<ByteBuffer> d = ThreadLocal.withInitial(() -> ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN));
   private static final long e = 0L;
   private static final int f = -1;
   long b;
   boolean c;
   private final Int2ObjectOpenHashMap g = new Int2ObjectOpenHashMap();
   private final Int2ObjectOpenHashMap h = new Int2ObjectOpenHashMap();
   private final KrunchNativePhysicsThreadPoolReference i;

   public KrunchNativePhysicsWorldReference(KrunchNativePhysicsThreadPoolReference physicsThreadPoolReference) {
      this.i = physicsThreadPoolReference;
      this.b = createKrunchNativePhysicsWorld(physicsThreadPoolReference.a);
      this.c = false;
   }

   private static ByteBuffer j() {
      ByteBuffer var0;
      (var0 = d.get()).clear();
      return var0;
   }

   @Override
   public final List<He.c> a(Vector3dc var1, double var2) {
      this.a();
      return Iy.a(ByteBuffer.wrap(preStep(this.b, var1.x(), var1.y(), var1.z(), var2)).order(ByteOrder.LITTLE_ENDIAN), this);
   }

   @Override
   public final void h() {
      this.a();
      postStep(this.b);
   }

   public final void a(IA var1) {
      this.a();
      setSettings(
         this.b,
         var1.a(),
         var1.b(),
         var1.c(),
         var1.d(),
         var1.e(),
         var1.f(),
         var1.g(),
         var1.h(),
         var1.i().getSolverType(),
         var1.j(),
         var1.k(),
         var1.l(),
         var1.m(),
         var1.n().getBackendType(),
         var1.o()
      );
   }

   @Override
   public final void b() {
      if (!this.c) {
         deleteKrunchNativePhysicsWorld(this.b);
         this.b = 0L;
         this.c = true;
      }
   }

   @Override
   public final boolean a(int var1) {
      this.a();
      this.h.remove(var1);
      return deleteRigidBody(this.b, var1);
   }

   @Override
   public final boolean g() {
      return this.c;
   }

   private void b(Integer var1) {
      if (var1 != null) {
         Hc var2;
         if ((var2 = (Hc)this.h.get(var1)) != null) {
            var2.q();
         }
      }
   }

   @Override
   public final boolean a(HM var1, boolean var2) {
      this.a();
      ByteBuffer var3 = j();
      Iv.a(var1, var3);
      var2 = addConstraintAndId2(this.b, var3.array(), var2);
      this.g.put(var1.a, new Pair(var1.b.a(), var1.b.c()));
      this.b(var1.b.a());
      this.b(var1.b.c());
      return var2;
   }

   @Override
   public final List<HM> d() {
      this.a();
      return Iv.a(ByteBuffer.wrap(getConstraints(this.b)).order(ByteOrder.LITTLE_ENDIAN));
   }

   @Override
   public final List<HM> e() {
      this.a();
      return Iv.a(ByteBuffer.wrap(getConstraintsBrokenLastTick(this.b)).order(ByteOrder.LITTLE_ENDIAN));
   }

   @Override
   public final List<HM> f() {
      this.a();
      return Iv.a(ByteBuffer.wrap(getConstraintsRemovedFromDeletedBodiesLastTick(this.b)).order(ByteOrder.LITTLE_ENDIAN));
   }

   @Override
   public final boolean b(int var1) {
      this.a();
      boolean var2 = removeConstraint(this.b, var1);
      Pair var3 = (Pair)this.g.remove(var1);
      this.b((Integer)var3.getFirst());
      this.b((Integer)var3.getSecond());
      return var2;
   }

   @Override
   public final boolean a(HM var1) {
      this.a();
      ByteBuffer var2 = j();
      Iv.a(var1, var2);
      boolean var5 = updateConstraint(this.b, var2.array());
      Pair var3 = (Pair)this.g.get(var1.a);
      this.g.put(var1.a, new Pair(var1.b.a(), var1.b.c()));
      this.b((Integer)var3.getFirst());
      this.b((Integer)var3.getSecond());
      this.b(var1.b.a());
      this.b(var1.b.c());
      return var5;
   }

   private <T extends GW> KrunchNativeRigidBodyReference<T> b(T var1) {
      this.a();
      IB var2 = (IB)var1;
      int var4;
      if ((var4 = createRigidBody(this.b, var2.b(), var2 instanceof KrunchNativeVoxelShapeReference)) == -1) {
         throw new Hl("Create rigid body failed because shape with id " + ((IB)var1).b() + " was deleted!");
      }

      KrunchNativeRigidBodyReference var3 = new KrunchNativeRigidBodyReference<>(this, var4, var1);
      this.h.put(var3.a, var3);
      return var3;
   }

   @Override
   public final void a(int var1, int var2) {
      this.a();
      disableCollisionBetweenBodies(this.b, var1, var2);
   }

   @Override
   public final void b(int var1, int var2) {
      this.a();
      enableCollisionBetweenBodies(this.b, var1, var2);
   }

   @Override
   public final Hh a(Vector3dc var1, Vector3dc var2, double var3, int... var5) {
      this.a();
      byte[] var6;
      return (var6 = rayTrace(this.b, var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3, var5)) == null ? null : IN.a(var6);
   }

   @Override
   public final Hi b(Vector3dc var1, Vector3dc var2, double var3, int... var5) {
      this.a();
      byte[] var6;
      return (var6 = rayTraceWithVel(this.b, var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3, var5)) == null ? null : IN.b(var6);
   }

   @Override
   public final void a(Integer var1) {
   }

   @Override
   public final List<HW> c() {
      this.a();
      byte[] var1;
      if ((var1 = getCollisionEvents(this.b)) != null && var1.length != 0) {
         int var2;
         ByteBuffer var16;
         if ((var2 = (var16 = ByteBuffer.wrap(var1).order(ByteOrder.LITTLE_ENDIAN)).getInt()) == 0) {
            return new ArrayList<>();
         }

         ArrayList var3 = new ArrayList(var2);

         for (int var4 = 0; var4 < var2; var4++) {
            ByteBuffer var5 = var16;
            int var6 = var16.getInt();
            int var7 = var5.getInt();
            int var8 = var5.getInt();
            ArrayList var9 = new ArrayList(var8);

            for (int var10 = 0; var10 < var8; var10++) {
               HV var11 = HV.values()[var5.get()];
               HV var12 = var11;
               ByteBuffer var18 = var5;
               Vector3d var13 = new Vector3d(var18.getDouble(), var18.getDouble(), var18.getDouble());
               Vector3d var14 = new Vector3d(var18.getDouble(), var18.getDouble(), var18.getDouble());
               float var15 = var18.getFloat();
               Vector3d var19 = new Vector3d(var18.getDouble(), var18.getDouble(), var18.getDouble());
               HU var20 = new HU(var12, var13, var14, var15, var19);
               var9.add(var20);
            }

            IR var17 = new IR(var6, var7, var9);
            var3.add(var17);
         }

         return var3;
      } else {
         return new ArrayList<>();
      }
   }

   protected final void a() {
      if (this.c) {
         throw new Hl("The underlying physics world has already been deleted!");
      }
   }

   protected final long i() {
      return this.b;
   }

   private static native long createKrunchNativePhysicsWorld(long var0);

   private static native void deleteKrunchNativePhysicsWorld(long var0);

   private static native void setSettings(
      long var0,
      int var2,
      int var3,
      int var4,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      int var15,
      int var16,
      double var17,
      double var19,
      int var21,
      int var22,
      boolean var23
   );

   private static native boolean deleteRigidBody(long var0, int var2);

   private static native boolean addConstraintAndId2(long var0, byte[] var2, boolean var3);

   private static native byte[] getConstraints(long var0);

   private static native byte[] getConstraintsBrokenLastTick(long var0);

   private static native byte[] getConstraintsRemovedFromDeletedBodiesLastTick(long var0);

   private static native boolean removeConstraint(long var0, int var2);

   private static native boolean updateConstraint(long var0, byte[] var2);

   private static native int createRigidBody(long var0, long var2, boolean var4);

   private static native byte[] preStep(long var0, double var2, double var4, double var6, double var8);

   private static native void postStep(long var0);

   private static native void disableCollisionBetweenBodies(long var0, int var2, int var3);

   private static native void enableCollisionBetweenBodies(long var0, int var2, int var3);

   private static native byte[] rayTrace(long var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int[] var16);

   private static native byte[] rayTraceWithVel(
      long var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int[] var16
   );

   private static native void setGroundBodyId(long var0, int var2, boolean var3);

   private static native byte[] getCollisionEvents(long var0);

   // $VF: synthetic method
   @Override
   public final Hc a(GW var1) {
      GW var2 = var1;
      KrunchNativePhysicsWorldReference var4 = this;
      this.a();
      IB var3 = (IB)var2;
      int var6;
      if ((var6 = createRigidBody(var4.b, var3.b(), var3 instanceof KrunchNativeVoxelShapeReference)) == -1) {
         throw new Hl("Create rigid body failed because shape with id " + ((IB)var2).b() + " was deleted!");
      }

      KrunchNativeRigidBodyReference var5 = new KrunchNativeRigidBodyReference<>(var4, var6, var2);
      var4.h.put(var5.a, var5);
      return var5;
   }
}
