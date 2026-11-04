package reloc.org.sat4j.tools;

import java.util.Set;
import java.util.TreeSet;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class SubModelIterator extends ModelIterator {
   private static final long serialVersionUID = 1L;
   private final Set<Integer> subsetVars = new TreeSet<>();

   public SubModelIterator(ISolver solver, IVecInt subsetVars) {
      this(solver, subsetVars, Long.MAX_VALUE);
   }

   public SubModelIterator(ISolver solver, IVecInt subsetVars, long bound) {
      super(solver, bound);
      IteratorInt it = subsetVars.iterator();

      while (it.hasNext()) {
         this.subsetVars.add(it.next());
      }
   }

   @Override
   public int[] model() {
      int[] last = super.model();
      this.nbModelFound++;
      int[] sub = new int[this.subsetVars.size()];
      IVecInt clause = new VecInt(sub.length);
      int i = 0;

      for (int q : last) {
         int var = Math.abs(q);
         if (this.subsetVars.contains(var)) {
            clause.push(-q);
            sub[i++] = q;
         }
      }

      try {
         this.addBlockingClause(clause);
      } catch (ContradictionException e) {
         this.trivialfalsity = true;
      }

      return sub;
   }

   @Override
   public int[] primeImplicant() {
      throw new UnsupportedOperationException();
   }
}
