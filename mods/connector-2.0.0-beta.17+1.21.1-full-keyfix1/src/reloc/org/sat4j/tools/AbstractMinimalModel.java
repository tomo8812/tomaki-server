package reloc.org.sat4j.tools;

import java.util.SortedSet;
import java.util.TreeSet;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class AbstractMinimalModel extends SolverDecorator<ISolver> {
   private static final long serialVersionUID = 1L;
   protected final SortedSet<Integer> pLiterals = new TreeSet<>();
   protected final SolutionFoundListener modelListener;

   public static IVecInt positiveLiterals(ISolver solver) {
      IVecInt literals = new VecInt(solver.nVars());

      for (int i = 1; i <= solver.nVars(); i++) {
         literals.push(i);
      }

      return literals;
   }

   public static IVecInt negativeLiterals(ISolver solver) {
      IVecInt literals = new VecInt(solver.nVars());

      for (int i = 1; i <= solver.nVars(); i++) {
         literals.push(-i);
      }

      return literals;
   }

   public AbstractMinimalModel(ISolver solver) {
      this(solver, SolutionFoundListener.VOID);
   }

   public AbstractMinimalModel(ISolver solver, IVecInt p) {
      this(solver, p, SolutionFoundListener.VOID);
   }

   public AbstractMinimalModel(ISolver solver, SolutionFoundListener modelListener) {
      this(solver, negativeLiterals(solver), modelListener);
   }

   public AbstractMinimalModel(ISolver solver, IVecInt p, SolutionFoundListener modelListener) {
      super(solver);
      IteratorInt it = p.iterator();

      while (it.hasNext()) {
         this.pLiterals.add(it.next());
      }

      this.modelListener = modelListener;
   }
}
