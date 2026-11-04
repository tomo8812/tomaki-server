package snownee.jade.addon.vanilla;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum ZombieVillagerProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, Integer> {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
      int time = this.decodeFromData(accessor).orElse(0);
      if (time > 0) {
         tooltip.add(Component.translatable("jade.zombieConversion.time", new Object[]{IThemeHelper.get().seconds(time, accessor.tickRate())}));
      }
   }

   public boolean shouldRequestData(EntityAccessor accessor) {
      return ((ZombieVillager)accessor.getEntity()).isConverting();
   }

   @Nullable
   public Integer streamData(EntityAccessor accessor) {
      int time = ((ZombieVillager)accessor.getEntity()).villagerConversionTime;
      return time > 0 ? time : null;
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, Integer> streamCodec() {
      return ByteBufCodecs.VAR_INT.cast();
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_ZOMBIE_VILLAGER;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void appendTooltip(ITooltip param1, Accessor nullx, IPluginConfig nullxx) {
      this.appendTooltip(var1, (EntityAccessor)nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
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
   private static ZombieVillagerProvider[] $values() {
      return new ZombieVillagerProvider[]{INSTANCE};
   }
}
