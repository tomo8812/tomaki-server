package org.valkyrienskies.core.impl.shadow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public final class IF {
   private static int a;
   private static int b;

   private static void a(Hg var0, ByteBuffer var1) {
      var1.putDouble(var0.b.x());
      var1.putDouble(var0.b.y());
      var1.putDouble(var0.b.z());
      var1.putDouble(var0.c.x());
      var1.putDouble(var0.c.y());
      var1.putDouble(var0.c.z());
      var1.putDouble(var0.c.w());
      var1.putDouble(var0.d.x());
      var1.putDouble(var0.d.y());
      var1.putDouble(var0.d.z());
      var1.putDouble(var0.e.x());
      var1.putDouble(var0.e.y());
      var1.putDouble(var0.e.z());
   }

   public static byte[] a(Hg var0) {
      ByteBuffer var1;
      (var1 = ByteBuffer.allocate(104)).order(ByteOrder.LITTLE_ENDIAN);
      a(var0, var1);
      return var1.array();
   }

   private static Hg a(ByteBuffer var0) {
      double var1 = var0.getDouble();
      double var3 = var0.getDouble();
      double var5 = var0.getDouble();
      double var7 = var0.getDouble();
      double var9 = var0.getDouble();
      double var11 = var0.getDouble();
      double var13 = var0.getDouble();
      double var15 = var0.getDouble();
      double var17 = var0.getDouble();
      double var19 = var0.getDouble();
      double var21 = var0.getDouble();
      double var23 = var0.getDouble();
      double var25 = var0.getDouble();
      Vector3d var27 = new Vector3d(var1, var3, var5);
      Quaterniond var28 = new Quaterniond(var7, var9, var11, var13);
      Vector3d var2 = new Vector3d(var15, var17, var19);
      Vector3d var29 = new Vector3d(var21, var23, var25);
      return new Hg(var27, var28, var2, var29);
   }

   public static Hg a(byte[] var0) {
      ByteBuffer var1;
      (var1 = ByteBuffer.wrap(var0)).order(ByteOrder.LITTLE_ENDIAN);
      return a(var1);
   }

   private static void a(Hf var0, ByteBuffer var1) {
      var1.putDouble(var0.a.x());
      var1.putDouble(var0.a.y());
      var1.putDouble(var0.a.z());
      var1.putDouble(var0.b.x());
      var1.putDouble(var0.b.y());
      var1.putDouble(var0.b.z());
      var1.putDouble(var0.b.w());
   }

   public static byte[] a(Hf var0) {
      ByteBuffer var1;
      (var1 = ByteBuffer.allocate(56)).order(ByteOrder.LITTLE_ENDIAN);
      a(var0, var1);
      return var1.array();
   }

   private static Hf b(ByteBuffer var0) {
      double var1 = var0.getDouble();
      double var3 = var0.getDouble();
      double var5 = var0.getDouble();
      double var7 = var0.getDouble();
      double var9 = var0.getDouble();
      double var11 = var0.getDouble();
      double var13 = var0.getDouble();
      Vector3d var15 = new Vector3d(var1, var3, var5);
      Quaterniond var16 = new Quaterniond(var7, var9, var11, var13);
      return new Hf(var15, var16);
   }

   public static Hf b(byte[] var0) {
      ByteBuffer var1;
      (var1 = ByteBuffer.wrap(var0)).order(ByteOrder.LITTLE_ENDIAN);
      return b(var1);
   }
}
