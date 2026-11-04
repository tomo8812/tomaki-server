package reloc.org.sat4j.minisat.learning;

import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.LearningStrategy;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.minisat.core.SolverStats;
import reloc.org.sat4j.minisat.core.VarActivityListener;
import reloc.org.sat4j.specs.Constr;

public abstract class LimitedLearning<D extends DataStructureFactory> implements LearningStrategy<D> {
   private static final long serialVersionUID = 1L;
   private final NoLearningButHeuristics<D> none = new NoLearningButHeuristics<>();
   private final MiniSATLearning<D> all = new MiniSATLearning<>();
   protected ILits lits;
   private SolverStats stats;

   @Override
   public void setSolver(Solver<D> s) {
      if (s != null) {
         this.lits = s.getVocabulary();
         this.setVarActivityListener(s);
         this.all.setDataStructureFactory(s.getDSFactory());
         this.stats = s.getStats();
      }
   }

   @Override
   public void learns(Constr constr) {
      if (this.learningCondition(constr)) {
         this.all.learns(constr);
      } else {
         this.none.learns(constr);
         this.stats.incIgnoredclauses();
      }
   }

   protected abstract boolean learningCondition(Constr var1);

   @Override
   public void init() {
      this.all.init();
      this.none.init();
   }

   @Override
   public void setVarActivityListener(VarActivityListener s) {
      this.none.setVarActivityListener(s);
      this.all.setVarActivityListener(s);
   }
}
