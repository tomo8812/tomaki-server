package reloc.org.sat4j.tools;

import java.math.BigInteger;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

@Feature("solver")
public class GateTranslator extends SolverDecorator<ISolver> {
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !GateTranslator.class.desiredAssertionStatus();

   public GateTranslator(ISolver solver) {
      super(solver);
   }

   public IConstr gateFalse(int y) throws ContradictionException {
      IVecInt clause = new VecInt(2);
      clause.push(-y);
      return this.processClause(clause);
   }

   public IConstr gateTrue(int y) throws ContradictionException {
      IVecInt clause = new VecInt(2);
      clause.push(y);
      return this.processClause(clause);
   }

   public IConstr[] ite(int y, int x1, int x2, int x3) throws ContradictionException {
      IConstr[] constrs = new IConstr[6];
      IVecInt clause = new VecInt(5);
      clause.push(-y).push(-x1).push(x2);
      constrs[0] = this.processClause(clause);
      clause.clear();
      clause.push(-y).push(x1).push(x3);
      constrs[1] = this.processClause(clause);
      clause.clear();
      clause.push(-x1).push(-x2).push(y);
      constrs[2] = this.processClause(clause);
      clause.clear();
      clause.push(x1).push(-x3).push(y);
      constrs[3] = this.processClause(clause);
      clause.clear();
      clause.push(-x2).push(-x3).push(y);
      constrs[4] = this.processClause(clause);
      clause.clear();
      clause.push(-y).push(x2).push(x3);
      constrs[5] = this.processClause(clause);
      return constrs;
   }

   public IConstr[] it(int y, int x1, int x2) throws ContradictionException {
      IConstr[] constrs = new IConstr[3];
      IVecInt clause = new VecInt(5);
      clause.push(-y).push(-x1).push(x2);
      constrs[0] = this.processClause(clause);
      clause.clear();
      clause.push(x1).push(y);
      constrs[1] = this.processClause(clause);
      clause.clear();
      clause.push(-x2).push(y);
      constrs[2] = this.processClause(clause);
      return constrs;
   }

   public IConstr[] and(int y, IVecInt literals) throws ContradictionException {
      IConstr[] constrs = new IConstr[literals.size() + 1];
      IVecInt clause = new VecInt(literals.size() + 2);
      clause.push(y);

      for (int i = 0; i < literals.size(); i++) {
         clause.push(-literals.get(i));
      }

      constrs[0] = this.processClause(clause);
      clause.clear();

      for (int i = 0; i < literals.size(); i++) {
         clause.clear();
         clause.push(-y);
         clause.push(literals.get(i));
         constrs[i + 1] = this.processClause(clause);
      }

      return constrs;
   }

   public IConstr[] and(int y, int x1, int x2) throws ContradictionException {
      IVecInt clause = new VecInt(4);
      IConstr[] constrs = new IConstr[3];
      clause.push(-y);
      clause.push(x1);
      constrs[0] = this.addClause(clause);
      clause.clear();
      clause.push(-y);
      clause.push(x2);
      constrs[1] = this.addClause(clause);
      clause.clear();
      clause.push(y);
      clause.push(-x1);
      clause.push(-x2);
      constrs[2] = this.addClause(clause);
      return constrs;
   }

   public IConstr[] or(int y, IVecInt literals) throws ContradictionException {
      IConstr[] constrs = new IConstr[literals.size() + 1];
      IVecInt clause = new VecInt(literals.size() + 2);
      literals.copyTo(clause);
      clause.push(-y);
      constrs[0] = this.processClause(clause);
      clause.clear();

      for (int i = 0; i < literals.size(); i++) {
         clause.clear();
         clause.push(y);
         clause.push(-literals.get(i));
         constrs[i + 1] = this.processClause(clause);
      }

      return constrs;
   }

   public IConstr[] halfOr(int y, IVecInt literals) throws ContradictionException {
      IConstr[] constrs = new IConstr[literals.size()];
      IVecInt clause = new VecInt(literals.size() + 2);

      for (int i = 0; i < literals.size(); i++) {
         clause.clear();
         clause.push(y);
         clause.push(-literals.get(i));
         constrs[i] = this.processClause(clause);
      }

      return constrs;
   }

