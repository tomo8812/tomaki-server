package reloc.org.sat4j.minisat.core;

import java.io.PrintWriter;
import reloc.org.sat4j.annotations.Feature;

@Feature(value = "varheuristics", parent = "expert")
public interface IOrder {
   void setLits(ILits var1);

   int select();

   void undo(int var1);

   void updateVar(int var1);

   void updateVar(int var1, double var2);

   void init();

   void printStat(PrintWriter var1, String var2);

   void setVarDecay(double var1);

   void varDecayActivity();

   double varActivity(int var1);

   void assignLiteral(int var1);

   void setPhaseSelectionStrategy(IPhaseSelectionStrategy var1);

   IPhaseSelectionStrategy getPhaseSelectionStrategy();

   void updateVarAtDecisionLevel(int var1);

   double[] getVariableHeuristics();
}
