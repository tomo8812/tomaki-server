package reloc.org.sat4j.minisat.constraints.card;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.minisat.constraints.cnf.Lits;
import reloc.org.sat4j.minisat.constraints.cnf.OriginalWLClause;
import reloc.org.sat4j.minisat.constraints.cnf.UnitClauses;
import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.Undoable;
import reloc.org.sat4j.specs.Constr;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.MandatoryLiteralListener;
import reloc.org.sat4j.specs.Propagatable;
import reloc.org.sat4j.specs.UnitPropagationListener;
import reloc.org.sat4j.specs.VarMapper;

@Feature("constraint")
public class AtLeast implements Propagatable, Constr, Undoable, Serializable {
   private static final long serialVersionUID = 1L;
   protected int maxUnsatisfied;
   private int counter;
   protected final int[] lits;
   protected final ILits voc;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AtLeast.class.desiredAssertionStatus();

   public AtLeast(ILits voc, IVecInt ps, int degree) {
      if (degree == 1) {
         throw new IllegalArgumentException("cards with degree 1 are clauses!!!!");
      }

      this.maxUnsatisfied = ps.size() - degree;
      this.voc = voc;
      this.counter = 0;
      this.lits = new int[ps.size()];
      ps.moveTo(this.lits);
   }

   protected static int niceParameters(UnitPropagationListener s, ILits voc, IVecInt ps, int deg) throws ContradictionException {
      if (ps.size() < deg) {
         throw new ContradictionException();
      }

      int degree = deg;
      int i = 0;

      while (i < ps.size()) {
         if (voc.isUnassigned(ps.get(i))) {
            i++;
         } else {
            if (voc.isSatisfied(ps.get(i))) {
               degree--;
            }

            ps.delete(i);
         }
      }

      ps.sortUnique();
      if (ps.size() == degree) {
         for (int ix = 0; ix < ps.size(); ix++) {
            if (!s.enqueue(ps.get(ix))) {
               throw new ContradictionException();
            }
         }

         return 0;
      } else if (ps.size() < degree) {
         throw new ContradictionException();
      } else {
         return degree;
      }
   }

   public static Constr atLeastNew(UnitPropagationListener s, ILits voc, IVecInt ps, int n) throws ContradictionException {
      int degree = niceParameters(s, voc, ps, n);
      if (degree == 0) {
         return new UnitClauses(ps);
      }

      if (degree == 1) {
         return OriginalWLClause.brandNewClause(s, voc, ps);
      }

      Constr constr = new AtLeast(voc, ps, degree);
      constr.register();
      return constr;
   }

   @Override
   public void remove(UnitPropagationListener upl) {
      for (int q : this.lits) {
         this.voc.watches(q ^ 1).remove(this);
      }
   }

