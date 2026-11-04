package reloc.org.sat4j.pb.constraints;

public class PBMaxClauseAtLeastConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public PBMaxClauseAtLeastConstrDataStructure() {
      super(new UnitBinaryHTClausePBConstructor(), new AtLeastCardPBConstructor(), new MaxWatchPBConstructor());
   }
}
