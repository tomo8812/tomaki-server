package org.valkyrienskies.mod.util;

import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_2338.class_2339;

public final class BlockWalker {
   private static final double EPS = 1.0E-7;
   private class_2339 nextValue = new class_2339();
   private final int xStep;
   private final int yStep;
   private final int zStep;
   private final double xPartialStep;
   private final double yPartialStep;
   private final double zPartialStep;
   private int x;
   private int y;
   private int z;
   private double xPartial;
   private double yPartial;
   private double zPartial;

   public BlockWalker(class_243 from, class_243 to) {
      if (from.equals(to)) {
         this.nextValue = null;
         this.xStep = 0;
         this.yStep = 0;
         this.zStep = 0;
         this.xPartialStep = 0.0;
         this.yPartialStep = 0.0;
         this.zPartialStep = 0.0;
      } else {
         double afterToX = from.field_1352 < to.field_1352
            ? to.field_1352 + 1.0E-7
            : (to.field_1352 < from.field_1352 ? to.field_1352 - 1.0E-7 : to.field_1352);
         double afterToY = from.field_1351 < to.field_1351
            ? to.field_1351 + 1.0E-7
            : (to.field_1351 < from.field_1351 ? to.field_1351 - 1.0E-7 : to.field_1351);
         double afterToZ = from.field_1350 < to.field_1350
            ? to.field_1350 + 1.0E-7
            : (to.field_1350 < from.field_1350 ? to.field_1350 - 1.0E-7 : to.field_1350);
         double beforeFromX = from.field_1352 < to.field_1352
            ? from.field_1352 - 1.0E-7
            : (to.field_1352 < from.field_1352 ? from.field_1352 + 1.0E-7 : from.field_1352);
         double beforeFromY = from.field_1351 < to.field_1351
            ? from.field_1351 - 1.0E-7
            : (to.field_1351 < from.field_1351 ? from.field_1351 + 1.0E-7 : from.field_1351);
         double beforeFromZ = from.field_1350 < to.field_1350
            ? from.field_1350 - 1.0E-7
            : (to.field_1350 < from.field_1350 ? from.field_1350 + 1.0E-7 : from.field_1350);
         this.x = class_3532.method_15357(from.field_1352);
         this.y = class_3532.method_15357(from.field_1351);
         this.z = class_3532.method_15357(from.field_1350);
         double xDiff = afterToX - beforeFromX;
         double yDiff = afterToY - beforeFromY;
         double zDiff = afterToZ - beforeFromZ;
         this.xStep = class_3532.method_17822(xDiff);
         this.yStep = class_3532.method_17822(yDiff);
         this.zStep = class_3532.method_17822(zDiff);
         this.xPartialStep = this.xStep == 0 ? Double.MAX_VALUE : this.xStep / xDiff;
         this.yPartialStep = this.yStep == 0 ? Double.MAX_VALUE : this.yStep / yDiff;
         this.zPartialStep = this.zStep == 0 ? Double.MAX_VALUE : this.zStep / zDiff;
         this.xPartial = this.xPartialStep * (this.xStep > 0 ? 1.0 - class_3532.method_15385(beforeFromX) : class_3532.method_15385(beforeFromX));
         this.yPartial = this.yPartialStep * (this.yStep > 0 ? 1.0 - class_3532.method_15385(beforeFromY) : class_3532.method_15385(beforeFromY));
         this.zPartial = this.zPartialStep * (this.zStep > 0 ? 1.0 - class_3532.method_15385(beforeFromZ) : class_3532.method_15385(beforeFromZ));
      }
   }

   public class_2338 value() {
      return this.nextValue == null ? null : this.nextValue.method_10103(this.x, this.y, this.z);
   }

   public boolean next() {
      if (this.nextValue == null) {
         return false;
      }

      if (this.xPartial > 1.0 && this.yPartial > 1.0 && this.zPartial > 1.0) {
         this.nextValue = null;
         return false;
      }

      if (this.xPartial < this.yPartial) {
         if (this.xPartial < this.zPartial) {
            this.x = this.x + this.xStep;
            this.xPartial = this.xPartial + this.xPartialStep;
         } else {
            this.z = this.z + this.zStep;
            this.zPartial = this.zPartial + this.zPartialStep;
         }
      } else if (this.yPartial < this.zPartial) {
         this.y = this.y + this.yStep;
         this.yPartial = this.yPartial + this.yPartialStep;
      } else {
         this.z = this.z + this.zStep;
         this.zPartial = this.zPartial + this.zPartialStep;
      }

      return true;
   }
}
