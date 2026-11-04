package reloc.org.sat4j.minisat.orders;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Random;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;

@Feature(value = "varheuristics", parent = "expert")
public class RandomWalkDecorator implements IOrder, Serializable {
   private static final long serialVersionUID = 1L;
   private final VarOrderHeap decorated;
   private double p;
   private static final Random RAND = System.getProperty("NONDETERMINISTIC") == null ? new Random(123456789L) : new Random();
   private ILits voc;
   private int nbRandomWalks;

   public RandomWalkDecorator(VarOrderHeap order) {
      this(order, 0.01);
   }

   public RandomWalkDecorator(VarOrderHeap order, double p) {
      this.decorated = order;
      this.p = p;
   }

   @Override
   public void assignLiteral(int q) {
      this.decorated.assignLiteral(q);
   }

   @Override
   public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
      return this.decorated.getPhaseSelectionStrategy();
   }

   public double getProbability() {
      return this.p;
   }

   public void setProbability(double p) {
      this.p = p;
   }

   @Override
   public void init() {
      this.decorated.init();
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
      out.println(prefix + "random walks\t: " + this.nbRandomWalks);
      this.decorated.printStat(out, prefix);
   }

   @Override
   public int select() {
      if (RAND.nextDouble() < this.p) {
         while (!this.decorated.heap.empty()) {
            int max = this.decorated.heap.size();
            int var = this.decorated.heap.get(RAND.nextInt(max) + 1);
            int lit = this.getPhaseSelectionStrategy().select(var);
            if (this.voc.isUnassigned(lit)) {
               this.nbRandomWalks++;
               return lit;
            }
         }
      }

      return this.decorated.select();
   }

   @Override
   public void setLits(ILits lits) {
      this.decorated.setLits(lits);
      this.voc = lits;
      this.nbRandomWalks = 0;
   }

   @Override
   public void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy) {
      this.decorated.setPhaseSelectionStrategy(strategy);
   }

   @Override
   public void setVarDecay(double d) {
      this.decorated.setVarDecay(d);
   }

   @Override
   public void undo(int x) {
      this.decorated.undo(x);
   }

   @Override
   public void updateVar(int q) {
      this.decorated.updateVar(q);
   }

   @Override
   public void updateVar(int p, double value) {
      this.updateVar(p);
   }

   @Override
   public double varActivity(int q) {
      return this.decorated.varActivity(q);
   }

   @Override
   public void varDecayActivity() {
      this.decorated.varDecayActivity();
   }

   @Override
   public void updateVarAtDecisionLevel(int q) {
      this.decorated.updateVarAtDecisionLevel(q);
   }

   @Override
   public String toString() {
      return this.decorated.toString() + " with random walks " + this.p;
   }

   @Override
   public double[] getVariableHeuristics() {
      return this.decorated.getVariableHeuristics();
   }
}
