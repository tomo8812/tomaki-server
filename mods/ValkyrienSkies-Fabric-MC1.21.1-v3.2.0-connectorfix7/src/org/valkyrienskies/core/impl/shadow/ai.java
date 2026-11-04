package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ai {
   public final List<ad> a = new ArrayList<>();
   public final EnumSet<ae> b;

   public ai(EnumSet<ae> var1) {
      this.b = var1.clone();
   }

   public static JsonNode a(JsonNode var0, JsonNode var1) {
      EnumSet var2 = ae.a();
      var1 = var1;
      var0 = var0;
      ai var3 = new ai(var2);
      if (var0 == null && var1 != null) {
         var3.a.add(ad.a(aq.ADD, am.b, var1));
      }

      if (var0 != null && var1 == null) {
         var3.a.add(ad.a(aq.REMOVE, am.b, var0));
      }

      if (var0 != null && var1 != null) {
         var3.a(am.b, var0, var1);
         if (!var2.contains(ae.OMIT_MOVE_OPERATION)) {
            ai var4 = var3;

            for (int var5 = 0; var5 < var4.a.size(); var5++) {
               ad var6 = var4.a.get(var5);
               if (aq.REMOVE == var6.a || aq.ADD == var6.a) {
                  for (int var7 = var5 + 1; var7 < var4.a.size(); var7++) {
                     ad var8 = var4.a.get(var7);
                     if (var6.c.equals(var8.c)) {
                        ad var9 = null;
                        if (aq.REMOVE == var6.a && aq.ADD == var8.a) {
                           am var42 = a(var8.b, var5 + 1, var7 - 1, var4.a);
                           var9 = new ad(aq.MOVE, var6.b, var42);
                        } else if (aq.ADD == var6.a && aq.REMOVE == var8.a) {
                           am var10 = a(var8.b, var5, var7 - 1, var4.a);
                           var9 = new ad(aq.MOVE, var10, var6.b);
                        }

                        if (var9 != null) {
                           var4.a.remove(var7);
                           var4.a.set(var5, var9);
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (!var2.contains(ae.OMIT_COPY_OPERATION)) {
            JsonNode var30 = var1;
            JsonNode var26 = var0;
            ai var23 = var3;
            var1 = var30;
            var0 = var26;
            HashMap var11;
            a(var11 = new HashMap(), am.b, var0, var1);
            HashMap var35 = var11;

            for (int var38 = 0; var38 < var23.a.size(); var38++) {
               ad var40 = var23.a.get(var38);
               if (aq.ADD == var40.a) {
                  var1 = var40.c;
                  am var43;
                  if ((var43 = (am)var35.get(var1)) != null) {
                     am var21 = var40.b;
                     am var16 = var43;
                     boolean var44 = var43.equals(var21);
                     int var12 = 0;
                     int var27 = 0;

                     boolean var10000;
                     label119: {
                        while (true) {
                           if (var12 < var16.a.length && var27 < var21.a.length) {
                              am.a var31 = var16.b(var12);
                              am.a var13 = var21.b(var27);
                              String var32 = var31.toString();
                              String var47 = var13.toString();
                              if (!a(var32) || !a(var47) || var32.compareTo(var47) <= 0) {
                                 var12++;
                                 var27++;
                                 continue;
                              }
                              break;
                           }

                           if (!var44) {
                              var10000 = true;
                              break label119;
                           }
                           break;
                        }

                        var10000 = false;
                     }

                     if (var10000) {
                        if (var23.b.contains(ae.EMIT_TEST_OPERATIONS)) {
                           var23.a.add(var38, new ad(aq.TEST, var43, var40.c));
                           var38++;
                        }

                        var23.a.set(var38, new ad(aq.COPY, var43, var40.b));
                     }
                  }
               }
            }
         }

         if (var2.contains(ae.ADD_EXPLICIT_REMOVE_ADD_ON_REPLACE)) {
            ai var24 = var3;
            ArrayList var28 = new ArrayList();
            Iterator var33 = var24.a.iterator();

            while (var33.hasNext()) {
               ad var36;
               if ((var36 = (ad)var33.next()).a.equals(aq.REPLACE) && var36.e != null) {
                  var28.add(new ad(aq.REMOVE, var36.b, var36.e));
                  var28.add(new ad(aq.ADD, var36.b, var36.c));
               } else {
                  var28.add(var36);
               }
            }

            var24.a.clear();
            var24.a.addAll(var28);
         }
      }

      ai var25 = var3;
      JsonNodeFactory var29 = JsonNodeFactory.instance;
      ArrayNode var34 = JsonNodeFactory.instance.arrayNode();

      for (ad var39 : var25.a) {
         EnumSet var22 = var25.b;
         ad var17 = var39;
         ObjectNode var45;
         ObjectNode var48 = var45 = var29.objectNode();
         aq var46 = var17.a;
         var48.put("op", var17.a.rfcName);
         switch (var17.a) {
            case MOVE:
            case COPY:
               var45.put("from", var17.b.toString());
               var45.put("path", var17.d.toString());
               break;
            case REMOVE:
               var45.put("path", var17.b.toString());
               if (!var22.contains(ae.OMIT_VALUE_ON_REMOVE)) {
                  var45.set("value", var17.c);
               }
               break;
            case REPLACE:
               if (var22.contains(ae.ADD_ORIGINAL_VALUE_ON_REPLACE)) {
                  var45.set("fromValue", var17.e);
               }
            case ADD:
            case TEST:
               var45.put("path", var17.b.toString());
               var45.set("value", var17.c);
               break;
            default:
               throw new IllegalArgumentException("Unknown operation specified:" + var17.a);
         }

         ObjectNode var41 = var45;
         var34.add(var41);
      }

      return var34;
   }

   private static JsonNode a(JsonNode var0, JsonNode var1, EnumSet<ae> var2) {
      ai var3 = new ai(var2);
      if (var0 == null && var1 != null) {
         var3.a.add(ad.a(aq.ADD, am.b, var1));
      }

      if (var0 != null && var1 == null) {
         var3.a.add(ad.a(aq.REMOVE, am.b, var0));
      }

      if (var0 != null && var1 != null) {
         var3.a(am.b, var0, var1);
         if (!var2.contains(ae.OMIT_MOVE_OPERATION)) {
            var3.a();
         }

         if (!var2.contains(ae.OMIT_COPY_OPERATION)) {
            var3.b(var0, var1);
         }

         if (var2.contains(ae.ADD_EXPLICIT_REMOVE_ADD_ON_REPLACE)) {
            var3.b();
         }
      }

      return var3.c();
   }

   private static am a(Map<JsonNode, am> var0, JsonNode var1) {
      return (am)var0.get(var1);
   }

   private void b(JsonNode var1, JsonNode var2) {
      Map var6 = c(var1, var2);

      for (int var7 = 0; var7 < this.a.size(); var7++) {
         ad var3 = this.a.get(var7);
         am var4;
         if (aq.ADD == var3.a && (var4 = a(var6, var3.c)) != null && a(var4, var3.b)) {
            if (this.b.contains(ae.EMIT_TEST_OPERATIONS)) {
               this.a.add(var7, new ad(aq.TEST, var4, var3.c));
               var7++;
            }

            this.a.set(var7, new ad(aq.COPY, var4, var3.b));
         }
      }
   }

   public static boolean a(String var0) {
      int var1 = var0.length();

      for (int var2 = 0; var2 < var1; var2++) {
         if (!Character.isDigit(var0.charAt(var2))) {
            return false;
         }
      }

      return var1 > 0;
   }

   private static boolean a(am var0, am var1) {
      boolean var2 = var0.equals(var1);
      int var3 = 0;

      for (int var4 = 0; var3 < var0.a.length && var4 < var1.a.length; var4++) {
         am.a var5 = var0.b(var3);
         am.a var6 = var1.b(var4);
         String var7 = var5.toString();
         String var8 = var6.toString();
         if (a(var7) && a(var8) && var7.compareTo(var8) > 0) {
            return false;
         }

         var3++;
      }

      return !var2;
   }

   private static Map<JsonNode, am> c(JsonNode var0, JsonNode var1) {
      HashMap var2;
      a(var2 = new HashMap(), am.b, var0, var1);
      return var2;
   }

   public static void a(Map<JsonNode, am> var0, am var1, JsonNode var2, JsonNode var3) {
      if (var2.equals(var3)) {
         if (!var0.containsKey(var3)) {
            var0.put(var3, var1);
         }
      } else {
         ao var4 = ao.a(var2);
         ao var5 = ao.a(var3);
         if (var4 == var5) {
            switch (var4) {
               case OBJECT:
                  c(var0, var1, var2, var3);
                  return;
               case ARRAY:
                  b(var0, var1, var2, var3);
            }
         }
      }
   }

   private static void b(Map<JsonNode, am> var0, am var1, JsonNode var2, JsonNode var3) {
      int var4 = Math.min(var2.size(), var3.size());

      for (int var5 = 0; var5 < var4; var5++) {
         am var6 = var1.a(var5);
         a(var0, var6, var2.get(var5), var3.get(var5));
      }
   }

   private static void c(Map<JsonNode, am> var0, am var1, JsonNode var2, JsonNode var3) {
      Iterator var4 = var2.fieldNames();

      while (var4.hasNext()) {
         String var5 = (String)var4.next();
         if (var3.has(var5)) {
            am var6 = var1.b(var5);
            a(var0, var6, var2.get(var5), var3.get(var5));
         }
      }
   }

   private void a() {
      for (int var1 = 0; var1 < this.a.size(); var1++) {
         ad var2 = this.a.get(var1);
         if (aq.REMOVE == var2.a || aq.ADD == var2.a) {
            for (int var3 = var1 + 1; var3 < this.a.size(); var3++) {
               ad var4 = this.a.get(var3);
               if (var2.c.equals(var4.c)) {
                  ad var5 = null;
                  if (aq.REMOVE == var2.a && aq.ADD == var4.a) {
                     am var8 = a(var4.b, var1 + 1, var3 - 1, this.a);
                     var5 = new ad(aq.MOVE, var2.b, var8);
                  } else if (aq.ADD == var2.a && aq.REMOVE == var4.a) {
                     am var7 = a(var4.b, var1, var3 - 1, this.a);
                     var5 = new ad(aq.MOVE, var7, var2.b);
                  }

                  if (var5 != null) {
                     this.a.remove(var3);
                     this.a.set(var1, var5);
                     break;
                  }
               }
            }
         }
      }
   }

   private void b() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.a.iterator();

      while (var2.hasNext()) {
         ad var3;
         if ((var3 = (ad)var2.next()).a.equals(aq.REPLACE) && var3.e != null) {
            var1.add(new ad(aq.REMOVE, var3.b, var3.e));
            var1.add(new ad(aq.ADD, var3.b, var3.c));
         } else {
            var1.add(var3);
         }
      }

      this.a.clear();
      this.a.addAll(var1);
   }

   public static am a(am var0, int var1, int var2, List<ad> var3) {
      ArrayList var4 = new ArrayList(var0.a.length);

      for (int var5 = 0; var5 < var0.a.length; var5++) {
         var4.add(0);
      }

      for (int var8 = var1; var8 <= var2; var8++) {
         ad var7 = (ad)var3.get(var8);
         if (aq.ADD == var7.a || aq.REMOVE == var7.a) {
            a(var0, var7, var4);
         }
      }

      return a(var4, var0);
   }

   private static am a(List<Integer> var0, am var1) {
      List var5 = var1.c();

      for (int var2 = 0; var2 < var0.size(); var2++) {
         int var3;
         if ((var3 = (Integer)var0.get(var2)) != 0) {
            int var4 = ((am.a)var5.get(var2)).b();
            var5.set(var2, new am.a(Integer.toString(var4 + var3)));
         }
      }

      return new am(var5);
   }

   private static void a(am var0, ad var1, List<Integer> var2) {
      am var5 = var1.b;
      if (var1.b.a.length <= var0.a.length) {
         int var3 = -1;
         int var4 = 0;

         while (true) {
            var5 = var1.b;
            if (var4 >= var1.b.a.length - 1 || !var1.b.b(var4).equals(var0.b(var4))) {
               var5 = var1.b;
               if (var3 == var1.b.a.length - 2) {
                  var5 = var1.b;
                  if (var1.b.b(var1.b.a.length - 1).a()) {
                     var5 = var1.b;
                     a(var1, var1.b.a.length - 1, var2);
                  }
               }
               break;
            }

            var3 = var4++;
         }
      }
   }

   private static void a(ad var0, int var1, List<Integer> var2) {
      if (aq.ADD == var0.a) {
         var2.set(var1, (Integer)var2.get(var1) - 1);
      } else {
         if (aq.REMOVE == var0.a) {
            var2.set(var1, (Integer)var2.get(var1) + 1);
         }
      }
   }

   private ArrayNode c() {
      JsonNodeFactory var1 = JsonNodeFactory.instance;
      ArrayNode var2 = JsonNodeFactory.instance.arrayNode();

      for (ad var4 : this.a) {
         ObjectNode var5 = a(var1, var4, this.b);
         var2.add(var5);
      }

      return var2;
   }

   private static ObjectNode a(JsonNodeFactory var0, ad var1, EnumSet<ae> var2) {
      ObjectNode var4;
      ObjectNode var10000 = var4 = var0.objectNode();
      aq var3 = var1.a;
      var10000.put("op", var1.a.rfcName);
      switch (var1.a) {
         case MOVE:
         case COPY:
            var4.put("from", var1.b.toString());
            var4.put("path", var1.d.toString());
            break;
         case REMOVE:
            var4.put("path", var1.b.toString());
            if (!var2.contains(ae.OMIT_VALUE_ON_REMOVE)) {
               var4.set("value", var1.c);
            }
            break;
         case REPLACE:
            if (var2.contains(ae.ADD_ORIGINAL_VALUE_ON_REPLACE)) {
               var4.set("fromValue", var1.e);
            }
         case ADD:
         case TEST:
            var4.put("path", var1.b.toString());
            var4.set("value", var1.c);
            break;
         default:
            throw new IllegalArgumentException("Unknown operation specified:" + var1.a);
      }

      return var4;
   }

   public void a(am var1, JsonNode var2, JsonNode var3) {
      if (!var2.equals(var3)) {
         ao var4 = ao.a(var2);
         ao var5 = ao.a(var3);
         if (var4 == ao.ARRAY && var5 == ao.ARRAY) {
            this.b(var1, var2, var3);
            return;
         }

         if (var4 == ao.OBJECT && var5 == ao.OBJECT) {
            this.c(var1, var2, var3);
            return;
         }

         if (this.b.contains(ae.EMIT_TEST_OPERATIONS)) {
            this.a.add(new ad(aq.TEST, var1, var2));
         }

         this.a.add(ad.a(aq.REPLACE, var1, var2, var3));
      }
   }

   private void b(am var1, JsonNode var2, JsonNode var3) {
      List var4 = d(var2, var3);
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      int var8 = var2.size();
      int var9 = var3.size();
      int var10 = var4.size();
      int var11 = 0;

      while (var7 < var10) {
         JsonNode var12 = (JsonNode)var4.get(var7);
         JsonNode var13 = var2.get(var5);
         JsonNode var14 = var3.get(var6);
         if (var12.equals(var13) && var12.equals(var14)) {
            var5++;
            var6++;
            var7++;
            var11++;
         } else if (var12.equals(var13)) {
            am var16 = var1.a(var11);
            this.a.add(ad.a(aq.ADD, var16, var14));
            var11++;
            var6++;
         } else if (var12.equals(var14)) {
            am var17 = var1.a(var11);
            if (this.b.contains(ae.EMIT_TEST_OPERATIONS)) {
               this.a.add(new ad(aq.TEST, var17, var13));
            }

            this.a.add(ad.a(aq.REMOVE, var17, var13));
            var5++;
         } else {
            am var18 = var1.a(var11);
            this.a(var18, var13, var14);
            var5++;
            var6++;
            var11++;
         }
      }

      while (var5 < var8 && var6 < var9) {
         JsonNode var19 = var2.get(var5);
         JsonNode var20 = var3.get(var6);
         am var21 = var1.a(var11);
         this.a(var21, var19, var20);
         var5++;
         var6++;
         var11++;
      }

      var11 = this.a(var1, var3, var11, var6, var9);
      this.a(var1, var11, var5, var8, var2);
   }

   private void a(am var1, int var2, int var3, int var4, JsonNode var5) {
      while (var3 < var4) {
         am var6 = var1.a(var2);
         if (this.b.contains(ae.EMIT_TEST_OPERATIONS)) {
            this.a.add(new ad(aq.TEST, var6, var5.get(var3)));
         }

         this.a.add(ad.a(aq.REMOVE, var6, var5.get(var3)));
         var3++;
      }
   }

   private int a(am var1, JsonNode var2, int var3, int var4, int var5) {
      while (var4 < var5) {
         JsonNode var6 = var2.get(var4);
         am var7 = var1.a(var3);
         this.a.add(ad.a(aq.ADD, var7, var6.deepCopy()));
         var3++;
         var4++;
      }

      return var3;
   }

   private void c(am var1, JsonNode var2, JsonNode var3) {
      Iterator var4 = var2.fieldNames();

      while (var4.hasNext()) {
         String var5 = (String)var4.next();
         if (!var3.has(var5)) {
            am var6 = var1.b(var5);
            if (this.b.contains(ae.EMIT_TEST_OPERATIONS)) {
               this.a.add(new ad(aq.TEST, var6, var2.get(var5)));
            }

            this.a.add(ad.a(aq.REMOVE, var6, var2.get(var5)));
         } else {
            am var9 = var1.b(var5);
            this.a(var9, var2.get(var5), var3.get(var5));
         }
      }

      Iterator var8 = var3.fieldNames();

      while (var8.hasNext()) {
         String var10 = (String)var8.next();
         if (!var2.has(var10)) {
            am var7 = var1.b(var10);
            this.a.add(ad.a(aq.ADD, var7, var3.get(var10)));
         }
      }
   }

   private static List<JsonNode> d(JsonNode var0, JsonNode var1) {
      return fs.a(ag.a((ArrayNode)var0), ag.a((ArrayNode)var1));
   }
}
