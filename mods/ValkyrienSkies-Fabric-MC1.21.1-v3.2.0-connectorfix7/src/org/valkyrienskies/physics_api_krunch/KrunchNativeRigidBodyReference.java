package org.valkyrienskies.physics_api_krunch;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;
import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.impl.shadow.GW;
import org.valkyrienskies.core.impl.shadow.Hb;
import org.valkyrienskies.core.impl.shadow.Hc;
import org.valkyrienskies.core.impl.shadow.He;
import org.valkyrienskies.core.impl.shadow.Hf;
import org.valkyrienskies.core.impl.shadow.Hg;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.IB;
import org.valkyrienskies.core.impl.shadow.IF;

class KrunchNativeRigidBodyReference<T extends GW> implements Hc<T> {
   private static final int b = 0;
   private static final int c = -1;
   private final KrunchNativePhysicsWorldReference d;
   final int a;
   private int e;
   private T f;

   protected KrunchNativeRigidBodyReference(KrunchNativePhysicsWorldReference physicsWorldReference, int rigidBodyUniqueId, T collisionShape) {
      this.d = physicsWorldReference;
      this.a = rigidBodyUniqueId;
      this.e = 0;
      this.f = collisionShape;
      GW var9 = collisionShape;
      KrunchNativeRigidBodyReference var8 = this;
      this.t();
      long var6 = ((IB)var9).b();
      KrunchNativePhysicsWorldReference var10 = var8.d;
      setCollisionShape(var8.d.b, var8.a, var8.e, var6, var9 instanceof KrunchNativeVoxelShapeReference);
      var8.f = (T)var9;
      var8.q();
   }

   @Override
   public final int a() {
      return this.a;
   }

   @Override
   public final Hg d() {
      this.t();
      byte[] var1 = new byte[104];
      KrunchNativePhysicsWorldReference var2 = this.d;
      getPoseVel(this.d.b, this.a, this.e, var1);
      return IF.a(var1);
   }

   @Override
   public final void a(Hg var1) {
      this.t();
      byte[] var3 = IF.a(var1);
      KrunchNativePhysicsWorldReference var2 = this.d;
      setPoseVel(this.d.b, this.a, this.e, var3);
      this.q();
   }

