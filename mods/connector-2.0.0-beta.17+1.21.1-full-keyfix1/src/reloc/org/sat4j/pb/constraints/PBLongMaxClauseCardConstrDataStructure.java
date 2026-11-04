package reloc.org.sat4j.pb.constraints;

public class PBLongMaxClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public PBLongMaxClauseCardConstrDataStructure() {
      super(new UnitBinaryHTClausePBConstructor(), new MinCardPBConstructor(), new MaxLongWatchPBCPConstructor());
   }
}
