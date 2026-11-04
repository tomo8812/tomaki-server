package org.valkyrienskies.core.impl.shadow;

import org.joml.Vector3dc;
import org.joml.primitives.AABBi;
import org.valkyrienskies.physics_api_krunch.voxel.KrunchUnsafeVoxelChunk16Reference;
import org.valkyrienskies.physics_api_krunch.voxel.KrunchVoxelChunk16;

public interface Hp extends GW {
   KrunchVoxelChunk16 a(int var1, int var2, int var3, Ik var4);

   void b(int var1, int var2, int var3, Ik var4);

   KrunchVoxelChunk16 a(int var1, int var2, int var3);

   void b(int var1, int var2, int var3);

   KrunchVoxelChunk16 c(int var1, int var2, int var3);

   void d(int var1, int var2, int var3);

   void a();

   boolean i_();

   KrunchVoxelChunk16 e(int var1, int var2, int var3);

   boolean f(int var1, int var2, int var3);

   KrunchUnsafeVoxelChunk16Reference g(int var1, int var2, int var3);

   void a(int var1, int var2, int var3, int var4, int var5, int var6);

   boolean c();

   void a(boolean var1);

   int d();

   boolean a(AABBi var1);

   GY e();

   Vector3dc f();

   void a(Vector3dc var1);
}
