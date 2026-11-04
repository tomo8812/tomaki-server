package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

public final class EntityHelper {
   private EntityHelper() {
   }

   public static boolean canEquip(ItemStack itemStack, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
      Objects.requireNonNull(itemStack, "item stack is null");
      Objects.requireNonNull(equipmentSlot, "equipment slot is null");
      Objects.requireNonNull(livingEntity, "living entity is null");
      return ProxyImpl.get().canEquip(itemStack, equipmentSlot, livingEntity);
   }

   @Deprecated
   @Nullable
   public static MobSpawnType getMobSpawnReason(Entity entity) {
      Objects.requireNonNull(entity, "entity is null");
      return entity instanceof Mob mob ? ProxyImpl.get().getMobSpawnReason(mob) : null;
   }

   public static boolean isMobGriefingAllowed(ServerLevel serverLevel, @Nullable Entity entity) {
      Objects.requireNonNull(serverLevel, "server level is null");
      return ProxyImpl.get().isMobGriefingAllowed(serverLevel, entity);
   }

   public static Entity getPartEntityParent(Entity entity) {
      Objects.requireNonNull(entity, "entity is null");
      return ProxyImpl.get().getPartEntityParent(entity);
   }

   public static boolean isFakePlayer(ServerPlayer serverPlayer) {
      Objects.requireNonNull(serverPlayer, "server player is null");
      return ProxyImpl.get().isFakePlayer(serverPlayer);
   }

   public static boolean isPiglinCurrency(ItemStack itemStack) {
      return ProxyImpl.get().isPiglinCurrency(itemStack);
   }

   public static boolean dropFromGiftLootTable(
      LivingEntity entity, ServerLevel level, ResourceKey<LootTable> key, BiConsumer<ServerLevel, ItemStack> dropConsumer
   ) {
      return dropFromLootTable(
         level,
         key,
         params -> params.withParameter(LootContextParams.ORIGIN, entity.position())
            .withParameter(LootContextParams.THIS_ENTITY, entity)
            .create(LootContextParamSets.GIFT),
         dropConsumer
      );
   }

   public static boolean dropFromShearingLootTable(
      LivingEntity entity, ServerLevel level, ResourceKey<LootTable> key, BiConsumer<ServerLevel, ItemStack> dropConsumer
   ) {
      return dropFromLootTable(
         level,
         key,
         params -> params.withParameter(LootContextParams.ORIGIN, entity.position())
            .withParameter(LootContextParams.THIS_ENTITY, entity)
            .create(LootContextParamSets.SHEARING),
         dropConsumer
      );
   }

   public static boolean dropFromLootTable(
      ServerLevel level, ResourceKey<LootTable> key, Function<Builder, LootParams> paramsBuilder, BiConsumer<ServerLevel, ItemStack> dropConsumer
   ) {
      LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(key);
      LootParams params = paramsBuilder.apply(new Builder(level));
      List<ItemStack> drops = lootTable.getRandomItems(params);
      if (!drops.isEmpty()) {
         drops.forEach(stack -> dropConsumer.accept(level, stack));
         return true;
      } else {
         return false;
      }
   }
}
