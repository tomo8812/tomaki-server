package dev.diorama.render;

import com.google.common.collect.Iterables;
import dev.diorama.MapSession;
import dev.diorama.Projection;
import dev.diorama.RadarCategories;
import dev.diorama.Settings;
import dev.diorama.TrackedPlayers;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class Radar {
   private static final double CARET_HEIGHT = 3.0;

   private Radar() {
   }

   public static Radar.@Nullable Kind kind(Entity entity) {
      if (entity instanceof AbstractClientPlayer) {
         return Radar.Kind.PLAYER;
      } else if (entity instanceof ItemEntity) {
         return Radar.Kind.ITEM;
      } else if (entity instanceof NeutralMob) {
         return Radar.Kind.NEUTRAL;
      } else if (entity instanceof Enemy) {
         return Radar.Kind.HOSTILE;
      } else {
         return entity instanceof Mob ? Radar.Kind.PASSIVE : null;
      }
   }

   public static List<Radar.Blip> collect(MapViewport.View view, float partialTick, boolean fullscreen) {
      Minecraft mc = Minecraft.getInstance();
      ClientLevel level = mc.level;
      LocalPlayer self = mc.player;
      Entity viewer = MapSession.viewer();
      List<Radar.Blip> blips = new ArrayList<>();
      if (level != null && self != null && viewer != null) {
         double selfY = viewer.getPosition(partialTick).y;
         Settings settings = Settings.get();
         double range = settings.radarRange;
         float margin = 12.0F;

         for (Entity entity : Iterables.concat(level.entitiesForRendering(), TrackedPlayers.unloaded(level))) {
            if (entity != self && entity != viewer && entity.isAlive() && !entity.isInvisibleTo(self)) {
               Radar.Kind kind = kind(entity);
               if (kind != null) {
                  RadarCategories.Entry category = RadarCategories.of(entity, kind);
                  if (category.shown()) {
                     Vec3 pos = entity.getPosition(partialTick);
                     if (!(Math.abs(pos.y - selfY) > range)) {
                        float x = view.screenX(Projection.u(pos.x, pos.z));
                        float y = view.screenY(Projection.v(pos.x, pos.y, pos.z));
                        if (!(x < view.x() - margin)
                           && !(y < view.y() - margin)
                           && !(x > view.x() + view.width() + margin)
                           && !(y > view.y() + view.height() + margin)) {
                           int percent = category.iconScale() == 0 ? settings.radarIconScale : category.iconScale();
                           Settings.NameTags names = category.hasNames() ? category.names() : Settings.NameTags.NEVER;
                           boolean named = names == Settings.NameTags.ALWAYS || names == Settings.NameTags.FULLSCREEN && fullscreen;
                           blips.add(new Radar.Blip(entity, kind, x, y, pos.y - selfY, size(kind, percent / 100.0F, view.zoom(), fullscreen), named));
                        }
                     }
                  }
               }
            }
         }

         blips.sort(Comparator.comparingInt(b -> b.kind().ordinal()));
         return blips;
      } else {
         return blips;
      }
   }

   private static float size(Radar.Kind kind, float scale, double zoom, boolean fullscreen) {
      if (kind == Radar.Kind.ITEM) {
         return scale * (fullscreen ? (float)Math.max(2.0, Math.min(4.0, zoom)) : 2.0F);
      } else {
         return kind == Radar.Kind.PLAYER
            ? scale * (fullscreen ? (float)Math.max(8.0, Math.min(16.0, zoom * 3.0)) : 7.0F)
            : scale * (fullscreen ? (float)Math.max(4.0, Math.min(9.0, zoom * 2.2)) : 4.0F);
      }
   }

   public static void draw(GuiGraphics graphics, MapViewport.View view, List<Radar.Blip> blips, boolean fullscreen, boolean players) {
      graphics.enableScissor(view.x(), view.y(), view.x() + view.width(), view.y() + view.height());
      IconBatch icons = players ? IconBatch.direct(graphics) : IconBatch.batched(graphics);
      Settings settings = Settings.get();
      List<Radar.Blip> named = null;
      Circle circle = view.round() ? Circle.in(view) : null;
      IconBatch measure = circle == null ? null : IconBatch.measuring(graphics);
      if (!players) {
         itemDots(icons, blips, circle);
      }

      for (Radar.Blip blip : blips) {
         if (blip.kind() != Radar.Kind.ITEM && blip.kind() == Radar.Kind.PLAYER == players) {
            int alpha = alphaFor(blip.dy());
            if (measure != null) {
               measure.clear();
               icon(measure, blip, alpha, settings);
               if (!measure.inside(circle)) {
                  continue;
               }
            }

            icon(icons, blip, alpha, settings);
            if (blip.named() && (players || circle == null || nameInside(circle, blip))) {
               if (players) {
                  nameTag(graphics, blip, alpha);
               } else {
                  if (named == null) {
                     named = new ArrayList<>();
                  }

                  named.add(blip);
               }
            }

            icons.endIcon();
         }
      }

      icons.draw();
      if (named != null) {
         for (Radar.Blip blip : named) {
            nameTag(graphics, blip, alphaFor(blip.dy()));
         }
      }

      if (measure != null) {
         measure.draw();
      }

      graphics.disableScissor();
   }

   private static void itemDots(IconBatch icons, List<Radar.Blip> blips, @Nullable Circle circle) {
      for (int pass = 0; pass < 2; pass++) {
         for (Radar.Blip blip : blips) {
            if (blip.kind() != Radar.Kind.ITEM) {
               break;
            }

            int alpha = alphaFor(blip.dy());
            int d = Math.max(1, Math.round(blip.size()));
            float x0 = blip.x() - d / 2.0F;
            float y0 = blip.y() - d / 2.0F;
            if (circle == null
               || circle.contains(x0 - 1.0F, y0 - 1.0F)
                  && circle.contains(x0 + d + 1.0F, y0 - 1.0F)
                  && circle.contains(x0 - 1.0F, y0 + d + 1.0F)
                  && circle.contains(x0 + d + 1.0F, y0 + d + 1.0F)) {
               if (pass == 0) {
                  icons.fill(x0 - 1.0F, y0 - 1.0F, x0 + d + 1.0F, y0 + d + 1.0F, withAlpha(-16777216, alpha));
               } else {
                  icons.fill(x0, y0, x0 + d, y0 + d, withAlpha(blip.kind().color, alpha));
               }
            }
         }
      }

      icons.endIcon();
   }

   private static void icon(IconBatch icons, Radar.Blip blip, int alpha, Settings settings) {
      switch (settings.radarStyle) {
         case FRAMED:
            framed(icons, blip, alpha);
            break;
         case DOTS:
            dot(icons, blip, alpha);
            break;
         default:
            pixelHead(icons, blip, alpha, settings.radarTypeOutline);
      }

      if (settings.radarHeightArrows && Math.abs(blip.dy()) > 3.0) {
         boolean below = blip.dy() < 0.0;
         float s = blip.size();
         if (settings.radarStyle == Settings.RadarStyle.FRAMED) {
            float caret = Math.max(4.0F, s * 0.7F);
            float cx = blip.x() + s * 0.55F + caret * 0.3F;
            float cy = blip.y() - s * 0.45F;
            MapIcons.caret(icons, cx, cy, caret + 2.0F, withAlpha(-535817192, alpha), below);
            MapIcons.caret(icons, cx, cy, caret, withAlpha(-1, alpha), below);
         } else {
            pixelCaret(icons, blip.x() + s * 0.7F + 1.0F, blip.y() - s * 0.5F, below, alpha);
         }
      }

      if (blip.kind() != Radar.Kind.PLAYER && CobblemonPortraits.shiny(blip.entity())) {
         sparkle(icons, blip.x() - blip.size() * 0.8F, blip.y() - blip.size() * 0.8F, alpha);
      }
   }

   private static void sparkle(IconBatch icons, float cx, float cy, int alpha) {
      int black = withAlpha(-16777216, alpha);
      int yellow = withAlpha(-7862, alpha);
      icons.fill(cx - 1.0F, cy - 2.0F, cx + 2.0F, cy + 3.0F, black);
      icons.fill(cx - 2.0F, cy - 1.0F, cx + 3.0F, cy + 2.0F, black);
      icons.fill(cx, cy - 1.0F, cx + 1.0F, cy + 2.0F, yellow);
      icons.fill(cx - 1.0F, cy, cx + 2.0F, cy + 1.0F, yellow);
      icons.fill(cx, cy, cx + 1.0F, cy + 1.0F, withAlpha(-1, alpha));
   }

   private static void framed(IconBatch icons, Radar.Blip blip, int alpha) {
      GuiGraphics graphics = icons.graphics();
      float s = blip.size();
      if (blip.kind() == Radar.Kind.PLAYER && blip.entity() instanceof AbstractClientPlayer player) {
         MapIcons.dot(icons, blip.x(), blip.y(), s * 1.55F, withAlpha(-15723496, alpha));
         graphics.pose().pushPose();
         graphics.pose().translate(blip.x() - s / 2.0F, blip.y() - s / 2.0F, 0.0F);
         graphics.pose().scale(s / 8.0F, s / 8.0F, 1.0F);
         icons.playerFace(player, 0, 0, 8, withAlpha(-1, alpha));
         graphics.pose().popPose();
      } else {
         XaeroRadarIcons.Icon icon = XaeroRadarIcons.of(blip.entity());
         if (icon != XaeroRadarIcons.Basic.DOT && (icon != null || MobFaces.has(blip.entity()))) {
            int box = Math.round(s * 1.6F);
            float x0 = blip.x() - box / 2.0F;
            float y0 = blip.y() - box / 2.0F;
            icons.fill(x0 - 1.0F, y0 - 1.0F, x0 + box + 1.0F, y0 + box + 1.0F, withAlpha(blip.kind().color, alpha));
            icons.fill(x0, y0, x0 + box, y0 + box, withAlpha(-15723496, alpha));
            if (icon instanceof XaeroRadarIcons.Sprite sprite) {
               sprite.drawFitted(icons, x0 + box / 2.0F, y0 + box / 2.0F, box - 2, withAlpha(-1, alpha));
            } else if (icon instanceof XaeroRadarIcons.ItemIcon item) {
               icons.item(item.stack(), x0 + 1.0F, y0 + 1.0F, (box - 2) / 16.0F);
            } else if (!MobFaces.draw(icons, blip.entity(), x0 + box / 2.0F, y0 + box / 2.0F, box - 2, withAlpha(-1, alpha))) {
               MapIcons.dot(icons, blip.x(), blip.y(), s, withAlpha(blip.kind().color, alpha));
            }
         } else {
            MapIcons.dot(icons, blip.x(), blip.y(), s + 2.0F, withAlpha(-535817192, alpha));
            MapIcons.dot(icons, blip.x(), blip.y(), s, withAlpha(blip.kind().color, alpha));
         }
      }
   }

   private static void pixelHead(IconBatch icons, Radar.Blip blip, int alpha, boolean typeOutline) {
      GuiGraphics graphics = icons.graphics();
      float s = blip.size();
      int outline = typeOutline && blip.kind() != Radar.Kind.PLAYER ? blip.kind().color : -16777216;
      if (blip.kind() == Radar.Kind.PLAYER && blip.entity() instanceof AbstractClientPlayer player) {
         float texel = texel(s, 8);
         pixelFrame(icons, blip.x(), blip.y(), 8, 8, texel, withAlpha(outline, alpha), alpha);
         icons.playerFace(player, 0, 0, 8, withAlpha(-1, alpha));
         graphics.pose().popPose();
      } else {
         XaeroRadarIcons.Icon icon = XaeroRadarIcons.of(blip.entity());
         if (icon instanceof XaeroRadarIcons.Sprite sprite) {
            sprite.drawPixel(icons, blip.x(), blip.y(), s * 1.6F, withAlpha(-1, alpha), withAlpha(outline, alpha), alpha);
         } else if (icon instanceof XaeroRadarIcons.ItemIcon item) {
            float texel = texel(s * 1.6F, 8);
            pixelFrame(icons, blip.x(), blip.y(), 8, 8, texel, withAlpha(outline, alpha), alpha);
            graphics.pose().popPose();
            icons.item(item.stack(), blip.x() - 4.0F * texel, blip.y() - 4.0F * texel, texel / 2.0F);
         } else if (icon == XaeroRadarIcons.Basic.DOT
            || !MobFaces.drawPixel(icons, blip.entity(), blip.x(), blip.y(), s * 1.6F, withAlpha(-1, alpha), withAlpha(outline, alpha), alpha)) {
            dot(icons, blip, alpha);
         }
      }
   }

   static float texel(float target, int texels) {
      double gui = guiScale();
      long pixels = Math.max(1L, Math.round(target * gui / texels));
      return (float)(pixels / gui);
   }

   private static double guiScale() {
      return Math.max(1.0, Minecraft.getInstance().getWindow().getGuiScale());
   }

   static int pixelsPerGuiPixel() {
      return (int)guiScale();
   }

   static void pixelFrame(IconBatch icons, float x, float y, int w, int h, float texel, int outline, int alpha) {
      GuiGraphics graphics = icons.graphics();
      float x0 = x - w * texel / 2.0F;
      float y0 = y - h * texel / 2.0F;
      graphics.pose().pushPose();
      graphics.pose().translate(x0, y0, 0.0F);
      graphics.pose().scale(texel, texel, 1.0F);
      icons.fill(0.0F, 0.0F, w + 2, h + 2, withAlpha(1610612736, alpha));
      icons.fill(-1.0F, -1.0F, w + 1, h + 1, outline);
      icons.fill(0.0F, 0.0F, w, h, withAlpha(-15723496, alpha));
   }

   private static void dot(IconBatch icons, Radar.Blip blip, int alpha) {
      int d = Math.max(2, Math.round(blip.size() * (blip.kind() == Radar.Kind.PLAYER ? 0.6F : 0.8F)));
      float x0 = blip.x() - d / 2.0F;
      float y0 = blip.y() - d / 2.0F;
      icons.fill(x0, y0, x0 + d + 2.0F, y0 + d + 2.0F, withAlpha(1610612736, alpha));
      icons.fill(x0 - 1.0F, y0 - 1.0F, x0 + d + 1.0F, y0 + d + 1.0F, withAlpha(-16777216, alpha));
      icons.fill(x0, y0, x0 + d, y0 + d, withAlpha(blip.kind().color, alpha));
      if (d >= 3) {
         int light = withAlpha(1358954495, alpha);
         icons.fill(x0, y0, x0 + d - 1.0F, y0 + 1.0F, light);
         icons.fill(x0, y0 + 1.0F, x0 + 1.0F, y0 + d - 1.0F, light);
      }
   }

   private static void pixelCaret(IconBatch icons, float cx, float cy, boolean down, int alpha) {
      int black = withAlpha(-16777216, alpha);
      int white = withAlpha(-1, alpha);

      for (int pass = 0; pass < 2; pass++) {
         int grow = pass == 0 ? 1 : 0;

         for (int row = 0; row < 3; row++) {
            int half = row;
            float y = down ? cy + 1.0F - row : cy - 1.0F + row;
            icons.fill(cx - half - grow, y - grow, cx + half + 1.0F + grow, y + 1.0F + grow, pass == 0 ? black : white);
         }
      }
   }

   private static float nameOffset(Radar.Blip blip) {
      return blip.size() * (blip.kind() == Radar.Kind.PLAYER ? 0.75F : 0.8F) + 3.0F;
   }

   private static boolean nameInside(Circle circle, Radar.Blip blip) {
      float half = Minecraft.getInstance().font.width(blip.entity().getName().getString()) / 2.0F;
      float top = blip.y() + nameOffset(blip) - 1.0F;
      return circle.contains(blip.x() - half - 1.0F, top)
         && circle.contains(blip.x() + half, top)
         && circle.contains(blip.x() - half - 1.0F, top + 9.0F)
         && circle.contains(blip.x() + half, top + 9.0F);
   }

   private static void nameTag(GuiGraphics graphics, Radar.Blip blip, int alpha) {
      Font font = Minecraft.getInstance().font;
      String name = blip.entity().getName().getString();
      int w = font.width(name);
      graphics.pose().pushPose();
      graphics.pose().translate(blip.x() - w / 2.0F, blip.y() + nameOffset(blip), 0.0F);
      graphics.fill(-1, -1, w, 8, withAlpha(1073741824, alpha));
      graphics.drawString(font, name, 0, 0, withAlpha(-1, alpha), false);
      graphics.pose().popPose();
   }

   public static Radar.@Nullable Blip under(List<Radar.Blip> blips, double mouseX, double mouseY) {
      Radar.Blip best = null;
      double bestDistance = Double.MAX_VALUE;

      for (Radar.Blip blip : blips) {
         double dx = blip.x() - mouseX;
         double dy = blip.y() - mouseY;
         double d = Math.sqrt(dx * dx + dy * dy);
         double reach = (blip.kind() != Radar.Kind.PLAYER && blip.kind() != Radar.Kind.ITEM ? blip.size() * 0.8 : blip.size() / 2.0F) + 3.0;
         if (!(Math.max(Math.abs(dx), Math.abs(dy)) > reach)) {
            boolean better = best == null || blip.kind().ordinal() > best.kind().ordinal() || blip.kind() == best.kind() && d < bestDistance;
            if (better) {
               best = blip;
               bestDistance = d;
            }
         }
      }

      return best;
   }

   public static Component describe(Radar.Blip blip, Entity viewer) {
      int distance = Math.round(blip.entity().distanceTo(viewer));
      int dy = (int)Math.round(blip.dy());
      String height = dy > 0 ? " · " + dy + " above" : (dy < 0 ? " · " + -dy + " below" : "");
      return Component.empty().append(blip.entity().getDisplayName()).append(" · " + distance + " m" + height);
   }

   private static int alphaFor(double dy) {
      double fade = Math.max(0.0, Math.abs(dy) - 8.0) / 40.0;
      return (int)Math.round(255.0 * Math.max(0.45, 1.0 - fade));
   }

   static int withAlpha(int argb, int alpha) {
      int a = (argb >>> 24) * alpha / 255;
      return a << 24 | argb & 16777215;
   }

   public record Blip(Entity entity, Radar.Kind kind, float x, float y, double dy, float size, boolean named) {
   }

   public enum Kind {
      ITEM(-11141121),
      PASSIVE(-10299029),
      NEUTRAL(-868018),
      HOSTILE(-1094837),
      PLAYER(-1);

      final int color;

      Kind(int color) {
         this.color = color;
      }

      // $VF: synthetic method
      private static Radar.Kind[] $values() {
         return new Radar.Kind[]{ITEM, PASSIVE, NEUTRAL, HOSTILE, PLAYER};
      }
   }
}
