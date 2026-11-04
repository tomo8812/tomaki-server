package reloc.org.sat4j.tools.xplain;

import java.util.Map;
import java.util.Set;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;

public class QuickXplain2001Strategy implements MinimizationStrategy {
   private static final long serialVersionUID = 1L;
   private boolean computationCanceled;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !QuickXplain2001Strategy.class.desiredAssertionStatus();

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

      Set<Integer> constraintsVariables = constrs.keySet();

      for (int i = 0; i < firstExplanation.size(); i++) {
         int p;
         if (constraintsVariables.contains(p = -firstExplanation.get(i))) {
            encodingAssumptions.push(p);
         }
      }

      IVecInt results = new VecInt(encodingAssumptions.size());
      this.computeExplanation(solver, encodingAssumptions, assumps.size(), encodingAssumptions.size() - 1, results);
      return results;
   }

   private void computeExplanation(ISolver solver, IVecInt encodingAssumptions, int start, int end, IVecInt result) throws TimeoutException {
      if (solver.isSatisfiable(encodingAssumptions)) {
         int i = start;
         encodingAssumptions.set(i, -encodingAssumptions.get(i));
         assert encodingAssumptions.get(i) < 0;

         while (!this.computationCanceled && solver.isSatisfiable(encodingAssumptions)) {
            if (i == end) {
               for (int j = start; j <= end; j++) {
                  encodingAssumptions.set(j, -encodingAssumptions.get(j));
               }

               return;
            }

            assert encodingAssumptions.get(++i) > 0;
            encodingAssumptions.set(i, -encodingAssumptions.get(i));
         }

         result.push(-encodingAssumptions.get(i));
         if (start != i) {
            int newend = i - 1;
            int split = (newend + start) / 2;
            if (split < newend) {
               for (int j = split + 1; j < i; j++) {
                  encodingAssumptions.set(j, -encodingAssumptions.get(j));
               }

               this.computeExplanation(solver, encodingAssumptions, split + 1, newend, result);
            }

            if (start <= split) {
               for (int j = start; j <= split; j++) {
                  encodingAssumptions.set(j, -encodingAssumptions.get(j));
               }

               this.computeExplanation(solver, encodingAssumptions, start, split, result);
            }

            if (this.computationCanceled) {
               throw new TimeoutException();
            }
         }
      }
   }

   @Override
   public String toString() {
      return "QuickXplain (IJCAI WS 2001 version) minimization strategy";
   }
}
