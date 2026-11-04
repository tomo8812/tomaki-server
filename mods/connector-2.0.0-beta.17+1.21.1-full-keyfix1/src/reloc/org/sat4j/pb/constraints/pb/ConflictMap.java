package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.constraints.cnf.Lits;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.VarActivityListener;
import reloc.org.sat4j.pb.core.PBSolverStats;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class ConflictMap extends MapPb implements IConflict {
   final ILits voc;
   private static final int NOTCOMPUTED = -2;
   protected boolean hasBeenReduced = false;
   protected long numberOfReductions = 0L;
   private SkipStrategy skip = SkipStrategy.NO_SKIP;
   private boolean endingSkipping = true;
   protected BigInteger currentSlack;
   protected BigInteger sumAllCoefs;
   protected int currentLevel;
   int backtrackLevel = -2;
   final PBSolverStats stats;
   protected IPreProcess preProcess = NoPreProcess.instance();
   protected VecInt[] byLevel;
   private final IRemoveSatisfiedLiterals rmSatLit;
   private final IPostProcess postProcess;
   private final IWeakeningStrategy weakeningStrategy;
   protected BigInteger coefMult = BigInteger.ZERO;
   protected BigInteger coefMultCons = BigInteger.ZERO;
   BigInteger possReducedCoefs = BigInteger.ZERO;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ConflictMap.class.desiredAssertionStatus();

   public static IConflict createConflict(
      PBConstr cpb,
      int level,
      boolean noRemove,
      SkipStrategy skip,
      IPreProcess preProcessing,
      IPostProcess postProcessing,
      IWeakeningStrategy weakeningStrategy,
      AutoDivisionStrategy autoDivisionStrategy,
      PBSolverStats stats
   ) {
      return new ConflictMap(cpb, level, noRemove, skip, preProcessing, postProcessing, weakeningStrategy, autoDivisionStrategy, stats);
   }

   public static IConflictFactory factory() {
      return new IConflictFactory() {
         @Override
         public IConflict createConflict(
            PBConstr cpb,
            int level,
            boolean noRemove,
            SkipStrategy skip,
            IPreProcess preprocess,
            IPostProcess postprocess,
            IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy,
            PBSolverStats stats
         ) {
            return ConflictMap.createConflict(cpb, level, noRemove, skip, preprocess, postprocess, weakeningStrategy, autoDivisionStrategy, stats);
         }

         @Override
         public String toString() {
            return "Use constraints as they come during conflict analysis";
         }
      };
   }

   ConflictMap(PBConstr cpb, int level) {
      this(
         cpb,
         level,
         false,
         SkipStrategy.NO_SKIP,
         NoPreProcess.instance(),
         NoPostProcess.instance(),
         IWeakeningStrategy.UNASSIGNED_FIRST,
         AutoDivisionStrategy.ENABLED,
         null
      );
   }

   ConflictMap(PBConstr cpb, int level, boolean noRemove) {
      this(
         cpb,
         level,
         noRemove,
         SkipStrategy.NO_SKIP,
         NoPreProcess.instance(),
         NoPostProcess.instance(),
         IWeakeningStrategy.UNASSIGNED_FIRST,
         AutoDivisionStrategy.ENABLED,
         null
      );
   }

   ConflictMap(PBConstr cpb, int level, boolean noRemove, SkipStrategy skip, PBSolverStats stats) {
      this(
         cpb,
         level,
         noRemove,
         skip,
         NoPreProcess.instance(),
         NoPostProcess.instance(),
         IWeakeningStrategy.UNASSIGNED_FIRST,
         AutoDivisionStrategy.ENABLED,
         stats
      );
   }

   ConflictMap(
      PBConstr cpb,
      int level,
      boolean noRemove,
      SkipStrategy skip,
      IPreProcess preProcessing,
      IPostProcess postProcessing,
      IWeakeningStrategy weakeningStrategy,
      AutoDivisionStrategy autoDivisionStrategy,
      PBSolverStats stats
   ) {
      super(cpb, level, noRemove, autoDivisionStrategy);
      this.stats = stats;
      this.skip = skip;
      this.voc = cpb.getVocabulary();
      this.currentLevel = level;
      this.preProcess = preProcessing;
      this.initStructures();
      this.postProcess = postProcessing;
      this.weakeningStrategy = weakeningStrategy;
      if (noRemove) {
         this.rmSatLit = new ConflictMap.NoRemoveSatisfied();
      } else {
         this.rmSatLit = new ConflictMap.RemoveSatisfied();
      }
   }

   private void initStructures() {
      this.currentSlack = BigInteger.ZERO;
      this.sumAllCoefs = BigInteger.ZERO;
      this.byLevel = new VecInt[levelToIndex(this.currentLevel) + 1];

      for (int i = 0; i < this.size(); i++) {
         int ilit = this.weightedLits.getLit(i);
         int litLevel = this.voc.getLevel(ilit);
         BigInteger tmp = this.weightedLits.getCoef(i);
         this.sumAllCoefs = this.sumAllCoefs.add(tmp);
         if (tmp.signum() > 0 && (!this.voc.isFalsified(ilit) || litLevel == this.currentLevel)) {
            this.currentSlack = this.currentSlack.add(tmp);
         }

         int index = levelToIndex(litLevel);
         if (this.byLevel[index] == null) {
            this.byLevel[index] = new VecInt();
         }

         this.byLevel[index].push(ilit);
      }
   }

   static int levelToIndex(int level) {
      return level + 1;
   }

   static int indexToLevel(int indLevel) {
      return indLevel - 1;
   }

   @Override
   public void postProcess(int dl) {
      this.postProcess.postProcess(dl, this);
   }

   @Override
   public BigInteger resolve(PBConstr cpb, int litImplied, VarActivityListener val) {
      assert litImplied > 1;
      this.preProcess();
      int nLitImplied = litImplied ^ 1;
      if (cpb == null || !this.weightedLits.containsKey(nLitImplied)) {
         int litLevel = levelToIndex(this.voc.getLevel(litImplied));
         int lit = 0;
         if (this.byLevel[litLevel] != null) {
            if (this.byLevel[litLevel].contains(litImplied)) {
               lit = litImplied;
               assert this.weightedLits.containsKey(litImplied);
            } else if (this.byLevel[litLevel].contains(nLitImplied)) {
               lit = nLitImplied;
               assert this.weightedLits.containsKey(nLitImplied);
            }
         }

         if (lit > 0) {
            this.byLevel[litLevel].remove(lit);
            if (this.byLevel[0] == null) {
               this.byLevel[0] = new VecInt();
            }

            this.byLevel[0].push(lit);
         }

         return this.degree;
      } else if (this.skip.skip(this, litImplied)) {
         if (this.endingSkipping) {
            this.stats.incNumberOfEndingSkipping();
         } else {
            this.stats.incNumberOfInternalSkipping();
         }

         assert this.slackConflict().signum() < 0;
         return this.degree;
      } else {
         this.endingSkipping = false;
         this.stats.incNumberOfDerivationSteps();
         assert this.slackConflict().signum() < 0;
         assert this.degree.signum() >= 0;
         BigInteger[] coefsCons = null;
         BigInteger degreeCons = cpb.getDegree();
         int ind = 0;

         while (cpb.get(ind) != litImplied) {
            ind++;
         }

         assert cpb.get(ind) == litImplied;
         assert cpb.getCoef(ind) != BigInteger.ZERO;
         if (cpb.getCoef(ind).equals(BigInteger.ONE)) {
            this.coefMultCons = this.weightedLits.get(nLitImplied);
            this.coefMult = BigInteger.ONE;
            degreeCons = degreeCons.multiply(this.coefMultCons);
         } else {
            IWatchPb wpb = (IWatchPb)cpb;
            coefsCons = wpb.getCoefs();
            degreeCons = this.rmSatLit.removeSatisfiedLiteralsFromHigherDecisionLevels(wpb, coefsCons, this.currentLevel, degreeCons);
            if (this.weightedLits.get(nLitImplied).equals(BigInteger.ONE)) {
               this.coefMult = coefsCons[ind];
               this.coefMultCons = BigInteger.ONE;
               this.degree = this.degree.multiply(this.coefMult);
            } else if (coefsCons[ind].equals(BigInteger.ONE)) {
               this.coefMultCons = this.weightedLits.get(nLitImplied);
               this.coefMult = BigInteger.ONE;
               degreeCons = degreeCons.multiply(this.coefMultCons);
            } else {
               assert positiveCoefs(coefsCons);
               degreeCons = this.reduceUntilConflict(litImplied, ind, coefsCons, degreeCons, wpb);
               degreeCons = degreeCons.multiply(this.coefMultCons);
               this.degree = this.degree.multiply(this.coefMult);

               for (int i = 0; i < cpb.size(); i++) {
                  if (coefsCons[i].signum() != 0) {
                     if (this.voc.isUnassigned(cpb.get(i))) {
                        this.stats.incNumberOfRemainingUnassigned();
                     } else {
                        this.stats.incNumberOfRemainingAssigned();
                     }
                  }
               }
            }

            long before = System.nanoTime();
            if (!this.coefMult.equals(BigInteger.ONE)) {
               for (int i = 0; i < this.size(); i++) {
                  this.changeCoef(i, this.weightedLits.getCoef(i).multiply(this.coefMult));
               }
            }

            long after = System.nanoTime();
            this.stats.incTimeForArithmeticOperations(after - before);
         }

         assert this.slackConflict().signum() < 0;
         this.degree = this.cuttingPlane(cpb, degreeCons, coefsCons, this.coefMultCons, val, ind);
         assert !this.weightedLits.containsKey(litImplied);
         assert !this.weightedLits.containsKey(nLitImplied);
         assert this.getLevelByLevel(litImplied) == -1;
         assert this.getLevelByLevel(nLitImplied) == -1;
         assert this.degree.signum() > 0;
         assert this.slackConflict().signum() < 0;
         this.degree = this.saturation();
         assert this.slackConflict().signum() < 0;
         this.divideCoefs();
         return this.degree;
      }
   }

   public void preProcess() {
      this.preProcess.preProcess(this.currentLevel, this);
   }

   void divideCoefs() {
   }

   protected BigInteger reduceUntilConflict(int litImplied, int ind, BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
      BigInteger slackResolve = BigInteger.ONE.negate();
      BigInteger slackThis = BigInteger.ZERO;
      BigInteger slackConflict = this.slackConflict();
      BigInteger reducedDegree = degreeReduced;
      BigInteger previousCoefLitImplied = BigInteger.ZERO;
      BigInteger coefLitImplied = this.weightedLits.get(litImplied ^ 1);
      this.possReducedCoefs = this.possConstraint(wpb, reducedCoefs);

      do {
         if (slackResolve.signum() >= 0) {
            assert slackThis.signum() > 0;
            BigInteger tmp = this.reduceInConstraint(wpb, reducedCoefs, ind, reducedDegree, slackResolve);
            assert tmp.compareTo(reducedDegree) < 0 && tmp.compareTo(BigInteger.ONE) >= 0;
            reducedDegree = tmp;
         }

         assert this.weightedLits.get(litImplied ^ 1).signum() > 0;
         assert reducedCoefs[ind].signum() > 0;
         if (!reducedCoefs[ind].equals(previousCoefLitImplied)) {
            assert coefLitImplied.equals(this.weightedLits.get(litImplied ^ 1));
            BigInteger ppcm = this.ppcm(reducedCoefs[ind], coefLitImplied);
            assert ppcm.signum() > 0;
            this.coefMult = ppcm.divide(coefLitImplied);
            this.coefMultCons = ppcm.divide(reducedCoefs[ind]);
            assert this.coefMultCons.signum() > 0;
            assert this.coefMult.signum() > 0;
            assert this.coefMult.multiply(coefLitImplied).equals(this.coefMultCons.multiply(reducedCoefs[ind]));
            previousCoefLitImplied = reducedCoefs[ind];
         }

         slackThis = this.possReducedCoefs.subtract(reducedDegree).multiply(this.coefMultCons);
         assert slackThis.equals(wpb.slackConstraint(reducedCoefs, reducedDegree).multiply(this.coefMultCons));
         assert slackConflict.equals(this.slackConflict());
         BigInteger slackIndex = slackConflict.multiply(this.coefMult);
         assert slackIndex.signum() <= 0;
         slackResolve = slackThis.add(slackIndex);
      } while (slackResolve.signum() >= 0 || this.isUnsat());

      assert this.coefMult.multiply(this.weightedLits.get(litImplied ^ 1)).equals(this.coefMultCons.multiply(reducedCoefs[ind]));
      return reducedDegree;

      throw new AssertionError();
   }

   protected BigInteger possConstraint(IWatchPb wpb, BigInteger[] theCoefs) {
      BigInteger poss = BigInteger.ZERO;

      for (int i = 0; i < wpb.size(); i++) {
         if (!this.voc.isFalsified(wpb.get(i))) {
            assert theCoefs[i].signum() >= 0;
            poss = poss.add(theCoefs[i]);
         }
      }

      return poss;
   }

   @Override
   public BigInteger slackConflict() {
      BigInteger poss = BigInteger.ZERO;

      for (int i = 0; i < this.size(); i++) {
         BigInteger tmp = this.weightedLits.getCoef(i);
         if (tmp.signum() != 0 && !this.voc.isFalsified(this.weightedLits.getLit(i))) {
            assert tmp.signum() > 0;
            poss = poss.add(tmp);
         }
      }

      return poss.subtract(this.degree);
   }

   public boolean oldIsAssertive(int dl) {
      BigInteger slack = this.computeSlack(dl).subtract(this.degree);
      if (slack.signum() < 0) {
         return false;
      }

      for (int i = 0; i < this.size(); i++) {
         BigInteger tmp = this.weightedLits.getCoef(i);
         int lit = this.weightedLits.getLit(i);
         if (tmp.signum() > 0 && (this.voc.isUnassigned(lit) || this.voc.getLevel(lit) >= dl) && slack.compareTo(tmp) < 0) {
            return true;
         }
      }

      return false;
   }

   BigInteger computeSlack(int dl) {
      BigInteger slack = BigInteger.ZERO;

      for (int i = 0; i < this.size(); i++) {
         BigInteger tmp = this.weightedLits.getCoef(i);
         int lit = this.weightedLits.getLit(i);
         if (tmp.signum() > 0 && (!this.voc.isFalsified(lit) || this.voc.getLevel(lit) >= dl)) {
            slack = slack.add(tmp);
         }
      }

      return slack;
   }

   @Override
   public boolean isAssertive(int dl) {
      assert dl <= this.currentLevel;
      BigInteger slack = this.currentSlack.subtract(this.degree);
      return slack.signum() < 0 ? false : this.isImplyingLiteral(slack, dl);
   }

   @Override
   public void setDecisionLevel(int dl) {
      assert dl <= this.currentLevel;
      if (dl < this.currentLevel) {
         this.updateSlack(dl);
      }

      this.currentLevel = dl;
   }

   @Override
   public boolean isUnsat() {
      return this.sumAllCoefs.subtract(this.degree).signum() < 0;
   }

   private boolean isImplyingLiteral(BigInteger slack, int currentDecisionLevel) {
      int unassigned = levelToIndex(-1);
      if (this.byLevel[unassigned] != null) {
         IteratorInt iterator = this.byLevel[unassigned].iterator();

         while (iterator.hasNext()) {
            int lit = iterator.next();
            if (slack.compareTo(this.weightedLits.get(lit)) < 0) {
               this.assertiveLiteral = this.weightedLits.getFromAllLits(lit);
               return true;
            }
         }
      }

      int level = levelToIndex(currentDecisionLevel);
      if (this.byLevel[level] != null) {
         IteratorInt iterator = this.byLevel[level].iterator();

         while (iterator.hasNext()) {
            int lit = iterator.next();
            BigInteger tmp = this.weightedLits.get(lit);
            if (tmp != null && slack.compareTo(tmp) < 0) {
               this.assertiveLiteral = this.weightedLits.getFromAllLits(lit);
               return true;
            }
         }
      }

      return false;
   }

   boolean isImplyingLiteralOrdered(int dl, BigInteger slack) {
      for (int i = 0; i < this.size(); i++) {
         int ilit = this.weightedLits.getLit(i);
         int litLevel = this.voc.getLevel(ilit);
         if ((litLevel >= dl || this.voc.isUnassigned(ilit)) && slack.compareTo(this.weightedLits.getCoef(i)) < 0) {
            this.assertiveLiteral = i;
            return true;
         }
      }

      return false;
   }

   boolean isImplyingLiteralOrdered(int dl, BigInteger slack, IVecInt literals) {
      assert literals.size() == 0;

      for (int i = 0; i < this.size(); i++) {
         int ilit = this.weightedLits.getLit(i);
         int litLevel = this.voc.getLevel(ilit);
         if ((litLevel >= dl || this.voc.isUnassigned(ilit)) && slack.compareTo(this.weightedLits.getCoef(i)) < 0) {
            literals.push(i);
         }
      }

      return literals.size() > 0;
   }

   protected BigInteger ppcm(BigInteger a, BigInteger b) {
      long before = System.nanoTime();
      BigInteger lcm = a.divide(a.gcd(b)).multiply(b);
      long after = System.nanoTime();
      this.stats.incTimeForArithmeticOperations(after - before);
      return lcm;
   }

   @Override
   public BigInteger reduceInConstraint(IWatchPb wpb, BigInteger[] coefsBis, int indLitImplied, BigInteger degreeBis, BigInteger slackResolve) {
      assert degreeBis.compareTo(BigInteger.ONE) > 0;
      int lit = this.weakeningStrategy.findLiteralToRemove(this.voc, wpb, coefsBis, indLitImplied, degreeBis);
      assert lit != -1;
      assert lit != indLitImplied;
      BigInteger degUpdate = degreeBis.subtract(coefsBis[lit]);
      this.possReducedCoefs = this.possReducedCoefs.subtract(coefsBis[lit]);
      coefsBis[lit] = BigInteger.ZERO;
      assert this.possReducedCoefs.equals(this.possConstraint(wpb, coefsBis));
      degUpdate = this.saturation(coefsBis, degUpdate, wpb);
      assert coefsBis[indLitImplied].signum() > 0;
      assert degreeBis.compareTo(degUpdate) > 0;
      assert this.possReducedCoefs.equals(this.possConstraint(wpb, coefsBis));
      return degUpdate;
   }

   protected BigInteger saturation(BigInteger[] coefs, BigInteger degree, IWatchPb wpb) {
      assert degree.signum() > 0;
      BigInteger degreeResult = degree;
      boolean isMinimumEqualsToDegree = true;

      for (int i = 0; i < coefs.length; i++) {
         int comparison = coefs[i].compareTo(degree);
         if (comparison > 0) {
            if (!this.voc.isFalsified(wpb.get(i))) {
               this.possReducedCoefs = this.possReducedCoefs.subtract(coefs[i]);
               this.possReducedCoefs = this.possReducedCoefs.add(degree);
            }

            coefs[i] = degree;
         } else if (comparison < 0 && coefs[i].signum() > 0) {
            isMinimumEqualsToDegree = false;
         }
      }

      if (isMinimumEqualsToDegree && !degree.equals(BigInteger.ONE)) {
         this.possReducedCoefs = BigInteger.ZERO;
         degreeResult = BigInteger.ONE;

         for (int i = 0; i < coefs.length; i++) {
            if (coefs[i].signum() > 0) {
               coefs[i] = BigInteger.ONE;
               if (!this.voc.isFalsified(wpb.get(i))) {
                  this.possReducedCoefs = this.possReducedCoefs.add(BigInteger.ONE);
               }
            }
         }
      }

      return degreeResult;
   }

   private static boolean positiveCoefs(BigInteger[] coefsCons) {
      for (BigInteger coefsCon : coefsCons) {
         if (coefsCon.signum() < 0) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int getBacktrackLevel(int maxLevel) {
      if (this.backtrackLevel != -2) {
         return this.backtrackLevel;
      }

      int indStop = levelToIndex(maxLevel) - 1;
      int indStart = levelToIndex(0);
      BigInteger slack = this.computeSlack(0).subtract(this.degree);
      int previous = 0;

      for (int indLevel = indStart; indLevel <= indStop; indLevel++) {
         if (this.byLevel[indLevel] != null) {
            int level = indexToLevel(indLevel);
            assert this.computeSlack(level).subtract(this.degree).equals(slack);
            if (this.isImplyingLiteralOrdered(level, slack)) {
               break;
            }

            VecInt lits = this.byLevel[indLevel];
            boolean anyFalsified = false;
            IteratorInt iterator = lits.iterator();

            while (iterator.hasNext()) {
               int lit = iterator.next();
               anyFalsified |= this.voc.isFalsified(lit);
               if (this.voc.isFalsified(lit) && this.voc.getLevel(lit) == indexToLevel(indLevel)) {
                  slack = slack.subtract(this.weightedLits.get(lit));
               }
            }

            if (anyFalsified) {
               previous = level;
            }
         }
      }

      assert previous == this.oldGetBacktrackLevel(maxLevel);
      return previous;
   }

   public int oldGetBacktrackLevel(int maxLevel) {
      int borneMax = maxLevel;
      int borneMin = -1;

      for (int i = 0; i < this.size(); i++) {
         int litLevel = this.voc.getLevel(this.weightedLits.getLit(i));
         if (litLevel < borneMax && litLevel > borneMin && this.oldIsAssertive(litLevel)) {
            borneMax = litLevel;
         }
      }

      int retour = 0;

      for (int i = 0; i < this.size(); i++) {
         int lit = this.weightedLits.getLit(i);
         int litLevel = this.voc.getLevel(lit);
         if (this.voc.isFalsified(lit) && litLevel > retour && litLevel < borneMax) {
            retour = litLevel;
         }
      }

      return retour;
   }

   @Override
   public void updateSlack(int level) {
      int dl = levelToIndex(level);
      if (this.byLevel[dl] != null) {
         IteratorInt iterator = this.byLevel[dl].iterator();

         while (iterator.hasNext()) {
            int lit = iterator.next();
            if (this.voc.isFalsified(lit)) {
               this.currentSlack = this.currentSlack.add(this.weightedLits.get(lit));
            }
         }
      }
   }

   @Override
   void increaseCoef(int lit, BigInteger incCoef) {
      if (!this.voc.isFalsified(lit) || this.voc.getLevel(lit) == this.currentLevel) {
         this.currentSlack = this.currentSlack.add(incCoef);
      }

      this.sumAllCoefs = this.sumAllCoefs.add(incCoef);
      assert this.byLevel[levelToIndex(this.voc.getLevel(lit))].contains(lit);
      super.increaseCoef(lit, incCoef);
   }

   @Override
   void decreaseCoef(int lit, BigInteger decCoef) {
      if (!this.voc.isFalsified(lit) || this.voc.getLevel(lit) == this.currentLevel) {
         this.currentSlack = this.currentSlack.subtract(decCoef);
      }

      this.sumAllCoefs = this.sumAllCoefs.subtract(decCoef);
      assert this.byLevel[levelToIndex(this.voc.getLevel(lit))].contains(lit);
      super.decreaseCoef(lit, decCoef);
   }

   @Override
   void setCoef(int lit, BigInteger newValue) {
      int litLevel = this.voc.getLevel(lit);
      if (!this.voc.isFalsified(lit) || litLevel == this.currentLevel) {
         if (this.weightedLits.containsKey(lit)) {
            this.currentSlack = this.currentSlack.subtract(this.weightedLits.get(lit));
         }

         this.currentSlack = this.currentSlack.add(newValue);
      }

      if (this.weightedLits.containsKey(lit)) {
         this.sumAllCoefs = this.sumAllCoefs.subtract(this.weightedLits.get(lit));
      }

      this.sumAllCoefs = this.sumAllCoefs.add(newValue);
      int indLitLevel = levelToIndex(litLevel);
      if (!this.weightedLits.containsKey(lit)) {
         if (this.byLevel[indLitLevel] == null) {
            this.byLevel[indLitLevel] = new VecInt();
         }

         this.byLevel[indLitLevel].push(lit);
      }

      assert this.byLevel[indLitLevel] != null;
      assert this.byLevel[indLitLevel].contains(lit);
      super.setCoef(lit, newValue);
   }

   @Override
   void changeCoef(int indLit, BigInteger newValue) {
      int lit = this.weightedLits.getLit(indLit);
      int litLevel = this.voc.getLevel(lit);
      if (!this.voc.isFalsified(lit) || litLevel == this.currentLevel) {
         if (this.weightedLits.containsKey(lit)) {
            this.currentSlack = this.currentSlack.subtract(this.weightedLits.get(lit));
         }

         this.currentSlack = this.currentSlack.add(newValue);
      }

      if (this.weightedLits.containsKey(lit)) {
         this.sumAllCoefs = this.sumAllCoefs.subtract(this.weightedLits.get(lit));
      }

      this.sumAllCoefs = this.sumAllCoefs.add(newValue);
      int indLitLevel = levelToIndex(litLevel);
      assert this.weightedLits.containsKey(lit);
      assert this.byLevel[indLitLevel] != null;
      assert this.byLevel[indLitLevel].contains(lit);
      super.changeCoef(indLit, newValue);
   }

   @Override
   void removeCoef(int lit) {
      int litLevel = this.voc.getLevel(lit);
      if (!this.voc.isFalsified(lit) || litLevel == this.currentLevel) {
         this.currentSlack = this.currentSlack.subtract(this.weightedLits.get(lit));
      }

      this.sumAllCoefs = this.sumAllCoefs.subtract(this.weightedLits.get(lit));
      int indLitLevel = levelToIndex(litLevel);
      assert indLitLevel < this.byLevel.length;
      assert this.byLevel[indLitLevel] != null;
      assert this.byLevel[indLitLevel].contains(lit);
      this.byLevel[indLitLevel].remove(lit);
      super.removeCoef(lit);
   }

   private int getLevelByLevel(int lit) {
      for (int i = 0; i < this.byLevel.length; i++) {
         if (this.byLevel[i] != null && this.byLevel[i].contains(lit)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public boolean slackIsCorrect(int dl) {
      return this.currentSlack.equals(this.computeSlack(dl));
   }

   @Override
   public String toString() {
      StringBuilder stb = new StringBuilder();

      for (int i = 0; i < this.size(); i++) {
         int lit = this.weightedLits.getLit(i);
         stb.append(this.weightedLits.getCoef(i));
         stb.append(".");
         stb.append(Lits.toString(lit));
         stb.append(" ");
         stb.append("[");
         stb.append(this.voc.valueToString(lit));
         stb.append("@");
         stb.append(this.voc.getLevel(lit));
         stb.append("]");
      }

      return stb.toString() + " >= " + this.degree;
   }

   public boolean hasBeenReduced() {
      return this.hasBeenReduced;
   }

   public long getNumberOfReductions() {
      return this.numberOfReductions;
   }

   @Override
   public void undoOne(int lit) {
      int nLit = lit ^ 1;
      if (this.weightedLits.containsKey(nLit)) {
         lit = nLit;
      }

      int litLevel = this.voc.getLevel(lit);
      int indLitLevel = levelToIndex(litLevel);
      assert indLitLevel < this.byLevel.length;
      if (this.byLevel[indLitLevel] != null && this.byLevel[indLitLevel].contains(lit)) {
         this.byLevel[indLitLevel].remove(lit);
         if (this.byLevel[0] == null) {
            this.byLevel[0] = new VecInt();
         }

         this.byLevel[0].push(lit);
      }
   }

   private static class NoRemoveSatisfied implements IRemoveSatisfiedLiterals {
      private NoRemoveSatisfied() {
      }

      @Override
      public BigInteger removeSatisfiedLiteralsFromHigherDecisionLevels(IWatchPb wpb, BigInteger[] coefsBis, int currentLevel, BigInteger degreeBis) {
         return degreeBis;
      }
   }

   private class RemoveSatisfied implements IRemoveSatisfiedLiterals {
      // $VF: synthetic field
      static final boolean $assertionsDisabled = !ConflictMap.class.desiredAssertionStatus();

      private RemoveSatisfied() {
      }

      @Override
      public BigInteger removeSatisfiedLiteralsFromHigherDecisionLevels(IWatchPb wpb, BigInteger[] coefsBis, int currentLevel, BigInteger degreeBis) {
         assert degreeBis.compareTo(BigInteger.ONE) > 0;
         int size = wpb.size();
         BigInteger degUpdate = degreeBis;
         ConflictMap.this.possReducedCoefs = ConflictMap.this.possConstraint(wpb, coefsBis);

         for (int ind = 0; ind < size; ind++) {
            int p = wpb.get(ind);
            if (coefsBis[ind].signum() != 0 && ConflictMap.this.voc.isSatisfied(p) && ConflictMap.this.voc.getLevel(p) < currentLevel) {
               degUpdate = degUpdate.subtract(coefsBis[ind]);
               ConflictMap.this.possReducedCoefs = ConflictMap.this.possReducedCoefs.subtract(coefsBis[ind]);
               coefsBis[ind] = BigInteger.ZERO;
               assert ConflictMap.this.possReducedCoefs.equals(ConflictMap.this.possConstraint(wpb, coefsBis));
            }
         }

         degUpdate = ConflictMap.this.saturation(coefsBis, degUpdate, wpb);
         assert degreeBis.compareTo(degUpdate) >= 0;
         assert ConflictMap.this.possReducedCoefs.equals(ConflictMap.this.possConstraint(wpb, coefsBis));
         return degUpdate;
      }
   }
}
