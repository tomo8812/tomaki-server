package org.valkyrienskies.core.impl.shadow;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class Iu {
   private static void a(IR var0, ByteBuffer var1) {
      var1.putInt(var0.a);
      var1.putInt(var0.b);
      List var4 = var0.c;
      var1.putInt(var4.size());

      for (HU var2 : var4) {
         var1.put((byte)var2.a.ordinal());
         Vector3dc var3 = var2.b;
         var1.putDouble(var3.x()).putDouble(var3.y()).putDouble(var3.z());
         var3 = var2.c;
         var1.putDouble(var3.x()).putDouble(var3.y()).putDouble(var3.z());
         var1.putFloat(var2.d);
         Vector3dc var6 = var2.e;
         var1.putDouble(var6.x()).putDouble(var6.y()).putDouble(var6.z());
      }
   }

   public static IR a(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      ArrayList var4 = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         HV var6 = HV.values()[var0.get()];
         HV var7 = var6;
         ByteBuffer var11 = var0;
         Vector3d var8 = new Vector3d(var11.getDouble(), var11.getDouble(), var11.getDouble());
         Vector3d var9 = new Vector3d(var11.getDouble(), var11.getDouble(), var11.getDouble());
         float var10 = var11.getFloat();
         Vector3d var12 = new Vector3d(var11.getDouble(), var11.getDouble(), var11.getDouble());
         HU var13 = new HU(var7, var8, var9, var10, var12);
         var4.add(var13);
      }

      return new IR(var1, var2, var4);
   }

   private static HU a(ByteBuffer var0, HV var1) {
      Vector3d var2 = new Vector3d(var0.getDouble(), var0.getDouble(), var0.getDouble());
      Vector3d var3 = new Vector3d(var0.getDouble(), var0.getDouble(), var0.getDouble());
      float var4 = var0.getFloat();
      Vector3d var5 = new Vector3d(var0.getDouble(), var0.getDouble(), var0.getDouble());
      return new HU(var1, var2, var3, var4, var5);
   }
}
