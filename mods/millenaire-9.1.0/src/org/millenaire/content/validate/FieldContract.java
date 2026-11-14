package org.millenaire.content.validate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

public final class FieldContract {
   public static final String CONTRACT_DIR = "/content/validate/contract/";
   private static final Set<String> LEGAL_TYPES = new TreeSet<>(
      List.of("any", "array", "boolean", "int", "number", "object", "string", "string_or_array", "string_or_int")
   );
   private static final Set<String> LEGAL_ATTRIBUTES = new TreeSet<>(
      List.of("path", "type", "required", "requiredUnless", "requiredWhen", "nullable", "aliases", "aliasTypes", "open", "deprecated", "ignored")
   );
   private static final Set<String> LEGAL_CONDITIONS = new TreeSet<>(List.of("absent", "present", "equals"));
   private final String family;
   private final FieldContract.Node rootNode = new FieldContract.Node("", false);
   private final Set<String> allKeys = new TreeSet<>();
   private final Set<String> ignoredKeys = new TreeSet<>();

   private FieldContract(String family) {
      this.family = family;
   }

   public String family() {
      return this.family;
   }

   public Set<String> keyNames() {
      Set<String> out = new TreeSet<>(this.allKeys);
      out.removeAll(this.ignoredKeys);
      return out;
   }

   public Set<String> ignoredKeyNames() {
      return Set.copyOf(this.ignoredKeys);
   }

   public static FieldContract load(String family) {
      try (InputStream in = FieldContract.class.getResourceAsStream("/content/validate/contract/" + family + ".json")) {
         if (in == null) {
            return null;
         }

         JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
         return parse(family, root);
      } catch (Exception e) {
         throw new IllegalStateException("Field contract for '" + family + "' does not load: " + e.getMessage(), e);
      }
   }

   static FieldContract parse(String family, JsonObject root) {
      FieldContract c = new FieldContract(family);

      for (JsonObject field : Json.objects(root, "fields")) {
         String path = Json.string(field, "path");
         if (path == null || path.isEmpty()) {
            throw new IllegalStateException("contract " + family + ": field without path");
         }

         for (String attribute : field.keySet()) {
            if (!LEGAL_ATTRIBUTES.contains(attribute)) {
               throw new IllegalStateException(
                  "contract "
                     + family
                     + ": field '"
                     + path
                     + "' declares unknown attribute '"
                     + attribute
                     + "'; accepted attributes are "
                     + String.join(", ", LEGAL_ATTRIBUTES)
               );
            }
         }

         FieldContract.Node node = c.rootNode;

         for (String segment : path.split("\\.")) {
            node = node.child(segment);
            c.allKeys.add(node.name);
         }

         node.declared = true;
         node.type = requireLegalType(family, path, "type", Json.string(field, "type", node.arrayOfObjects ? "array" : "any"));
         node.required = Json.bool(field, "required", false);
         node.requiredUnless = Json.string(field, "requiredUnless");
         node.requiredWhen = parseCondition(family, path, Json.object(field, "requiredWhen"));
         node.nullable = Json.bool(field, "nullable", false);
         node.aliases = Json.strings(field, "aliases");
         c.allKeys.addAll(node.aliases);
         JsonObject aliasTypes = Json.object(field, "aliasTypes");
         if (aliasTypes != null) {
            Map<String, String> types = new LinkedHashMap<>();

            for (String alias : aliasTypes.keySet()) {
               if (!node.aliases.contains(alias)) {
                  throw new IllegalStateException("contract " + family + ": aliasTypes names '" + alias + "' which is not an alias of '" + path + "'");
               }

               types.put(alias, requireLegalType(family, path, "aliasTypes." + alias, Json.string(aliasTypes, alias, node.type)));
            }

            node.aliasTypes = types;
         }

         node.open = Json.bool(field, "open", false);
         node.deprecated = Json.bool(field, "deprecated", false);
         node.ignored = Json.bool(field, "ignored", false);
         if (node.ignored) {
            c.ignoredKeys.add(node.name);
         }
      }

      c.allKeys.remove("*");
      checkSiblingReferences(family, c.rootNode, "");
      return c;
   }