   @Override
   public boolean propagate(UnitPropagationListener s, int p) {
      this.voc.watch(p, this);
      if (this.counter == this.maxUnsatisfied) {
         return false;
      }

      this.counter++;
      this.voc.undos(p).push(this);
      if (this.counter == this.maxUnsatisfied) {
         for (int q : this.lits) {
            if (this.voc.isUnassigned(q) && !s.enqueue(q, this)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public boolean simplify() {
      return false;
   }

   @Override
   public void undo(int p) {
      this.counter--;
   }

   @Override
   public void calcReason(int p, IVecInt outReason) {
      int c = p == -1 ? -1 : 0;

      for (int q : this.lits) {
         if (this.voc.isFalsified(q)) {
            outReason.push(q ^ 1);
            if (++c >= this.maxUnsatisfied) {
               return;
            }
         }
      }
   }

   @Override
   public boolean learnt() {
      return false;
   }

   @Override
   public double getActivity() {
      return 0.0;
   }

   @Override
   public void setActivity(double d) {
   }

   @Override
   public void incActivity(double claInc) {
   }

   @Override
   public boolean locked() {
      return true;
   }

   @Override
   public void setLearnt() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void register() {
      this.counter = 0;

      for (int q : this.lits) {
         this.voc.watch(q ^ 1, this);
         if (this.voc.isFalsified(q)) {
            this.counter++;
            this.voc.undos(q ^ 1).push(this);
         }
      }
   }

   @Override
   public int size() {
      return this.lits.length;
   }

   @Override
   public int get(int i) {
      return this.lits[i];
   }

   @Override
   public void rescaleBy(double d) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void assertConstraint(UnitPropagationListener s) {
      boolean ret = true;
      int[] var3 = this.lits;
      int var4 = var3.length;

      for (int var5 = 0; var5 < var4; var5++) {
         Integer lit = var3[var5];
         if (this.voc.isUnassigned(lit)) {
            ret &= s.enqueue(lit, this);
         }
      }

      assert ret;
   }

   @Override
   public void assertConstraintIfNeeded(UnitPropagationListener s) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      StringBuilder stb = new StringBuilder();
      stb.append("Card (" + this.lits.length + ") : ");

      for (int lit : this.lits) {
         stb.append(" + ");
         stb.append(Lits.toString(lit));
         stb.append("[");
         stb.append(this.voc.valueToString(lit));
         stb.append("@");
         stb.append(this.voc.getLevel(lit));
         stb.append("]  ");
      }

      stb.append(">= ");
      stb.append(this.size() - this.maxUnsatisfied);
      return stb.toString();
   }

   @Override
   public void forwardActivity(double claInc) {
   }

   @Override
   public boolean canBePropagatedMultipleTimes() {
      return false;
   }

   @Override
   public Constr toConstraint() {
      return this;
   }

   @Override
   public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
      int c = p == -1 ? -1 : 0;
      IVecInt vlits = new VecInt(this.lits);
      IteratorInt it = trail.iterator();

      while (it.hasNext()) {
         int q = it.next();
         if (vlits.contains(q ^ 1)) {
            outReason.push(q);
            if (++c >= this.maxUnsatisfied) {
               return;
            }
         }
      }
   }

   @Override
   public boolean propagatePI(MandatoryLiteralListener l, int p) {
      this.voc.watch(p, this);
      this.counter++;
      this.voc.undos(p).push(this);
      if (this.counter == this.maxUnsatisfied) {
         for (int q : this.lits) {
            if (!this.voc.isFalsified(q)) {
               l.isMandatory(q);
            }
         }
      }

      return true;
   }

   @Override
   public boolean canBeSatisfiedByCountingLiterals() {
      return true;
   }

   @Override
   public int requiredNumberOfSatisfiedLiterals() {
      return this.lits.length - this.maxUnsatisfied;
   }

   @Override
   public boolean isSatisfied() {
      int nbSatisfied = 0;
      int degree = this.size() - this.maxUnsatisfied;

      for (int p : this.lits) {
         if (this.voc.isSatisfied(p)) {
            if (++nbSatisfied >= degree) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public int getAssertionLevel(IVecInt trail, int decisionLevel) {
      int nUnsat = 0;
      Set<Integer> litsSet = new HashSet<>();
      int[] i = this.lits;
      int var6 = i.length;

      for (int var7 = 0; var7 < var6; var7++) {
         Integer ix = i[var7];
         litsSet.add(ix);
      }

      for (int ix = 0; ix < trail.size(); ix++) {
         if (litsSet.contains(trail.get(ix) ^ 1)) {
            if (++nUnsat == this.maxUnsatisfied) {
               return ix;
            }
         }
      }

      return -1;
   }

   @Override
   public String toString(VarMapper mapper) {
      StringBuilder stb = new StringBuilder();

      for (int lit : this.lits) {
         stb.append(" + ");
         stb.append(mapper.map(LiteralsUtils.toDimacs(lit)));
         stb.append("[");
         stb.append(this.voc.valueToString(lit));
         stb.append("]  ");
      }

      stb.append(">= ");
      stb.append(this.size() - this.maxUnsatisfied);
      return stb.toString();
   }

   @Override
   public String dump() {
      StringBuilder stb = new StringBuilder();
      stb.append(LiteralsUtils.toOPB(this.lits[0]));
      int i = 1;

      while (i < this.lits.length) {
         stb.append(" + ");
         stb.append(LiteralsUtils.toOPB(this.lits[i++]));
      }

      stb.append(" >= ");
      stb.append(this.size() - this.maxUnsatisfied);
      return stb.toString();
   }
}
