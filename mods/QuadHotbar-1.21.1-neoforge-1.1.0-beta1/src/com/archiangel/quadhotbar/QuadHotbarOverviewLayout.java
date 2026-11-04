package com.archiangel.quadhotbar;

public final class QuadHotbarOverviewLayout {
   public static final int MAX_WIDTH = 27;

   private QuadHotbarOverviewLayout() {
   }

   public static int normalizeWidth(int width) {
      return Math.max(9, Math.min(27, width) / 9 * 9);
   }

   public static int maxWidthForScale(double scale) {
      return scale <= 1.0 ? 27 : (scale <= 2.0 ? 18 : 9);
   }

   public static int rows(int pages, int columns) {
      return (pages + columns - 1) / columns;
   }

   public static int column(int page, int pages, int columns, boolean horizontal, boolean right) {
      int column = horizontal ? page % columns : page / rows(pages, columns);
      return right ? columns - 1 - column : column;
   }

   public static int row(int page, int pages, int columns, boolean horizontal) {
      return horizontal ? page / columns : page % rows(pages, columns);
   }
}
