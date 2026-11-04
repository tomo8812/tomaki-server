package dev.emi.emi.api;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import java.util.function.Predicate;

public interface EmiInitRegistry {
   <T extends EmiIngredient> void addIngredientSerializer(Class<T> var1, EmiIngredientSerializer<T> var2);

   void disableStack(EmiStack var1);

   void disableStacks(Predicate<EmiStack> var1);

   void addRegistryAdapter(EmiRegistryAdapter<?> var1);
}
