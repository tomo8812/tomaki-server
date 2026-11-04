package reloc.org.sat4j.pb.constraints;

public class CompetResolutionPBLongMixedWLClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public CompetResolutionPBLongMixedWLClauseCardConstrDataStructure() {
      super(new UnitBinaryWLClauseConstructor(), new MinCardConstructor(), new MaxLongWatchPBConstructor());
   }
}
