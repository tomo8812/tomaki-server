package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.Backbone;
import reloc.org.sat4j.tools.GateTranslator;

public class DependencyHelper<T, C> {
   public static final INegator NO_NEGATION = new INegator() {
      @Override
      public boolean isNegated(Object thing) {
         return false;
      }

      @Override
      public Object unNegate(Object thing) {
         return thing;
      }
   };
   public static final INegator BASIC_NEGATION = new INegator() {
      @Override
      public boolean isNegated(Object thing) {
         return thing instanceof DependencyHelper.Negation;
      }

      @Override
      public Object unNegate(Object thing) {
         return ((DependencyHelper.Negation)thing).getThing();
      }
   };
   private static final long serialVersionUID = 1L;
   private final Map<T, Integer> mapToDimacs = new HashMap<>();
   private final Map<Integer, T> mapToDomain = new HashMap<>();
   final Map<IConstr, C> descs = new HashMap<>();
   private final XplainPB xplain;
   private final GateTranslator gator;
   final IPBSolver solver;
   private INegator negator = BASIC_NEGATION;
   private ObjectiveFunction objFunction;
   private IVecInt objLiterals;
   private IVec<BigInteger> objCoefs;
   private final boolean explanationEnabled;
   private final boolean canonicalOptFunction;

   public DependencyHelper(IPBSolver solver) {
      this(solver, true);
   }

   public DependencyHelper(IPBSolver solver, boolean explanationEnabled) {
      this(solver, explanationEnabled, true);
   }

   public DependencyHelper(IPBSolver solver, boolean explanationEnabled, boolean canonicalOptFunctionEnabled) {
      if (explanationEnabled) {
         this.xplain = new XplainPB(solver);
         this.solver = this.xplain;
      } else {
         this.xplain = null;
         this.solver = solver;
      }

      this.gator = new GateTranslator(this.solver);
      this.explanationEnabled = explanationEnabled;
      this.canonicalOptFunction = canonicalOptFunctionEnabled;
   }

   public void setNegator(INegator negator) {
      this.negator = negator;
   }

   protected int getIntValue(T thing) {
      return this.getIntValue(thing, true);
   }

   protected int getIntValue(T thing, boolean create) {
      boolean negated = this.negator.isNegated(thing);
      T myThing;
      if (negated) {
         myThing = (T)this.negator.unNegate(thing);
      } else {
         myThing = thing;
      }

      Integer intValue = this.mapToDimacs.get(myThing);
      if (intValue == null) {
         if (!create) {
            throw new IllegalArgumentException("" + myThing + " is unknown in the solver!");
         }

         intValue = this.solver.nextFreeVarId(true);
         this.mapToDomain.put(intValue, myThing);
         this.mapToDimacs.put(myThing, intValue);
      }

      return negated ? -intValue : intValue;
   }

   public IVec<T> getSolution() {
      int[] model = this.solver.model();
      IVec<T> toInstall = new Vec<>();
      if (model != null) {
         for (int i : model) {
            T obj = this.mapToDomain.get(i);
            if (obj != null) {
               toInstall.push(obj);
            }
         }
      }

      return toInstall;
   }

   public Collection<T> getASolution() {
      int[] model = this.solver.model();
      Collection<T> toInstall = new ArrayList<>();
      if (model != null) {
         for (int i : model) {
            if (i > 0) {
               toInstall.add(this.mapToDomain.get(i));
            }
         }
      }

      return toInstall;
   }

   public BigInteger getSolutionCost() {
      return this.objFunction.calculateDegree(this.solver);
   }

   public boolean getBooleanValueFor(T t) {
      int dimacsValue = this.getIntValue(t, false);
      return dimacsValue > 0 ? this.solver.model(dimacsValue) : !this.solver.model(-dimacsValue);
   }

   public boolean hasASolution() throws TimeoutException {
      return this.solver.isSatisfiable();
   }

