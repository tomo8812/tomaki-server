package reloc.org.sat4j.minisat.core;

import reloc.org.sat4j.annotations.Feature;

@Feature(value = "primeimplicant", parent = "expert")
public interface PrimeImplicantStrategy {
   int[] compute(Solver<? extends DataStructureFactory> var1);

   int[] getPrimeImplicantAsArrayWithHoles();
}
