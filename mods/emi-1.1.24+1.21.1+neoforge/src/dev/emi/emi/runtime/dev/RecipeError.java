package dev.emi.emi.runtime.dev;

import java.util.List;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public record RecipeError(RecipeError.Severity severity, List<ClientTooltipComponent> tooltip) {
   public enum Severity {
      ERROR,
      WARNING;

      // $VF: synthetic method
      private static RecipeError.Severity[] $values() {
         return new RecipeError.Severity[]{ERROR, WARNING};
      }
   }
}
