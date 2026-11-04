package org.valkyrienskies.physics_api_krunch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.joml.Vector3dc;
import org.valkyrienskies.core.impl.shadow.Hd;
import org.valkyrienskies.core.impl.shadow.Hl;

public class KrunchNativePhysicsThreadPoolReference implements AutoCloseable, Hd {
   public long a;
   private static final long b = -1L;

   public KrunchNativePhysicsThreadPoolReference(int threadCount) {
      this.a = createKrunchNativePhysicsThreadPoolReference(threadCount);
   }

   protected final long c() {
      return this.a;
   }

   @Override
   public void close() {
      this.d();
      if (!deletePhysicsThreadPool(this.a)) {
         throw new IllegalStateException(
            "Delete KrunchNativePhysicsThreadPoolReference failed! This KrunchNativePhysicsThreadPoolReference is currently in use!"
         );
      }

      this.a = -1L;
   }

   @Override
   protected void finalize() {
      if (this.a != -1L) {
         try {
            this.close();
            return;
         } catch (Exception var2) {
            System.out.println("Failed to finalize() KrunchNativePhysicsThreadPoolReference, this is a memory leak and a thread leak!");
            var2.printStackTrace();
         }
      }
   }

   private boolean e() {
      return this.a == -1L;
   }

   public void d() {
      if (this.e()) {
         throw new Hl("This shape has been deleted!");
      }
   }

   @Override
   public final int b() {
      this.d();
      return getMaxThreadCountNative(this.a);
   }

   @Override
   public final int a() {
      this.d();
      return getThreadCountNative(this.a);
   }

   @Override
   public final void a(int var1) {
      this.d();
      setThreadCountNative(this.a, var1);
   }

   @Override
   public final List<CompletableFuture<Void>> a(List<Hd.a> var1, double var2) {
      this.d();
      if (var1.isEmpty()) {
         return Collections.emptyList();
      }

      int var4;
      long[] var5 = new long[var4 = var1.size()];
      double[] var6 = new double[var4 * 3];

      for (int var7 = 0; var7 < var4; var7++) {
         Hd.a var8;
         KrunchNativePhysicsWorldReference var9 = (KrunchNativePhysicsWorldReference)(var8 = (Hd.a)var1.get(var7)).a;
         Vector3dc var11 = var8.b;
         var5[var7] = var9.b;
         int var12 = var7 * 3;
         var6[var12] = var11.x();
         var6[var12 + 1] = var11.y();
         var6[var12 + 2] = var11.z();
      }

      return Arrays.asList(this.simulateMultipleWorlds(this.a, var5, var6, var2));
   }

   private static native long createKrunchNativePhysicsThreadPoolReference(int var0);

   private static native int getMaxThreadCountNative(long var0);

   private static native int getThreadCountNative(long var0);

   public static native void setThreadCountNative(long var0, int var2);

   private static native boolean deletePhysicsThreadPool(long var0);

   public native CompletableFuture<Void>[] simulateMultipleWorlds(long var1, long[] var3, double[] var4, double var5);
}
