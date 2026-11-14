package org.millenaire.culture;

public record ReputationLabel(int threshold, String key) implements Comparable<ReputationLabel> {
   public int compareTo(ReputationLabel o) {
      return Integer.compare(this.threshold, o.threshold);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compareTo(Object param1) {
      return this.compareTo((ReputationLabel)var1);
   }
}
