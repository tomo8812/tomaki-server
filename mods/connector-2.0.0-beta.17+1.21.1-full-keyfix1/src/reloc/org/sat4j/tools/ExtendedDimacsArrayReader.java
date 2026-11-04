package reloc.org.sat4j.tools;

import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class ExtendedDimacsArrayReader extends DimacsArrayReader {
   public static final int FALSE = 1;
   public static final int TRUE = 2;
   public static final int NOT = 3;
   public static final int AND = 4;
   public static final int NAND = 5;
   public static final int OR = 6;
   public static final int NOR = 7;
   public static final int XOR = 8;
   public static final int XNOR = 9;
   public static final int IMPLIES = 10;
   public static final int IFF = 11;
   public static final int IFTHENELSE = 12;
   public static final int ATLEAST = 13;
   public static final int ATMOST = 14;
   public static final int COUNT = 15;
   private static final long serialVersionUID = 1L;
   private final GateTranslator gater;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ExtendedDimacsArrayReader.class.desiredAssertionStatus();

   public ExtendedDimacsArrayReader(ISolver solver) {
      super(solver);
      this.gater = new GateTranslator(solver);
   }

   @Override
   protected boolean handleConstr(int gateType, int output, int[] inputs) throws ContradictionException {
      switch (gateType) {
         case 1:
            assert inputs.length == 0;
            this.gater.gateFalse(output);
            break;
         case 2:
            assert inputs.length == 0;
            this.gater.gateTrue(output);
            break;
         case 3:
            assert inputs.length == 1;
            this.gater.not(output, inputs[0]);
            break;
         case 4: {
            IVecInt literals = new VecInt(inputs);
            this.gater.and(output, literals);
            break;
         }
         case 5:
         case 7:
         case 9:
         case 10:
         default:
            throw new UnsupportedOperationException("Gate type " + gateType + " not handled yet");
         case 6: {
            IVecInt literals = new VecInt(inputs);
            this.gater.or(output, literals);
            break;
         }
         case 8: {
            IVecInt literals = new VecInt(inputs);
            this.gater.xor(output, literals);
            break;
         }
         case 11: {
            IVecInt literals = new VecInt(inputs);
            this.gater.iff(output, literals);
            break;
         }
         case 12:
            assert inputs.length == 3;
            this.gater.ite(output, inputs[0], inputs[1], inputs[2]);
      }

      return true;
   }
}
