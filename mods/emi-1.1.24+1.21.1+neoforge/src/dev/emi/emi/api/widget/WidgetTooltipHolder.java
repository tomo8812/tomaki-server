package dev.emi.emi.api.widget;

import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public interface WidgetTooltipHolder<T> {
   T tooltip(BiFunction<Integer, Integer, List<ClientTooltipComponent>> var1);

   default T tooltip(List<ClientTooltipComponent> tooltip) {
      return this.tooltip((mx, my) -> tooltip);
   }

   default T tooltipText(BiFunction<Integer, Integer, List<Component>> tooltipSupplier) {
      return this.tooltip(
         (x, y) -> tooltipSupplier.apply(x, y).stream().map(Component::getVisualOrderText).<ClientTooltipComponent>map(ClientTooltipComponent::create).toList()
      );
   }

   default T tooltipText(List<Component> tooltip) {
      return this.tooltip(tooltip.stream().map(Component::getVisualOrderText).<ClientTooltipComponent>map(ClientTooltipComponent::create).toList());
   }
}
