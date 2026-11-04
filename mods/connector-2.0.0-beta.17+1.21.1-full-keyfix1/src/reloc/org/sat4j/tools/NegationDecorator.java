package reloc.org.sat4j.tools;

import java.util.ArrayList;
import java.util.Collection;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public class NegationDecorator<T extends ISolver> extends AbstractClauseSelectorSolver<T> {
   private static final long serialVersionUID = 1L;
   private final Collection<Integer> addedVars = new ArrayList<>();

   public NegationDecorator(T decorated) {
      super(decorated);
      this.internalState();
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      int newVar = this.createNewVar(literals);
      IVecInt clause = new VecInt(2);
      clause.push(newVar);
      ConstrGroup group = new ConstrGroup();
      IteratorInt it = literals.iterator();

      while (it.hasNext()) {
         clause.push(-it.next());
         group.add(super.addClause(clause));
         clause.pop();
      }

      this.addedVars.add(newVar);
      return group;
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean global) throws TimeoutException {
      IVecInt vars = new VecInt();

      for (int var : this.getAddedVars()) {
         vars.push(var);
      }

      try {
         IConstr constr = super.addClause(vars);

         boolean returnValue;
         try {
            returnValue = super.isSatisfiable(assumps, global);
         } finally {
            this.removeConstr(constr);
         }

         return returnValue;
      } catch (ContradictionException e) {
         return false;
      }
   }

   @Override
   public Collection<Integer> getAddedVars() {
      return this.addedVars;
   }
}
