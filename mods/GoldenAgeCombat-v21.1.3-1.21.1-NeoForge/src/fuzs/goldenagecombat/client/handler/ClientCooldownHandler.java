package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ClientCooldownHandler {
   private static final Component ATTACK_INDICATOR_TOOLTIP = Component.literal(
      String.format("Attack Indicator has been disabled by %s mod.", "Golden Age Combat")
   );
   @Nullable
   private static AttackIndicatorStatus attackIndicator = null;

   public static EventResult onBeforeRenderGuiLayer(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
      if (!((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).removeAttackCooldown) {
         return EventResult.PASS;
      }

      if (attackIndicator == null) {
         attackIndicator = (AttackIndicatorStatus)minecraft.options.attackIndicator().get();
         minecraft.options.attackIndicator().set(AttackIndicatorStatus.OFF);
      }

      return EventResult.PASS;
   }

   public static void onAfterRenderGuiLayer(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
      if (((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).removeAttackCooldown) {
         if (attackIndicator != null) {
            minecraft.options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
         }
      }
   }

   public static void onAfterInit(
      Minecraft minecraft,
      VideoSettingsScreen screen,
      int screenWidth,
      int screenHeight,
      List<AbstractWidget> widgets,
      UnaryOperator<AbstractWidget> addWidget,
      Consumer<AbstractWidget> removeWidget
   ) {
      if (GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable()
         && ((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).removeAttackCooldown) {
         screen.children()
            .stream()
            .filter(OptionsList.class::isInstance)
            .findAny()
            .map(OptionsList.class::cast)
            .map(optionsList -> optionsList.findOption(minecraft.options.attackIndicator()))
            .ifPresent(widget -> {
               widget.active = false;
               widget.setTooltip(Tooltip.create(ATTACK_INDICATOR_TOOLTIP));
            });
      }
   }
}
