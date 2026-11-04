package reloc.org.sat4j.tools;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.Counter;
import reloc.org.sat4j.specs.AssignmentOrigin;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.SearchListener;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.specs.UnitClauseConsumer;
import reloc.org.sat4j.specs.UnitClauseProvider;
import reloc.org.sat4j.specs.UnitPropagationListener;

@Feature("solver")
public class ManyCore<S extends ISolver> implements ISolver, OutcomeListener, UnitClauseProvider, UnitClauseConsumer {
   private static final int NORMAL_SLEEP = 500;
   private static final int FAST_SLEEP = 50;
   private static final long serialVersionUID = 1L;
   private final String[] availableSolvers;
   protected final List<S> solvers;
   protected final int numberOfSolvers;
   private int winnerId = -1;
   private boolean resultFound;
   private AtomicInteger remainingSolvers;
   private volatile int sleepTime;
   private volatile boolean solved;
   private final IVecInt sharedUnitClauses = new VecInt();
   private final IVec<Counter> solversStats = new Vec<>();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ManyCore.class.desiredAssertionStatus();

   public ManyCore(ASolverFactory<S> factory, String... solverNames) {
      this(factory, false, solverNames);
   }

   public ManyCore(ASolverFactory<S> factory, boolean shareLearnedUnitClauses, String... solverNames) {
      this.availableSolvers = solverNames;
      this.numberOfSolvers = solverNames.length;
      this.solvers = new ArrayList<>(this.numberOfSolvers);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         S solver = factory.createSolverByName(this.availableSolvers[i]);
         solver.setUnitClauseConsumer(this);
         if (shareLearnedUnitClauses) {
            solver.setUnitClauseProvider(this);
         }

         this.solvers.add(solver);
         this.solversStats.push(new Counter(0));
      }
   }

   public ManyCore(String[] names, S... solverObjects) {
      this(false, names, solverObjects);
   }

   public ManyCore(boolean shareLearnedUnitClauses, String[] names, S... solverObjects) {
      this(shareLearnedUnitClauses, solverObjects);

      for (int i = 0; i < names.length; i++) {
         this.availableSolvers[i] = names[i];
      }
   }

   public ManyCore(S... solverObjects) {
      this(false, solverObjects);
   }

   public ManyCore(boolean shareLearnedUnitClauses, S... solverObjects) {
      this.availableSolvers = new String[solverObjects.length];

      for (int i = 0; i < solverObjects.length; i++) {
         this.availableSolvers[i] = "solver" + i;
      }

      this.numberOfSolvers = solverObjects.length;
      this.solvers = new ArrayList<>(this.numberOfSolvers);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.add(solverObjects[i]);
         solverObjects[i].setUnitClauseConsumer(this);
         if (shareLearnedUnitClauses) {
            solverObjects[i].setUnitClauseProvider(this);
         }

         this.solversStats.push(new Counter(0));
      }
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).addAllClauses(clauses);
      }
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtLeast(literals, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtMost(literals, degree));
      }

      return group;
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addExactly(literals, n));
      }

      return group;
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addClause(literals));
      }

      return group;
   }

   @Override
   public void clearLearntClauses() {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).clearLearntClauses();
      }

      this.sharedUnitClauses.clear();
   }

   @Override
   public void expireTimeout() {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).expireTimeout();
      }

      this.sleepTime = 50;
   }

   @Override
   public Map<String, Number> getStat() {
      return this.solvers.get(this.getWinnerId()).getStat();
   }

   @Override
   public int getTimeout() {
      return this.solvers.get(0).getTimeout();
   }

   @Override
   public long getTimeoutMs() {
      return this.solvers.get(0).getTimeoutMs();
   }

   @Override
   public int newVar() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int newVar(int howmany) {
      int result = 0;

      for (int i = 0; i < this.numberOfSolvers; i++) {
         result = this.solvers.get(i).newVar(howmany);
      }

      return result;
   }

   @Deprecated
   @Override
   public void printStat(PrintStream out, String prefix) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         out.printf("%s>>>>>>>>>> Solver number %d (%d answers) <<<<<<<<<<<<<<<<<<%n", prefix, i, this.solversStats.get(i).getValue());
         this.solvers.get(i).printStat(out, prefix);
      }
   }

   @Deprecated
   @Override
   public void printStat(PrintWriter out, String prefix) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         out.printf("%s>>>>>>>>>> Solver number %d (%d answers) <<<<<<<<<<<<<<<<<<%n", prefix, i, this.solversStats.get(i).getValue());
         this.solvers.get(i).printStat(out, prefix);
      }
   }

   @Override
   public boolean removeConstr(IConstr c) {
      if (c instanceof ConstrGroup) {
         ConstrGroup group = (ConstrGroup)c;
         boolean removed = true;

         for (int i = 0; i < this.numberOfSolvers; i++) {
            IConstr toRemove = group.getConstr(i);
            if (toRemove != null) {
               removed &= this.solvers.get(i).removeConstr(toRemove);
            }
         }

         this.sharedUnitClauses.clear();
         return removed;
      } else {
         throw new IllegalArgumentException("Can only remove a group of constraints!");
      }
   }

   @Override
   public void reset() {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).reset();
      }

      this.sharedUnitClauses.clear();
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setExpectedNumberOfClauses(nb);
      }
   }

   @Override
   public void setTimeout(int t) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setTimeout(t);
      }
   }

   @Override
   public void setTimeoutMs(long t) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setTimeoutMs(t);
      }
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setTimeoutOnConflicts(count);
      }
   }

   @Override
   public String toString(String prefix) {
      StringBuilder res = new StringBuilder();
      res.append(prefix);
      res.append("ManyCore solver with ");
      res.append(this.numberOfSolvers);
      res.append(" solvers running in parallel");
      res.append("\n");

      for (int i = 0; i < this.numberOfSolvers; i++) {
         res.append(prefix);
         res.append(">>>>>>>>>> Solver number ");
         res.append(i);
         res.append(" <<<<<<<<<<<<<<<<<<\n");
         res.append(this.solvers.get(i).toString(prefix));
         if (i < this.numberOfSolvers - 1) {
            res.append("\n");
         }
      }

      return res.toString();
   }

   @Override
   public int[] findModel() throws TimeoutException {
      return this.isSatisfiable() ? this.model() : null;
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      return this.isSatisfiable(assumps) ? this.model() : null;
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      return this.isSatisfiable(VecInt.EMPTY, false);
   }

   @Override
   public synchronized boolean isSatisfiable(IVecInt assumps, boolean globalTimeout) throws TimeoutException {
      this.remainingSolvers = new AtomicInteger(this.numberOfSolvers);
      this.solved = false;

      for (int i = 0; i < this.numberOfSolvers; i++) {
         new Thread(new RunnableSolver(i, this.solvers.get(i), assumps, globalTimeout, this)).start();
      }

      try {
         this.sleepTime = 500;

         do {
            this.wait(this.sleepTime);
         } while (this.remainingSolvers.get() > 0);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }

      if (!this.solved) {
         assert this.remainingSolvers.get() == 0;
         throw new TimeoutException();
      } else {
         return this.resultFound;
      }
   }

   @Override
   public boolean isSatisfiable(boolean globalTimeout) throws TimeoutException {
      return this.isSatisfiable(VecInt.EMPTY, globalTimeout);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      return this.isSatisfiable(assumps, false);
   }

   @Override
   public int[] model() {
      return this.solvers.get(this.getWinnerId()).model();
   }

   @Override
   public boolean model(int var) {
      return this.solvers.get(this.getWinnerId()).model(var);
   }

   @Override
   public int nConstraints() {
      return this.solvers.get(0).nConstraints();
   }

   @Override
   public int nVars() {
      return this.solvers.get(0).nVars();
   }

   @Override
   public void printInfos(PrintWriter out, String prefix) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         out.printf("%s>>>>>>>>>> Solver number %d <<<<<<<<<<<<<<<<<<%n", prefix, i);
         this.solvers.get(i).printInfos(out, prefix);
      }
   }

   @Override
   public synchronized void onFinishWithAnswer(boolean finished, boolean result, int index) {
      if (finished && !this.solved) {
         this.winnerId = index;
         this.solversStats.get(index).inc();
         this.solved = true;
         this.resultFound = result;

         for (int i = 0; i < this.numberOfSolvers; i++) {
            if (i != this.getWinnerId()) {
               this.solvers.get(i).expireTimeout();
            }
         }

         this.sleepTime = 50;
         if (this.isVerbose()) {
            System.out.println(this.getLogPrefix() + "And the winner is " + this.availableSolvers[this.getWinnerId()]);
         }
      }

      this.remainingSolvers.getAndDecrement();
   }

   @Override
   public boolean isDBSimplificationAllowed() {
      return this.solvers.get(0).isDBSimplificationAllowed();
   }

   @Override
   public void setDBSimplificationAllowed(boolean status) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setDBSimplificationAllowed(status);
      }
   }

   @Override
   public <I extends ISolverService> void setSearchListener(SearchListener<I> sl) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setSearchListener(sl);
      }
   }

   @Override
   public <I extends ISolverService> SearchListener<I> getSearchListener() {
      return this.solvers.get(0).getSearchListener();
   }

   @Override
   public int nextFreeVarId(boolean reserve) {
      int res = -1;

      for (int i = 0; i < this.numberOfSolvers; i++) {
         res = this.solvers.get(i).nextFreeVarId(reserve);
      }

      return res;
   }

   @Override
   public IConstr addBlockingClause(IVecInt literals) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addBlockingClause(literals));
      }

      return group;
   }

   private void checkWinnerId() {
      if (this.winnerId < 0) {
         throw new IllegalStateException("No solver solved the problem!");
      }
   }

   @Override
   public IVecInt createBlockingClauseForCurrentModel() {
      return this.solvers.get(this.getWinnerId()).createBlockingClauseForCurrentModel();
   }

   @Override
   public IConstr discardCurrentModel() throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      IVecInt blockingClause = this.createBlockingClauseForCurrentModel();

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addBlockingClause(blockingClause));
      }

      return group;
   }

   @Override
   public boolean removeSubsumedConstr(IConstr c) {
      if (c instanceof ConstrGroup) {
         ConstrGroup group = (ConstrGroup)c;
         boolean removed = true;

         for (int i = 0; i < this.numberOfSolvers; i++) {
            IConstr toRemove = group.getConstr(i);
            if (toRemove != null) {
               removed &= this.solvers.get(i).removeSubsumedConstr(toRemove);
            }
         }

         return removed;
      } else {
         throw new IllegalArgumentException("Can only remove a group of constraints!");
      }
   }

   @Override
   public boolean isVerbose() {
      return this.solvers.get(0).isVerbose();
   }

   @Override
   public void setVerbose(boolean value) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setVerbose(value);
      }
   }

   @Override
   public void setLogPrefix(String prefix) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setLogPrefix(prefix);
      }
   }

   @Override
   public String getLogPrefix() {
      return this.solvers.get(0).getLogPrefix();
   }

   @Override
   public IVecInt unsatExplanation() {
      return this.solvers.get(this.getWinnerId()).unsatExplanation();
   }

   @Override
   public int[] primeImplicant() {
      return this.solvers.get(this.getWinnerId()).primeImplicant();
   }

   @Override
   public boolean primeImplicant(int p) {
      return this.solvers.get(this.getWinnerId()).primeImplicant(p);
   }

   public List<S> getSolvers() {
      return new ArrayList<>(this.solvers);
   }

   @Override
   public int[] modelWithInternalVariables() {
      return this.solvers.get(this.getWinnerId()).modelWithInternalVariables();
   }

   @Override
   public int realNumberOfVariables() {
      return this.solvers.get(0).realNumberOfVariables();
   }

   @Override
   public void registerLiteral(int p) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).registerLiteral(p);
      }
   }

   @Override
   public boolean isSolverKeptHot() {
      return this.solvers.get(0).isSolverKeptHot();
   }

   @Override
   public void setKeepSolverHot(boolean value) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setKeepSolverHot(value);
      }
   }

   @Override
   public ISolver getSolvingEngine() {
      throw new UnsupportedOperationException("Not supported yet in ManyCore");
   }

   @Override
   public void printStat(PrintWriter out) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         out.printf(
            "%s>>>>>>>>>> Solver number %d (%d answers) <<<<<<<<<<<<<<<<<<%n", this.solvers.get(i).getLogPrefix(), i, this.solversStats.get(i).getValue()
         );
         this.solvers.get(i).printStat(out);
      }
   }

   @Override
   public void printInfos(PrintWriter out) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         out.printf("%s>>>>>>>>>> Solver number %d <<<<<<<<<<<<<<<<<<%n", this.getLogPrefix(), i);
         this.solvers.get(i).printInfos(out);
      }
   }

   @Override
   public synchronized void learnUnit(int p) {
      this.sharedUnitClauses.push(LiteralsUtils.toInternal(p));
   }

   @Feature(value = "unitclauseprovider", parent = "expert")
   @Override
   public synchronized void provideUnitClauses(UnitPropagationListener upl) {
      for (int i = 0; i < this.sharedUnitClauses.size(); i++) {
         upl.enqueue(this.sharedUnitClauses.get(i));
      }
   }

   @Override
   public void setUnitClauseProvider(UnitClauseProvider ucp) {
      throw new UnsupportedOperationException("Does not make sense in the parallel context");
   }

   @Override
   public IConstr addConstr(Constr constr) {
      throw new UnsupportedOperationException("Not implemented yet in ManyCore: cannot add a specific constraint to each solver");
   }

   @Override
   public IConstr addParity(IVecInt literals, boolean even) {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addParity(literals, even));
      }

      return group;
   }

   public int getWinnerId() {
      this.checkWinnerId();
      return this.winnerId;
   }

   @Override
   public AssignmentOrigin getOriginInModel(int p) {
      return this.solvers.get(this.getWinnerId()).getOriginInModel(p);
   }

   @Override
   public void setUnitClauseConsumer(UnitClauseConsumer ucc) {
      throw new UnsupportedOperationException("Does not make sense in the parallel context");
   }
}
