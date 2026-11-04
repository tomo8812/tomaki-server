package reloc.org.sat4j.pb;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map;

public class ObjectiveFunctionComparator implements Comparator<Integer>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<Integer, BigInteger> obj;

   public ObjectiveFunctionComparator(ObjectiveFunction objf) {
      this.obj = objf.toMap();
   }

   public int compare(Integer o1, Integer o2) {
      BigInteger b1 = this.obj.get(o1);
      BigInteger b2 = this.obj.get(o2);
      if (b2 == null) {
         return b1 == null ? 0 : -b1.intValue();
      } else {
         return b2.compareTo(b1);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compare(Object var1, Object var2) {
      return this.compare((Integer)var1, (Integer)var2);
   }
}
