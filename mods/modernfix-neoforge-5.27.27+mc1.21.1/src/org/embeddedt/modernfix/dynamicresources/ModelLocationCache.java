package org.embeddedt.modernfix.dynamicresources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class ModelLocationCache {
   private static final LoadingCache<BlockState, ModelResourceLocation> blockLocationCache = CacheBuilder.newBuilder()
      .maximumSize(10000L)
      .build(new CacheLoader<BlockState, ModelResourceLocation>() {
         public ModelResourceLocation load(BlockState key) throws Exception {
            return BlockModelShaper.stateToModelLocation(key);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object load(Object param1) throws Exception {
            return this.load((BlockState)var1);
         }
      });
   private static final LoadingCache<Item, ModelResourceLocation> itemLocationCache = CacheBuilder.newBuilder()
      .maximumSize(10000L)
      .build(new CacheLoader<Item, ModelResourceLocation>() {
         public ModelResourceLocation load(Item key) throws Exception {
            return new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(key), "inventory");
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object load(Object param1) throws Exception {
            return this.load((Item)var1);
         }
      });

   public static ModelResourceLocation get(BlockState state) {
      if (state == null) {
         return null;
      }

      try {
         return (ModelResourceLocation)blockLocationCache.get(state);
      } catch (ExecutionException e) {
         throw new RuntimeException(e.getCause());
      }
   }

   public static ModelResourceLocation get(Item item) {
      if (item == null) {
         return null;
      }

      try {
         return (ModelResourceLocation)itemLocationCache.get(item);
      } catch (ExecutionException e) {
         throw new RuntimeException(e.getCause());
      }
   }
}
