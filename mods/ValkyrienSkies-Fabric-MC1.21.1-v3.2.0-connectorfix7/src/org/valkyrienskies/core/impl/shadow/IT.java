package org.valkyrienskies.core.impl.shadow;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class IT {
   private IT() {
   }

   public static void a(Id var0, ByteBuffer var1) {
      a(var0.a, var1);
      var1.putFloat(var0.b);
      var1.putFloat(var0.c);
      var1.putFloat(var0.d);
      var1.putInt(var0.e);
   }

   public static Id a(ByteBuffer var0) {
      If var1 = c(var0);
      float var2 = var0.getFloat();
      float var3 = var0.getFloat();
      float var4 = var0.getFloat();
      int var5 = var0.getInt();
      return new Id(var1, var2, var3, var4, var5);
   }

   public static void a(Ib var0, ByteBuffer var1) {
      a(var0.a, var1);
      var1.putFloat(var0.b);
      var1.putFloat(var0.c);
      a(var0.d, var1);
      var1.putInt(var0.e);
   }

   public static Ib b(ByteBuffer var0) {
      Ih var1 = f(var0);
      float var2 = var0.getFloat();
      float var3 = var0.getFloat();
      Vector3f var4 = i(var0);
      int var5 = var0.getInt();
      return new Ib(var1, var2, var3, var4, var5);
   }

   private static void a(If var0, ByteBuffer var1) {
      var1.putInt(var0.f().ordinal());
      switch (var0.f()) {
         case SOLID_BOXES:
            a((Ie)var0, var1);
            return;
         case BELT:
            a((Ic)var0, var1);
      }
   }

   private static If c(ByteBuffer var0) {
      Ig var1 = Ig.values()[var0.getInt()];
      switch (var1) {
         case SOLID_BOXES:
            return d(var0);
         case BELT:
            return e(var0);
         default:
            throw new RuntimeException("Failed to deserialize unknown Lod1SolidCollisionShape");
      }
   }

   private static void a(Ie var0, ByteBuffer var1) {
      a(var0.a, var1);
      a(var0.b, var1);
      var1.putInt(var0.c.size());
      Iterator var2 = var0.c.iterator();

      while (var2.hasNext()) {
         a((Ih)var2.next(), var1);
      }

      var1.putInt(var0.d.size());
      var2 = var0.d.iterator();

      while (var2.hasNext()) {
         a((Ih)var2.next(), var1);
      }
   }

   private static Ie d(ByteBuffer var0) {
      Ih var1 = f(var0);
      List var2 = g(var0);
      int var3 = var0.getInt();
      ArrayList var4 = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(f(var0));
      }

      int var8 = var0.getInt();
      ArrayList var7 = new ArrayList(var8);

      for (int var6 = 0; var6 < var8; var6++) {
         var7.add(f(var0));
      }

      return new Ie(var1, var2, var4, var7);
   }

   private static void a(Ic var0, ByteBuffer var1) {
      a(var0.a, var1);
      a(var0.b, var1);
      a(var0.c, var1);
      var1.putInt(var0.d.ordinal());
      a(var0.e, var1);
   }

   private static Ic e(ByteBuffer var0) {
      Ih var1 = f(var0);
      List var2 = g(var0);
      Ih var3 = f(var0);
      HZ var4 = HZ.values()[var0.getInt()];
      Vector3f var5 = i(var0);
      return new Ic(var1, var2, var3, var4, var5);
   }

   public static void a(Ih var0, ByteBuffer var1) {
      var1.putInt(var0.b);
   }

   private static Ih f(ByteBuffer var0) {
      return Ih.a.a(var0.getInt());
   }

   public static void a(List<HX> var0, ByteBuffer var1) {
      var1.putInt(var0.size());
      Iterator var3 = var0.iterator();

      while (var3.hasNext()) {
         a((HX)var3.next(), var1);
      }
   }

   private static List<HX> g(ByteBuffer var0) {
      int var1 = var0.getInt();
      ArrayList var2 = new ArrayList(var1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.add(h(var0));
      }

      return var2;
   }

   private static void a(HX var0, ByteBuffer var1) {
      a(var0.a, var1);
      var1.putFloat(var0.b);
   }

   private static HX h(ByteBuffer var0) {
      Vector3f var1 = i(var0);
      float var2 = var0.getFloat();
      return new HX(var1, var2);
   }

   public static void a(Vector3fc var0, ByteBuffer var1) {
      var1.putFloat(var0.x());
      var1.putFloat(var0.y());
      var1.putFloat(var0.z());
   }

   private static Vector3f i(ByteBuffer var0) {
      float var1 = var0.getFloat();
      float var2 = var0.getFloat();
      float var3 = var0.getFloat();
      return new Vector3f(var1, var2, var3);
   }
}
