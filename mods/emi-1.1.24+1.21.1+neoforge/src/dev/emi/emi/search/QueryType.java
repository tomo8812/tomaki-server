package dev.emi.emi.search;

import java.util.function.Function;
import net.minecraft.network.chat.Style;

public enum QueryType {
   DEFAULT("", 16777215, 13383479, 16733525, 16558421, NameQuery::new, RegexNameQuery::new),
   MOD("@", 5592575, 5592575, 10181851, 15870962, ModQuery::new, RegexModQuery::new),
   TOOLTIP("$", 16777045, 16777045, 12448902, 16039302, TooltipQuery::new, RegexTooltipQuery::new),
   TAG("#", 5635925, 5635925, 4319950, 39146, TagQuery::new, RegexTagQuery::new);

   public final String prefix;
   public final Style color;
   public final Style slashColor;
   public final Style regexColor;
   public final Style escapeColor;
   public final Function<String, Query> queryConstructor;
   public final Function<String, Query> regexQueryConstructor;

   QueryType(
      String prefix,
      int color,
      int slashColor,
      int regexColor,
      int escapeColor,
      Function<String, Query> queryConstructor,
      Function<String, Query> regexQueryConstructor
   ) {
      this.prefix = prefix;
      this.color = Style.EMPTY.withColor(color);
      this.slashColor = Style.EMPTY.withColor(slashColor);
      this.regexColor = Style.EMPTY.withColor(regexColor);
      this.escapeColor = Style.EMPTY.withColor(escapeColor);
      this.queryConstructor = queryConstructor;
      this.regexQueryConstructor = regexQueryConstructor;
   }

   public static QueryType fromString(String s) {
      for (int i = values().length - 1; i >= 0; i--) {
         QueryType type = values()[i];
         if (s.startsWith(type.prefix)) {
            return type;
         }
      }

      return null;
   }

   // $VF: synthetic method
   private static QueryType[] $values() {
      return new QueryType[]{DEFAULT, MOD, TOOLTIP, TAG};
   }
}
