package reloc.org.sat4j.pb.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
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

public class ObjectiveReducerPBSolverDecorator implements IPBSolver {
   private static final long serialVersionUID = -1637773414229087105L;
   private final IPBSolver decorated;
   private final List<IVecInt> atMostOneCstrs = new ArrayList<>();

   public ObjectiveReducerPBSolverDecorator(IPBSolver decorated) {
      this.decorated = decorated;
   }

   @Override
   public int[] model() {
      return this.decorated.model();
   }

   @Override
   public int newVar() {
      return this.decorated.newVar();
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      return this.decorated.addPseudoBoolean(lits, coeffs, moreThan, d);
   }

   @Override
   public boolean model(int var) {
      return this.decorated.model(var);
   }

   @Override
   public int nextFreeVarId(boolean reserve) {
      return this.decorated.nextFreeVarId(reserve);
   }

   @Override
   public int[] primeImplicant() {
      return this.decorated.primeImplicant();
   }

   @Override
   public boolean primeImplicant(int p) {
      return this.decorated.primeImplicant(p);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      IteratorInt it = coeffs.iterator();

      while (it.hasNext()) {
         if (it.next() != degree) {
            return this.decorated.addAtMost(literals, coeffs, degree);
         }
      }

      this.atMostOneCstrs.add(literals);
      return this.decorated.addAtMost(literals, coeffs, degree);
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      return this.decorated.isSatisfiable();
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean globalTimeout) throws TimeoutException {
      return this.decorated.isSatisfiable(assumps, globalTimeout);
   }

   @Override
   public void registerLiteral(int p) {
      this.decorated.registerLiteral(p);
   }

   @Override
   public void setExpectedNumberOfClauses(int nb) {
      this.decorated.setExpectedNumberOfClauses(nb);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      Iterator<BigInteger> it = coeffs.iterator();

      while (it.hasNext()) {
         if (!it.next().equals(degree)) {
            return this.decorated.addAtMost(literals, coeffs, degree);
         }
      }

      this.atMostOneCstrs.add(literals);
      return this.decorated.addAtMost(literals, coeffs, degree);
   }

   @Override
   public boolean isSatisfiable(boolean globalTimeout) throws TimeoutException {
      return this.decorated.isSatisfiable(globalTimeout);
   }

