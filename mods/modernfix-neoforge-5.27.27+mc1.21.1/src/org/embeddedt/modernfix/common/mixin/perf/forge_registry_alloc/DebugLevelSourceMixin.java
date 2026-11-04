package org.embeddedt.modernfix.common.mixin.perf.forge_registry_alloc;

import java.util.AbstractList;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.minecraft.core.IdMapper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.neoforged.neoforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DebugLevelSource.class)
public class DebugLevelSourceMixin {
   @Redirect(
      method = "initValidStates",
      at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;", ordinal = 0),
      remap = false
   )
   private static Object getStateList(Stream<?> instance, Collector<?, ?, ?> arCollector) {
      final IdMapper<BlockState> idMapper = GameData.getBlockStateIDMap();
      return new AbstractList<Object>() {
         @Override
         public int size() {
            return idMapper.size();
         }

         @Override
         public Object get(int index) {
            BlockState o = (BlockState)idMapper.byId(index);
            if (o == null) {
               throw new IndexOutOfBoundsException();
            } else {
               return o;
            }
         }
      };
   }
}
