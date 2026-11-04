package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.tools.ManyCore;

public class ManyCorePB<S extends IPBSolver> extends ManyCore<S> implements IPBSolver {
   private static final long serialVersionUID = 1L;

   public ManyCorePB(ASolverFactory<S> factory, String... solverNames) {
      super(factory, solverNames);
   }

   public ManyCorePB(S... iSolver) {
      super(iSolver);
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addPseudoBoolean(lits, coeffs, moreThan, d));
      }

      return group;
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      for (int i = 0; i < this.numberOfSolvers; i++) {
         this.solvers.get(i).setObjectiveFunction(obj);
      }
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.solvers.get(0).getObjectiveFunction();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtMost(literals, coeffs, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtMost(literals, coeffs, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtLeast(literals, coeffs, degree));
      }

      return group;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addAtLeast(literals, coeffs, degree));
      }

      return group;
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addExactly(literals, coeffs, weight));
      }

      return group;
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);

      for (int i = 0; i < this.numberOfSolvers; i++) {
         group.add(this.solvers.get(i).addExactly(literals, coeffs, weight));
      }

      return group;
   }
}
