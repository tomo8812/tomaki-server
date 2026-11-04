package fuzs.puzzleslib.api.attachment.v4;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public final class DataAttachmentRegistry {
   private DataAttachmentRegistry() {
   }

   public static <A> DataAttachmentRegistry.EntityBuilder<A> entityBuilder() {
      return DataAttachmentRegistryImpl.INSTANCE.getEntityTypeBuilder();
   }

   public static <A> DataAttachmentRegistry.BlockEntityBuilder<A> blockEntityBuilder() {
      return DataAttachmentRegistryImpl.INSTANCE.getBlockEntityTypeBuilder();
   }

   public static <A> DataAttachmentRegistry.Builder<LevelChunk, A> levelChunkBuilder() {
      return DataAttachmentRegistryImpl.INSTANCE.getLevelChunkBuilder();
   }

   public static <A> DataAttachmentRegistry.Builder<Level, A> levelBuilder() {
      return DataAttachmentRegistryImpl.INSTANCE.getLevelBuilder();
   }

   public interface BlockEntityBuilder<A> extends DataAttachmentRegistry.RegistryBuilder<BlockEntity, A> {
      default DataAttachmentRegistry.BlockEntityBuilder<A> defaultValue(A defaultValue) {
         return (DataAttachmentRegistry.BlockEntityBuilder<A>)DataAttachmentRegistry.RegistryBuilder.super.defaultValue(defaultValue);
      }

      @Override
      default DataAttachmentRegistry.RegistryBuilder<BlockEntity, A> defaultValue(Function<RegistryAccess, A> defaultValueProvider) {
         return DataAttachmentRegistry.RegistryBuilder.super.defaultValue(defaultValueProvider);
      }

      default DataAttachmentRegistry.BlockEntityBuilder<A> defaultValue(Class<? extends BlockEntity> type, A defaultValue) {
         return (DataAttachmentRegistry.BlockEntityBuilder<A>)DataAttachmentRegistry.RegistryBuilder.super.defaultValue(type, defaultValue);
      }

      default DataAttachmentRegistry.BlockEntityBuilder<A> defaultValue(Predicate<BlockEntity> defaultFilter, A defaultValue) {
         return (DataAttachmentRegistry.BlockEntityBuilder<A>)DataAttachmentRegistry.RegistryBuilder.super.defaultValue(defaultFilter, defaultValue);
      }

      @Override
      DataAttachmentRegistry.RegistryBuilder<BlockEntity, A> defaultValue(Predicate<BlockEntity> var1, Function<RegistryAccess, A> var2);

      default DataAttachmentRegistry.BlockEntityBuilder<A> defaultValue(BlockEntityType<?> type, A defaultValue) {
         return this.defaultValue(blockEntity -> blockEntity.getType() == type, (A)defaultValue);
      }

      DataAttachmentRegistry.BlockEntityBuilder<A> persistent(Codec<A> var1);

      DataAttachmentRegistry.BlockEntityBuilder<A> networkSynchronized(
         StreamCodec<? super RegistryFriendlyByteBuf, A> var1, Function<BlockEntity, PlayerSet> var2
      );

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.RegistryBuilder defaultValue(Predicate param1, Object nullx) {
         return this.defaultValue(var1, (A)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.RegistryBuilder defaultValue(Class param1, Object nullx) {
         return this.defaultValue(var1, (A)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder networkSynchronized(StreamCodec param1, Function nullx) {
         return this.networkSynchronized(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder persistent(Codec param1) {
         return this.persistent(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder defaultValue(Function param1) {
         return this.defaultValue(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder defaultValue(Object param1) {
         return this.defaultValue((A)var1);
      }
   }

   public interface Builder<T, A> {
      default DataAttachmentRegistry.Builder<T, A> defaultValue(A defaultValue) {
         return this.defaultValue(registries -> defaultValue);
      }

      DataAttachmentRegistry.Builder<T, A> defaultValue(Function<RegistryAccess, A> var1);

      DataAttachmentRegistry.Builder<T, A> persistent(Codec<A> var1);

      DataAttachmentRegistry.Builder<T, A> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, A> var1, Function<T, PlayerSet> var2);

      DataAttachmentType<T, A> build(ResourceLocation var1);
   }

   public interface EntityBuilder<A> extends DataAttachmentRegistry.RegistryBuilder<Entity, A> {
      default DataAttachmentRegistry.EntityBuilder<A> defaultValue(A defaultValue) {
         return (DataAttachmentRegistry.EntityBuilder<A>)DataAttachmentRegistry.RegistryBuilder.super.defaultValue(defaultValue);
      }

      default DataAttachmentRegistry.EntityBuilder<A> defaultValue(Function<RegistryAccess, A> defaultValueProvider) {
         return (DataAttachmentRegistry.EntityBuilder<A>)DataAttachmentRegistry.RegistryBuilder.super.defaultValue(defaultValueProvider);
      }

      default DataAttachmentRegistry.EntityBuilder<A> defaultValue(Predicate<Entity> defaultFilter, A defaultValue) {
         return (DataAttachmentRegistry.EntityBuilder<A>)DataAttachmentRegistry.RegistryBuilder.super.defaultValue(defaultFilter, defaultValue);
      }

      default DataAttachmentRegistry.EntityBuilder<A> defaultValue(Class<? extends Entity> type, A defaultValue) {
         return (DataAttachmentRegistry.EntityBuilder<A>)DataAttachmentRegistry.RegistryBuilder.super.defaultValue(type, defaultValue);
      }

      DataAttachmentRegistry.EntityBuilder<A> defaultValue(Predicate<Entity> var1, Function<RegistryAccess, A> var2);

      default DataAttachmentRegistry.EntityBuilder<A> defaultValue(EntityType<?> type, A defaultValue) {
         return this.defaultValue(entity -> entity.getType() == type, (A)defaultValue);
      }

      DataAttachmentRegistry.EntityBuilder<A> persistent(Codec<A> var1);

      @Deprecated
      default DataAttachmentRegistry.EntityBuilder<A> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec) {
         return this.networkSynchronized(streamCodec, PlayerSet::ofEntity);
      }

      DataAttachmentRegistry.EntityBuilder<A> networkSynchronized(
         StreamCodec<? super RegistryFriendlyByteBuf, A> var1, @Nullable Function<Entity, PlayerSet> var2
      );

      DataAttachmentRegistry.EntityBuilder<A> copyOnDeath();

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.RegistryBuilder defaultValue(Predicate param1, Function nullx) {
         return this.defaultValue(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.RegistryBuilder defaultValue(Predicate param1, Object nullx) {
         return this.defaultValue(var1, (A)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.RegistryBuilder defaultValue(Class param1, Object nullx) {
         return this.defaultValue(var1, (A)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.RegistryBuilder defaultValue(Function param1) {
         return this.defaultValue(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder networkSynchronized(StreamCodec param1, @Nullable Function nullx) {
         return this.networkSynchronized(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder persistent(Codec param1) {
         return this.persistent(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder defaultValue(Function param1) {
         return this.defaultValue(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder defaultValue(Object param1) {
         return this.defaultValue((A)var1);
      }
   }

   public interface RegistryBuilder<T, A> extends DataAttachmentRegistry.Builder<T, A> {
      default DataAttachmentRegistry.RegistryBuilder<T, A> defaultValue(Function<RegistryAccess, A> defaultValueProvider) {
         return this.defaultValue(Predicates.alwaysTrue(), defaultValueProvider);
      }

      default DataAttachmentRegistry.RegistryBuilder<T, A> defaultValue(Class<? extends T> type, A defaultValue) {
         return this.defaultValue(type::isInstance, defaultValue);
      }

      default DataAttachmentRegistry.RegistryBuilder<T, A> defaultValue(Predicate<T> defaultFilter, A defaultValue) {
         return this.defaultValue(defaultFilter, registries -> defaultValue);
      }

      DataAttachmentRegistry.RegistryBuilder<T, A> defaultValue(Predicate<T> var1, Function<RegistryAccess, A> var2);

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default DataAttachmentRegistry.Builder defaultValue(Function param1) {
         return this.defaultValue(var1);
      }
   }
}
