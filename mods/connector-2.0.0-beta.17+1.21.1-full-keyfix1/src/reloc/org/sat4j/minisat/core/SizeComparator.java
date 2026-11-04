package reloc.org.sat4j.minisat.core;

import java.io.Serializable;
import java.util.Comparator;
import reloc.org.sat4j.specs.Constr;

public class SizeComparator implements Comparator<Constr>, Serializable {
   private static final long serialVersionUID = 1L;

   public int compare(Constr c1, Constr c2) {
      int delta = c1.size() - c2.size();
      return delta == 0 ? (int)Math.round(c2.getActivity() - c1.getActivity()) : delta;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compare(Object var1, Object var2) {
      return this.compare((Constr)var1, (Constr)var2);
   }
}
