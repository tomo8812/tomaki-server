package org.embeddedt.modernfix.common.mixin.perf.tag_id_caching;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.util.ExtraCodecs.TagOrElementLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagEntry.class)
public class TagEntryMixin {
   @Shadow
   @Final
   private boolean tag;
   @Shadow
   @Final
   private ResourceLocation id;
   private TagOrElementLocation cachedLoc;

   @Overwrite
   private TagOrElementLocation elementOrTag() {
      TagOrElementLocation loc = this.cachedLoc;
      if (loc == null) {
         loc = new TagOrElementLocation(this.id, this.tag);
         this.cachedLoc = loc;
      }

      return loc;
   }
}