   public boolean hasASolution(IVec<T> assumps) throws TimeoutException {
      IVecInt assumptions = new VecInt();
      Iterator<T> it = assumps.iterator();

      while (it.hasNext()) {
         assumptions.push(this.getIntValue(it.next()));
      }

      return this.solver.isSatisfiable(assumptions);
   }

   public boolean hasASolution(Collection<T> assumps) throws TimeoutException {
      IVecInt assumptions = new VecInt();

      for (T t : assumps) {
         assumptions.push(this.getIntValue(t));
      }

      return this.solver.isSatisfiable(assumptions);
   }

   public Set<C> why() throws TimeoutException {
      if (!this.explanationEnabled) {
         throw new UnsupportedOperationException("Explanation not enabled!");
      }

      Collection<IConstr> explanation = this.xplain.explain();
      Set<C> ezexplain = new TreeSet<>();

      for (IConstr constr : explanation) {
         C desc = this.descs.get(constr);
         if (desc != null) {
            ezexplain.add(desc);
         }
      }

      return ezexplain;
   }

   public Set<C> why(T thing) throws TimeoutException {
      IVecInt assumps = new VecInt();
      assumps.push(-this.getIntValue(thing));
      return this.why(assumps);
   }

   public Set<C> whyNot(T thing) throws TimeoutException {
      IVecInt assumps = new VecInt();
      assumps.push(this.getIntValue(thing));
      return this.why(assumps);
   }

   private Set<C> why(IVecInt assumps) throws TimeoutException {
      return this.xplain.isSatisfiable(assumps) ? new TreeSet<>() : this.why();
   }

   public void setTrue(T thing, C name) throws ContradictionException {
      IConstr constr = this.gator.gateTrue(this.getIntValue(thing));
      if (constr != null) {
         this.descs.put(constr, name);
      }
   }

   public void setFalse(T thing, C name) throws ContradictionException {
      IConstr constr = this.gator.gateFalse(this.getIntValue(thing));
      if (constr != null) {
         this.descs.put(constr, name);
      }
   }

   public ImplicationRHS<T, C> implication(T... lhs) {
      IVecInt clause = new VecInt();

      for (T t : lhs) {
         clause.push(-this.getIntValue(t));
      }

      return new ImplicationRHS<>(this, clause);
   }

   public DisjunctionRHS<T, C> disjunction(T... lhs) {
      IVecInt literals = new VecInt();

      for (T t : lhs) {
         literals.push(-this.getIntValue(t));
      }

      return new DisjunctionRHS<>(this, literals);
   }

   public void atLeast(C name, int i, T... things) throws ContradictionException {
      IVecInt literals = new VecInt();

      for (T t : things) {
         literals.push(this.getIntValue(t));
      }

      this.descs.put(this.solver.addAtLeast(literals, i), name);
   }

   public ImplicationNamer<T, C> atMost(int i, T... things) throws ContradictionException {
      IVec<IConstr> toName = new Vec<>();
      IVecInt literals = new VecInt();

      for (T t : things) {
         literals.push(this.getIntValue(t));
      }

      toName.push(this.solver.addAtMost(literals, i));
      return new ImplicationNamer<>(this, toName);
   }

   public void atMost(C name, int i, T... things) throws ContradictionException {
      IVecInt literals = new VecInt();

      for (T t : things) {
         literals.push(this.getIntValue(t));
      }

      this.descs.put(this.solver.addAtMost(literals, i), name);
   }

   public void clause(C name, T... things) throws ContradictionException {
      IVecInt literals = new VecInt(things.length);

      for (T t : things) {
         literals.push(this.getIntValue(t));
      }

      IConstr constr = this.gator.addClause(literals);
      if (constr != null) {
         this.descs.put(constr, name);
      }
   }

