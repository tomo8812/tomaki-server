package reloc.org.sat4j.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.tools.GateTranslator;

@Feature(value = "reader", parent = "expert")
public class AIGReader extends Reader {
   private static final int FALSE = 0;
   private static final int TRUE = 1;
   private final GateTranslator solver;
   private int maxvarid;
   private int nbinputs;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AIGReader.class.desiredAssertionStatus();

   AIGReader(ISolver s) {
      this.solver = new GateTranslator(s);
   }

   @Override
   public String decode(int[] model) {
      StringBuilder stb = new StringBuilder();

      for (int i = 0; i < this.nbinputs; i++) {
         stb.append(model[i] > 0 ? 1 : 0);
      }

      return stb.toString();
   }

   @Override
   public void decode(int[] model, PrintWriter out) {
      for (int i = 0; i < this.nbinputs; i++) {
         out.print(model[i] > 0 ? 1 : 0);
      }
   }

   int parseInt(InputStream in, char expected) throws IOException, ParseFormatException {
      int ch = in.read();
      if (ch >= 48 && ch <= 57) {
         int res = ch - 48;

         while ((ch = in.read()) >= 48 && ch <= 57) {
            res = 10 * res + ch - 48;
         }

         if (ch != expected) {
            throw new ParseFormatException("unexpected character");
         } else {
            return res;
         }
      } else {
         throw new ParseFormatException("expected digit");
      }
   }

   @Override
   public IProblem parseInstance(InputStream in) throws ParseFormatException, ContradictionException, IOException {
      if (in.read() == 97 && in.read() == 105 && in.read() == 103 && in.read() == 32) {
         this.maxvarid = this.parseInt(in, ' ');
         this.nbinputs = this.parseInt(in, ' ');
         int nblatches = this.parseInt(in, ' ');
         if (nblatches > 0) {
            throw new ParseFormatException("CNF conversion cannot handle latches!");
         }

         int nboutputs = this.parseInt(in, ' ');
         if (nboutputs > 1) {
            throw new ParseFormatException("CNF conversion allowed for single output circuit only!");
         }

         int nbands = this.parseInt(in, '\n');
         this.solver.newVar(this.maxvarid + 1);
         this.solver.setExpectedNumberOfClauses(3 * nbands + 2);
         if (nboutputs > 0) {
            assert nboutputs == 1;
            int output0 = this.parseInt(in, '\n');
            this.readAnd(nbands, output0, in, 2 * (this.nbinputs + 1));
         }

         return this.solver;
      } else {
         throw new ParseFormatException("AIG format only!");
      }
   }

   static int safeGet(InputStream in) throws IOException, ParseFormatException {
      int ch = in.read();
      if (ch == -1) {
         throw new ParseFormatException("AIG Error, EOF met too early");
      } else {
         return ch;
      }
   }

   static int decode(InputStream in) throws IOException, ParseFormatException {
      int x = 0;
      int i = 0;

      int ch;
      while (((ch = safeGet(in)) & 128) > 0) {
         System.out.println("=>" + ch);
         x |= (ch & 127) << 7 * i++;
      }

      return x | ch << 7 * i;
   }

   private void readAnd(int nbands, int output0, InputStream in, int startid) throws ContradictionException, IOException, ParseFormatException {
      int lhs = startid;

      for (int i = 0; i < nbands; i++) {
         int delta0 = decode(in);
         int delta1 = decode(in);
         int rhs0 = lhs - delta0;
         int rhs1 = rhs0 - delta1;
         this.solver.and(this.toDimacs(lhs), this.toDimacs(rhs0), this.toDimacs(rhs1));
         lhs += 2;
      }

      this.solver.gateTrue(this.maxvarid + 1);
      this.solver.gateTrue(this.toDimacs(output0));
   }

   private int toDimacs(int v) {
      if (v == 0) {
         return -(this.maxvarid + 1);
      }

      if (v == 1) {
         return this.maxvarid + 1;
      }

      int var = v >> 1;
      return (v & 1) == 0 ? var : -var;
   }
}
