package reloc.org.sat4j.core;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.VarMapper;

@Feature("constraint")
public class ConstrGroup implements IConstr {
   private final IVec<IConstr> constrs = new Vec<>();
   private final boolean disallowNullConstraints;

   public ConstrGroup() {
      this(true);
   }

   public ConstrGroup(boolean disallowNullConstraints) {
      this.disallowNullConstraints = disallowNullConstraints;
   }

   public void add(IConstr constr) {
      if (constr == null && this.disallowNullConstraints) {
         throw new IllegalArgumentException("The constraint you entered cannot be removed from the solver.");
      }

      this.constrs.push(constr);
   }

   public void clear() {
      this.constrs.clear();
   }

   public void removeFrom(ISolver solver) {
      for (int i = this.constrs.size() - 1; i >= 0; i--) {
         solver.removeConstr(this.constrs.get(i));
      }
   }

   public IConstr getConstr(int i) {
      return this.constrs.get(i);
   }

   @Override
   public int size() {
      return this.constrs.size();
   }

   @Override
   public boolean learnt() {
      return this.constrs.size() == 0 ? false : this.constrs.get(0).learnt();
   }

   @Override
   public double getActivity() {
      return 0.0;
   }

   @Override
   public int get(int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean canBePropagatedMultipleTimes() {
      return false;
   }

   @Override
   public String toString() {
      return this.constrs.toString();
   }

   @Override
   public String toString(VarMapper mapper) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();

      for (int i = 0; i < this.constrs.size(); i++) {
         stb.append(this.constrs.get(i).dump());
         if (i < this.constrs.size() - 1) {
            stb.append(System.lineSeparator());
         }
      }

      return stb.toString();
   }
}
