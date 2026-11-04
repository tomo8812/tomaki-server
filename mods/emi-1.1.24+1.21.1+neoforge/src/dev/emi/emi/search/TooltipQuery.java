package dev.emi.emi.search;

import com.google.common.collect.Sets;
import dev.emi.emi.api.stack.EmiStack;
import java.util.List;
import java.util.Set;
import net.minecraft.network.chat.Component;

public class TooltipQuery extends Query {
   private final Set<EmiStack> valid = Sets.newIdentityHashSet();
   private final String name;

   public TooltipQuery(String name) {
      EmiSearch.tooltips.search(name.toLowerCase()).forEach(s -> this.valid.add(s.stack));
      this.name = name.toLowerCase();
   }

   @Override
   public boolean matches(EmiStack stack) {
      return this.valid.contains(stack);
   }

   @Override
   public boolean matchesUnbaked(EmiStack stack) {
      for (Component text : getText(stack)) {
         if (text.getString().toLowerCase().contains(this.name)) {
            return true;
         }
      }

      return false;
   }

   public static List<Component> getText(EmiStack stack) {
      List<Component> lines = stack.getTooltipText();
      return lines.isEmpty() ? lines : lines.subList(1, lines.size());
   }
}
