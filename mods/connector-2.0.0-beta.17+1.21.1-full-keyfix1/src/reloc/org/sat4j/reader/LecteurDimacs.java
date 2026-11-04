package reloc.org.sat4j.reader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.tools.Minimal4InclusionModel;

@Feature(value = "reader", parent = "expert")
public class LecteurDimacs extends Reader implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final int TAILLE_BUF = 16384;
   private ISolver s;
   protected transient BufferedInputStream in;
   private int nbVars = -1;
   private int nbClauses = -1;
   private static final char EOF = '\uffff';
   private final boolean pminimal = false;
   private Map<Integer, String> mapping;

   public LecteurDimacs(ISolver s) {
      this.s = s;
   }

   @Override
   public final IProblem parseInstance(InputStream input) throws ParseFormatException, ContradictionException, IOException {
      this.mapping = null;
      this.in = new BufferedInputStream(input, 16384);
      this.s.reset();
      this.passerCommentaire();
      if (this.nbVars < 0) {
         throw new ParseFormatException("DIMACS error: wrong max number of variables");
      }

      this.s.newVar(this.nbVars);
      this.s.setExpectedNumberOfClauses(this.nbClauses);
      char car = this.passerEspaces();
      if (this.nbClauses > 0) {
         if (car == '\uffff') {
            throw new ParseFormatException("DIMACS error: the clauses are missing");
         }

         this.ajouterClauses(car);
      }

      input.close();
      return this.s;
   }

   private char passerCommentaire() throws IOException, ParseFormatException {
      while (true) {
         char car = this.passerEspaces();
         if (car == 'p') {
            this.checkFormat();
            car = this.lectureNombreLiteraux();
         }

         if (car == 'c' || car == 'p') {
            car = this.manageCommentLine();
            if (car != '\uffff') {
               continue;
            }
         }

         return car;
      }
   }

   private void checkFormat() throws ParseFormatException {
      try {
         char car = this.passerEspaces();
         if ('c' != car || 110 != this.in.read() || 102 != this.in.read()) {
            throw new ParseFormatException("Expecting file in cnf format.");
         }
      } catch (IOException e) {
         throw new ParseFormatException(e);
      }
   }

   private char lectureNombreLiteraux() throws IOException {
      char car = this.nextChiffre();
      if (car != '\uffff') {
         this.nbVars = car - '0';

         while (true) {
            car = (char)this.in.read();
            if (car < '0' || car > '9') {
               car = this.nextChiffre();
               this.nbClauses = car - '0';

               while (true) {
                  car = (char)this.in.read();
                  if (car < '0' || car > '9') {
                     if (car != '\n' && car != '\uffff') {
                        this.nextLine();
                     }

                     return car;
                  }

                  this.nbClauses = 10 * this.nbClauses + car - 48;
               }
            }

            this.nbVars = 10 * this.nbVars + car - 48;
         }
      } else {
         return car;
      }
   }

   private void ajouterClauses(char car) throws IOException, ContradictionException, ParseFormatException {
      IVecInt lit = new VecInt();
      int val = 0;
      boolean neg = false;

      do {
         while (car == 'c') {
            this.manageCommentLine();
            car = (char)this.in.read();
         }

         if (car == '-') {
            neg = true;
            car = (char)this.in.read();
         } else if (car == '+') {
            car = (char)this.in.read();
         } else {
            if (car < '0' || car > '9') {
               throw new ParseFormatException("Unknown character " + car);
            }

            val = car - '0';
            car = (char)this.in.read();
         }

         while (car >= '0' && car <= '9') {
            val = val * 10 + car - 48;
            car = (char)this.in.read();
         }

         if (val == 0) {
            this.s.addClause(lit);
            lit.clear();
         } else {
            lit.push(neg ? -val : val);
            neg = false;
            val = 0;
         }

         if (car != '\uffff') {
            car = this.passerEspaces();
         }
      } while (car != '\uffff');

      if (!lit.isEmpty()) {
         this.s.addClause(lit);
      }
   }

   private char passerEspaces() throws IOException {
      char car;
      do {
         car = (char)this.in.read();
      } while (car == ' ' || car == '\n');

      return car;
   }

   private char nextLine() throws IOException {
      char car;
      do {
         car = (char)this.in.read();
      } while (car != '\n' && car != '\uffff');

      return car;
   }

   protected char manageCommentLine() throws IOException {
      StringBuilder stb = new StringBuilder();

      char car;
      do {
         car = (char)this.in.read();
         stb.append(car);
      } while (car != '\n' && car != '\uffff');

      String str = stb.toString().trim();
      if (str.startsWith("pmin")) {
         String[] tokens = str.split(" ");
         IVecInt p = new VecInt(tokens.length - 2);

         for (int i = 1; i < tokens.length - 1; i++) {
            p.push(Integer.parseInt(tokens[i]));
         }

         this.s = new Minimal4InclusionModel(this.s, p);
         System.out.println("c computing p-minimal model for p=" + p);
      } else if (this.isUsingMapping()) {
         String[] values = str.split("=");
         if (values.length == 2) {
            this.startsMapping();
            this.mapping.put(Integer.valueOf(values[0].trim()), values[1].trim());
         }
      }

      return car;
   }

   private char nextChiffre() throws IOException {
      char car;
      do {
         car = (char)this.in.read();
      } while (car < '0' || car > '9' && car != '\uffff');

      return car;
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
      if (this.isUsingMapping() && this.hasAMapping()) {
         this.decodeWithMapping(model, out);
      } else {
         for (int element : model) {
            out.print(element);
            out.print(" ");
         }

         out.print("0");
      }
   }

   private void decodeWithMapping(int[] model, PrintWriter out) {
      for (int element : model) {
         if (element > 0) {
            String mapped = this.mapping.get(element);
            if (mapped == null) {
               out.print(element);
            } else {
               out.print(mapped);
            }

            out.print(" ");
         }
      }

      out.print("0");
   }

   @Override
   public boolean hasAMapping() {
      return this.mapping != null;
   }

   @Override
   public Map<Integer, String> getMapping() {
      return this.mapping;
   }

   protected void startsMapping() {
      if (this.mapping == null) {
         this.mapping = new HashMap<>();
      }
   }
}
