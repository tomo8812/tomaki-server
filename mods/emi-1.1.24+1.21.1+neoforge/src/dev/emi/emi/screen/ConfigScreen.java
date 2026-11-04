package dev.emi.emi.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.com.unascribed.qdcss.QDCSS;
import dev.emi.emi.config.ConfigEnum;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.IntGroup;
import dev.emi.emi.config.ScreenAlign;
import dev.emi.emi.config.SidebarPages;
import dev.emi.emi.config.SidebarSubpanels;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.input.EmiInput;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.screen.widget.SizedButtonWidget;
import dev.emi.emi.screen.widget.config.BooleanWidget;
import dev.emi.emi.screen.widget.config.ConfigEntryWidget;
import dev.emi.emi.screen.widget.config.ConfigJumpButton;
import dev.emi.emi.screen.widget.config.ConfigSearch;
import dev.emi.emi.screen.widget.config.EmiBindWidget;
import dev.emi.emi.screen.widget.config.EmiNameWidget;
import dev.emi.emi.screen.widget.config.EnumWidget;
import dev.emi.emi.screen.widget.config.GroupNameWidget;
import dev.emi.emi.screen.widget.config.IntGroupWidget;
import dev.emi.emi.screen.widget.config.IntWidget;
import dev.emi.emi.screen.widget.config.ListWidget;
import dev.emi.emi.screen.widget.config.ScreenAlignWidget;
import dev.emi.emi.screen.widget.config.SidebarPagesWidget;
import dev.emi.emi.screen.widget.config.SidebarSubpanelsWidget;
import dev.emi.emi.screen.widget.config.SubGroupNameWidget;
import dev.emi.emi.search.EmiSearch;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class ConfigScreen extends Screen {
   private static final int maxWidth = 240;
   private Screen last;
   private ConfigSearch search;
   public ListWidget list;
   public EmiBind activeBind;
   public int activeBindOffset;
   public int activeModifiers;
   public int lastModifier;
   public String originalConfig;
   public Button resetButton;

   public ConfigScreen(Screen last) {
      super(EmiPort.translatable("screen.emi.config"));
      this.last = last;
      this.originalConfig = EmiConfig.getSavedConfig();
   }

   public void setActiveBind(EmiBind bind, int offset) {
      this.activeBind = bind;
      this.activeBindOffset = offset;
      this.activeModifiers = 0;
      this.lastModifier = 0;
   }

   public void onClose() {
      EmiConfig.writeConfig();
      EmiSearch.update();
      Minecraft.getInstance().setScreen(this.last);
   }

   public static List<ClientTooltipComponent> getFieldTooltip(Field field) {
      EmiConfig.ConfigValue annot = field.getAnnotation(EmiConfig.ConfigValue.class);
      String key = "config.emi.tooltip." + annot.value().replace('-', '_');
      EmiConfig.Comment comment = field.getAnnotation(EmiConfig.Comment.class);
      List<ClientTooltipComponent> text;
      if (I18n.exists(key)) {
         text = Arrays.stream(I18n.get(key, new Object[0]).split("\n")).map(EmiPort::literal).map(EmiTooltipComponents::of).toList();
      } else if (comment != null) {
         text = Arrays.stream(comment.value().split("\n")).map(EmiPort::literal).map(EmiTooltipComponents::of).toList();
      } else {
         text = null;
      }

      return text == null ? List.of() : text;
   }

   protected void init() {
      super.init();
      int scroll = 0;
      String query = "";
      Set<String> collapsed = Sets.newHashSet();
      if (this.list != null) {
         scroll = (int)this.list.getScrollAmount();
         query = this.search.getSearch();

         for (ListWidget.Entry e : this.list.children()) {
            if (e instanceof GroupNameWidget g && g.collapsed) {
               collapsed.add(g.text.getString());
            }
         }
      }

      this.list = new ListWidget(this.minecraft, this.width, this.height, 40, this.height - 60);
      this.addRenderableOnly(new EmiNameWidget(this.width / 2, 16));
      int w = Math.min(400, this.width - 40) / 4 * 4;
      int x = (this.width - w) / 2;
      this.search = new ConfigSearch(x + 3, this.height - 51, w / 2 - 4, 18);
      this.addRenderableOnly(this.search.field);
      this.resetButton = EmiPort.newButton(x + 2, this.height - 30, w / 2 - 2, 20, EmiPort.translatable("gui.done"), button -> {
         EmiConfig.loadConfig(QDCSS.load("revert", this.originalConfig));
         Minecraft client = Minecraft.getInstance();
         this.init(client, client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight());
      });
      this.addRenderableWidget(EmiPort.newButton(x + w / 2 + 2, this.height - 30, w / 2 - 2, 20, EmiPort.translatable("gui.done"), button -> this.onClose()));
      this.addRenderableWidget(EmiPort.newButton(x + w / 2 + 2, this.height - 52, w / 2 - 24, 20, EmiPort.translatable("screen.emi.presets"), button -> {
         Minecraft client = Minecraft.getInstance();
         client.setScreen(new ConfigPresetScreen(this));
      }));
      this.addRenderableWidget(
         new SizedButtonWidget(
            x + w - 20,
            this.height - 52,
            20,
            20,
            164,
            0,
            () -> true,
            widget -> {
               EmiConfig.setGlobalState(!EmiConfig.useGlobalConfig);
               this.resize(this.minecraft, this.width, this.height);
            },
            () -> EmiConfig.useGlobalConfig ? 40 : 0,
            () -> Arrays.stream(I18n.get("tooltip.emi.config.global", new Object[0]).split("\n"))
               .map(s -> this.minecraft.font.getSplitter().splitLines(FormattedText.of(s), 240, Style.EMPTY))
               .flatMap(l -> l.stream())
               .map(v -> EmiPort.literal(v.getString()))
               .toList()
         )
      );
      this.addRenderableWidget(this.resetButton);
      this.addWidget(this.search.field);

      try {
         String lastGroup = "";
         GroupNameWidget lastGroupWidget = null;
         EmiConfig.ConfigGroup currentGroup = null;
         SubGroupNameWidget currentSubGroupWidget = null;
         Supplier<String> searchSupplier = () -> this.search.getSearch();

         for (final Field field : EmiConfig.class.getFields()) {
            EmiConfig.ConfigValue annot = field.getAnnotation(EmiConfig.ConfigValue.class);
            if (annot != null) {
               String group = annot.value().split("\\.")[0];
               if (!group.equals("persistent")) {
                  if (!group.equals(lastGroup)) {
                     lastGroup = group;
                     Component text = EmiPort.translatable("config.emi.group." + group.replace('-', '_'));
                     lastGroupWidget = new GroupNameWidget(group, text);
                     if (collapsed.contains(text.getString())) {
                        lastGroupWidget.collapsed = true;
                     }

                     this.list.addEntry(lastGroupWidget);
                  }

                  EmiConfig.ConfigGroup configGroup = field.getAnnotation(EmiConfig.ConfigGroup.class);
                  if (configGroup != null) {
                     currentGroup = configGroup;
                     Component text = EmiPort.translatable("config.emi.group." + configGroup.value().replace('-', '_'));
                     currentSubGroupWidget = new SubGroupNameWidget(configGroup.value(), text);
                     if (collapsed.contains(text.getString())) {
                        currentSubGroupWidget.collapsed = true;
                     }

                     currentSubGroupWidget.parent = lastGroupWidget;
                     this.list.addEntry(currentSubGroupWidget);
                  }

                  Predicate<?> predicate = EmiConfig.FILTERS.getOrDefault(annot.value(), v -> true);
                  Component translation = EmiPort.translatable("config.emi." + annot.value().replace('-', '_'));
                  ConfigEntryWidget entry = null;
                  if (field.getType() == boolean.class) {
                     entry = new BooleanWidget(translation, getFieldTooltip(field), searchSupplier, new ConfigScreen.Mutator<Boolean>() {
                        public Boolean getValue() {
                           try {
                              return field.getBoolean(null);
                           } catch (Exception var2) {
                              return false;
                           }
                        }

                        public void setValue(Boolean value) {
                           try {
                              field.setBoolean(null, value);
                           } catch (Exception var3) {
                           }
                        }

                        // $VF: synthetic method
                        // $VF: bridge method
                        @Override
                        public void setValue(Object param1) {
                           this.setValue((Boolean)var1);
                        }

                        // $VF: synthetic method
                        // $VF: bridge method
                        @Override
                        public Object getValue() {
                           return this.getValue();
                        }
                     });
                  } else if (field.getType() == int.class) {
                     entry = new IntWidget(translation, getFieldTooltip(field), searchSupplier, new ConfigScreen.Mutator<Integer>() {
                        public Integer getValue() {
                           try {
                              return field.getInt(null);
                           } catch (Exception var2) {
                              return -1;
                           }
                        }

                        public void setValue(Integer value) {
                           try {
                              field.setInt(null, value);
                           } catch (Exception var3) {
                           }
                        }

                        // $VF: synthetic method
                        // $VF: bridge method
                        @Override
                        public void setValue(Object param1) {
                           this.setValue((Integer)var1);
                        }

                        // $VF: synthetic method
                        // $VF: bridge method
                        @Override
                        public Object getValue() {
                           return this.getValue();
                        }
                     });
                  } else if (field.getType() == EmiBind.class) {
                     entry = new EmiBindWidget(this, getFieldTooltip(field), searchSupplier, (EmiBind)field.get(null));
                  } else if (field.getType() == ScreenAlign.class) {
                     entry = new ScreenAlignWidget(translation, getFieldTooltip(field), searchSupplier, this.objectMutator(field));
                  } else if (field.getType() == SidebarPages.class) {
                     entry = new SidebarPagesWidget(translation, getFieldTooltip(field), searchSupplier, this.objectMutator(field));
                  } else if (field.getType() == SidebarSubpanels.class) {
                     entry = new SidebarSubpanelsWidget(translation, getFieldTooltip(field), searchSupplier, this.objectMutator(field));
                  } else if (IntGroup.class.isAssignableFrom(field.getType())) {
                     entry = new IntGroupWidget(translation, getFieldTooltip(field), searchSupplier, this.objectMutator(field));
                  } else if (ConfigEnum.class.isAssignableFrom(field.getType())) {
                     entry = new EnumWidget(translation, getFieldTooltip(field), searchSupplier, this.objectMutator(field), (Predicate<ConfigEnum>)predicate);
                  }

                  boolean endGroup = field.getAnnotation(EmiConfig.ConfigGroupEnd.class) != null;
                  if (entry != null) {
                     entry.group = currentGroup;
                     entry.endGroup = endGroup;
                     this.list.addEntry(entry);
                     if (lastGroupWidget != null) {
                        lastGroupWidget.children.add(entry);
                        entry.parentGroups.add(lastGroupWidget);
                     }

                     if (currentSubGroupWidget != null) {
                        currentSubGroupWidget.children.add(entry);
                        entry.parentGroups.add(currentSubGroupWidget);
                     }
                  }

                  if (endGroup) {
                     currentGroup = null;
                     currentSubGroupWidget = null;
                  }
               }
            }
         }
      } catch (Exception e) {
         EmiLog.error("Error initializing config screen", e);
      }

      this.addWidget(this.list);
      this.list.setScrollAmount(scroll);
      this.search.setText(query);
      this.addJumpButtons();
      this.updateChanges();
   }

   private void addJumpButtons() {
      List<String> jumps = Lists.newArrayList(
         new String[]{
            "general",
            "general.search",
            "ui",
            "ui.left-sidebar",
            "ui.right-sidebar",
            "ui.top-sidebar",
            "ui.bottom-sidebar",
            "binds",
            "binds.crafts",
            "binds.cheats",
            "dev"
         }
      );
      List<List<String>> removes = List.of(
         List.of("binds.cheats"),
         List.of("general.search"),
         List.of("ui.top-sidebar", "ui.bottom-sidebar"),
         List.of("binds.crafts"),
         List.of("ui.left-sidebar", "ui.right-sidebar")
      );
      int space = this.list.getLogicalHeight() - 10;

      for (List<String> r : removes) {
         if (jumps.size() * 16 > space) {
            jumps.removeAll(r);
         }
      }

      int y = 40 + (this.list.getLogicalHeight() - jumps.size() * 16) / 2;
      int u = 0;
      int v = -16;

      for (String s : jumps) {
         boolean newGroup = !s.contains(".");
         if (newGroup) {
            v += 16;
            u = 0;
         } else {
            u += 16;
         }

         this.addRenderableWidget(
            new ConfigJumpButton(2 + (newGroup ? 0 : 8), y, u, v, w -> this.jump(s), List.of(EmiPort.translatable("config.emi.group." + s.replace('-', '_'))))
         );
         y += 16;
      }
   }

   public void jump(String jump) {
      for (ListWidget.Entry e : this.list.children()) {
         if (e instanceof ConfigEntryWidget c) {
            for (GroupNameWidget p : c.parentGroups) {
               if (p.id.equals(jump)) {
                  this.list.centerScrollOn(e);
                  return;
               }
            }
         }
      }
   }

   public <T> ConfigScreen.Mutator<T> objectMutator(Field field) {
      return new ConfigScreen.Mutator<T>() {
         @Override
         public T getValue() {
            try {
               return (T)field.get(null);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }

         @Override
         public void setValue(T en) {
            try {
               field.set(null, en);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }
      };
   }

   public void updateChanges() {
      String[] oLines = this.originalConfig.split("\n\n");
      String[] cLines = EmiConfig.getSavedConfig().split("\n\n");
      int different = 0;

      for (int i = 0; i < oLines.length && i < cLines.length; i++) {
         if (!oLines[i].equals(cLines[i])) {
            different++;
         }
      }

      this.resetButton.active = different > 0;
      this.resetButton.setMessage(EmiPort.translatable("screen.emi.config.reset", different));
   }

   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      this.list.setScrollAmount(this.list.getScrollAmount());
      super.render(context.raw(), mouseX, mouseY, delta);
      this.list.render(context.raw(), mouseX, mouseY, delta);
      if (this.list.getHoveredEntry() != null) {
         EmiRenderHelper.drawTooltip(this, context, this.list.getHoveredEntry().getTooltip(mouseX, mouseY), mouseX, mouseY, Math.min(this.width / 2 - 16, 240));
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.activeBind != null) {
         this.pushModifier(0);
         this.activeBind.setBind(this.activeBindOffset, new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(button), this.activeModifiers));
         this.activeBind = null;
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   private void pushModifier(int lastModifier) {
      this.activeModifiers = this.activeModifiers | EmiInput.maskFromCode(this.lastModifier);
      this.lastModifier = lastModifier;
      this.activeModifiers = this.activeModifiers & ~EmiInput.maskFromCode(lastModifier);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.activeBind != null) {
         if (EmiInput.maskFromCode(keyCode) != 0) {
            this.pushModifier(keyCode);
         } else {
            this.pushModifier(0);
            if (keyCode == 256) {
               this.activeBind.setBind(this.activeBindOffset, new EmiBind.ModifiedKey(InputConstants.UNKNOWN, 0));
            } else {
               this.activeBind.setBind(this.activeBindOffset, new EmiBind.ModifiedKey(Type.KEYSYM.getOrCreate(keyCode), this.activeModifiers));
            }

            this.activeBind = null;
            this.updateChanges();
         }

         return true;
      } else {
         if (keyCode == 258) {
            return false;
         }

         if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
         }

         if (this.getFocused() instanceof EditBox tfw && tfw.isFocused()) {
            if (keyCode == 256) {
               EmiPort.focus(tfw, false);
               return true;
            }
         } else {
            if (keyCode == 256) {
               this.onClose();
               return true;
            }

            if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
               this.onClose();
               return true;
            }
         }

         return false;
      }
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      if (this.activeBind != null) {
         this.activeModifiers = this.activeModifiers & ~EmiInput.maskFromCode(keyCode);
         if (keyCode == this.lastModifier) {
            this.activeBind.setBind(this.activeBindOffset, new EmiBind.ModifiedKey(Type.KEYSYM.getOrCreate(keyCode), this.activeModifiers));
            this.activeBind = null;
         }

         return true;
      } else {
         return super.keyReleased(keyCode, scanCode, modifiers);
      }
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public abstract class Mutator<T> {
      protected abstract T getValue();

      protected abstract void setValue(T var1);

      public T get() {
         return this.getValue();
      }

      public void set(T value) {
         this.setValue(value);
         ConfigScreen.this.updateChanges();
      }
   }
}
