package reloc.org.sat4j.pb.constraints;

public class CompetResolutionPBLongMixedHTClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public CompetResolutionPBLongMixedHTClauseCardConstrDataStructure() {
      super(new UnitBinaryHTClauseConstructor(), new MinCardConstructor(), new MaxLongWatchPBConstructor());
   }
}
