package reloc.org.sat4j.tools.counting;

import java.util.Random;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.constraints.xor.Xor;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class ParityConstraintGenerator {
   private static final Random RANDOM = System.getProperty("NONDETERMINISTIC") == null ? new Random(123456789L) : new Random();
   private final ISolver solver;
   private final SamplingSet samplingSet;
   private final ConstrGroup parityConstraints;
   private boolean activated;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ParityConstraintGenerator.class.desiredAssertionStatus();

   public ParityConstraintGenerator(ISolver solver, SamplingSet samplingSet) {
      this.solver = solver;
      this.samplingSet = samplingSet;
      this.parityConstraints = new ConstrGroup();
   }

   public int nbConstraints() {
      return this.parityConstraints.size();
   }

   public void generate(int nb) {
      for (int i = 0; i < nb; i++) {
         IVecInt lits = new VecInt();
         IteratorInt it = this.samplingSet.variables();

         while (it.hasNext()) {
            int var = it.next();
            if (RANDOM.nextBoolean()) {
               lits.push(var);
            }
         }

         IConstr constr = this.solver.addParity(lits, RANDOM.nextBoolean());
         this.parityConstraints.add(constr);
      }

      this.activated = true;
   }

   public Xor getConstraint(int i) {
      assert i < this.nbConstraints();
      return (Xor)this.parityConstraints.getConstr(i);
   }

   public void deactivate() {
      this.deactivate(this.nbConstraints());
   }

   public void deactivate(int nb) {
      for (int i = 0; i < nb; i++) {
         this.getConstraint(i).deactivate();
      }

      this.activated = false;
      this.solver.clearLearntClauses();
   }

   public void activate() {
      this.activate(this.nbConstraints());
   }

   public void activate(int nb) {
      for (int i = 0; i < nb; i++) {
         this.getConstraint(i).activate();
      }

      this.activated = true;
   }

   public void clear() {
      if (!this.activated) {
         this.activate();
      }

      this.parityConstraints.removeFrom(this.solver);
      this.parityConstraints.clear();
   }
}
