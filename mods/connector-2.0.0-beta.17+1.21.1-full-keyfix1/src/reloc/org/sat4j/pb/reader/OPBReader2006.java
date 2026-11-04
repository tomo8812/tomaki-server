package reloc.org.sat4j.pb.reader;

import java.io.IOException;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.reader.ParseFormatException;

public class OPBReader2006 extends OPBReader2005 {
   private static final long serialVersionUID = 1L;

   public OPBReader2006(IPBSolver solver) {
      super(solver);
   }

   @Override
   protected void readTerm(StringBuilder coeff, StringBuilder var) throws IOException, ParseFormatException {
      this.readInteger(coeff);
      this.skipSpaces();
      if (!this.readIdentifier(var)) {
         throw new ParseFormatException("identifier expected");
      }
   }
}
