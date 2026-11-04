package org.valkyrienskies.physics_api_krunch.voxel;

import org.valkyrienskies.core.impl.shadow.Ij;

public class KrunchUnsafeVoxelChunk16Reference implements Ij {
   private final long a;
   private final KrunchLod1BlockRegistry b;

   public KrunchUnsafeVoxelChunk16Reference(long chunkAddress, KrunchLod1BlockRegistry lod1BlockRegistry) {
      this.a = chunkAddress;
      this.b = lod1BlockRegistry;
   }

   private KrunchVoxelChunk16 b() {
      long var1 = copyVoxel16ChunkUnsafe(this.a);
      return new KrunchVoxelChunk16(var1, this.b);
   }

   private static native long copyVoxel16ChunkUnsafe(long var0);

   // $VF: synthetic method
   @Override
   public final KrunchVoxelChunk16 a() {
      KrunchUnsafeVoxelChunk16Reference var1 = this;
      long var2 = copyVoxel16ChunkUnsafe(this.a);
      return new KrunchVoxelChunk16(var2, var1.b);
   }
}
