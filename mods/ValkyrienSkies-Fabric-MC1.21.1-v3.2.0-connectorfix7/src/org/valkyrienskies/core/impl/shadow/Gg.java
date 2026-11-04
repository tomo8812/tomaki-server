package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class Gg<S> {
   public final ArrayList<Gu<S>> a = new ArrayList<>();

   private void a(Gu<S> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(var1);
   }

   private void b(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gu.a.a((S[])Arrays.copyOf(var1, var1.length)));
   }

   public final void a(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gu.a.b((S[])Arrays.copyOf(var1, var1.length)));
   }

   public final void b(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gu.a.c((S[])Arrays.copyOf(var1, var1.length)));
   }

   private void a(Gx<S>... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gu.a.a(Arrays.copyOf(var1, var1.length)));
   }

   public final void a(Function1<? super Gh<S>, Unit> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gu.a.a(var1));
   }

   private void c(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gu.a.d((S[])Arrays.copyOf(var1, var1.length)));
   }

   private void d(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gu.a.e((S[])Arrays.copyOf(var1, var1.length)));
   }

   public final void a(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gu.a.f((S[])Arrays.copyOf(var1, var1.length)));
   }

   private void e(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      ArrayList var10000 = this.a;
      Object[] var2 = Arrays.copyOf(var1, var1.length);
      Intrinsics.checkNotNullParameter(var2, "");
      (var1 = new Gu[2])[0] = Gu.a.c(Arrays.copyOf(var2, var2.length));
      var1[1] = Gu.a.d(Arrays.copyOf(var2, var2.length));
      var10000.add(Gu.a.a(var1));
   }

   private void a(Predicate<Thread> var1, S... var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      this.a.add(Gu.a.a(var1, (S[])Arrays.copyOf(var2, var2.length)));
   }

   public final List<Gu<S>> a() {
      return this.a;
   }
}
