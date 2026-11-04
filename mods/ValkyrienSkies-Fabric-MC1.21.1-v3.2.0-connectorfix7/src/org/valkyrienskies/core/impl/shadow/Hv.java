package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import java.util.Map;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3f;

public final class Hv implements GY {
   private final Id e;
   private final Ib f;
   private final Ia g;
   private boolean h;

   public Hv() {
      Ih var10005 = Ih.a.a();
      List var10006 = CollectionsKt.emptyList();
      List var10007 = CollectionsKt.emptyList();
      Ih.a var1 = Ih.a;
      this.e = new Id(
         new Ie(var10005, var10006, var10007, CollectionsKt.listOf(Ih.a.a((byte)0, (byte)0, (byte)0, (byte)15, (byte)15, (byte)15))), 1.0F, 1.0F, 1.0F, 0
      );
      this.f = new Ib(Ih.a.a(), 0.0F, 0.0F, new Vector3f(), 0);
      this.g = new Ia(this.e, this.f, 0);
   }

   @Override
   public final Id b() {
      return this.e;
   }

   @Override
   public final Ib c() {
      return this.f;
   }

   @Override
   public final Ia d() {
      return this.g;
   }

   @Override
   public final void a(Id var1, Hm var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
   }

   @Override
   public final void a(Ib var1, Hm var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
   }

   @Override
   public final Ia a(int var1, int var2, int var3) {
      return this.g;
   }

   @Override
   public final Id a(int var1, Hm var2) {
      Intrinsics.checkNotNullParameter(var2, "");
      return null;
   }

   @Override
   public final Map<Integer, Id> a(Hm var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return MapsKt.emptyMap();
   }

   @Override
   public final Ib b(int var1, Hm var2) {
      Intrinsics.checkNotNullParameter(var2, "");
      return null;
   }

   @Override
   public final Map<Integer, Id> b(Hm var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return MapsKt.emptyMap();
   }

   @Override
   public final Ia c(int var1, Hm var2) {
      Intrinsics.checkNotNullParameter(var2, "");
      return null;
   }

   @Override
   public final Map<Integer, Ia> c(Hm var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return MapsKt.emptyMap();
   }

   @Override
   public final boolean a() {
      return this.h;
   }

   @Override
   public final void close() {
      this.h = true;
   }
}
