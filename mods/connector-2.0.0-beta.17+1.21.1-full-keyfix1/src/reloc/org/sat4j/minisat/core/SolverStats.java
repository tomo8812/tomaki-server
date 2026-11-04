package reloc.org.sat4j.minisat.core;

import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SolverStats implements Serializable {
   private static final long serialVersionUID = 1L;
   private int starts;
   private long decisions;
   private long propagations;
   private long inspects;
   private long conflicts;
   private long learnedliterals;
   private long learnedbinaryclauses;
   private long learnedternaryclauses;
   private long learnedclauses;
   private long ignoredclauses;
   private long rootSimplifications;
   private long reducedliterals;
   private long changedreason;
   private int reduceddb;
   private int shortcuts;
   private long updateLBD;
   private int importedUnits;

   public void reset() {
      this.starts = 0;
      this.decisions = 0L;
      this.propagations = 0L;
      this.inspects = 0L;
      this.shortcuts = 0;
      this.conflicts = 0L;
      this.learnedliterals = 0L;
      this.learnedclauses = 0L;
      this.ignoredclauses = 0L;
      this.learnedbinaryclauses = 0L;
      this.learnedternaryclauses = 0L;
      this.rootSimplifications = 0L;
      this.reducedliterals = 0L;
      this.changedreason = 0L;
      this.reduceddb = 0;
      this.updateLBD = 0L;
      this.importedUnits = 0;
   }

   public void printStat(PrintWriter out, String prefix) {
      out.println(prefix + "starts\t\t: " + this.getStarts());
      out.println(prefix + "conflicts\t\t: " + this.conflicts);
      out.println(prefix + "decisions\t\t: " + this.decisions);
      out.println(prefix + "propagations\t\t: " + this.propagations);
      out.println(prefix + "inspects\t\t: " + this.inspects);
      out.println(prefix + "shortcuts\t\t: " + this.shortcuts);
      out.println(prefix + "learnt literals\t: " + this.learnedliterals);
      out.println(prefix + "learnt binary clauses\t: " + this.learnedbinaryclauses);
      out.println(prefix + "learnt ternary clauses\t: " + this.learnedternaryclauses);
      out.println(prefix + "learnt constraints\t: " + this.learnedclauses);
      out.println(prefix + "ignored constraints\t: " + this.ignoredclauses);
      out.println(prefix + "root simplifications\t: " + this.rootSimplifications);
      out.println(prefix + "removed literals (reason simplification)\t: " + this.reducedliterals);
      out.println(prefix + "reason swapping (by a shorter reason)\t: " + this.changedreason);
      out.println(prefix + "Calls to reduceDB\t: " + this.reduceddb);
      out.println(prefix + "Number of update (reduction) of LBD\t: " + this.updateLBD);
      out.println(prefix + "Imported unit clauses\t: " + this.importedUnits);
   }

   public Map<String, Number> toMap() {
      Map<String, Number> map = new HashMap<>();
      Class<?> clazz = this.getClass();

      do {
         for (Field f : clazz.getDeclaredFields()) {
            try {
               f.setAccessible(true);
               Object value = f.get(this);
               if (!"serialVersionUID".equals(f.getName()) && value instanceof Number) {
                  map.put(f.getName(), (Number)value);
               }
            } catch (IllegalArgumentException var8) {
            } catch (IllegalAccessException var9) {
            }
         }

         clazz = clazz.getSuperclass();
      } while (clazz != null);

      return map;
   }

   public int getStarts() {
      return this.starts;
   }

   public void incStarts() {
      this.starts++;
   }

   public long getDecisions() {
      return this.decisions;
   }

   public void incDecisions() {
      this.decisions++;
   }

   public long getPropagations() {
      return this.propagations;
   }

   public void incPropagations() {
      this.propagations++;
   }

   public long getInspects() {
      return this.inspects;
   }

   public void incInspects() {
      this.inspects++;
   }

   public long getConflicts() {
      return this.conflicts;
   }

   public void incConflicts() {
      this.conflicts++;
   }

   public long getLearnedliterals() {
      return this.learnedliterals;
   }

   public void incLearnedliterals() {
      this.learnedliterals++;
   }

   public long getLearnedbinaryclauses() {
      return this.learnedbinaryclauses;
   }

   public void incLearnedbinaryclauses() {
      this.learnedbinaryclauses++;
   }

   public long getLearnedternaryclauses() {
      return this.learnedternaryclauses;
   }

   public void incLearnedternaryclauses() {
      this.learnedternaryclauses++;
   }

   public long getLearnedclauses() {
      return this.learnedclauses;
   }

   public void incLearnedclauses() {
      this.learnedclauses++;
   }

   public long getIgnoredclauses() {
      return this.ignoredclauses;
   }

   public void incIgnoredclauses() {
      this.ignoredclauses++;
   }

   public long getRootSimplifications() {
      return this.rootSimplifications;
   }

   public void incRootSimplifications() {
      this.rootSimplifications++;
   }

   public long getReducedliterals() {
      return this.reducedliterals;
   }

   public void incReducedliterals(int increment) {
      this.reducedliterals += increment;
   }

   public long getChangedreason() {
      return this.changedreason;
   }

   public void incChangedreason() {
      this.changedreason++;
   }

   public int getReduceddb() {
      return this.reduceddb;
   }

   public void incReduceddb() {
      this.reduceddb++;
   }

   public int getShortcuts() {
      return this.shortcuts;
   }

   public void incShortcuts() {
      this.shortcuts++;
   }

   public long getUpdateLBD() {
      return this.updateLBD;
   }

   public void incUpdateLBD() {
      this.updateLBD++;
   }

   public int getImportedUnits() {
      return this.importedUnits;
   }

   public void incImportedUnits(int increment) {
      this.importedUnits += increment;
   }
}
