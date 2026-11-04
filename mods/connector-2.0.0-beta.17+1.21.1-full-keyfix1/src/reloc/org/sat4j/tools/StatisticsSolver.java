package reloc.org.sat4j.tools;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.Counter;
import reloc.org.sat4j.specs.AssignmentOrigin;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.SearchListener;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.specs.UnitClauseConsumer;
import reloc.org.sat4j.specs.UnitClauseProvider;

@Feature("solver")
public class StatisticsSolver implements ISolver {
   private static final String NOT_IMPLEMENTED_YET = "Not implemented yet!";
   private static final String THAT_SOLVER_ONLY_COMPUTE_STATISTICS = "That solver only compute statistics";
   private static final long serialVersionUID = 1L;
   private int expectedNumberOfConstraints;
   private int nbvars;
   private IVecInt[] sizeoccurrences;
   private int binarynegative = 0;
   private int binarypositive = 0;
   private int allpositive = 0;
   private int allnegative = 0;
   private int horn = 0;
   private int dualhorn = 0;
   private final Map<Integer, Counter> sizes = new HashMap<>();

   @Override
   public int[] model() {
      throw new UnsupportedOperationException("That solver only compute statistics");
   }

   @Override
   public boolean model(int var) {
      throw new UnsupportedOperationException("That solver only compute statistics");
   }

   @Override
   public int[] primeImplicant() {
      throw new UnsupportedOperationException("That solver only compute statistics");
   }

   @Override
   public boolean primeImplicant(int p) {
      throw new UnsupportedOperationException("That solver only compute statistics");
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      throw new TimeoutException("That solver only compute statistics");
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean globalTimeout) throws TimeoutException {
      throw new TimeoutException("That solver only compute statistics");
   }

   @Override
   public boolean isSatisfiable(boolean globalTimeout) throws TimeoutException {
      throw new TimeoutException("That solver only compute statistics");
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      throw new TimeoutException("That solver only compute statistics");
   }

   @Override
   public int[] findModel() throws TimeoutException {
      throw new TimeoutException("That solver only compute statistics");
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      throw new TimeoutException("That solver only compute statistics");
   }

   @Override
   public int nConstraints() {
      return this.expectedNumberOfConstraints;
   }

   @Override
   public int newVar(int howmany) {
      this.nbvars = howmany;
      this.sizeoccurrences = new IVecInt[howmany + 1 << 1];
      return howmany;
   }

   @Override
   public int nVars() {
      return this.nbvars;
   }

   @Deprecated
   @Override
   public void printInfos(PrintWriter out, String prefix) {
   }

   @Override
   public void printInfos(PrintWriter out) {
   }

   @Deprecated
   @Override
   public int newVar() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int nextFreeVarId(boolean reserve) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void registerLiteral(int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      this.expectedNumberOfConstraints = nb;
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      int size = literals.size();
      Counter counter = this.sizes.get(size);
      if (counter == null) {
         counter = new Counter(0);
         this.sizes.put(size, counter);
      }

      counter.inc();
      int pos = 0;
      int neg = 0;
      IteratorInt it = literals.iterator();

      while (it.hasNext()) {
         int x = it.next();
         if (x > 0) {
            pos++;
         } else {
            neg++;
         }

         int p = LiteralsUtils.toInternal(x);
         IVecInt list = this.sizeoccurrences[p];
         if (list == null) {
            list = new VecInt();
            this.sizeoccurrences[p] = list;
         }

         list.push(size);
      }

      if (neg == 0) {
         this.allpositive++;
         if (size == 2) {
            this.binarypositive++;
         }
      } else if (pos == 0) {
         this.allnegative++;
         if (size == 2) {
            this.binarynegative++;
         }
      } else if (pos == 1) {
         this.horn++;
      } else if (neg == 1) {
         this.dualhorn++;
      }

      return null;
   }

