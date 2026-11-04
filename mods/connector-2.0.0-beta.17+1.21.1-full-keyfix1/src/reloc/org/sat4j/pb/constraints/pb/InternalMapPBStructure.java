package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class InternalMapPBStructure {
   private final IVecInt lits;
   private final IVec<BigInteger> coefs;
   private IVecInt allLits;
   protected BigInteger degree;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !InternalMapPBStructure.class.desiredAssertionStatus();

   InternalMapPBStructure(IVecInt lits, IVec<BigInteger> coefs) {
      this.lits = lits;
      this.coefs = coefs;
   }

   InternalMapPBStructure(int size) {
      assert size > 0;
      this.allLits = new VecInt(size, -1);
      this.coefs = new Vec<>();
      this.lits = new VecInt();
   }

   InternalMapPBStructure(PBConstr cpb, int level, boolean noRemove) {
      ILits voc = cpb.getVocabulary();
      this.allLits = new VecInt(cpb.getVocabulary().nVars() * 2 + 2, -1);
      this.coefs = new Vec<>(cpb.size());
      this.lits = new VecInt(cpb.size());
      int ind = 0;
      BigInteger degree = cpb.getDegree();
      boolean clause = degree.equals(BigInteger.ONE);

      for (int i = 0; i < cpb.size(); i++) {
         assert cpb.get(i) != 0;
         assert cpb.getCoef(i).signum() > 0;
         int lit = cpb.get(i);
         if (!noRemove && !clause && voc.isSatisfied(lit) && voc.getLevel(lit) < level) {
            BigInteger coef = cpb.getCoef(i);
            degree = degree.subtract(coef);
         } else {
            this.lits.push(lit);
            assert ind + 1 == this.lits.size();
            this.allLits.set(lit, ind);
            this.coefs.push(cpb.getCoef(i));
            ind++;
         }
      }

      this.degree = degree;
   }

   public BigInteger getComputedDegree() {
      return this.degree;
   }

   BigInteger get(int lit) {
      assert this.allLits.get(lit) != -1;
      return this.coefs.get(this.allLits.get(lit));
   }

   int getFromAllLits(int lit) {
      return this.allLits.get(lit);
   }

   int getLit(int indLit) {
      assert indLit < this.lits.size();
      return this.lits.get(indLit);
   }

   BigInteger getCoef(int indLit) {
      assert indLit < this.coefs.size();
      return this.coefs.get(indLit);
   }

   boolean containsKey(int lit) {
      return this.allLits.get(lit) != -1;
   }

   int size() {
      return this.lits.size();
   }

   boolean delete(int indLit) {
      this.allLits.set(this.lits.get(indLit), -1);
      this.lits.delete(indLit);
      this.coefs.delete(indLit);
      return true;
   }

   void put(int lit, BigInteger newValue) {
      int indLit = this.allLits.get(lit);
      if (indLit != -1) {
         this.coefs.set(indLit, newValue);
      } else {
         this.lits.push(lit);
         this.coefs.push(newValue);
         this.allLits.set(lit, this.lits.size() - 1);
      }
   }

   void changeCoef(int indLit, BigInteger newValue) {
      assert indLit <= this.coefs.size();
      this.coefs.set(indLit, newValue);
   }

   void remove(int lit) {
      int indLit = this.allLits.get(lit);
      if (indLit != -1) {
         int tmp = this.lits.last();
         this.coefs.delete(indLit);
         this.lits.delete(indLit);
         this.allLits.set(tmp, indLit);
         this.allLits.set(lit, -1);
      }
   }

   void copyCoefs(IVec<BigInteger> dest) {
      this.coefs.copyTo(dest);
   }

   void copyCoefs(BigInteger[] dest) {
      this.coefs.copyTo(dest);
   }

   void copyLits(IVecInt dest) {
      this.lits.copyTo(dest);
   }

   void copyLits(int[] dest) {
      this.lits.copyTo(dest);
   }
}
