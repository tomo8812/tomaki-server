package reloc.org.sat4j.core;

import java.io.Serializable;
import java.util.Comparator;

public final class DefaultComparator<A extends Comparable<A>> implements Comparator<A>, Serializable {
   private static final long serialVersionUID = 1L;

   public int compare(A a, A b) {
      return a.compareTo(b);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compare(Object var1, Object var2) {
      return this.compare((A)var1, (A)var2);
   }
}
