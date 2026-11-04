package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class NeoForgeBlockEntityDataAttachmentBuilder<V>
   extends NeoForgeDataAttachmentBuilder<BlockEntity, V>
   implements DataAttachmentRegistry.BlockEntityBuilder<V> {
   public DataAttachmentRegistry.BlockEntityBuilder<V> defaultValue(Predicate<BlockEntity> defaultFilter, Function<RegistryAccess, V> defaultValueProvider) {
      Objects.requireNonNull(defaultFilter, "default filter is null");
      Objects.requireNonNull(defaultValueProvider, "default value provider is null");
      this.defaultValues.put(defaultFilter, defaultValueProvider);
      return this;
   }

   protected RegistryAccess getRegistryAccess(BlockEntity holder) {
      return holder.getLevel().registryAccess();
   }

   public DataAttachmentRegistry.BlockEntityBuilder<V> defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
      return (DataAttachmentRegistry.BlockEntityBuilder<V>)super.defaultValue(defaultValueProvider);
   }

   @Override
   public DataAttachmentRegistry.BlockEntityBuilder<V> persistent(Codec<V> codec) {
      return (DataAttachmentRegistry.BlockEntityBuilder<V>)super.persistent(codec);
   }

   @Override
   public DataAttachmentRegistry.BlockEntityBuilder<V> networkSynchronized(
      StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec, Function<BlockEntity, PlayerSet> targetSelector
   ) {
      return (DataAttachmentRegistry.BlockEntityBuilder<V>)super.networkSynchronized(streamCodec, targetSelector);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected RegistryAccess getRegistryAccess(Object param1) {
      return this.getRegistryAccess((BlockEntity)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public DataAttachmentRegistry.Builder networkSynchronized(StreamCodec param1, Function nullx) {
      return this.networkSynchronized(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public DataAttachmentRegistry.Builder persistent(Codec param1) {
      return this.persistent(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public DataAttachmentRegistry.Builder defaultValue(Function param1) {
      return this.defaultValue(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public DataAttachmentRegistry.RegistryBuilder defaultValue(Predicate param1, Function nullx) {
      return this.defaultValue(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public DataAttachmentRegistry.RegistryBuilder defaultValue(Function param1) {
      return this.defaultValue(var1);
   }
}
