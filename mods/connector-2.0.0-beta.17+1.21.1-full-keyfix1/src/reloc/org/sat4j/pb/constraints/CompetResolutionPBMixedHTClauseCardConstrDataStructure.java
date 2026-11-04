package reloc.org.sat4j.pb.constraints;

public class CompetResolutionPBMixedHTClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public CompetResolutionPBMixedHTClauseCardConstrDataStructure() {
      super(new UnitBinaryHTClauseConstructor(), new MinCardConstructor(), new MaxWatchPBConstructor());
   }
}
