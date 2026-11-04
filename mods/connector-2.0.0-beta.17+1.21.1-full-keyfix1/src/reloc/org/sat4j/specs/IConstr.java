package reloc.org.sat4j.specs;

import reloc.org.sat4j.annotations.Feature;

@Feature("constraint")
public interface IConstr {
   boolean learnt();

   int size();

   int get(int var1);

   double getActivity();

   boolean canBePropagatedMultipleTimes();

   String toString(VarMapper var1);

   String dump();
}
