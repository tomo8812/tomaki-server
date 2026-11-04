package reloc.org.sat4j.minisat.orders;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;
import reloc.org.sat4j.specs.IVecInt;

@Feature(value = "varheuristics", parent = "expert")
public class OrientedOrder implements IOrder {
   private final IVecInt orderedLits = new VecInt();
   private boolean[] managed;
   private ILits voc;
   private final String fileName;
   private final IOrder order;

   public OrientedOrder(String fileName, IOrder order) {
      this.fileName = fileName;
      this.order = order;
   }

   @Override
   public void setLits(ILits lits) {
      this.order.setLits(lits);
      this.voc = lits;
   }

   @Override
   public int select() {
      for (int i = 0; i < this.orderedLits.size(); i++) {
         int p = this.orderedLits.get(i);
         if (this.voc.isUnassigned(p)) {
            return p;
         }
      }

      return this.order.select();
   }

   @Override
   public void undo(int x) {
      if (!this.managed[x >> 1]) {
         this.order.undo(x);
      }
   }

   @Override
   public void updateVar(int p) {
      if (!this.managed[p >> 1]) {
         this.order.updateVar(p);
      }
   }

   @Override
   public void updateVar(int p, double value) {
      this.updateVar(p);
   }

   @Override
   public void init() {
      this.order.init();
      this.managed = new boolean[this.voc.nVars() + 1];

      try (Scanner in = new Scanner(new BufferedReader(new FileReader(this.fileName)))) {
         while (in.hasNext()) {
            this.append(in.nextInt());
         }
      } catch (FileNotFoundException e) {
         throw new IllegalStateException(e);
      }
   }

   private void append(int l) {
      int p = LiteralsUtils.toInternal(l);
      this.orderedLits.push(p);
      this.managed[p >> 1] = true;
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
      this.order.printStat(out, prefix);
   }

   @Override
   public void setVarDecay(double d) {
      this.order.setVarDecay(d);
   }

   @Override
   public void varDecayActivity() {
      this.order.varDecayActivity();
   }

   @Override
   public double varActivity(int p) {
      return this.order.varActivity(p);
   }

   @Override
   public void assignLiteral(int p) {
      if (!this.managed[p >> 1]) {
         this.order.assignLiteral(p);
      }
   }

   @Override
   public void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy) {
      this.order.setPhaseSelectionStrategy(strategy);
   }

   @Override
   public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
      return this.order.getPhaseSelectionStrategy();
   }

   @Override
   public void updateVarAtDecisionLevel(int q) {
      if (!this.managed[q >> 1]) {
         this.order.updateVarAtDecisionLevel(q);
      }
   }

   @Override
   public double[] getVariableHeuristics() {
      return this.order.getVariableHeuristics();
   }
}