   private IConstr processClause(IVecInt clause) throws ContradictionException {
      return this.addClause(clause);
   }

   public IConstr[] not(int y, int x) throws ContradictionException {
      IConstr[] constrs = new IConstr[2];
      IVecInt clause = new VecInt(3);
      clause.push(-y).push(-x);
      constrs[0] = this.processClause(clause);
      clause.clear();
      clause.push(y).push(x);
      constrs[1] = this.processClause(clause);
      return constrs;
   }

   public IConstr[] xor(int y, IVecInt literals) throws ContradictionException {
      literals.push(-y);
      int[] f = new int[literals.size()];
      literals.copyTo(f);
      IVec<IConstr> vconstrs = new Vec<>();
      this.xor2Clause(f, 0, false, vconstrs);
      IConstr[] constrs = new IConstr[vconstrs.size()];
      vconstrs.copyTo(constrs);
      return constrs;
   }

   public IConstr nativeXor(int y, IVecInt literals) {
      literals.push(-y);
      return this.addParity(literals, false);
   }

   public IConstr[] iff(int y, IVecInt literals) throws ContradictionException {
      literals.push(y);
      int[] f = new int[literals.size()];
      literals.copyTo(f);
      IVec<IConstr> vconstrs = new Vec<>();
      this.iff2Clause(f, 0, false, vconstrs);
      IConstr[] constrs = new IConstr[vconstrs.size()];
      vconstrs.copyTo(constrs);
      return constrs;
   }

   public void xor(int x, int a, int b) throws ContradictionException {
      IVecInt clause = new VecInt(3);
      clause.push(-a).push(b).push(x);
      this.processClause(clause);
      clause.clear();
      clause.push(a).push(-b).push(x);
      this.processClause(clause);
      clause.clear();
      clause.push(-a).push(-b).push(-x);
      this.processClause(clause);
      clause.clear();
      clause.push(a).push(b).push(-x);
      this.processClause(clause);
      clause.clear();
   }

   private void xor2Clause(int[] f, int prefix, boolean negation, IVec<IConstr> constrs) throws ContradictionException {
      if (prefix != f.length - 1) {
         if (negation) {
            f[prefix] = -f[prefix];
            this.xor2Clause(f, prefix + 1, false, constrs);
            f[prefix] = -f[prefix];
            this.xor2Clause(f, prefix + 1, true, constrs);
         } else {
            this.xor2Clause(f, prefix + 1, false, constrs);
            f[prefix] = -f[prefix];
            this.xor2Clause(f, prefix + 1, true, constrs);
            f[prefix] = -f[prefix];
         }
      } else {
         IVecInt clause = new VecInt(f.length + 1);

         for (int i = 0; i < f.length - 1; i++) {
            clause.push(f[i]);
         }

         clause.push(f[f.length - 1] * (negation ? -1 : 1));
         constrs.push(this.processClause(clause));
      }
   }

   private void iff2Clause(int[] f, int prefix, boolean negation, IVec<IConstr> constrs) throws ContradictionException {
      if (prefix != f.length - 1) {
         if (negation) {
            this.iff2Clause(f, prefix + 1, false, constrs);
            f[prefix] = -f[prefix];
            this.iff2Clause(f, prefix + 1, true, constrs);
            f[prefix] = -f[prefix];
         } else {
            f[prefix] = -f[prefix];
            this.iff2Clause(f, prefix + 1, false, constrs);
            f[prefix] = -f[prefix];
            this.iff2Clause(f, prefix + 1, true, constrs);
         }
      } else {
         IVecInt clause = new VecInt(f.length + 1);

         for (int i = 0; i < f.length - 1; i++) {
            clause.push(f[i]);
         }

         clause.push(f[f.length - 1] * (negation ? -1 : 1));
         this.processClause(clause);
      }
   }

