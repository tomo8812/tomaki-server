package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;

public final class eC<T> implements ek<Set<T>> {
   private static final ek<Set<Object>> a = eo.a(Collections.emptySet());
   private final List<ew<T>> b;
   private final List<ew<Collection<T>>> c;

   private static <T> ek<Set<T>> a() {
      return (ek<Set<T>>)a;
   }

   private static <T> eC.a<T> a(int individualProviderSize, int collectionProviderSize) {
      return new eC.a<>(individualProviderSize, collectionProviderSize, (byte)0);
   }

   private eC(List<ew<T>> individualProviders, List<ew<Collection<T>>> collectionProviders) {
      this.b = individualProviders;
      this.c = collectionProviders;
   }

   private Set<T> b() {
      int var1 = this.b.size();
      ArrayList var2 = new ArrayList(this.c.size());
      int var3 = 0;

      for (int var4 = this.c.size(); var3 < var4; var3++) {
         Collection var5 = this.c.get(var3).get();
         var1 += var5.size();
         var2.add(var5);
      }

      var1 = var1;
      HashSet var9 = new HashSet(eg.d(var1));
      int var10 = 0;

      for (int var12 = this.b.size(); var10 < var12; var10++) {
         var9.add(ev.a(this.b.get(var10).get()));
      }

      var10 = 0;

      for (int var13 = var2.size(); var10 < var13; var10++) {
         for (Object var6 : (Collection)var2.get(var10)) {
            var9.add(ev.a(var6));
         }
      }

      return Collections.unmodifiableSet(var9);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      eC var1 = this;
      int var2 = this.b.size();
      ArrayList var3 = new ArrayList(var1.c.size());
      int var4 = 0;

      for (int var5 = var1.c.size(); var4 < var5; var4++) {
         Collection var6 = var1.c.get(var4).get();
         var2 += var6.size();
         var3.add(var6);
      }

      var2 = var2;
      HashSet var10 = new HashSet(eg.d(var2));
      int var11 = 0;

      for (int var13 = var1.b.size(); var11 < var13; var11++) {
         var10.add(ev.a(var1.b.get(var11).get()));
      }

      var11 = 0;

      for (int var14 = var3.size(); var11 < var14; var11++) {
         for (Object var9 : (Collection)var3.get(var11)) {
            var10.add(ev.a(var9));
         }
      }

      return Collections.unmodifiableSet(var10);
   }

   // $VF: synthetic method
   eC(List var1, List var2, byte var3) {
      this(var1, var2);
   }

   public static final class a<T> {
      private final List<ew<T>> a;
      private final List<ew<Collection<T>>> b;
      // $VF: synthetic field
      private static boolean c = !eC.class.desiredAssertionStatus();

      private a(int individualProviderSize, int collectionProviderSize) {
         this.a = eg.a(individualProviderSize);
         this.b = eg.a(collectionProviderSize);
      }

      private eC.a<T> a(ew<? extends T> individualProvider) {
         assert individualProvider != null : "Codegen error? Null provider";
         this.a.add(individualProvider);
         return this;
      }

      @Deprecated
      private eC.a<T> a(Provider<? extends T> individualProvider) {
         var var2 = ey.a(individualProvider);
         Provider var3 = this;
         assert var2 != null : "Codegen error? Null provider";
         var3.a.add(var2);
         return var3;
      }

      private eC.a<T> b(ew<? extends Collection<? extends T>> collectionProvider) {
         assert collectionProvider != null : "Codegen error? Null provider";
         this.b.add(collectionProvider);
         return this;
      }

      @Deprecated
      private eC.a<T> b(Provider<? extends Collection<? extends T>> collectionProvider) {
         var var2 = ey.a(collectionProvider);
         Provider var3 = this;
         assert var2 != null : "Codegen error? Null provider";
         var3.b.add(var2);
         return var3;
      }

      private eC<T> a() {
         assert !eg.a(this.a) : "Codegen error?  Duplicates in the provider list";
         assert !eg.a(this.b) : "Codegen error?  Duplicates in the provider list";
         return new eC<>(this.a, this.b, (byte)0);
      }

      // $VF: synthetic method
      a(int var1, int var2, byte var3) {
         this(var1, var2);
      }
   }
}
