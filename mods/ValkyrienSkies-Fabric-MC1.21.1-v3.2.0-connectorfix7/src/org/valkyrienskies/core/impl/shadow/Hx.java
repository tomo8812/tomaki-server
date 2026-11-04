package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;

public final class Hx<T extends GW> implements Hc<T> {
   private final int b;
   private final He c;
   private Hg d;
   private Hb e;
   private boolean f;
   private double g;
   private double h;
   private double i;
   private double j;
   private Vector3dc k;
   private double l;
   private boolean m;
   private int n;
   private T o;
   private Hf p;
   boolean a;

   public Hx(
      int var1,
      He var2,
      Hg var3,
      Hb var4,
      boolean var5,
      double var6,
      double var8,
      double var10,
      double var12,
      Vector3dc var14,
      double var15,
      boolean var17,
      int var18,
      T var19,
      Hf var20
   ) {
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var14, "");
      super();
      this.b = var1;
      this.c = var2;
      this.d = var3;
      this.e = var4;
      this.f = true;
      this.g = 1.0;
      this.h = 1.0;
      this.i = 1.0;
      this.j = 1.0;
      this.k = var14;
      this.l = 1.0;
      this.m = true;
      this.n = 0;
      this.o = (T)var19;
      this.p = null;
   }

   @Override
   public final int a() {
      return this.b;
   }

   @Override
   public final He b() {
      return this.c;
   }

   @Override
   public final Hg d() {
      return this.d;
   }

   @Override
   public final void a(Hg var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.d = var1;
   }

   @Override
   public final Hb e() {
      return this.e;
   }

   @Override
   public final void a(Hb var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.e = var1;
   }

   @Override
   public final boolean f() {
      return this.f;
   }

   @Override
   public final void a(boolean var1) {
      this.f = var1;
   }

   @Override
   public final double g() {
      return this.g;
   }

   @Override
   public final void a(double var1) {
      this.g = var1;
   }

   @Override
   public final double h() {
      return this.h;
   }

   @Override
   public final void b(double var1) {
      this.h = var1;
   }

   @Override
   public final double i() {
      return this.i;
   }

   @Override
   public final void c(double var1) {
      this.i = var1;
   }

   @Override
   public final double j() {
      return this.j;
   }

   @Override
   public final void d(double var1) {
      this.j = var1;
   }

   @Override
   public final Vector3dc k() {
      return this.k;
   }

   @Override
   public final void a(Vector3dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.k = var1;
   }

   @Override
   public final double l() {
      return this.l;
   }

   @Override
   public final void e(double var1) {
      this.l = var1;
   }

   @Override
   public final boolean m() {
      return this.m;
   }

   @Override
   public final void b(boolean var1) {
      this.m = var1;
   }

   @Override
   public final int n() {
      return this.n;
   }

   @Override
   public final void a(int var1) {
      this.n = var1;
   }

   @Override
   public final T c() {
      return this.o;
   }

   @Override
   public final void a(T var1) {
      this.o = (T)var1;
   }

   @Override
   public final Hf o() {
      return this.p;
   }

   @Override
   public final void a(Hf var1) {
      this.p = var1;
   }

   private boolean t() {
      return this.a;
   }

   public final void c(boolean var1) {
      this.a = true;
   }

   @Override
   public final void b(Vector3dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
   }

   @Override
   public final void c(Vector3dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
   }

   @Override
   public final void d(Vector3dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
   }

   @Override
   public final void e(Vector3dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
   }

   @Override
   public final void a(Vector3dc var1, Vector3dc var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
   }

   @Override
   public final boolean p() {
      return this.a;
   }

   @Override
   public final boolean a(AABBd var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      var1.setMin(0.0, 0.0, 0.0);
      var1.setMax(0.0, 0.0, 0.0);
      return true;
   }

   @Override
   public final void q() {
   }

   @Override
   public final boolean r() {
      return true;
   }

   @Override
   public final double s() {
      return 0.0;
   }
}
