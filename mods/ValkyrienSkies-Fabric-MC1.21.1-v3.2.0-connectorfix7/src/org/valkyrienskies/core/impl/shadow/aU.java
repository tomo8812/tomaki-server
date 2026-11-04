package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class aU {
   public final TypeResolver a = new TypeResolver();
   private final MemberResolver c = new MemberResolver(this.a);
   private final AnnotationConfiguration d;
   final boolean b;

   public aU(AnnotationConfiguration var1) {
      this(var1, false);
   }

   public aU(AnnotationConfiguration var1, aK var2) {
      this(var1, var2.k());
   }

   private aU(AnnotationConfiguration var1, boolean var2) {
      this.d = var1;
      this.b = var2;
   }

   public final boolean a() {
      return this.b;
   }

   public final ResolvedType a(Type var1, Type... var2) {
      return this.a.resolve(var1, var2);
   }

   public final ResolvedType a(ResolvedType var1, Class<?> var2) {
      return this.a.resolveSubtype(var1, var2);
   }

   public final ResolvedTypeWithMembers a(ResolvedType var1) {
      return this.c.resolve(var1, this.d, null);
   }

   public final ay a(ResolvedField var1, ResolvedTypeWithMembers var2) {
      return new ay(var1, var2, this);
   }

   public final aC a(ResolvedMethod var1, ResolvedTypeWithMembers var2) {
      return new aC(var1, var2, this);
   }

   public final aV b(ResolvedType var1) {
      return new aV(var1, this);
   }

   public final ResolvedType a(ResolvedType var1, Class<?> var2, int var3) {
      List var4;
      if ((var4 = var1.typeParametersFor(var2)) != null
         && (var4.isEmpty() || var4.size() > var3)
         && (!var4.isEmpty() || var2.getTypeParameters().length > var3)) {
         return var4.isEmpty() ? this.a(Object.class) : (ResolvedType)var4.get(var3);
      } else {
         return null;
      }
   }

   public static boolean c(ResolvedType var0) {
      return var0.isArray() || var0.isInstanceOf(Collection.class);
   }

   public final ResolvedType d(ResolvedType var1) {
      ResolvedType var2;
      if ((var2 = var1.getArrayElementType()) == null && c(var1)) {
         var2 = this.a(var1, Iterable.class, 0);
      }

      return var2;
   }

   public final String e(ResolvedType var1) {
      return this.a(var1, true);
   }

   public final String f(ResolvedType var1) {
      return this.a(var1, false);
   }

   private String a(ResolvedType var1, boolean var2) {
      Class var3 = var1.getErasedType();
      String var5 = var2 ? var3.getSimpleName() : var3.getTypeName();
      List var4;
      if (!(var4 = var1.getTypeParameters()).isEmpty()) {
         var5 = var5 + var4.stream().map(var2x -> this.a(var2x, var2)).collect(Collectors.joining(", ", "<", ">"));
      }

      return var5;
   }

   public final String g(ResolvedType var1) {
      ResolvedType var2 = var1;
      return this.a(var2, true);
   }
}
