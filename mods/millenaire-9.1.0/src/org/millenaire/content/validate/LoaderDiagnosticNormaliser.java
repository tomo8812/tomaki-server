package org.millenaire.content.validate;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LoaderDiagnosticNormaliser {
   private static final List<LoaderDiagnosticNormaliser.Mapping> TABLE = List.of(
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Building plan (\\S+) — filename stem '\\S+' does not match building_id"), "S-02"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Plan set '(\\S+)' for culture '\\S+' dropped because NBT"), "S-06"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Ignoring \\w+ at unexpected depth: (\\S+)"), "S-03"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Ignoring \\w+ in sub-directory: (\\S+)"), "S-03"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("\\w+ '(\\S+)' at '\\S+' \\(.*\\) shadowed by"), "S-08"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Duplicate (?:building_id|villager_type id|village_type id) '(\\S+)'"), "S-08"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Duplicate (?:gathering type|external visit goal) id '(\\S+)'"), "S-08"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Traded good '(\\S+)' already declared"), "S-08"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Variant fragment '(\\S+)'"), "S-09"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("'(\\S+)' is named like a variant fragment"), "S-09"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("VillagerType (\\S+): missing goal in GoalRegistry"), "X-01"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Unknown goal ignored: (\\S+)"), "X-01"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("VillageType (\\S+): (?:layout slot|playerBuilding|hamlets) references non-existent"), "X-03"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("VillageType (\\S+) references nameList"), "X-03"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("BuildingPlan (\\S+): shopId '\\S+' has no loaded ShopProfile"), "X-04"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Unknown handler '\\S+' for gathering type (\\S+)"), "X-05"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("BuildingPlanSet (\\S+): unknown parent building plan"), "X-10"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("BuildingPlanSet (\\S+): unknown level or variant for parent"), "X-10"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("BuildingPlanSet (\\S+) is a sub-building but has no parent_building"), "X-10"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("BuildingPlanSet (\\S+): parent building plan \\S+ does not list this sub-building"), "X-10"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("BuildingPlanSet (\\S+): footprint of parent building plan"), "X-10"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Building plan (\\S+) has a malformed parent_building"), "X-10"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("\\[I18nKeyAuditor\\]"), "X-09"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Gathering type (\\S+) \\(handler=\\S+\\):"), "M-01"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Gathering types reference \\d+ unresolvable id"), "M-01"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("validation error\\(s\\) found in gathering types"), "M-01"),
      new LoaderDiagnosticNormaliser.Mapping(Pattern.compile("Unknown mock block: (\\S+)"), "M-02")
   );

   private LoaderDiagnosticNormaliser() {
   }

   public static LoaderDiagnosticNormaliser.Normalised normalise(String message) {
      if (message == null) {
         return new LoaderDiagnosticNormaliser.Normalised("M-04/uncategorised", null);
      }

      for (LoaderDiagnosticNormaliser.Mapping m : TABLE) {
         Matcher matcher = m.pattern().matcher(message);
         if (matcher.find()) {
            String subject = matcher.groupCount() >= 1 ? matcher.group(1) : null;
            return new LoaderDiagnosticNormaliser.Normalised(m.ruleId(), subject);
         }
      }

      return new LoaderDiagnosticNormaliser.Normalised("M-04/uncategorised", null);
   }

   public static boolean covers(List<Finding> staticFindings, String ruleId, String subject, Collection<String> cultures) {
      if (subject == null) {
         return false;
      }

      String s = normaliseSubject(subject);
      String culture = cultureIn(s, cultures);
      String tail = s.substring(s.lastIndexOf(47) + 1);

      for (Finding f : staticFindings) {
         if (f.ruleId().equals(ruleId)) {
            String identity = f.identity().toLowerCase(Locale.ROOT);
            if (identity.equals(s)) {
               return true;
            }

            boolean sameTail = identity.endsWith("/" + tail) || identity.endsWith(":" + tail) || identity.equals(tail);
            if (sameTail) {
               String identityCulture = cultureIn(identity, cultures);
               if (culture == null ? identityCulture == null : culture.equals(identityCulture)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static String cultureIn(String path, Collection<String> cultures) {
      for (String segment : path.split("/")) {
         if (cultures.contains(segment)) {
            return segment;
         }
      }

      return null;
   }

   static String normaliseSubject(String subject) {
      String s = subject.toLowerCase(Locale.ROOT);
      if (s.endsWith("'") || s.endsWith(":") || s.endsWith(",")) {
         s = s.substring(0, s.length() - 1);
      }

      if (s.endsWith(".json")) {
         s = s.substring(0, s.length() - 5);
      }

      if (s.startsWith("cultures/")) {
         s = s.substring("cultures/".length());
      }

      int colon = s.indexOf(58);
      int slash = s.indexOf(47);
      if (colon >= 0 && (slash < 0 || colon < slash)) {
         s = s.substring(colon + 1);
      }

      return s;
   }

   private record Mapping(Pattern pattern, String ruleId) {
   }

   public record Normalised(String ruleId, String subject) {
   }
}
