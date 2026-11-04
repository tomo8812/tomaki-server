package reloc.org.sat4j.pb.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class OPBReader2005 extends Reader implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final IPBSolver solver;
   protected final IVecInt lits;
   protected final IVec<BigInteger> coeffs;
   protected BigInteger d;
   protected String operator;
   private final IVecInt objectiveVars = new VecInt();
   private final IVec<BigInteger> objectiveCoeffs = new Vec<>();
   protected boolean hasObjFunc = false;
   protected boolean hasVariablesExplanation = false;
   protected int nbVars;
   protected int nbConstr;
   protected int nbConstraintsRead;
   protected transient BufferedReader in;
   char savedChar;
   boolean charAvailable = false;
   boolean eofReached = false;
   private boolean eolReached = false;
   private Map<Integer, String> mapping;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !OPBReader2005.class.desiredAssertionStatus();

   protected void metaData(int nbvar, int nbconstr) {
      this.solver.newVar(nbvar);
      this.solver.setExpectedNumberOfClauses(nbconstr);
   }

   protected void beginObjective() {
   }

   protected void endObjective() {
      assert this.lits.size() == this.coeffs.size();
      assert this.lits.size() == this.coeffs.size();

      for (int i = 0; i < this.lits.size(); i++) {
         this.objectiveVars.push(this.lits.get(i));
         this.objectiveCoeffs.push(this.coeffs.get(i));
      }
   }

   protected void beginConstraint() {
      this.lits.clear();
      this.coeffs.clear();
      assert this.lits.size() == 0;
      assert this.coeffs.size() == 0;
   }

   protected void endConstraint() throws ContradictionException {
      assert this.lits.size() != 0;
      assert this.coeffs.size() != 0;
      assert this.lits.size() == this.coeffs.size();
      if ("=".equals(this.operator)) {
         this.solver.addExactly(this.lits, this.coeffs, this.d);
      } else if ("<=".equals(this.operator)) {
         this.solver.addAtMost(this.lits, this.coeffs, this.d);
      } else {
         assert ">=".equals(this.operator);
         this.solver.addAtLeast(this.lits, this.coeffs, this.d);
      }

      this.nbConstraintsRead++;
   }

   private void constraintTerm(BigInteger coeff, String var) throws ParseFormatException {
      this.coeffs.push(coeff);
      this.lits.push(this.translateVarToId(var));
   }

   protected int translateVarToId(String var) throws ParseFormatException {
      int id = Integer.parseInt(var.substring(1));
      return (this.savedChar == 45 ? -1 : 1) * id;
   }

   protected void constraintRelOp(String relop) {
      this.operator = relop;
   }

   protected void constraintRightTerm(BigInteger val) {
      this.d = val;
   }

   protected char get() throws IOException {
      if (this.charAvailable) {
         this.charAvailable = false;
         return this.savedChar;
      }

      int c = this.in.read();
      if (c == -1) {
         this.eofReached = true;
      }

      if (c != 10 && c != 13) {
         this.eolReached = false;
      } else {
         this.eolReached = true;
      }

      return (char)c;
   }

   public IVecInt getVars() {
      return this.objectiveVars;
   }

   public IVec<BigInteger> getCoeffs() {
      return this.objectiveCoeffs;
   }

   protected void putback(char c) {
      this.savedChar = c;
      this.charAvailable = true;
   }

   protected boolean eof() {
      return this.eofReached;
   }

   protected boolean eol() {
      return this.eolReached;
   }

   protected void skipSpaces() throws IOException {
      char c;
      do {
         c = this.get();
      } while (Character.isWhitespace(c));

      this.putback(c);
   }

   public String readWord() throws IOException {
      StringBuilder s = new StringBuilder();
      this.skipSpaces();

      char c;
      while (!Character.isWhitespace(c = this.get()) && !this.eol() && !this.eof()) {
         s.append(c);
      }

      this.putback(c);
      return s.toString();
   }

   public void readInteger(StringBuilder s) throws IOException {
      this.skipSpaces();
      s.setLength(0);
      char c = this.get();
      if (c == '-' || Character.isDigit(c)) {
         s.append(c);
      }

      while (Character.isDigit(c = this.get()) && !this.eol() && !this.eof()) {
         s.append(c);
      }

      this.putback(c);
   }

   protected boolean readIdentifier(StringBuilder s) throws IOException, ParseFormatException {
      s.setLength(0);
      this.skipSpaces();
      char c = this.get();
      if (this.eof()) {
         return false;
      }

      if (!this.isGoodFirstCharacter(c)) {
         this.putback(c);
         return false;
      }

      s.append(c);

      while (true) {
         c = this.get();
         if (this.eof()) {
            break;
         }

         if (!this.isGoodFollowingCharacter(c)) {
            this.putback(c);
            break;
         }

         s.append(c);
      }

      this.checkId(s);
      return true;
   }

   protected boolean isGoodFirstCharacter(char c) {
      return Character.isLetter(c) || c == '_';
   }

   protected boolean isGoodFollowingCharacter(char c) {
      return Character.isLetter(c) || Character.isDigit(c) || c == '_';
   }

   protected void checkId(StringBuilder s) throws ParseFormatException {
      int varID = Integer.parseInt(s.substring(1));
      if (varID > this.nbVars) {
         throw new ParseFormatException("Variable identifier larger than #variables in metadata.");
      }
   }

   private String readRelOp() throws IOException {
      this.skipSpaces();
      char c = this.get();
      if (this.eof()) {
         return null;
      } else if (c == '=') {
         return "=";
      } else {
         char following = this.get();
         if (c == '>' && following == '=') {
            return ">=";
         } else {
            return c == '<' && following == '=' ? "<=" : null;
         }
      }
   }

   protected void readMetaData() throws IOException, ParseFormatException {
      char c = this.get();
      if (c != '*') {
         throw new ParseFormatException("First line of input file should be a comment");
      }

      String s = this.readWord();
      if (!this.eof() && "#variable=".equals(s)) {
         this.nbVars = Integer.parseInt(this.readWord());
         s = this.readWord();
         if (!this.eof() && "#constraint=".equals(s)) {
            this.nbConstr = Integer.parseInt(this.readWord());
            this.in.readLine();
            this.metaData(this.nbVars, this.nbConstr);
         } else {
            throw new ParseFormatException("First line should contain #constraint= as second keyword");
         }
      } else {
         throw new ParseFormatException("First line should contain #variable= as first keyword");
      }
   }

   private void skipComments() throws IOException {
      char c = ' ';

      while (!this.eof() && (c = this.get()) == '*') {
         this.in.readLine();
      }

      this.putback(c);
   }

   protected void readTerm(StringBuilder coeff, StringBuilder var) throws IOException, ParseFormatException {
      this.readInteger(coeff);
      this.skipSpaces();
      char c = this.get();
      if (c != '*') {
         throw new ParseFormatException("'*' expected between a coefficient and a variable");
      }

      if (!this.readIdentifier(var)) {
         throw new ParseFormatException("identifier expected");
      }
   }

   protected void readVariablesExplanation() throws IOException, ParseFormatException {
      char c = this.get();

      while (c == '*') {
         String s = this.readWord();
         if ("beginMapping".equals(s)) {
            this.startsMapping();
            this.get();
            c = this.get();
         } else {
            if ("endMapping".equals(s)) {
               this.get();
               return;
            }

            String[] values = s.split("=");
            if (values.length == 2) {
               this.mapping.put(Integer.valueOf(values[0]), values[1]);
            }

            if (this.savedChar != '\n') {
               this.in.readLine();
            }

            this.get();
            c = this.get();
         }
      }

      this.putback(c);
   }

   protected void startsMapping() {
      this.mapping = new HashMap<>();
   }

   protected void readObjective() throws IOException, ParseFormatException {
      StringBuilder var = new StringBuilder();
      StringBuilder coeff = new StringBuilder();
      this.skipSpaces();
      char c = this.get();
      if (c != 'm') {
         this.putback(c);
         if (this.solver.isVerbose()) {
            System.out.println(this.solver.getLogPrefix() + " no objective function found");
         }
      } else {
         if (this.solver.isVerbose()) {
            System.out.println(this.solver.getLogPrefix() + " objective function found");
         }

         this.hasObjFunc = true;
         if (this.get() == 'i' && this.get() == 'n' && this.get() == ':') {
            this.beginObjective();

            while (!this.eof()) {
               this.readTerm(coeff, var);
               this.constraintTerm(new BigInteger(coeff.toString()), var.toString());
               this.skipSpaces();
               c = this.get();
               if (c == ';') {
                  break;
               }

               if (c != '-' && c != '+' && !Character.isDigit(c)) {
                  throw new ParseFormatException("unexpected character in objective function");
               }

               this.putback(c);
            }

            this.endObjective();
         } else {
            throw new ParseFormatException("input format error: 'min:' expected");
         }
      }
   }

   protected void readConstraint() throws IOException, ParseFormatException, ContradictionException {
      StringBuilder var = new StringBuilder();
      StringBuilder coeff = new StringBuilder();
      this.beginConstraint();

      while (!this.eof()) {
         this.readTerm(coeff, var);
         this.constraintTerm(new BigInteger(coeff.toString()), var.toString());
         this.skipSpaces();
         char c = this.get();
         if (c == '>' || c == '=' || c == '<') {
            this.putback(c);
            break;
         }

         if (c != '-' && c != '+' && !Character.isDigit(c)) {
            throw new ParseFormatException("unexpected character in constraint");
         }

         this.putback(c);
      }

      if (this.eof()) {
         throw new ParseFormatException("unexpected EOF before end of constraint");
      }

      String relop;
      if ((relop = this.readRelOp()) == null) {
         throw new ParseFormatException("unexpected relational operator in constraint");
      }

      this.constraintRelOp(relop);
      this.readInteger(coeff);
      this.constraintRightTerm(new BigInteger(coeff.toString()));
      this.skipSpaces();
      char c = this.get();
      if (!this.eof() && c == ';') {
         this.endConstraint();
      } else {
         throw new ParseFormatException("semicolon expected at end of constraint");
      }
   }

   public OPBReader2005(IPBSolver solver) {
      this.solver = solver;
      this.lits = new VecInt();
      this.coeffs = new Vec<>();
   }

   public void parse() throws IOException, ParseFormatException, ContradictionException {
      this.mapping = null;
      this.readMetaData();
      if (this.mapping != null) {
         this.readVariablesExplanation();
      }

      this.skipComments();
      this.readObjective();
      this.nbConstraintsRead = 0;

      while (!this.eof()) {
         this.skipSpaces();
         if (this.eof()) {
            break;
         }

         char c = this.get();
         this.putback(c);
         if (c == '*') {
            this.skipComments();
         }

         if (this.eof()) {
            break;
         }

         this.readConstraint();
      }

      if (this.nbConstraintsRead != this.nbConstr) {
         throw new ParseFormatException("Number of constraints read (" + this.nbConstraintsRead + ") is different from metadata (" + this.nbConstr + ")");
      }
   }

   @Override
   public IProblem parseInstance(java.io.Reader input) throws ParseFormatException, ContradictionException {
      IProblem problem = this.parseInstance(new LineNumberReader(input));
      this.solver.setObjectiveFunction(this.getObjectiveFunction());
      return problem;
   }

   protected IProblem parseInstance(LineNumberReader input) throws ParseFormatException, ContradictionException {
      this.solver.reset();
      this.in = input;

      try {
         this.parse();
         return this.solver;
      } catch (ContradictionException ce) {
         throw ce;
      } catch (ParseFormatException pfe) {
         throw new ParseFormatException(" line " + (input.getLineNumber() + 1) + ", " + pfe.getMessage().substring("Parsing Error".length()));
      } catch (Exception e) {
         throw new ParseFormatException(" line " + (input.getLineNumber() + 1) + ", " + e.toString(), e);
      }
   }

   @Override
   public String decode(int[] model) {
      StringBuilder stb = new StringBuilder();

      for (int i = 0; i < model.length; i++) {
         if (model[i] < 0) {
            stb.append("-x");
            stb.append(-model[i]);
         } else {
            stb.append("x");
            stb.append(model[i]);
         }

         stb.append(" ");
      }

      return stb.toString();
   }

   @Override
   public void decode(int[] model, PrintWriter out) {
      for (int i = 0; i < model.length; i++) {
         if (model[i] < 0) {
            out.print("-x");
            out.print(-model[i]);
         } else {
            out.print("x");
            out.print(model[i]);
         }

         out.print(" ");
      }
   }

   public ObjectiveFunction getObjectiveFunction() {
      return this.hasObjFunc ? new ObjectiveFunction(this.getVars(), this.getCoeffs()) : null;
   }

   public IVecInt getListOfVariables() {
      return null;
   }

   @Override
   public IProblem parseInstance(InputStream in) throws ParseFormatException, ContradictionException, IOException {
      return this.parseInstance(new InputStreamReader(in));
   }

   @Override
   public boolean hasAMapping() {
      return this.mapping != null;
   }

   @Override
   public Map<Integer, String> getMapping() {
      return this.mapping;
   }
}
