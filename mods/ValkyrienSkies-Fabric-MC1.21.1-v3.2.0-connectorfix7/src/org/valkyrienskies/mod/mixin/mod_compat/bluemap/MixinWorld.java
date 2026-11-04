package org.valkyrienskies.mod.mixin.mod_compat.bluemap;

import de.bluecolored.bluemap.core.world.mca.MCAWorld;
import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.mixin.server.MinecraftServerAccessor;
import org.valkyrienskies.mod.mixinducks.mod_compat.bluemap.WorldDuck;

@Mixin(MCAWorld.class)
@Pseudo
public class MixinWorld implements WorldDuck {
   @Shadow
   @Final
   private Path worldFolder;

   @Override
   public class_1937 valkyrienskies$getCorrelatingLevel() {
      for (class_3218 level : Objects.requireNonNull(ValkyrienSkiesMod.getCurrentServer()).method_3738()) {
         MinecraftServerAccessor accessor = (MinecraftServerAccessor)ValkyrienSkiesMod.getCurrentServer();
         Path path1 = accessor.getStorageSource().method_27424(level.method_27983()).toAbsolutePath().normalize();
         Path path2 = this.worldFolder.toAbsolutePath().normalize();
         if (path1.equals(path2)) {
            return level;
         }
      }

      return null;
   }
}
