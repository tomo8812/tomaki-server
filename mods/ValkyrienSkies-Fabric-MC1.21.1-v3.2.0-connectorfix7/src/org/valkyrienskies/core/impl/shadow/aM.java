package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class aM<M extends aB<?, ?>> extends aO<M> {
   public final List<ax<M>> a = new ArrayList<>();
   public final List<aA<M>> b = new ArrayList<>();
   private final List<Predicate<M>> c = new ArrayList<>();
   private final List<Predicate<M>> d = new ArrayList<>();
   private final List<Predicate<M>> e = new ArrayList<>();
   private final List<Predicate<M>> f = new ArrayList<>();
   private final List<as<M, Boolean>> g = new ArrayList<>();
   private final List<as<M, List<ResolvedType>>> h = new ArrayList<>();
   private final List<as<M, String>> i = new ArrayList<>();

   public final aM<M> a(ax<M> var1) {
      this.a.add(var1);
      return this;
   }

   public final List<ax<M>> a() {
      return Collections.unmodifiableList(this.a);
   }

   @Deprecated
   public final aM<M> a(az<M> var1) {
      return this.b(var1);
   }

   public final aM<M> b(aA<M> var1) {
      this.b.add(var1);
      return this;
   }

   public final List<aA<M>> b() {
      return Collections.unmodifiableList(this.b);
   }

   public final aM<M> a(Predicate<M> var1) {
      this.c.add(var1);
      return this;
   }

   public final boolean a(M var1) {
      return this.c.stream().anyMatch(var1x -> var1x.test((M)var1));
   }

   public final aM<M> b(Predicate<M> var1) {
      this.d.add(var1);
      return this;
   }

   public final boolean b(M var1) {
      return this.d.stream().anyMatch(var1x -> var1x.test((M)var1));
   }

   public final aM<M> c(Predicate<M> var1) {
      this.e.add(var1);
      return this;
   }

   public final boolean c(M var1) {
      return this.e.stream().anyMatch(var1x -> var1x.test((M)var1));
   }

   public final aM<M> d(Predicate<M> var1) {
      this.f.add(var1);
      return this;
   }

   public final boolean d(M var1) {
      return this.f.stream().anyMatch(var1x -> var1x.test((M)var1));
   }

   public final aM<M> a(as<M, Boolean> var1) {
      this.g.add(var1);
      return this;
   }

   public final Boolean e(M var1) {
      Set var2;
      return (var2 = this.g.stream().map(var1x -> var1x.apply((M)var1)).filter(Objects::nonNull).collect(Collectors.toSet())).isEmpty()
         ? null
         : var2.stream().anyMatch(var0 -> var0);
   }

   @Deprecated
   private aM<M> I(as<M, ResolvedType> var1) {
      this.h.add(var1x -> Optional.ofNullable((ResolvedType)var1.apply(var1x)).map(Collections::singletonList).orElse(null));
      return this;
   }

   public final aM<M> b(as<M, List<ResolvedType>> var1) {
      this.h.add(var1);
      return this;
   }

   public final List<ResolvedType> f(M var1) {
      return a(this.h, (M)var1);
   }

   public final aM<M> c(as<M, String> var1) {
      this.i.add(var1);
      return this;
   }

   public final String g(M var1) {
      return a(this.i, (M)var1);
   }

   public final aM<M> d(as<M, String> var1) {
      return (aM<M>)super.H(var1);
   }

   public final aM<M> e(as<M, String> var1) {
      return (aM<M>)super.G(var1);
   }

   public final aM<M> f(as<M, Object> var1) {
      return (aM<M>)super.F(var1);
   }

   public final aM<M> g(as<M, Collection<?>> var1) {
      return (aM<M>)super.E(var1);
   }

   private aM<M> J(as<M, Type> var1) {
      return (aM<M>)super.D(var1);
   }

   private aM<M> K(as<M, Map<String, Type>> var1) {
      return (aM<M>)super.C(var1);
   }

   public final aM<M> h(as<M, Integer> var1) {
      return (aM<M>)super.B(var1);
   }

   public final aM<M> i(as<M, Integer> var1) {
      return (aM<M>)super.A(var1);
   }

   public final aM<M> j(as<M, String> var1) {
      return (aM<M>)super.z(var1);
   }

   public final aM<M> k(as<M, String> var1) {
      return (aM<M>)super.y(var1);
   }

   public final aM<M> l(as<M, BigDecimal> var1) {
      return (aM<M>)super.x(var1);
   }

   public final aM<M> m(as<M, BigDecimal> var1) {
      return (aM<M>)super.w(var1);
   }

   public final aM<M> n(as<M, BigDecimal> var1) {
      return (aM<M>)super.v(var1);
   }

   public final aM<M> o(as<M, BigDecimal> var1) {
      return (aM<M>)super.u(var1);
   }

   public final aM<M> p(as<M, BigDecimal> var1) {
      return (aM<M>)super.t(var1);
   }

   private aM<M> L(as<M, Integer> var1) {
      return (aM<M>)super.s(var1);
   }

   private aM<M> M(as<M, Integer> var1) {
      return (aM<M>)super.r(var1);
   }

   private aM<M> N(as<M, Boolean> var1) {
      return (aM<M>)super.q(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO q(as var1) {
      as var2 = var1;
      return (aM)super.q(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO r(as var1) {
      as var2 = var1;
      return (aM)super.r(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO s(as var1) {
      as var2 = var1;
      return (aM)super.s(var2);
   }

   // $VF: synthetic method
   @Override
   public final aO t(as var1) {
      return this.p(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO u(as var1) {
      return this.o(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO v(as var1) {
      return this.n(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO w(as var1) {
      return this.m(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO x(as var1) {
      return this.l(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO y(as var1) {
      return this.k(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO z(as var1) {
      return this.j(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO A(as var1) {
      return this.i(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO B(as var1) {
      return this.h(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO C(as var1) {
      as var2 = var1;
      return (aM)super.C(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aO D(as var1) {
      as var2 = var1;
      return (aM)super.D(var2);
   }

   // $VF: synthetic method
   @Override
   public final aO E(as var1) {
      return this.g(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO F(as var1) {
      return this.f(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO G(as var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   @Override
   public final aO H(as var1) {
      return this.d(var1);
   }
}
