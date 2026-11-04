package org.valkyrienskies.physics_api_krunch;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBi;
import org.valkyrienskies.core.impl.shadow.GY;
import org.valkyrienskies.core.impl.shadow.Hj;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.Hp;
import org.valkyrienskies.core.impl.shadow.IB;
import org.valkyrienskies.core.impl.shadow.Ik;
import org.valkyrienskies.physics_api_krunch.voxel.KrunchLod1BlockRegistry;
import org.valkyrienskies.physics_api_krunch.voxel.KrunchUnsafeVoxelChunk16Reference;
import org.valkyrienskies.physics_api_krunch.voxel.KrunchVoxelChunk16;

class KrunchNativeVoxelShapeReference implements Hp, IB {
   private static final long a = 0L;
   private static int b;
   private static final long c = -1L;
   private long d;
   private final KrunchLod1BlockRegistry e;

   protected KrunchNativeVoxelShapeReference(long shapeAddress, KrunchLod1BlockRegistry lod1BlockRegistry) {
      this.d = shapeAddress;
      this.e = lod1BlockRegistry;
   }

   @Override
   public void close() {
      this.h();
      if (!deleteVoxelShape(this.d)) {
         throw new Hj("Delete voxel shape failed! This shape is currently in use!");
      }

      this.d = -1L;
   }

   @Override
   protected void finalize() {
      if (this.d != -1L) {
         this.close();
      }
   }

   @Override
   public final boolean c() {
      this.h();
      return getIsVoxelTerrainFullyLoaded(this.d);
   }

   @Override
   public final void a(boolean var1) {
      this.h();
      setIsVoxelTerrainFullyLoaded(this.d, var1);
   }

   @Override
   public final boolean a(AABBi var1) {
      this.h();
      int[] var2 = new int[6];
      if (!getVoxelShapeAABB(this.d, var2)) {
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
   public final long b() {
      this.h();
      return this.d;
   }

   @Override
   public final void a() {
      this.h();
      bakeVoxelShape(this.d);
   }

   @Override
   public final KrunchVoxelChunk16 e(int var1, int var2, int var3) {
      this.h();
      long var4;
      return (var4 = copyVoxel16Chunk(this.d, var1, var2, var3)) != 0L ? new KrunchVoxelChunk16(var4, this.e) : null;
   }

   @Override
   public final boolean f(int var1, int var2, int var3) {
      this.h();
      return voxel16ChunkExists(this.d, var1, var2, var3);
   }

   @Override
   public final KrunchVoxelChunk16 a(int var1, int var2, int var3, Ik var4) {
      this.h();
      long var5;
      return (var5 = insertChunk(this.d, var1, var2, var3, ((KrunchVoxelChunk16)var4).d(), true)) != 0L ? new KrunchVoxelChunk16(var5, this.e) : null;
   }

   @Override
   public final void b(int var1, int var2, int var3, Ik var4) {
      this.h();
      if (insertChunk(this.d, var1, var2, var3, ((KrunchVoxelChunk16)var4).d(), false) != 0L) {
         throw new IllegalStateException("This should not be possible!");
      }
   }

   @Override
   public final KrunchVoxelChunk16 a(int var1, int var2, int var3) {
      this.h();
      long var4;
      return (var4 = insertAirChunk(this.d, var1, var2, var3, true)) != 0L ? new KrunchVoxelChunk16(var4, this.e) : null;
   }

   @Override
   public final void b(int var1, int var2, int var3) {
      this.h();
      if (insertAirChunk(this.d, var1, var2, var3, false) != 0L) {
         throw new IllegalStateException("This should not be possible!");
      }
   }

   @Override
   public final KrunchVoxelChunk16 c(int var1, int var2, int var3) {
      this.h();
      long var4;
      return (var4 = deleteChunk(this.d, var1, var2, var3, true)) != 0L ? new KrunchVoxelChunk16(var4, this.e) : null;
   }

   @Override
   public final void d(int var1, int var2, int var3) {
      this.h();
      if (deleteChunk(this.d, var1, var2, var3, false) != 0L) {
         throw new IllegalStateException("This should not be possible!");
      }
   }

   @Override
   public final boolean i_() {
      return getIsBaked(this.d);
   }

   @Override
   public final GY e() {
      this.h();
      return this.e;
   }

   @Override
   public final KrunchUnsafeVoxelChunk16Reference g(int var1, int var2, int var3) {
      this.h();
      long var4;
      return (var4 = getVoxel16ChunkPtrUnsafe(this.d, var1, var2, var3)) != 0L ? new KrunchUnsafeVoxelChunk16Reference(var4, this.e) : null;
   }

   @Override
   public final void a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.h();
      setDefinedAABBNative(this.d, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public final int d() {
      this.h();
      return getVoxelChunkCountNative(this.d);
   }

   @Override
   public final void a(Vector3dc var1) {
      this.h();
      setVoxelOffsetNative(this.d, var1.x(), var1.y(), var1.z());
   }

   @Override
   public final Vector3dc f() {
      this.h();
      double[] var1 = new double[3];
      getVoxelOffsetNative(this.d, var1);
      return new Vector3d(var1[0], var1[1], var1[2]);
   }

   private static native boolean getIsVoxelTerrainFullyLoaded(long var0);

   private static native void setIsVoxelTerrainFullyLoaded(long var0, boolean var2);

   private static native boolean getIsBaked(long var0);

   private static native boolean getVoxelShapeAABB(long var0, int[] var2);

   private static native void bakeVoxelShape(long var0);

   private static native long deleteChunk(long var0, int var2, int var3, int var4, boolean var5);

   private static native long insertChunk(long var0, int var2, int var3, int var4, long var5, boolean var7);

   private static native long insertAirChunk(long var0, int var2, int var3, int var4, boolean var5);

   private static native long copyVoxel16Chunk(long var0, int var2, int var3, int var4);

   private static native boolean voxel16ChunkExists(long var0, int var2, int var3, int var4);

   private static native boolean deleteVoxelShape(long var0);

   private static native long getVoxel16ChunkPtrUnsafe(long var0, int var2, int var3, int var4);

   private static native void setDefinedAABBNative(long var0, int var2, int var3, int var4, int var5, int var6, int var7);

   private static native int getVoxelChunkCountNative(long var0);

   private static native void setVoxelOffsetNative(long var0, double var2, double var4, double var6);

   private static native void getVoxelOffsetNative(long var0, double[] var2);

   private int h(int var1, int var2, int var3) {
      this.h();
      return getSolidVoxelState(this.d, var1, var2, var3);
   }

   private int i(int var1, int var2, int var3) {
      this.h();
      return getLiquidVoxelState(this.d, var1, var2, var3);
   }

   private List<Vector3ic> g() {
      this.h();
      int var1;
      int[] var2 = new int[(var1 = getSolidSetVoxelsSize(this.d)) * 3];
      getSolidSetVoxels(this.d, var2);
      ArrayList var3 = new ArrayList(var1);

      for (int var4 = 0; var4 < var1; var4++) {
         var3.add(new Vector3i(var2[var4 * 3], var2[var4 * 3 + 1], var2[var4 * 3 + 2]));
      }

      return var3;
   }

   @Override
   public final boolean j_() {
      return this.d == -1L;
   }

   private void h() {
      if (this.j_()) {
         throw new Hl("This shape has been deleted!");
      }
   }

   private static native int getSolidVoxelState(long var0, int var2, int var3, int var4);

   private static native int getLiquidVoxelState(long var0, int var2, int var3, int var4);

   private static native int getSolidSetVoxelsSize(long var0);

   private static native void getSolidSetVoxels(long var0, int[] var2);
}
