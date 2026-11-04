package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jcodings.specific.UTF8Encoding;
import org.joni.Regex;
import org.joni.Syntax;
import org.joni.exception.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cM implements cv {
   private final cv a;
   private final df b;

   public cM(String var1, JsonNode var2, cp var3, df var4) {
      this.b = var4;
      if (var4.d() != null && var4.d().f()) {
         this.a = new cM.a(var1, var2, var3, var4);
      } else {
         this.a = new cM.b(var1, var2, var3, var4);
      }
   }

   @Override
   public Set<dg> a(JsonNode var1) {
      return this.a.a(var1);
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      return this.a.a(var1, var2, var3);
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      return this.a.a(var1, var2, var3, var4);
   }

   static class a extends bT implements cv {
      private static final Logger g = LoggerFactory.getLogger(cM.class);
      private String i = "";
      private Regex j;

      public a(String var1, JsonNode var2, cp var3, df var4) {
         super(var1, var2, var3, dj.PATTERN, var4);
         if (var2 != null && var2.isTextual()) {
            this.i = var2.textValue();

            try {
               this.a(this.i, var4.d() != null && var4.d().f());
            } catch (SyntaxException var5) {
               g.error("Failed to compile pattern : Invalid syntax [" + this.i + "]", var5);
               throw var5;
            }
         }

         super.d = var4;
         this.a(this.e().d());
      }

      private void a(String var1, boolean var2) {
         byte[] var3 = var1.getBytes();
         this.j = new Regex(var3, 0, var3.length, 0, UTF8Encoding.INSTANCE, Syntax.ECMAScript);
      }

      private boolean b(String var1) {
         if (this.j == null) {
            return true;
         }

         byte[] var2 = var1.getBytes();
         return this.j.matcher(var2).search(0, var2.length, 0) >= 0;
      }

      @Override
      public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
         this.a(g, (JsonNode)var1, var2, var3);
         if (cZ.a(var1, super.d.d()) != cu.STRING) {
            return Collections.emptySet();
         }

         try {
            if (!this.b(var1.asText())) {
               return Collections.singleton(this.a(var3, this.i));
            }
         } catch (PatternSyntaxException var4) {
            g.error("Failed to apply pattern on " + var3 + ": Invalid syntax [" + this.i + "]", var4);
         }

         return Collections.emptySet();
      }
   }

   static class b extends bT implements cv {
      private static final Logger g = LoggerFactory.getLogger(cM.class);
      private String i = "";
      private Pattern j;

      public b(String var1, JsonNode var2, cp var3, df var4) {
         super(var1, var2, var3, dj.PATTERN, var4);
         if (var2 != null && var2.isTextual()) {
            this.i = var2.textValue();

            try {
               this.j = Pattern.compile(this.i);
            } catch (PatternSyntaxException var5) {
               g.error("Failed to compile pattern : Invalid syntax [" + this.i + "]", var5);
               throw var5;
            }
         }

         super.d = var4;
         this.a(this.e().d());
      }

      private boolean b(String var1) {
         return this.j == null || this.j.matcher(var1).find();
      }

      @Override
      public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
         this.a(g, (JsonNode)var1, var2, var3);
         if (cZ.a(var1, super.d.d()) != cu.STRING) {
            return Collections.emptySet();
         }

         try {
            if (!this.b(var1.asText())) {
               return Collections.singleton(this.a(var3, this.i));
            }
         } catch (PatternSyntaxException var4) {
            g.error("Failed to apply pattern on " + var3 + ": Invalid syntax [" + this.i + "]", var4);
         }

         return Collections.emptySet();
      }
   }
}
