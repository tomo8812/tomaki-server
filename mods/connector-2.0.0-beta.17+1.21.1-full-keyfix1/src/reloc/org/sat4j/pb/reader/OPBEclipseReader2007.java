package reloc.org.sat4j.pb.reader;

import java.io.IOException;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.specs.IVecInt;

public class OPBEclipseReader2007 extends OPBReader2007 {
   private static final long serialVersionUID = 1L;
   private final IVecInt varExplain = new VecInt();

   public OPBEclipseReader2007(IPBSolver solver) {
      super(solver);
   }

   protected void beginListOfVariables() {
   }

   protected void endListOfVariables() {
   }

   @Override
   protected void readVariablesExplanation() throws IOException, ParseFormatException {
      StringBuilder var = new StringBuilder();
      this.skipSpaces();
      char c = this.get();
      if (c != 'e') {
         this.putback(c);
      } else {
         this.hasVariablesExplanation = true;
         if (this.get() == 'x' && this.get() == 'p' && this.get() == 'l' && this.get() == 'a' && this.get() == 'i' && this.get() == 'n' && this.get() == ':') {
            this.beginListOfVariables();

            while (!this.eof()) {
               this.readIdentifier(var);
               this.varExplain.push(this.translateVarToId(var.toString()));
               this.skipSpaces();
               c = this.get();
               if (c == ';') {
                  break;
               }

               this.putback(c);
            }

            this.endListOfVariables();
         } else {
            throw new ParseFormatException("input format error: 'explain:' expected");
         }
      }
   }

   @Override
   public IVecInt getListOfVariables() {
      if (this.hasVariablesExplanation) {
         IVecInt tmp = new VecInt();
         this.varExplain.moveTo(tmp);
         return tmp;
      } else {
         return null;
      }
   }
}
