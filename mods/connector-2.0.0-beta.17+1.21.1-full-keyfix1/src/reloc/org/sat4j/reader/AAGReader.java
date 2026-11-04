package reloc.org.sat4j.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.tools.GateTranslator;

@Feature(value = "reader", parent = "expert")
public class AAGReader extends Reader {
   private static final int FALSE = 0;
   private static final int TRUE = 1;
   private final GateTranslator solver;
   private int maxvarid;
   private int nbinputs;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AAGReader.class.desiredAssertionStatus();

   AAGReader(ISolver s) {
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

   @Override
   public IProblem parseInstance(InputStream in) throws ParseFormatException, ContradictionException, IOException {
      EfficientScanner scanner = new EfficientScanner(in);
      String prefix = scanner.next();
      if (!"aag".equals(prefix)) {
         throw new ParseFormatException("AAG format only!");
      }

      this.maxvarid = scanner.nextInt();
      this.nbinputs = scanner.nextInt();
      int nblatches = scanner.nextInt();
      int nboutputs = scanner.nextInt();
      if (nboutputs > 1) {
         throw new ParseFormatException("CNF conversion allowed for single output circuit only!");
      }

      int nbands = scanner.nextInt();
      this.solver.newVar(this.maxvarid + 1);
      this.solver.setExpectedNumberOfClauses(3 * nbands + 2);
      this.readInput(this.nbinputs, scanner);
      assert nblatches == 0;
      if (nboutputs > 0) {
         int output0 = this.readOutput(nboutputs, scanner);
         this.readAnd(nbands, output0, scanner);
      }

      return this.solver;
   }

   private void readAnd(int nbands, int output0, EfficientScanner scanner) throws ContradictionException, IOException, ParseFormatException {
      for (int i = 0; i < nbands; i++) {
         int lhs = scanner.nextInt();
         int rhs0 = scanner.nextInt();
         int rhs1 = scanner.nextInt();
         this.solver.and(this.toDimacs(lhs), this.toDimacs(rhs0), this.toDimacs(rhs1));
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

   private int readOutput(int nboutputs, EfficientScanner scanner) throws IOException, ParseFormatException {
      IVecInt outputs = new VecInt(nboutputs);

      for (int i = 0; i < nboutputs; i++) {
         outputs.push(scanner.nextInt());
      }

      return outputs.get(0);
   }

   private IVecInt readInput(int numberOfInputs, EfficientScanner scanner) throws IOException, ParseFormatException {
      IVecInt inputs = new VecInt(numberOfInputs);

      for (int i = 0; i < numberOfInputs; i++) {
         inputs.push(scanner.nextInt());
      }

      return inputs;
   }
}
