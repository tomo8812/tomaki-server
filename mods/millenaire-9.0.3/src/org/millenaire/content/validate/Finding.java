package org.millenaire.content.validate;

import java.util.List;

public record Finding(String ruleId, Severity severity, String family, String identity, List<String> origins, String message, String fix, boolean allowlisted) {
   public Finding {
      if (ruleId == null) {
         throw new IllegalArgumentException("ruleId null");
      }

      if (severity == null) {
         throw new IllegalArgumentException("severity null");
      }

      if (identity == null) {
         throw new IllegalArgumentException("identity null");
      }

      origins = origins == null ? List.of() : List.copyOf(origins);
      message = message == null ? "" : message;
      fix = fix == null ? "" : fix;
      family = family == null ? "" : family;
   }

   public static Finding of(String ruleId, Severity severity, String family, String identity, String origin, String message, String fix) {
      return new Finding(ruleId, severity, family, identity, origin == null ? List.of() : List.of(origin), message, fix, false);
   }

   public Finding withSeverity(Severity newSeverity) {
      return new Finding(this.ruleId, newSeverity, this.family, this.identity, this.origins, this.message, this.fix, this.allowlisted);
   }

   public Finding asAllowlisted() {
      return new Finding(this.ruleId, Severity.INFO, this.family, this.identity, this.origins, this.message, this.fix, true);
   }

   public String render() {
      StringBuilder sb = new StringBuilder();
      sb.append('[').append(this.severity).append("] ").append(this.ruleId).append(' ').append(this.identity);
      if (this.allowlisted) {
         sb.append(" (allowlisted)");
      }

      sb.append(" — ").append(this.message);
      if (!this.fix.isEmpty()) {
         sb.append(" Fix: ").append(this.fix);
      }

      if (!this.origins.isEmpty()) {
         sb.append(" [").append(String.join(", ", this.origins)).append(']');
      }

      return sb.toString();
   }
}
