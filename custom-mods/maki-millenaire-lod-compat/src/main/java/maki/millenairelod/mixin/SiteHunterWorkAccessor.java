package maki.millenairelod.mixin;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import org.millenaire.world.SiteHunter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value=SiteHunter.class,remap=false)
public interface SiteHunterWorkAccessor {
    @Accessor("work") LongArrayFIFOQueue maki$work();
}
