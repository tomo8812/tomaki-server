package dev.emi.emi.search;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiTagKey;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagQuery extends Query {
   private final Set<Object> valid;

   public TagQuery(String name) {
      String lowerName = name.toLowerCase();
      this.valid = Stream.<EmiTagKey>concat(EmiTagKey.fromRegistry(EmiPort.getItemRegistry()), EmiTagKey.fromRegistry(EmiPort.getBlockRegistry()))
         .filter(t -> t.hasTranslation() && t.getTagName().getString().toLowerCase().contains(lowerName) ? true : t.id().toString().contains(lowerName))
         .flatMap(v -> v.stream())
         .collect(Collectors.toSet());
   }

   @Override
   public boolean matches(EmiStack stack) {
      return this.valid.contains(stack.getKey());
   }
}
