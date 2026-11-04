package dev.emi.emi.data;

import dev.emi.emi.api.stack.EmiIngredient;
import java.util.List;
import net.minecraft.network.chat.Component;

public record EmiAlias(List<EmiIngredient> stacks, List<String> keys) {
   public record Baked(List<EmiIngredient> stacks, List<Component> text) {
   }
}
