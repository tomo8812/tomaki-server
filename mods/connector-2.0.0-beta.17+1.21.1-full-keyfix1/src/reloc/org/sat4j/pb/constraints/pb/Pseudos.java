package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.IVecInt;

public abstract class Pseudos {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !Pseudos.class.desiredAssertionStatus();

   public static IDataStructurePB niceCheckedParameters(IVecInt ps, IVec<BigInteger> bigCoefs, boolean moreThan, BigInteger bigDeg, ILits voc) {
      assert ps.size() != 0 && ps.size() == bigCoefs.size();
      int[] lits = new int[ps.size()];
      ps.copyTo(lits);
      BigInteger[] bc = new BigInteger[bigCoefs.size()];
      bigCoefs.copyTo(bc);
      BigInteger bigDegree = niceCheckedParametersForCompetition(lits, bc, moreThan, bigDeg);
      IDataStructurePB mpb = new MapPb(voc.nVars() * 2 + 2);
      if (bigDegree.signum() > 0) {
         bigDegree = mpb.cuttingPlane(lits, bc, bigDegree);
      }

      if (bigDegree.signum() > 0) {
         bigDegree = mpb.saturation();
      }

      return bigDegree.signum() <= 0 ? IDataStructurePB.TAUTOLOGY : mpb;
   }

   public static BigInteger niceCheckedParametersForCompetition(int[] lits, BigInteger[] bc, boolean moreThan, BigInteger bigDeg) {
      BigInteger bigDegree = bigDeg;
      if (!moreThan) {
         for (int i = 0; i < lits.length; i++) {
            bc[i] = bc[i].negate();
         }

         bigDegree = bigDegree.negate();
      }

      for (int i = 0; i < bc.length; i++) {
         if (bc[i].signum() < 0) {
            lits[i] ^= 1;
            bc[i] = bc[i].negate();
            bigDegree = bigDegree.add(bc[i]);
         }
      }

      for (int i = 0; i < bc.length; i++) {
         if (bc[i].compareTo(bigDegree) > 0) {
            bc[i] = bigDegree;
         }
      }

      return bigDegree;
   }

   public static IDataStructurePB niceParameters(IVecInt ps, IVec<BigInteger> bigCoefs, boolean moreThan, BigInteger bigDeg, ILits voc) throws ContradictionException {
      if (ps.size() != 0) {
         if (ps.size() != bigCoefs.size()) {
            throw new IllegalArgumentException("Contradiction dans la taille des tableaux ps=" + ps.size() + " coefs=" + bigCoefs.size() + ".");
         } else {
            return niceCheckedParameters(ps, bigCoefs, moreThan, bigDeg, voc);
         }
      } else if ((!moreThan || bigDeg.signum() <= 0) && (moreThan || bigDeg.signum() >= 0)) {
         return IDataStructurePB.TAUTOLOGY;
      } else {
         throw new ContradictionException("Creating Empty clause ?");
      }
   }

   public static BigInteger niceParametersForCompetition(int[] ps, BigInteger[] bigCoefs, boolean moreThan, BigInteger bigDeg) throws ContradictionException {
      if (ps.length != 0) {
         if (ps.length != bigCoefs.length) {
            throw new IllegalArgumentException("Contradiction dans la taille des tableaux ps=" + ps.length + " coefs=" + bigCoefs.length + ".");
         } else {
            return niceCheckedParametersForCompetition(ps, bigCoefs, moreThan, bigDeg);
         }
      } else if ((!moreThan || bigDeg.signum() <= 0) && (moreThan || bigDeg.signum() >= 0)) {
         return bigDeg;
      } else {
         throw new ContradictionException("Creating Empty clause ?");
      }
   }

   public static IVec<BigInteger> toVecBigInt(IVecInt vec) {
      IVec<BigInteger> bigVec = new Vec<>(vec.size());

      for (int i = 0; i < vec.size(); i++) {
         bigVec.push(BigInteger.valueOf(vec.get(i)));
      }

      return bigVec;
   }

   public static BigInteger toBigInt(int i) {
      return BigInteger.valueOf(i);
   }

   public static ObjectiveFunction normalizeObjective(ObjectiveFunction initial) {
      IVec<BigInteger> initCoeffs = initial.getCoeffs();
      IVecInt initLits = initial.getVars();
      assert initCoeffs.size() == initLits.size();
      Map<Integer, BigInteger> reduced = new HashMap<>();

      for (int i = 0; i < initLits.size(); i++) {
         int lit = initLits.get(i);
         BigInteger oldCoef = reduced.get(lit);
         if (oldCoef == null) {
            reduced.put(lit, initCoeffs.get(i));
         } else {
            reduced.put(lit, oldCoef.add(initCoeffs.get(i)));
         }
      }

      assert reduced.size() <= initLits.size();
      if (reduced.size() >= initLits.size()) {
         return initial;
      }

      IVecInt newLits = new VecInt(reduced.size());
      IVec<BigInteger> newCoefs = new Vec<>(reduced.size());

      for (Entry<Integer, BigInteger> entry : reduced.entrySet()) {
         newLits.push(entry.getKey());
         newCoefs.push(entry.getValue());
      }

      return new ObjectiveFunction(newLits, newCoefs);
   }
}
