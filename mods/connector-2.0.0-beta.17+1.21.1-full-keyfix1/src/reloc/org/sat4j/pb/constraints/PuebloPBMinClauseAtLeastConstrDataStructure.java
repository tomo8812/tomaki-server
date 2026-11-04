package reloc.org.sat4j.pb.constraints;

public class PuebloPBMinClauseAtLeastConstrDataStructure extends AbstractPBClauseCardConstrDataStructure {
   private static final long serialVersionUID = 1L;

   public PuebloPBMinClauseAtLeastConstrDataStructure() {
      super(new UnitBinaryHTClausePBConstructor(), new AtLeastCardPBConstructor(), new PuebloMinWatchPBConstructor());
   }
}
