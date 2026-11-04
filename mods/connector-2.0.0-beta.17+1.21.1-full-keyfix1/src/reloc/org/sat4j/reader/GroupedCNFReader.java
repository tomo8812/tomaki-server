package reloc.org.sat4j.reader;

import java.io.IOException;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IGroupSolver;

@Feature(value = "reader", parent = "expert")
public class GroupedCNFReader extends DimacsReader {
   private static final long serialVersionUID = 1L;
   private int numberOfComponents;
   private final IGroupSolver groupSolver;
   private int currentComponentIndex;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !GroupedCNFReader.class.desiredAssertionStatus();

   public GroupedCNFReader(IGroupSolver solver) {
      super(solver, "gcnf");
      this.groupSolver = solver;
   }

   @Override
   protected void readProblemLine() throws IOException, ParseFormatException {
      String line = this.scanner.nextLine();
      assert line != null;
      line = line.trim();
      String[] tokens = line.split("\\s+");
      if (tokens.length >= 5 && "p".equals(tokens[0]) && this.formatString.equals(tokens[1])) {
         int vars = Integer.parseInt(tokens[2]);
         assert vars > 0;
         this.solver.newVar(vars);
         this.expectedNbOfConstr = Integer.parseInt(tokens[3]);
         assert this.expectedNbOfConstr > 0;
         this.numberOfComponents = Integer.parseInt(tokens[4]);
         this.solver.setExpectedNumberOfClauses(this.expectedNbOfConstr);
      } else {
         throw new ParseFormatException("problem line expected (p " + this.formatString + " ...)");
      }
   }

   @Override
   protected boolean handleLine() throws ContradictionException, IOException, ParseFormatException {
      boolean added = false;
      String component = this.scanner.next();
      if (component.startsWith("{") && component.endsWith("}")) {
         this.currentComponentIndex = Integer.valueOf(component.substring(1, component.length() - 1));
         if (this.currentComponentIndex >= 0 && this.currentComponentIndex <= this.numberOfComponents) {
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
         } else {
            throw new ParseFormatException("wrong component index: " + this.currentComponentIndex);
         }
      } else {
         throw new ParseFormatException("Component index required at the beginning of the clause");
      }
   }

   @Override
   protected void flushConstraint() throws ContradictionException {
      try {
         if (this.currentComponentIndex == 0) {
            this.groupSolver.addClause(this.literals);
         } else {
            this.groupSolver.addClause(this.literals, this.currentComponentIndex);
         }
      } catch (IllegalArgumentException ex) {
         if (this.isVerbose()) {
            System.err.println("c Skipping constraint " + this.literals);
         }
      }
   }
}
