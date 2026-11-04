package reloc.org.sat4j.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public class AllMUSes {
   private final AbstractClauseSelectorSolver<? extends ISolver> css;
   private final List<IVecInt> mssList;
   private final List<IVecInt> secondPhaseClauses;
   private final List<IVecInt> musList;
   private final ASolverFactory<? extends ISolver> factory;

   public AllMUSes(boolean group, ASolverFactory<? extends ISolver> factory) {
      this(group ? new GroupClauseSelectorSolver<>(factory.defaultSolver()) : new FullClauseSelectorSolver<>(factory.defaultSolver(), false), factory);
   }

   public AllMUSes(ASolverFactory<? extends ISolver> factory) {
      this(false, factory);
   }

   public AllMUSes(AbstractClauseSelectorSolver<? extends ISolver> css, ASolverFactory<? extends ISolver> factory) {
      this.css = css;
      this.factory = factory;
      this.mssList = new ArrayList<>();
      this.musList = new ArrayList<>();
      this.secondPhaseClauses = new ArrayList<>();
   }

   public <T extends ISolver> T getSolverInstance() {
      return (T)this.css;
   }

   public List<IVecInt> computeAllMUSes() {
      return this.computeAllMUSes(VecInt.EMPTY);
   }

   public void reset() {
      this.secondPhaseClauses.clear();
   }

   public List<IVecInt> computeAllMUSes(SolutionFoundListener listener) {
      return this.computeAllMUSes(VecInt.EMPTY, listener);
   }

   public List<IVecInt> computeAllMUSes(IVecInt assumptions) {
      return this.computeAllMUSes(assumptions, SolutionFoundListener.VOID);
   }

   public List<IVecInt> computeAllMUSes(IVecInt assumptions, SolutionFoundListener listener) {
      if (this.secondPhaseClauses.isEmpty()) {
         this.computeAllMSS(assumptions);
      }

      ISolver solver = this.factory.defaultSolver();

      for (IVecInt v : this.secondPhaseClauses) {
         try {
            solver.addClause(v);
         } catch (ContradictionException e) {
            return this.musList;
         }
      }

      AbstractMinimalModel minSolver = new Minimal4InclusionModel(solver, Minimal4InclusionModel.positiveLiterals(solver));
      return this.computeAllMUSes(assumptions, listener, minSolver);
   }

   public List<IVecInt> computeAllMUSesOrdered(SolutionFoundListener listener) {
      return this.computeAllMUSesOrdered(VecInt.EMPTY, listener);
   }

   public List<IVecInt> computeAllMUSesOrdered(IVecInt assumptions, SolutionFoundListener listener) {
      if (this.secondPhaseClauses.isEmpty()) {
         this.computeAllMSS();
      }

      ISolver solver = this.factory.defaultSolver();

      for (IVecInt v : this.secondPhaseClauses) {
         try {
            solver.addClause(v);
         } catch (ContradictionException e) {
            return this.musList;
         }
      }

      AbstractMinimalModel minSolver = new Minimal4CardinalityModel(solver, Minimal4InclusionModel.positiveLiterals(solver));
      return this.computeAllMUSes(assumptions, listener, minSolver);
   }

   private List<IVecInt> computeAllMUSes(IVecInt assumptions, SolutionFoundListener listener, ISolver minSolver) {
      if (this.css.isVerbose()) {
         System.out.println(this.css.getLogPrefix() + "Computing all MUSes ...");
      }

      this.css.internalState();

      try {
         while (minSolver.isSatisfiable(assumptions)) {
            IVecInt blockingClause = new VecInt();
            IVecInt mus = new VecInt();
            int[] model = minSolver.model();

            for (int i = 0; i < model.length; i++) {
               if (model[i] > 0) {
                  blockingClause.push(-model[i]);
                  mus.push(model[i]);
               }
            }

            this.musList.add(mus);
            listener.onSolutionFound(mus);

            try {
               minSolver.addBlockingClause(blockingClause);
            } catch (ContradictionException e) {
               break;
            }
         }
      } catch (TimeoutException e) {
         Logger.getLogger("reloc.org.sat4j.core").log(Level.INFO, "Timeout when computing all muses", e);
      }

      if (this.css.isVerbose()) {
         System.out.println(this.css.getLogPrefix() + "... done.");
      }

      this.css.externalState();
      return this.musList;
   }

   public List<IVecInt> computeAllMSS() {
      return this.computeAllMSS(VecInt.EMPTY);
   }

   public List<IVecInt> computeAllMSS(IVecInt assumptions) {
      return this.computeAllMSS(assumptions, SolutionFoundListener.VOID);
   }

   public List<IVecInt> computeAllMSS(SolutionFoundListener mssListener) {
      return this.computeAllMSS(VecInt.EMPTY, mssListener);
   }

   public List<IVecInt> computeAllMSS(IVecInt assumptions, SolutionFoundListener listener) {
      IVecInt pLits = new VecInt();

      for (Integer i : this.css.getAddedVars()) {
         pLits.push(i);
      }

      AbstractMinimalModel min4Inc = new Minimal4InclusionModel(this.css, pLits);
      return this.computeAllMSS(assumptions, listener, min4Inc, pLits);
   }

   public List<IVecInt> computeAllMSSOrdered(IVecInt assumptions, SolutionFoundListener listener) {
      IVecInt pLits = new VecInt();

      for (Integer i : this.css.getAddedVars()) {
         pLits.push(i);
      }

      AbstractMinimalModel min4Inc = new Minimal4CardinalityModel(this.css, pLits);
      return this.computeAllMSS(assumptions, listener, min4Inc, pLits);
   }

   private List<IVecInt> computeAllMSS(IVecInt assumptions, SolutionFoundListener listener, ISolver min4Inc, IVecInt pLits) {
      if (this.css.isVerbose()) {
         System.out.println(this.css.getLogPrefix() + "Computing all MSSes ...");
      }

      this.css.internalState();
      int nVar = this.css.nVars();
      IVecInt fullMSS = new VecInt();

      for (int i = 0; i < this.css.getAddedVars().size(); i++) {
         fullMSS.push(i + 1);
      }

      try {
         while (min4Inc.isSatisfiable(assumptions)) {
            int[] fullmodel = min4Inc.modelWithInternalVariables();
            IVecInt mss = new VecInt();
            fullMSS.copyTo(mss);
            IVecInt blockingClause = new VecInt();
            IVecInt secondPhaseClause = new VecInt();

            for (int i = 0; i < pLits.size(); i++) {
               int clause = Math.abs(pLits.get(i));
               if (fullmodel[clause - 1] > 0) {
                  blockingClause.push(-clause);
                  secondPhaseClause.push(clause - nVar);
                  mss.remove(clause - nVar);
               }
            }

            this.mssList.add(mss);
            listener.onSolutionFound(mss);
            this.secondPhaseClauses.add(secondPhaseClause);

            try {
               this.css.addBlockingClause(blockingClause);
            } catch (ContradictionException e) {
               Logger.getLogger("reloc.org.sat4j.core").log(Level.INFO, "Trivial inconsistency", e);
               break;
            }
         }
      } catch (TimeoutException e) {
         Logger.getLogger("reloc.org.sat4j.core").log(Level.INFO, "Timeout during the first stage", e);
      }

      if (this.css.isVerbose()) {
         System.out.println(this.css.getLogPrefix() + "... done.");
      }

      this.css.externalState();
      return this.mssList;
   }

   public List<IVecInt> getMssList() {
      return this.mssList;
   }
}
