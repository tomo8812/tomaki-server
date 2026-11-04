package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.tools.SolverDecorator;

public class PBAdapter extends SolverDecorator<ISolver> implements IPBSolver {
   private static final long serialVersionUID = 1L;

   public PBAdapter(ISolver solver) {
      super(solver);
   }

   @Override
   public IConstr addPseudoBoolean(IVecInt lits, IVec<BigInteger> coeffs, boolean moreThan, BigInteger d) throws ContradictionException {
      return moreThan ? this.addAtLeast(lits, coeffs, d) : this.addAtMost(lits, coeffs, d);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      BigInteger coeffsSum = BigInteger.ZERO;
      IVecInt newLiterals = new VecInt(literals.size());
      IteratorInt it = literals.iterator();

      while (it.hasNext()) {
         newLiterals.push(-it.next());
      }

      IVecInt newCoeffs = new VecInt(coeffs.size());
      IteratorInt itx = coeffs.iterator();

      while (itx.hasNext()) {
         int c = itx.next();
         newCoeffs.push(c);
         coeffsSum = coeffsSum.add(BigInteger.valueOf(c));
      }

      int newDegree = coeffsSum.intValue() - degree;
      return this.addAtLeast(newLiterals, newCoeffs, newDegree);
   }

   @Override
   public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      BigInteger coeffsSum = BigInteger.ZERO;
      IVecInt newLiterals = new VecInt(literals.size());
      IteratorInt it = literals.iterator();

      while (it.hasNext()) {
         newLiterals.push(-it.next());
      }

      IVec<BigInteger> newCoeffs = new Vec<>(coeffs.size());

      for (BigInteger c : coeffs) {
         newCoeffs.push(c);
         coeffsSum = coeffsSum.add(c);
      }

      BigInteger newDegree = coeffsSum.subtract(degree);
      return this.addAtLeast(newLiterals, newCoeffs, newDegree);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree) throws ContradictionException {
      this.assertConstraintIsCard(coeffs);
      Set<Integer> negLitsSet = new HashSet<>(literals.size());
      IteratorInt it = literals.iterator();

      while (it.hasNext()) {
         negLitsSet.add(-it.next());
      }

      int clausesDegree = literals.size() - degree + 1;
      ConstrGroup group = new ConstrGroup(false);

      for (Set<Integer> comb : new CombinationIterator(literals.size() - degree, negLitsSet)) {
         IteratorInt itx = literals.iterator();

         while (itx.hasNext()) {
            int lit = itx.next();
            if (!comb.contains(-lit)) {
               IVecInt clause = new VecInt(clausesDegree);
               clause.push(lit);

               for (Integer negLit : comb) {
                  clause.push(-negLit);
               }

               group.add(this.addClause(clause));
            }
         }
      }

      return group;
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs, BigInteger degree) throws ContradictionException {
      this.assertConstraintIsCard(coeffs);
      Set<Integer> negLitsSet = new HashSet<>(literals.size());
      IteratorInt it = literals.iterator();

      while (it.hasNext()) {
         negLitsSet.add(-it.next());
      }

      int clausesDegree = literals.size() - degree.intValue() + 1;
      ConstrGroup group = new ConstrGroup(false);

      for (Set<Integer> comb : new CombinationIterator(literals.size() - degree.intValue(), negLitsSet)) {
         IteratorInt itx = literals.iterator();

         while (itx.hasNext()) {
            int lit = itx.next();
            if (!comb.contains(-lit)) {
               IVecInt clause = new VecInt(clausesDegree);
               clause.push(lit);

               for (Integer negLit : comb) {
                  clause.push(-negLit);
               }

               group.add(this.addClause(clause));
            }
         }
      }

      return group;
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      group.add(this.addAtLeast(literals, coeffs, weight));
      group.add(this.addAtMost(literals, coeffs, weight));
      return group;
   }

   @Override
   public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs, BigInteger weight) throws ContradictionException {
      ConstrGroup group = new ConstrGroup(false);
      group.add(this.addAtLeast(literals, coeffs, weight));
      group.add(this.addAtMost(literals, coeffs, weight));
      return group;
   }

   @Override
   public void setObjectiveFunction(ObjectiveFunction obj) {
      if (obj != null) {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public ObjectiveFunction getObjectiveFunction() {
      return null;
   }

   private void assertConstraintIsCard(IVecInt weights) {
      IteratorInt it = weights.iterator();

      while (it.hasNext()) {
         if (it.next() != 1) {
            throw new UnsupportedOperationException();
         }
      }
   }

   private void assertConstraintIsCard(IVec<BigInteger> weights) {
      Iterator<BigInteger> it = weights.iterator();

      while (it.hasNext()) {
         if (it.next().compareTo(BigInteger.ONE) != 0) {
            throw new UnsupportedOperationException();
         }
      }
   }
}
