package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.tools.encoding.EncodingStrategyAdapter;
import reloc.org.sat4j.tools.encoding.Policy;

@Feature("solver")
public class ClausalCardinalitiesDecorator<T extends ISolver> extends SolverDecorator<T> {
   private static final long serialVersionUID = 1L;
   private final EncodingStrategyAdapter encodingAdapter;

   public ClausalCardinalitiesDecorator(T solver) {
      super(solver);
      this.encodingAdapter = new Policy();
   }

   public ClausalCardinalitiesDecorator(T solver, EncodingStrategyAdapter encodingAd) {
      super(solver);
      this.encodingAdapter = encodingAd;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int k) throws ContradictionException {
      return k == 1 ? this.encodingAdapter.addAtLeastOne(this.decorated(), literals) : this.encodingAdapter.addAtLeast(this.decorated(), literals, k);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int k) throws ContradictionException {
      return k == 1 ? this.encodingAdapter.addAtMostOne(this.decorated(), literals) : this.encodingAdapter.addAtMost(this.decorated(), literals, k);
   }

   @Override
   public IConstr addExactly(IVecInt literals, int k) throws ContradictionException {
      return k == 1 ? this.encodingAdapter.addExactlyOne(this.decorated(), literals) : this.encodingAdapter.addExactly(this.decorated(), literals, k);
   }

   @Override
   public String toString() {
      return this.toString("");
   }

   @Override
   public String toString(String prefix) {
      return super.toString(prefix) + "\nCardinality to SAT encoding: \nEncoding: " + this.encodingAdapter + "\n";
   }
}
