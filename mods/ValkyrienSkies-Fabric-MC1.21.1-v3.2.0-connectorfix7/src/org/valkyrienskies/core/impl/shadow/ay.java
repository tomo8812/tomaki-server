package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMember;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public final class ay extends aB<ResolvedField, Field> {
   private final aY<aC> f = new aY<>(this::z);

   protected ay(ResolvedField var1, ResolvedTypeWithMembers var2, aU var3) {
      this(var1, null, null, var2, false, var3);
   }

   private ay(ResolvedField var1, ResolvedType var2, String var3, ResolvedTypeWithMembers var4, boolean var5, aU var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public final ay a(ResolvedType var1) {
      return new ay((ResolvedField)this.f(), var1, this.l(), this.g(), this.j(), this.t());
   }

   public final ay a(String var1) {
      return new ay((ResolvedField)this.f(), this.i(), var1, this.g(), this.j(), this.t());
   }

   private ay y() {
      return (ay)super.e();
   }

   @Override
   protected final String a() {
      return this.m();
   }

   public final boolean b() {
      return ((ResolvedField)this.f()).isTransient();
   }

   public final aC c() {
      return this.f.a();
   }

   private aC z() {
      String var1 = this.k().substring(0, 1).toUpperCase() + this.k().substring(1);
      String var2 = "get" + var1;
      var1 = "is" + var1;
      return Stream.of(this.g().getMemberMethods())
         .filter(var0 -> ((Method)var0.getRawMember()).getParameterCount() == 0)
         .filter(ResolvedMember::isPublic)
         .filter(var2x -> var2x.getName().equals(var2) || var2x.getName().equals(var1))
         .findFirst()
         .map(var1x -> this.t().a(var1x, this.g()))
         .orElse(null);
   }

   @Override
   public final boolean d() {
      return this.c() != null;
   }

   @Override
   public final <A extends Annotation> A a(Class<A> var1) {
      AnnotatedType var2;
      AnnotatedType[] var3;
      return (var2 = ((Field)this.o()).getAnnotatedType()) instanceof AnnotatedParameterizedType
            && (var3 = ((AnnotatedParameterizedType)var2).getAnnotatedActualTypeArguments()).length == 1
         ? var3[0].getAnnotation(var1)
         : null;
   }

   @Override
   public final <A extends Annotation> A b(Class<A> var1) {
      Annotation var2;
      if ((var2 = this.d(var1)) == null) {
         aC var3;
         var2 = (var3 = this.c()) == null ? null : var3.d(var1);
      }

      return (A)var2;
   }

   @Override
   public final <A extends Annotation> A c(Class<A> var1) {
      Annotation var2;
      if ((var2 = this.a(var1)) == null) {
         aC var3;
         var2 = (var3 = this.c()) == null ? null : var3.a(var1);
      }

      return (A)var2;
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
      return (ay)super.e();
   }

   // $VF: synthetic method
   @Override
   public final aB b(ResolvedType var1) {
      return this.a(var1);
   }
}
