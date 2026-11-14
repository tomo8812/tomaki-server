package org.millenaire.language;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.millenaire.village.panel.PanelContentGenerator;

public final class DisplayTextResolver {
   private DisplayTextResolver() {
   }

   public static String resolve(List<PanelContentGenerator.TextPart> parts, BiFunction<String, List<String>, String> translate) {
      return resolve(parts, translate, key -> true);
   }

   public static String resolve(List<PanelContentGenerator.TextPart> parts, BiFunction<String, List<String>, String> translate, Predicate<String> exists) {
      StringBuilder sb = new StringBuilder();

      for (PanelContentGenerator.TextPart part : parts) {
         if (!part.key()) {
            sb.append(part.value());
         } else if (part.fallback() != null && !exists.test(part.value())) {
            sb.append(part.fallback());
         } else {
            sb.append(translate.apply(part.value(), part.args()));
         }
      }

      return sb.toString();
   }
}
