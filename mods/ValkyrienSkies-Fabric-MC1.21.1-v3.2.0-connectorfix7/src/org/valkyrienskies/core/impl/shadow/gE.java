package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Comparator;

public final class gE<E extends Comparable<? super E>> implements Serializable, Comparator<E> {
   private static final long a = -291439688585137865L;
   private static gE b = new gE();

   public static <E extends Comparable<? super E>> gE<E> a() {
      return b;
   }

   private static int a(E var0, E var1) {
      return var0.compareTo(var1);
   }

   @Override
   public final int hashCode() {
      return "ComparableComparator".hashCode();
   }

   @Override
   public final boolean equals(Object object) {
      return this == object || object != null && object.getClass().equals(this.getClass());
   }

   // $VF: synthetic method
   @Override
   public final int compare(Object var1, Object var2) {
      Comparable var10000 = (Comparable)var1;
      Comparable var3 = (Comparable)var2;
      return var10000.compareTo(var3);
   }
}
