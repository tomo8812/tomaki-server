package reloc.org.sat4j.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

@Feature(value = "reader", parent = "expert")
public class JSONReader<S extends ISolver> extends Reader {
   protected final S solver;
   public static final String CLAUSE = "(\\[(-?(\\d+)(,-?(\\d+))*)?\\])";
   public static final String CARD = "(\\[(\\[(-?(\\d+)(,-?(\\d+))*)?\\]),'[=<>]=?',-?\\d+\\])";
   public final String constraint;
   public final String formula;
   private static final Pattern CLAUSE_PATTERN = Pattern.compile("(\\[(-?(\\d+)(,-?(\\d+))*)?\\])");
   private static final Pattern CARD_PATTERN = Pattern.compile("(\\[(\\[(-?(\\d+)(,-?(\\d+))*)?\\]),'[=<>]=?',-?\\d+\\])");
   private final Pattern constraintPattern;

   public JSONReader(S solver) {
      this.solver = solver;
      this.constraint = this.constraintRegexp();
      this.formula = "^\\[(" + this.constraint + "(," + this.constraint + ")*)?\\]$";
      this.constraintPattern = Pattern.compile(this.constraint);
   }

   protected String constraintRegexp() {
      return "((\\[(-?(\\d+)(,-?(\\d+))*)?\\])|(\\[(\\[(-?(\\d+)(,-?(\\d+))*)?\\]),'[=<>]=?',-?\\d+\\]))";
   }

   private void handleConstraint(String constraint) throws ParseFormatException, ContradictionException {
      if (CARD_PATTERN.matcher(constraint).matches()) {
         this.handleCard(constraint);
      } else if (CLAUSE_PATTERN.matcher(constraint).matches()) {
         this.handleClause(constraint);
      } else {
         this.handleNotHandled(constraint);
      }
   }

   protected void handleNotHandled(String constraint) throws ParseFormatException, ContradictionException {
      throw new ParseFormatException("Unknown constraint: " + constraint);
   }

   private void handleClause(String constraint) throws ParseFormatException, ContradictionException {
      this.solver.addClause(this.getLiterals(constraint));
   }

   protected IVecInt getLiterals(String constraint) throws ParseFormatException {
      String trimmed = constraint.trim();
      trimmed = trimmed.substring(1, trimmed.length() - 1);
      String[] literals = trimmed.split(",");
      IVecInt clause = new VecInt();

      for (String literal : literals) {
         if (literal.length() > 0) {
            clause.push(Integer.valueOf(literal.trim()));
         }
      }

      return clause;
   }

   protected void handleCard(String constraint) throws ParseFormatException, ContradictionException {
      String trimmed = constraint.trim();
      trimmed = trimmed.substring(1, trimmed.length() - 1);
      Matcher matcher = CLAUSE_PATTERN.matcher(trimmed);
      if (matcher.find()) {
         IVecInt clause = this.getLiterals(matcher.group());
         trimmed = matcher.replaceFirst("");
         String[] str = trimmed.split(",");
         int degree = Integer.valueOf(str[2]);
         String comparator = str[1].substring(1, str[1].length() - 1);
         if ("=".equals(comparator) || "==".equals(comparator)) {
            this.solver.addExactly(clause, degree);
         } else if ("<=".equals(comparator)) {
            this.solver.addAtMost(clause, degree);
         } else if ("<".equals(comparator)) {
            this.solver.addAtMost(clause, degree - 1);
         } else if (">=".equals(comparator)) {
            this.solver.addAtLeast(clause, degree);
         } else if (">".equals(comparator)) {
            this.solver.addAtLeast(clause, degree + 1);
         }
      }
   }

   @Override
   public IProblem parseInstance(InputStream in) throws ParseFormatException, ContradictionException, IOException {
      StringWriter out = new StringWriter();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      String line;
      while ((line = reader.readLine()) != null) {
         out.append(line);
      }

      return this.parseString(out.toString());
   }

   public ISolver parseString(String json) throws ParseFormatException, ContradictionException {
      String trimmed = json.trim();
      if (!trimmed.matches(this.formula)) {
         throw new ParseFormatException("Wrong input " + json);
      }

      Matcher matcher = this.constraintPattern.matcher(trimmed);

      while (matcher.find()) {
         this.handleConstraint(matcher.group());
      }

      return this.solver;
   }

   @Deprecated
   @Override
   public String decode(int[] model) {
      return "[" + new VecInt(model) + "]";
   }

   @Override
   public void decode(int[] model, PrintWriter out) {
      out.print("[");
      out.print(new VecInt(model));
      out.print("]");
   }
}
