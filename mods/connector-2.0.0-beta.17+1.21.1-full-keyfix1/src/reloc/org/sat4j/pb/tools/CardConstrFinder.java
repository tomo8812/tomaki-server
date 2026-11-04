package reloc.org.sat4j.pb.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.IPBSolverService;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.SearchListener;
import reloc.org.sat4j.specs.SearchListenerAdapter;
import reloc.org.sat4j.specs.TimeoutException;

public class CardConstrFinder implements Iterator<AtLeastCard>, Iterable<AtLeastCard> {
   private final IPBSolver coSolver;
   private BitSet propagated = null;
   private final SearchListener<ISolverService> oldListener;
   private final SortedSet<AtLeastCard> atLeastCards = new TreeSet<>(new CardConstrFinder.AtLeastCardDegreeComparator());
   private final Map<Integer, List<BitSet>> atLeastCardCache = new HashMap<>();
   private final Map<BitSet, Integer> atLeastCardDegree = new HashMap<>();
   private Iterator<BitSet> cardIt;
   private int initNumberOfConstraints;
   private BitSet zeroProps = null;
   private boolean printCards = false;
   private boolean shouldDisplayStatus = false;
   private Set<Integer> authorizedExtLits = null;
   private boolean verbose = false;
   private final Map<BitSet, BitSet> implied = new HashMap<>();

   public CardConstrFinder(IPBSolver coSolver) {
      this.coSolver = coSolver;
      this.coSolver.setTimeoutOnConflicts(Integer.MAX_VALUE);
      this.oldListener = this.coSolver.getSearchListener();
      this.coSolver.setSearchListener(new CardConstrFinder.CardConstrFinderListener(this));
   }

   public void forget() {
      this.coSolver.setSearchListener(this.oldListener);
   }

   public void addClause(IVecInt clause) {
      this.addAtLeast(clause, 1);
   }

   public void addAtLeast(IVecInt lits, int threshold) {
      this.atLeastCards.add(new AtLeastCard(lits, threshold));
   }

   public void addAtMost(IVecInt vec, int threshold) {
      this.atLeastCards.add(new AtMostCard(vec, threshold).toAtLeast());
   }

   public void rissPreprocessing(String rissLocation, String instance) {
      this.initNumberOfConstraints = this.atLeastCards.size();
      int status = -1;
      if (this.verbose) {
         System.out.println("c executing riss subprocess");
      }

      try {
         Process p = Runtime.getRuntime()
            .exec(rissLocation + " -findCard -card_print -no-card_amt -no-card_amo -no-card_sub -no-card_twoProd -no-card_merge -card_noLim " + instance);
         BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

         String line;
         while ((line = reader.readLine()) != null) {
            if (!line.startsWith("c ")) {
               IVecInt lits = new VecInt();
               String[] words = line.split(" +");

               try {
                  for (int i = 0; i < words.length - 2; i++) {
                     lits.push(Integer.valueOf(words[i]));
                  }

                  int degree = Integer.valueOf(words[words.length - 1]);
                  if (this.verbose) {
                     System.out.println("c riss extracted: " + new AtMostCard(lits, degree));
                  }

                  this.storeAtMostCard(lits, degree);
               } catch (Exception e) {
                  System.err.println("WARNING: read \"" + line + "\" from Riss subprocess");
               }
            }
         }

         reader.close();
         status = p.waitFor();
         if (this.verbose) {
            System.out.println("c riss process exited with status " + status);
         }
      } catch (Exception e) {
         Logger.getLogger("reloc.org.sat4j.pb").log(Level.INFO, "Issue when riss subprocess", e);
      }

      Iterator<AtLeastCard> cardIt = this.atLeastCards.iterator();

      while (cardIt.hasNext()) {
         AtLeastCard card = cardIt.next();
         BitSet atLeastLits = new BitSet(card.getLits().size());
         IteratorInt litIt = card.getLits().iterator();

         while (litIt.hasNext()) {
            atLeastLits.set(litIt.next() + this.coSolver.realNumberOfVariables());
         }

         if (this.cardIsSubsumed(atLeastLits, card.getDegree())) {
            cardIt.remove();
         }
      }

      this.cardIt = this.atLeastCardDegree.keySet().iterator();
   }

