package dev.emi.emi.screen.widget.config;

import com.google.common.collect.Lists;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public abstract class ConfigEntryWidget extends ListWidget.Entry {
   private final Component name;
   private final List<ClientTooltipComponent> tooltip;
   protected final Supplier<String> search;
   private final int height;
   public EmiConfig.ConfigGroup group;
   public boolean endGroup = false;
   private List<? extends GuiEventListener> children = List.of();
   public List<GroupNameWidget> parentGroups = Lists.newArrayList();

   public ConfigEntryWidget(Component name, List<ClientTooltipComponent> tooltip, Supplier<String> search, int height) {
      this.name = name;
      this.tooltip = tooltip;
      this.search = search;
      this.height = height;
   }

   public void setChildren(List<? extends GuiEventListener> children) {
      this.children = children;
   }

   public void update(int y, int x, int width, int height) {
   }

   @Override
   public void render(GuiGraphics raw, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      if (this.group != null) {
         context.fill(x + 4, y + height / 2 - 1, 6, 2, -1);
         if (this.endGroup) {
            context.fill(x + 2, y - 4, 2, height / 2 + 5, -1);
         } else {
            context.fill(x + 2, y - 4, 2, height + 4, -1);
         }

         x += 10;
         width -= 10;
      }

      this.update(y, x, width, height);
      context.fill(x, y, width, height, 1711276032);
      context.drawTextWithShadow(this.name, x + 6, y + 10 - 9 / 2, 16777215);

      for (GuiEventListener element : this.children()) {
         if (element instanceof Renderable drawable) {
            drawable.render(context.raw(), mouseX, mouseY, delta);
         }
      }
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      return this.tooltip;
   }

   public String getSearchableText() {
      return this.name.getString();
   }

   public boolean isParentVisible() {
      for (GroupNameWidget g : this.parentGroups) {
         if (g.collapsed) {
            return false;
         }
      }

      return true;
   }

   public boolean isVisible() {
      String s = this.search.get().toLowerCase();
      if (this.getSearchableText().toLowerCase().contains(s)) {
         return true;
      }

      for (GroupNameWidget g : this.parentGroups) {
         if (g.text.getString().toLowerCase().contains(s)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int getHeight() {
      return this.isParentVisible() && this.isVisible() ? this.height : 0;
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }
}