   @Override
   public IConstr addBlockingClause(IVecInt literals) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr discardCurrentModel() throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IVecInt createBlockingClauseForCurrentModel() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean removeConstr(IConstr c) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean removeSubsumedConstr(IConstr c) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setTimeout(int t) {
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setTimeoutMs(long t) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int getTimeout() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public long getTimeoutMs() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void expireTimeout() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void reset() {
   }

   @Deprecated
   @Override
   public void printStat(PrintStream out, String prefix) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Deprecated
   @Override
   public void printStat(PrintWriter out, String prefix) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void printStat(PrintWriter out) {
      int realNumberOfVariables = 0;
      int realNumberOfLiterals = 0;
      int pureLiterals = 0;
      int minOccV = Integer.MAX_VALUE;
      int maxOccV = Integer.MIN_VALUE;
      int sumV = 0;
      int minOccL = Integer.MAX_VALUE;
      int maxOccL = Integer.MIN_VALUE;
      int sumL = 0;
      if (this.sizeoccurrences != null) {
         int max = this.sizeoccurrences.length - 1;

         for (int i = 2; i < max; i += 2) {
            int sizeV = 0;
            boolean oneNull = false;

            for (int k = 0; k < 2; k++) {
               IVecInt list = this.sizeoccurrences[i + k];
               if (list == null) {
                  oneNull = true;
               } else {
                  realNumberOfLiterals++;
                  int sizeL = list.size();
                  sizeV += sizeL;
                  if (minOccL > sizeL) {
                     minOccL = sizeL;
                  }

                  if (sizeL > maxOccL) {
                     maxOccL = sizeL;
                  }

                  sumL += sizeL;
               }
            }

            if (sizeV > 0) {
               if (oneNull) {
                  pureLiterals++;
               }

               realNumberOfVariables++;
               if (minOccV > sizeV) {
                  minOccV = sizeV;
               }

               if (sizeV > maxOccV) {
                  maxOccV = sizeV;
               }

               sumV += sizeV;
            }
         }

         if (realNumberOfVariables > 0 && realNumberOfLiterals > 0) {
            System.out.println("c Distribution of constraints size:");
            int nbclauses = 0;

            for (Entry<Integer, Counter> entry : this.sizes.entrySet()) {
               System.out.printf("c %d => %d%n", entry.getKey(), entry.getValue().getValue());
               nbclauses += entry.getValue().getValue();
            }

            System.out.printf("c Real number of variables, literals, number of clauses, size (#literals), #pureliterals, ");
            System.out.printf("variable occurrences (min/max/avg) ");
            System.out.printf("literals occurrences (min/max/avg) ");
            System.out.println("Specific clauses: #positive  #negative #horn  #dualhorn #binary #binarynegative #binarypositive #binaryhorn #remaining");
            Counter binaryCounter = this.sizes.get(2);
            int nbBinary = binaryCounter == null ? 0 : binaryCounter.getValue();
            System.out
               .printf(
                  Locale.US,
                  "%d %d %d %d %d %d %d %.2f %d %d %.2f ",
                  realNumberOfVariables,
                  realNumberOfLiterals,
                  nbclauses,
                  sumL,
                  pureLiterals,
                  minOccV,
                  maxOccV,
                  sumV / (realNumberOfVariables * 1.0),
                  minOccL,
                  maxOccL,
                  sumL / (realNumberOfLiterals * 1.0)
               );
            System.out
               .printf(
                  "%d %d %d %d %d %d %d %d %d%n",
                  this.allpositive,
                  this.allnegative,
                  this.horn,
                  this.dualhorn,
                  nbBinary,
                  this.binarynegative,
                  this.binarypositive,
                  nbBinary - this.binarynegative - this.binarypositive,
                  nbclauses - this.allpositive - this.allnegative - this.horn - this.dualhorn
               );
         }
      }
   }

   @Override
   public Map<String, Number> getStat() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public String toString(String prefix) {
      return prefix + "Statistics about the benchmarks";
   }

   @Override
   public void clearLearntClauses() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setDBSimplificationAllowed(boolean status) {
   }

   @Override
   public boolean isDBSimplificationAllowed() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public <S extends ISolverService> void setSearchListener(SearchListener<S> sl) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public <S extends ISolverService> SearchListener<S> getSearchListener() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public boolean isVerbose() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setVerbose(boolean value) {
   }

   @Override
   public void setLogPrefix(String prefix) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public String getLogPrefix() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IVecInt unsatExplanation() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int[] modelWithInternalVariables() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public int realNumberOfVariables() {
      return this.nbvars;
   }

   @Override
   public boolean isSolverKeptHot() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setKeepSolverHot(boolean keepHot) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public ISolver getSolvingEngine() {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setUnitClauseProvider(UnitClauseProvider ucp) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addConstr(Constr constr) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public IConstr addParity(IVecInt literals, boolean even) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public AssignmentOrigin getOriginInModel(int p) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }

   @Override
   public void setUnitClauseConsumer(UnitClauseConsumer ucc) {
      throw new UnsupportedOperationException("Not implemented yet!");
   }
}
