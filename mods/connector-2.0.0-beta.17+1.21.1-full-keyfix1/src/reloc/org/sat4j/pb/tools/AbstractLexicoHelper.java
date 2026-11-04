package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import java.util.Collection;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.OptToPBSATAdapter;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.SolutionFoundListener;

public abstract class AbstractLexicoHelper<T, C> extends DependencyHelper<T, C> implements SolutionFoundListener {
   private final LexicoDecoratorPB lexico;
   private boolean hasASolution;

   public AbstractLexicoHelper(LexicoDecoratorPB lexico) {
      super(new OptToPBSATAdapter(lexico));
      this.lexico = lexico;
      ((OptToPBSATAdapter)this.getSolver()).setSolutionFoundListener(this);
   }

   public AbstractLexicoHelper(LexicoDecoratorPB lexico, boolean explanationEnabled) {
      super(new OptToPBSATAdapter(lexico), explanationEnabled);
      this.lexico = lexico;
      ((OptToPBSATAdapter)this.getSolver()).setSolutionFoundListener(this);
   }

   public AbstractLexicoHelper(LexicoDecoratorPB lexico, boolean explanationEnabled, boolean canonicalOptFunctionEnabled) {
      super(new OptToPBSATAdapter(lexico), explanationEnabled, canonicalOptFunctionEnabled);
      this.lexico = lexico;
      ((OptToPBSATAdapter)this.getSolver()).setSolutionFoundListener(this);
   }

   public void addCriterion(Collection<T> things) {
      IVecInt literals = new VecInt(things.size());

      for (T thing : things) {
         literals.push(this.getIntValue(thing));
      }

      this.lexico.addCriterion(literals);
   }

   public void addWeightedCriterion(Collection<WeightedObject<T>> things) {
      IVecInt literals = new VecInt(things.size());
      IVec<BigInteger> coefs = new Vec<>(things.size());

      for (WeightedObject<T> wo : things) {
         literals.push(this.getIntValue(wo.thing));
         coefs.push(wo.getWeight());
      }

      this.lexico.addCriterion(literals, coefs);
   }

   @Override
   public boolean hasASolution() throws TimeoutException {
      try {
         return super.hasASolution();
      } catch (TimeoutException e) {
         if (this.hasASolution) {
            return true;
         } else {
            throw e;
         }
      }
   }

   @Override
   public boolean hasASolution(IVec<T> assumps) throws TimeoutException {
      try {
         return super.hasASolution(assumps);
      } catch (TimeoutException e) {
         if (this.hasASolution) {
            return true;
         } else {
            throw e;
         }
      }
   }

   @Override
   public boolean hasASolution(Collection<T> assumps) throws TimeoutException {
      try {
         return super.hasASolution(assumps);
      } catch (TimeoutException e) {
         if (this.hasASolution) {
            return true;
         } else {
            throw e;
         }
      }
   }

   public boolean isOptimal() {
      return ((OptToPBSATAdapter)this.getSolver()).isOptimal();
   }

   @Override
   public void onSolutionFound(int[] solution) {
      this.hasASolution = true;
   }

   @Override
   public void onSolutionFound(IVecInt solution) {
      this.hasASolution = true;
   }

   @Override
   public void onUnsatTermination() {
   }
}
