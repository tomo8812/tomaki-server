package reloc.org.sat4j.tools.encoding;

import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class Policy extends EncodingStrategyAdapter {
   private static final long serialVersionUID = 1L;
   private static final Sequential seq = new Sequential();
   private static final Binary binary = new Binary();
   private static final Product product = new Product();
   private static final Commander commander = new Commander();
   private static final Binomial binomial = new Binomial();
   private static final Ladder ladder = new Ladder();
   private EncodingStrategyAdapter atMostOneEncoding = null;
   private EncodingStrategyAdapter atMostKEncoding = null;
   private EncodingStrategyAdapter exactlyOneEncoding = null;
   private EncodingStrategyAdapter exactlyKEncoding = null;
   private EncodingStrategyAdapter atLeastOneEncoding = null;
   private EncodingStrategyAdapter atLeastKEncoding = null;

   public static EncodingStrategyAdapter getAdapterFromEncodingName(EncodingStrategy encodingName) {
      switch (encodingName) {
         case BINARY:
            return binary;
         case BINOMIAL:
            return binomial;
         case COMMANDER:
            return commander;
         case LADDER:
            return ladder;
         case PRODUCT:
            return product;
         case SEQUENTIAL:
            return seq;
         case NATIVE:
         default:
            throw new IllegalStateException("The switch does not cover all encodings");
      }
   }

   public static EncodingStrategy getEncodingTypeFromAdapter(EncodingStrategyAdapter adapter) {
      if (adapter instanceof Binary) {
         return EncodingStrategy.BINARY;
      } else if (adapter instanceof Binomial) {
         return EncodingStrategy.BINOMIAL;
      } else if (adapter instanceof Commander) {
         return EncodingStrategy.COMMANDER;
      } else if (adapter instanceof Ladder) {
         return EncodingStrategy.LADDER;
      } else if (adapter instanceof Product) {
         return EncodingStrategy.PRODUCT;
      } else {
         return adapter instanceof Sequential ? EncodingStrategy.SEQUENTIAL : EncodingStrategy.NATIVE;
      }
   }

   public EncodingStrategyAdapter getAtMostOneEncoding() {
      return this.atMostOneEncoding;
   }

   public void setAtMostOneEncoding(EncodingStrategyAdapter atMostOneEncoding) {
      this.atMostOneEncoding = atMostOneEncoding;
   }

   public void setAtMostOneEncoding(EncodingStrategy atMostOneEncoding) {
      this.atMostOneEncoding = getAdapterFromEncodingName(atMostOneEncoding);
   }

   public EncodingStrategyAdapter getAtMostKEncoding() {
      return this.atMostKEncoding;
   }

   public void setAtMostKEncoding(EncodingStrategyAdapter atMostKEncoding) {
      this.atMostKEncoding = atMostKEncoding;
   }

   public void setAtMostKEncoding(EncodingStrategy atMostKEncoding) {
      this.atMostKEncoding = getAdapterFromEncodingName(atMostKEncoding);
   }

   public EncodingStrategyAdapter getExactlyOneEncoding() {
      return this.exactlyOneEncoding;
   }

   public void setExactlyOneEncoding(EncodingStrategyAdapter exactlyOneEncoding) {
      this.exactlyOneEncoding = exactlyOneEncoding;
   }

   public void setExactlyOneEncoding(EncodingStrategy exactlyOneEncoding) {
      this.exactlyOneEncoding = getAdapterFromEncodingName(exactlyOneEncoding);
   }

   public EncodingStrategyAdapter getExactlyKEncoding() {
      return this.exactlyKEncoding;
   }

   public void setExactlyKEncoding(EncodingStrategyAdapter exactlyKEncoding) {
      this.exactlyKEncoding = exactlyKEncoding;
   }

   public void setExactlyKEncoding(EncodingStrategy exactlyKEncoding) {
      this.exactlyKEncoding = getAdapterFromEncodingName(exactlyKEncoding);
   }

   public EncodingStrategyAdapter getAtLeastOneEncoding() {
      return this.atLeastOneEncoding;
   }

   public void setAtLeastOneEncoding(EncodingStrategyAdapter atLeastOneEncoding) {
      this.atLeastOneEncoding = atLeastOneEncoding;
   }

   public void setAtLeastOneEncoding(EncodingStrategy atLeastOneEncoding) {
      this.atLeastOneEncoding = getAdapterFromEncodingName(atLeastOneEncoding);
   }

   public EncodingStrategyAdapter getAtLeastKEncoding() {
      return this.atLeastKEncoding;
   }

   public void setAtLeastKEncoding(EncodingStrategyAdapter atLeastKEncoding) {
      this.atLeastKEncoding = atLeastKEncoding;
   }

   public void setAtLeastKEncoding(EncodingStrategy atLeastKEncoding) {
      this.atLeastKEncoding = getAdapterFromEncodingName(atLeastKEncoding);
   }

   @Override
   public IConstr addAtMost(ISolver solver, IVecInt literals, int k) throws ContradictionException {
      if (k == 0 || literals.size() == 1) {
         return super.addAtMost(solver, literals, k);
      } else if (literals.size() <= 1) {
         throw new UnsupportedOperationException("requires at least 2 literals");
      } else if (k == 1 && this.atMostOneEncoding != null) {
         return this.atMostOneEncoding.addAtMostOne(solver, literals);
      } else if (this.atMostKEncoding == null) {
         return super.addAtMost(solver, literals, k);
      } else {
         return k == 1 ? this.atMostKEncoding.addAtMostOne(solver, literals) : this.atMostKEncoding.addAtMost(solver, literals, k);
      }
   }

   @Override
   public IConstr addExactly(ISolver solver, IVecInt literals, int n) throws ContradictionException {
      if (n == 1 && this.exactlyOneEncoding != null) {
         return this.exactlyOneEncoding.addExactlyOne(solver, literals);
      } else if (this.exactlyKEncoding != null) {
         return n == 1 ? this.exactlyKEncoding.addExactlyOne(solver, literals) : this.exactlyKEncoding.addExactly(solver, literals, n);
      } else {
         return super.addExactly(solver, literals, n);
      }
   }

   @Override
   public IConstr addAtLeast(ISolver solver, IVecInt literals, int n) throws ContradictionException {
      if (n == 1) {
         if (this.atLeastOneEncoding != null) {
            return this.atLeastOneEncoding.addAtLeastOne(solver, literals);
         }
      } else if (this.atLeastKEncoding != null) {
         return this.atLeastKEncoding.addAtLeast(solver, literals, n);
      }

      return super.addAtLeast(solver, literals, n);
   }

   @Override
   public String toString() {
      String s = "";
      return s
         + "Policy = [At most K: "
         + getEncodingTypeFromAdapter(this.getAtMostKEncoding())
         + ", at most 1: "
         + getEncodingTypeFromAdapter(this.getAtMostOneEncoding())
         + ", exactly K: "
         + getEncodingTypeFromAdapter(this.getExactlyKEncoding())
         + ", exactly 1: "
         + getEncodingTypeFromAdapter(this.getExactlyOneEncoding())
         + "]";
   }
}
