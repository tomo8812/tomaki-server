package reloc.org.sat4j.minisat.core;

import java.io.Serializable;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVec;

@Feature(value = "deletion", parent = "expert")
public interface LearnedConstraintsDeletionStrategy extends Serializable {
   void init();

   ConflictTimer getTimer();

   void reduce(IVec<Constr> var1);

   void onClauseLearning(Constr var1);

   void onConflictAnalysis(Constr var1);

   void onPropagation(Constr var1, int var2);
}