   public void searchCards() {
      this.initNumberOfConstraints = this.atLeastCards.size();
      int cpt = 0;
      Timer timerStatus = new Timer();
      timerStatus.scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            CardConstrFinder.this.shouldDisplayStatus = true;
         }
      }, 30000L, 30000L);
      Iterator<AtLeastCard> itCard = this.atLeastCards.iterator();

      while (itCard.hasNext()) {
         AtLeastCard atLeastCard = itCard.next();
         BitSet atLeastLits = new BitSet(atLeastCard.getLits().size());
         IteratorInt itLits = atLeastCard.getLits().iterator();

         while (itLits.hasNext()) {
            atLeastLits.set(itLits.next() + this.coSolver.realNumberOfVariables());
         }

         if (!this.cardIsSubsumed(atLeastLits, atLeastCard.getDegree())) {
            BitSet cardFound = this.searchCardFromAtLeastCard(atLeastLits, atLeastCard.getDegree());
            if (cardFound != null) {
               itCard.remove();
            }
         } else {
            itCard.remove();
         }

         cpt++;
         if (this.shouldDisplayStatus) {
            System.out.println("c processed " + cpt + "/" + this.initNumberOfConstraints + " constraints");
            this.shouldDisplayStatus = false;
         }
      }

      timerStatus.cancel();
      this.cardIt = this.atLeastCardDegree.keySet().iterator();
   }

   public IVecInt searchCardFromClause(IVecInt clause) {
      return this.searchCardFromAtMostCard(clause, clause.size() - 1);
   }

   public IVecInt searchCardFromAtMostCard(IVecInt literals, int threshold) {
      BitSet atLeastLits = new BitSet(literals.size());
      IteratorInt it = literals.iterator();

      while (it.hasNext()) {
         atLeastLits.set(it.next() + this.coSolver.realNumberOfVariables());
      }

      BitSet cardFound = this.searchCardFromAtLeastCard(atLeastLits, atLeastLits.cardinality() - threshold);
      if (cardFound == null) {
         return null;
      }

      IVecInt atMostLits = new VecInt(cardFound.cardinality());

      for (int from = 0; (from = cardFound.nextSetBit(from)) != -1; from++) {
         atMostLits.push(from - this.coSolver.realNumberOfVariables());
      }

      return atMostLits;
   }

   private BitSet searchCardFromAtLeastCard(BitSet atLeastLits, int threshold) {
      BitSet atMostLits = new BitSet(atLeastLits.cardinality());
      int from = 0;

      int cur;
      while ((cur = atLeastLits.nextSetBit(from)) != -1) {
         int negBit = 2 * this.coSolver.realNumberOfVariables() - cur;
         atMostLits.set(negBit);
         from = cur + 1;
      }

      int atMostDegree = atLeastLits.cardinality() - threshold;
      Set<Integer> newLits = this.expendAtMostCard(atMostLits, atMostDegree);

      for (Integer lit : newLits) {
         atLeastLits.set(-lit + this.coSolver.realNumberOfVariables());
      }

      if (newLits.isEmpty()) {
         return null;
      }

      this.storeAtLeastCard(atLeastLits, atLeastLits.cardinality() - atMostDegree);
      if (this.printCards) {
         System.out.println("c newConstr: " + new AtMostCard(atMostLits, atMostDegree, -this.coSolver.realNumberOfVariables()));
      }

      return atMostLits;
   }

   private boolean cardIsSubsumed(BitSet atLeastLits, int threshold) {
      List<BitSet> storedCards = this.atLeastCardCache.get(atLeastLits.nextSetBit(0) - this.coSolver.realNumberOfVariables());
      if (storedCards == null) {
         return false;
      }

      for (BitSet storedCard : storedCards) {
         BitSet atLeastLitsClone = (BitSet)atLeastLits.clone();
         atLeastLitsClone.andNot(storedCard);
         if (atLeastLitsClone.isEmpty()) {
            BitSet intersection = (BitSet)storedCard.clone();
            intersection.andNot(atLeastLits);
            if (intersection.cardinality() <= this.atLeastCardDegree.get(storedCard) - threshold) {
               return true;
            }
         }
      }

      return false;
   }

   private void storeAtLeastCard(BitSet atLeastLits, int atLeastDegree) {
      int from = 0;

      int cur;
      while ((cur = atLeastLits.nextSetBit(from)) != -1) {
         List<BitSet> cardsList = this.atLeastCardCache.get(cur - this.coSolver.realNumberOfVariables());
         if (cardsList == null) {
            cardsList = new LinkedList<>();
            this.atLeastCardCache.put(cur - this.coSolver.realNumberOfVariables(), cardsList);
         }

         cardsList.add(atLeastLits);
         from = cur + 1;
      }

      this.atLeastCardDegree.put(atLeastLits, atLeastDegree);
   }

   private void storeAtMostCard(IVecInt lits, int degree) {
      BitSet bs = new BitSet();
      IteratorInt it = lits.iterator();

      while (it.hasNext()) {
         int lit = it.next();
         bs.set(this.coSolver.realNumberOfVariables() - lit);
      }

      this.storeAtLeastCard(bs, lits.size() - degree);
   }

   private Set<Integer> expendAtMostCard(BitSet atMostLits, int degree) {
      Set<Integer> res = new HashSet<>();
      BitSet candidates = this.computeInitialCandidates(atMostLits, degree);
      if (candidates != null && !candidates.isEmpty()) {
         int from = 0;

         int cur;
         while ((cur = candidates.nextSetBit(from)) != -1) {
            from = cur + 1;
            if (!atMostLits.get(2 * this.coSolver.realNumberOfVariables() - cur)) {
               res.add(-(cur - this.coSolver.realNumberOfVariables()));
               this.refineCandidates(atMostLits, degree, cur, candidates);
               atMostLits.set(2 * this.coSolver.realNumberOfVariables() - cur);
            }
         }

         return res;
      } else {
         return res;
      }
   }

   private void refineCandidates(BitSet atMostLits, int degree, int newLitInCard, BitSet candidates) {
      if (degree == 1) {
         BitSet newLit = new BitSet(1);
         newLit.set(2 * this.coSolver.realNumberOfVariables() - newLitInCard);
         BitSet implied = this.impliedBy(newLit);
         candidates.and(implied);
      } else {
         CombinationIterator combIt = new CombinationIterator(degree - 1, atMostLits);

         while (combIt.hasNext()) {
            BitSet comb = combIt.nextBitSet();
            comb.set(2 * this.coSolver.realNumberOfVariables() - newLitInCard);
            candidates.and(this.impliedBy(comb));
            if (candidates.isEmpty()) {
               break;
            }
         }
      }
   }

   private BitSet computeInitialCandidates(BitSet atMostLits, int degree) {
      BitSet candidates = null;
      CombinationIterator combIt = new CombinationIterator(degree, atMostLits);

      while (combIt.hasNext()) {
         BitSet nextBitSet = combIt.nextBitSet();
         BitSet implied = this.impliedBy(nextBitSet);
         if (candidates == null) {
            candidates = implied;
         } else {
            candidates.and(implied);
         }

         if (candidates.isEmpty()) {
            return candidates;
         }
      }

      return candidates;
   }

   private BitSet impliedBy(BitSet lits) {
      if (this.zeroProps == null) {
         this.zeroProps = new BitSet(0);
         this.zeroProps = this.impliedBy(new BitSet(0));
         if (this.verbose) {
            System.out.println("c " + this.zeroProps.cardinality() + " literals propagated at decision level 0");
         }
      }

      BitSet cached = this.implied.get(lits);
      if (cached != null) {
         return cached;
      }

      IVecInt litVec = new VecInt(this.zeroProps.cardinality() + lits.cardinality());
      int from = 0;

      int cur;
      while ((cur = lits.nextSetBit(from)) != -1) {
         litVec.push(cur - this.coSolver.realNumberOfVariables());
         from = cur + 1;
      }

      this.propagated = new BitSet();

      try {
         this.coSolver.isSatisfiable(litVec);
      } catch (TimeoutException var7) {
      }

      this.propagated.andNot(this.zeroProps);
      this.implied.put(lits, (BitSet)this.propagated.clone());
      return this.propagated;
   }

   public Set<AtLeastCard> remainingAtLeastCards() {
      return this.atLeastCards;
   }

   public int initNumberOfClauses() {
      return this.initNumberOfConstraints;
   }

   public void setAuthorizedExtLits(IVecInt lits) {
      this.authorizedExtLits = new HashSet<>();
      IteratorInt it = lits.iterator();

      while (it.hasNext()) {
         this.authorizedExtLits.add(it.next());
      }
   }

   @Override
   public boolean hasNext() {
      if (this.cardIt == null) {
         return false;
      }

      boolean res = this.cardIt.hasNext();
      if (!res) {
         this.cardIt = this.atLeastCardDegree.keySet().iterator();
      }

      return res;
   }

   public AtLeastCard next() {
      BitSet next = this.cardIt.next();
      return new AtLeastCard(next, this.atLeastCardDegree.get(next), -this.coSolver.realNumberOfVariables());
   }

   @Override
   public void remove() {
      this.cardIt.remove();
   }

   public void setPrintCards(boolean b) {
      this.printCards = b;
   }

   public void setVerbose(boolean verbose) {
      this.verbose = verbose;
   }

   @Override
   public Iterator<AtLeastCard> iterator() {
      this.cardIt = this.atLeastCardDegree.keySet().iterator();
      return this;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }

   private static class AtLeastCardDegreeComparator implements Comparator<AtLeastCard>, Serializable {
      private static final long serialVersionUID = 1L;

      private AtLeastCardDegreeComparator() {
      }

      public int compare(AtLeastCard arg0, AtLeastCard arg1) {
         int degreeComparison = arg0.getLits().size() - arg0.getDegree() - arg1.getLits().size() + arg1.getDegree();
         return degreeComparison != 0 ? degreeComparison : 1;
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public int compare(Object var1, Object var2) {
         return this.compare((AtLeastCard)var1, (AtLeastCard)var2);
      }
   }

   private class CardConstrFinderListener extends SearchListenerAdapter<IPBSolverService> {
      private static final long serialVersionUID = 1L;
      private final CardConstrFinder ccf;

      private CardConstrFinderListener(CardConstrFinder ccf) {
         this.ccf = ccf;
      }

      @Override
      public void propagating(int p) {
         if (CardConstrFinder.this.authorizedExtLits == null || CardConstrFinder.this.authorizedExtLits.contains(p)) {
            this.ccf.propagated.set(p + this.ccf.coSolver.realNumberOfVariables());
         }
      }

      @Override
      public void beginLoop() {
         this.ccf.coSolver.expireTimeout();
      }
   }
}