   private static FieldContract.Condition parseCondition(String family, String path, JsonObject clause) {
      if (clause == null) {
         return null;
      }

      if (clause.size() == 1 && LEGAL_CONDITIONS.contains(clause.keySet().iterator().next())) {
         String kind = (String)clause.keySet().iterator().next();
         if (!"equals".equals(kind)) {
            String key = Json.string(clause, kind);
            if (key != null && !key.isEmpty()) {
               return new FieldContract.Condition(kind, key, null);
            } else {
               throw new IllegalStateException("contract " + family + ": field '" + path + "' declares requiredWhen." + kind + " without a sibling key name");
            }
         } else {
            JsonObject equals = Json.object(clause, "equals");
            if (equals != null && equals.size() == 1 && ((JsonElement)((Entry)equals.entrySet().iterator().next()).getValue()).isJsonPrimitive()) {
               Entry<String, JsonElement> e = (Entry<String, JsonElement>)equals.entrySet().iterator().next();
               return new FieldContract.Condition("equals", e.getKey(), e.getValue());
            } else {
               throw new IllegalStateException(
                  "contract " + family + ": field '" + path + "' declares requiredWhen.equals; it takes exactly one sibling key mapped to a primitive"
               );
            }
         }
      } else {
         throw new IllegalStateException(
            "contract "
               + family
               + ": field '"
               + path
               + "' declares a requiredWhen with "
               + clause.keySet()
               + "; it takes exactly one of "
               + String.join(", ", LEGAL_CONDITIONS)
         );
      }
   }

   private static void checkSiblingReferences(String family, FieldContract.Node node, String path) {
      for (FieldContract.Node child : node.children.values()) {
         String childPath = path.isEmpty() ? child.name : path + "." + child.name;
         requireSibling(family, node, childPath, "requiredUnless", child.requiredUnless);
         if (child.requiredWhen != null) {
            requireSibling(family, node, childPath, "requiredWhen", child.requiredWhen.key());
         }

         checkSiblingReferences(family, child, childPath);
      }
   }

   private static void requireSibling(String family, FieldContract.Node parent, String path, String attribute, String sibling) {
      if (sibling != null && !parent.children.containsKey(sibling)) {
         throw new IllegalStateException(
            "contract "
               + family
               + ": field '"
               + path
               + "' declares "
               + attribute
               + " on '"
               + sibling
               + "', which the contract does not declare as a sibling key"
         );
      }
   }

   private static String requireLegalType(String family, String path, String attribute, String type) {
      if (!LEGAL_TYPES.contains(type)) {
         throw new IllegalStateException(
            "contract "
               + family
               + ": field '"
               + path
               + "' declares "
               + attribute
               + " '"
               + type
               + "' which is not a known type; accepted types are "
               + String.join(", ", LEGAL_TYPES)
         );
      } else {
         return type;
      }
   }

   public List<FieldContract.Problem> check(JsonObject json) {
      List<FieldContract.Problem> out = new ArrayList<>();
      if (json != null) {
         this.checkObject(json, this.rootNode, "", out);
      }

      return out;
   }

