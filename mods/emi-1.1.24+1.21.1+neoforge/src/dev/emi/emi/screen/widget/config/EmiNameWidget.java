package dev.emi.emi.screen.widget.config;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.runtime.EmiDrawContext;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Style;

public class EmiNameWidget implements Renderable {
   private static Minecraft client = Minecraft.getInstance();
   private List<String[]> NAMES = Lists.newArrayList(
      new String[][]{
         "Emi Memy Imi".split(" "),
         "Exhaustively Many Ingredients".split(" "),
         "Explicitly Mandated Items".split(" "),
         "Endless Material Information".split(" "),
         "Evolving Manufacturing Index".split(" "),
         "Evidently, Many Ingredients".split(" "),
         "Earnestly Made Imitation".split(" "),
         "Even More Items".split(" "),
         "Eminence, My Inception".split(" "),
         "Explore My Inventory".split(" "),
         "Expounded Minutia Introspection".split(" "),
         "Exciting Minecraft Information".split(" "),
         "Expropriated Matter Insights".split(" "),
         "Efficiently Managed Inventory".split(" "),
         "Eerily Many Ingredients".split(" "),
         "Eventually Made Impressive".split(" "),
         "Exceptionally Motionless Interface".split(" "),
         "Emi's Magic Inventory".split(" "),
         "Egad, My Items!".split(" "),
         "Exploring Modified: Iridescent".split(" "),
         "E M I".split(" ")
      }
   );
   public int x;
   public int y;

   public EmiNameWidget(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public IntSet pruneSet(Random rand, int bound, int portion) {
      IntList ints = new IntArrayList();

      for (int i = 0; i < bound; i++) {
         ints.add(i);
      }

      for (int i = 0; i < portion && ints.size() != 0; i++) {
         ints.removeInt(rand.nextInt(ints.size()));
      }

      return new IntOpenHashSet(ints);
   }

   public String interpolate(Random source, String first, String second, float progress) {
      Font render = client.font;
      String both = first + second;
      int fw = render.width(first);
      int sw = render.width(second);
      int width = fw + Math.round((sw - fw) * progress);
      String ret = "";
      Random rand = new Random(source.nextLong() + width);

      label21:
      while (true) {
         for (int i = 0; i < 10; i++) {
            char c = both.charAt(rand.nextInt(both.length()));
            int w = render.width(ret + c);
            if (w <= width) {
               ret = ret + c;
               continue label21;
            }
         }

         return ret;
      }
   }

   public String transformString(Random rand, String string, float progress) {
      String ret = "";
      IntSet glitched = this.pruneSet(new Random(rand.nextLong()), string.length(), Math.round(string.length() * (1.0F - progress)));

      for (int i = 0; i < string.length(); i++) {
         if (glitched.contains(i)) {
            ret = ret + "§k";
         }

         ret = ret + string.charAt(i);
      }

      return ret;
   }

   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      long time = System.currentTimeMillis();
      long progress = time % 5000L;
      Random rand = new Random(time / 5000L);
      String[] parts = (String[])this.NAMES.get(rand.nextInt(this.NAMES.size())).clone();
      if (progress < 500L) {
         float p = (float)progress / 500.0F;
         String[] orig = (String[])this.NAMES.get(new Random((time - 5000L) / 5000L).nextInt(this.NAMES.size())).clone();
         parts[0] = this.transformString(rand, this.interpolate(rand, orig[0], parts[0], p), 2.0F);
         parts[1] = this.transformString(rand, this.interpolate(rand, orig[1], parts[1], p), 2.0F);
         parts[2] = this.transformString(rand, this.interpolate(rand, orig[2], parts[2], p), 2.0F);
      } else if (progress < 1000L) {
         float p = 1.0F - (float)(progress - 500L) / 500.0F;
         parts[0] = this.transformString(rand, parts[0], p);
         parts[1] = this.transformString(rand, parts[1], p);
         parts[2] = this.transformString(rand, parts[2], p);
      } else if (progress >= 4500L) {
         float p = (float)(progress - 4500L) / 500.0F;
         parts[0] = this.transformString(rand, parts[0], p);
         parts[1] = this.transformString(rand, parts[1], p);
         parts[2] = this.transformString(rand, parts[2], p);
      }

      context.drawCenteredTextWithShadow(
         EmiPort.literal(parts[0], Style.EMPTY.withColor(15432700))
            .append(EmiPort.literal("  "))
            .append(EmiPort.literal(parts[1], Style.EMPTY.withColor(8125602)))
            .append(EmiPort.literal("  "))
            .append(EmiPort.literal(parts[2], Style.EMPTY.withColor(8121340))),
         this.x,
         this.y,
         -1
      );
   }
}
