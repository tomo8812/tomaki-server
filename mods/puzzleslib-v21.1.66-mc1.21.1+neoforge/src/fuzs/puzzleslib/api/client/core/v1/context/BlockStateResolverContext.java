package fuzs.puzzleslib.api.client.core.v1.context;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateResolverContext {
   void registerBlockStateResolver(Block var1, Consumer<BiConsumer<BlockState, UnbakedModel>> var2);

   <T> void registerBlockStateResolver(
      Block var1, BiFunction<ResourceManager, Executor, CompletableFuture<T>> var2, BiConsumer<T, BiConsumer<BlockState, UnbakedModel>> var3
   );
}
