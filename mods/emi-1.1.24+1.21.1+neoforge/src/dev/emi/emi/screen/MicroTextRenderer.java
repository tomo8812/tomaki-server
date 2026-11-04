package dev.emi.emi.screen;

import dev.emi.emi.EmiPort;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.FluidUnit;
import dev.emi.emi.runtime.EmiDrawContext;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class MicroTextRenderer {
   private static final ResourceLocation TEXTURE = EmiPort.id("emi", "textures/gui/microfont.png");
   private static final Char2ObjectMap<MicroTextRenderer.MicroChar> MICRO_CHARS = new Char2ObjectOpenHashMap();
   private static final List<MicroTextRenderer.UnitScale> QUANTITY_SCALES = List.of(
      new MicroTextRenderer.UnitScale("", 0),
      new MicroTextRenderer.UnitScale("k", 3),
      new MicroTextRenderer.UnitScale("M", 6),
      new MicroTextRenderer.UnitScale("B", 9),
      new MicroTextRenderer.UnitScale("T", 12),
      new MicroTextRenderer.UnitScale("Q", 15)
   );
   private static final List<MicroTextRenderer.UnitScale> BUCKET_VOLUME_SCALES = List.of(
      new MicroTextRenderer.UnitScale("mB", 0),
      new MicroTextRenderer.UnitScale("B", 3),
      new MicroTextRenderer.UnitScale("kB", 6),
      new MicroTextRenderer.UnitScale("MB", 9),
      new MicroTextRenderer.UnitScale("GB", 12),
      new MicroTextRenderer.UnitScale("TB", 15),
      new MicroTextRenderer.UnitScale("PB", 18)
   );
   private static final List<MicroTextRenderer.UnitScale> LITER_VOLUME_SCALES = List.of(
      new MicroTextRenderer.UnitScale("mL", 0),
      new MicroTextRenderer.UnitScale("L", 3),
      new MicroTextRenderer.UnitScale("kL", 6),
      new MicroTextRenderer.UnitScale("ML", 9),
      new MicroTextRenderer.UnitScale("GL", 12),
      new MicroTextRenderer.UnitScale("TL", 15),
      new MicroTextRenderer.UnitScale("PL", 18)
   );
   private static final List<MicroTextRenderer.UnitScale> DROPLET_VOLUME_SCALES = List.of(
      new MicroTextRenderer.UnitScale("d", 0),
      new MicroTextRenderer.UnitScale("kd", 3),
      new MicroTextRenderer.UnitScale("Md", 6),
      new MicroTextRenderer.UnitScale("Gd", 9),
      new MicroTextRenderer.UnitScale("Td", 12),
      new MicroTextRenderer.UnitScale("Pd", 15)
   );

   private static void addMicroChar(MicroTextRenderer.MicroChar c) {
      MICRO_CHARS.put(c.c, c);
   }

   public static void render(EmiDrawContext context, long amount, boolean volume, int constraint, int right, int bottom) {
      render(context, amount, volume, constraint, right, bottom, -1);
   }

   public static void render(EmiDrawContext context, long amount, boolean volume, int constraint, int right, int bottom, int color) {
      List<MicroTextRenderer.UnitScale> scales;
      if (volume) {
         if (EmiConfig.fluidUnit != FluidUnit.DROPLETS) {
            amount /= FluidUnit.literDivisor();
         }
         scales = switch (EmiConfig.fluidUnit) {
            case MILLIBUCKETS -> BUCKET_VOLUME_SCALES;
            case LITERS -> LITER_VOLUME_SCALES;
            case DROPLETS -> DROPLET_VOLUME_SCALES;
         };
      } else {
         scales = QUANTITY_SCALES;
      }

      String string = getConstrainedAmount(amount, constraint, scales);
      int width = measure(string);
      int height = 7;
      int x = right - width;
      int y = bottom - height;
      float a = ((color & 0xFF000000) >> 24 & 0xFF) / 255.0F;
      float r = ((color & 0xFF0000) >> 16 & 0xFF) / 255.0F;
      float g = ((color & 0xFF00) >> 8 & 0xFF) / 255.0F;
      float b = ((color & 0xFF) >> 0 & 0xFF) / 255.0F;
      context.push();
      context.matrices().translate(0.0F, 0.0F, 300.0F);
      context.disableBlend();

      for (int i = 0; i < string.length(); i++) {
         MicroTextRenderer.MicroChar c = (MicroTextRenderer.MicroChar)MICRO_CHARS.get(string.charAt(i));
         if (c == null) {
            x++;
         } else {
            context.setColor(r, g, b, a);
            context.drawTexture(TEXTURE, x, y, c.u, c.v, c.width, 7);
            context.resetColor();
            context.drawTexture(TEXTURE, x, y, c.u, c.v + 7, c.width, 7);
            x += c.width - 1;
         }
      }

      context.pop();
   }

   private static String getConstrainedAmount(long amount, int constraint, List<MicroTextRenderer.UnitScale> scales) {
      String astring = amount + "";

      for (MicroTextRenderer.UnitScale scale : scales) {
         if (astring.length() <= scale.shift + 3) {
            for (int i = 2; i >= 0; i--) {
               String construct = construct(astring, scale.shift, i, scale.unit);
               if (measure(construct) <= constraint) {
                  return construct;
               }
            }

            if (astring.length() <= scale.shift + 2) {
               return "+";
            }
         }
      }

      return "+";
   }

   private static String construct(String amount, int shift, int afterDecimal, String unit) {
      while (amount.length() < shift) {
         amount = "0" + amount;
      }

      int start = amount.length() - shift;
      String before = amount.substring(0, start);
      if (afterDecimal <= 0) {
         return before + unit;
      }

      String after = amount.substring(start, Math.min(start + afterDecimal, amount.length()));

      while (after.length() > 0 && after.charAt(after.length() - 1) == '0') {
         after = after.substring(0, after.length() - 1);
      }

      return after.isEmpty() ? before + unit : before + "." + after + unit;
   }

   private static int measure(String string) {
      int total = 0;
      int refund = 0;

      for (int i = 0; i < string.length(); i++) {
         MicroTextRenderer.MicroChar c = (MicroTextRenderer.MicroChar)MICRO_CHARS.get(string.charAt(i));
         int amount = 1;
         if (c != null) {
            amount = c.width;
         }

         total += amount - refund;
         refund = 1;
      }

      return total;
   }

   static {
      for (int i = 0; i <= 9; i++) {
         addMicroChar(new MicroTextRenderer.MicroChar((char)(48 + i), 5, i * 5, 0));
      }

      addMicroChar(new MicroTextRenderer.MicroChar('.', 3, 0, 14));
      addMicroChar(new MicroTextRenderer.MicroChar('k', 5, 3, 14));
      addMicroChar(new MicroTextRenderer.MicroChar('M', 5, 8, 14));
      addMicroChar(new MicroTextRenderer.MicroChar('B', 5, 13, 14));
      addMicroChar(new MicroTextRenderer.MicroChar('T', 5, 18, 14));
      addMicroChar(new MicroTextRenderer.MicroChar('Q', 5, 23, 14));
      addMicroChar(new MicroTextRenderer.MicroChar('L', 5, 0, 28));
      addMicroChar(new MicroTextRenderer.MicroChar('d', 5, 5, 28));
      addMicroChar(new MicroTextRenderer.MicroChar('m', 5, 10, 28));
      addMicroChar(new MicroTextRenderer.MicroChar('G', 5, 15, 28));
      addMicroChar(new MicroTextRenderer.MicroChar('P', 5, 20, 28));
   }

   private record MicroChar(char c, int width, int u, int v) {
   }

   private record UnitScale(String unit, int shift) {
   }
}
