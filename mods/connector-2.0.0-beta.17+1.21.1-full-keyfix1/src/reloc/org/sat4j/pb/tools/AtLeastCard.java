package reloc.org.sat4j.pb.tools;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class AtLeastCard {
   private final IVecInt lits;
   private final int degree;

   public AtLeastCard(IVecInt atLeastLits, int degree) {
      this.lits = new VecInt(atLeastLits.size());
      atLeastLits.copyTo(this.lits);
      this.degree = degree;
   }

   public AtLeastCard(BitSet atLeastLits, int degree, int offset) {
      this.lits = new VecInt(atLeastLits.cardinality());
      int from = 0;

      int cur;
      while ((cur = atLeastLits.nextSetBit(from)) != -1) {
         this.lits.push(cur + offset);
         from = cur + 1;
      }

      this.degree = degree;
   }

   public IVecInt getLits() {
      return this.lits;
   }

   public int getDegree() {
      return this.degree;
   }

   public AtMostCard toAtMost() {
      IVecInt atMostLits = new VecInt(this.lits.size());
      IteratorInt it = this.lits.iterator();

      while (it.hasNext()) {
         atMostLits.push(-it.next());
      }

      int atMostDegree = this.lits.size() - this.degree;
      return new AtMostCard(atMostLits, atMostDegree);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      IteratorInt it = this.lits.iterator();

      while (it.hasNext()) {
         sb.append(it.next());
         sb.append(" + ");
      }

      sb.delete(sb.length() - 2, sb.length());
      sb.append(">= ");
      sb.append(this.degree);
      return sb.toString();
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + this.degree;
      Set<Integer> litsSet = new HashSet<>();
      IteratorInt it = this.lits.iterator();

      while (it.hasNext()) {
         litsSet.add(it.next());
      }

      return 31 * result + litsSet.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      AtLeastCard other = (AtLeastCard)obj;
      if (this.degree != other.degree) {
         return false;
      }

      if (this.lits == null) {
         return other.lits == null;
      }

      Set<Integer> litsSet1 = new HashSet<>();
      IteratorInt it = this.lits.iterator();

      while (it.hasNext()) {
         litsSet1.add(it.next());
      }

      Set<Integer> litsSet2 = new HashSet<>();
      IteratorInt itx = other.lits.iterator();

      while (itx.hasNext()) {
         litsSet2.add(itx.next());
      }

      return litsSet1.equals(litsSet2);
   }
}
