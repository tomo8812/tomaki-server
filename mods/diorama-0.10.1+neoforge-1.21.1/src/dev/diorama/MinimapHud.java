package dev.diorama;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.diorama.compat.Cobblemon;
import dev.diorama.compat.HudElement;
import dev.diorama.drawing.DrawingOverlay;
import dev.diorama.drawing.DrawingStore;
import dev.diorama.render.Circle;
import dev.diorama.render.MapLevel;
import dev.diorama.render.MapViewport;
import dev.diorama.render.Markers;
import dev.diorama.render.MinimapFrames;
import dev.diorama.render.Radar;
import dev.diorama.render.TextSize;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.waypoint.Waypoint;
import dev.diorama.waypoint.WaypointOverlay;
import dev.diorama.waypoint.WaypointStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class MinimapHud implements HudElement {
   public static final int MARGIN = 6;
   private static final int HOTBAR_HALF_WIDTH = 91;
   private static final int HOTBAR_HEIGHT = 50;
   private static final int EFFECT_WIDTH = 25;
   private static final int EFFECT_ROW = 26;
   private static final int[] NO_EFFECTS = new int[]{0, 0};
   private static final int BOSS_BAR_HALF_WIDTH = 91;
   private static final int BOSS_BAR_HEIGHT = 5;
   private static final int SMALLEST = 64;
   private static final long LAYER_HOLD_MS = 3000L;
   private static final long LAYER_FADE_MS = 300L;
   private static final int INFO_COLOR = -1;
   private static final int BED_TIME_COLOR = -6503169;
   private static boolean effectIconsDrawn;
   private static int bossBarsBottom;
   private static int @Nullable [] shown;
   private static int heldSize;
   private final MapViewport viewport = new MapViewport();
   private final MapViewport fading = new MapViewport();
   private int fadingLayer;
   private long fadeStart = -1L;
   private final DrawingOverlay drawings = new DrawingOverlay();
   private final WorldBorderOverlay worldBorder = new WorldBorderOverlay();
   private int shownLayer;
   private boolean showing;
   private long holdStart = -1L;

   @Override
   public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
      float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
      Minecraft mc = Minecraft.getInstance();
      Settings settings = Settings.get();
      Screen screen = mc.screen;
      boolean effectIcons = effectIconsDrawn;
      effectIconsDrawn = false;
      int bossBars = bossBarsBottom;
      bossBarsBottom = 0;
      shown = null;
      boolean placing = screen instanceof MinimapPositionScreen;
      if ((
            placing
               || settings.minimap && !mc.options.hideGui && (screen == null || screen instanceof ChatScreen) && !Cobblemon.inBattle() && !debugScreenOpen(mc)
         )
         && this.canDraw(mc)) {
         int size = DioramaClient.minimapEnlarged() ? Math.round(settings.minimapSize * settings.minimapEnlarged / 100.0F) : settings.minimapSize;
         int[] bounds = this.draw(graphics, partialTick, MinimapHud.Anchor.of(settings), effectIcons ? effectCounts(mc.player) : NO_EFFECTS, bossBars, size);
         shown = new int[]{bounds[4], bounds[5], bounds[6], bounds[7]};
      } else {
         this.holdStart = -1L;
      }
   }

   public static int @Nullable [] shown() {
      return shown;
   }

   static void holdSize(int size) {
      heldSize = size;
   }

   public void preview(GuiGraphics graphics, float partialTick) {
      Minecraft mc = Minecraft.getInstance();
      if (this.canDraw(mc)) {
         Settings settings = Settings.get();
         boolean top = settings.minimapCorner == Settings.Corner.TOP_RIGHT || settings.minimapCorner == Settings.Corner.TOP_LEFT;
         int[] bounds = this.draw(graphics, partialTick, new MinimapHud.Anchor(true, top, false, 6, 6), NO_EFFECTS, 0, settings.minimapSize);
         if (!settings.minimap) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(0.0F, 0.0F, 1.0F);
            if (settings.minimapShape == Settings.Shape.ROUND) {
               MapCanvas.of(graphics)
                  .solid(new Circle(bounds[4] + bounds[6] / 2.0F, bounds[5] + bounds[6] / 2.0F, bounds[6] / 2.0F + 2.0F).disc(), -1341123560);
               if (bounds[3] > bounds[5] + bounds[6] + 2) {
                  graphics.fill(bounds[0], bounds[5] + bounds[6] + 2, bounds[2], bounds[3], -1341123560);
               }
            } else {
               graphics.fill(bounds[0], bounds[1], bounds[2], bounds[3], -1341123560);
            }

            pose.popPose();
         }
      }
   }

   public void close() {
      this.viewport.close();
      this.fading.close();
   }

   private static boolean debugScreenOpen(Minecraft mc) {
      return mc.getDebugOverlay().showDebugScreen();
   }

   private boolean canDraw(Minecraft mc) {
      return MapSession.get() != null && mc.player != null && mc.level != null && ServerRules.minimapAllowed();
   }

   private int[] draw(GuiGraphics graphics, float partialTick, MinimapHud.Anchor anchor, int[] effects, int bossBars, int wanted) {
      Minecraft mc = Minecraft.getInstance();
      MapSession session = MapSession.get();
      Entity viewer = MapSession.viewer();
      Settings settings = Settings.get();
      Vec3 pos = viewer.getPosition(partialTick);
      Font font = mc.font;
      List<MinimapHud.InfoLine> info = this.infoLines(settings, viewer, mc);
      float text = TextSize.scale(settings.minimapTextSize);
      int infoHeight = (int)Math.ceil(info.size() * 10 * text);
      int[] placed = place(anchor, heldSize > 0 ? heldSize : wanted, heldSize <= 0, graphics.guiWidth(), graphics.guiHeight(), infoHeight, effects, bossBars);
      int x = placed[0];
      int y = placed[1];
      int size = placed[2];
      int wantedLayer = settings.autoCaves && ServerRules.caveMapsAllowed() ? session.autoLayer(viewer) : Integer.MIN_VALUE;
      int layer = this.layerToShow(
         session.map(),
         new MapViewport.View(x, y, size, size, Projection.u(pos.x, pos.z), Projection.v(pos.x, pos.y, pos.z), zoom(session.map(), wantedLayer), wantedLayer)
      );
      boolean round = settings.minimapShape == Settings.Shape.ROUND;
      float opacity = settings.opacity();
      float fadeIn = this.fadeStart < 0L ? 1.0F : (float)(System.currentTimeMillis() - this.fadeStart) / 300.0F;
      if (fadeIn < 1.0F) {
         this.fading.opacity(opacity * (1.0F - fadeIn) / (1.0F - opacity * fadeIn));
         this.fading
            .draw(
               graphics,
               session.map(),
               new MapViewport.View(
                  x,
                  y,
                  size,
                  size,
                  Projection.u(pos.x, pos.z),
                  Projection.v(pos.x, pos.y, pos.z),
                  zoom(session.map(), this.fadingLayer),
                  this.fadingLayer,
                  round,
                  true
               ),
               0,
               false,
               null
            );
      } else {
         this.fadeStart = -1L;
      }

      float shownOpacity = opacity * Math.min(1.0F, fadeIn);
      this.viewport.opacity(shownOpacity);
      MapViewport.View view = new MapViewport.View(
         x, y, size, size, Projection.u(pos.x, pos.z), Projection.v(pos.x, pos.y, pos.z), zoom(session.map(), layer), layer, round, true
      );
      this.viewport
         .draw(
            graphics,
            session.map(),
            view,
            0,
            Settings.get().claims == Settings.ClaimsShown.ALWAYS,
            () -> session.lights().draw(graphics, view, partialTick, shownOpacity, ServerRules.radarAllowed() && settings.radarOnMinimap)
         );
      session.crafts().draw(graphics, view, partialTick, opacity);
      if (settings.worldBorder == Settings.WorldBorderShown.ALWAYS) {
         this.worldBorder.draw(graphics, font, view, mc.level, viewer);
      }

      DrawingStore drawings = DrawingStore.get();
      if (drawings != null && settings.drawings == Settings.DrawingsShown.ALWAYS) {
         this.drawings.draw(graphics, font, view, drawings.in(mc.level.dimension().location().toString()), null, null, false);
      }

      MinimapFrames.draw(graphics, settings.minimapFrame, round, x, y, size);
      List<Radar.Blip> blips = ServerRules.radarAllowed() && settings.radarOnMinimap ? Radar.collect(view, partialTick, false) : List.of();
      Radar.draw(graphics, view, blips, false, false);
      WaypointStore store = WaypointStore.get();
      if (store != null && settings.waypointsOnMap) {
         List<Waypoint> near = store.shownIn(mc.level.dimension().location().toString()).stream().filter(wx -> WaypointStore.isNear(wx, viewer)).toList();
         WaypointOverlay.draw(graphics, view, WaypointOverlay.layout(view, near, false), false, null);
      }

      Radar.draw(graphics, view, blips, false, true);
      Markers.player(graphics, x + size / 2.0F, y + size / 2.0F, Markers.onMap(viewer.getViewYRot(partialTick)), 5.0F);
      if (settings.compass) {
         this.compass(graphics, font, x, y, size, text);
      }

      float textY = y + size + 4;
      float left = x - 2;
      float textRight = x + size + 2;

      for (MinimapHud.InfoLine line : info) {
         float w = font.width(line.text()) * text;
         float tx = anchor.centered() ? x + (float)Math.floor((size - w) / 2.0F) : (anchor.right() ? x + size - w : x);
         tx = TextSize.snap(Math.max(1.0F, Math.min(tx, graphics.guiWidth() - 1 - w)));
         graphics.pose().pushPose();
         graphics.pose().translate(tx, textY, 0.0F);
         graphics.pose().scale(text, text, 1.0F);
         graphics.drawString(font, line.text(), 0, 0, line.color(), true);
         graphics.pose().popPose();
         left = Math.min(left, tx - 1.0F);
         textRight = Math.max(textRight, tx + w + 1.0F);
         textY += 10.0F * text;
      }

      return new int[]{
         (int)Math.floor(left), y - 2, (int)Math.ceil(textRight), info.isEmpty() ? y + size + 2 : y + size + 4 + infoHeight, x, y, size, size + infoHeight
      };
   }

   private int layerToShow(MapLevel level, MapViewport.View wanted) {
      long now = System.currentTimeMillis();
      if (this.showing && wanted.layer() != this.shownLayer) {
         if (this.holdStart < 0L) {
            this.holdStart = now;
         }

         MapViewport.View shown = new MapViewport.View(
            wanted.x(), wanted.y(), wanted.width(), wanted.height(), wanted.centerU(), wanted.centerV(), zoom(level, this.shownLayer), this.shownLayer
         );
         boolean oldShows = MapViewport.showsAnything(level, shown);
         if (MapViewport.ready(level, wanted) || now - this.holdStart > 3000L || !oldShows) {
            if (oldShows && this.shownLayer != Integer.MIN_VALUE && wanted.layer() != Integer.MIN_VALUE) {
               this.fadingLayer = this.shownLayer;
               this.fadeStart = now;
            } else {
               this.fadeStart = -1L;
            }

            this.shownLayer = wanted.layer();
            this.holdStart = -1L;
         }

         return this.shownLayer;
      } else {
         if (!this.showing) {
            this.fadeStart = -1L;
         }

         this.showing = true;
         this.shownLayer = wanted.layer();
         this.holdStart = -1L;
         return this.shownLayer;
      }
   }

   private static double zoom(MapLevel level, int layer) {
      double zoom = Settings.get().zoom();
      return layer == Integer.MIN_VALUE && level.hasOverviews() ? zoom : Math.max(zoom, 0.5);
   }

   static int[] place(MinimapHud.Anchor anchor, int wanted, boolean avoidHotbar, int width, int height, int infoHeight, int[] effects, int bossBars) {
      int fitted = Math.min(wanted, width - 12 - 4);
      if (!anchor.top()) {
         fitted = Math.min(fitted, height - infoHeight - 12);
         int besideHotbar = width / 2 - 91 - 6 - 4 - anchor.dx();
         if (avoidHotbar && !anchor.centered() && anchor.dy() < 50 && (besideHotbar >= 64 || anchor.dx() == 6)) {
            fitted = Math.min(fitted, besideHotbar);
         }
      }

      int size = Math.max(64, fitted);
      int top = 6;
      int right = x(anchor, size, width) + size + 2;
      if (right > width - 25 * effects[1]) {
         top = 58;
      } else if (right > width - 25 * effects[0]) {
         top = 32;
      }

      if (bossBars > 0 && right > width / 2 - 91 && right - size - 4 < width / 2 + 91) {
         top = Math.max(top, bossBars + 6);
      }

      int room = anchor.top() ? height - top - infoHeight - 6 - 4 : height - anchor.dy() - infoHeight - top;
      size = Math.max(64, Math.min(size, room));
      int y = anchor.top() ? anchor.dy() : height - anchor.dy() - infoHeight - size;
      y = Math.max(top, Math.min(y, height - 6 - infoHeight - size));
      return new int[]{x(anchor, size, width), y, size};
   }

   private static int x(MinimapHud.Anchor anchor, int size, int width) {
      int x = anchor.centered() ? (width - size) / 2 : (anchor.right() ? width - anchor.dx() - size : anchor.dx());
      return Math.max(6, Math.min(x, width - 6 - size));
   }

   private void compass(GuiGraphics graphics, Font font, int x, int y, int size, float text) {
      String[] letters = new String[]{"N", "E", "S", "W"};
      int turns = Projection.facing().turns;

      for (int i = 0; i < 4; i++) {
         int w = font.width(letters[i]) + 3;
         int h = 9;
         float bx;
         float by;
         switch (Math.floorMod(i - turns, 4)) {
            case 0:
               bx = x + size / 2 - (w - 3) / 2 - 2 + w / 2.0F - w * text / 2.0F;
               by = y;
               break;
            case 1:
               bx = x + size - 5 - (w - 3) / 2 - 2 + w - w * text;
               by = y + size / 2 - 5 + h / 2.0F - h * text / 2.0F;
               break;
            case 2:
               bx = x + size / 2 - (w - 3) / 2 - 2 + w / 2.0F - w * text / 2.0F;
               by = y + size - 10 + h - h * text;
               break;
            default:
               bx = x + 5 - (w - 3) / 2 - 2;
               by = y + size / 2 - 5 + h / 2.0F - h * text / 2.0F;
         }

         graphics.pose().pushPose();
         graphics.pose().translate(TextSize.snap(bx), TextSize.snap(by), 0.0F);
         graphics.pose().scale(text, text, 1.0F);
         graphics.fill(0, 0, w, h, -1609559016);
         graphics.drawString(font, letters[i], 2, 1, i == 0 ? -38037 : -1, false);
         graphics.pose().popPose();
      }
   }

   private List<MinimapHud.InfoLine> infoLines(Settings settings, Entity viewer, Minecraft mc) {
      List<MinimapHud.InfoLine> lines = new ArrayList<>();
      BlockPos pos = viewer.blockPosition();
      if (settings.coordinates) {
         lines.add(new MinimapHud.InfoLine(pos.getX() + ", " + pos.getY() + ", " + pos.getZ(), -1));
      }

      if (settings.biome) {
         mc.level
            .getBiome(pos)
            .unwrapKey()
            .ifPresent(key -> lines.add(new MinimapHud.InfoLine(Component.translatable(key.location().toLanguageKey("biome")).getString(), -1)));
      }

      if (settings.gameTime != Settings.GameTime.OFF) {
         mc.level.updateSkyBrightness();
         lines.add(new MinimapHud.InfoLine(gameTime(settings.gameTime, mc.level.getDayTime()), mc.level.isNight() ? -6503169 : -1));
      }

      return lines;
   }

   public static String gameTime(Settings.GameTime format, long ticks) {
      long sinceMidnight = ticks + 6000L;
      long day = Math.floorDiv(sinceMidnight, 24000) + 1L;
      int inDay = Math.floorMod(sinceMidnight, 24000);
      int hour = inDay / 1000;
      int minute = inDay % 1000 * 60 / 1000;
      boolean twelveHour = format == Settings.GameTime.DAY_12H || format == Settings.GameTime.ONLY_12H;
      String time = String.format(Locale.ROOT, "%02d:%02d", twelveHour && hour % 12 == 0 ? 12 : (twelveHour ? hour % 12 : hour), minute);
      if (twelveHour) {
         time = Component.translatable(hour < 12 ? "diorama.minimap.time_am" : "diorama.minimap.time_pm", new Object[]{time}).getString();
      }

      boolean withDay = format == Settings.GameTime.DAY_24H || format == Settings.GameTime.DAY_12H;
      return withDay ? Component.translatable("diorama.minimap.day", new Object[]{day, time}).getString() : time;
   }

   public static void bossBarDrawn(int y) {
      bossBarsBottom = Math.max(bossBarsBottom, y + 5);
   }

   public static void effectIconsDrawn() {
      effectIconsDrawn = true;
   }

   private static int[] effectCounts(LocalPlayer player) {
      int beneficial = 0;
      int harmful = 0;

      for (MobEffectInstance effect : player.getActiveEffects()) {
         if (effect.showIcon()) {
            if (((MobEffect)effect.getEffect().value()).isBeneficial()) {
               beneficial++;
            } else {
               harmful++;
            }
         }
      }

      return new int[]{beneficial, harmful};
   }

   record Anchor(boolean right, boolean top, boolean centered, int dx, int dy) {
      static MinimapHud.Anchor of(Settings settings) {
         Settings.Corner corner = settings.minimapCorner;
         return new MinimapHud.Anchor(
            corner == Settings.Corner.TOP_RIGHT || corner == Settings.Corner.BOTTOM_RIGHT,
            corner == Settings.Corner.TOP_RIGHT || corner == Settings.Corner.TOP_LEFT,
            settings.minimapCentered,
            settings.minimapOffsetX,
            settings.minimapOffsetY
         );
      }
   }

   private record InfoLine(String text, int color) {
   }
}
