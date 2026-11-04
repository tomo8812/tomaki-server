package reloc.org.sat4j.minisat.constraints;

import reloc.org.sat4j.minisat.constraints.cnf.Lits;
import reloc.org.sat4j.minisat.core.ILits;

public abstract class AbstractCardinalityDataStructure extends AbstractDataStructureFactory {
   private static final long serialVersionUID = 1L;

   @Override
   protected ILits createLits() {
      return new Lits();
   }
}
