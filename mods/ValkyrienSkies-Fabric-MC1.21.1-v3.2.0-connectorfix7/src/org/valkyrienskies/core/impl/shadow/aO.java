package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class aO<S extends aV> {
   private final List<as<S, String>> a = new ArrayList<>();
   private final List<as<S, String>> b = new ArrayList<>();
   private final List<as<S, Object>> c = new ArrayList<>();
   private final List<as<S, Collection<?>>> d = new ArrayList<>();
   private final List<as<S, Type>> e = new ArrayList<>();
   private final List<as<S, Map<String, Type>>> f = new ArrayList<>();
   private final List<as<S, Integer>> g = new ArrayList<>();
   private final List<as<S, Integer>> h = new ArrayList<>();
   private final List<as<S, String>> i = new ArrayList<>();
   private final List<as<S, String>> j = new ArrayList<>();
   private final List<as<S, BigDecimal>> k = new ArrayList<>();
   private final List<as<S, BigDecimal>> l = new ArrayList<>();
   private final List<as<S, BigDecimal>> m = new ArrayList<>();
   private final List<as<S, BigDecimal>> n = new ArrayList<>();
   private final List<as<S, BigDecimal>> o = new ArrayList<>();
   private final List<as<S, Integer>> p = new ArrayList<>();
   private final List<as<S, Integer>> q = new ArrayList<>();
   private final List<as<S, Boolean>> r = new ArrayList<>();

   protected static <S extends aV, R> R a(List<as<S, R>> var0, S var1) {
      return var0.stream().map(var1x -> (R)var1x.apply(var1)).filter(Objects::nonNull).findFirst().orElse(null);
   }

   public aO<S> H(as<S, String> var1) {
      this.a.add(var1);
      return this;
   }

   public final String c(S var1) {
      return a(this.a, (S)var1);
   }

   public aO<S> G(as<S, String> var1) {
      this.b.add(var1);
      return this;
   }

   public final String d(S var1) {
      return a(this.b, (S)var1);
   }

   public aO<S> F(as<S, Object> var1) {
      this.c.add(var1);
      return this;
   }

   public final Object e(S var1) {
      return a(this.c, (S)var1);
   }

   public aO<S> E(as<S, Collection<?>> var1) {
      this.d.add(var1);
      return this;
   }

   public final Collection<?> f(S var1) {
      return a(this.d, (S)var1);
   }

   public aO<S> D(as<S, Type> var1) {
      this.e.add(var1);
      return this;
   }

   public final Type g(S var1) {
      return a(this.e, (S)var1);
   }

   public aO<S> C(as<S, Map<String, Type>> var1) {
      this.f.add(var1);
      return this;
   }

   public final Map<String, Type> h(S var1) {
      return a(this.f, (S)var1);
   }

   public aO<S> B(as<S, Integer> var1) {
      this.g.add(var1);
      return this;
   }

   public final Integer i(S var1) {
      return a(this.g, (S)var1);
   }

   public aO<S> A(as<S, Integer> var1) {
      this.h.add(var1);
      return this;
   }

   public final Integer j(S var1) {
      return a(this.h, (S)var1);
   }

   public aO<S> z(as<S, String> var1) {
      this.i.add(var1);
      return this;
   }

   public final String k(S var1) {
      return a(this.i, (S)var1);
   }

   public aO<S> y(as<S, String> var1) {
      this.j.add(var1);
      return this;
   }

   public final String l(S var1) {
      return a(this.j, (S)var1);
   }

   public aO<S> x(as<S, BigDecimal> var1) {
      this.k.add(var1);
      return this;
   }

   public final BigDecimal m(S var1) {
      return a(this.k, (S)var1);
   }

   public aO<S> w(as<S, BigDecimal> var1) {
      this.l.add(var1);
      return this;
   }

   public final BigDecimal n(S var1) {
      return a(this.l, (S)var1);
   }

   public aO<S> v(as<S, BigDecimal> var1) {
      this.m.add(var1);
      return this;
   }

   public final BigDecimal o(S var1) {
      return a(this.m, (S)var1);
   }

   public aO<S> u(as<S, BigDecimal> var1) {
      this.n.add(var1);
      return this;
   }

   public final BigDecimal p(S var1) {
      return a(this.n, (S)var1);
   }

   public aO<S> t(as<S, BigDecimal> var1) {
      this.o.add(var1);
      return this;
   }

   public final BigDecimal q(S var1) {
      return a(this.o, (S)var1);
   }

   public aO<S> s(as<S, Integer> var1) {
      this.p.add(var1);
      return this;
   }

   public final Integer r(S var1) {
      return a(this.p, (S)var1);
   }

   public aO<S> r(as<S, Integer> var1) {
      this.q.add(var1);
      return this;
   }

   public final Integer s(S var1) {
      return a(this.q, (S)var1);
   }

   public aO<S> q(as<S, Boolean> var1) {
      this.r.add(var1);
      return this;
   }

   public final Boolean t(S var1) {
      return a(this.r, (S)var1);
   }
}
