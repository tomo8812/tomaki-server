package reloc.org.sat4j.pb.constraints;

public class CompetResolutionPBMixedWLClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public CompetResolutionPBMixedWLClauseCardConstrDataStructure() {
      super(new UnitBinaryWLClauseConstructor(), new MinCardConstructor(), new MaxWatchPBConstructor());
   }
}
