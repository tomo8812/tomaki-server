package reloc.org.sat4j.minisat.core;

import java.io.Serializable;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.Constr;

@Feature(value = "restarts", parent = "expert")
public interface RestartStrategy extends Serializable, ConflictTimer {
   void init(SearchParams var1, SolverStats var2);

   @Deprecated
   long nextRestartNumberOfConflict();

   boolean shouldRestart();

   void onRestart();

   void onBackjumpToRootLevel();

   void newLearnedClause(Constr var1, int var2);
}
