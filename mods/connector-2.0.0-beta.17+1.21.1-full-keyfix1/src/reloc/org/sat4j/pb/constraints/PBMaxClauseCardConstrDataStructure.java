package reloc.org.sat4j.pb.constraints;

public class PBMaxClauseCardConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public PBMaxClauseCardConstrDataStructure() {
      super(new UnitBinaryHTClausePBConstructor(), new MinCardPBConstructor(), new MaxWatchPBConstructor());
   }
}
