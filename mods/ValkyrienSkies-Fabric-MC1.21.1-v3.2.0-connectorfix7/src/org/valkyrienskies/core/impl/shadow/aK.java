package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface aK {
   aQ a();

   String a(aP var1);

   boolean b();

   boolean c();

   boolean d();

   boolean e();

   boolean f();

   boolean g();

   boolean h();

   boolean i();

   boolean j();

   boolean k();

   boolean l();

   boolean m();

   ObjectMapper n();

   ObjectNode o();

   ArrayNode p();

   int a(aB<?, ?> var1, aB<?, ?> var2);

   bq q();

   <M extends aB<?, ?>> at a(M var1, aI var2, ax<M> var3);

   at a(ResolvedType var1, aI var2, av var3);

   List<ResolvedType> a(ResolvedType var1, aI var2);

   List<aT> r();

   List<aA<ay>> s();

   List<aA<aC>> t();

   boolean a(ay var1);

   boolean a(aC var1);

   boolean b(ay var1);

   boolean b(aC var1);

   boolean c(ay var1);

   boolean c(aC var1);

   boolean d(ay var1);

   boolean d(aC var1);

   boolean e(ay var1);

   boolean e(aC var1);

   @Deprecated
   default ResolvedType f(ay var1) {
      List var2;
      return (var2 = this.g(var1)) != null && !var2.isEmpty() ? (ResolvedType)var2.get(0) : null;
   }

   @Deprecated
   default ResolvedType f(aC var1) {
      List var2;
      return (var2 = this.g(var1)) != null && !var2.isEmpty() ? (ResolvedType)var2.get(0) : null;
   }

   List<ResolvedType> g(ay var1);

   List<ResolvedType> g(aC var1);

   String h(ay var1);

   String h(aC var1);

   String a(aV var1);

   String b(aV var1);

   String i(ay var1);

   String i(aC var1);

   String c(aV var1);

   String j(ay var1);

   String j(aC var1);

   String d(aV var1);

   Object k(ay var1);

   Object k(aC var1);

   Object e(aV var1);

   Collection<?> l(ay var1);

   Collection<?> l(aC var1);

   Collection<?> f(aV var1);

   Type m(ay var1);

   Type m(aC var1);

   Type g(aV var1);

   Map<String, Type> n(ay var1);

   Map<String, Type> n(aC var1);

   Map<String, Type> h(aV var1);

   Integer o(ay var1);

   Integer o(aC var1);

   Integer i(aV var1);

   Integer p(ay var1);

   Integer p(aC var1);

   Integer j(aV var1);

   String q(ay var1);

   String q(aC var1);

   String k(aV var1);

   String r(ay var1);

   String r(aC var1);

   String l(aV var1);

   BigDecimal s(ay var1);

   BigDecimal s(aC var1);

   BigDecimal m(aV var1);

   BigDecimal t(ay var1);

   BigDecimal t(aC var1);

   BigDecimal n(aV var1);

   BigDecimal u(ay var1);

   BigDecimal u(aC var1);

   BigDecimal o(aV var1);

   BigDecimal v(ay var1);

   BigDecimal v(aC var1);

   BigDecimal p(aV var1);

   BigDecimal w(ay var1);

   BigDecimal w(aC var1);

   BigDecimal q(aV var1);

   Integer x(ay var1);

   Integer x(aC var1);

   Integer r(aV var1);

   Integer y(ay var1);

   Integer y(aC var1);

   Integer s(aV var1);

   Boolean z(ay var1);

   Boolean z(aC var1);

   Boolean t(aV var1);
}
