package org.millenaire.content.validate;

public enum Severity {
   INFO,
   WARN,
   ERROR;

   public Severity capAt(Severity cap) {
      return this.compareTo(cap) > 0 ? cap : this;
   }

   // $VF: synthetic method
   private static Severity[] $values() {
      return new Severity[]{INFO, WARN, ERROR};
   }
}
