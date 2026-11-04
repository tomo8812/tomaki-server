package reloc.org.sat4j.specs;

import java.io.PrintWriter;

public interface IProblem extends RandomAccessModel {
   int[] model();

   int[] primeImplicant();

   boolean primeImplicant(int var1);

   boolean isSatisfiable() throws TimeoutException;

   boolean isSatisfiable(IVecInt var1, boolean var2) throws TimeoutException;

   boolean isSatisfiable(boolean var1) throws TimeoutException;

   boolean isSatisfiable(IVecInt var1) throws TimeoutException;

   int[] findModel() throws TimeoutException;

   int[] findModel(IVecInt var1) throws TimeoutException;

   int nConstraints();

   int newVar(int var1);

   int nVars();

   @Deprecated
   void printInfos(PrintWriter var1, String var2);

   void printInfos(PrintWriter var1);
}
