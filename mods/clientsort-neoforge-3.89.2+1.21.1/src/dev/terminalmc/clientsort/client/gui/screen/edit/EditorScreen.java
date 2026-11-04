package dev.terminalmc.clientsort.client.gui.screen.edit;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.client.config.ClassPolicy;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.config.Policy;
import dev.terminalmc.clientsort.client.config.Vec2i;
import dev.terminalmc.clientsort.client.gui.screen.config.ConfigScreenProvider;
import dev.terminalmc.clientsort.client.gui.widget.TriggerButton;
import dev.terminalmc.clientsort.mixin.client.accessor.AbstractContainerScreenAccessor;
import dev.terminalmc.clientsort.util.Localization;
import dev.terminalmc.clientsort.util.inject.ISlot;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EditorScreen extends Screen {
   private final Screen lastScreen;
   private final AbstractContainerScreen<?> underlay;
   private final boolean isPlayerInv;
   private final LinkedList<TriggerButton> buttons = new LinkedList<>();
   private boolean offsetFromSlot = false;
   @Nullable
   private Operation autoOp = null;
   private boolean autoOpOther = false;
   public final Set<Integer> ignoredSlots = new TreeSet<>();
   private TriggerButton rep;
   private String lowestPolicyClassName;
   private boolean dragging;

   public EditorScreen(AbstractContainerScreen<?> underlay, boolean isPlayerInv, TriggerButton button) {
      this(underlay, isPlayerInv, button, underlay);
   }

   public EditorScreen(AbstractContainerScreen<?> underlay, boolean isPlayerInv, TriggerButton button, Screen lastScreen) {
      super(Localization.localized("title", "positionEditor"));
      this.font = Minecraft.getInstance().font;
      this.lastScreen = lastScreen;
      this.underlay = underlay;
      this.isPlayerInv = isPlayerInv;
      this.rep = button;
      this.buttons.add(button);
   }

   public void init() {
      super.init();
      this.underlay.init(Minecraft.getInstance(), this.width, this.height);
      if (!this.reloadButtonsAndIgnoredSlots()) {
         this.clearWidgets();
      } else {
         this.rebuildGui();
      }
   }

   private boolean reloadButtonsAndIgnoredSlots() {
      this.buttons.clear();
      this.ignoredSlots.clear();
      this.buttons.addAll(Config.options().justifyButtonsTopLeft ? this.getButtons() : this.getButtons().reversed());
      if (this.buttons.size() != 4) {
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG
               .error("Failed to reload buttons on PositionEditScreen: Button list is too small (expected: {}, actual: {})", 4, this.buttons.size());
         }

         return false;
      } else {
         this.rep = this.buttons.getFirst();
         ClassPolicy policy = Config.options().classPolicies.get(this.rep.activePolicyKey);
         if (policy != null) {
            this.buttons.forEach(button -> button.active = button.getPolicyStatus(policy));
            this.offsetFromSlot = policy.offsetFromSlot();
            this.autoOp = policy.autoOp();
            this.autoOpOther = policy.autoOpOther();
            this.ignoredSlots.addAll(policy.ignoredSlots());
         }

         Object keyObject = this.rep.container instanceof SimpleContainer ? this.underlay.getMenu() : this.rep.container;
         this.lowestPolicyClassName = keyObject.getClass().getName();
         return true;
      }
   }

   protected abstract LinkedList<TriggerButton> getButtons();

   private void rebuildGui() {
      this.clearWidgets();
      Minecraft mc = Minecraft.getInstance();
      StringWidget titleWidget = new StringWidget(0, 2, this.width, 9, this.title, this.font);
      this.addRenderableWidget(titleWidget);
      int numButtons = 14;
      int x = 2;
      int movingY = this.height - 21 * numButtons;
      int width = 100;
      int height = 20;
      Button instructionsButton = Button.builder(Localization.localized("editor", "instructions"), button -> {})
         .tooltip(
            Tooltip.create(
               Localization.localized("editor", "instructions.tooltip.1")
                  .append("\n\n")
                  .append(Localization.localized("editor", "instructions.tooltip.2"))
                  .append("\n\n")
                  .append(Localization.localized("editor", "instructions.tooltip.3"))
                  .append("\n\n")
                  .append(Localization.localized("editor", "instructions.tooltip.4"))
                  .append("\n\n")
                  .append(Localization.localized("editor", "instructions.tooltip.5"))
            )
         )
         .pos(x, movingY)
         .size(width, height)
         .build();
      instructionsButton.active = false;
      this.addRenderableWidget(instructionsButton);
      movingY += 21;
      Button copyPolicyKeyButton = Button.builder(
            Localization.localized("editor", "copyPolicyKey"),
            button -> mc.keyboardHandler.setClipboard(this.rep.activePolicyKey == null ? "null" : this.rep.activePolicyKey)
         )
         .pos(x, movingY)
         .size(width, height)
         .build();
      copyPolicyKeyButton.active = this.rep.activePolicyKey != null;
      this.addRenderableWidget(copyPolicyKeyButton);
      movingY += 21;
      Button splitPolicyClassButton = Button.builder(
            Localization.localized("editor", "splitPolicyClass"),
            button -> Minecraft.getInstance()
               .setScreen(
                  new ConfirmScreen(
                     confirm -> {
                        if (confirm) {
                           Config.options()
                              .classPolicies
                              .put(
                                 ClassPolicy.getKey(this.lowestPolicyClassName, null),
                                 new ClassPolicy(
                                    this.lowestPolicyClassName,
                                    null,
                                    this.buttons.getFirst().offset,
                                    this.offsetFromSlot,
                                    this.buttons.getFirst().operationAllowed
                                       ? (this.buttons.getFirst().active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND)
                                       : Policy.NONE,
                                    this.buttons.get(1).operationAllowed ? (this.buttons.get(1).active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND) : Policy.NONE,
                                    this.buttons.get(2).operationAllowed ? (this.buttons.get(2).active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND) : Policy.NONE,
                                    this.buttons.get(3).operationAllowed ? (this.buttons.get(3).active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND) : Policy.NONE,
                                    this.autoOp,
                                    this.autoOpOther,
                                    new TreeSet<>(this.ignoredSlots)
                                 )
                              );
                           Config.save();
                           this.init();
                        }

                        Minecraft.getInstance().setScreen(this);
                     },
                     Localization.localized("title", "confirm.splitPolicyClass"),
                     Localization.localized(
                        "message",
                        "confirm.splitPolicyClass",
                        Component.literal(this.rep.activePolicyKey == null ? this.lowestPolicyClassName : this.rep.activePolicyKey)
                           .withStyle(ChatFormatting.GOLD),
                        Component.literal(this.lowestPolicyClassName).withStyle(ChatFormatting.GOLD)
                     )
                  )
               )
         )
         .tooltip(Tooltip.create(Localization.localized("editor", "splitPolicyClass.tooltip")))
         .pos(x, movingY)
         .size(width, height)
         .build();
      splitPolicyClassButton.active = this.rep.activePolicyKey != null
         && !((String)ClassPolicy.parseKey(this.rep.activePolicyKey).getFirst()).equals(this.lowestPolicyClassName);
      this.addRenderableWidget(splitPolicyClassButton);
      movingY += 21;
      Button splitPolicyTitleButton = Button.builder(
            Localization.localized("editor", "splitPolicyTitle"),
            button -> {
               Component invTitle = this.isPlayerInv
                  ? ((AbstractContainerScreenAccessor)this.underlay).clientsort$getPlayerInventoryTitle()
                  : this.underlay.getTitle();
               Minecraft.getInstance()
                  .setScreen(
                     new ConfirmScreen(
                        confirm -> {
                           if (confirm) {
                              if (ClassPolicy.hasInvTitle(this.rep.activePolicyKey)) {
                                 dev.terminalmc.clientsort.client.ClientSort.LOG
                                    .error("Cannot split policy with title: activePolicyKey '{}' already has title.", this.rep.activePolicyKey);
                                 return;
                              }

                              Config.options()
                                 .classPolicies
                                 .put(
                                    ClassPolicy.getKey(this.rep.activePolicyKey, invTitle.getString()),
                                    new ClassPolicy(
                                       this.rep.activePolicyKey,
                                       invTitle.getString(),
                                       this.buttons.getFirst().offset,
                                       this.offsetFromSlot,
                                       this.buttons.getFirst().operationAllowed
                                          ? (this.buttons.getFirst().active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND)
                                          : Policy.NONE,
                                       this.buttons.get(1).operationAllowed
                                          ? (this.buttons.get(1).active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND)
                                          : Policy.NONE,
                                       this.buttons.get(2).operationAllowed
                                          ? (this.buttons.get(2).active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND)
                                          : Policy.NONE,
                                       this.buttons.get(3).operationAllowed
                                          ? (this.buttons.get(3).active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND)
                                          : Policy.NONE,
                                       this.autoOp,
                                       this.autoOpOther,
                                       new TreeSet<>(this.ignoredSlots)
                                    )
                                 );
                              Config.save();
                              this.init();
                           }

                           Minecraft.getInstance().setScreen(this);
                        },
                        Localization.localized("title", "confirm.splitPolicyTitle"),
                        Localization.localized(
                           "message",
                           "confirm.splitPolicyTitle",
                           Component.literal(this.rep.activePolicyKey == null ? this.lowestPolicyClassName : this.rep.activePolicyKey)
                              .withStyle(ChatFormatting.GOLD),
                           Component.literal(invTitle.getString()).withStyle(ChatFormatting.GOLD)
                        )
                     )
                  );
            }
         )
         .tooltip(Tooltip.create(Localization.localized("editor", "splitPolicyTitle.tooltip")))
         .pos(x, movingY)
         .size(width, height)
         .build();
      splitPolicyTitleButton.active = this.rep.activePolicyKey != null && !ClassPolicy.hasInvTitle(this.rep.activePolicyKey);
      this.addRenderableWidget(splitPolicyTitleButton);
      movingY += 21;
      CycleButton<Boolean> switchOffsetTypeButton = CycleButton.booleanBuilder(
            Localization.localized("editor", "switchOffsetType.slot"), Localization.localized("editor", "switchOffsetType.edge")
         )
         .withInitialValue(this.offsetFromSlot)
         .withTooltip(v -> Tooltip.create(Localization.localized("editor", "switchOffsetType.tooltip." + (v ? "slot" : "edge"))))
         .create(x, movingY, width, height, Localization.localized("editor", "switchOffsetType"), (button, v) -> {
            this.offsetFromSlot = v;
            this.buttons.forEach(b -> b.offsetFromSlot = v);
         });
      this.addRenderableWidget(switchOffsetTypeButton);
      movingY += 21;
      Button moveToDefaultButton = Button.builder(Localization.localized("editor", "moveToDefault"), button -> {
         Vec2i before = this.buttons.getFirst().offset;
         this.buttons.getFirst().offset = Config.options().layoutOffset;
         this.repositionButtons(this.buttons.getFirst(), before);
      }).tooltip(Tooltip.create(Localization.localized("editor", "moveToDefault.tooltip"))).pos(x, movingY).size(width, height).build();
      this.addRenderableWidget(moveToDefaultButton);
      movingY += 21;
      Button saveAsDefaultButton = Button.builder(
            Localization.localized("editor", "saveAsDefault"), button -> Minecraft.getInstance().setScreen(new ConfirmScreen(confirm -> {
               if (confirm) {
                  Config.options().layoutOffset = this.buttons.getFirst().offset;
                  Config.save();
                  this.init();
               }

               Minecraft.getInstance().setScreen(this);
            }, Localization.localized("title", "confirm.saveAsDefault"), Localization.localized("message", "confirm.saveAsDefault")))
         )
         .tooltip(Tooltip.create(Localization.localized("editor", "saveAsDefault.tooltip")))
         .pos(x, movingY)
         .size(width, height)
         .build();
      this.addRenderableWidget(saveAsDefaultButton);
      movingY += 21;
      CycleButton<Boolean> autoOpOtherButton = CycleButton.booleanBuilder(
            Component.literal("1").withStyle(ChatFormatting.RED), Component.literal("0").withStyle(ChatFormatting.GREEN)
         )
         .withInitialValue(this.autoOpOther)
         .withTooltip(v -> Tooltip.create(Localization.localized("editor", "autoOp.other.tooltip")))
         .displayOnlyValue()
         .create(x + width - 10, movingY, 10, height, Component.empty(), (b, v) -> this.autoOpOther = v);
      this.addRenderableWidget(autoOpOtherButton);
      CycleButton<Integer> autoOpButton = CycleButton.builder(
            v -> v == 0 ? Localization.localized("editor", "autoOp.none") : Localization.localized("key", "op." + Operation.values()[v - 1].translationKey)
         )
         .withInitialValue(this.autoOp == null ? 0 : List.of(Operation.values()).indexOf(this.autoOp) + 1)
         .withTooltip(v -> Tooltip.create(Localization.localized("editor", "autoOp.tooltip")))
         .withValues(new Integer[]{0, 1, 2, 3, 4})
         .create(x, movingY, width - 10, height, Localization.localized("editor", "autoOp"), (b, v) -> this.autoOp = v == 0 ? null : Operation.values()[v - 1]);
      this.addRenderableWidget(autoOpButton);
      movingY += 21;
      Button toggleButtonsVisibleButton = Button.builder(Localization.localized("editor", "toggleVisibility"), button -> {
         boolean status = this.buttons.stream().noneMatch(b -> b.active);
         this.buttons.forEach(b -> b.active = status);
      }).tooltip(Tooltip.create(Localization.localized("editor", "toggleVisibility.tooltip"))).pos(x, movingY).size(width, height).build();
      this.addRenderableWidget(toggleButtonsVisibleButton);
      movingY += 21;
      Button toggleSlotsIgnoredButton = Button.builder(Localization.localized("editor", "toggleIgnoreSlots"), button -> {
         if (this.ignoredSlots.isEmpty()) {
            for (Slot slot : this.underlay.getMenu().slots) {
               Object object = ClientSort.getObj(slot, this.underlay.getMenu());
               if (object != null && object.getClass().getName().equals(this.lowestPolicyClassName)) {
                  this.ignoredSlots.add(((ISlot)slot).clientsort$getIndexInContainer());
               }
            }
         } else {
            this.ignoredSlots.clear();
         }
      }).tooltip(Tooltip.create(Localization.localized("editor", "toggleIgnoreSlots.tooltip"))).pos(x, movingY).size(width, height).build();
      this.addRenderableWidget(toggleSlotsIgnoredButton);
      movingY += 21;
      Button undoChangesButton = Button.builder(Localization.localized("editor", "undoChanges"), button -> this.init())
         .tooltip(Tooltip.create(Localization.localized("editor", "undoChanges.tooltip")))
         .pos(x, movingY)
         .size(width, height)
         .build();
      this.addRenderableWidget(undoChangesButton);
      movingY += 21;
      Button reselectButton = Button.builder(Localization.localized("editor", "reselect"), button -> {
         this.onClose();
         Minecraft.getInstance().setScreen(new SelectorScreen(this.underlay, this));
      }).tooltip(Tooltip.create(Localization.localized("editor", "reselect.tooltip"))).pos(x, movingY).size(width, height).build();
      this.addRenderableWidget(reselectButton);
      movingY += 21;
      Button configButton = Button.builder(
            Localization.localized("editor", "openConfig"), button -> Minecraft.getInstance().setScreen(ConfigScreenProvider.getConfigScreen(this))
         )
         .tooltip(Tooltip.create(Localization.localized("editor", "openConfig.tooltip")))
         .pos(x, movingY)
         .size(width, height)
         .build();
      this.addRenderableWidget(configButton);
      movingY += 21;
      Button cancelButton = Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose()).pos(x, movingY).size(width / 2, height).build();
      this.addRenderableWidget(cancelButton);
      Button doneButton = Button.builder(CommonComponents.GUI_DONE, button -> this.saveAndClose())
         .pos(x + cancelButton.getWidth(), movingY)
         .size(width - cancelButton.getWidth(), height)
         .build();
      this.addRenderableWidget(doneButton);
   }

   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.underlay.render(graphics, mouseX, mouseY, partialTick);
      super.render(graphics, mouseX, mouseY, partialTick);

      for (Slot slot : this.underlay.getMenu().slots) {
         Object object = ClientSort.getObj(slot, this.underlay.getMenu());
         if (object != null
            && object.getClass().getName().equals(this.lowestPolicyClassName)
            && this.ignoredSlots.contains(((ISlot)slot).clientsort$getIndexInContainer())) {
            graphics.drawString(
               Minecraft.getInstance().font,
               "❌",
               ((AbstractContainerScreenAccessor)this.underlay).clientsort$getLeftPos() + slot.x,
               ((AbstractContainerScreenAccessor)this.underlay).clientsort$getTopPos() + slot.y,
               -65536
            );
         }
      }

      if (!this.buttons.isEmpty()) {
         this.drawLineFor(graphics, this.buttons.getFirst());
         Vec2i offset = this.buttons.getFirst().offset;
         graphics.drawString(this.font, Localization.localized("editor", "offset", offset.x(), offset.y()).getString(), 105, this.height - (9 + 1) * 4, -1);
         graphics.drawString(
            this.font,
            Localization.localized(
               "editor", "policyKey.current", this.rep.activePolicyKey == null ? Localization.localized("editor", "policyKey.unset") : this.rep.activePolicyKey
            ),
            105,
            this.height - (9 + 1) * 3,
            -1
         );
         graphics.drawString(this.font, Localization.localized("editor", "policyKey.menu", this.lowestPolicyClassName), 105, this.height - (9 + 1) * 2, -1);
         graphics.drawString(
            this.font, Localization.localized("editor", "policyKey.screen", this.underlay.getClass().getName()), 105, this.height - (9 + 1), -1
         );

         for (TriggerButton cb : this.buttons) {
            cb.renderWidget(graphics, mouseX, mouseY, partialTick);
         }
      }
   }

   protected void renderBlurredBackground(float partialTick) {
      int original = (Integer)Minecraft.getInstance().options.menuBackgroundBlurriness().get();
      Minecraft.getInstance().options.menuBackgroundBlurriness().set(1);
      super.renderBlurredBackground(partialTick);
      Minecraft.getInstance().options.menuBackgroundBlurriness().set(original);
   }

   private void drawLineFor(GuiGraphics graphics, TriggerButton button) {
      graphics.hLine(button.getX() - button.offset.x(), button.getX(), button.getY(), -4473925);
      graphics.vLine(button.getX() - button.offset.x(), button.getY() - button.offset.y(), button.getY(), -4473925);
   }

   public void onClose() {
      super.onClose();
      this.lastScreen.init(Minecraft.getInstance(), this.width, this.height);
      Minecraft.getInstance().setScreen(this.lastScreen);
   }

   public void saveAndClose() {
      Vec2i offset = this.buttons.getFirst().offset.equals(Config.options().layoutOffset) ? null : this.buttons.getFirst().offset;
      this.buttons.forEach(b -> b.savePolicy(offset, this.offsetFromSlot, this.autoOp, this.autoOpOther, this.ignoredSlots));
      Config.save();
      this.onClose();
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      int distance = Screen.hasShiftDown() ? 6 : 1;

      Vec2i movement = switch (keyCode) {
         case 262 -> new Vec2i(distance, 0);
         case 263 -> new Vec2i(-distance, 0);
         case 264 -> new Vec2i(0, distance);
         case 265 -> new Vec2i(0, -distance);
         default -> null;
      };
      if (movement != null) {
         Vec2i before = this.rep.offset;
         this.rep.offset = this.rep.offset.add(movement);
         this.repositionButtons(this.rep, before);
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
         this.dragging = false;
         return true;
      }

      for (TriggerButton cb : this.buttons) {
         if (cb.isMouseOver(mouseX, mouseY)) {
            cb.mouseClicked(mouseX, mouseY, mouseButton);
            this.rep = cb;
            this.dragging = true;
            return true;
         }
      }

      for (Slot slot : this.underlay.getMenu().slots) {
         if (((AbstractContainerScreenAccessor)this.underlay).clientsort$isHovering(slot, mouseX, mouseY)) {
            Object object = ClientSort.getObj(slot, this.underlay.getMenu());
            if (object != null && object.getClass().getName().equals(this.lowestPolicyClassName)) {
               int slotId = ((ISlot)slot).clientsort$getIndexInContainer();
               if (this.ignoredSlots.contains(slotId)) {
                  this.ignoredSlots.remove(slotId);
               } else {
                  this.ignoredSlots.add(slotId);
               }
            }
         }
      }

      return false;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.dragging) {
         Vec2i before = this.rep.offset;
         if (this.rep.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            this.repositionButtons(this.rep, before);
            return true;
         }
      }

      return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
      this.dragging = false;
      return super.mouseReleased(mouseX, mouseY, mouseButton);
   }

   private void repositionButtons(TriggerButton button, Vec2i before) {
      if (!button.offset.equals(before)) {
         Vec2i diff = button.offset.subtract(before);

         for (TriggerButton cb : this.buttons) {
            if (cb != button) {
               cb.offset = cb.offset.add(diff);
            }
         }
      }
   }
}