   public void fullAdderSum(int x, int a, int b, int c) throws ContradictionException {
      IVecInt clause = new VecInt(4);
      clause.push(a).push(b).push(c).push(-x);
      this.processClause(clause);
      clause.clear();
      clause.push(a).push(-b).push(-c).push(-x);
      this.processClause(clause);
      clause.clear();
      clause.push(-a).push(b).push(-c).push(-x);
      this.processClause(clause);
      clause.clear();
      clause.push(-a).push(-b).push(c).push(-x);
      this.processClause(clause);
      clause.clear();
      clause.push(-a).push(-b).push(-c).push(x);
      this.processClause(clause);
      clause.clear();
      clause.push(-a).push(b).push(c).push(x);
      this.processClause(clause);
      clause.clear();
      clause.push(a).push(-b).push(c).push(x);
      this.processClause(clause);
      clause.clear();
      clause.push(a).push(b).push(-c).push(x);
      this.processClause(clause);
      clause.clear();
   }

   public void fullAdderCarry(int x, int a, int b, int c) throws ContradictionException {
      IVecInt clause = new VecInt(3);
      clause.push(-b).push(-c).push(x);
      this.processClause(clause);
      clause.clear();
      clause.push(-a).push(-c).push(x);
      this.processClause(clause);
      clause.clear();
      clause.push(-a).push(-b).push(x);
      this.processClause(clause);
      clause.clear();
      clause.push(b).push(c).push(-x);
      this.processClause(clause);
      clause.clear();
      clause.push(a).push(c).push(-x);
      this.processClause(clause);
      clause.clear();
      clause.push(a).push(b).push(-x);
      this.processClause(clause);
      clause.clear();
   }

   public void additionalFullAdderConstraints(int xcarry, int xsum, int a, int b, int c) throws ContradictionException {
      IVecInt clause = new VecInt(3);
      clause.push(-xcarry).push(-xsum).push(a);
      this.processClause(clause);
      clause.push(-xcarry).push(-xsum).push(b);
      this.processClause(clause);
      clause.push(-xcarry).push(-xsum).push(c);
      this.processClause(clause);
      clause.push(xcarry).push(xsum).push(-a);
      this.processClause(clause);
      clause.push(xcarry).push(xsum).push(-b);
      this.processClause(clause);
      clause.push(xcarry).push(xsum).push(-c);
      this.processClause(clause);
   }

   public void halfAdderSum(int x, int a, int b) throws ContradictionException {
      this.xor(x, a, b);
   }

   public void halfAdderCarry(int x, int a, int b) throws ContradictionException {
      this.and(x, a, b);
   }

   public void optimisationFunction(IVecInt literals, IVec<BigInteger> coefs, IVecInt result) throws ContradictionException {
      IVec<IVecInt> buckets = new Vec<>();

      for (int i = 0; i < literals.size(); i++) {
         int p = literals.get(i);
         BigInteger a = coefs.get(i);

         for (int j = 0; j < a.bitLength(); j++) {
            IVecInt bucket = this.createIfNull(buckets, j);
            if (a.testBit(j)) {
               bucket.push(p);
            }
         }
      }

      for (int i = 0; i < buckets.size(); i++) {
         IVecInt bucket = buckets.get(i);

         while (bucket.size() >= 3) {
            int x = bucket.get(0);
            int y = bucket.get(1);
            int z = bucket.get(2);
            bucket.remove(x);
            bucket.remove(y);
            bucket.remove(z);
            int sum = this.nextFreeVarId(true);
            int carry = this.nextFreeVarId(true);
            this.fullAdderSum(sum, x, y, z);
            this.fullAdderCarry(carry, x, y, z);
            this.additionalFullAdderConstraints(carry, sum, x, y, z);
            bucket.push(sum);
            this.createIfNull(buckets, i + 1).push(carry);
         }

         while (bucket.size() == 2) {
            int x = bucket.get(0);
            int y = bucket.get(1);
            bucket.remove(x);
            bucket.remove(y);
            int sum = this.nextFreeVarId(true);
            int carry = this.nextFreeVarId(true);
            this.halfAdderSum(sum, x, y);
            this.halfAdderCarry(carry, x, y);
            bucket.push(sum);
            this.createIfNull(buckets, i + 1).push(carry);
         }

         assert bucket.size() == 1;
         result.push(bucket.last());
         bucket.pop();
         assert bucket.isEmpty();
      }
   }

   private IVecInt createIfNull(IVec<IVecInt> buckets, int i) {
      IVecInt bucket = buckets.get(i);
      if (bucket == null) {
         bucket = new VecInt();
         buckets.push(bucket);
         assert buckets.get(i) == bucket;
      }

      return bucket;
   }
}
