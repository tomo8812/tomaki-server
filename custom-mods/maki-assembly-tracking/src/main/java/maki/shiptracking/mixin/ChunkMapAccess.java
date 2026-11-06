package maki.shiptracking.mixin;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=ChunkMap.class,remap=false)
public interface ChunkMapAccess {
    @Accessor("entityMap") Int2ObjectMap<Object> maki$entities();
}
