package reloc.org.sat4j.minisat.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchParams implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Logger LOGGER = Logger.getLogger("reloc.org.sat4j.core");
   private double claDecay;
   private double varDecay;
   private double conflictBoundIncFactor;
   private int initConflictBound;

   public SearchParams() {
      this(0.95, 0.999, 1.5, 100);
   }

   public SearchParams(int conflictBound) {
      this(0.95, 0.999, 1.5, conflictBound);
   }

   public SearchParams(double confincfactor, int conflictBound) {
      this(0.95, 0.999, confincfactor, conflictBound);
   }

   public SearchParams(double d, double e, double f, int i) {
      this.varDecay = d;
      this.claDecay = e;
      this.conflictBoundIncFactor = f;
      this.initConflictBound = i;
   }

   public double getClaDecay() {
      return this.claDecay;
   }

   public double getVarDecay() {
      return this.varDecay;
   }

   @Override
   public String toString() {
      StringBuilder stb = new StringBuilder();

      for (Field field : SearchParams.class.getDeclaredFields()) {
         if (!field.getName().startsWith("serial") && !field.getName().startsWith("class")) {
            stb.append(field.getName());
            stb.append("=");

            try {
               stb.append(field.get(this));
            } catch (IllegalArgumentException e) {
               LOGGER.log(Level.INFO, "Issue when reflectively accessing field", e);
            } catch (IllegalAccessException e) {
               LOGGER.log(Level.INFO, "Access issue when reflectively accessing field", e);
            }

            stb.append(" ");
         }
      }

      return stb.toString();
   }

   public void setConflictBoundIncFactor(double conflictBoundIncFactor) {
      this.conflictBoundIncFactor = conflictBoundIncFactor;
   }

   public void setInitConflictBound(int initConflictBound) {
      this.initConflictBound = initConflictBound;
   }

   public double getConflictBoundIncFactor() {
      return this.conflictBoundIncFactor;
   }

   public int getInitConflictBound() {
      return this.initConflictBound;
   }

   public void setClaDecay(double claDecay) {
      this.claDecay = claDecay;
   }

   public void setVarDecay(double varDecay) {
      this.varDecay = varDecay;
   }
}
