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
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType.Builder;

public final class NeoForgeEntityDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<Entity, V> implements DataAttachmentRegistry.EntityBuilder<V> {
   private boolean copyOnDeath;

   @Override
   public DataAttachmentRegistry.EntityBuilder<V> defaultValue(Predicate<Entity> defaultFilter, Function<RegistryAccess, V> defaultValueProvider) {
      Objects.requireNonNull(defaultFilter, "default filter is null");
      Objects.requireNonNull(defaultValueProvider, "default value provider is null");
      this.defaultValues.put(defaultFilter, defaultValueProvider);
      return this;
   }

   @Override
   public DataAttachmentRegistry.EntityBuilder<V> copyOnDeath() {
      this.copyOnDeath = true;
      return this;
   }

   @Override
   void configureBuilder(Builder<V> builder) {
      super.configureBuilder(builder);
      if (this.copyOnDeath) {
         Objects.requireNonNull(this.codec, "codec is null");
         builder.copyOnDeath();
      }
   }

   protected RegistryAccess getRegistryAccess(Entity holder) {
      return holder.registryAccess();
   }

   @Override
   public DataAttachmentRegistry.EntityBuilder<V> defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
      return (DataAttachmentRegistry.EntityBuilder<V>)super.defaultValue(defaultValueProvider);
   }

   @Override
   public DataAttachmentRegistry.EntityBuilder<V> persistent(Codec<V> codec) {
      return (DataAttachmentRegistry.EntityBuilder<V>)super.persistent(codec);
   }

   @Override
   public DataAttachmentRegistry.EntityBuilder<V> networkSynchronized(
      StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec, Function<Entity, PlayerSet> targetSelector
   ) {
      return (DataAttachmentRegistry.EntityBuilder<V>)super.networkSynchronized(streamCodec, targetSelector != null ? targetSelector : PlayerSet::ofEntity);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected RegistryAccess getRegistryAccess(Object param1) {
      return this.getRegistryAccess((Entity)var1);
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
