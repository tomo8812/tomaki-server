package reloc.org.sat4j.tools.xplain;

import java.util.Map;
import java.util.Set;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;

public class QuickXplainStrategy implements MinimizationStrategy {
   private static final long serialVersionUID = 1L;
   private boolean computationCanceled;

   @Override
   public void cancelExplanationComputation() {
      this.computationCanceled = true;
   }

   @Override
   public IVecInt explain(ISolver solver, Map<Integer, ?> constrs, IVecInt assumps) throws TimeoutException {
      this.computationCanceled = false;
      IVecInt encodingAssumptions = new VecInt(constrs.size() + assumps.size());
      assumps.copyTo(encodingAssumptions);
      IVecInt firstExplanation = solver.unsatExplanation();
      IVecInt results = new VecInt(firstExplanation.size());
      if (firstExplanation.size() == 1) {
         results.push(-firstExplanation.get(0));
         return results;
      }

      if (solver.isVerbose()) {
         System.out.print(solver.getLogPrefix() + "initial unsat core ");
         firstExplanation.sort();
         IteratorInt it = firstExplanation.iterator();

         while (it.hasNext()) {
            System.out.print(constrs.get(-it.next()));
            System.out.print(" ");
         }

         System.out.println();
      }

      int i = 0;

      while (i < firstExplanation.size()) {
         if (assumps.contains(firstExplanation.get(i))) {
            firstExplanation.delete(i);
         } else {
            i++;
         }
      }

      Set<Integer> constraintsVariables = constrs.keySet();
      IVecInt remainingVariables = new VecInt(constraintsVariables.size());

      for (Integer v : constraintsVariables) {
         remainingVariables.push(v);
      }

      IteratorInt it = firstExplanation.iterator();

      while (it.hasNext()) {
         int p = it.next();
         if (p < 0) {
            p = -p;
         }

         remainingVariables.remove(p);
         encodingAssumptions.push(p);
      }

      int unsatcorelimit = encodingAssumptions.size() - 1;
      remainingVariables.copyTo(encodingAssumptions);
      this.computeExplanation(solver, constrs, encodingAssumptions, assumps.size(), unsatcorelimit, results);
      return results;
   }

   private void computeExplanation(ISolver solver, Map<Integer, ?> constrs, IVecInt encodingAssumptions, int start, int end, IVecInt result) throws TimeoutException {
      if (solver.isVerbose()) {
         System.out.println(solver.getLogPrefix() + "qxplain " + start + "/" + end);
      }

      if (solver.isSatisfiable(encodingAssumptions)) {
         if (start == end) {
            result.push(encodingAssumptions.get(start));
            encodingAssumptions.set(start, -encodingAssumptions.get(start));
            if (solver.isVerbose()) {
               System.out.println(solver.getLogPrefix() + constrs.get(-encodingAssumptions.get(start)) + " is mandatory ");
            }
         } else {
            int split = (end + start) / 2;
            if (split < end) {
               for (int j = start; j <= split; j++) {
                  encodingAssumptions.set(j, -encodingAssumptions.get(j));
               }

               this.computeExplanation(solver, constrs, encodingAssumptions, split + 1, end, result);
            }

            if (start <= split) {
               for (int j = start; j <= split; j++) {
                  encodingAssumptions.set(j, -encodingAssumptions.get(j));
               }

               this.computeExplanation(solver, constrs, encodingAssumptions, start, split, result);
            }

            if (this.computationCanceled) {
               throw new TimeoutException();
            }
         }
      }
   }

   @Override
   public String toString() {
      return "QuickXplain (AAAI 2004 version) minimization strategy";
   }
}
