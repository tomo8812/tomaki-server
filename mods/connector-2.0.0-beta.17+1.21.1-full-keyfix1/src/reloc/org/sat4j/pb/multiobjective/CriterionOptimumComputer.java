package reloc.org.sat4j.pb.multiobjective;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.OptToPBSATAdapter;
import reloc.org.sat4j.pb.PseudoOptDecorator;
import reloc.org.sat4j.pb.tools.ManyCorePB;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.SolutionFoundListener;

public class CriterionOptimumComputer<S extends IPBSolver> {
   private final ManyCorePB<S> solvers;
   private final List<ObjectiveFunction> objs = new ArrayList<>();
   private BigInteger[] opts;
   protected Semaphore lock;
   protected boolean timeoutOccured = false;

   public CriterionOptimumComputer(ManyCorePB<S> solvers) {
      this.solvers = solvers;
   }

   public void addObjectiveFunction(ObjectiveFunction obj) {
      this.objs.add(obj);
   }

   public void addObjectiveFunctions(Collection<ObjectiveFunction> objs) {
      this.objs.addAll(objs);
   }

   public void compute() {
      this.opts = new BigInteger[this.objs.size()];
      this.lock = new Semaphore(this.objs.size());
      this.timeoutOccured = false;

      for (int i = 0; i < this.objs.size(); i++) {
         OptToPBSATAdapter optimizer = new OptToPBSATAdapter(new PseudoOptDecorator(this.solvers.getSolvers().get(i)));
         optimizer.setObjectiveFunction(this.objs.get(i));
         optimizer.setSolutionFoundListener(new CriterionOptimumComputer.OptimumValueWriter(this, i));
         new CriterionOptimumComputer.SolverLauncher(optimizer).start();

         try {
            this.lock.acquire();
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }
   }

   public void expireAll() {
      for (int i = 0; i < this.objs.size(); i++) {
         this.solvers.getSolvers().get(i).expireTimeout();
      }

      this.opts = null;
   }

   public boolean timeoutOccured() {
      return this.timeoutOccured;
   }

   public BigInteger[] getOptimums() {
      return this.opts;
   }

   public void waitForSolvers() {
      try {
         this.lock.acquire(this.objs.size());
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   class OptimumValueWriter implements SolutionFoundListener {
      private static final long serialVersionUID = 1L;
      private final CriterionOptimumComputer<S> coc;
      private final int solverIndex;

      OptimumValueWriter(CriterionOptimumComputer<S> coc, int solverIndex) {
         this.coc = coc;
         this.solverIndex = solverIndex;
      }

      @Override
      public synchronized void onSolutionFound(int[] solution) {
         this.coc.opts[this.solverIndex] = this.coc.objs.get(this.solverIndex).calculateDegree(this.coc.solvers.getSolvers().get(this.solverIndex));
      }

      @Override
      public synchronized void onSolutionFound(IVecInt solution) {
         this.coc.opts[this.solverIndex] = this.coc.objs.get(this.solverIndex).calculateDegree(this.coc.solvers.getSolvers().get(this.solverIndex));
      }

      @Override
      public synchronized void onUnsatTermination() {
         this.coc.lock.release();
      }
   }

   class SolverLauncher extends Thread {
      private final IPBSolver solver;

      SolverLauncher(IPBSolver solver) {
         this.solver = solver;
      }

      @Override
      public void run() {
         try {
            this.solver.isSatisfiable();
         } catch (TimeoutException e) {
            Logger.getLogger("reloc.org.sat4j.pb").log(Level.INFO, "Timeout", e);
            CriterionOptimumComputer.this.timeoutOccured = true;
            CriterionOptimumComputer.this.lock.release();
         }
      }
   }
}
