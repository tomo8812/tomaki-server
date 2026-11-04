package com.archiangel.quadhotbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.payload.ModdedNetworkQueryComponent;

public final class QuadHotbarConnectionGuard {
   private static final Map<String, PacketFlow> REQUIRED = Map.of(
      "server_support",
      PacketFlow.CLIENTBOUND,
      "page_sync",
      PacketFlow.CLIENTBOUND,
      "selection_sync",
      PacketFlow.CLIENTBOUND,
      "player_policy",
      PacketFlow.CLIENTBOUND,
      "overview_return",
      PacketFlow.CLIENTBOUND,
      "container_overview_header",
      PacketFlow.CLIENTBOUND,
      "container_overview_frame",
      PacketFlow.CLIENTBOUND,
      "page_request",
      PacketFlow.SERVERBOUND,
      "overview_request",
      PacketFlow.SERVERBOUND,
      "container_overview_action",
      PacketFlow.SERVERBOUND
   );

   private QuadHotbarConnectionGuard() {
   }

   public static QuadHotbarConnectionGuard.Status status(Map<ConnectionProtocol, Set<ModdedNetworkQueryComponent>> channels) {
      boolean installed = false;
      Map<String, ModdedNetworkQueryComponent> play = new HashMap<>();

      for (Entry<ConnectionProtocol, Set<ModdedNetworkQueryComponent>> entry : channels.entrySet()) {
         for (ModdedNetworkQueryComponent channel : entry.getValue()) {
            if (channel.id().getNamespace().equals("quadhotbar")) {
               installed = true;
               if (!channel.version().equals("11")) {
                  try {
                     return Integer.parseInt(channel.version()) < Integer.parseInt("11")
                        ? QuadHotbarConnectionGuard.Status.OUTDATED
                        : QuadHotbarConnectionGuard.Status.INCOMPATIBLE;
                  } catch (NumberFormatException ignored) {
                     return QuadHotbarConnectionGuard.Status.INCOMPATIBLE;
                  }
               }

               if (entry.getKey() == ConnectionProtocol.PLAY && play.put(channel.id().getPath(), channel) != null) {
                  return QuadHotbarConnectionGuard.Status.INCOMPATIBLE;
               }
            }
         }
      }

      if (!installed) {
         return QuadHotbarConnectionGuard.Status.NOT_INSTALLED;
      }

      for (Entry<String, PacketFlow> required : REQUIRED.entrySet()) {
         ModdedNetworkQueryComponent channel = play.get(required.getKey());
         if (channel == null || !channel.flow().equals(Optional.of(required.getValue()))) {
            return QuadHotbarConnectionGuard.Status.INCOMPATIBLE;
         }
      }

      return QuadHotbarConnectionGuard.Status.COMPATIBLE;
   }

   public static Component reason(QuadHotbarConnectionGuard.Status status, String language) {
      if (!status.rejected()) {
         throw new IllegalArgumentException("Client is not incompatible");
      }

      boolean russian = "ru_ru".equalsIgnoreCase(language);
      boolean outdated = status == QuadHotbarConnectionGuard.Status.OUTDATED;
      String fallback = russian
         ? (outdated ? "Устаревшая версия QuadHotbar." : "Несовместимая версия QuadHotbar.") + "\nУстановите QuadHotbar %s"
         : (outdated ? "Outdated QuadHotbar version." : "Incompatible QuadHotbar version.") + "\nInstall QuadHotbar %s";
      String version = ((ModContainer)ModList.get().getModContainerById("quadhotbar").orElseThrow()).getModInfo().getVersion().toString();
      return Component.translatableWithFallback(
         outdated ? "quadhotbar.disconnect.outdated_version" : "quadhotbar.disconnect.incompatible_version", fallback, new Object[]{version}
      );
   }

   public enum Status {
      NOT_INSTALLED,
      COMPATIBLE,
      OUTDATED,
      INCOMPATIBLE;

      public boolean rejected() {
         return this == OUTDATED || this == INCOMPATIBLE;
      }

      // $VF: synthetic method
      private static QuadHotbarConnectionGuard.Status[] $values() {
         return new QuadHotbarConnectionGuard.Status[]{NOT_INSTALLED, COMPATIBLE, OUTDATED, INCOMPATIBLE};
      }
   }
}
