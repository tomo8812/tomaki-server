package org.millenaire.content.validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class ValidationResult {
   private final List<Finding> findings;
   private final List<String> tiersRun;
   private final List<String> tiersSkipped;
   private final Map<String, Long> tierDurationsMs;

   public ValidationResult(List<Finding> findings, List<String> tiersRun, List<String> tiersSkipped) {
      this(findings, tiersRun, tiersSkipped, Map.of());
   }

   public ValidationResult(List<Finding> findings, List<String> tiersRun, List<String> tiersSkipped, Map<String, Long> tierDurationsMs) {
      this.findings = List.copyOf(findings);
      this.tiersRun = List.copyOf(tiersRun);
      this.tiersSkipped = List.copyOf(tiersSkipped);
      this.tierDurationsMs = Collections.unmodifiableMap(new LinkedHashMap<>(tierDurationsMs));
   }

   public static ValidationResult merge(ValidationResult a, ValidationResult b) {
      List<Finding> findings = new ArrayList<>(a.findings);
      findings.addAll(b.findings);
      findings.sort(Comparator.comparing(Finding::ruleId).thenComparing(Finding::identity).thenComparing(Finding::message));
      List<String> run = new ArrayList<>(a.tiersRun);
      run.addAll(b.tiersRun);
      List<String> skipped = new ArrayList<>(a.tiersSkipped);
      skipped.addAll(b.tiersSkipped);
      Map<String, Long> durations = new LinkedHashMap<>(a.tierDurationsMs);
      durations.putAll(b.tierDurationsMs);
      return new ValidationResult(findings, run, skipped, durations);
   }

   public List<Finding> findings() {
      return this.findings;
   }

   public List<Finding> findings(String ruleId) {
      List<Finding> out = new ArrayList<>();

      for (Finding f : this.findings) {
         if (f.ruleId().equals(ruleId) || f.ruleId().startsWith(ruleId + "/")) {
            out.add(f);
         }
      }

      return out;
   }

   public List<Finding> errors() {
      List<Finding> out = new ArrayList<>();

      for (Finding f : this.findings) {
         if (f.severity() == Severity.ERROR) {
            out.add(f);
         }
      }

      return out;
   }

   public boolean hasErrors() {
      return !this.errors().isEmpty();
   }

   public List<String> tiersRun() {
      return this.tiersRun;
   }

   public List<String> tiersSkipped() {
      return this.tiersSkipped;
   }

   public List<String> bookkeepingProblems() {
      List<String> out = new ArrayList<>();

      for (String tier : this.tiersSkipped) {
         out.add("tier NOT RUN: " + tier);
      }

      for (Finding f : this.findings) {
         if (f.severity() == Severity.ERROR) {
            RuleCatalog.Rule rule = RuleCatalog.get(f.ruleId());
            if (rule != null && "V".equals(rule.tier())) {
               out.add(f.render());
            }
         }
      }

      return out;
   }

   public Map<String, Long> tierDurationsMs() {
      return this.tierDurationsMs;
   }

   public String describeTiersRun() {
      List<String> parts = new ArrayList<>();

      for (String tier : this.tiersRun) {
         Long ms = this.tierDurationsMs.get(tier);
         parts.add(ms == null ? tier : tier + " (" + ms + " ms)");
      }

      return String.join(", ", parts);
   }

   public Map<String, Map<Severity, Integer>> countsByRule() {
      Map<String, Map<Severity, Integer>> out = new TreeMap<>();

      for (Finding f : this.findings) {
         String rule = f.ruleId();
         int slash = rule.indexOf(47);
         if (slash > 0) {
            rule = rule.substring(0, slash);
         }

         out.computeIfAbsent(rule, k -> new TreeMap<>()).merge(f.severity(), 1, Integer::sum);
      }

      return out;
   }

   public String render() {
      StringBuilder sb = new StringBuilder();
      sb.append("Content validation — tiers run: ").append(this.describeTiersRun());
      if (!this.tiersSkipped.isEmpty()) {
         sb.append("; NOT RUN: ").append(String.join(", ", this.tiersSkipped));
      }

      sb.append('\n');
      int errors = 0;
      int warns = 0;
      int infos = 0;

      for (Finding f : this.findings) {
         switch (f.severity()) {
            case ERROR:
               errors++;
               break;
            case WARN:
               warns++;
               break;
            default:
               infos++;
         }
      }

      sb.append(String.format("%d finding(s): %d error(s), %d warning(s), %d info%n", this.findings.size(), errors, warns, infos));

      for (Entry<String, Map<Severity, Integer>> e : this.countsByRule().entrySet()) {
         sb.append("  ").append(e.getKey()).append(": ");
         List<String> parts = new ArrayList<>();

         for (Entry<Severity, Integer> s : e.getValue().entrySet()) {
            parts.add(s.getValue() + " " + s.getKey());
         }

         sb.append(String.join(", ", parts)).append('\n');
      }

      sb.append('\n');
      String lastRule = null;

      for (Finding f : this.findings) {
         if (!f.ruleId().equals(lastRule)) {
            RuleCatalog.Rule rule = RuleCatalog.get(f.ruleId());
            sb.append("== ").append(f.ruleId());
            if (rule != null) {
               sb.append(" — ").append(rule.title());
            }

            sb.append('\n');
            lastRule = f.ruleId();
         }

         sb.append(f.render()).append('\n');
      }

      return sb.toString();
   }
}
