package reloc.org.sat4j.pb.reader;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class OPBReader2007 extends OPBReader2006 {
   private static final long serialVersionUID = 1L;
   protected int nbNewSymbols;
   private final Map<String, IVec<String>> varToProduct = new HashMap<>();
   private final Map<String, Map<String, String>> binaryProductToVar = new HashMap<>();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !OPBReader2007.class.desiredAssertionStatus();

   public OPBReader2007(IPBSolver solver) {
      super(solver);
   }

   @Override
   protected boolean isGoodFirstCharacter(char c) {
      return Character.isLetter(c) || c == '_' || c == '~';
   }

   @Override
   protected void checkId(StringBuilder s) throws ParseFormatException {
      int cpt = 1;
      if (s.charAt(0) == '~') {
         cpt = 2;
      }

      if (s.charAt(cpt - 1) != 'x') {
         throw new ParseFormatException("Only identifiers of the form 'x'YYY are allowed!");
      }

      int varID = Integer.parseInt(s.substring(cpt));
      if (varID > this.nbVars) {
         throw new ParseFormatException("Variable identifier larger than #variables in metadata.");
      }
   }

   @Override
   protected void readTerm(StringBuilder coeff, StringBuilder var) throws IOException, ParseFormatException {
      this.readInteger(coeff);
      this.skipSpaces();
      var.setLength(0);
      IVec<String> tmpLit = new Vec<>();
      StringBuilder tmpVar = new StringBuilder();

      while (this.readIdentifier(tmpVar)) {
         tmpLit = tmpLit.push(tmpVar.toString());
         this.skipSpaces();
      }

      if (tmpLit.size() == 0) {
         throw new ParseFormatException("identifier expected");
      }

      if (tmpLit.size() == 1) {
         var.append(tmpLit.last());
         tmpLit.pop();
      } else {
         try {
            var.append(this.linearizeProduct(tmpLit));
         } catch (ContradictionException e) {
            throw new ParseFormatException(e);
         }
      }
   }

   protected void literalInAProduct(String var, IVecInt lits) throws ParseFormatException {
      int beginning = var.charAt(0) == '~' ? 2 : 1;
      int id = Integer.parseInt(var.substring(beginning));
      int lid = (var.charAt(0) == '~' ? -1 : 1) * id;
      if (lid != 0 && Math.abs(lid) < this.nbNewSymbols) {
         lits.push(lid);
      } else {
         throw new ParseFormatException("Wrong variable id");
      }
   }

   protected void negateLiteralInAProduct(String var, IVecInt lits) {
      int beginning = var.charAt(0) == '~' ? 2 : 1;
      int id = Integer.parseInt(var.substring(beginning));
      int lid = (var.charAt(0) == '~' ? 1 : -1) * id;
      lits.push(lid);
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
   protected int translateVarToId(String var) throws ParseFormatException {
      int beginning = var.charAt(0) == '~' ? 2 : 1;
      int id = Integer.parseInt(var.substring(beginning));
      if (id != 0 && id < this.nbNewSymbols) {
         return (var.charAt(0) == 126 ? -1 : 1) * id;
      } else {
         throw new ParseFormatException("Wrong variable id format: " + var);
      }
   }

   private String linearizeProduct(IVec<String> tmpLit) throws ContradictionException, ParseFormatException {
      tmpLit.sort(String.CASE_INSENSITIVE_ORDER);
      String newVar = this.getProductVariable(tmpLit);
      if (newVar == null) {
         newVar = "X" + this.nbNewSymbols++;
         if (tmpLit.size() == 2) {
            Map<String, String> map1 = this.binaryProductToVar.get(tmpLit.get(0));
            if (map1 == null) {
               map1 = new HashMap<>();
               this.binaryProductToVar.put(tmpLit.get(0), map1);
            }

            map1.put(tmpLit.get(1), newVar);
         }

         this.varToProduct.put(newVar, tmpLit);
         IVecInt newLits = new VecInt();
         Iterator<String> iterator = tmpLit.iterator();

         while (iterator.hasNext()) {
            this.negateLiteralInAProduct(iterator.next(), newLits);
         }

         this.literalInAProduct(newVar, newLits);
         this.solver.addClause(newLits);
         newLits.clear();
         IVec<BigInteger> newCoefs = new Vec<>();
         Iterator<String> iteratorx = tmpLit.iterator();

         while (iteratorx.hasNext()) {
            this.literalInAProduct(iteratorx.next(), newLits);
            newCoefs.push(BigInteger.ONE);
         }

         this.literalInAProduct(newVar, newLits);
         newCoefs.push(new BigInteger(String.valueOf(-tmpLit.size())));
         this.solver.addPseudoBoolean(newLits, newCoefs, true, BigInteger.ZERO);
      }

      return newVar;
   }

   private String getProductVariable(IVec<String> lits) {
      if (lits.size() == 2) {
         Map<String, String> map = this.binaryProductToVar.get(lits.get(0));
         return map == null ? null : map.get(lits.get(1));
      }

      for (Entry<String, IVec<String>> c : this.varToProduct.entrySet()) {
         if (c.getValue().equals(lits)) {
            return c.getKey();
         }
      }

      return null;
   }

   @Override
   public String decode(int[] model) {
      StringBuilder stb = new StringBuilder();

      for (int element : model) {
         int p = element;
         if (Math.abs(p) <= this.nbVars) {
            if (p < 0) {
               stb.append("-x");
               stb.append(-p);
            } else {
               stb.append("x");
               stb.append(p);
            }

            stb.append(" ");
         }
      }

      return stb.toString();
   }

   @Override
   public void decode(int[] model, PrintWriter out) {
      for (int element : model) {
         int p = element;
         if (Math.abs(p) <= this.nbVars) {
            if (element < 0) {
               out.print("-x");
               out.print(-p);
            } else {
               out.print("x");
               out.print(p);
            }

            out.print(" ");
         }
      }
   }
}
