package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.constraints.cnf.Lits;
import reloc.org.sat4j.minisat.core.VarActivityListener;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public class MapPb implements IDataStructurePB {
   protected InternalMapPBStructure weightedLits;
   protected BigInteger degree;
   protected int assertiveLiteral = -1;
   private int cpCardsReduction = 0;
   private BigInteger cardDegree;
   protected final AutoDivisionStrategy autoDivisionStrategy;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MapPb.class.desiredAssertionStatus();

   MapPb(PBConstr cpb, int level, boolean noRemove, AutoDivisionStrategy autoDivisionStrategy) {
      this.weightedLits = new InternalMapPBStructure(cpb, level, noRemove);
      this.degree = this.weightedLits.getComputedDegree();
      this.autoDivisionStrategy = autoDivisionStrategy;
   }

   MapPb(int size) {
      this.weightedLits = new InternalMapPBStructure(size);
      this.degree = BigInteger.ZERO;
      this.autoDivisionStrategy = AutoDivisionStrategy.ENABLED;
   }

   public MapPb(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
      this.weightedLits = new InternalMapPBStructure(literals, coefs);
      this.degree = degree;
      this.autoDivisionStrategy = AutoDivisionStrategy.ENABLED;
   }

   @Override
   public int reduceCoeffsByPower2() {
      if (this.weightedLits.size() <= 0) {
         return 0;
      }

      int nbBits = this.weightedLits.getCoef(0).bitLength();

      for (int i = 0; i < this.weightedLits.size() && nbBits > 0; i++) {
         nbBits = Math.min(nbBits, this.weightedLits.getCoef(i).getLowestSetBit());
      }

      if (nbBits > 0) {
         for (int i = 0; i < this.weightedLits.size(); i++) {
            this.changeCoef(i, this.weightedLits.getCoef(i).shiftRight(nbBits));
         }

         int nbBitsDegree = this.degree.getLowestSetBit();
         this.degree = this.degree.shiftRight(nbBits);
         if (nbBitsDegree < nbBits) {
            this.degree = this.degree.add(BigInteger.ONE);
         }
      }

      return nbBits;
   }

   public int reduceCoeffsByGCD() {
      if (this.weightedLits.size() <= 0) {
         return 1;
      }

      BigInteger gcd = this.weightedLits.getCoef(0);

      for (int i = 0; i < this.weightedLits.size() && gcd.compareTo(BigInteger.ONE) > 0; i++) {
         gcd = gcd.gcd(this.weightedLits.getCoef(i));
      }

      if (gcd.compareTo(BigInteger.ONE) > 0) {
         for (int i = 0; i < this.weightedLits.size(); i++) {
            this.changeCoef(i, this.weightedLits.getCoef(i).divide(gcd));
         }

         BigInteger[] result = this.degree.divideAndRemainder(gcd);
         this.degree = result[0];
         if (result[1].compareTo(BigInteger.ZERO) > 0) {
            this.degree = this.degree.add(BigInteger.ONE);
         }
      }

      return gcd.intValue();
   }

   @Override
   public boolean isCardinality() {
      boolean newcase = false;

      for (int i = 0; i < this.size(); i++) {
         if (!this.weightedLits.getCoef(i).equals(BigInteger.ONE) && !this.weightedLits.getCoef(i).equals(BigInteger.ZERO)) {
            newcase = true;
            break;
         }
      }

      if (newcase) {
         if (!this.autoDivisionStrategy.isCardinality(this.weightedLits)) {
            return false;
         }

         this.cpCardsReduction++;
         BigInteger[] division = this.degree.divideAndRemainder(this.weightedLits.getCoef(0));
         if (!division[1].equals(BigInteger.ZERO)) {
            division[0] = division[0].add(BigInteger.ONE);
         }

         this.cardDegree = division[0];
      } else {
         this.cardDegree = this.degree;
      }

      return true;
   }

   public int getNumberOfCuttingPlanesCardinalities() {
      return this.cpCardsReduction;
   }

   @Override
   public boolean isLongSufficient() {
      BigInteger som = BigInteger.ZERO;

      for (int i = 0; i < this.size() && som.bitLength() < 64; i++) {
         assert this.weightedLits.getCoef(i).compareTo(BigInteger.ZERO) >= 0;
         som = som.add(this.weightedLits.getCoef(i));
      }

      return som.bitLength() < 64;
   }

   @Override
   public int getAssertiveLiteral() {
      return this.assertiveLiteral;
   }

   @Override
   public BigInteger saturation() {
      assert this.degree.signum() > 0;
      BigInteger minimum = this.degree;

      for (int ind = 0; ind < this.size(); ind++) {
         assert this.weightedLits.getCoef(ind).signum() >= 0;
         if (this.degree.compareTo(this.weightedLits.getCoef(ind)) < 0) {
            this.changeCoef(ind, this.degree);
         }

         assert this.weightedLits.getCoef(ind).signum() >= 0;
         if (this.weightedLits.getCoef(ind).signum() > 0) {
            minimum = minimum.min(this.weightedLits.getCoef(ind));
         }
      }

      if (minimum.equals(this.degree) && minimum.compareTo(BigInteger.ONE) > 0) {
         this.degree = BigInteger.ONE;

         for (int ind = 0; ind < this.size(); ind++) {
            this.changeCoef(ind, BigInteger.ONE);
         }
      }

      return this.degree;
   }

   @Override
   public BigInteger cuttingPlane(PBConstr cpb, BigInteger deg, BigInteger[] reducedCoefs, VarActivityListener val, int p) {
      return this.cuttingPlane(cpb, deg, reducedCoefs, BigInteger.ONE, val, p);
   }

   @Override
   public BigInteger cuttingPlane(PBConstr cpb, BigInteger degreeCons, BigInteger[] reducedCoefs, BigInteger coefMult, VarActivityListener val, int p) {
      this.degree = this.degree.add(degreeCons);
      assert this.degree.signum() > 0;
      if (reducedCoefs == null) {
         for (int i = 0; i < cpb.size(); i++) {
            val.varBumpActivity(cpb, i, p);
            this.cuttingPlaneStep(cpb.get(i), this.multiplyCoefficient(cpb.getCoef(i), coefMult));
         }
      } else {
         for (int i = 0; i < cpb.size(); i++) {
            val.varBumpActivity(cpb, i, p);
            this.cuttingPlaneStep(cpb.get(i), this.multiplyCoefficient(reducedCoefs[i], coefMult));
         }
      }

      val.postBumpActivity(cpb);
      return this.degree;
   }

   @Override
   public BigInteger cuttingPlane(int[] lits, BigInteger[] reducedCoefs, BigInteger deg) {
      return this.cuttingPlane(lits, reducedCoefs, deg, BigInteger.ONE);
   }

   @Override
   public BigInteger cuttingPlane(int[] lits, BigInteger[] reducedCoefs, BigInteger degreeCons, BigInteger coefMult) {
      this.degree = this.degree.add(degreeCons);
      assert this.degree.signum() > 0;

      for (int i = 0; i < lits.length; i++) {
         this.cuttingPlaneStep(lits[i], reducedCoefs[i].multiply(coefMult));
      }

      return this.degree;
   }

   private void cuttingPlaneStep(int lit, BigInteger coef) {
      assert coef.signum() >= 0;
      int nlit = lit ^ 1;
      if (coef.signum() > 0) {
         if (this.weightedLits.containsKey(nlit)) {
            assert !this.weightedLits.containsKey(lit);
            assert this.weightedLits.get(nlit) != null;
            if (this.weightedLits.get(nlit).compareTo(coef) < 0) {
               BigInteger tmp = this.weightedLits.get(nlit);
               this.setCoef(lit, coef.subtract(tmp));
               assert this.weightedLits.get(lit).signum() > 0;
               this.degree = this.degree.subtract(tmp);
               this.removeCoef(nlit);
            } else if (this.weightedLits.get(nlit).equals(coef)) {
               this.degree = this.degree.subtract(coef);
               this.removeCoef(nlit);
            } else {
               this.decreaseCoef(nlit, coef);
               assert this.weightedLits.get(nlit).signum() > 0;
               this.degree = this.degree.subtract(coef);
            }
         } else {
            assert !this.weightedLits.containsKey(lit) || this.weightedLits.get(lit).signum() > 0;
            if (this.weightedLits.containsKey(lit)) {
               this.increaseCoef(lit, coef);
            } else {
               this.setCoef(lit, coef);
            }

            assert this.weightedLits.get(lit).signum() > 0;
         }
      }

      assert !this.weightedLits.containsKey(nlit) || !this.weightedLits.containsKey(lit);
   }

   @Override
   public void buildConstraintFromConflict(IVecInt resLits, IVec<BigInteger> resCoefs) {
      resLits.clear();
      resCoefs.clear();

      for (int i = 0; i < this.weightedLits.size(); i++) {
         if (!this.weightedLits.getCoef(i).equals(BigInteger.ZERO)) {
            resLits.push(this.weightedLits.getLit(i));
            resCoefs.push(this.weightedLits.getCoef(i));
         }
      }
   }

   @Override
   public void buildConstraintFromMapPb(int[] resLits, BigInteger[] resCoefs) {
      assert resLits.length == resCoefs.length;
      assert resLits.length == this.size();
      this.weightedLits.copyCoefs(resCoefs);
      this.weightedLits.copyLits(resLits);
   }

   @Override
   public BigInteger getDegree() {
      return this.degree;
   }

   @Override
   public int size() {
      return this.weightedLits.size();
   }

   @Override
   public String toString() {
      StringBuilder stb = new StringBuilder();

      for (int ind = 0; ind < this.size(); ind++) {
         stb.append(this.weightedLits.getCoef(ind));
         stb.append(".");
         stb.append(Lits.toString(this.weightedLits.getLit(ind)));
         stb.append(" ");
      }

      return stb.toString() + " >= " + this.degree;
   }

   private BigInteger multiplyCoefficient(BigInteger coef, BigInteger mult) {
      return coef.equals(BigInteger.ONE) ? mult : coef.multiply(mult);
   }

   void increaseCoef(int lit, BigInteger incCoef) {
      this.weightedLits.put(lit, this.weightedLits.get(lit).add(incCoef));
   }

   void decreaseCoef(int lit, BigInteger decCoef) {
      this.weightedLits.put(lit, this.weightedLits.get(lit).subtract(decCoef));
   }

   void setCoef(int lit, BigInteger newValue) {
      this.weightedLits.put(lit, newValue);
   }

   void changeCoef(int indLit, BigInteger newValue) {
      this.weightedLits.changeCoef(indLit, newValue);
   }

   void removeCoef(int lit) {
      this.weightedLits.remove(lit);
   }

   @Override
   public BigInteger getCardDegree() {
      return this.cardDegree;
   }
}
