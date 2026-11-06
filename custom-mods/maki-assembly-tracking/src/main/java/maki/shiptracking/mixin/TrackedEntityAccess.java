package maki.shiptracking.mixin;
import java.util.Set;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(targets="net.minecraft.server.level.ChunkMap$TrackedEntity",remap=false)
public interface TrackedEntityAccess {
    @Accessor("serverEntity") ServerEntity maki$serverEntity();
    @Accessor("seenBy") Set<ServerPlayerConnection> maki$seenBy();
}
