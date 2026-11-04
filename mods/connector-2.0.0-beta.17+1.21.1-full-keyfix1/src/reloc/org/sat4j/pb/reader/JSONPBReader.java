package reloc.org.sat4j.pb.reader;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.reader.JSONReader;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class JSONPBReader extends JSONReader<IPBSolver> {
   public static final String WLITERAL = "\\[(-?\\d+),(-?\\d+)\\]";
   public static final String WCLAUSE = "(\\[(\\[(-?\\d+),(-?\\d+)\\](,\\[(-?\\d+),(-?\\d+)\\])*)?\\])";
   public static final String PB = "(\\[(\\[(\\[(-?\\d+),(-?\\d+)\\](,\\[(-?\\d+),(-?\\d+)\\])*)?\\]),'[=<>]=?',-?\\d+\\])";
   public static final String OBJECTIVE_FUNCTION = "(\\[('min'|'max'),(\\[(\\[(-?\\d+),(-?\\d+)\\](,\\[(-?\\d+),(-?\\d+)\\])*)?\\])\\])";
   public static final Pattern PSEUDO_PATTERN = Pattern.compile("(\\[(\\[(\\[(-?\\d+),(-?\\d+)\\](,\\[(-?\\d+),(-?\\d+)\\])*)?\\]),'[=<>]=?',-?\\d+\\])");
   public static final Pattern WCLAUSE_PATTERN = Pattern.compile("(\\[(\\[(-?\\d+),(-?\\d+)\\](,\\[(-?\\d+),(-?\\d+)\\])*)?\\])");
   public static final Pattern WLITERAL_PATTERN = Pattern.compile("\\[(-?\\d+),(-?\\d+)\\]");
   public static final Pattern OBJECTIVE_FUNCTION_PATTERN = Pattern.compile(
      "(\\[('min'|'max'),(\\[(\\[(-?\\d+),(-?\\d+)\\](,\\[(-?\\d+),(-?\\d+)\\])*)?\\])\\])"
   );
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !JSONPBReader.class.desiredAssertionStatus();

   public JSONPBReader(IPBSolver solver) {
      super(solver);
   }

   @Override
   protected void handleNotHandled(String constraint) throws ParseFormatException, ContradictionException {
      if (PSEUDO_PATTERN.matcher(constraint).matches()) {
         this.handlePB(constraint);
      } else {
         if (!OBJECTIVE_FUNCTION_PATTERN.matcher(constraint).matches()) {
            throw new UnsupportedOperationException("Wrong formula " + constraint);
         }

         this.handleObj(constraint);
      }
   }

   private void handleObj(String constraint) {
      Matcher matcher = WCLAUSE_PATTERN.matcher(constraint);
      if (matcher.find()) {
         String weightedLiterals = matcher.group();
         constraint = matcher.replaceFirst("");
         matcher = WLITERAL_PATTERN.matcher(weightedLiterals);
         IVecInt literals = new VecInt();
         String[] pieces = constraint.split(",");
         boolean negate = pieces[0].contains("max");
         IVec<BigInteger> coefs = new Vec<>();

         while (matcher.find()) {
            literals.push(Integer.valueOf(matcher.group(2)));
            BigInteger coef = new BigInteger(matcher.group(1));
            coefs.push(negate ? coef.negate() : coef);
         }

         this.solver.setObjectiveFunction(new ObjectiveFunction(literals, coefs));
      }
   }

   private void handlePB(String constraint) throws ContradictionException {
      Matcher matcher = WCLAUSE_PATTERN.matcher(constraint);
      if (matcher.find()) {
         String weightedLiterals = matcher.group();
         constraint = matcher.replaceFirst("");
         matcher = WLITERAL_PATTERN.matcher(weightedLiterals);
         IVecInt literals = new VecInt();
         IVecInt coefs = new VecInt();

         while (matcher.find()) {
            literals.push(Integer.valueOf(matcher.group(2)));
            coefs.push(Integer.valueOf(matcher.group(1)));
         }

         String[] pieces = constraint.split(",");
         String comp = pieces[1].substring(1, pieces[1].length() - 1);
         int degree = Integer.valueOf(pieces[2].substring(0, pieces[2].length() - 1));
         if ("=".equals(comp) || "==".equals(comp)) {
            this.solver.addExactly(literals, coefs, degree);
         } else if ("<=".equals(comp)) {
            this.solver.addAtMost(literals, coefs, degree);
         } else if ("<".equals(comp)) {
            this.solver.addAtMost(literals, coefs, degree - 1);
         } else if (">=".equals(comp)) {
            this.solver.addAtLeast(literals, coefs, degree);
         } else {
            assert ">".equals(comp);
            this.solver.addAtLeast(literals, coefs, degree + 1);
         }
      }
   }

   @Override
   protected String constraintRegexp() {
      return "((\\[(-?(\\d+)(,-?(\\d+))*)?\\])|(\\[(\\[(-?(\\d+)(,-?(\\d+))*)?\\]),'[=<>]=?',-?\\d+\\])|(\\[(\\[(\\[(-?\\d+),(-?\\d+)\\](,\\[(-?\\d+),(-?\\d+)\\])*)?\\]),'[=<>]=?',-?\\d+\\])|(\\[('min'|'max'),(\\[(\\[(-?\\d+),(-?\\d+)\\](,\\[(-?\\d+),(-?\\d+)\\])*)?\\])\\]))";
   }
}
