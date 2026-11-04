package reloc.org.sat4j.pb.constraints;

public class PBMinClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public PBMinClauseCardConstrDataStructure() {
      super(new UnitBinaryHTClausePBConstructor(), new MinCardPBConstructor(), new MinWatchPBConstructor());
   }
}
