package reloc.org.sat4j.pb.reader;

import java.io.IOException;
import java.io.Reader;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.PBSolverHandle;
import reloc.org.sat4j.pb.tools.LexicoDecoratorPB;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;

public class OPBReader2012 extends OPBReader2010 {
   private LexicoDecoratorPB lexico = null;
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !OPBReader2012.class.desiredAssertionStatus();

   public OPBReader2012(PBSolverHandle solver) {
      super(solver);
   }

   @Override
   protected void readMetaData() throws IOException, ParseFormatException {
      super.readMetaData();
      this.skipSpaces();
      char c = this.get();
      if (c != '*') {
         this.putback(c);
      } else {
         this.skipWhiteSpacesButNewline();
         c = this.get();
         if (c != '\n') {
            this.putback(c);
            String s = this.readWord();
            if ("#aggregation=".equals(s)) {
               s = this.readWord();
               assert "lexico".equals(s);
               PBSolverHandle handle = (PBSolverHandle)this.solver;
               this.lexico = new LexicoDecoratorPB(handle.decorated());
               handle.changeDecorated(this.lexico);
            }

            if ("beginMapping".equals(s)) {
               this.startsMapping();
               this.get();
            } else {
               if (this.savedChar != '\n') {
                  this.in.readLine();
               }

               this.get();
            }
         }
      }
   }

   private void skipWhiteSpacesButNewline() throws IOException {
      char c;
      do {
         c = this.get();
      } while (c == ' ');

      this.putback(c);
   }

   @Override
   protected void readObjective() throws IOException, ParseFormatException {
      super.readObjective();
      if (this.lexico != null && !this.lits.isEmpty()) {
         this.lexico.addCriterion(this.lits.clone(), this.coeffs.clone());
         this.lits.clear();
         this.coeffs.clear();
         this.readObjective();
      }
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.hasObjFunc && this.lexico == null ? new ObjectiveFunction(this.getVars(), this.getCoeffs()) : null;
   }

   @Override
   public IProblem parseInstance(Reader input) throws ParseFormatException, ContradictionException {
      super.parseInstance(input);
      return ((PBSolverHandle)this.solver).decorated();
   }
}
