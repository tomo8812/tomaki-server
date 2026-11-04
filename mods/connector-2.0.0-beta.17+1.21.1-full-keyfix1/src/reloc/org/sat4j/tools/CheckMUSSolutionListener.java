package reloc.org.sat4j.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solutionlistener")
public class CheckMUSSolutionListener implements SolutionFoundListener {
   private List<IVecInt> clauses = new ArrayList<>();
   private String explain;
   private final ASolverFactory<? extends ISolver> factory;

   public CheckMUSSolutionListener(ASolverFactory<? extends ISolver> factory) {
      this.factory = factory;
   }

   public void addOriginalClause(IVecInt clause) {
      IVecInt newClause = new VecInt(clause.size());
      if (this.clauses == null) {
         this.clauses = new ArrayList<>();
      }

      clause.copyTo(newClause);
      this.clauses.add(newClause);
   }

   public boolean checkThatItIsAMUS(IVecInt mus) {
      boolean result = false;
      ISolver solver = this.factory.defaultSolver();

      try {
         for (int i = 0; i < mus.size(); i++) {
            solver.addClause(this.clauses.get(mus.get(i) - 1));
         }

         result = !solver.isSatisfiable();
         if (!result) {
            this.explain = "The set of clauses in the MUS is SAT : " + Arrays.toString(solver.model());
            return result;
         }
      } catch (ContradictionException e) {
         Logger.getLogger("reloc.org.sat4j.core").log(Level.INFO, "Trivial inconsistency", e);
         result = true;
      } catch (TimeoutException e) {
         Logger.getLogger("reloc.org.sat4j.core").log(Level.INFO, "Timeout when checking unsatisfiability", e);
      }

      try {
         for (int i = 0; i < mus.size(); i++) {
            solver = this.factory.defaultSolver();

            for (int j = 0; j < mus.size(); j++) {
               if (j != i) {
                  solver.addClause(this.clauses.get(mus.get(j) - 1));
               }
            }

            result = result && solver.isSatisfiable();
            if (!result) {
               this.explain = "The subset of clauses in the MUS not containing clause " + (i + 1) + " is SAT : " + Arrays.toString(solver.model());
               return result;
            }
         }
      } catch (ContradictionException e) {
         Logger.getLogger("reloc.org.sat4j.core").log(Level.INFO, "Trivial inconsistency", e);
         result = false;
      } catch (TimeoutException e) {
         Logger.getLogger("reloc.org.sat4j.core").log(Level.INFO, "Timeout when checking satisfiability", e);
      }

      return result;
   }

   @Override
   public void onSolutionFound(int[] solution) {
   }

   @Override
   public void onSolutionFound(IVecInt solution) {
      if (this.checkThatItIsAMUS(solution)) {
         System.out.println(solution + " is a MUS");
      } else {
         System.out.println(solution + " is not a MUS \n" + this.explain);
      }
   }

   @Override
   public void onUnsatTermination() {
   }
}
