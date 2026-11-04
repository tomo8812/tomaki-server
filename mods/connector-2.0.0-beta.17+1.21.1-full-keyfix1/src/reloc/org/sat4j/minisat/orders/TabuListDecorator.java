package reloc.org.sat4j.minisat.orders;

import java.io.PrintWriter;
import java.util.LinkedList;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;

@Feature(value = "varheuristics", parent = "expert")
public class TabuListDecorator implements IOrder {
   private final VarOrderHeap decorated;
   private final int tabuSize;
   private ILits voc;
   private int lastVar = -1;
   private final LinkedList<Integer> tabuList = new LinkedList<>();

   public TabuListDecorator(VarOrderHeap order) {
      this(order, 10);
   }

   public TabuListDecorator(VarOrderHeap order, int tabuSize) {
      this.decorated = order;
      this.tabuSize = tabuSize;
   }

   @Override
   public void assignLiteral(int q) {
      this.decorated.assignLiteral(q);
   }

   @Override
   public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
      return this.decorated.getPhaseSelectionStrategy();
   }

   @Override
   public void init() {
      this.decorated.init();
      this.lastVar = -1;
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
      out.println(prefix + "tabu list size\t: " + this.tabuSize);
      this.decorated.printStat(out, prefix);
   }

   @Override
   public int select() {
      int lit = this.decorated.select();
      if (lit != -1) {
         this.lastVar = lit >> 1;
         return lit;
      }

      while (!this.tabuList.isEmpty()) {
         int var = this.tabuList.removeFirst();
         if (this.voc.isUnassigned(var << 1)) {
            return this.getPhaseSelectionStrategy().select(var);
         }
      }

      return -1;
   }

   @Override
   public void setLits(ILits lits) {
      this.decorated.setLits(lits);
      this.voc = lits;
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
      if (this.tabuList.size() == this.tabuSize) {
         int var = this.tabuList.removeFirst();
         this.decorated.undo(var);
      }

      if (x == this.lastVar) {
         this.tabuList.add(x);
         this.lastVar = -1;
      } else {
         this.decorated.undo(x);
      }
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
      return this.decorated.toString() + " with tabu list of size " + this.tabuSize;
   }

   @Override
   public double[] getVariableHeuristics() {
      return this.decorated.getVariableHeuristics();
   }
}