   public void iff(C name, T thing, T... otherThings) throws ContradictionException {
      IVecInt literals = new VecInt(otherThings.length);

      for (T t : otherThings) {
         literals.push(this.getIntValue(t));
      }

      IConstr[] constrs = this.gator.iff(this.getIntValue(thing), literals);

      for (IConstr constr : constrs) {
         if (constr != null) {
            this.descs.put(constr, name);
         }
      }
   }

   public void and(C name, T thing, T... otherThings) throws ContradictionException {
      IVecInt literals = new VecInt(otherThings.length);

      for (T t : otherThings) {
         literals.push(this.getIntValue(t));
      }

      IConstr[] constrs = this.gator.and(this.getIntValue(thing), literals);

      for (IConstr constr : constrs) {
         if (constr != null) {
            this.descs.put(constr, name);
         }
      }
   }

   public void or(C name, T thing, T... otherThings) throws ContradictionException {
      IVecInt literals = new VecInt(otherThings.length);

      for (T t : otherThings) {
         literals.push(this.getIntValue(t));
      }

      IConstr[] constrs = this.gator.or(this.getIntValue(thing), literals);

      for (IConstr constr : constrs) {
         if (constr != null) {
            this.descs.put(constr, name);
         }
      }
   }

   public void halfOr(C name, T thing, T... otherThings) throws ContradictionException {
      IVecInt literals = new VecInt(otherThings.length);

      for (T t : otherThings) {
         literals.push(this.getIntValue(t));
      }

      IConstr[] constrs = this.gator.halfOr(this.getIntValue(thing), literals);

      for (IConstr constr : constrs) {
         if (constr != null) {
            this.descs.put(constr, name);
         }
      }
   }

   public void ifThenElse(C name, T thing, T conditionThing, T thenThing, T elseThing) throws ContradictionException {
      IConstr[] constrs = this.gator.ite(this.getIntValue(thing), this.getIntValue(conditionThing), this.getIntValue(thenThing), this.getIntValue(elseThing));

      for (IConstr constr : constrs) {
         if (constr != null) {
            this.descs.put(constr, name);
         }
      }
   }

   public void setObjectiveFunction(WeightedObject<T>... wobj) {
      this.createObjectivetiveFunctionIfNeeded(wobj.length);

      for (WeightedObject<T> wo : wobj) {
         this.addProperly(wo.thing, wo.getWeight());
      }
   }

   private void addProperly(T thing, BigInteger weight) {
      int lit = this.getIntValue(thing);
      int index;
      if (this.canonicalOptFunction && (index = this.objLiterals.indexOf(lit)) != -1) {
         this.objCoefs.set(index, this.objCoefs.get(index).add(weight));
         if (this.objCoefs.get(index).equals(BigInteger.ZERO)) {
            this.objLiterals.delete(index);
            this.objCoefs.delete(index);
         }
      } else {
         this.objLiterals.push(lit);
         this.objCoefs.push(weight);
      }
   }

   private void createObjectivetiveFunctionIfNeeded(int n) {
      if (this.objFunction == null) {
         this.objLiterals = new VecInt(n);
         this.objCoefs = new Vec<>(n);
         this.objFunction = new ObjectiveFunction(this.objLiterals, this.objCoefs);
         this.solver.setObjectiveFunction(this.objFunction);
      }
   }

   public void addToObjectiveFunction(T thing, int weight) {
      this.addToObjectiveFunction(thing, BigInteger.valueOf(weight));
   }

   public void addToObjectiveFunction(T thing, BigInteger weight) {
      this.createObjectivetiveFunctionIfNeeded(20);
      this.addProperly(thing, weight);
   }

   public void atLeast(C name, BigInteger degree, WeightedObject<T>... wobj) throws ContradictionException {
      IVecInt literals = new VecInt(wobj.length);
      IVec<BigInteger> coeffs = new Vec<>(wobj.length);

      for (WeightedObject<T> wo : wobj) {
         literals.push(this.getIntValue(wo.thing));
         coeffs.push(wo.getWeight());
      }

      this.descs.put(this.solver.addPseudoBoolean(literals, coeffs, true, degree), name);
   }

