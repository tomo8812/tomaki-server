package org.valkyrienskies.mod.mixin.server.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.Queue;
import net.minecraft.class_1923;
import net.minecraft.class_3193;
import net.minecraft.class_3227;
import net.minecraft.class_3898;
import net.minecraft.class_3900;
import net.minecraft.class_4153;
import net.minecraft.class_3898.class_3216;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_3898.class)
public class MixinChunkMapClose {
   private static final Logger LOGGER = LoggerFactory.getLogger("VS2-ShutdownDiag");
   @Shadow
   @Final
   private class_3227 field_17215;
   @Shadow
   @Final
   private Long2ObjectLinkedOpenHashMap<class_3193> field_18807;
   @Shadow
   @Final
   private Long2ObjectLinkedOpenHashMap<class_3193> field_17213;
   @Shadow
   @Final
   private class_4153 field_18808;
   @Shadow
   @Final
   private LongSet field_17221;
   @Shadow
   @Final
   private Queue<Runnable> field_19343;
   @Shadow
   @Final
   private class_3900 field_17223;
   @Shadow
   @Final
   class_3216 field_17228;

   @Inject(method = "hasWork", at = @At("HEAD"), cancellable = true)
   private void vs$hasWorkSkipShipyard(CallbackInfoReturnable<Boolean> cir) {
      if (!this.field_17213.isEmpty()) {
         boolean hasNonShipyard = false;
         ObjectBidirectionalIterator result = this.field_17213.long2ObjectEntrySet().iterator();

         while (result.hasNext()) {
            Entry<class_3193> entry = (Entry<class_3193>)result.next();
            long key = entry.getLongKey();
            int cx = class_1923.method_8325(key);
            int cz = class_1923.method_8332(key);
            if (!VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(cx, cz)) {
               hasNonShipyard = true;
               break;
            }
         }

         if (!hasNonShipyard) {
            boolean resultx = this.field_17215.method_15518()
               || !this.field_18807.isEmpty()
               || this.field_18808.method_40020()
               || !this.field_17221.isEmpty()
               || !this.field_19343.isEmpty()
               || this.field_17223.method_39994();
            cir.setReturnValue(resultx);
         }
      }
   }
}
