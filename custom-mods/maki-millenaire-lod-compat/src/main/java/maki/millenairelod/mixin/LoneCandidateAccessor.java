package maki.millenairelod.mixin;

import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "org.millenaire.world.VillageSpawnQueue$LBCandidate", remap = false)
public interface LoneCandidateAccessor {
    @Accessor("pos") BlockPos compat$pos();
}
