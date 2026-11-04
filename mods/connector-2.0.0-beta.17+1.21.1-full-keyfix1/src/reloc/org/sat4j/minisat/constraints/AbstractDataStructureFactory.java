package reloc.org.sat4j.minisat.constraints;

import java.io.Serializable;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.Learner;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.Propagatable;
import reloc.org.sat4j.specs.UnitPropagationListener;

public abstract class AbstractDataStructureFactory implements DataStructureFactory, Serializable {
   private static final long serialVersionUID = 1L;
   protected ILits lits;
   private final IVec<Propagatable> tmp = new Vec<>();
   protected UnitPropagationListener solver;
   protected Learner learner;

   @Override
   public void conflictDetectedInWatchesFor(int p, int i) {
      for (int j = i + 1; j < this.tmp.size(); j++) {
         this.lits.watch(p, this.tmp.get(j));
      }
   }

   @Override
   public IVec<Propagatable> getWatchesFor(int p) {
      this.tmp.clear();
      this.lits.watches(p).moveTo(this.tmp);
      return this.tmp;
   }

   protected AbstractDataStructureFactory() {
      this.lits = this.createLits();
   }

   protected abstract ILits createLits();

   @Override
   public ILits getVocabulary() {
      return this.lits;
   }

   @Override
   public void setUnitPropagationListener(UnitPropagationListener s) {
      this.solver = s;
   }

   @Override
   public void setLearner(Learner learner) {
      this.learner = learner;
   }

   @Override
   public void reset() {
   }

   @Override
   public void learnConstraint(Constr constr) {
      this.learner.learn(constr);
   }

   @Override
   public Constr createCardinalityConstraint(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Constr createUnregisteredCardinalityConstraint(IVecInt literals, int degree) {
      throw new UnsupportedOperationException();
   }
}
