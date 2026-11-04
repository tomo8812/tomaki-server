package reloc.org.sat4j.specs;

import reloc.org.sat4j.annotations.Feature;

@Feature(value = "unitclauseprovider", parent = "expert")
public interface UnitClauseProvider {
   UnitClauseProvider VOID = new UnitClauseProvider() {
      @Override
      public void provideUnitClauses(UnitPropagationListener upl) {
      }
   };

   void provideUnitClauses(UnitPropagationListener var1);
}
