package org.valkyrienskies.physics_api_krunch.voxel;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import kotlin.NotImplementedError;
import org.valkyrienskies.core.impl.shadow.GY;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.Hm;
import org.valkyrienskies.core.impl.shadow.IT;
import org.valkyrienskies.core.impl.shadow.Ia;
import org.valkyrienskies.core.impl.shadow.Ib;
import org.valkyrienskies.core.impl.shadow.Ic;
import org.valkyrienskies.core.impl.shadow.Id;
import org.valkyrienskies.core.impl.shadow.Ie;
import org.valkyrienskies.core.impl.shadow.If;
import org.valkyrienskies.core.impl.shadow.Ih;

public class KrunchLod1BlockRegistry implements GY {
   private static final long e = 0L;
   private long f;
   private final Int2ObjectOpenHashMap g = new Int2ObjectOpenHashMap();
   private final Int2ObjectOpenHashMap h = new Int2ObjectOpenHashMap();
   private final Id i;
   private final Ib j;
   private final Ia k;

   public KrunchLod1BlockRegistry(Hm vsByteBuffer) {
      this.f = allocateNewKrunchLod1BlockRegistry();
      this.i = this.a(0, vsByteBuffer);
      this.j = this.b(0, vsByteBuffer);
      Hm var2 = vsByteBuffer;
      boolean var6 = false;
      KrunchLod1BlockRegistry var7 = this;
      this.f();
      Ia var10001;
      if (getLod1BlockState(var7.f, 0, var2.b(), var2.c())) {
         var2.a().position(0);
         int var3 = var2.a().getInt();
         int var4 = var2.a().getInt();
         Id var5;
         if ((var5 = var7.a(var3, var2)) == null) {
            throw new RuntimeException("Lod1SolidBlockState with id " + var3 + " not found!");
         }

         Ib var8;
         if ((var8 = var7.b(var4, var2)) == null) {
            throw new RuntimeException("Lod1LiquidBlockState with id " + var4 + " not found!");
         }

         var10001 = new Ia(var5, var8, 0);
      } else {
         var10001 = null;
      }

      this.k = var10001;
      this.g.put(0, this.i);
      this.h.put(0, this.j);
   }

   public final long e() {
      this.f();
      return this.f;
   }

   @Override
   public final void a(Id var1, Hm var2) {
      this.f();
      var2.a().position(0);
      ByteBuffer var4 = var2.a();
      Id var3 = var1;
      ByteBuffer var6 = var4;
      If var5 = var1.a;
      var6.putInt(var5.f().ordinal());
      switch (var5.f()) {
         case SOLID_BOXES:
            Ie var14 = (Ie)var5;
            var6 = var6;
            Ie var10 = var14;
            IT.a(var14.a, var6);
            IT.a(var10.b, var6);
            var6.putInt(var10.c.size());
            Iterator var7 = var10.c.iterator();

            while (var7.hasNext()) {
               IT.a((Ih)var7.next(), var6);
            }

            var6.putInt(var10.d.size());
            var7 = var10.d.iterator();

            while (var7.hasNext()) {
               IT.a((Ih)var7.next(), var6);
            }
            break;
         case BELT:
            Ic var10000 = (Ic)var5;
            var6 = var6;
            Ic var9 = var10000;
            IT.a(var10000.a, var6);
            IT.a(var9.b, var6);
            IT.a(var9.c, var6);
            var6.putInt(var9.d.ordinal());
            IT.a(var9.e, var6);
      }

      var4.putFloat(var3.b);
      var4.putFloat(var3.c);
      var4.putFloat(var3.d);
      var4.putInt(var3.e);
      if (!registerLod1SolidBlockState(this.f, var2.b(), var2.c())) {
         throw new IllegalArgumentException("Already existing Lod1SolidBlockState found for Lod1SolidBlockState.lod1SolidBlockStateId");
      }

      this.g.put(var1.e, var1);
   }

