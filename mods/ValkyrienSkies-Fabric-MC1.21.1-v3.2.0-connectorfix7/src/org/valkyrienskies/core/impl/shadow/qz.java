package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class qz implements qF {
   List<qp> a;
   private int b;

   public qz(int var1) {
      this(Collections.emptyList(), var1);
   }

   public qz(List<qp> var1, int var2) {
      if (var1 == null) {
         throw new pl();
      }

      if (var2 <= 0) {
         throw new pj(px.POPULATION_LIMIT_NOT_POSITIVE, var2);
      }

      if (var1.size() > var2) {
         throw new pm(px.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, var1.size(), var2, false);
      }

      this.b = var2;
      this.a = new ArrayList<>(var2);
      this.a.addAll(var1);
   }

   @Deprecated
   private void a(List<qp> var1) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.size() > this.b) {
         throw new pm(px.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, var1.size(), this.b, false);
      }

      this.a.clear();
      this.a.addAll(var1);
   }

   private void a(Collection<qp> var1) {
      if (this.a.size() + var1.size() > this.b) {
         throw new pm(px.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, this.a.size(), this.b, false);
      }

      this.a.addAll(var1);
   }

   public final List<qp> b() {
      return Collections.unmodifiableList(this.a);
   }

   protected final List<qp> c() {
      return this.a;
   }

   @Override
   public final void a(qp var1) {
      if (this.a.size() >= this.b) {
         throw new pm(px.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, this.a.size(), this.b, false);
      }

      this.a.add(var1);
   }

   @Override
   public final qp d() {
      qp var1 = this.a.get(0);
      Iterator var2 = this.a.iterator();

      while (var2.hasNext()) {
         qp var3;
         if ((var3 = (qp)var2.next()).b(var1) > 0) {
            var1 = var3;
         }
      }

      return var1;
   }

   @Override
   public final int e() {
      return this.b;
   }

   private void a(int var1) {
      if (var1 <= 0) {
         throw new pj(px.POPULATION_LIMIT_NOT_POSITIVE, var1);
      }

      if (var1 < this.a.size()) {
         throw new pn(var1, this.a.size(), true);
      }

      this.b = var1;
   }

   @Override
   public final int f() {
      return this.a.size();
   }

   @Override
   public String toString() {
      return this.a.toString();
   }

   @Override
   public Iterator<qp> iterator() {
      return Collections.unmodifiableList(this.a).iterator();
   }
}
