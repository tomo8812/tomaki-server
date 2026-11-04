package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class dr extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(dr.class);
   private static final String i = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
   private static final String j = "(\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]";
   private static final String k = "(\"(\\\\\"|[^\"])*\")";
   private static final String l = "(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\"))";
   private static final String m = "^\\s*?(.+)@(.+?)\\s*$";
   private static final String n = "^\\[(.*)\\]$";
   private static final String o = "^\\s*(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\"))(\\.(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\")))*$";
   private static final Pattern p = Pattern.compile("^\\s*?(.+)@(.+?)\\s*$");
   private static final Pattern q = Pattern.compile("^\\[(.*)\\]$");
   private static final Pattern r = Pattern.compile(
      "^\\s*(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\"))(\\.(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\")))*$"
   );
   private static final int s = 64;
   private final boolean t = false;
   private final boolean u = false;
   private final String v;

   public boolean b(String var1) {
      if (var1 == null) {
         return false;
      } else if (var1.endsWith(".")) {
         return false;
      } else {
         Matcher var2;
         if (!(var2 = p.matcher(var1)).matches()) {
            return false;
         } else {
            return !this.d(var2.group(1)) ? false : this.c(var2.group(2));
         }
      }
   }

   protected boolean c(String var1) {
      Matcher var2;
      return (var2 = q.matcher(var1)).matches() ? ds.a().a(var2.group(1)) : dq.a(false).a(var1);
   }

   protected boolean d(String var1) {
      return var1 != null && var1.length() <= 64 ? r.matcher(var1).matches() : false;
   }

   public dr(String var1, JsonNode var2, cp var3, df var4, String var5) {
      super(var1, var2, var3, dj.FORMAT, var4);
      this.v = var5;
      super.d = var4;
      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var5 = new LinkedHashSet();
      if (cZ.a(var1, super.d.d()) != cu.STRING) {
         return var5;
      }

      if (!this.b(var1.textValue())) {
         var5.add(this.a(var3, var1.textValue(), this.v));
      }

      return Collections.unmodifiableSet(var5);
   }
}
