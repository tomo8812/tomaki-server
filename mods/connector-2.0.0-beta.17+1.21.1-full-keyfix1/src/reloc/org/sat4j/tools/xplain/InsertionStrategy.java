package reloc.org.sat4j.tools.xplain;

import java.util.Map;
import java.util.Set;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;

public class InsertionStrategy implements MinimizationStrategy {
   private static final long serialVersionUID = 1L;
   private boolean computationCanceled;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !InsertionStrategy.class.desiredAssertionStatus();

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
      if (firstExplanation.size() == 1) {
         IVecInt results = new VecInt();
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

      remainingVariables.copyTo(encodingAssumptions);
      int startingPoint = assumps.size();

      do {
         shouldContinue = false;
         int ix = startingPoint;
         encodingAssumptions.set(ix, -encodingAssumptions.get(ix));
         assert encodingAssumptions.get(ix) < 0;

         while (!this.computationCanceled && solver.isSatisfiable(encodingAssumptions)) {
            assert encodingAssumptions.get(++ix) > 0;
            encodingAssumptions.set(ix, -encodingAssumptions.get(ix));
         }

         if (!this.computationCanceled && ix > startingPoint) {
            assert !solver.isSatisfiable(encodingAssumptions);
            if (ix < encodingAssumptions.size()) {
               int tmp = encodingAssumptions.get(ix);

               for (int j = ix; j > startingPoint; j--) {
                  encodingAssumptions.set(j, -encodingAssumptions.get(j - 1));
               }

               encodingAssumptions.set(startingPoint, tmp);
               if (solver.isVerbose()) {
                  System.out.println(solver.getLogPrefix() + constrs.get(tmp) + " is mandatory ");
               }
            }

            shouldContinue = true;
         }

         startingPoint++;
      } while (!this.computationCanceled && shouldContinue && solver.isSatisfiable(encodingAssumptions));

      if (this.computationCanceled) {
         throw new TimeoutException();
      }

      IVecInt constrsKeys = new VecInt(startingPoint);

      for (int ix = assumps.size(); ix < startingPoint; ix++) {
         constrsKeys.push(-encodingAssumptions.get(ix));
      }

      return constrsKeys;
   }

   @Override
   public String toString() {
      return "Replay (Insertion-based) minimization strategy";
   }
}
