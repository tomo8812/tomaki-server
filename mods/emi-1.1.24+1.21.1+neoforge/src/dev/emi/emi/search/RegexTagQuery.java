package dev.emi.emi.search;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiTagKey;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegexTagQuery extends Query {
   private final Set<Object> valid;

   public RegexTagQuery(String name) {
      Pattern p = null;

      try {
         p = Pattern.compile(name, 2);
      } catch (Exception var4) {
      }

      if (p == null) {
         this.valid = Set.of();
      } else {
         Pattern pat = p;
         this.valid = Stream.<EmiTagKey>concat(EmiTagKey.fromRegistry(EmiPort.getItemRegistry()), EmiTagKey.fromRegistry(EmiPort.getBlockRegistry()))
            .filter(t -> t.hasTranslation() && pat.matcher(t.getTagName().getString().toLowerCase()).find() ? true : pat.matcher(t.id().toString()).find())
            .flatMap(v -> v.stream())
            .collect(Collectors.toSet());
      }
   }

   @Override
   public boolean matches(EmiStack stack) {
      return this.valid.contains(stack.getKey());
   }
}