   @Override
   public final void a(Ib var1, Hm var2) {
      this.f();
      var2.a().position(0);
      ByteBuffer var4 = var2.a();
      Ib var3 = var1;
      IT.a(var1.a, var4);
      var4.putFloat(var3.b);
      var4.putFloat(var3.c);
      IT.a(var3.d, var4);
      var4.putInt(var3.e);
      if (!registerLod1LiquidBlockState(this.f, var2.b(), var2.c())) {
         throw new IllegalArgumentException("Already existing Lod1LiquidBlockState found for lod1LiquidBlockState.lod1LiquidBlockStateId");
      }

      this.h.put(var1.e, var1);
   }

   @Override
   public final Ia a(int var1, int var2, int var3) {
      this.f();
      Id var4;
      if ((var4 = (Id)this.g.get(var1)) == null) {
         throw new IllegalArgumentException("No Lod1SolidBlockState found for id " + var1);
      } else {
         Ib var5;
         if ((var5 = (Ib)this.h.get(var2)) == null) {
            throw new IllegalArgumentException("No Lod1LiquidBlockState found for id " + var2);
         } else if (!registerLod1BlockState(this.f, var3, var1, var2)) {
            throw new IllegalArgumentException("Already existing Lod1BlockState found for lod1BlockStateId");
         } else {
            return new Ia(var4, var5, var3);
         }
      }
   }

   @Override
   public final Id a(int var1, Hm var2) {
      this.f();
      if (!getLod1SolidBlockState(this.f, var1, var2.b(), var2.c())) {
         throw new IllegalArgumentException("No Lod1SolidBlockState found for lod1SolidBlockStateId");
      }

      var2.a().position(0);
      return IT.a(var2.a());
   }

   @Override
   public final Ib b(int var1, Hm var2) {
      this.f();
      if (!getLod1LiquidBlockState(this.f, var1, var2.b(), var2.c())) {
         throw new IllegalArgumentException("No Lod1LiquidBlockState found for lod1LiquidBlockStateId");
      }

      var2.a().position(0);
      return IT.b(var2.a());
   }

   @Override
   public final Ia c(int var1, Hm var2) {
      this.f();
      if (getLod1BlockState(this.f, 0, var2.b(), var2.c())) {
         var2.a().position(0);
         var1 = var2.a().getInt();
         int var3 = var2.a().getInt();
         Id var4;
         if ((var4 = this.a(var1, var2)) == null) {
            throw new RuntimeException("Lod1SolidBlockState with id " + var1 + " not found!");
         } else {
            Ib var6;
            if ((var6 = this.b(var3, var2)) == null) {
               throw new RuntimeException("Lod1LiquidBlockState with id " + var3 + " not found!");
            } else {
               return new Ia(var4, var6, 0);
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public final Map<Integer, Id> a(Hm var1) {
      throw new NotImplementedError();
   }

   @Override
   public final Map<Integer, Id> b(Hm var1) {
      throw new NotImplementedError();
   }

   @Override
   public final Map<Integer, Ia> c(Hm var1) {
      throw new NotImplementedError();
   }

   @Override
   public final Ib c() {
      return this.j;
   }

   @Override
   public final Id b() {
      return this.i;
   }

   @Override
   public final Ia d() {
      return this.k;
   }

   @Override
   public final boolean a() {
      return this.f == 0L;
   }

   @Override
   public void close() {
      if (!this.a()) {
         long var1 = this.f;
         this.f = 0L;
         deleteKrunchLod1BlockRegistry(var1);
      }
   }

   @Override
   protected void finalize() {
      if (!this.a()) {
         this.close();
      }
   }

   private void f() {
      if (this.a()) {
         throw new Hl("This shape has been deleted!");
      }
   }

   private static native long allocateNewKrunchLod1BlockRegistry();

   private static native void deleteKrunchLod1BlockRegistry(long var0);

   private static native boolean registerLod1SolidBlockState(long var0, long var2, int var4);

   private static native boolean registerLod1LiquidBlockState(long var0, long var2, int var4);

   private static native boolean registerLod1BlockState(long var0, int var2, int var3, int var4);

   private static native boolean getLod1SolidBlockState(long var0, int var2, long var3, int var5);

   private static native boolean getLod1LiquidBlockState(long var0, int var2, long var3, int var5);

   private static native boolean getLod1BlockState(long var0, int var2, long var3, int var5);
}
