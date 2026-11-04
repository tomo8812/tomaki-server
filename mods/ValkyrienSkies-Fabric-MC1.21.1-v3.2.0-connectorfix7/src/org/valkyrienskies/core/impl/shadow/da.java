package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class da extends bT implements cv {
   private static final String g = "type";
   private static final String i = "enum";
   private static final String j = "$ref";
   private static final Logger k = LoggerFactory.getLogger(da.class);
   private cu l;
   private cp m;
   private dd n;
   private static final char o = '0';
   private static final char p = '1';
   private static final char q = '9';
   private static final char r = '-';
   private static final char s = '+';
   private static final char t = '.';
   private static final char u = 'E';
   private static final char v = 'e';

   public da(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.TYPE, var4);
      this.l = cZ.a(var2);
      this.m = var3;
      super.d = var4;
      if (this.l == cu.UNION) {
         this.n = new dd(var1, var2, var3, var4);
      }

      this.a(this.e().d());
   }

   public cu h() {
      return this.l;
   }

   public boolean b(JsonNode var1) {
      cu var2;
      if ((var2 = cZ.a(var1, super.d.d())) == this.l) {
         return true;
      }

      if (this.l == cu.ANY) {
         return true;
      }

      if (this.l == cu.NUMBER && var2 == cu.INTEGER) {
         return true;
      }

      di var3 = (di)bV.a().a("com.networknt.schema.ValidatorState");
      if (!cu.NULL.equals(var2) || (!var3.b() || !dE.a(this.m.d().c(), super.d.d())) && !dE.a(this.d().c())) {
         if (a(this.m)) {
            return true;
         }

         if (super.d.d().a()) {
            if (this.l == cu.ARRAY) {
               return true;
            }

            if (var2 == cu.STRING) {
               if (this.l == cu.INTEGER) {
                  if (b(var1.textValue())) {
                     return true;
                  }
               } else if (this.l == cu.BOOLEAN) {
                  if (c(var1.textValue())) {
                     return true;
                  }
               } else if (this.l == cu.NUMBER && d(var1.textValue())) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(k, (JsonNode)var1, var2, var3);
      if (this.l == cu.UNION) {
         return this.n.a(var1, var2, var3);
      }

      if (!dE.a(var1, this.l, this.m, super.d.d())) {
         cu var4 = cZ.a(var1, super.d.d());
         return Collections.singleton(this.a(var3, var4.toString(), this.l.toString()));
      }

      if (super.a.endsWith("/type")) {
         this.e(var3);
      }

      return Collections.emptySet();
   }

   private void e(String var1) {
      Object var2;
      if ((var2 = (List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties")) == null) {
         var2 = new ArrayList();
         bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var2);
      } else {
         var2 = var2;
      }

      var2.add(var1);
   }

   public static boolean b(String var0) {
      if (var0 != null && !var0.equals("")) {
         int var1 = 0;
         if (var0.charAt(0) == '-' || var0.charAt(0) == '+') {
            if (var0.length() == 1) {
               return false;
            }

            var1 = 1;
         }

         while (var1 < var0.length()) {
            char var2;
            if ((var2 = var0.charAt(var1)) < '0' || var2 > '9') {
               return false;
            }

            var1++;
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean c(String var0) {
      return "true".equals(var0) || "false".equals(var0);
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   public static boolean d(String var0) {
      if (var0 != null && !var0.equals("")) {
         int var1 = 0;
         int var2 = var0.length();
         if (var0.charAt(0) == '-' || var0.charAt(0) == '+') {
            if (var0.length() == 1) {
               return false;
            }

            var1 = 1;
         }

         char var3;
         if ((var3 = var0.charAt(var1++)) == '0') {
            if (var1 < var2 && (var3 = var0.charAt(var1++)) != '.' && var3 != 'E' && var3 != 'e') {
               return false;
            }
         } else {
            if ('1' > var3 || var3 > '9') {
               return false;
            }

            while (var1 < var2 && '0' <= var3 && var3 <= '9') {
               var3 = var0.charAt(var1++);
            }
         }

         if (var3 == '.') {
            if (var1 >= var2) {
               return false;
            }

            do {
               var3 = var0.charAt(var1++);
            } while (var1 < var2 && '0' <= var3 && var3 <= '9');
         }

         if (var3 == 'E' || var3 == 'e') {
            if (var1 >= var2) {
               return false;
            }

            if ((var3 = var0.charAt(var1++)) != '+') {
               if (var3 == '-') {
                  if (var1 >= var2) {
                     return false;
                  }
               } else if (var1 >= var2 || '0' > var3 || var3 > '9') {
                  return var1 >= var2 && '0' <= var3 && var3 <= '9';
               }
            } else if (var1 >= var2) {
               return false;
            }

            do {
               var3 = var0.charAt(var1++);
            } while (var1 < var2 && '0' <= var3 && var3 <= '9');
         }

         return var1 >= var2 && '0' <= var3 && var3 <= '9';
      } else {
         return false;
      }
   }

   public static boolean a(JsonNode var0, cT var1) {
      if (var0.isNumber()) {
         return true;
      } else {
         return var1.a() && cZ.a(var0, var1) == cu.STRING ? d(var0.textValue()) : false;
      }
   }

   private static boolean a(cp var0) {
      JsonNode var1 = null;
      JsonNode var2 = null;
      JsonNode var3 = null;
      if (var0 != null) {
         if (var0.c() != null) {
            var1 = var0.c().get("type");
            var2 = var0.c().get("enum");
         }

         if (var0.d() != null && var0.d().c() != null) {
            var3 = var0.d().c().get("$ref");
         }
      }

      return var1 != null && var2 != null && var3 != null ? cZ.a(var1) == cu.OBJECT && var2.isArray() : false;
   }
}
