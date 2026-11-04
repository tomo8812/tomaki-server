package reloc.org.sat4j.minisat.orders;

import java.io.PrintWriter;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;

@Feature(value = "varheuristics", parent = "expert")
public class NaturalStaticOrder implements IOrder {
   private ILits voc;
   private IPhaseSelectionStrategy phaseSelectionStrategy = new NegativeLiteralSelectionStrategy();
   int index;

   @Override
   public void setLits(ILits lits) {
      this.voc = lits;
   }

   @Override
   public int select() {
      while (!this.voc.isUnassigned(LiteralsUtils.posLit(this.index)) || !this.voc.belongsToPool(this.index)) {
         this.index++;
         if (this.index > this.voc.nVars()) {
            return -1;
         }
      }

      return this.phaseSelectionStrategy.select(this.index);
   }

   @Override
   public void undo(int x) {
      this.index = Math.min(this.index, x);
   }

   @Override
   public void updateVar(int p) {
   }

   @Override
   public void updateVar(int p, double value) {
      this.updateVar(p);
   }

   @Override
   public void init() {
      this.index = 1;
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
   }

   @Override
   public void setVarDecay(double d) {
   }

   @Override
   public void varDecayActivity() {
   }

   @Override
   public double varActivity(int p) {
      return 0.0;
   }

   @Override
   public void assignLiteral(int p) {
   }

   @Override
   public void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy) {
      this.phaseSelectionStrategy = strategy;
   }

   @Override
   public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
      return this.phaseSelectionStrategy;
   }

   @Override
   public void updateVarAtDecisionLevel(int q) {
   }

   @Override
   public double[] getVariableHeuristics() {
      return new double[0];
   }

   @Override
   public String toString() {
      return "Natural static ordering";
   }
}
