package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class qm<T> extends qp {
   final List<T> a;

   public qm(List<T> var1) {
      this(var1, true);
   }

   public qm(T[] var1) {
      this(Arrays.asList((T[])var1));
   }

   private qm(List<T> var1, boolean var2) {
      this.a(var1);
      this.a = Collections.unmodifiableList(new ArrayList<>(var1));
   }

   protected abstract void a(List<T> var1);

   protected final List<T> a() {
      return this.a;
   }

   public final int b() {
      return this.a.size();
   }

   public abstract qm<T> c();

   @Override
   public String toString() {
      return String.format("(f=%s %s)", this.d(), this.a);
   }
}
