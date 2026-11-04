package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class co {
   private static final Logger b = LoggerFactory.getLogger(co.class);
   private static Map<String, String> c = new ConcurrentHashMap<>();
   public static final List<ci> a;
   private final String d;
   private final String e;
   private final Map<String, cw> f;

   static cK a(String var0, String var1) {
      return new cK(var0, var1);
   }

   private co(String var1, String var2, Map<String, cw> var3) {
      if (StringUtils.isBlank(var1)) {
         throw new IllegalArgumentException("uri must not be null or blank");
      }

      if (StringUtils.isBlank(var2)) {
         throw new IllegalArgumentException("idKeyword must not be null or blank");
      }

      if (var3 == null) {
         throw new IllegalArgumentException("keywords must not be null ");
      }

      this.d = var1;
      this.e = var2;
      this.f = var3;
   }

   public static co a() {
      return new dm().a();
   }

   public static co b() {
      return new dn().a();
   }

   public static co c() {
      return new do().a();
   }

   public static co d() {
      return new dk().a();
   }

   public static co.a a(String var0) {
      return new co.a(var0);
   }

   public static co.a a(String var0, co var1) {
      cj var2;
      if ((var2 = (cj)var1.f.get(dj.FORMAT.a())) == null) {
         throw new IllegalArgumentException("The formatKeyword did not exist - blueprint is invalid.");
      } else {
         return a(var0).a(var1.e).a(var1.f.values()).b(var2.b());
      }
   }

   public String a(JsonNode var1) {
      return this.a(var1, this.e);
   }

   public JsonNode a(String var1, JsonNode var2) {
      boolean var3;
      String var4 = (var3 = this.f.containsKey("$anchor")) ? var1.substring(1) : var1;
      String var6 = var3 ? "$anchor" : this.e;
      if (var4.equals(this.a(var2, var6))) {
         return var2;
      }

      Iterator var5 = var2.elements();

      while (var5.hasNext()) {
         JsonNode var7;
         if ((var7 = this.a(var1, (JsonNode)var5.next())) != null) {
            return var7;
         }
      }

      return null;
   }

   private String a(JsonNode var1, String var2) {
      JsonNode var3;
      return (var3 = var1.get(var2)) != null && var3.isTextual() ? var3.textValue() : null;
   }

   public String e() {
      return this.d;
   }

   public cv a(df var1, String var2, String var3, JsonNode var4, cp var5, String var6) {
      try {
         cw var7;
         if ((var7 = this.f.get(var3)) == null) {
            if (c.put(var3, var3) == null) {
               b.warn(
                  "Unknown keyword "
                     + var3
                     + " - you should define your own Meta Schema. If the keyword is irrelevant for validation, just use a NonValidationKeyword"
               );
            }

            return null;
         } else {
            var7.a(var6);
            return var7.a(var2, var4, var5, var1);
         }
      } catch (InvocationTargetException var8) {
         if (var8.getTargetException() instanceof cq) {
            b.error("Error:", var8);
            throw (cq)var8.getTargetException();
         } else {
            b.warn("Could not load validator " + var3);
            throw new cq(var8.getTargetException());
         }
      } catch (cq var9) {
         throw var9;
      } catch (Exception var10) {
         b.warn("Could not load validator " + var3);
         throw new cq(var10);
      }
   }

   static {
      (a = new ArrayList<>()).add(a("time", "^\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?$"));
      a.add(a("ip-address", "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"));
      a.add(a("ipv4", "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$"));
      a.add(
         a(
            "ipv6",
            "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$"
         )
      );
      a.add(
         a(
            "uri",
            "^[A-Za-z][A-Za-z0-9+.-]*:(\\/\\/([A-Za-z0-9._~\\-%!$&'()*+,;=:]*@)?[A-Za-z0-9._~\\-!$&'()*+,;=%:\\[\\]]*(:[0-9]*)?)?[A-Za-z0-9._~\\-%!$&'()*+,;=:@\\/]*([?][A-Za-z0-9._~\\-%!$&'()*+,;=:@\\/?]*)?([#][A-Za-z0-9._~\\-%!$&'()*+,;=:@\\/?]*)?"
         )
      );
      a.add(
         a(
            "color",
            "(#?([0-9A-Fa-f]{3,6})\\b)|(aqua)|(black)|(blue)|(fuchsia)|(gray)|(green)|(lime)|(maroon)|(navy)|(olive)|(orange)|(purple)|(red)|(silver)|(teal)|(white)|(yellow)|(rgb\\(\\s*\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\s*,\\s*\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\s*,\\s*\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\s*\\))|(rgb\\(\\s*(\\d?\\d%|100%)+\\s*,\\s*(\\d?\\d%|100%)+\\s*,\\s*(\\d?\\d%|100%)+\\s*\\))"
         )
      );
      a.add(a("hostname", "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$"));
      a.add(a("alpha", "^[a-zA-Z]+$"));
      a.add(a("alphanumeric", "^[a-zA-Z0-9]+$"));
      a.add(a("phone", "^\\+(?:[0-9] ?){6,14}[0-9]$"));
      a.add(a("utc-millisec", "^[0-9]+(\\.?[0-9]+)?$"));
      a.add(a("style", "\\s*(.+?):\\s*([^;]+);?"));
   }

   public static class a {
      private Map<String, cw> a = new HashMap<>();
      private Map<String, ci> b = new HashMap<>();
      private String c;
      private String d = "id";

      public a(String var1) {
         this.c = var1;
      }

      private static Map<String, cw> a(Map<String, cw> var0, Map<String, ci> var1) {
         HashMap var2 = new HashMap();
         Iterator var5 = var0.entrySet().iterator();

         while (var5.hasNext()) {
            Entry var3;
            String var4 = (String)(var3 = (Entry)var5.next()).getKey();
            cw var7 = (cw)var3.getValue();
            if (dj.FORMAT.a().equals(var4)) {
               if (!(var7 instanceof cj)) {
                  throw new IllegalArgumentException("Overriding the keyword 'format' is not supported");
               }
            } else {
               var2.put(var7.a(), var7);
            }
         }

         cj var6 = new cj(dj.FORMAT, var1);
         var2.put(var6.a(), var6);
         return Collections.unmodifiableMap(var2);
      }

      public co.a a(cw var1) {
         this.a.put(var1.a(), var1);
         return this;
      }

      public co.a a(Collection<? extends cw> var1) {
         for (cw var2 : var1) {
            this.a.put(var2.a(), var2);
         }

         return this;
      }

      public co.a a(ci var1) {
         this.b.put(var1.a(), var1);
         return this;
      }

      public co.a b(Collection<? extends ci> var1) {
         for (ci var2 : var1) {
            this.a(var2);
         }

         return this;
      }

      public co.a a(String var1) {
         this.d = var1;
         return this;
      }

      public co a() {
         Map var1 = a(this.a, this.b);
         return new co(this.c, this.d, var1);
      }
   }
}
