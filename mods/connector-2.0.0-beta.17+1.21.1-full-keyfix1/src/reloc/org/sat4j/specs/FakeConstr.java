package reloc.org.sat4j.specs;

import reloc.org.sat4j.annotations.Feature;

@Feature("constraint")
public final class FakeConstr implements IConstr {
   private static final String FAKE_I_CONSTR_MSG = "Fake IConstr";
   private static final IConstr instance = new FakeConstr();

   private FakeConstr() {
   }

   public static final IConstr instance() {
      return instance;
   }

   @Override
   public int size() {
      throw new UnsupportedOperationException("Fake IConstr");
   }

   @Override
   public boolean learnt() {
      throw new UnsupportedOperationException("Fake IConstr");
   }

   @Override
   public double getActivity() {
      throw new UnsupportedOperationException("Fake IConstr");
   }

   @Override
   public int get(int i) {
      throw new UnsupportedOperationException("Fake IConstr");
   }

   @Override
   public boolean canBePropagatedMultipleTimes() {
      throw new UnsupportedOperationException("Fake IConstr");
   }

   @Override
   public String toString(VarMapper mapper) {
      return "Fake IConstr";
   }

   @Override
   public String dump() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }
}
