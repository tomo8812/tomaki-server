package reloc.org.sat4j.tools.counting;

import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public final class SamplingSetAdapter implements SamplingSet {
   private final IVecInt adaptee;

   private SamplingSetAdapter(IVecInt adaptee) {
      this.adaptee = adaptee;
   }

   public static SamplingSet of(IVecInt vec) {
      return new SamplingSetAdapter(vec);
   }

   public static SamplingSet of(int... variables) {
      return new SamplingSetAdapter(VecInt.of(variables));
   }

   @Override
   public int nVars() {
      return this.adaptee.size();
   }

   @Override
   public IteratorInt variables() {
      return this.adaptee.iterator();
   }
}
