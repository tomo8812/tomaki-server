package reloc.org.sat4j.pb.reader;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.util.Iterator;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;

public class OPBReader2010 extends OPBReader2007 {
   public static final BigInteger SAT4J_MAX_BIG_INTEGER = new BigInteger("100000000000000000000000000000000000000000");
   private boolean isWbo = false;
   private BigInteger softLimit = SAT4J_MAX_BIG_INTEGER;
   private static final long serialVersionUID = 1L;
   private boolean softConstraint;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !OPBReader2010.class.desiredAssertionStatus();

   public OPBReader2010(IPBSolver solver) {
      super(solver);
   }

   @Override
   protected void readMetaData() throws IOException, ParseFormatException {
      char c = this.get();
      if (c != '*') {
         throw new ParseFormatException("First line of input file should be a comment");
      }

      String s = this.readWord();
      if (!this.eof() && "#variable=".equals(s)) {
         this.nbVars = Integer.parseInt(this.readWord());
         this.nbNewSymbols = this.nbVars + 1;
         s = this.readWord();
         if (!this.eof() && "#constraint=".equals(s)) {
            this.nbConstr = Integer.parseInt(this.readWord());
            this.charAvailable = false;
            if (!this.eol()) {
               String rest = this.in.readLine();
               if (rest != null && rest.contains("#soft")) {
                  this.isWbo = true;
                  this.hasObjFunc = true;
               }

               if (rest != null && rest.indexOf("#product=") != -1) {
                  String[] splitted = rest.trim().split(" ");
                  if (splitted[0].equals("#product=")) {
                     int nbproduct = Integer.parseInt(splitted[1]);
                     assert nbproduct >= 0;
                  }
               }
            }

            this.metaData(this.nbVars, this.nbConstr);
         } else {
            throw new ParseFormatException("First line should contain #constraint= as second keyword");
         }
      } else {
         throw new ParseFormatException("First line should contain #variable= as first keyword");
      }
   }

   @Override
   protected void readObjective() throws IOException, ParseFormatException {
      if (this.isWbo) {
         this.readSoftLine();
      } else {
         super.readObjective();
      }
   }

   private void readSoftLine() throws IOException, ParseFormatException {
      String s = this.readWord();
      if (s != null && "soft:".equals(s)) {
         s = this.readWord().trim();
         if (s != null && !";".equals(s)) {
            this.softLimit = new BigInteger(s);
         }

         this.skipSpaces();
         if (this.get() != ';') {
            throw new ParseFormatException("soft: line should end with a semicolon");
         }
      } else {
         throw new ParseFormatException("Did not find expected soft: line");
      }
   }

   @Override
   protected void beginConstraint() {
      super.beginConstraint();
      this.softConstraint = false;

      try {
         if (this.isWbo) {
            this.skipSpaces();
            char c = this.get();
            this.putback(c);
            if (c == '[') {
               this.softConstraint = true;
               String s = this.readWord();
               if (!s.endsWith("]")) {
                  throw new ParseFormatException("Expecting end of weight ");
               }

               BigInteger coeff = new BigInteger(s.substring(1, s.length() - 1));
               this.getCoeffs().push(coeff);
               int varId = this.nbNewSymbols++;
               this.getVars().push(varId);
            }
         }
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   @Override
   protected void endConstraint() throws ContradictionException {
      if (this.softConstraint) {
         int varId = this.getVars().last();
         BigInteger constrWeight = this.d;
         Iterator<BigInteger> it = this.coeffs.iterator();

         while (it.hasNext()) {
            constrWeight = constrWeight.add(it.next().abs());
         }

         if ("<=".equals(this.operator)) {
            constrWeight = constrWeight.negate();
         }

         this.coeffs.push(constrWeight);
         this.lits.push(varId);
      }

      super.endConstraint();
   }

   @Override
   public IProblem parseInstance(Reader input) throws ParseFormatException, ContradictionException {
      super.parseInstance(input);
      if (this.isWbo && this.softLimit != SAT4J_MAX_BIG_INTEGER) {
         this.solver.addPseudoBoolean(this.getVars(), this.getCoeffs(), false, this.softLimit.subtract(BigInteger.ONE));
      }

      return this.solver;
   }
}
