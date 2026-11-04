package reloc.org.sat4j.pb.constraints;

public class CompetMinHTmixedClauseCardConstrDataStructureFactory extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public CompetMinHTmixedClauseCardConstrDataStructureFactory() {
      super(new UnitBinaryHTClauseConstructor(), new MinCardConstructor(), new MinWatchPBConstructor());
   }
}
