package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.valkyrienskies.physics_api_krunch.KrunchNativePhysicsWorldIsland;
import org.valkyrienskies.physics_api_krunch.KrunchNativePhysicsWorldReference;

public final class Iy {
   public static List<He.c> a(ByteBuffer var0, KrunchNativePhysicsWorldReference var1) {
      int var2 = var0.getInt();
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 < var2; var4++) {
         int var7 = var4;
         KrunchNativePhysicsWorldReference var6 = var1;
         ByteBuffer var5 = var0;
         int var8 = var0.getInt();
         IntArrayList var9 = new IntArrayList(var8);

         for (int var10 = 0; var10 < var8; var10++) {
            int var11 = var5.getInt();
            var9.add(var11);
         }

         KrunchNativePhysicsWorldIsland var12 = new KrunchNativePhysicsWorldIsland(var7, var9, var6);
         var3.add(var12);
      }

      return var3;
   }

   private static KrunchNativePhysicsWorldIsland a(ByteBuffer var0, KrunchNativePhysicsWorldReference var1, int var2) {
      int var3 = var0.getInt();
      IntArrayList var4 = new IntArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         int var6 = var0.getInt();
         var4.add(var6);
      }

      return new KrunchNativePhysicsWorldIsland(var2, var4, var1);
   }
}
