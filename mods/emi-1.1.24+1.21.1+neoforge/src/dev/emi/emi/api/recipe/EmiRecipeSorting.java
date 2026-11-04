package dev.emi.emi.api.recipe;

import dev.emi.emi.registry.EmiRecipeSorter;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Comparator;
import net.minecraft.resources.ResourceLocation;

public class EmiRecipeSorting {
   private static final Comparator<EmiRecipe> NONE = (a, b) -> 0;

   public static Comparator<EmiRecipe> none() {
      return NONE;
   }

   public static Comparator<EmiRecipe> identifier() {
      return (ar, br) -> {
         ResourceLocation a = ar.getId();
         ResourceLocation b = br.getId();
         if (a == null) {
            return b == null ? 0 : 1;
         } else {
            return b == null ? -1 : a.compareTo(b);
         }
      };
   }

   public static Comparator<EmiRecipe> compareOutputThenInput() {
      return (a, b) -> {
         int comp = compareStacks(EmiRecipeSorter.getOutput(a), EmiRecipeSorter.getOutput(b));
         return comp != 0 ? comp : compareStacks(EmiRecipeSorter.getInput(a), EmiRecipeSorter.getInput(b));
      };
   }

   public static Comparator<EmiRecipe> compareInputThenOutput() {
      return (a, b) -> {
         int comp = compareStacks(EmiRecipeSorter.getInput(a), EmiRecipeSorter.getInput(b));
         return comp != 0 ? comp : compareStacks(EmiRecipeSorter.getOutput(a), EmiRecipeSorter.getOutput(b));
      };
   }

   private static int compareStacks(IntList a, IntList b) {
      int min = Math.min(a.size(), b.size());

      for (int i = 0; i < min; i++) {
         int comparison = Integer.compare(a.getInt(i), b.getInt(i));
         if (comparison != 0) {
            return comparison;
         }
      }

      return Integer.compare(a.size(), b.size());
   }
}
