package org.millenaire.content.validate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Allowlist {
   public static final String RESOURCE = "/content/validate/allowlist.json";
   private final List<Allowlist.AllowEntry> entries;
   private final List<Allowlist.AllowEntry> refused;

   private Allowlist(List<Allowlist.AllowEntry> entries, List<Allowlist.AllowEntry> refused) {
      this.entries = List.copyOf(entries);
      this.refused = List.copyOf(refused);
   }

   public static Allowlist empty() {
      return new Allowlist(List.of(), List.of());
   }

   public List<Allowlist.AllowEntry> entries() {
      return this.entries;
   }

   public static Allowlist loadShipped() {
      try (InputStream in = Allowlist.class.getResourceAsStream("/content/validate/allowlist.json")) {
         if (in == null) {
            return empty();
         }

         JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
         return parse(root);
      } catch (Exception e) {
         throw new IllegalStateException("allowlist does not load: " + e.getMessage(), e);
      }
   }

   public static Allowlist load(Path file) {
      try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
         return parse(JsonParser.parseReader(reader).getAsJsonObject());
      } catch (Exception e) {
         throw new IllegalStateException("allowlist " + file + " does not load: " + e.getMessage(), e);
      }
   }

   public List<Allowlist.AllowEntry> refused() {
      return this.refused;
   }

   public static Allowlist parse(JsonObject root) {
      List<Allowlist.AllowEntry> out = new ArrayList<>();
      List<Allowlist.AllowEntry> refused = new ArrayList<>();

      for (JsonObject e : Json.objects(root, "entries")) {
         String ruleId = Json.string(e, "ruleId");
         String identity = Json.string(e, "identity");
         String pattern = Json.string(e, "identityPattern");
         String reason = Json.string(e, "reason");
         if (ruleId == null || reason == null || reason.isBlank() || identity == null == (pattern == null)) {
            throw new IllegalStateException("allowlist entry needs ruleId, a reason and exactly one of identity / identityPattern: " + e);
         }

         Allowlist.AllowEntry entry = new Allowlist.AllowEntry(
            ruleId, identity, pattern == null ? null : Pattern.compile(pattern), Json.string(e, "origin"), Json.string(e, "messageContains"), reason
         );
         RuleCatalog.Rule rule = RuleCatalog.get(ruleId);
         if (rule != null && "V".equals(rule.tier())) {
            refused.add(entry);
         } else {
            out.add(entry);
         }
      }

      return new Allowlist(out, refused);
   }

   public List<Finding> apply(List<Finding> findings, Predicate<String> ruleRan) {
      List<Finding> out = new ArrayList<>(findings.size());
      boolean[] used = new boolean[this.entries.size()];

      for (Finding f : findings) {
         Finding result = f;

         for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i).matches(f)) {
               used[i] = true;
               result = f.asAllowlisted();
               break;
            }
         }

         out.add(result);
      }

      for (Allowlist.AllowEntry entry : this.refused) {
         out.add(
            Finding.of(
               "ALLOWLIST",
               Severity.ERROR,
               "allowlist",
               entry.identity() != null ? entry.identity() : entry.identityPattern().pattern(),
               "/content/validate/allowlist.json",
               "allowlist entry '"
                  + entry.describe()
                  + "' targets a validator bookkeeping rule (tier V) and was refused at load: the list cannot switch off the check that guards it",
               "fix what the rule reports — a stale entry, a contract that does not load, a rule that threw."
            )
         );
      }

      for (int i = 0; i < this.entries.size(); i++) {
         if (!used[i] && ruleRan.test(this.entries.get(i).ruleId())) {
            Allowlist.AllowEntry stale = this.entries.get(i);
            out.add(
               Finding.of(
                  "ALLOWLIST",
                  Severity.ERROR,
                  "allowlist",
                  stale.identity() != null ? stale.identity() : stale.identityPattern().pattern(),
                  "/content/validate/allowlist.json",
                  "allowlist entry '" + stale.describe() + "' matches no finding any more",
                  "remove the entry (the defect is fixed) or correct its identity/rule."
               )
            );
         }
      }

      return out;
   }

   public record AllowEntry(String ruleId, String identity, Pattern identityPattern, String origin, String messageContains, String reason) {
      boolean matches(Finding f) {
         if (!this.ruleId.equals(f.ruleId())) {
            return false;
         } else if (this.identity != null && !this.identity.equals(f.identity())) {
            return false;
         } else if (this.identityPattern != null && !this.identityPattern.matcher(f.identity()).matches()) {
            return false;
         } else {
            return this.origin != null && f.origins().stream().noneMatch(o -> o.equals(this.origin) || o.startsWith(this.origin + " ("))
               ? false
               : this.messageContains == null || f.message().contains(this.messageContains);
         }
      }

      String describe() {
         return this.ruleId
            + " "
            + (this.identity != null ? this.identity : "/" + this.identityPattern.pattern() + "/")
            + (this.messageContains != null ? " ~'" + this.messageContains + "'" : "")
            + (this.origin != null ? " @" + this.origin : "");
      }
   }
}
