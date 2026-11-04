package org.embeddedt.modernfix.common.mixin.perf.deduplicate_wall_shapes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin extends Block {
   private static Map<ImmutableList<Float>, Pair<Map<Map<Property<?>, Comparable<?>>, VoxelShape>, StateDefinition<Block, BlockState>>> CACHE_BY_SHAPE_VALS = new HashMap<>();

   public WallBlockMixin(Properties properties) {
      super(properties);
   }

   @Inject(method = "makeShapes", at = @At("HEAD"), cancellable = true)
   private synchronized void useCachedShapeMap(
      float f1, float f2, float f3, float f4, float f5, float f6, CallbackInfoReturnable<Map<BlockState, VoxelShape>> cir
   ) {
      ImmutableList<Float> key = ImmutableList.of(f1, f2, f3, f4, f5, f6);
      Pair<Map<Map<Property<?>, Comparable<?>>, VoxelShape>, StateDefinition<Block, BlockState>> cache = CACHE_BY_SHAPE_VALS.get(key);
      if (cache != null && ((StateDefinition)cache.getSecond()).getProperties().equals(this.stateDefinition.getProperties())) {
         Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
         UnmodifiableIterator var11 = this.stateDefinition.getPossibleStates().iterator();

         while (var11.hasNext()) {
            BlockState state = (BlockState)var11.next();
            VoxelShape shape = (VoxelShape)((Map)cache.getFirst()).get(state.getValues());
            if (shape == null) {
               return;
            }

            builder.put(state, shape);
         }

         cir.setReturnValue(builder.build());
      }
   }

   @Inject(method = "makeShapes", at = @At("RETURN"))
   private synchronized void storeCachedShapesByProperty(
      float f1, float f2, float f3, float f4, float f5, float f6, CallbackInfoReturnable<Map<BlockState, VoxelShape>> cir
   ) {
      if (this.getClass() == WallBlock.class) {
         ImmutableList<Float> key = ImmutableList.of(f1, f2, f3, f4, f5, f6);
         if (!CACHE_BY_SHAPE_VALS.containsKey(key)) {
            Map<Map<Property<?>, Comparable<?>>, VoxelShape> cacheByProperties = new HashMap<>();
            Map<BlockState, VoxelShape> shapeMap = (Map<BlockState, VoxelShape>)cir.getReturnValue();

            for (Entry<BlockState, VoxelShape> entry : shapeMap.entrySet()) {
               cacheByProperties.put(entry.getKey().getValues(), entry.getValue());
            }

            CACHE_BY_SHAPE_VALS.put(key, Pair.of(cacheByProperties, this.stateDefinition));
         }
      }
   }
}
