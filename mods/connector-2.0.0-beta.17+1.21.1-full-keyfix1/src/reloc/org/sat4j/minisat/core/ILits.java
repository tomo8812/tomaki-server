package reloc.org.sat4j.minisat.core;

import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.Propagatable;

public interface ILits {
   int UNDEFINED = -1;

   void init(int var1);

   int getFromPool(int var1);

   boolean belongsToPool(int var1);

   void resetPool();

   void ensurePool(int var1);

   void unassign(int var1);

   void satisfies(int var1);

   void forgets(int var1);

   boolean isSatisfied(int var1);

   boolean isFalsified(int var1);

   boolean isUnassigned(int var1);

   boolean isImplied(int var1);

   int nVars();

   int realnVars();

   int nextFreeVarId(boolean var1);

   void reset(int var1);

   int getLevel(int var1);

   void setLevel(int var1, int var2);

   Constr getReason(int var1);

   void setReason(int var1, Constr var2);

   IVec<Undoable> undos(int var1);

   void watch(int var1, Propagatable var2);

   IVec<Propagatable> watches(int var1);

   String valueToString(int var1);

   void setTrailPosition(int var1, int var2);

   int getTrailPosition(int var1);
}
