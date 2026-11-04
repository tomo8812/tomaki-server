package net.caffeinemc.mods.lithium.common.shapes.lists;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class OffsetFractionalDoubleList extends AbstractDoubleList {
   private final int numSections;
   private final double offset;

   public OffsetFractionalDoubleList(int numSections, double offset) {
      this.numSections = numSections;
      this.offset = offset;
   }

   public double getDouble(int position) {
      return this.offset + (double)position / this.numSections;
   }

   public int size() {
      return this.numSections + 1;
   }
}
