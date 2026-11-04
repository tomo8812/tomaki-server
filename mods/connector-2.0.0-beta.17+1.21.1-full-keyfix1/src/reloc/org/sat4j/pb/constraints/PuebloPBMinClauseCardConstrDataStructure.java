package reloc.org.sat4j.pb.constraints;

import reloc.org.sat4j.minisat.constraints.cnf.Clauses;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;

public class PuebloPBMinClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public PuebloPBMinClauseCardConstrDataStructure() {
      super(new UnitBinaryHTClausePBConstructor(), new MinCardPBConstructor(), new PuebloMinWatchPBConstructor());
   }

   @Override
   public Constr createClause(IVecInt literals) throws ContradictionException {
      IVecInt v = Clauses.sanityCheck(literals, this.getVocabulary(), this.solver);
      return this.constructClause(v);
   }
}