   public void atMost(C name, BigInteger degree, WeightedObject<T>... wobj) throws ContradictionException {
      IVecInt literals = new VecInt(wobj.length);
      IVec<BigInteger> coeffs = new Vec<>(wobj.length);

      for (WeightedObject<T> wo : wobj) {
         literals.push(this.getIntValue(wo.thing));
         coeffs.push(wo.getWeight());
      }

      this.descs.put(this.solver.addPseudoBoolean(literals, coeffs, false, degree), name);
   }

   public void atMost(C name, int degree, WeightedObject<T>... wobj) throws ContradictionException {
      this.atMost(name, BigInteger.valueOf(degree), wobj);
   }

   public void stopSolver() {
      this.solver.expireTimeout();
   }

   public void stopExplanation() {
      if (!this.explanationEnabled) {
         throw new UnsupportedOperationException("Explanation not enabled!");
      }

      this.xplain.cancelExplanation();
   }

   public void discard(IVec<T> things) throws ContradictionException {
      IVecInt literals = new VecInt(things.size());
      Iterator<T> it = things.iterator();

      while (it.hasNext()) {
         literals.push(-this.getIntValue(it.next()));
      }

      this.solver.addBlockingClause(literals);
   }

   public void discardSolutionsWithObjectiveValueGreaterThan(long value) throws ContradictionException {
      ObjectiveFunction obj = this.solver.getObjectiveFunction();
      IVecInt literals = new VecInt(obj.getVars().size());
      obj.getVars().copyTo(literals);
      IVec<BigInteger> coeffs = new Vec<>(obj.getCoeffs().size());
      obj.getCoeffs().copyTo(coeffs);
      this.solver.addPseudoBoolean(literals, coeffs, false, BigInteger.valueOf(value));
   }

   public String getObjectiveFunction() {
      ObjectiveFunction obj = this.solver.getObjectiveFunction();
      StringBuilder stb = new StringBuilder();

      for (int i = 0; i < obj.getVars().size(); i++) {
         stb.append(obj.getCoeffs().get(i) + (obj.getVars().get(i) > 0 ? " " : "~") + this.mapToDomain.get(Math.abs(obj.getVars().get(i))) + " ");
      }

      return stb.toString();
   }

   public int getNumberOfVariables() {
      return this.mapToDimacs.size();
   }

   public int getNumberOfConstraints() {
      return this.descs.size();
   }

   public Map<Integer, T> getMappingToDomain() {
      return this.mapToDomain;
   }

   public Object not(T thing) {
      return new DependencyHelper.Negation(thing);
   }

   public IPBSolver getSolver() {
      return this.explanationEnabled ? this.xplain.decorated() : this.solver;
   }

   public void reset() {
      this.mapToDimacs.clear();
      this.mapToDomain.clear();
      this.descs.clear();
      this.solver.reset();
      if (this.objLiterals != null) {
         this.objLiterals.clear();
         this.objCoefs.clear();
      }
   }

   public void impliedBy(Collection<T> assumptions, Collection<T> satisfied, Collection<T> falsified) throws TimeoutException {
      IVecInt assump = new VecInt(assumptions.size());

      for (T thing : assumptions) {
         assump.push(this.getIntValue(thing));
      }

      IVecInt implied = Backbone.instance().compute(this.solver, assump);
      IteratorInt it = implied.iterator();

      while (it.hasNext()) {
         int p = it.next();
         if (p > 0) {
            satisfied.add(this.mapToDomain.get(p));
         } else {
            falsified.add(this.mapToDomain.get(-p));
         }
      }
   }

   private static final class Negation {
      private final Object thing;

      Negation(Object thing) {
         this.thing = thing;
      }

      Object getThing() {
         return this.thing;
      }

      @Override
      public String toString() {
         return "-" + this.thing;
      }
   }
}
