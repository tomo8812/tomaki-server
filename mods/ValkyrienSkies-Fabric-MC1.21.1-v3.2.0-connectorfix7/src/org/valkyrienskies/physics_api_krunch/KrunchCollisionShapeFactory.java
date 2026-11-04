package org.valkyrienskies.physics_api_krunch;

import org.joml.Vector3dc;
import org.joml.Vector3ic;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.impl.shadow.GT;
import org.valkyrienskies.core.impl.shadow.GU;
import org.valkyrienskies.core.impl.shadow.GV;
import org.valkyrienskies.core.impl.shadow.GX;
import org.valkyrienskies.core.impl.shadow.GY;
import org.valkyrienskies.core.impl.shadow.Hk;
import org.valkyrienskies.core.impl.shadow.Hp;
import org.valkyrienskies.core.impl.shadow.Hq;
import org.valkyrienskies.physics_api_krunch.voxel.KrunchLod1BlockRegistry;

public class KrunchCollisionShapeFactory implements GV {
   private static KrunchNativeVoxelShapeReference b(Vector3ic var0, Vector3ic var1, AABBic var2, GY var3) {
      long var4 = createVoxelShape(
         var0.x(),
         var0.y(),
         var0.z(),
         var1.x(),
         var1.y(),
         var1.z(),
         var2.minX(),
         var2.minY(),
         var2.minZ(),
         var2.maxX(),
         var2.maxY(),
         var2.maxZ(),
         ((KrunchLod1BlockRegistry)var3).e()
      );
      KrunchNativeVoxelShapeReference var6;
      (var6 = new KrunchNativeVoxelShapeReference(var4, (KrunchLod1BlockRegistry)var3)).a(false);
      return var6;
   }

   private static KrunchNativeBoxShapeReference b(Vector3dc var0) {
      long var1 = createBoxShape(var0.x(), var0.y(), var0.z());
      return new KrunchNativeBoxShapeReference(var1);
   }

   private static KrunchNativeCapsuleShapeReference b(double var0, double var2) {
      long var4 = createCapsuleShape(var0, var2);
      return new KrunchNativeCapsuleShapeReference(var4);
   }

   private static KrunchNativeSphereShapeReference b(double var0) {
      long var2 = createSphereShape(var0);
      return new KrunchNativeSphereShapeReference(var2);
   }

   private static KrunchNativeWheelShapeReference b(double var0, double var2, int var4) {
      long var5 = createWheelShape(var0, var2, var4);
      return new KrunchNativeWheelShapeReference(var5);
   }

   @Override
   public final GX a() {
      long var1 = createCompoundShape();
      return new KrunchNativeCompoundShapeReference(var1);
   }

   private static native long createVoxelShape(
      int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, long var12
   );

   private static native long createBoxShape(double var0, double var2, double var4);

   private static native long createSphereShape(double var0);

   private static native long createCapsuleShape(double var0, double var2);

   private static native long createWheelShape(double var0, double var2, int var4);

   private static native long createCompoundShape();

   // $VF: synthetic method
   @Override
   public final GT a(Vector3dc var1) {
      Vector3dc var5;
      long var3 = createBoxShape((var5 = var1).x(), var5.y(), var5.z());
      return new KrunchNativeBoxShapeReference(var3);
   }

   // $VF: synthetic method
   @Override
   public final Hk a(double var1) {
      long var5 = createSphereShape(var1);
      return new KrunchNativeSphereShapeReference(var5);
   }

   // $VF: synthetic method
   @Override
   public final Hq a(double var1, double var3, int var5) {
      int var13 = var5;
      double var8 = var3;
      long var11 = createWheelShape(var1, var8, var13);
      return new KrunchNativeWheelShapeReference(var11);
   }

   // $VF: synthetic method
   @Override
   public final GU a(double var1, double var3) {
      double var7 = var3;
      long var9 = createCapsuleShape(var1, var7);
      return new KrunchNativeCapsuleShapeReference(var9);
   }

   // $VF: synthetic method
   @Override
   public final Hp a(Vector3ic var1, Vector3ic var2, AABBic var3, GY var4) {
      var4 = var4;
      var3 = var3;
      var2 = var2;
      Vector3ic var11 = var1;
      long var9 = createVoxelShape(
         var1.x(),
         var11.y(),
         var11.z(),
         var2.x(),
         var2.y(),
         var2.z(),
         var3.minX(),
         var3.minY(),
         var3.minZ(),
         var3.maxX(),
         var3.maxY(),
         var3.maxZ(),
         ((KrunchLod1BlockRegistry)var4).e()
      );
      KrunchNativeVoxelShapeReference var12;
      (var12 = new KrunchNativeVoxelShapeReference(var9, (KrunchLod1BlockRegistry)var4)).a(false);
      return var12;
   }
}
