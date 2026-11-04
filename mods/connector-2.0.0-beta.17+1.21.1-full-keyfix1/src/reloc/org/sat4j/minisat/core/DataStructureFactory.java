package reloc.org.sat4j.minisat.core;

import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.Propagatable;
import reloc.org.sat4j.specs.UnitPropagationListener;

public interface DataStructureFactory {
   Constr createClause(IVecInt var1) throws ContradictionException;

   Constr createUnregisteredClause(IVecInt var1);

   void learnConstraint(Constr var1);

   Constr createCardinalityConstraint(IVecInt var1, int var2) throws ContradictionException;

   Constr createUnregisteredCardinalityConstraint(IVecInt var1, int var2);

   void setUnitPropagationListener(UnitPropagationListener var1);

   void setLearner(Learner var1);

   void reset();

   ILits getVocabulary();

   IVec<Propagatable> getWatchesFor(int var1);

   void conflictDetectedInWatchesFor(int var1, int var2);
}