   private void checkObject(JsonObject obj, FieldContract.Node node, String path, List<FieldContract.Problem> out) {
      FieldContract.Node star = node.children.get("*");
      Set<String> consumed = new HashSet<>();
      Iterator var7 = node.children.values().iterator();

      while (true) {
         FieldContract.Node child;
         String presentKey;
         String childPath;
         JsonElement value;
         while (true) {
            if (!var7.hasNext()) {
               if (star != null) {
                  for (Entry<String, JsonElement> e : obj.entrySet()) {
                     if (!consumed.contains(e.getKey())) {
                        consumed.add(e.getKey());
                        presentKey = (path.isEmpty() ? "" : path + ".") + e.getKey();
                        JsonElement valuex = e.getValue();
                        if (valuex != null && !valuex.isJsonNull()) {
                           if (!typeMatches(star.type, valuex)) {
                              out.add(
                                 new FieldContract.Problem(
                                    "S-04", presentKey, "field '" + presentKey + "' should be " + star.type + " but is " + describe(valuex)
                                 )
                              );
                           } else {
                              this.descend(valuex, star, presentKey, out);
                           }
                        }
                     }
                  }

                  return;
               }

               if (node.open) {
                  return;
               }

               for (String key : obj.keySet()) {
                  if (!consumed.contains(key)) {
                     presentKey = path.isEmpty() ? key : path + "." + key;
                     out.add(new FieldContract.Problem("S-05", presentKey, "unknown field '" + presentKey + "' — the loader ignores it" + suggest(key, node)));
                  }
               }

               return;
            }

            child = (FieldContract.Node)var7.next();
            if (child != star) {
               presentKey = null;
               if (obj.has(child.name)) {
                  presentKey = child.name;
               } else {
                  for (String alias : child.aliases) {
                     if (obj.has(alias)) {
                        presentKey = alias;
                        break;
                     }
                  }
               }

               childPath = path.isEmpty() ? child.name : path + "." + child.name;
               value = presentKey == null ? null : obj.get(presentKey);
               if (presentKey != null && value != null && !value.isJsonNull()) {
                  break;
               }

               boolean required = (child.required || child.requiredWhen != null && child.requiredWhen.holds(obj))
                  && (child.requiredUnless == null || !Json.bool(obj, child.requiredUnless, false));
               if (required) {
                  out.add(
                     new FieldContract.Problem(
                        "S-04",
                        childPath,
                        "required field '"
                           + childPath
                           + (presentKey == null ? "' is missing" : "' is null")
                           + (child.requiredWhen == null ? "" : " when " + child.requiredWhen.describe())
                           + (child.aliases.isEmpty() ? "" : " (aliases: " + String.join(", ", child.aliases) + ")")
                     )
                  );
               } else if (presentKey != null && !child.nullable) {
                  out.add(
                     new FieldContract.Problem(
                        "S-04", childPath, "field '" + childPath + "' is null; the loader accepts the key being absent but throws on an explicit null"
                     )
                  );
               }

               if (presentKey != null) {
                  break;
               }
            }
         }

         consumed.add(presentKey);

         for (String alias : child.aliases) {
            consumed.add(alias);
         }

         consumed.add(child.name);
         if (value != null && !value.isJsonNull()) {
            String expectedType = child.aliasTypes.getOrDefault(presentKey, child.type);
            if (!typeMatches(expectedType, value)) {
               String presentPath = path.isEmpty() ? presentKey : path + "." + presentKey;
               out.add(new FieldContract.Problem("S-04", childPath, "field '" + presentPath + "' should be " + expectedType + " but is " + describe(value)));
            } else {
               this.descend(value, child, childPath, out);
            }
         }
      }
   }

   private void descend(JsonElement value, FieldContract.Node child, String childPath, List<FieldContract.Problem> out) {
      if (child.arrayOfObjects && value.isJsonArray()) {
         JsonArray arr = value.getAsJsonArray();

         for (int i = 0; i < arr.size(); i++) {
            JsonElement el = arr.get(i);
            if (el.isJsonObject()) {
               this.checkObject(el.getAsJsonObject(), child, childPath + "[" + i + "]", out);
            } else if (!child.children.isEmpty()) {
               out.add(
                  new FieldContract.Problem("S-04", childPath + "[" + i + "]", "element of '" + childPath + "' should be an object but is " + describe(el))
               );
            }
         }
      } else if (value.isJsonObject() && !child.open && !child.children.isEmpty()) {
         this.checkObject(value.getAsJsonObject(), child, childPath, out);
      }
   }

