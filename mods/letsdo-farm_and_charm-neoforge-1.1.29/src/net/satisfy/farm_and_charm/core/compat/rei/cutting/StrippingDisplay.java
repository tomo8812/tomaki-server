package net.satisfy.farm_and_charm.core.compat.rei.cutting;

import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.satisfy.farm_and_charm.core.util.Strippables;

public class StrippingDisplay extends BasicDisplay {
   public static final CategoryIdentifier<StrippingDisplay> ID = CategoryIdentifier.of("farm_and_charm", "log_stripping");

   public StrippingDisplay(Strippables.Entry entry) {
      super(List.of(EntryIngredients.of(entry.log())), List.of(EntryIngredients.of(entry.stripped())), Optional.empty());
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return ID;
   }
}
