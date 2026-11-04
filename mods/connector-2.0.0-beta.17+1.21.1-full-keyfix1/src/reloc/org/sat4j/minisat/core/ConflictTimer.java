package reloc.org.sat4j.minisat.core;

public interface ConflictTimer {
   void reset();

   void newConflict();
}
