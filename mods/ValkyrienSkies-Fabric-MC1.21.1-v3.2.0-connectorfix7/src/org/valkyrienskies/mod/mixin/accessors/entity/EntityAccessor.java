package org.valkyrienskies.mod.mixin.accessors.entity;

import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1297.class)
public interface EntityAccessor {
   @Accessor("position")
   void setPosNoUpdates(class_243 var1);

   @Accessor("blockPosition")
   void setBlockPosition(class_2338 var1);

   @Accessor("blockPosition")
   class_2338 getBlockPosition();

   @Accessor("inBlockState")
   void setInBlockState(class_2680 var1);

   @Accessor("portalCooldown")
   void setPortalCooldown(int var1);

   @Accessor("portalCooldown")
   int getPortalCooldown();
}