   @Override
   public final double h() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getDynamicFrictionCoefficient(this.d.b, this.a, this.e);
   }

   @Override
   public final void b(double var1) {
      this.t();
      KrunchNativePhysicsWorldReference var3 = this.d;
      setDynamicFrictionCoefficient(this.d.b, this.a, this.e, var1);
      this.q();
   }

   @Override
   public final boolean f() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getIsStatic(this.d.b, this.a, this.e);
   }

   @Override
   public final void a(boolean var1) {
      this.t();
      KrunchNativePhysicsWorldReference var2 = this.d;
      setStatic(this.d.b, this.a, this.e, var1);
      this.q();
   }

   @Override
   public final double g() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getRestitutionCoefficient(this.d.b, this.a, this.e);
   }

   @Override
   public final void a(double var1) {
      this.t();
      KrunchNativePhysicsWorldReference var3 = this.d;
      setRestitutionCoefficient(this.d.b, this.a, this.e, var1);
      this.q();
   }

   @Override
   public final double i() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getStaticFrictionCoefficient(this.d.b, this.a, this.e);
   }

   @Override
   public final void c(double var1) {
      this.t();
      KrunchNativePhysicsWorldReference var3 = this.d;
      setStaticFrictionCoefficient(this.d.b, this.a, this.e, var1);
      this.q();
   }

   @Override
   public final Vector3dc k() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getCollisionShapeOffset(this.d.b, this.a, this.e);
   }

   @Override
   public final void a(Vector3dc var1) {
      this.t();
      KrunchNativePhysicsWorldReference var2 = this.d;
      setCollisionShapeOffset(this.d.b, this.a, this.e, var1.x(), var1.y(), var1.z());
      this.q();
   }

   @Override
   public final double l() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getBuoyancyFactor(this.d.b, this.a, this.e);
   }

   @Override
   public final void e(double var1) {
      this.t();
      KrunchNativePhysicsWorldReference var3 = this.d;
      setBuoyancyFactor(this.d.b, this.a, this.e, var1);
      this.q();
   }

   @Override
   public final Hb e() {
      this.t();
      byte[] var1 = new byte[80];
      KrunchNativePhysicsWorldReference var2 = this.d;
      getInertiaData(this.d.b, this.a, this.e, var1);
      ByteBuffer var24;
      (var24 = ByteBuffer.wrap(var1)).order(ByteOrder.LITTLE_ENDIAN);
      double var4 = var24.getDouble();
      double var6 = var24.getDouble();
      double var8 = var24.getDouble();
      double var10 = var24.getDouble();
      double var12 = var24.getDouble();
      double var14 = var24.getDouble();
      double var16 = var24.getDouble();
      double var18 = var24.getDouble();
      double var20 = var24.getDouble();
      double var22 = var24.getDouble();
      Matrix3d var25 = new Matrix3d(var6, var12, var18, var8, var14, var20, var10, var16, var22);
      return new Hb(var4, var25);
   }

   @Override
   public final void a(Hb var1) {
      this.t();
      var1 = var1;
      ByteBuffer var2;
      (var2 = ByteBuffer.allocate(80)).order(ByteOrder.LITTLE_ENDIAN);
      var2.putDouble(var1.a);
      var2.putDouble(var1.b.m00());
      var2.putDouble(var1.b.m10());
      var2.putDouble(var1.b.m20());
      var2.putDouble(var1.b.m01());
      var2.putDouble(var1.b.m11());
      var2.putDouble(var1.b.m21());
      var2.putDouble(var1.b.m02());
      var2.putDouble(var1.b.m12());
      var2.putDouble(var1.b.m22());
      byte[] var5 = var2.array();
      KrunchNativePhysicsWorldReference var3 = this.d;
      setInertiaData(this.d.b, this.a, this.e, var5);
      this.q();
   }

   @Override
   public final double j() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getCollisionShapeScaling(this.d.b, this.a, this.e);
   }

   @Override
   public final void d(double var1) {
      this.t();
      KrunchNativePhysicsWorldReference var3 = this.d;
      setCollisionShapeScaling(this.d.b, this.a, this.e, var1);
      this.q();
   }

   private void t() {
      if (this.p()) {
         throw new Hl("The underlying rigid body has been deleted!");
      }
   }

   @Override
   public final boolean p() {
      this.u();
      return this.v();
   }

   private void u() {
      if (!this.v()) {
         KrunchNativePhysicsWorldReference var1 = this.d;
         if (this.d.c) {
            this.e = -1;
         } else {
            var1 = this.d;
            this.e = getCachedRigidBodyIndex(this.d.b, this.a, this.e);
         }
      }
   }

   private boolean v() {
      return this.e == -1;
   }

   @Override
   public final He b() {
      return this.d;
   }

   @Override
   public final void a(Vector3dc var1, Vector3dc var2) {
      this.t();
      KrunchNativePhysicsWorldReference var3 = this.d;
      addInvariantForceAtPosToNextPhysTick(this.d.b, this.a, this.e, var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
      this.q();
   }

   @Override
   public final void e(Vector3dc var1) {
      this.t();
      KrunchNativePhysicsWorldReference var2 = this.d;
      addInvariantForceToNextPhysTick(this.d.b, this.a, this.e, var1.x(), var1.y(), var1.z());
      this.q();
   }

   @Override
   public final void c(Vector3dc var1) {
      this.t();
      KrunchNativePhysicsWorldReference var2 = this.d;
      addInvariantTorqueToNextPhysTick(this.d.b, this.a, this.e, var1.x(), var1.y(), var1.z());
      this.q();
   }

   @Override
   public final void d(Vector3dc var1) {
      this.t();
      KrunchNativePhysicsWorldReference var2 = this.d;
      addRotDependentForceToNextPhysTick(this.d.b, this.a, this.e, var1.x(), var1.y(), var1.z());
      this.q();
   }

   @Override
   public final void b(Vector3dc var1) {
      this.t();
      KrunchNativePhysicsWorldReference var2 = this.d;
      addRotDependentTorqueToNextPhysTick(this.d.b, this.a, this.e, var1.x(), var1.y(), var1.z());
      this.q();
   }

   @Override
   public final boolean a(AABBd var1) {
      this.t();
      double[] var2 = new double[6];
      KrunchNativePhysicsWorldReference var3 = this.d;
      if (!getAABB(this.d.b, this.a, this.e, var2)) {
         return false;
      }

      var1.minX = var2[0];
      var1.minY = var2[1];
      var1.minZ = var2[2];
      var1.maxX = var2[3];
      var1.maxY = var2[4];
      var1.maxZ = var2[5];
      return true;
   }

   @Override
   public final boolean m() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getDoFluidDrag(this.d.b, this.a, this.e);
   }

   @Override
   public final void b(boolean var1) {
      this.t();
      KrunchNativePhysicsWorldReference var2 = this.d;
      setDoFluidDrag(this.d.b, this.a, this.e, var1);
   }

   @Override
   public final int n() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getCollisionMask(this.d.b, this.a, this.e);
   }

   @Override
   public final void a(int var1) {
      this.t();
      KrunchNativePhysicsWorldReference var2 = this.d;
      setCollisionMask(this.d.b, this.a, this.e, var1);
   }

   @Override
   public final T c() {
      return this.f;
   }

   @Override
   public final void a(T var1) {
      this.t();
      long var2 = ((IB)var1).b();
      KrunchNativePhysicsWorldReference var4 = this.d;
      setCollisionShape(this.d.b, this.a, this.e, var2, var1 instanceof KrunchNativeVoxelShapeReference);
      this.f = (T)var1;
      this.q();
   }

   @Override
   public final Hf o() {
      this.t();
      byte[] var1 = new byte[56];
      KrunchNativePhysicsWorldReference var2 = this.d;
      if (getKinematicTarget(this.d.b, this.a, this.e, var1)) {
         ByteBuffer var20;
         (var20 = ByteBuffer.wrap(var1)).order(ByteOrder.LITTLE_ENDIAN);
         ByteBuffer var21;
         double var6 = (var21 = var20).getDouble();
         double var8 = var21.getDouble();
         double var10 = var21.getDouble();
         double var12 = var21.getDouble();
         double var14 = var21.getDouble();
         double var16 = var21.getDouble();
         double var18 = var21.getDouble();
         Vector3d var22 = new Vector3d(var6, var8, var10);
         Quaterniond var23 = new Quaterniond(var12, var14, var16, var18);
         return new Hf(var22, var23);
      } else {
         return null;
      }
   }

   @Override
   public final void a(Hf var1) {
      this.t();
      if (var1 != null) {
         var1 = var1;
         ByteBuffer var2;
         (var2 = ByteBuffer.allocate(56)).order(ByteOrder.LITTLE_ENDIAN);
         ByteBuffer var3 = var2;
         var1 = var1;
         var3.putDouble(var1.a.x());
         var3.putDouble(var1.a.y());
         var3.putDouble(var1.a.z());
         var3.putDouble(var1.b.x());
         var3.putDouble(var1.b.y());
         var3.putDouble(var1.b.z());
         var3.putDouble(var1.b.w());
         byte[] var7 = var2.array();
         KrunchNativePhysicsWorldReference var4 = this.d;
         setKinematicTarget(this.d.b, this.a, this.e, true, var7);
      } else {
         KrunchNativePhysicsWorldReference var8 = this.d;
         setKinematicTarget(this.d.b, this.a, this.e, false, new byte[0]);
      }

      this.q();
   }

   @Override
   public final void q() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      wakeUp(this.d.b, this.a, this.e);
   }

   @Override
   public final double s() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getLiquidCollisionOverlap(this.d.b, this.a, this.e);
   }

   @Override
   public final boolean r() {
      this.t();
      KrunchNativePhysicsWorldReference var1 = this.d;
      return isAsleep(this.d.b, this.a, this.e);
   }

   private Vector3d w() {
      this.t();
      double[] var1 = new double[3];
      KrunchNativePhysicsWorldReference var2 = this.d;
      getTotalInvariantForcesNextPhysTick(this.d.b, this.a, this.e, var1);
      return new Vector3d(var1[0], var1[1], var1[2]);
   }

   private Vector3d x() {
      this.t();
      double[] var1 = new double[3];
      KrunchNativePhysicsWorldReference var2 = this.d;
      getTotalInvariantTorquesNextPhysTick(this.d.b, this.a, this.e, var1);
      return new Vector3d(var1[0], var1[1], var1[2]);
   }

   private Vector3d y() {
      this.t();
      double[] var1 = new double[3];
      KrunchNativePhysicsWorldReference var2 = this.d;
      getTotalRotDependentForcesNextPhysTick(this.d.b, this.a, this.e, var1);
      return new Vector3d(var1[0], var1[1], var1[2]);
   }

   private Vector3d z() {
      this.t();
      double[] var1 = new double[3];
      KrunchNativePhysicsWorldReference var2 = this.d;
      getTotalRotDependentTorquesNextPhysTick(this.d.b, this.a, this.e, var1);
      return new Vector3d(var1[0], var1[1], var1[2]);
   }

   private List<Pair<Vector3dc, Vector3dc>> A() {
      this.t();
      KrunchNativePhysicsWorldReference var3 = this.d;
      int var1;
      double[] var2 = new double[(var1 = getInvariantForcesAtPosNextPhysTickCount(this.d.b, this.a, this.e)) * 6];
      var3 = this.d;
      getInvariantForcesAtPosNextPhysTick(this.d.b, this.a, this.e, var2);
      ArrayList var8 = new ArrayList();

      for (int var4 = 0; var4 < var1; var4++) {
         int var5 = var4 * 6;
         Vector3d var6 = new Vector3d(var2[var5], var2[var5 + 1], var2[var5 + 2]);
         Vector3d var9 = new Vector3d(var2[var5 + 3], var2[var5 + 4], var2[var5 + 5]);
         var8.add(new Pair(var6, var9));
      }

      return var8;
   }

   private boolean B() {
      KrunchNativePhysicsWorldReference var1 = this.d;
      return getIsStatic(this.d.b, this.a, this.e);
   }

   private static native int getCachedRigidBodyIndex(long var0, int var2, int var3);

   private static native void getPoseVel(long var0, int var2, int var3, byte[] var4);

   private static native void setPoseVel(long var0, int var2, int var3, byte[] var4);

   private static native double getDynamicFrictionCoefficient(long var0, int var2, int var3);

   private static native void setDynamicFrictionCoefficient(long var0, int var2, int var3, double var4);

   private static native boolean getIsStatic(long var0, int var2, int var3);

   private static native void setStatic(long var0, int var2, int var3, boolean var4);

   private static native double getRestitutionCoefficient(long var0, int var2, int var3);

   private static native void setRestitutionCoefficient(long var0, int var2, int var3, double var4);

   private static native double getStaticFrictionCoefficient(long var0, int var2, int var3);

   private static native void setStaticFrictionCoefficient(long var0, int var2, int var3, double var4);

   private static native Vector3dc getCollisionShapeOffset(long var0, int var2, int var3);

   private static native void setCollisionShapeOffset(long var0, int var2, int var3, double var4, double var6, double var8);

   private static native double getBuoyancyFactor(long var0, int var2, int var3);

   private static native void setBuoyancyFactor(long var0, int var2, int var3, double var4);

   private static native void getInertiaData(long var0, int var2, int var3, byte[] var4);

   private static native void setInertiaData(long var0, int var2, int var3, byte[] var4);

   private static native double getCollisionShapeScaling(long var0, int var2, int var3);

   private static native void setCollisionShapeScaling(long var0, int var2, int var3, double var4);

   private static native void addInvariantForceAtPosToNextPhysTick(
      long var0, int var2, int var3, double var4, double var6, double var8, double var10, double var12, double var14
   );

   private static native void addInvariantForceToNextPhysTick(long var0, int var2, int var3, double var4, double var6, double var8);

   private static native void addInvariantTorqueToNextPhysTick(long var0, int var2, int var3, double var4, double var6, double var8);

   private static native void addRotDependentForceToNextPhysTick(long var0, int var2, int var3, double var4, double var6, double var8);

   private static native void addRotDependentTorqueToNextPhysTick(long var0, int var2, int var3, double var4, double var6, double var8);

   private static native boolean getAABB(long var0, int var2, int var3, double[] var4);

   private static native void getTotalInvariantForcesNextPhysTick(long var0, int var2, int var3, double[] var4);

   private static native void getTotalInvariantTorquesNextPhysTick(long var0, int var2, int var3, double[] var4);

   private static native void getTotalRotDependentForcesNextPhysTick(long var0, int var2, int var3, double[] var4);

   private static native void getTotalRotDependentTorquesNextPhysTick(long var0, int var2, int var3, double[] var4);

   private static native int getInvariantForcesAtPosNextPhysTickCount(long var0, int var2, int var3);

   private static native void getInvariantForcesAtPosNextPhysTick(long var0, int var2, int var3, double[] var4);

   private static native boolean getDoFluidDrag(long var0, int var2, int var3);

   private static native void setDoFluidDrag(long var0, int var2, int var3, boolean var4);

   private static native void setCollisionMask(long var0, int var2, int var3, int var4);

   private static native int getCollisionMask(long var0, int var2, int var3);

   private static native void setCollisionShape(long var0, int var2, int var3, long var4, boolean var6);

   private static native boolean getKinematicTarget(long var0, int var2, int var3, byte[] var4);

   private static native void setKinematicTarget(long var0, int var2, int var3, boolean var4, byte[] var5);

   private static native void wakeUp(long var0, int var2, int var3);

   private static native double getLiquidCollisionOverlap(long var0, int var2, int var3);

   private static native boolean isAsleep(long var0, int var2, int var3);
}
