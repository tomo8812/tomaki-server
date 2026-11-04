package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;

public final class Ft implements Fq<JsonNode> {
   private final ObjectMapper a;

   public Ft(ObjectMapper var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private ByteBuf a(JsonNode var1, JsonNode var2, ByteBuf var3) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      var2 = var2;
      EnumSet var4 = ae.a();
      var2 = var2;
      var1 = var1;
      ai var5 = new ai(var4);
      if (var1 == null && var2 != null) {
         var5.a.add(ad.a(aq.ADD, am.b, var2));
      }

      if (var1 != null && var2 == null) {
         var5.a.add(ad.a(aq.REMOVE, am.b, var1));
      }

      if (var1 != null && var2 != null) {
         var5.a(am.b, var1, var2);
         if (!var4.contains(ae.OMIT_MOVE_OPERATION)) {
            ai var6 = var5;

            for (int var7 = 0; var7 < var6.a.size(); var7++) {
               ad var8 = var6.a.get(var7);
               if (aq.REMOVE == var8.a || aq.ADD == var8.a) {
                  for (int var9 = var7 + 1; var9 < var6.a.size(); var9++) {
                     ad var10 = var6.a.get(var9);
                     if (var8.c.equals(var10.c)) {
                        ad var11 = null;
                        if (aq.REMOVE == var8.a && aq.ADD == var10.a) {
                           am var47 = ai.a(var10.b, var7 + 1, var9 - 1, var6.a);
                           var11 = new ad(aq.MOVE, var8.b, var47);
                        } else if (aq.ADD == var8.a && aq.REMOVE == var10.a) {
                           am var12 = ai.a(var10.b, var7, var9 - 1, var6.a);
                           var11 = new ad(aq.MOVE, var12, var8.b);
                        }

                        if (var11 != null) {
                           var6.a.remove(var9);
                           var6.a.set(var7, var11);
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (!var4.contains(ae.OMIT_COPY_OPERATION)) {
            JsonNode var35 = var2;
            JsonNode var31 = var1;
            ai var28 = var5;
            var2 = var35;
            var1 = var31;
            HashMap var13;
            ai.a(var13 = new HashMap(), am.b, var1, var2);
            HashMap var40 = var13;

            for (int var43 = 0; var43 < var28.a.size(); var43++) {
               ad var45 = var28.a.get(var43);
               if (aq.ADD == var45.a) {
                  var2 = var45.c;
                  am var48;
                  if ((var48 = (am)var40.get(var2)) != null) {
                     am var26 = var45.b;
                     am var18 = var48;
                     boolean var49 = var48.equals(var26);
                     int var14 = 0;
                     int var32 = 0;

                     boolean var10000;
                     label119: {
                        while (true) {
                           if (var14 < var18.a.length && var32 < var26.a.length) {
                              am.a var36 = var18.b(var14);
                              am.a var15 = var26.b(var32);
                              String var37 = var36.toString();
                              String var52 = var15.toString();
                              if (!ai.a(var37) || !ai.a(var52) || var37.compareTo(var52) <= 0) {
                                 var14++;
                                 var32++;
                                 continue;
                              }
                              break;
                           }

                           if (!var49) {
                              var10000 = true;
                              break label119;
                           }
                           break;
                        }

                        var10000 = false;
                     }

                     if (var10000) {
                        if (var28.b.contains(ae.EMIT_TEST_OPERATIONS)) {
                           var28.a.add(var43, new ad(aq.TEST, var48, var45.c));
                           var43++;
                        }

                        var28.a.set(var43, new ad(aq.COPY, var48, var45.b));
                     }
                  }
               }
            }
         }

         if (var4.contains(ae.ADD_EXPLICIT_REMOVE_ADD_ON_REPLACE)) {
            ai var29 = var5;
            ArrayList var33 = new ArrayList();
            Iterator var38 = var29.a.iterator();

            while (var38.hasNext()) {
               ad var41;
               if ((var41 = (ad)var38.next()).a.equals(aq.REPLACE) && var41.e != null) {
                  var33.add(new ad(aq.REMOVE, var41.b, var41.e));
                  var33.add(new ad(aq.ADD, var41.b, var41.c));
               } else {
                  var33.add(var41);
               }
            }

            var29.a.clear();
            var29.a.addAll(var33);
         }
      }

      ai var30 = var5;
      JsonNodeFactory var34 = JsonNodeFactory.instance;
      ArrayNode var39 = JsonNodeFactory.instance.arrayNode();

      for (ad var44 : var30.a) {
         EnumSet var27 = var30.b;
         ad var19 = var44;
         ObjectNode var50;
         ObjectNode var53 = var50 = var34.objectNode();
         aq var51 = var19.a;
         var53.put("op", var19.a.rfcName);
         switch (var19.a) {
            case MOVE:
            case COPY:
               var50.put("from", var19.b.toString());
               var50.put("path", var19.d.toString());
               break;
            case REMOVE:
               var50.put("path", var19.b.toString());
               if (!var27.contains(ae.OMIT_VALUE_ON_REMOVE)) {
                  var50.set("value", var19.c);
               }
               break;
            case REPLACE:
               if (var27.contains(ae.ADD_ORIGINAL_VALUE_ON_REPLACE)) {
                  var50.set("fromValue", var19.e);
               }
            case ADD:
            case TEST:
               var50.put("path", var19.b.toString());
               var50.set("value", var19.c);
               break;
            default:
               throw new IllegalArgumentException("Unknown operation specified:" + var19.a);
         }

         ObjectNode var46 = var50;
         var39.add(var46);
      }

      ArrayNode var20 = var39;
      byte[] var21 = this.a.writeValueAsBytes(var20);
      var3.writeInt(var21.length);
      var3.writeBytes(var21);
      return var3;
   }

   private JsonNode a(JsonNode var1, ByteBuf var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      int var3 = var2.readInt();
      JsonNode var10000 = this.a.readTree((InputStream)(new ByteBufInputStream(var2, var3)));
      JsonNode var4 = var1;
      var10000 = aj.a(var10000, var4, aa.a());
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }

   // $VF: synthetic method
   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final ByteBuf a(Object var1, Object var2, ByteBuf var3) {
      JsonNode var10001 = (JsonNode)var1;
      JsonNode var10002 = (JsonNode)var2;
      ByteBuf var4 = var3;
      JsonNode var25 = var10002;
      am var19 = var10001;
      Ft var17 = this;
      Intrinsics.checkNotNullParameter(var19, "");
      Intrinsics.checkNotNullParameter(var25, "");
      Intrinsics.checkNotNullParameter(var4, "");
      JsonNode var26 = var25;
      EnumSet var5 = ae.a();
      JsonNode var27 = var26;
      var19 = var19;
      ai var6 = new ai(var5);
      if (var19 == null && var27 != null) {
         var6.a.add(ad.a(aq.ADD, am.b, var27));
      }

      if (var19 != null && var27 == null) {
         var6.a.add(ad.a(aq.REMOVE, am.b, var19));
      }

      if (var19 != null && var27 != null) {
         var6.a(am.b, var19, var27);
         if (!var5.contains(ae.OMIT_MOVE_OPERATION)) {
            ai var7 = var6;

            for (int var8 = 0; var8 < var7.a.size(); var8++) {
               ad var9 = var7.a.get(var8);
               if (aq.REMOVE == var9.a || aq.ADD == var9.a) {
                  for (int var10 = var8 + 1; var10 < var7.a.size(); var10++) {
                     ad var11 = var7.a.get(var10);
                     if (var9.c.equals(var11.c)) {
                        ad var12 = null;
                        if (aq.REMOVE == var9.a && aq.ADD == var11.a) {
                           am var51 = ai.a(var11.b, var8 + 1, var10 - 1, var7.a);
                           var12 = new ad(aq.MOVE, var9.b, var51);
                        } else if (aq.ADD == var9.a && aq.REMOVE == var11.a) {
                           am var13 = ai.a(var11.b, var8, var10 - 1, var7.a);
                           var12 = new ad(aq.MOVE, var13, var9.b);
                        }

                        if (var12 != null) {
                           var7.a.remove(var10);
                           var7.a.set(var8, var12);
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (!var5.contains(ae.OMIT_COPY_OPERATION)) {
            JsonNode var39 = var27;
            JsonNode var35 = var19;
            ai var32 = var6;
            JsonNode var28 = var39;
            var19 = var35;
            HashMap var14;
            ai.a(var14 = new HashMap(), am.b, var19, var28);
            HashMap var44 = var14;

            for (int var47 = 0; var47 < var32.a.size(); var47++) {
               ad var49 = var32.a.get(var47);
               if (aq.ADD == var49.a) {
                  JsonNode var29 = var49.c;
                  am var52;
                  if ((var52 = (am)var44.get(var29)) != null) {
                     am var30 = var49.b;
                     var19 = var52;
                     boolean var53 = var52.equals(var30);
                     int var15 = 0;
                     int var36 = 0;

                     boolean var10000;
                     label119: {
                        while (true) {
                           if (var15 < var19.a.length && var36 < var30.a.length) {
                              am.a var40 = var19.b(var15);
                              am.a var16 = var30.b(var36);
                              String var41 = var40.toString();
                              String var56 = var16.toString();
                              if (!ai.a(var41) || !ai.a(var56) || var41.compareTo(var56) <= 0) {
                                 var15++;
                                 var36++;
                                 continue;
                              }
                              break;
                           }

                           if (!var53) {
                              var10000 = true;
                              break label119;
                           }
                           break;
                        }

                        var10000 = false;
                     }

                     if (var10000) {
                        if (var32.b.contains(ae.EMIT_TEST_OPERATIONS)) {
                           var32.a.add(var47, new ad(aq.TEST, var52, var49.c));
                           var47++;
                        }

                        var32.a.set(var47, new ad(aq.COPY, var52, var49.b));
                     }
                  }
               }
            }
         }

         if (var5.contains(ae.ADD_EXPLICIT_REMOVE_ADD_ON_REPLACE)) {
            ai var33 = var6;
            ArrayList var37 = new ArrayList();
            Iterator var42 = var33.a.iterator();

            while (var42.hasNext()) {
               ad var45;
               if ((var45 = (ad)var42.next()).a.equals(aq.REPLACE) && var45.e != null) {
                  var37.add(new ad(aq.REMOVE, var45.b, var45.e));
                  var37.add(new ad(aq.ADD, var45.b, var45.c));
               } else {
                  var37.add(var45);
               }
            }

            var33.a.clear();
            var33.a.addAll(var37);
         }
      }

      ai var34 = var6;
      JsonNodeFactory var38 = JsonNodeFactory.instance;
      ArrayNode var43 = JsonNodeFactory.instance.arrayNode();

      for (ad var48 : var34.a) {
         EnumSet var31 = var34.b;
         ad var23 = var48;
         ObjectNode var54;
         ObjectNode var57 = var54 = var38.objectNode();
         aq var55 = var23.a;
         var57.put("op", var23.a.rfcName);
         switch (var23.a) {
            case MOVE:
            case COPY:
               var54.put("from", var23.b.toString());
               var54.put("path", var23.d.toString());
               break;
            case REMOVE:
               var54.put("path", var23.b.toString());
               if (!var31.contains(ae.OMIT_VALUE_ON_REMOVE)) {
                  var54.set("value", var23.c);
               }
               break;
            case REPLACE:
               if (var31.contains(ae.ADD_ORIGINAL_VALUE_ON_REPLACE)) {
                  var54.set("fromValue", var23.e);
               }
            case ADD:
            case TEST:
               var54.put("path", var23.b.toString());
               var54.set("value", var23.c);
               break;
            default:
               throw new IllegalArgumentException("Unknown operation specified:" + var23.a);
         }

         ObjectNode var50 = var54;
         var43.add(var50);
      }

      var19 = var43;
      byte[] var18 = var17.a.writeValueAsBytes(var19);
      var4.writeInt(var18.length);
      var4.writeBytes(var18);
      return var4;
   }

   // $VF: synthetic method
   @Override
   public final JsonNode a(Object var1, ByteBuf var2) {
      JsonNode var10001 = (JsonNode)var1;
      ByteBuf var3 = var2;
      JsonNode var6 = var10001;
      Ft var5 = this;
      Intrinsics.checkNotNullParameter(var6, "");
      Intrinsics.checkNotNullParameter(var3, "");
      int var4 = var3.readInt();
      JsonNode var10000 = var5.a.readTree((InputStream)(new ByteBufInputStream(var3, var4)));
      JsonNode var7 = var6;
      var10000 = aj.a(var10000, var7, aa.a());
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }
}
