package org.valkyrienskies.physics_api_krunch.voxel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.valkyrienskies.core.impl.shadow.GY;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.Hm;
import org.valkyrienskies.core.impl.shadow.Ik;
import org.valkyrienskies.core.impl.shadow.In;
import org.valkyrienskies.core.impl.shadow.Iq;
import org.valkyrienskies.core.impl.shadow.Ir;

public class KrunchVoxelChunk16 implements Ik {
   private static final long a = 0L;
   private long b;
   private final KrunchLod1BlockRegistry c;
   private final List<Iq> d;

   public KrunchVoxelChunk16(long chunkAddress, KrunchLod1BlockRegistry lod1BlockRegistry) {
      this.b = chunkAddress;
      this.c = lod1BlockRegistry;
      this.d = new ArrayList<>();
   }

   public KrunchVoxelChunk16(KrunchLod1BlockRegistry lod1BlockRegistry) {
      this.b = createEmptyVoxelChunk16Native();
      this.c = lod1BlockRegistry;
      this.d = new ArrayList<>();
   }

   public final long d() {
      this.e();
      long var1 = this.b;
      this.b = 0L;
      return var1;
   }

   @Override
   public final void a(Hm var1) {
      this.e();

      for (Iq var3 : this.d) {
         var1.a().position(0);
         if (var3 instanceof In) {
            In var14 = (In)var3;
            ByteBuffer var10 = var1.a();
            In var9 = var14;
            var10.putInt(var9.b);
            var10.putInt(var9.c);
            var10.putInt(var9.d);

            for (int var11 = 0; var11 < 16; var11++) {
               for (int var12 = 0; var12 < 16; var12++) {
                  for (int var13 = 0; var13 < 16; var13++) {
                     var10.putInt(var9.a(var11, var12, var13));
                  }
               }
            }

            applyDenseVoxelChunkUpdate(this.b, this.c.e(), var1.b(), var1.c());
         } else {
            if (!(var3 instanceof Ir)) {
               throw new IllegalStateException("Unsupported voxel update!");
            }

            Ir var10000 = (Ir)var3;
            ByteBuffer var4 = var1.a();
            Ir var8 = var10000;
            var4.putInt(var8.b);
            var4.putInt(var8.c);
            var4.putInt(var8.d);
            var4.putInt(var8.e.size());

            for (int var5 = 0; var5 < var8.e.size(); var5++) {
               short var6 = var8.e.getShort(var5);
               int var7 = var8.f.getInt(var5);
               var4.putShort(var6);
               var4.putInt(var7);
            }

            applySparseVoxelChunkUpdate(this.b, this.c.e(), var1.b(), var1.c());
         }
      }

      this.d.clear();
      bakeVoxel16Chunk(this.b, this.c.e());
   }

   @Override
   public final boolean a() {
      this.e();
      return this.d.isEmpty();
   }

   @Override
   public final void a(Iq var1) {
      this.e();
      this.d.add(var1);
   }

   @Override
   public final GY c() {
      this.e();
      return this.c;
   }

   @Override
   public void close() {
      if (!this.b()) {
         deleteChunk(this.b);
         this.b = 0L;
      }
   }

   @Override
   protected void finalize() {
      if (!this.b()) {
         this.close();
      }
   }

   @Override
   public final boolean b() {
      return this.b == 0L;
   }

   private void e() {
      if (this.b()) {
         throw new Hl("This shape has been deleted!");
      }
   }

   private int a(int var1, int var2, int var3) {
      this.e();
      return getLod1SolidBlockStateId(this.b, var1, var2, var3);
   }

   private int b(int var1, int var2, int var3) {
      this.e();
      return getLod1LiquidBlockStateId(this.b, var1, var2, var3);
   }

   private static native void applyDenseVoxelChunkUpdate(long var0, long var2, long var4, int var6);

   private static native void applySparseVoxelChunkUpdate(long var0, long var2, long var4, int var6);

   private static native void bakeVoxel16Chunk(long var0, long var2);

   private static native void deleteChunk(long var0);

   private static native long createEmptyVoxelChunk16Native();

   private static native int getLod1SolidBlockStateId(long var0, int var2, int var3, int var4);

   private static native int getLod1LiquidBlockStateId(long var0, int var2, int var3, int var4);
}
