package org.valkyrienskies.physics_api_krunch;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.impl.shadow.GW;
import org.valkyrienskies.core.impl.shadow.GX;
import org.valkyrienskies.core.impl.shadow.Ha;
import org.valkyrienskies.core.impl.shadow.Hg;
import org.valkyrienskies.core.impl.shadow.Hj;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.IB;
import org.valkyrienskies.core.impl.shadow.IF;

class KrunchNativeCompoundShapeReference implements GX, IB {
   private long a;
   private static final long b = -1L;
   private final Int2ObjectOpenHashMap c = new Int2ObjectOpenHashMap();

   protected KrunchNativeCompoundShapeReference(long shapeAddress) {
      this.a = shapeAddress;
   }

   @Override
   public void close() {
      this.d();
      if (!deleteCompoundShape(this.a)) {
         throw new Hj("Delete compound shape failed! This shape is currently in use!");
      }

      this.a = -1L;
   }

   @Override
   protected void finalize() {
      if (this.a != -1L) {
         this.close();
      }

      this.a = -1L;
   }

   @Override
   public final long b() {
      this.d();
      return this.a;
   }

   @Override
   public final boolean j_() {
      return this.a == -1L;
   }

   @Override
   public final Map<Integer, GX.a> a() {
      this.d();
      return this.c;
   }

   @Override
   public final boolean a(int var1) {
      this.d();
      return this.c.remove(var1) == null ? false : deleteSegmentNative(this.b(), var1);
   }

   @Override
   public final GX.a a(int var1, GW var2, Hg var3, double var4, Vector3dc var6, Ha var7) {
      this.d();
      if (this.c.containsKey(var1)) {
         throw new IllegalArgumentException("Segment with id " + var1 + " already exists!");
      }

      createSegmentNative(this.b(), var1);
      KrunchNativeCompoundShapeReference.a var8 = new KrunchNativeCompoundShapeReference.a(var1, var2, (byte)0);
      setSegmentCollisionShape(this.b(), var1, ((IB)var2).b());
      var8.a(var3);
      var8.a(var4);
      var8.a(var6);
      this.c.put(var1, var8);
      return var8;
   }

   private void d() {
      if (this.j_()) {
         throw new Hl("This shape has been deleted!");
      }
   }

   private static native boolean createSegmentNative(long var0, int var2);

   private static native boolean deleteSegmentNative(long var0, int var2);

   private static native boolean setSegmentCollisionShape(long var0, int var2, long var3);

   private static native boolean getCollisionShapeOffsetNative(long var0, int var2, double[] var3);

   private static native boolean setCollisionShapeOffsetNative(long var0, int var2, double[] var3);

   private static native double getCollisionShapeScalingNative(long var0, int var2);

   private static native double setCollisionShapeScalingNative(long var0, int var2, double var3);

   private static native void getPoseVelNative(long var0, int var2, byte[] var3);

   private static native void setPoseVelNative(long var0, int var2, byte[] var3);

   private static native boolean deleteCompoundShape(long var0);

   class a implements GX.a {
      private final int a;
      private GW b;
      private Ha c;

      private a(int var2, GW var3) {
         this.a = var2;
         this.b = var3;
         this.c = null;
      }

      @Override
      public final GW a() {
         KrunchNativeCompoundShapeReference.this.d();
         return this.b;
      }

      @Override
      public final void a(GW var1) {
         KrunchNativeCompoundShapeReference.this.d();
         this.b = var1;
         KrunchNativeCompoundShapeReference.setSegmentCollisionShape(KrunchNativeCompoundShapeReference.this.b(), this.a, ((IB)var1).b());
      }

      @Override
      public final Vector3dc d() {
         KrunchNativeCompoundShapeReference.this.d();
         double[] var1 = new double[3];
         if (!KrunchNativeCompoundShapeReference.getCollisionShapeOffsetNative(KrunchNativeCompoundShapeReference.this.a, this.a, var1)) {
            throw new IllegalStateException("Call to getCollisionShapeOffset() failed");
         } else {
            return new Vector3d(var1[0], var1[1], var1[2]);
         }
      }

      @Override
      public final void a(Vector3dc var1) {
         KrunchNativeCompoundShapeReference.this.d();
         if (!KrunchNativeCompoundShapeReference.setCollisionShapeOffsetNative(
            KrunchNativeCompoundShapeReference.this.a, this.a, new double[]{var1.x(), var1.y(), var1.z()}
         )) {
            throw new IllegalStateException("Call to setCollisionShapeOffset() failed");
         }
      }

      @Override
      public final double c() {
         KrunchNativeCompoundShapeReference.this.d();
         return KrunchNativeCompoundShapeReference.getCollisionShapeScalingNative(KrunchNativeCompoundShapeReference.this.a, this.a);
      }

      @Override
      public final void a(double var1) {
         KrunchNativeCompoundShapeReference.this.d();
         KrunchNativeCompoundShapeReference.setCollisionShapeScalingNative(KrunchNativeCompoundShapeReference.this.a, this.a, var1);
      }

      @Override
      public final Ha e() {
         KrunchNativeCompoundShapeReference.this.d();
         return this.c;
      }

      @Override
      public final void a(Ha var1) {
         KrunchNativeCompoundShapeReference.this.d();
         this.c = var1;
      }

      @Override
      public final Hg b() {
         KrunchNativeCompoundShapeReference.this.d();
         byte[] var1 = new byte[104];
         KrunchNativeCompoundShapeReference.getPoseVelNative(KrunchNativeCompoundShapeReference.this.a, this.a, var1);
         return IF.a(var1);
      }

      @Override
      public final void a(Hg var1) {
         KrunchNativeCompoundShapeReference.this.d();
         byte[] var2 = IF.a(var1);
         KrunchNativeCompoundShapeReference.setPoseVelNative(KrunchNativeCompoundShapeReference.this.a, this.a, var2);
      }

      // $VF: synthetic method
      a(int var2, GW var3, byte var4) {
         this(var2, var3);
      }
   }
}
