package reloc.org.sat4j.minisat.core;

import reloc.org.sat4j.specs.ILogAble;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.UnitPropagationListener;

public interface ICDCL<D extends DataStructureFactory> extends ISolver, UnitPropagationListener, ActivityListener, Learner {
   void setDataStructureFactory(D var1);

   @Deprecated
   void setLearner(LearningStrategy<D> var1);

   void setLearningStrategy(LearningStrategy<D> var1);

   void setSearchParams(SearchParams var1);

   SearchParams getSearchParams();

   SolverStats getStats();

   void setRestartStrategy(RestartStrategy var1);

   RestartStrategy getRestartStrategy();

   void setSimplifier(SimplificationType var1);

   void setSimplifier(ISimplifier var1);

   ISimplifier getSimplifier();

   void setLearnedConstraintsDeletionStrategy(LearnedConstraintsDeletionStrategy var1);

   void setLearnedConstraintsDeletionStrategy(ConflictTimer var1, LearnedConstraintsEvaluationType var2);

   void setLearnedConstraintsDeletionStrategy(LearnedConstraintsEvaluationType var1);

   IOrder getOrder();

   void setOrder(IOrder var1);

   void setNeedToReduceDB(boolean var1);

   void setLogger(ILogAble var1);

   ILogAble getLogger();
}
