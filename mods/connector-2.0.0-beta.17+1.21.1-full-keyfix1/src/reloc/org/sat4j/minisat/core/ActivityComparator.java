package reloc.org.sat4j.minisat.core;

import java.io.Serializable;
import java.util.Comparator;
import reloc.org.sat4j.specs.Constr;

public class ActivityComparator implements Comparator<Constr>, Serializable {
   private static final long serialVersionUID = 1L;

   public int compare(Constr c1, Constr c2) {
      long delta = Math.round(c1.getActivity() - c2.getActivity());
      return delta == 0L ? c1.size() - c2.size() : (int)delta;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compare(Object var1, Object var2) {
      return this.compare((Constr)var1, (Constr)var2);
   }
}
