package com.archiangel.quadhotbar;

import java.util.Locale;

public final class QuadHotbarLayout {
   private QuadHotbarLayout() {
   }

   public static int[] coordinates(int row, int rows, int screenWidth, int screenHeight, int barWidth, int barHeight, QuadHotbarLayout.MainCorner corner) {
      int levels = (rows + 1) / 2;
      boolean centered = rows == 1 || rows % 2 != 0 && row == rows - 1;
      boolean right = row % 2 != 0 != corner.right;
      int x = centered ? screenWidth / 2 - barWidth / 2 : screenWidth / 2 - (right ? 0 : barWidth);
      int level = corner.top ? levels - 1 - row / 2 : row / 2;
      int y = screenHeight - barHeight * (level + 1);
      return new int[]{x, y};
   }

   public enum InventoryOrder {
      TOP_TO_BOTTOM,
      BOTTOM_TO_TOP;

      public int inventoryRow(int hotbarRow) {
         return this == BOTTOM_TO_TOP && hotbarRow != 0 ? 4 - hotbarRow : hotbarRow;
      }

      public int hotbarRow(int inventoryRow) {
         return this.inventoryRow(inventoryRow);
      }

      public String translationKey() {
         return "quadhotbar.layout.order." + this.name().toLowerCase(Locale.ROOT);
      }

      // $VF: synthetic method
      private static QuadHotbarLayout.InventoryOrder[] $values() {
         return new QuadHotbarLayout.InventoryOrder[]{TOP_TO_BOTTOM, BOTTOM_TO_TOP};
      }
   }

   public enum MainCorner {
      TOP_LEFT(true, false),
      TOP_RIGHT(true, true),
      BOTTOM_RIGHT(false, true),
      BOTTOM_LEFT(false, false);

      public final boolean top;
      public final boolean right;

      MainCorner(boolean top, boolean right) {
         this.top = top;
         this.right = right;
      }

      public String translationKey() {
         return "quadhotbar.layout.corner." + this.name().toLowerCase(Locale.ROOT);
      }

      // $VF: synthetic method
      private static QuadHotbarLayout.MainCorner[] $values() {
         return new QuadHotbarLayout.MainCorner[]{TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT};
      }
   }
}
