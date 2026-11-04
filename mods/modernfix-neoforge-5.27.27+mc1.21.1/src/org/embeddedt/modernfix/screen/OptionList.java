package org.embeddedt.modernfix.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent.Action;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;
import org.embeddedt.modernfix.core.config.Option;
import org.embeddedt.modernfix.core.config.OptionCategories;
import org.embeddedt.modernfix.core.config.OptionType;
import org.embeddedt.modernfix.platform.ModernFixPlatformHooks;

public class OptionList extends ContainerObjectSelectionList<OptionList.Entry> {
   private int maxNameWidth = 0;
   private static final int DEPTH_OFFSET = 20;
   private static final Component OPTION_ON = Component.translatable("modernfix.option.on").withStyle(style -> style.withColor(ChatFormatting.GREEN));
   private static final Component OPTION_OFF = Component.translatable("modernfix.option.off").withStyle(style -> style.withColor(ChatFormatting.RED));
   private static final Set<String> OPTIONS_MISSING_HELP = new HashSet<>();
   private ModernFixConfigScreen mainScreen;
   private final Set<Option<?>> addedOptions = new HashSet<>();

   private static MutableComponent getOptionComponent(Option<?> option) {
      String friendlyKey = "modernfix.option.name." + option.getName();
      MutableComponent baseComponent = Component.literal(option.getSelfName());
      return I18n.exists(friendlyKey)
         ? Component.translatable(friendlyKey).withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, baseComponent)))
         : baseComponent;
   }

   public void updateOptionEntryStatuses() {
      for (OptionList.Entry e : this.children()) {
         if (e instanceof OptionList.OptionEntry) {
            ((OptionList.OptionEntry)e).updateStatus();
         }
      }
   }

   private void addOption(Option<?> option) {
      if (option.getType() == OptionType.BOOLEAN) {
         if (this.addedOptions.add(option)) {
            int w = this.minecraft.font.width(getOptionComponent(option)) + 20 * option.getDepth();
            this.maxNameWidth = Math.max(w, this.maxNameWidth);
            this.addEntry(new OptionList.OptionEntry(option.getName(), option));
            ModernFixMixinPlugin.instance
               .config
               .getOptionMap()
               .values()
               .stream()
               .filter(subOption -> subOption.getParent() == option)
               .sorted(Comparator.comparing(Option::getName))
               .forEach(this::addOption);
         }
      }
   }

   public OptionList(ModernFixConfigScreen arg, Minecraft arg2) {
      super(arg2, arg.width + 45, arg.height - 52, 20, 20);
      this.mainScreen = arg;
      Multimap<String, Option<?>> optionsByCategory = ModernFixMixinPlugin.instance.config.getOptionCategoryMap();

      for (String category : OptionCategories.getCategoriesInOrder()) {
         String categoryTranslationKey = "modernfix.option.category." + category;
         this.addEntry(
            new OptionList.CategoryEntry(
               Component.translatable(categoryTranslationKey)
                  .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.translatable(categoryTranslationKey + ".description"))))
            )
         );
         optionsByCategory.get(category).stream().filter(key -> {
            int dotCount = 0;

            for (char c : key.getName().toCharArray()) {
               if (c == '.') {
                  dotCount++;
               }
            }

            return dotCount >= 2;
         }).sorted(Comparator.comparing(Option::getName)).forEach(this::addOption);
      }
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 15 + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 32;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   public GuiEventListener getFocused() {
      return super.getFocused();
   }

   class CategoryEntry extends OptionList.Entry {
      private final Component name;
      private final int width;

      public CategoryEntry(Component component) {
         this.name = component;
         this.width = OptionList.this.minecraft.font.width(this.name);
      }

      public void render(
         GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks
      ) {
         Font var10000 = OptionList.this.minecraft.font;
         float x = OptionList.this.minecraft.screen.width / 2 - this.width / 2;
         int y = top + height - 10;
         guiGraphics.drawString(var10000, this.name, (int)x, y, 16777215);
      }

      public boolean changeFocus(boolean focus) {
         return false;
      }

      public List<? extends GuiEventListener> children() {
         return Collections.emptyList();
      }

      public List<? extends NarratableEntry> narratables() {
         return Collections.emptyList();
      }
   }

   public abstract static class Entry extends net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry<OptionList.Entry> {
   }

   class OptionEntry extends OptionList.Entry {
      private final String name;
      private final Button toggleButton;
      private final Button helpButton;
      private final Option<Boolean> option;

      public OptionEntry(String optionName, Option<?> option) {
         this.name = optionName;
         this.option = option.asBoolean();
         Tooltip toggleTooltip = null;
         if (this.option.isModDefined()) {
            String disablingMods = String.join(", ", this.option.getDefiningMods());
            toggleTooltip = Tooltip.create(
               Component.translatable("modernfix.option." + (this.option.getValue() ? "enabled" : "disabled"))
                  .append(Component.translatable("modernfix.option.mod_override", new Object[]{disablingMods}))
            );
         }

         this.toggleButton = new Builder(Component.literal(""), arg -> {
            this.option.setValue(!this.option.getValue(), !this.option.isUserDefined());

            try {
               ModernFixMixinPlugin.instance.config.save();
               if (!OptionList.this.mainScreen.madeChanges) {
                  OptionList.this.mainScreen.madeChanges = true;
               }
            } catch (IOException e) {
               this.option.setValue(!this.option.getValue(), !this.option.isUserDefined());
               ModernFix.LOGGER.error("Unable to save config", e);
            }

            OptionList.this.updateOptionEntryStatuses();
         }).tooltip(toggleTooltip).pos(0, 0).size(55, 20).build();
         this.updateStatus();
         this.helpButton = new Builder(Component.literal("?"), arg -> {
            OptionList.this.mainScreen.setLastScrollAmount(OptionList.this.getScrollAmount());
            Minecraft.getInstance().setScreen(new ModernFixOptionInfoScreen(OptionList.this.mainScreen, optionName));
         }).pos(75, 0).size(20, 20).build();
         if (!I18n.exists("modernfix.option." + optionName)) {
            this.helpButton.active = false;
            if (ModernFixPlatformHooks.INSTANCE.isDevEnv() && OptionList.OPTIONS_MISSING_HELP.add(optionName)) {
               ModernFix.LOGGER.warn("Missing help for {}", optionName);
            }
         }
      }

      void updateStatus() {
         this.toggleButton.active = !this.option.isModDefined() && !this.option.isEffectivelyDisabledByParent();
      }

      public void render(
         GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks
      ) {
         MutableComponent nameComponent = OptionList.getOptionComponent(this.option);
         if (this.option.isUserDefined()) {
            nameComponent = nameComponent.withStyle(style -> style.withItalic(true)).append(Component.translatable("modernfix.config.not_default"));
         }

         float textX = left + 20 * this.option.getDepth() + 160 - OptionList.this.maxNameWidth;
         float textY = top + height / 2 - 4;
         guiGraphics.drawString(OptionList.this.minecraft.font, nameComponent, (int)textX, (int)textY, 16777215);
         this.toggleButton.setPosition(left + 175, top);
         this.toggleButton.setMessage(this.getOptionMessage(this.option));
         this.toggleButton.render(guiGraphics, mouseX, mouseY, partialTicks);
         this.helpButton.setPosition(left + 175 + 55, top);
         this.helpButton.render(guiGraphics, mouseX, mouseY, partialTicks);
      }

      private Component getOptionMessage(Option<Boolean> option) {
         return option.getValue() ? OptionList.OPTION_ON : OptionList.OPTION_OFF;
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.toggleButton, this.helpButton);
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         for (GuiEventListener listener : this.children()) {
            if (listener.mouseClicked(mouseX, mouseY, button)) {
               return true;
            }
         }

         return false;
      }

      public boolean mouseReleased(double mouseX, double mouseY, int button) {
         for (GuiEventListener listener : this.children()) {
            if (listener.mouseReleased(mouseX, mouseY, button)) {
               return true;
            }
         }

         return false;
      }

      public List<? extends NarratableEntry> narratables() {
         return Collections.emptyList();
      }
   }
}
