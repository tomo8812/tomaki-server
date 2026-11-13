package maki.millenairelod.mixin;

import java.util.Queue;
import java.util.Deque;
import net.minecraft.core.BlockPos;
import org.millenaire.world.VillageSpawnQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value=VillageSpawnQueue.class,remap=false)
public interface VillageQueueAccessor {
    @Accessor("candidates") Queue<BlockPos> maki$candidates();
    @Accessor("loneBuildingQueue") Deque<Object> maki$loneBuildings();
}
