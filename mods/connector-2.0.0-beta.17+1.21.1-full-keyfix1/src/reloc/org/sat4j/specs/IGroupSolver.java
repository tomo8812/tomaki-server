package reloc.org.sat4j.specs;

import java.util.Collection;

public interface IGroupSolver extends ISolver {
   IConstr addClause(IVecInt var1, int var2) throws ContradictionException;

   Collection<Integer> getAddedVars();
}
