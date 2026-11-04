package org.valkyrienskies.mod.mixin.feature.sound.client;

import net.minecraft.class_1101;
import net.minecraft.class_1102;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.client.audio.VelocityTickableSoundInstance;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1101.class)
public abstract class MixinAbstractTickableSoundInstance extends class_1102 implements VelocityTickableSoundInstance {
   @Unique
   private Vector3dc position = null;
   @Unique
   private Vector3dc velocity = new Vector3d();

   protected MixinAbstractTickableSoundInstance() {
      super((class_2960)null, null, null);
   }

   public double method_4784() {
      return this.position == null ? this.field_5439 : this.position.x();
   }

   public double method_4779() {
      return this.position == null ? this.field_5450 : this.position.y();
   }

   public double method_4778() {
      return this.position == null ? this.field_5449 : this.position.z();
   }

   @Override
   public @NonNull Vector3dc getVelocity() {
      return this.velocity;
   }

   @Override
   public void updateVelocity() {
      Vector3d newPosition = new Vector3d(this.field_5439, this.field_5450, this.field_5449);
      ClientShip ship = VSGameUtilsKt.getLoadedShipManagingPos(class_310.method_1551().field_1687, this.field_5439, this.field_5450, this.field_5449);
      if (ship != null) {
         ship.getRenderTransform().getShipToWorld().transformPosition(newPosition);
      }

      if (this.position != null) {
         this.velocity = newPosition.sub(this.position, new Vector3d());
      }

      this.position = newPosition;
   }
}
