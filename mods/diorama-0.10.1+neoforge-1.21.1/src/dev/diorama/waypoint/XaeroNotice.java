package dev.diorama.waypoint;

import dev.diorama.Diorama;
import dev.diorama.WorldKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import org.jspecify.annotations.Nullable;

public final class XaeroNotice {
   private static final int DELAY_TICKS = 100;
   private static final String SHOWN_FILE = "xaero-import-offered";
   public static final String COMMAND = "/diorama waypoint list";
   private static @Nullable String checked;
   private static @Nullable String pending;
   private static int ticks;

   private XaeroNotice() {
   }

   public static void tick(Minecraft mc) {
      String key = mc.level != null && mc.player != null ? WorldKey.current() : null;
      if (key == null) {
         if (mc.level == null) {
            checked = null;
         }

         pending = null;
      } else if (!key.equals(checked)) {
         if (!key.equals(pending)) {
            pending = key;
            ticks = 0;
         }

         if (++ticks >= 100) {
            checked = key;
            pending = null;
            Path shown = WorldKey.folder(key).resolve("xaero-import-offered");
            if (!Files.exists(shown)) {
               WaypointStore store = WaypointStore.get();
               if (store != null) {
                  Component message = message(XaeroImport.find(), store);
                  if (message != null) {
                     try {
                        Files.createDirectories(shown.getParent());
                        Files.writeString(shown, "");
                     } catch (IOException e) {
                        Diorama.LOGGER.warn("Couldn't note that the Xaero's import was offered for {}", key, e);
                     }

                     mc.gui.getChat().addMessage(message);
                  }
               }
            }
         }
      }
   }

   static @Nullable Component message(XaeroImport.@Nullable Found found, WaypointStore store) {
      if (found == null) {
         return null;
      }

      XaeroImport.World chosen = found.chosen();
      if (chosen != null) {
         int count = XaeroImport.missing(chosen, store).size();
         if (count == 0) {
            return null;
         }

         Component waypoints = count == 1
            ? Component.translatable("diorama.xaero.count_one")
            : Component.translatable("diorama.xaero.count", new Object[]{count});
         return Component.translatable("diorama.xaero.notice", new Object[]{waypoints, link()});
      } else {
         return found.worlds().stream().allMatch(world -> XaeroImport.missing(world, store).isEmpty())
            ? null
            : Component.translatable("diorama.xaero.notice_server", new Object[]{link()});
      }
   }

   private static Component link() {
      return Component.translatable("diorama.xaero.notice_link")
         .withStyle(
            style -> style.withColor(ChatFormatting.GREEN)
               .withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/diorama waypoint list"))
               .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("diorama.xaero.notice_hover")))
         );
   }
}
