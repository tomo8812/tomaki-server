package org.millenaire.world.sitescore;

import com.mojang.logging.LogUtils;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public final class SiteMarkers {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String TAG = "millenaire_site_marker";
   public static final int HEIGHT_ABOVE_GROUND = markerHeight();
   private static final float CUBE = 2.0F;
   private static final float VIEW_RANGE = 8.0F;

   private static int markerHeight() {
      try {
         return Math.max(2, Integer.parseInt(System.getProperty("millenaire.siteMarkerHeight", "40").trim()));
      } catch (NumberFormatException e) {
         return 40;
      }
   }

   private SiteMarkers() {
   }

   public static void mark(ServerLevel level, BlockPos site, String category, @Nullable String dryRun, @Nullable SiteMeasures m) {
      SiteMarkers.Look look = look(category);
      if (look != null) {
         String dry = dryRun == null ? "dry run not run" : ("PASS".equals(dryRun) ? "dry run PASS" : "dry run FAIL");

         for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(chatLine(site, category, dry, look, player));
         }

         spawnCube(level, site, look);
         spawnLabel(level, site, category, dry, m, look);
      }
   }

   @Nullable
   private static SiteMarkers.Look look(String category) {
      return switch (category) {
         case "new_only" -> new SiteMarkers.Look(Blocks.LIME_STAINED_GLASS, 5635925, ChatFormatting.GREEN);
         case "both_pass" -> new SiteMarkers.Look(Blocks.WHITE_STAINED_GLASS, 16777215, ChatFormatting.WHITE);
         case "old_only" -> new SiteMarkers.Look(Blocks.RED_STAINED_GLASS, 16733525, ChatFormatting.RED);
         case "near_miss" -> new SiteMarkers.Look(Blocks.YELLOW_STAINED_GLASS, 16777045, ChatFormatting.YELLOW);
         case "neither" -> new SiteMarkers.Look(Blocks.LIGHT_GRAY_STAINED_GLASS, 11184810, ChatFormatting.GRAY);
         default -> null;
      };
   }

   private static Component chatLine(BlockPos site, String category, String dry, SiteMarkers.Look look, ServerPlayer player) {
      double dx = site.getX() + 0.5 - player.getX();
      double dz = site.getZ() + 0.5 - player.getZ();
      int distance = (int)Math.round(Math.hypot(dx, dz));
      String tp = "/tp @s " + site.getX() + " " + (site.getY() + 1) + " " + site.getZ();
      MutableComponent line = Component.literal(
            "[Site] " + category + " · " + dry + " · " + distance + " blocks " + compass(dx, dz) + " (x " + site.getX() + ", z " + site.getZ() + ") "
         )
         .withStyle(look.chat());
      MutableComponent link = Component.literal("[teleport]")
         .withStyle(
            Style.EMPTY
               .withColor(ChatFormatting.AQUA)
               .withUnderlined(true)
               .withClickEvent(new ClickEvent(Action.RUN_COMMAND, tp))
               .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal(tp)))
         );
      return line.append(link)
         .append(Component.literal(" "))
         .append(labelButton(site, category, "good", ChatFormatting.GREEN))
         .append(Component.literal(" "))
         .append(labelButton(site, category, "ok", ChatFormatting.GOLD))
         .append(Component.literal(" "))
         .append(labelButton(site, category, "bad", ChatFormatting.RED));
   }

   static MutableComponent labelButton(BlockPos site, String category, String label, ChatFormatting colour) {
      String command = labelCommand(site, category, label);
      return Component.literal("[" + label + "]")
         .withStyle(
            Style.EMPTY
               .withColor(colour)
               .withBold(true)
               .withClickEvent(new ClickEvent(Action.RUN_COMMAND, command))
               .withHoverEvent(
                  new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal(labelMeaning(label) + "\n(recorded in labels.csv)"))
               )
         );
   }

   static String labelMeaning(String label) {
      return switch (label) {
         case "good" -> "Good site, even with today's building placement";
         case "ok" -> "Good terrain, but only once placement handles height differences";
         case "bad" -> "Bad terrain, whatever placement does";
         default -> label;
      };
   }

   static String labelCommand(BlockPos site, String category, String label) {
      return "/millenaire dev sitelabel " + site.getX() + " " + site.getZ() + " " + label + " " + category;
   }

   static String compass(double dx, double dz) {
      if (dx == 0.0 && dz == 0.0) {
         return "here";
      }

      double degrees = Math.toDegrees(Math.atan2(dx, -dz));
      int sector = (int)Math.round((degrees % 360.0 + 360.0) % 360.0 / 45.0) % 8;
      return new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"}[sector];
   }

   private static void spawnCube(ServerLevel level, BlockPos site, SiteMarkers.Look look) {
      CompoundTag tag = base("minecraft:block_display");
      tag.put("block_state", (Tag)BlockState.CODEC.encodeStart(NbtOps.INSTANCE, look.block().defaultBlockState()).getOrThrow());
      tag.put("transformation", transformation(-1.0F, 0.0F, -1.0F, 2.0F));
      tag.putBoolean("Glowing", true);
      tag.putInt("glow_color_override", look.rgb());
      tag.putFloat("width", 2.0F);
      tag.putFloat("height", 2.0F);
      spawn(level, tag, site.getX() + 0.5, site.getY() + HEIGHT_ABOVE_GROUND, site.getZ() + 0.5);
   }

   private static void spawnLabel(ServerLevel level, BlockPos site, String category, String dry, @Nullable SiteMeasures m, SiteMarkers.Look look) {
      String detail;
      if (m == null) {
         detail = "";
      } else if (m.lonePad() != null && m.lonePad().distance() >= 0) {
         SiteMeasures.LonePad p = m.lonePad();
         detail = String.format(
            Locale.ROOT,
            "\nlone pad %d×%d blocks · usable %.2f · range %d\npad %d blocks from centre · water %.2f",
            p.padW() * 4,
            p.padD() * 4,
            p.bestUsable(),
            p.range(),
            p.distance(),
            m.waterFraction()
         );
      } else {
         detail = String.format(
            Locale.ROOT,
            "\ngentle2 %.2f · weighted %.2f · connected %.2f\nrange %d · water %.2f",
            m.gentle2Fraction(),
            m.centreWeightedGentle2(),
            m.connectedGentle2(),
            m.heightRange(),
            m.waterFraction()
         );
      }

      if (m != null && m.features() != null) {
         TerrainFeatures t = m.features();
         detail = detail
            + String.format(
               Locale.ROOT,
               "\nblockers %.2f%s%s · big-scale range %d\nfix: cover %d · cut %d blocks",
               t.blockerFraction(),
               t.bigWaterAtEdge() ? " · sea/river" : "",
               t.ravine() ? " · ravine" : "",
               t.largeRange(),
               t.coverBlocks(),
               t.hillCutBlocks()
            );
      }

      Component text = Component.literal(category + " · " + dry + detail).withStyle(look.chat());
      CompoundTag tag = base("minecraft:text_display");
      tag.putString("text", Serializer.toJson(text, level.registryAccess()));
      tag.putString("billboard", "center");
      tag.putBoolean("see_through", true);
      tag.putInt("background", Integer.MIN_VALUE);
      tag.putInt("line_width", 260);
      spawn(level, tag, site.getX() + 0.5, site.getY() + HEIGHT_ABOVE_GROUND + 2.0F + 1.5, site.getZ() + 0.5);
   }

   private static CompoundTag base(String id) {
      CompoundTag tag = new CompoundTag();
      tag.putString("id", id);
      ListTag tags = new ListTag();
      tags.add(StringTag.valueOf("millenaire_site_marker"));
      tag.put("Tags", tags);
      CompoundTag brightness = new CompoundTag();
      brightness.putInt("sky", 15);
      brightness.putInt("block", 15);
      tag.put("brightness", brightness);
      tag.putFloat("view_range", 8.0F);
      return tag;
   }

   private static ListTag floats(float... values) {
      ListTag list = new ListTag();

      for (float v : values) {
         list.add(FloatTag.valueOf(v));
      }

      return list;
   }

   private static CompoundTag transformation(float tx, float ty, float tz, float scale) {
      CompoundTag t = new CompoundTag();
      t.put("translation", floats(tx, ty, tz));
      t.put("left_rotation", floats(0.0F, 0.0F, 0.0F, 1.0F));
      t.put("scale", floats(scale, scale, scale));
      t.put("right_rotation", floats(0.0F, 0.0F, 0.0F, 1.0F));
      return t;
   }

   private static void spawn(ServerLevel level, CompoundTag tag, double x, double y, double z) {
      Entity entity = EntityType.loadEntityRecursive(tag, level, e -> {
         e.moveTo(x, y, z, 0.0F, 0.0F);
         return e;
      });
      if (entity == null || !level.addFreshEntity(entity)) {
         LOGGER.warn("[Millenaire site scores] could not place a site marker at {} {} {}", new Object[]{x, y, z});
      }
   }

   private record Look(Block block, int rgb, ChatFormatting chat) {
   }
}