   private static String suggest(String key, FieldContract.Node node) {
      String best = null;
      int bestDist = Integer.MAX_VALUE;

      for (FieldContract.Node child : node.children.values()) {
         List<String> names = new ArrayList<>(child.aliases);
         names.add(child.name);

         for (String name : names) {
            int d = levenshtein(key, name);
            if (d < bestDist) {
               bestDist = d;
               best = name;
            }
         }
      }

      return best != null && bestDist <= 3 && bestDist > 0 ? " (did you mean '" + best + "'?)" : "";
   }

   private static boolean typeMatches(String type, JsonElement value) {
      return switch (type) {
         case "string" -> value.isJsonPrimitive();
         case "int", "number" -> value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber();
         case "boolean" -> value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean();
         case "array" -> value.isJsonArray();
         case "object" -> value.isJsonObject();
         case "string_or_array" -> value.isJsonArray() || value.isJsonPrimitive();
         case "string_or_int" -> value.isJsonPrimitive();
         default -> true;
      };
   }

   private static String describe(JsonElement value) {
      if (value.isJsonArray()) {
         return "an array";
      } else if (value.isJsonObject()) {
         return "an object";
      } else if (value.isJsonNull()) {
         return "null";
      } else {
         JsonPrimitive p = value.getAsJsonPrimitive();
         if (p.isString()) {
            return "a string";
         } else {
            return p.isBoolean() ? "a boolean" : "a number";
         }
      }
   }

   private static int levenshtein(String a, String b) {
      int[] prev = new int[b.length() + 1];
      int[] cur = new int[b.length() + 1];
      int j = 0;

      while (j <= b.length()) {
         prev[j] = j++;
      }

      for (int i = 1; i <= a.length(); i++) {
         cur[0] = i;

         for (int jx = 1; jx <= b.length(); jx++) {
            int cost = a.charAt(i - 1) == b.charAt(jx - 1) ? 0 : 1;
            cur[jx] = Math.min(Math.min(cur[jx - 1] + 1, prev[jx] + 1), prev[jx - 1] + cost);
         }

         int[] t = prev;
         prev = cur;
         cur = t;
      }

      return prev[b.length()];
   }

   record Condition(String kind, String key, JsonElement value) {
      boolean holds(JsonObject siblings) {
         JsonElement sibling = siblings.get(this.key);
         boolean present = sibling != null && !sibling.isJsonNull();

         return switch (this.kind) {
            case "absent" -> !present;
            case "present" -> present;
            case "equals" -> present && sibling.equals(this.value);
            default -> false;
         };
      }

      String describe() {
         return switch (this.kind) {
            case "absent" -> "'" + this.key + "' is absent";
            case "present" -> "'" + this.key + "' is present";
            default -> "'" + this.key + "' is " + this.value;
         };
      }
   }

   public static final class Node {
      final String name;
      String type = "any";
      boolean required;
      String requiredUnless;
      FieldContract.Condition requiredWhen;
      boolean nullable;
      List<String> aliases = List.of();
      Map<String, String> aliasTypes = Map.of();
      boolean open;
      boolean deprecated;
      boolean ignored;
      boolean declared;
      final boolean arrayOfObjects;
      final Map<String, FieldContract.Node> children = new LinkedHashMap<>();

      Node(String name, boolean arrayOfObjects) {
         this.name = name;
         this.arrayOfObjects = arrayOfObjects;
      }

      FieldContract.Node child(String segment) {
         boolean arr = segment.endsWith("[]");
         String key = arr ? segment.substring(0, segment.length() - 2) : segment;
         FieldContract.Node n = this.children.get(key);
         if (n == null) {
            n = new FieldContract.Node(key, arr);
            n.type = arr ? "array" : "object";
            this.children.put(key, n);
         }

         return n;
      }
   }

   public record Problem(String ruleId, String path, String message) {
   }
}
