package dev.emi.emi.search;

import dev.emi.emi.api.stack.EmiStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexNameQuery extends Query {
   private final Pattern pattern;

   public RegexNameQuery(String name) {
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

      Matcher m = this.pattern.matcher(NameQuery.getText(stack).getString());
      return m.find();
   }
}
