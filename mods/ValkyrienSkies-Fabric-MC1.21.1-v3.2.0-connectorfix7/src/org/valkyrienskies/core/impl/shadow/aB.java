package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedMember;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Optional;

public abstract class aB<M extends ResolvedMember<T>, T extends Member> extends aV {
   public final M a;
   private final ResolvedType f;
   private final String g;
   public final ResolvedTypeWithMembers b;
   public boolean c;
   private final aU h;
   private final aY<String> i = new aY<>(this::a);

   protected aB(M var1, ResolvedType var2, String var3, ResolvedTypeWithMembers var4, boolean var5, aU var6) {
      super(Optional.ofNullable(var2).orElseGet(var1::getType), var6);
      this.a = (M)var1;
      this.f = var2;
      this.g = var3;
      this.b = var4;
      this.c = var5;
      this.h = var6;
   }

   private void b() {
      this.c = true;
   }

   public abstract aB<M, T> b(ResolvedType var1);

   public aB<M, T> e() {
      if (!this.v()) {
         return this.b(this.f);
      }

      aB var1;
      (var1 = this.b(this.w())).c = true;
      return var1;
   }

   public abstract aB<M, T> b(String var1);

   public final M f() {
      return this.a;
   }

   public final ResolvedTypeWithMembers g() {
      return this.b;
   }

   public final ResolvedType h() {
      return this.a.getType();
   }

   public final ResolvedType i() {
      return this.f;
   }

   public final boolean j() {
      return this.c;
   }

   public String k() {
      return this.a.getName();
   }

   public final String l() {
      return this.g;
   }

   public final String m() {
      return Optional.ofNullable(this.g).orElseGet(this::k);
   }

   public final ResolvedType n() {
      return this.a.getDeclaringType();
   }

   public final T o() {
      return (T)this.a.getRawMember();
   }

   public final boolean p() {
      return this.a.isStatic();
   }

   public final boolean q() {
      return this.a.isFinal();
   }

   private boolean c() {
      return this.a.isPrivate();
   }

   private boolean d() {
      return this.a.isProtected();
   }

   public final boolean r() {
      return this.a.isPublic();
   }

   public <A extends Annotation> A d(Class<A> var1) {
      return (A)this.a.get(var1);
   }

   public abstract <A extends Annotation> A a(Class<A> var1);

   public abstract <A extends Annotation> A b(Class<A> var1);

   public final <A extends Annotation> A e(Class<A> var1) {
      return this.c ? null : this.b(var1);
   }

   public abstract <A extends Annotation> A c(Class<A> var1);

   private <A extends Annotation> A f(Class<A> var1) {
      if (this.c) {
         return this.c(var1);
      } else {
         return this.f != null
               && this.a.getType().getErasedType() == Optional.class
               && this.f.getErasedType() == ((ResolvedType)this.a.getType().getTypeParameters().get(0)).getErasedType()
            ? this.c(var1)
            : null;
      }
   }

   public String s() {
      return this.i.a();
   }

   @Override
   protected abstract String a();

   @Override
   public String toString() {
      return this.x() + " " + this.s();
   }
}
