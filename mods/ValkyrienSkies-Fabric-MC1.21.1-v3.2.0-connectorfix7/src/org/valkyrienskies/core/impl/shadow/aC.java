package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class aC extends aB<ResolvedMethod, Method> {
   private final aY<ay> f = new aY<>(this::B);

   protected aC(ResolvedMethod var1, ResolvedTypeWithMembers var2, aU var3) {
      this(var1, null, null, var2, false, var3);
   }

   private aC(ResolvedMethod var1, ResolvedType var2, String var3, ResolvedTypeWithMembers var4, boolean var5, aU var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public final aC a(ResolvedType var1) {
      return new aC((ResolvedMethod)this.f(), var1, this.l(), this.g(), this.j(), this.t());
   }

   public final aC a(String var1) {
      return new aC((ResolvedMethod)this.f(), this.i(), var1, this.g(), this.j(), this.t());
   }

   private aC y() {
      return (aC)super.e();
   }

   public final boolean b() {
      return this.u() == null;
   }

   private int z() {
      return ((ResolvedMethod)this.f()).getArgumentCount();
   }

   private List<ResolvedType> A() {
      return IntStream.range(0, this.z()).<ResolvedType>mapToObj(((ResolvedMethod)this.f())::getArgumentType).collect(Collectors.toList());
   }

   public final ay c() {
      return this.f.a();
   }

   private ay B() {
      if (this.u() != null && this.r() && this.z() <= 0) {
         String var1;
         if ((var1 = this.k()).startsWith("get") && var1.length() > 3 && Character.isUpperCase(var1.charAt(3))) {
            var1 = var1.substring(3, 4).toLowerCase() + var1.substring(4);
         } else if (var1.startsWith("is") && var1.length() > 2 && Character.isUpperCase(var1.charAt(2))) {
            var1 = var1.substring(2, 3).toLowerCase() + var1.substring(3);
         } else {
            var1 = null;
         }

         return var1 == null
            ? null
            : Stream.of(this.g().getMemberFields())
               .filter(var1x -> var1x.getName().equals(var1))
               .findFirst()
               .map(var1x -> this.t().a(var1x, this.g()))
               .orElse(null);
      } else {
         return null;
      }
   }

   @Override
   public final boolean d() {
      return this.c() != null;
   }

   @Override
   public final <A extends Annotation> A d(Class<A> var1) {
      Annotation var2;
      if ((var2 = super.d(var1)) == null) {
         var2 = ((Method)this.o()).getAnnotatedReturnType().getAnnotation(var1);
      }

      return (A)var2;
   }

   @Override
   public final <A extends Annotation> A a(Class<A> var1) {
      AnnotatedType var2;
      AnnotatedType[] var3;
      return (var2 = ((Method)this.o()).getAnnotatedReturnType()) instanceof AnnotatedParameterizedType
            && (var3 = ((AnnotatedParameterizedType)var2).getAnnotatedActualTypeArguments()).length == 1
         ? var3[0].getAnnotation(var1)
         : null;
   }

   @Override
   public final <A extends Annotation> A b(Class<A> var1) {
      Annotation var2;
      if ((var2 = this.d(var1)) == null) {
         ay var3;
         var2 = (var3 = this.c()) == null ? null : var3.d(var1);
      }

      return (A)var2;
   }

   @Override
   public final <A extends Annotation> A c(Class<A> var1) {
      Annotation var2;
      if ((var2 = this.a(var1)) == null) {
         ay var3;
         var2 = (var3 = this.c()) == null ? null : var3.a(var1);
      }

      return (A)var2;
   }

   @Override
   protected final String a() {
      String var1 = this.m();
      if (!this.t().b || this.z() != 0) {
         StringBuilder var10000 = new StringBuilder().append(var1);
         aC var2 = this;
         var1 = var10000.append(
               IntStream.range(0, var2.z())
                  .<ResolvedType>mapToObj(((ResolvedMethod)var2.f())::getArgumentType)
                  .collect(Collectors.toList())
                  .stream()
                  .map(this.t()::g)
                  .collect(Collectors.joining(", ", "(", ")"))
            )
            .toString();
      } else if (this.l() == null) {
         if (var1.startsWith("get") && var1.length() > 3) {
            var1 = Character.toLowerCase(var1.charAt(3)) + var1.substring(4);
         } else if (var1.startsWith("is") && var1.length() > 2) {
            var1 = Character.toLowerCase(var1.charAt(2)) + var1.substring(3);
         } else {
            var1 = var1 + "()";
         }
      }

      return var1;
   }

   // $VF: synthetic method
   @Override
   public final aB b(String var1) {
      return this.a(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final aB e() {
      return (aC)super.e();
   }

   // $VF: synthetic method
   @Override
   public final aB b(ResolvedType var1) {
      return this.a(var1);
   }
}
