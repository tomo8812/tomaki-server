package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class bc implements aK {
   private final ObjectMapper a;
   private final aQ b;
   private final Set<aE> c;
   private final aN d;
   private final aM<ay> e;
   private final aM<aC> f;

   public bc(ObjectMapper var1, aQ var2, Set<aE> var3, aN var4, aM<ay> var5, aM<aC> var6) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
      this.f = var6;
   }

   private boolean a(aE var1) {
      return this.c.contains(var1);
   }

   @Override
   public final aQ a() {
      return this.b;
   }

   @Override
   public final String a(aP var1) {
      return var1.a(this.b);
   }

   @Override
   public final boolean b() {
      return this.a(aE.DEFINITIONS_FOR_ALL_OBJECTS);
   }

   @Override
   public final boolean c() {
      return this.a(aE.DEFINITION_FOR_MAIN_SCHEMA);
   }

   @Override
   public final boolean d() {
      return this.a(aE.INLINE_ALL_SCHEMAS);
   }

   @Override
   public final boolean f() {
      return this.a(aE.PLAIN_DEFINITION_KEYS);
   }

   @Override
   public final boolean g() {
      return this.a(aE.EXTRA_OPEN_API_FORMAT_VALUES);
   }

   @Override
   public final boolean h() {
      return this.a(aE.ALLOF_CLEANUP_AT_THE_END);
   }

   @Override
   public final boolean i() {
      return this.a(aE.PUBLIC_STATIC_FIELDS) || this.a(aE.NONPUBLIC_STATIC_FIELDS);
   }

   @Override
   public final boolean j() {
      return this.a(aE.STATIC_METHODS);
   }

   @Override
   public final boolean k() {
      return this.a(aE.FIELDS_DERIVED_FROM_ARGUMENTFREE_METHODS);
   }

   @Override
   public final boolean l() {
      return !this.a(aE.ENUM_KEYWORD_FOR_SINGLE_VALUES);
   }

   @Override
   public final boolean m() {
      return this.a(aE.NULLABLE_ARRAY_ITEMS_ALLOWED);
   }

   @Override
   public final boolean e() {
      return this.a(aE.SCHEMA_VERSION_INDICATOR);
   }

   @Override
   public final ObjectMapper n() {
      return this.a;
   }

   @Override
   public final ObjectNode o() {
      return this.a.createObjectNode();
   }

   @Override
   public final ArrayNode p() {
      return this.a.createArrayNode();
   }

   @Override
   public final int a(aB<?, ?> var1, aB<?, ?> var2) {
      aN var3 = this.d;
      return this.d.a.compare(var1, var2);
   }

   @Override
   public final bq q() {
      aN var1 = this.d;
      return this.d.b;
   }

   @Override
   public final <M extends aB<?, ?>> at a(M var1, aI var2, ax<M> var3) {
      at var4;
      if (var1 instanceof ay) {
         var4 = a(this.e, (ay)var1, var2, var3);
      } else {
         if (!(var1 instanceof aC)) {
            throw new IllegalArgumentException("Unexpected member scope: " + (var1 == null ? null : var1.getClass().getName()));
         }

         var4 = a(this.f, (aC)var1, var2, var3);
      }

      if (var4 == null) {
         var4 = this.a(var1.d, var2, null);
      }

      return var4;
   }

   private static <M extends aB<?, ?>> aw a(aM<M> var0, M var1, aI var2, ax<? extends M> var3) {
      List var4 = Collections.unmodifiableList(var0.a);
      aw var5;
      if (var3 != null && !var4.contains(var3)) {
         var5 = null;
      } else {
         int var6 = 1 + var4.indexOf(var3);
         var5 = var4.subList(var6, var4.size())
            .stream()
            .map(var2x -> var2x.provideCustomSchemaDefinition((M)var1, var2))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
      }

      return var5;
   }

   @Override
   public final at a(ResolvedType var1, aI var2, av var3) {
      aN var4 = this.d;
      List var6 = Collections.unmodifiableList(this.d.c);
      int var5 = 1 + var6.indexOf(var3);
      return var6.subList(var5, var6.size()).stream().map(var2x -> var2x.a(var1, var2)).filter(Objects::nonNull).findFirst().orElse(null);
   }

   @Override
   public final List<ResolvedType> a(ResolvedType var1, aI var2) {
      aN var3 = this.d;
      return Collections.unmodifiableList(this.d.d)
         .stream()
         .map(var2x -> var2x.b(var1, var2))
         .filter(Objects::nonNull)
         .findFirst()
         .orElseGet(Collections::emptyList);
   }

   @Override
   public final List<aT> r() {
      aN var1 = this.d;
      return Collections.unmodifiableList(this.d.e);
   }

   @Override
   public final List<aA<ay>> s() {
      aM var1 = this.e;
      return Collections.unmodifiableList(this.e.b);
   }

   @Override
   public final List<aA<aC>> t() {
      aM var1 = this.f;
      return Collections.unmodifiableList(this.f.b);
   }

   @Override
   public final boolean a(ay var1) {
      return Optional.ofNullable(this.e.e(var1)).orElseGet(() -> !var1.c && this.a(aE.NULLABLE_FIELDS_BY_DEFAULT));
   }

   @Override
   public final boolean a(aC var1) {
      return Optional.ofNullable(this.f.e(var1)).orElseGet(() -> !var1.c && this.a(aE.NULLABLE_METHOD_RETURN_VALUES_BY_DEFAULT));
   }

   @Override
   public final boolean e(ay var1) {
      return this.e.a(var1);
   }

   @Override
   public final boolean e(aC var1) {
      return this.f.a(var1);
   }

   @Override
   public final boolean b(ay var1) {
      return this.e.b(var1);
   }

   @Override
   public final boolean b(aC var1) {
      return this.f.b(var1);
   }

   @Override
   public final boolean c(ay var1) {
      return this.e.c(var1);
   }

   @Override
   public final boolean c(aC var1) {
      return this.f.c(var1);
   }

   @Override
   public final boolean d(ay var1) {
      return this.e.d(var1);
   }

   @Override
   public final boolean d(aC var1) {
      return this.f.d(var1);
   }

   @Override
   public final List<ResolvedType> g(ay var1) {
      return this.e.f(var1);
   }

   @Override
   public final List<ResolvedType> g(aC var1) {
      return this.f.f(var1);
   }

   @Override
   public final String h(ay var1) {
      return this.e.g(var1);
   }

   @Override
   public final String h(aC var1) {
      return this.f.g(var1);
   }

   @Override
   public final String a(aV var1) {
      return this.d.a(var1);
   }

   @Override
   public final String b(aV var1) {
      return this.d.b(var1);
   }

   @Override
   public final String i(ay var1) {
      return this.e.c(var1);
   }

   @Override
   public final String i(aC var1) {
      return this.f.c(var1);
   }

   @Override
   public final String c(aV var1) {
      return this.d.c(var1);
   }

   @Override
   public final String j(ay var1) {
      return this.e.d(var1);
   }

   @Override
   public final String j(aC var1) {
      return this.f.d(var1);
   }

   @Override
   public final String d(aV var1) {
      return this.d.d(var1);
   }

   @Override
   public final Object k(ay var1) {
      return this.e.e(var1);
   }

   @Override
   public final Object k(aC var1) {
      return this.f.e(var1);
   }

   @Override
   public final Object e(aV var1) {
      return this.d.e(var1);
   }

   @Override
   public final Collection<?> l(ay var1) {
      return this.e.f(var1);
   }

   @Override
   public final Collection<?> l(aC var1) {
      return this.f.f(var1);
   }

   @Override
   public final Collection<?> f(aV var1) {
      return this.d.f(var1);
   }

   @Override
   public final Type m(ay var1) {
      return this.e.g(var1);
   }

   @Override
   public final Type m(aC var1) {
      return this.f.g(var1);
   }

   @Override
   public final Type g(aV var1) {
      return this.d.g(var1);
   }

   @Override
   public final Map<String, Type> n(ay var1) {
      return this.e.h(var1);
   }

   @Override
   public final Map<String, Type> n(aC var1) {
      return this.f.h(var1);
   }

   @Override
   public final Map<String, Type> h(aV var1) {
      return this.d.h(var1);
   }

   @Override
   public final Integer o(ay var1) {
      return this.e.i(var1);
   }

   @Override
   public final Integer o(aC var1) {
      return this.f.i(var1);
   }

   @Override
   public final Integer i(aV var1) {
      return this.d.i(var1);
   }

   @Override
   public final Integer p(ay var1) {
      return this.e.j(var1);
   }

   @Override
   public final Integer p(aC var1) {
      return this.f.j(var1);
   }

   @Override
   public final Integer j(aV var1) {
      return this.d.j(var1);
   }

   @Override
   public final String q(ay var1) {
      return this.e.k(var1);
   }

   @Override
   public final String q(aC var1) {
      return this.f.k(var1);
   }

   @Override
   public final String k(aV var1) {
      return this.d.k(var1);
   }

   @Override
   public final String r(ay var1) {
      return this.e.l(var1);
   }

   @Override
   public final String r(aC var1) {
      return this.f.l(var1);
   }

   @Override
   public final String l(aV var1) {
      return this.d.l(var1);
   }

   @Override
   public final BigDecimal s(ay var1) {
      return this.e.m(var1);
   }

   @Override
   public final BigDecimal s(aC var1) {
      return this.f.m(var1);
   }

   @Override
   public final BigDecimal m(aV var1) {
      return this.d.m(var1);
   }

   @Override
   public final BigDecimal t(ay var1) {
      return this.e.n(var1);
   }

   @Override
   public final BigDecimal t(aC var1) {
      return this.f.n(var1);
   }

   @Override
   public final BigDecimal n(aV var1) {
      return this.d.n(var1);
   }

   @Override
   public final BigDecimal u(ay var1) {
      return this.e.o(var1);
   }

   @Override
   public final BigDecimal u(aC var1) {
      return this.f.o(var1);
   }

   @Override
   public final BigDecimal o(aV var1) {
      return this.d.o(var1);
   }

   @Override
   public final BigDecimal v(ay var1) {
      return this.e.p(var1);
   }

   @Override
   public final BigDecimal v(aC var1) {
      return this.f.p(var1);
   }

   @Override
   public final BigDecimal p(aV var1) {
      return this.d.p(var1);
   }

   @Override
   public final BigDecimal w(ay var1) {
      return this.e.q(var1);
   }

   @Override
   public final BigDecimal w(aC var1) {
      return this.f.q(var1);
   }

   @Override
   public final BigDecimal q(aV var1) {
      return this.d.q(var1);
   }

   @Override
   public final Integer x(ay var1) {
      return this.e.r(var1);
   }

   @Override
   public final Integer x(aC var1) {
      return this.f.r(var1);
   }

   @Override
   public final Integer r(aV var1) {
      return this.d.r(var1);
   }

   @Override
   public final Integer y(ay var1) {
      return this.e.s(var1);
   }

   @Override
   public final Integer y(aC var1) {
      return this.f.s(var1);
   }

   @Override
   public final Integer s(aV var1) {
      return this.d.s(var1);
   }

   @Override
   public final Boolean z(ay var1) {
      return this.e.t(var1);
   }

   @Override
   public final Boolean z(aC var1) {
      return this.f.t(var1);
   }

   @Override
   public final Boolean t(aV var1) {
      return this.d.t(var1);
   }
}
