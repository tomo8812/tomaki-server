package dev.diorama.render;

import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import dev.diorama.waypoint.Waypoint;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jspecify.annotations.Nullable;

public final class Pins {
   public static final int LARGE = 15;
   public static final int SMALL = 13;
   private static final int CARET_STEPS = 32;
   public static final int SHADOW = 3;
   public static final int TEXT_GROWS_TO = 100;
   private static final Map<String, Pins.Sprite> SPRITES = new HashMap<>();
   private static final String ICON = "icon ";

   private Pins() {
   }

   public static float scale(int percent) {
      return Radar.texel(percent / 100.0F, 1);
   }

   public static float textScale(int percent) {
      int gui = Radar.pixelsPerGuiPixel();
      int pin = Math.round(scale(Math.min(percent, 100)) * gui);
      int usual = Math.round(scale(60) * gui);
      return (float)Math.max(1, (2 * gui * pin + usual) / (2 * usual)) / gui;
   }

   public static int[] sizes() {
      int gui = Radar.pixelsPerGuiPixel();
      int first = Math.max(1, Math.round(25 * gui / 100.0F));
      int last = 200 * gui / 100;
      int[] sizes = new int[last - first + 1];

      for (int i = 0; i < sizes.length; i++) {
         sizes[i] = Math.round((first + i) * 100.0F / gui);
      }

      return sizes;
   }

   public static void standing(GuiGraphics graphics, Waypoint waypoint, float x, float y, int size) {
      standing(graphics, waypoint, x, y, size, 1.0F);
   }

   public static void standing(GuiGraphics graphics, Waypoint waypoint, float x, float y, int size, float scale) {
      PinArt.Image icon = icon(waypoint);
      if (icon != null) {
         Pins.Sprite sprite = sprite("icon pin " + waypoint.icon + " " + rgb(waypoint), () -> PinArt.iconPin(icon, rgb(waypoint)));
         draw(graphics, sprite, waypoint, x, y, scale);
      } else {
         int head = PinArt.standingHead(size);
         boolean death = waypoint.kind == Waypoint.Kind.DEATH;
         int stretch = stretch(waypoint, head);
         Pins.Sprite sprite = sprite(
            "pin " + head + " " + stretch + " " + rgb(waypoint) + " " + death, () -> PinArt.standing(rgb(waypoint), death, head, stretch)
         );
         draw(graphics, sprite, waypoint, x, y, scale);
      }
   }

   public static void badge(GuiGraphics graphics, Waypoint waypoint, float x, float y, int head) {
      boolean death = waypoint.kind == Waypoint.Kind.DEATH;
      PinArt.Image icon = icon(waypoint);
      Pins.Sprite sprite = icon != null
         ? sprite("icon badge " + waypoint.icon + " " + rgb(waypoint), () -> PinArt.badge(rgb(waypoint), false, PinArt.iconHead(icon), 0, Double.NaN, 0, icon))
         : sprite(
            "badge " + head + " " + stretch(waypoint, head) + " " + rgb(waypoint) + " " + death,
            () -> PinArt.badge(rgb(waypoint), death, head, stretch(waypoint, head), Double.NaN, 0, null)
         );
      draw(graphics, sprite, waypoint, x, y, 1.0F);
   }

   public static void edge(GuiGraphics graphics, Waypoint waypoint, float x, float y, int head, double angle, float scale) {
      boolean death = waypoint.kind == Waypoint.Kind.DEATH;
      int step = Math.floorMod(Math.round((float)(angle / (Math.PI * 2) * 32.0)), 32);
      double snapped = step * Math.PI * 2.0 / 32.0;
      int caret = head >= 15 ? 6 : 5;
      PinArt.Image icon = icon(waypoint);
      Pins.Sprite sprite = icon != null
         ? sprite(
            "icon edge " + waypoint.icon + " " + rgb(waypoint) + " " + step,
            () -> PinArt.badge(rgb(waypoint), false, PinArt.iconHead(icon), 0, snapped, 6, icon)
         )
         : sprite(
            "edge " + head + " " + stretch(waypoint, head) + " " + rgb(waypoint) + " " + death + " " + step,
            () -> PinArt.badge(rgb(waypoint), death, head, stretch(waypoint, head), snapped, caret, null)
         );
      draw(graphics, sprite, waypoint, x, y, scale);
   }

   public static int badgeWidth(Waypoint waypoint, int head) {
      PinArt.Image icon = icon(waypoint);
      return icon != null ? PinArt.iconHead(icon) : head + stretch(waypoint, head);
   }

   public static float headAbove(Waypoint waypoint, int size) {
      PinArt.Image icon = icon(waypoint);
      return icon != null ? PinArt.iconHeadAbove(icon) : headAbove(size);
   }

   public static float reach(Waypoint waypoint, int size) {
      PinArt.Image icon = icon(waypoint);
      return (icon != null ? PinArt.iconHead(icon) : size) / 2.0F + 2.0F;
   }

