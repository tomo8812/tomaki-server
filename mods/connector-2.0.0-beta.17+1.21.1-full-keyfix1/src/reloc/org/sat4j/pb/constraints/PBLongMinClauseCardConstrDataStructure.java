package reloc.org.sat4j.pb.constraints;

public class PBLongMinClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public PBLongMinClauseCardConstrDataStructure() {
      super(new UnitBinaryHTClausePBConstructor(), new MinCardPBConstructor(), new MinLongWatchPBCPConstructor());
   }
}
