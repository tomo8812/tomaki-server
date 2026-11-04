package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ba {
   private final aK a;

   public ba(aK var1) {
      this.a = var1;
   }

   public final void a(List<ObjectNode> var1) {
      String var2 = this.a.a(aP.TAG_ALLOF);
      this.a(var1, var2x -> this.a(var2x, var2));
   }

   public final void b(List<ObjectNode> var1) {
      String var2 = this.a.a(aP.TAG_ANYOF);
      this.a(var1, var2x -> this.b(var2x, var2));
   }

   private Set<String> a() {
      return Stream.of(aP.TAG_ADDITIONAL_PROPERTIES, aP.TAG_ITEMS).map(var1 -> var1.a(var0)).collect(Collectors.toSet());
   }

   private Set<String> b() {
      return Stream.of(aP.TAG_ALLOF, aP.TAG_ANYOF, aP.TAG_ONEOF).map(var1 -> var1.a(var0)).collect(Collectors.toSet());
   }

   private Set<String> c() {
      return Stream.of(aP.TAG_PATTERN_PROPERTIES, aP.TAG_PROPERTIES).map(var1 -> var1.a(var0)).collect(Collectors.toSet());
   }

   private void a(List<ObjectNode> var1, Consumer<ObjectNode> var2) {
      ArrayList var9;
      Consumer var3 = var1x -> {
         if (var1x instanceof ObjectNode) {
            var0.add((ObjectNode)var1x);
         }
      };
      Set var4 = this.a();
      Set var5 = this.b();
      Set var6 = this.c();

      do {
         ArrayList var7 = new ArrayList(var9);
         var9.clear();

         for (ObjectNode var8 : var7) {
            var2.accept(var8);
            var4.stream().map(var8::get).forEach(var3);
            var5.stream().map(var8::get).filter(var0 -> var0 instanceof ArrayNode).forEach(var1x -> var1x.forEach(var3));
            var6.stream().map(var8::get).filter(var0 -> var0 instanceof ObjectNode).forEach(var1x -> var1x.forEach(var3));
         }
      } while (!var9.isEmpty());
   }

   private void a(JsonNode var1, String var2) {
      if (var1 instanceof ObjectNode) {
         JsonNode var3;
         if ((var3 = var1.get(var2)) instanceof ArrayNode) {
            var3.forEach(var2x -> this.a(var2x, var2));
            ArrayList var4 = new ArrayList();
            var3.forEach(var4::add);
            if (!var4.stream().anyMatch(var0 -> !(var0 instanceof ObjectNode) && !var0.asBoolean())) {
               List var8 = var4.stream().filter(var0 -> var0 instanceof ObjectNode).map(var0 -> (ObjectNode)var0).collect(Collectors.toList());
               Map var9 = Stream.concat(Stream.of(var1), var8.stream())
                  .flatMap(var0 -> StreamSupport.stream((() -> var0.fields()).spliterator(), false))
                  .collect(Collectors.groupingBy(Entry::getKey, Collectors.mapping(Entry::getValue, Collectors.toList())));
               if (this.a.a() != aQ.DRAFT_6 && this.a.a() != aQ.DRAFT_7 || !var9.containsKey(this.a.a(aP.TAG_REF))) {
                  String var5 = this.a.a(aP.TAG_IF);
                  Iterator var10 = var9.entrySet().iterator();

                  while (var10.hasNext()) {
                     Entry var6;
                     if (((List)(var6 = (Entry)var10.next()).getValue()).size() != 1) {
                        if (var5.equals(var6.getKey())) {
                           return;
                        }

                        byte var7;
                        if (!var2.equals(var6.getKey())) {
                           var7 = 0;
                        } else {
                           if (((List)var6.getValue()).size() == 2) {
                              continue;
                           }

                           var7 = 1;
                        }

                        if (!((List)var6.getValue()).stream().skip(var7 + 1).allMatch(((JsonNode)((List)var6.getValue()).get(var7))::equals)) {
                           return;
                        }
                     }
                  }

                  ObjectNode var11;
                  (var11 = (ObjectNode)var1).remove(var2);
                  var8.forEach(var11::setAll);
               }
            }
         }
      }
   }

   private void b(JsonNode var1, String var2) {
      if (var1 instanceof ObjectNode) {
         if ((var1 = var1.get(var2)) instanceof ArrayNode) {
            var1.forEach(var2x -> this.b(var2x, var2));

            for (int var3 = var1.size() - 1; var3 >= 0; var3--) {
               JsonNode var4;
               if ((var4 = var1.get(var3)) instanceof ObjectNode && var4.size() == 1 && (var4 = var4.get(var2)) instanceof ArrayNode) {
                  ((ArrayNode)var1).remove(var3);

                  for (int var5 = var4.size() - 1; var5 >= 0; var5--) {
                     ((ArrayNode)var1).insert(var3, var4.get(var5));
                  }
               }
            }
         }
      }
   }

   public final String a(String var1) {
      return var1.replaceAll("\\[\\]", "*").replaceAll("<", "(").replaceAll(">", ")").replaceAll("[^a-zA-Z0-9\\.\\-_\\$\\*\\(\\),]+", "");
   }

   public final String b(String var1) {
      return var1.replaceAll("\\$", "-").replaceAll("\\[\\]", "...").replaceAll("[<>]", "_").replaceAll(",", ".").replaceAll("[^a-zA-Z0-9\\.\\-_]+", "");
   }
}
