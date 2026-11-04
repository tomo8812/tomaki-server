package org.millenaire.content.validate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.millenaire.goal.BuiltInGoals;
import org.millenaire.goal.gathering.BuiltInGatheringHandlers;

public final class ValidationContext {
   public static final String EN_US = "/assets/millenaire/lang/en_us.json";
   private final Set<String> engineGoalIds;
   private final Set<String> handlerIds;
   private final Set<String> enUsKeys;
   private final Map<String, FieldContract> contracts = new HashMap<>();
   private final Map<String, String> contractErrors = new HashMap<>();

   private ValidationContext(Set<String> engineGoalIds, Set<String> handlerIds, Set<String> enUsKeys) {
      this.engineGoalIds = Set.copyOf(engineGoalIds);
      this.handlerIds = Set.copyOf(handlerIds);
      this.enUsKeys = Set.copyOf(enUsKeys);
   }

   public static ValidationContext shipped() {
      ValidationContext ctx = new ValidationContext(new TreeSet<>(BuiltInGoals.IDS), new TreeSet<>(BuiltInGatheringHandlers.IDS), loadEnUsKeys());

      for (ContentIndex.Family family : ContentIndex.Family.values()) {
         try {
            FieldContract c = FieldContract.load(family.label());
            if (c != null) {
               ctx.contracts.put(family.label(), c);
            }
         } catch (RuntimeException e) {
            ctx.contractErrors.put(family.label(), e.getMessage());
         }
      }

      return ctx;
   }

   public static ValidationContext of(Set<String> engineGoalIds, Set<String> handlerIds, Set<String> enUsKeys, Map<String, FieldContract> contracts) {
      return of(engineGoalIds, handlerIds, enUsKeys, contracts, Map.of());
   }

   public static ValidationContext of(
      Set<String> engineGoalIds, Set<String> handlerIds, Set<String> enUsKeys, Map<String, FieldContract> contracts, Map<String, String> contractErrors
   ) {
      ValidationContext ctx = new ValidationContext(engineGoalIds, handlerIds, enUsKeys);
      ctx.contracts.putAll(contracts);
      ctx.contractErrors.putAll(contractErrors);
      return ctx;
   }

   private static Set<String> loadEnUsKeys() {
      Set<String> keys = new TreeSet<>();

      try (InputStream in = ValidationContext.class.getResourceAsStream("/assets/millenaire/lang/en_us.json")) {
         if (in == null) {
            return keys;
         }

         JsonElement el = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8));
         if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();
            keys.addAll(obj.keySet());
         }
      } catch (Exception var6) {
      }

      return keys;
   }

   public boolean isEngineGoal(String id) {
      return this.engineGoalIds.contains(id);
   }

   public Set<String> engineGoalIds() {
      return this.engineGoalIds;
   }

   public boolean isHandler(String id) {
      return this.handlerIds.contains(id);
   }

   public boolean hasEnUs() {
      return !this.enUsKeys.isEmpty();
   }

   public boolean hasKey(String key) {
      return this.enUsKeys.contains(key);
   }

   public FieldContract contract(String family) {
      return this.contracts.get(family);
   }

   public Map<String, String> contractErrors() {
      return Map.copyOf(this.contractErrors);
   }
}