   @Override
   public IConstr addClause(IVecInt literals) throws ContradictionException {
      return this.decorated.addClause(literals);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      return this.decorated.isSatisfiable(assumps);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      return this.decorated.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public int[] findModel() throws TimeoutException {
      return this.decorated.findModel();
   }

   @Override
   public IConstr addBlockingClause(IVecInt literals) throws ContradictionException {
      return this.decorated.addBlockingClause(literals);
   }

   @Override
   public IConstr discardCurrentModel() throws ContradictionException {
      return this.decorated.discardCurrentModel();
   }

   @Override
   public IVecInt createBlockingClauseForCurrentModel() {
      return this.decorated.createBlockingClauseForCurrentModel();
   }

   @Override
   public boolean removeConstr(IConstr c) {
      return this.decorated.removeConstr(c);
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      return this.decorated.findModel(assumps);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      return this.decorated.addAtLeast(literals, coeffs, degree);
   }

   @Override
   public boolean removeSubsumedConstr(IConstr c) {
      return this.decorated.removeSubsumedConstr(c);
   }

   @Override
   public int nConstraints() {
      return this.decorated.nConstraints();
   }

   @Override
   public int newVar(int howmany) {
      return this.decorated.newVar(howmany);
   }

   @Override
   public void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException {
      Iterator<IVecInt> it = clauses.iterator();

      while (it.hasNext()) {
         this.addClause(it.next());
      }
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      return this.decorated.addExactly(literals, coeffs, weight);
   }

   @Override
   public int nVars() {
      return this.decorated.nVars();
   }

   @Override
   public void printInfos(PrintWriter out, String prefix) {
      this.decorated.printInfos(out, prefix);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      if (degree == 1) {
         this.atMostOneCstrs.add(literals);
      }

      return this.decorated.addAtMost(literals, degree);
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      return this.decorated.addExactly(literals, coeffs, weight);
   }

   @Override
   public void printInfos(PrintWriter out) {
      this.decorated.printInfos(out);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      return this.decorated.addAtLeast(literals, degree);
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      if (obj != null) {
         IVecInt newVars = new VecInt();
         IVec<BigInteger> newCoeffs = new Vec<>();
         Set<Integer> oldVarsToIgnore = new HashSet<>();
         IVecInt oldObjVars = obj.getVars();
         IVec<BigInteger> oldObjCoeffs = obj.getCoeffs();
         int nbReduc = this.processAtMostOneCstrs(obj, newVars, newCoeffs, oldVarsToIgnore);
         System.out.println("c " + nbReduc + " reductions due to atMostOne constraints");

         for (int i = 0; i < oldObjVars.size(); i++) {
            if (!oldVarsToIgnore.contains(oldObjVars.get(i))) {
               newVars.push(oldObjVars.get(i));
               newCoeffs.push(oldObjCoeffs.get(i));
            }
         }

         obj = new ObjectiveFunction(newVars, newCoeffs);
      }

      this.decorated.setObjectiveFunction(obj);
   }

   private int processAtMostOneCstrs(ObjectiveFunction obj, IVecInt newVars, IVec<BigInteger> newCoeffs, Set<Integer> oldVarsToIgnore) {
      System.out.println("c " + this.atMostOneCstrs.size() + " atMostOne constraints found");
      int nbReduc = 0;
      IVecInt oldObjVars = obj.getVars();
      IVec<BigInteger> oldObjCoeffs = obj.getCoeffs();

      for (IVecInt lits : this.atMostOneCstrs) {
         boolean allLitsIn = true;
         IteratorInt it = lits.iterator();

         while (it.hasNext()) {
            int next = it.next();
            if (oldVarsToIgnore.contains(next)) {
               allLitsIn = false;
               break;
            }

            int indexOf = oldObjVars.indexOf(next);
            if (indexOf == -1 || !oldObjCoeffs.get(indexOf).equals(BigInteger.ONE)) {
               allLitsIn = false;
               break;
            }
         }

         if (allLitsIn) {
            int newObjVar = this.nextFreeVarId(true);

            try {
               for (IteratorInt itx = lits.iterator(); itx.hasNext(); nbReduc++) {
                  int nextInt = itx.next();
                  this.decorated.addClause(new VecInt(new int[]{nextInt, newObjVar}));
                  oldVarsToIgnore.add(nextInt);
               }
            } catch (ContradictionException e) {
               Logger.getLogger("reloc.org.sat4j.pb").log(Level.SEVERE, "Inconsistency found, should not happen", e);
            }

            newVars.push(newObjVar);
            newCoeffs.push(BigInteger.ONE);
            nbReduc--;
         }
      }

      return nbReduc;
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return this.decorated.getObjectiveFunction();
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      return this.decorated.addExactly(literals, n);
   }

   @Override
   public void setTimeout(int t) {
      this.decorated.setTimeout(t);
   }

   @Override
   public void setTimeoutOnConflicts(int count) {
      this.decorated.setTimeoutOnConflicts(count);
   }

   @Override
   public void setTimeoutMs(long t) {
      this.decorated.setTimeoutMs(t);
   }

   @Override
   public int getTimeout() {
      return this.decorated.getTimeout();
   }

   @Override
   public long getTimeoutMs() {
      return this.decorated.getTimeoutMs();
   }

   @Override
   public void expireTimeout() {
      this.decorated.expireTimeout();
   }

   @Override
   public void reset() {
      this.decorated.reset();
   }

   @Override
   public void printStat(PrintStream out, String prefix) {
      this.decorated.printStat(out, prefix);
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
      this.decorated.printStat(out, prefix);
   }

   @Override
   public void printStat(PrintWriter out) {
      this.decorated.printStat(out);
   }

   @Override
   public Map<String, Number> getStat() {
      return this.decorated.getStat();
   }

   @Override
   public String toString(String prefix) {
      return this.decorated.toString(prefix);
   }

   @Override
   public void clearLearntClauses() {
      this.decorated.clearLearntClauses();
   }

   @Override
   public void setDBSimplificationAllowed(boolean status) {
      this.decorated.setDBSimplificationAllowed(status);
   }

   @Override
   public boolean isDBSimplificationAllowed() {
      return this.decorated.isDBSimplificationAllowed();
   }

   @Override
   public <S extends ISolverService> void setSearchListener(SearchListener<S> sl) {
      this.decorated.setSearchListener(sl);
   }

   @Override
   public <S extends ISolverService> SearchListener<S> getSearchListener() {
      return this.decorated.getSearchListener();
   }

   @Override
   public boolean isVerbose() {
      return this.decorated.isVerbose();
   }

   @Override
   public void setVerbose(boolean value) {
      this.decorated.setVerbose(value);
   }

   @Override
   public void setLogPrefix(String prefix) {
      this.decorated.setLogPrefix(prefix);
   }

   @Override
   public String getLogPrefix() {
      return this.decorated.getLogPrefix();
   }

   @Override
   public IVecInt unsatExplanation() {
      return this.decorated.unsatExplanation();
   }

   @Override
   public int[] modelWithInternalVariables() {
      return this.decorated.modelWithInternalVariables();
   }

   @Override
   public int realNumberOfVariables() {
      return this.decorated.realNumberOfVariables();
   }

   @Override
   public boolean isSolverKeptHot() {
      return this.decorated.isSolverKeptHot();
   }

   @Override
   public void setKeepSolverHot(boolean keepHot) {
      this.decorated.setKeepSolverHot(keepHot);
   }

   @Override
   public ISolver getSolvingEngine() {
      return this.decorated.getSolvingEngine();
   }

   @Override
   public void setUnitClauseProvider(UnitClauseProvider ucp) {
      this.decorated.setUnitClauseProvider(ucp);
   }

   @Override
   public IConstr addConstr(Constr constr) {
      return this.decorated.addConstr(constr);
   }

   @Override
   public IConstr addParity(IVecInt literals, boolean even) {
      return this.decorated.addParity(literals, even);
   }

   @Override
   public AssignmentOrigin getOriginInModel(int p) {
      return this.decorated.getOriginInModel(p);
   }

   @Override
   public void setUnitClauseConsumer(UnitClauseConsumer ucc) {
      this.decorated.setUnitClauseConsumer(ucc);
   }
}