   public static float headAbove(int size) {
      int head = PinArt.standingHead(size);
      return (head == 13 ? 5 : 6) + head / 2.0F;
   }

   public static boolean fitsStanding(Waypoint waypoint, int size, float scale, float x, float y, float x0, float y0, float x1, float y1) {
      float[] box = standingBox(waypoint, size, scale, x, y);
      return box[0] >= x0 && box[2] <= x1 && box[1] >= y0 && box[3] <= y1;
   }

   public static boolean fitsStanding(Waypoint waypoint, int size, float scale, float x, float y, Circle circle) {
      float[] box = standingBox(waypoint, size, scale, x, y);
      return circle.contains(box[0], box[1]) && circle.contains(box[2], box[1]) && circle.contains(box[0], box[3]) && circle.contains(box[2], box[3]);
   }

   private static float[] standingBox(Waypoint waypoint, int size, float scale, float x, float y) {
      PinArt.Image icon = icon(waypoint);
      int head = icon != null ? PinArt.iconHead(icon) : PinArt.standingHead(size);
      float above = head + (head == 13 ? 5 : 6);
      float side = (head + 4 + (icon != null ? 0 : stretch(waypoint, head))) / 2.0F;
      return new float[]{x - side * scale, y - above * scale, x + side * scale, y + 3.0F * scale};
   }

   public static float edgeReach(Waypoint waypoint, int head) {
      PinArt.Image icon = icon(waypoint);
      return (icon != null ? PinArt.iconHead(icon) : head + stretch(waypoint, head)) / 2.0F + 6.0F;
   }

   public static int stretch(Waypoint waypoint, int head) {
      if (waypoint.kind != Waypoint.Kind.DEATH && icon(waypoint) == null) {
         String text = waypoint.label();
         return text.isEmpty() ? 0 : PinArt.stretchFor(head, ink(Minecraft.getInstance().font, text));
      } else {
         return 0;
      }
   }

   public static boolean showsIcon(Waypoint waypoint) {
      return icon(waypoint) != null;
   }

   private static PinArt.@Nullable Image icon(Waypoint waypoint) {
      return waypoint.icon != null && !waypoint.icon.isEmpty() && waypoint.kind != Waypoint.Kind.DEATH ? WaypointIcons.image(waypoint.icon) : null;
   }

   static void forgetIcons() {
      SPRITES.entrySet().removeIf(entry -> {
         if (!entry.getKey().startsWith("icon ")) {
            return false;
         }

         entry.getValue().texture().close();
         return true;
      });
   }

   private static int rgb(Waypoint waypoint) {
      return waypoint.color & 16777215;
   }

   private static Pins.Sprite sprite(String key, Supplier<PinArt.Art> art) {
      Pins.Sprite sprite = SPRITES.get(key);
      if (sprite == null) {
         PinArt.Art made = art.get();
         int edge = Math.max(made.image().width, made.image().height);
         MapTexture texture = MapTexture.create("diorama " + key, edge, 1);
         texture.write(0, 0, 0, edge, made.image().abgr(edge));
         sprite = new Pins.Sprite(texture, edge, made);
         SPRITES.put(key, sprite);
      }

      return sprite;
   }

   private static void draw(GuiGraphics graphics, Pins.Sprite sprite, Waypoint waypoint, float x, float y, float scale) {
      PinArt.Art art = sprite.art();
      graphics.pose().pushPose();
      graphics.pose().translate(x, y, 0.0F);
      graphics.pose().scale(scale, scale, 1.0F);
      graphics.pose().translate(-art.anchorX(), -art.anchorY(), 0.0F);
      int w = art.image().width;
      int h = art.image().height;
      float edge = sprite.edge();
      int alpha = waypoint.color >>> 24 == 0 ? 255 : waypoint.color >>> 24;
      MapCanvas.of(graphics).rect(sprite.texture(), Sampling.CLAMP_CRISP, 0.0F, 0.0F, w, h, 0.0F, 0.0F, w / edge, h / edge, alpha << 24 | 16777215);
      if (waypoint.kind != Waypoint.Kind.DEATH && art.labelX1() > art.labelX0()) {
         label(graphics, waypoint, art.labelX0(), art.labelY0(), art.labelX1(), art.labelY1());
      }

      graphics.pose().popPose();
   }

   private static void label(GuiGraphics graphics, Waypoint waypoint, int x0, int y0, int x1, int y1) {
      Font font = Minecraft.getInstance().font;
      String text = waypoint.label();
      if (!text.isEmpty()) {
         int rgb = rgb(waypoint);
         boolean light = PinArt.light(rgb);
         int x = PinArt.centred(x0, x1, ink(font, text));
         int y = PinArt.centred(y0, y1, 7);
         graphics.drawString(font, text, x, y, light ? -15723496 : -1, !light);
      }
   }

   private static int ink(Font font, String text) {
      return font.width(text) - 1;
   }

   private record Sprite(MapTexture texture, int edge, PinArt.Art art) {
   }
}
