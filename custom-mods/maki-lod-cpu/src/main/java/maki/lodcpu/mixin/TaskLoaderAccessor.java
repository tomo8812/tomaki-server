package maki.lodcpu.mixin;

import java.util.Map;
import java.util.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets="com.kishku7.chunksmith.util.TaskLoader",remap=false)
public interface TaskLoaderAccessor {
    @Accessor("tasks")
    Map<String,Properties> maki$getTasks();
}
