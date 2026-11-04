package de.maxhenkel.gravestone.corelib.math;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class Rotation {
   private List<Rotation.AxisRotation> rotations = new ArrayList<>();

   public Rotation(float angle, Axis axis) {
      this();
      this.add(angle, axis);
   }

   public Rotation() {
   }

   public Rotation add(float angle, Axis axis) {
      this.rotations.add(new Rotation.AxisRotation(angle, axis));
      return this;
   }

   public Rotation add(@Nullable Rotation rotation) {
      if (rotation != null) {
         this.rotations.addAll(rotation.rotations);
      }

      return this;
   }

   public void applyRotation(PoseStack matrixStack) {
      for (Rotation.AxisRotation rotation : this.rotations) {
         matrixStack.mulPose(rotation.axis.rotationDegrees(rotation.angle));
      }
   }

   private static class AxisRotation {
      private float angle;
      private Axis axis;

      public AxisRotation(float angle, Axis axis) {
         this.angle = angle;
         this.axis = axis;
      }
   }
}
