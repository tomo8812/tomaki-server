package dev.diorama.render;

public final class TextSize {
   private static final int ALWAYS_UP_TO = 4;

   private TextSize() {
   }

   public static float scale(int percent) {
      int gui = Radar.pixelsPerGuiPixel();
      int pixels = Math.max(first(gui), Math.min(last(gui), Math.round(percent * gui / 100.0F)));
      return (float)pixels / gui;
   }

   public static int[] sizes() {
      int gui = Radar.pixelsPerGuiPixel();
      int first = first(gui);
      int[] sizes = new int[last(gui) - first + 1];

      for (int i = 0; i < sizes.length; i++) {
         sizes[i] = Math.round((first + i) * 100.0F / gui);
      }

      return sizes;
   }

   public static float snap(float value) {
      int gui = Radar.pixelsPerGuiPixel();
      return (float)Math.round(value * gui) / gui;
   }

   private static int first(int gui) {
      return Math.max(1, (25 * gui + 99) / 100);
   }

   private static int last(int gui) {
      return Math.max(2 * gui, 4);
   }
}
