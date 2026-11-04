package reloc.org.sat4j.tools;

import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public class ModelIteratorToSATAdapter extends ModelIterator {
   private static final long serialVersionUID = 1L;
   private int[] lastModel = null;
   private final SolutionFoundListener sfl;

   public ModelIteratorToSATAdapter(ISolver solver, SolutionFoundListener sfl) {
      this(solver, Long.MAX_VALUE, sfl);
   }

   public ModelIteratorToSATAdapter(ISolver solver, long bound, SolutionFoundListener sfl) {
      super(solver, bound);
      this.sfl = sfl;
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      boolean isSat = false;

      while (super.isSatisfiable()) {
         isSat = true;
         this.lastModel = super.model();
         this.sfl.onSolutionFound(this.lastModel);
      }

      this.expireTimeout();
      return isSat;
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      boolean isSat = false;

      while (super.isSatisfiable(assumps)) {
         isSat = true;
         this.lastModel = super.model();
         this.sfl.onSolutionFound(this.lastModel);
      }

      this.expireTimeout();
      return isSat;
   }

   @Override
   public int[] model() {
      return this.lastModel;
   }
}
