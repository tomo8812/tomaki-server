package dev.emi.emi.search;

import dev.emi.emi.api.stack.EmiStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;

public class RegexTooltipQuery extends Query {
   private final Pattern pattern;

   public RegexTooltipQuery(String name) {
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

      for (Component text : TooltipQuery.getText(stack)) {
         Matcher m = this.pattern.matcher(text.getString());
         if (m.find()) {
            return true;
         }
      }

      return false;
   }
}
