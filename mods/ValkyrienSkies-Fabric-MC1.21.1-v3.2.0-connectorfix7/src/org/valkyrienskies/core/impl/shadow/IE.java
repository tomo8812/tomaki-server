package org.valkyrienskies.core.impl.shadow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.joml.Matrix3d;

public final class IE {
   private static int a;

   public static byte[] a(Hb var0) {
      ByteBuffer var1;
      (var1 = ByteBuffer.allocate(80)).order(ByteOrder.LITTLE_ENDIAN);
      var1.putDouble(var0.a);
      var1.putDouble(var0.b.m00());
      var1.putDouble(var0.b.m10());
      var1.putDouble(var0.b.m20());
      var1.putDouble(var0.b.m01());
      var1.putDouble(var0.b.m11());
      var1.putDouble(var0.b.m21());
      var1.putDouble(var0.b.m02());
      var1.putDouble(var0.b.m12());
      var1.putDouble(var0.b.m22());
      return var1.array();
   }

   public static Hb a(byte[] var0) {
      ByteBuffer var22;
      (var22 = ByteBuffer.wrap(var0)).order(ByteOrder.LITTLE_ENDIAN);
      double var2 = var22.getDouble();
      double var4 = var22.getDouble();
      double var6 = var22.getDouble();
      double var8 = var22.getDouble();
      double var10 = var22.getDouble();
      double var12 = var22.getDouble();
      double var14 = var22.getDouble();
      double var16 = var22.getDouble();
      double var18 = var22.getDouble();
      double var20 = var22.getDouble();
      Matrix3d var23 = new Matrix3d(var4, var10, var16, var6, var12, var18, var8, var14, var20);
      return new Hb(var2, var23);
   }
}
