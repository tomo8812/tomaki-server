package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class aN extends aO<aV> {
   public Comparator<aB<?, ?>> a = aZ.c;
   public bq b = null;
   public final List<av> c = new ArrayList<>();
   public final List<aR> d = new ArrayList<>();
   public final List<aT> e = new ArrayList<>();
   private final List<as<aV, String>> f = new ArrayList<>();
   private final List<as<aV, String>> g = new ArrayList<>();

   public final aN a(Comparator<aB<?, ?>> var1) {
      this.a = var1;
      return this;
   }

   public final Comparator<aB<?, ?>> a() {
      return this.a;
   }

   private aN a(bq var1) {
      this.b = var1;
      return this;
   }

   public final bq b() {
      return this.b;
   }

   public final aN a(av var1) {
      this.c.add(var1);
      return this;
   }

   public final List<av> c() {
      return Collections.unmodifiableList(this.c);
   }

   public final aN a(bv var1) {
      this.d.add(var1);
      return this;
   }

   public final List<aR> d() {
      return Collections.unmodifiableList(this.d);
   }

   public final aN a(aS var1) {
      this.e.add(var1);
      return this;
   }

   public final List<aT> e() {
      return Collections.unmodifiableList(this.e);
   }

   private aN d(as<aV, String> var1) {
      this.f.add(var1);
      return this;
   }

   public final String a(aV var1) {
      return a(this.f, var1);
   }

   private aN e(as<aV, String> var1) {
      this.g.add(var1);
      return this;
   }

   public final String b(aV var1) {
      return a(this.g, var1);
   }

   private aN f(as<aV, String> var1) {
      return (aN)super.H(var1);
   }

   public final aN a(as<aV, String> var1) {
      return (aN)super.G(var1);
   }

   private aN g(as<aV, Object> var1) {
      return (aN)super.F(var1);
   }

   private aN h(as<aV, Collection<?>> var1) {
      return (aN)super.E(var1);
   }

   public final aN b(as<aV, Type> var1) {
      return (aN)super.D(var1);
   }

   public final aN c(as<aV, Map<String, Type>> var1) {
      return (aN)super.C(var1);
   }

   private aN i(as<aV, Integer> var1) {
      return (aN)super.B(var1);
   }

   private aN j(as<aV, Integer> var1) {
      return (aN)super.A(var1);
   }

   private aN k(as<aV, String> var1) {
      return (aN)super.z(var1);
   }

   private aN l(as<aV, String> var1) {
      return (aN)super.y(var1);
   }

   private aN m(as<aV, BigDecimal> var1) {
      return (aN)super.x(var1);
   }

   private aN n(as<aV, BigDecimal> var1) {
      return (aN)super.w(var1);
   }

   private aN o(as<aV, BigDecimal> var1) {
      return (aN)super.v(var1);
   }

   private aN p(as<aV, BigDecimal> var1) {
      return (aN)super.u(var1);
   }

   private aN I(as<aV, BigDecimal> var1) {
      return (aN)super.t(var1);
   }

   private aN J(as<aV, Integer> var1) {
      return (aN)super.s(var1);
   }

   private aN K(as<aV, Integer> var1) {
      return (aN)super.r(var1);
   }

   private aN L(as<aV, Boolean> var1) {
      return (aN)super.q(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO q(as var1) {
      as var2 = var1;
      return (aN)super.q(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO r(as var1) {
      as var2 = var1;
      return (aN)super.r(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO s(as var1) {
      as var2 = var1;
      return (aN)super.s(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO t(as var1) {
      as var2 = var1;
      return (aN)super.t(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO u(as var1) {
      as var2 = var1;
      return (aN)super.u(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO v(as var1) {
      as var2 = var1;
      return (aN)super.v(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO w(as var1) {
      as var2 = var1;
      return (aN)super.w(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO x(as var1) {
      as var2 = var1;
      return (aN)super.x(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO y(as var1) {
      as var2 = var1;
      return (aN)super.y(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO z(as var1) {
      as var2 = var1;
      return (aN)super.z(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO A(as var1) {
      as var2 = var1;
      return (aN)super.A(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO B(as var1) {
      as var2 = var1;
      return (aN)super.B(var2);
   }

   // $VF: synthetic method
   @Override
   public final aO C(as var1) {
      return this.c(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO D(as var1) {
      return this.b(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO E(as var1) {
      as var2 = var1;
      return (aN)super.E(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO F(as var1) {
      as var2 = var1;
      return (aN)super.F(var2);
   }

   // $VF: synthetic method
   @Override
   public final aO G(as var1) {
      return this.a(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO H(as var1) {
      as var2 = var1;
      return (aN)super.H(var2);
   }
}
