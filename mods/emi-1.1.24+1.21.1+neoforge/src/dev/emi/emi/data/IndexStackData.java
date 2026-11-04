package dev.emi.emi.data;

import dev.emi.emi.api.stack.EmiIngredient;
import java.util.List;
import java.util.function.Predicate;

public record IndexStackData(boolean disable, List<IndexStackData.Added> added, List<EmiIngredient> removed, List<IndexStackData.Filter> filters) {
   public record Added(EmiIngredient added, EmiIngredient after) {
   }

   public record Filter(Predicate<String> filter) {
   }
}
