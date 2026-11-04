package snownee.jade.addon.vanilla;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.util.CommonProxy;

public enum AnimalOwnerProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, String> {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
      String name = this.decodeFromData(accessor).orElse("");
      if (name.isEmpty()) {
         UUID ownerUUID = getOwnerUUID(accessor.getEntity());
         if (ownerUUID == null) {
            return;
         }

         name = CommonProxy.getLastKnownUsername(ownerUUID);
         if (name == null) {
            name = "???";
         }
      }

      tooltip.add(Component.translatable("jade.owner", new Object[]{name}));
   }

   public String streamData(EntityAccessor accessor) {
      return CommonProxy.getLastKnownUsername(getOwnerUUID(accessor.getEntity()));
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, String> streamCodec() {
      return ByteBufCodecs.STRING_UTF8.cast();
   }

   public static UUID getOwnerUUID(Entity entity) {
      return entity instanceof OwnableEntity ownableEntity ? ownableEntity.getOwnerUUID() : null;
   }

   public boolean shouldRequestData(EntityAccessor accessor) {
      Entity entity = accessor.getEntity();
      return entity instanceof OwnableEntity && getOwnerUUID(entity) == null;
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_ANIMAL_OWNER;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void appendTooltip(ITooltip param1, Accessor nullx, IPluginConfig nullxx) {
      this.appendTooltip(var1, (EntityAccessor)nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object streamData(Accessor param1) {
      return this.streamData((EntityAccessor)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean shouldRequestData(Accessor param1) {
      return this.shouldRequestData((EntityAccessor)var1);
   }

   // $VF: synthetic method
   private static AnimalOwnerProvider[] $values() {
      return new AnimalOwnerProvider[]{INSTANCE};
   }
}
