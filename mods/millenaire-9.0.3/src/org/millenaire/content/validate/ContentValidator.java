package org.millenaire.content.validate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.millenaire.content.validate.rules.CrossReferenceRules;
import org.millenaire.content.validate.rules.StaticSemanticRules;
import org.millenaire.content.validate.rules.StructuralRules;

public final class ContentValidator {
   public static final String TIER_STATIC = "S/X";
   private final List<ValidationRule> rules = new ArrayList<>();
   private final List<String> tiers = new ArrayList<>();
   private final List<String> skipped = new ArrayList<>();
   private Allowlist allowlist = Allowlist.empty();

   private ContentValidator() {
   }

   public static ContentValidator standard() {
      ContentValidator v = new ContentValidator();
      v.rules.addAll(StructuralRules.all());
      v.rules.addAll(CrossReferenceRules.all());
      v.rules.addAll(StaticSemanticRules.all());
      v.tiers.add("S/X");
      v.allowlist = Allowlist.loadShipped();
      return v;
   }

   public static ContentValidator withShippedAllowlist() {
      ContentValidator v = new ContentValidator();
      v.allowlist = Allowlist.loadShipped();
      return v;
   }

   public static ContentValidator of(List<ValidationRule> rules) {
      ContentValidator v = new ContentValidator();
      v.rules.addAll(rules);
      v.tiers.add("custom");
      return v;
   }

   public ContentValidator withAllowlist(Allowlist allowlist) {
      this.allowlist = allowlist;
      return this;
   }

   public ContentValidator withTier(String name, List<ValidationRule> tierRules) {
      this.rules.addAll(tierRules);
      this.tiers.add(name);
      return this;
   }

   public ContentValidator withSkippedTier(String name, String cause) {
      this.skipped.add(name + " (" + cause + ")");
      return this;
   }

   public ValidationResult run(ContentIndex index, ValidationContext context) {
      long start = System.nanoTime();
      List<Finding> raw = new ArrayList<>();

      for (ValidationRule rule : this.rules) {
         List<Finding> mine = new ArrayList<>();

         try {
            rule.check(index, context, mine);
         } catch (RuntimeException e) {
            mine.add(
               Finding.of(
                  "VALIDATOR",
                  Severity.ERROR,
                  "validator",
                  "rule/" + rule.id(),
                  null,
                  "rule threw " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                  "fix the rule."
               )
            );
         }

         raw.addAll(mine);
      }

      List<Finding> normalised = new ArrayList<>(raw.size());

      for (Finding f : raw) {
         normalised.add(RuleCatalog.normalise(f));
      }

      Set<String> ran = new HashSet<>();

      for (ValidationRule rule : this.rules) {
         ran.addAll(rule.ids());
      }

      Predicate<String> ruleRan = id -> {
         int slash = id.indexOf(47);
         String base = slash > 0 ? id.substring(0, slash) : id;
         return ran.contains(base) || RuleCatalog.get(base) != null && "V".equals(RuleCatalog.get(base).tier());
      };
      List<Finding> finished = new ArrayList<>(this.allowlist.apply(normalised, ruleRan));
      finished.sort(Comparator.comparing(Finding::ruleId).thenComparing(Finding::identity).thenComparing(Finding::message));
      long elapsedMs = (System.nanoTime() - start) / 1000000L;
      Map<String, Long> durations = new LinkedHashMap<>();
      if (!this.tiers.isEmpty()) {
         durations.put(String.join("+", this.tiers), elapsedMs);
      }

      return new ValidationResult(finished, this.tiers, this.skipped, durations);
   }
}
