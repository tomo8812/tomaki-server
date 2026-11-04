package reloc.org.sat4j.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

@Feature(value = "reader", parent = "expert")
public class DimacsReader extends Reader implements Serializable {
   private static final long serialVersionUID = 1L;
   protected int expectedNbOfConstr;
   protected final ISolver solver;
   private boolean checkConstrNb = true;
   protected final String formatString;
   protected EfficientScanner scanner;
   protected IVecInt literals = new VecInt();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !DimacsReader.class.desiredAssertionStatus();

   public DimacsReader(ISolver solver) {
      this(solver, "cnf");
   }

   public DimacsReader(ISolver solver, String format) {
      this.solver = solver;
      this.formatString = format;
   }

   public void disableNumberOfConstraintCheck() {
      this.checkConstrNb = false;
   }

   protected void skipComments() throws IOException {
      this.scanner.skipComments();
   }

   protected void readProblemLine() throws IOException, ParseFormatException {
      String line = this.scanner.nextLine().trim();
      if (line == null) {
         throw new ParseFormatException("premature end of file: <p cnf ...> expected");
      }

      String[] tokens = line.split("\\s+");
      if (tokens.length >= 4 && "p".equals(tokens[0]) && this.formatString.equals(tokens[1])) {
         int vars = Integer.parseInt(tokens[2]);
         assert vars > 0;
         this.solver.newVar(vars);
         this.expectedNbOfConstr = Integer.parseInt(tokens[3]);
         assert this.expectedNbOfConstr > 0;
         this.solver.setExpectedNumberOfClauses(this.expectedNbOfConstr);
      } else {
         throw new ParseFormatException("problem line expected (p cnf ...)");
      }
   }

   protected void readConstrs() throws IOException, ParseFormatException, ContradictionException {
      int realNbOfConstr = 0;
      this.literals.clear();
      boolean needToContinue = true;

      while (needToContinue) {
         boolean added = false;
         if (this.scanner.eof()) {
            if (this.literals.size() > 0) {
               this.flushConstraint();
               added = true;
            }

            needToContinue = false;
         } else {
            if (this.scanner.currentChar() == 'c') {
               this.scanner.skipRestOfLine();
               continue;
            }

            if (this.scanner.currentChar() == '%' && this.expectedNbOfConstr == realNbOfConstr) {
               if (this.solver.isVerbose()) {
                  System.out.println("Ignoring the rest of the file (SATLIB format");
               }
               break;
            }

            added = this.handleLine();
         }

         if (added) {
            realNbOfConstr++;
         }
      }

      if (this.checkConstrNb && this.expectedNbOfConstr != realNbOfConstr) {
         throw new ParseFormatException("wrong nbclauses parameter. Found " + realNbOfConstr + ", " + this.expectedNbOfConstr + " expected");
      }
   }

   protected void flushConstraint() throws ContradictionException {
      try {
         this.solver.addClause(this.literals);
      } catch (IllegalArgumentException ex) {
         if (this.isVerbose()) {
            System.err.println("c Skipping constraint " + this.literals);
         }
      }
   }

   protected boolean handleLine() throws ContradictionException, IOException, ParseFormatException {
      boolean added = false;

      while (!this.scanner.eof()) {
         int lit = this.scanner.nextInt();
         if (lit == 0) {
            if (this.literals.size() > 0) {
               this.flushConstraint();
               this.literals.clear();
               added = true;
            }
            break;
         }

         this.literals.push(lit);
      }

      return added;
   }

   @Override
   public IProblem parseInstance(InputStream in) throws ParseFormatException, ContradictionException, IOException {
      this.scanner = new EfficientScanner(in);
      return this.parseInstance();
   }

   private IProblem parseInstance() throws ParseFormatException, ContradictionException {
      this.solver.reset();

      try {
         this.skipComments();
         this.readProblemLine();
         this.readConstrs();
         this.scanner.close();
         return this.solver;
      } catch (IOException e) {
         throw new ParseFormatException(e);
      } catch (NumberFormatException e) {
         throw new ParseFormatException("integer value expected ");
      }
   }

   @Override
   public String decode(int[] model) {
      StringBuilder stb = new StringBuilder();

      for (int element : model) {
         stb.append(element);
         stb.append(" ");
      }

      stb.append("0");
      return stb.toString();
   }

   @Override
   public void decode(int[] model, PrintWriter out) {
      for (int element : model) {
         out.print(element);
         out.print(" ");
      }

      out.print("0");
   }

   protected ISolver getSolver() {
      return this.solver;
   }
}
