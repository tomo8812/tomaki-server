package reloc.org.sat4j.minisat.orders;

import java.io.PrintWriter;
import java.io.Serializable;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.minisat.core.Heap;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.core.IPhaseSelectionStrategy;

@Feature(value = "varheuristics", parent = "expert")
public class VarOrderHeap implements IOrder, Serializable {
   private static final long serialVersionUID = 1L;
   private static final double VAR_RESCALE_FACTOR = 1.0E-100;
   private static final double VAR_RESCALE_BOUND = 1.0E100;
   protected double[] activity = new double[1];
   private double varDecay = 1.0;
   private double varInc = 1.0;
   protected ILits lits;
   private long nullchoice = 0L;
   protected Heap heap;
   protected IPhaseSelectionStrategy phaseStrategy;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !VarOrderHeap.class.desiredAssertionStatus();

   public VarOrderHeap() {
      this(new PhaseInLastLearnedClauseSelectionStrategy());
   }

   public VarOrderHeap(IPhaseSelectionStrategy strategy) {
      this.phaseStrategy = strategy;
   }

   @Override
   public void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy) {
      this.phaseStrategy = strategy;
   }

   @Override
   public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
      return this.phaseStrategy;
   }

   @Override
   public void setLits(ILits lits) {
      this.lits = lits;
   }

   @Override
   public int select() {
      while (!this.heap.empty()) {
         int var = this.heap.getmin();
         int next = this.phaseStrategy.select(var);
         if (this.lits.isUnassigned(next)) {
            if (this.activity[var] < 1.0E-4) {
               this.nullchoice++;
            }

            return next;
         }
      }

      return -1;
   }

   @Override
   public void setVarDecay(double d) {
      this.varDecay = d;
   }

   @Override
   public void undo(int x) {
      if (!this.heap.inHeap(x)) {
         this.heap.insert(x);
      }
   }

   @Override
   public void updateVar(int p) {
      this.updateVar(p, 1.0);
   }

   @Override
   public void updateVar(int p, double value) {
      int var = LiteralsUtils.var(p);
      this.updateActivity(var, value);
      this.phaseStrategy.updateVar(p);
      if (this.heap.inHeap(var)) {
         this.heap.increase(var);
      }
   }

   protected void updateActivity(int var, double inc) {
      if ((this.activity[var] = this.activity[var] + inc * this.varInc) > 1.0E100) {
         this.varRescaleActivity();
      }
   }

   @Override
   public void varDecayActivity() {
      this.varInc = this.varInc * this.varDecay;
   }

   private void varRescaleActivity() {
      for (int i = 1; i < this.activity.length; i++) {
         this.activity[i] = this.activity[i] * 1.0E-100;
      }

      this.varInc *= 1.0E-100;
   }

   @Override
   public double varActivity(int p) {
      return this.activity[LiteralsUtils.var(p)];
   }

   public int numberOfInterestingVariables() {
      int cpt = 0;

      for (int i = 1; i < this.activity.length; i++) {
         if (this.activity[i] > 1.0) {
            cpt++;
         }
      }

      return cpt;
   }

   protected Heap createHeap(double[] activity) {
      return new Heap(new ActivityBasedVariableComparator(activity));
   }

   @Override
   public void init() {
      int nlength = this.lits.nVars() + 1;
      if (this.activity == null || this.activity.length < nlength) {
         this.activity = new double[nlength];
      }

      this.phaseStrategy.init(nlength);
      this.activity[0] = -1.0;
      this.heap = this.createHeap(this.activity);
      this.heap.setBounds(nlength);

      for (int i = 1; i < nlength; i++) {
         assert i > 0;
         assert i <= this.lits.nVars() : "" + this.lits.nVars() + "/" + i;
         this.activity[i] = 0.0;
         if (this.lits.belongsToPool(i)) {
            this.heap.insert(i);
         }
      }
   }

   @Override
   public String toString() {
      return "VSIDS like heuristics from MiniSAT using a heap " + this.phaseStrategy;
   }

   public ILits getVocabulary() {
      return this.lits;
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
      out.println(prefix + "non guided choices\t: " + this.nullchoice);
   }

   @Override
   public void assignLiteral(int p) {
      this.phaseStrategy.assignLiteral(p);
   }

   @Override
   public void updateVarAtDecisionLevel(int q) {
      this.phaseStrategy.updateVarAtDecisionLevel(q);
   }

   @Override
   public double[] getVariableHeuristics() {
      return this.activity;
   }
}
