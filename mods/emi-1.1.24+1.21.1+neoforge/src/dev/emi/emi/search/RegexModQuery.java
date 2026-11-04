package dev.emi.emi.search;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexModQuery extends Query {
   private final Pattern pattern;

   public RegexModQuery(String name) {
      Pattern p = null;

      try {
         p = Pattern.compile(name, 2);
      } catch (Exception var4) {
      }

      this.pattern = p;
   }

   @Override
   public boolean matches(EmiStack stack) {
      if (this.pattern == null) {
         return false;
      }

      String namespace = stack.getId().getNamespace();
      String mod = EmiUtil.getModName(namespace);
      Matcher m = this.pattern.matcher(mod);
      return m.find();
   }
}
