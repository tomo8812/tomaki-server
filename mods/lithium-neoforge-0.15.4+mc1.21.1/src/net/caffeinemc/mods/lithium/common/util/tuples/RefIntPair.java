package net.caffeinemc.mods.lithium.common.util.tuples;

import java.util.Objects;

public record RefIntPair<A>(A left, int right) {
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj.getClass() == this.getClass()) {
         RefIntPair<?> that = (RefIntPair<?>)obj;
         return this.left == that.left && this.right == that.right;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(System.identityHashCode(this.left), this.right);
   }

   @Override
   public String toString() {
      return "RefIntPair[left=" + this.left + ", right=" + this.right + "]";
   }
}
