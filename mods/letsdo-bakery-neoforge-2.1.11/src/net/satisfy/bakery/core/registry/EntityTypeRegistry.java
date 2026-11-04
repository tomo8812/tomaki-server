package net.satisfy.bakery.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.satisfy.bakery.core.block.entity.BakerStationBlockEntity;
import net.satisfy.bakery.core.block.entity.BlankCakeBlockEntity;
import net.satisfy.bakery.core.block.entity.CakeCandleBlockEntity;
import net.satisfy.bakery.core.block.entity.SmallCookingPotBlockEntity;
import net.satisfy.bakery.core.block.entity.StreetSignBlockEntity;
import net.satisfy.foundation.banner.CompletionistBannerEntity;
import net.satisfy.foundation.block.CabinetBlockEntity;
import net.satisfy.foundation.storage.StorageBlockEntity;

public class EntityTypeRegistry {
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create("bakery", Registries.BLOCK_ENTITY_TYPE);
   public static final RegistrySupplier<BlockEntityType<SmallCookingPotBlockEntity>> SMALL_COOKING_POT_BLOCK_ENTITY = registerBlockEntity(
      "small_cooking_pot", () -> Builder.of(SmallCookingPotBlockEntity::new, new Block[]{(Block)ObjectRegistry.SMALL_COOKING_POT.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CompletionistBannerEntity>> BAKERY_BANNER = registerBlockEntity(
      "bakery_banner",
      () -> Builder.of(CompletionistBannerEntity::new, new Block[]{(Block)ObjectRegistry.BAKERY_BANNER.get(), (Block)ObjectRegistry.BAKERY_WALL_BANNER.get()})
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> STORAGE_ENTITY = registerBlockEntity(
      "storage",
      () -> Builder.of(
            (pos, state) -> new StorageBlockEntity((BlockEntityType)STORAGE_ENTITY.get(), pos, state),
            StorageTypeRegistry.registerBlocks(new HashSet<>()).toArray(new Block[0])
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CabinetBlockEntity>> CABINET_BLOCK_ENTITY = registerBlockEntity(
      "cabinet",
      () -> Builder.of(
            (pos, state) -> new CabinetBlockEntity((BlockEntityType)CABINET_BLOCK_ENTITY.get(), pos, state), addCabinet(new HashSet<>()).toArray(new Block[0])
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<BakerStationBlockEntity>> BAKER_STATION_BLOCK_ENTITY = registerBlockEntity(
      "baker_station", () -> Builder.of(BakerStationBlockEntity::new, new Block[]{(Block)ObjectRegistry.BAKER_STATION.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<BlankCakeBlockEntity>> BLANK_CAKE_BLOCK_ENTITY = registerBlockEntity(
      "blank_cake", () -> Builder.of(BlankCakeBlockEntity::new, new Block[]{(Block)ObjectRegistry.BLANK_CAKE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CakeCandleBlockEntity>> CAKE_CANDLE_BLOCK_ENTITY = registerBlockEntity(
      "cake_candle",
      () -> Builder.of(
            CakeCandleBlockEntity::new,
            new Block[]{
               (Block)ObjectRegistry.CHOCOLATE_GATEAU.get(),
               (Block)ObjectRegistry.CHOCOLATE_TART.get(),
               (Block)ObjectRegistry.STRAWBERRY_CAKE.get(),
               (Block)ObjectRegistry.SWEETBERRY_CAKE.get(),
               (Block)ObjectRegistry.CHOCOLATE_CAKE.get(),
               (Block)ObjectRegistry.BUNDT_CAKE.get(),
               (Block)ObjectRegistry.LINZER_TART.get(),
               (Block)ObjectRegistry.APPLE_PIE.get(),
               (Block)ObjectRegistry.GLOWBERRY_TART.get(),
               (Block)ObjectRegistry.PUDDING.get()
            }
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<StreetSignBlockEntity>> STREET_SIGN_BLOCK_ENTITY = registerBlockEntity(
      "street_sign", () -> Builder.of(StreetSignBlockEntity::new, new Block[]{(Block)ObjectRegistry.STREET_SIGN.get()}).build(null)
   );

   public static Set<Block> addCabinet(Set<Block> blocks) {
      blocks.add((Block)ObjectRegistry.CABINET.get());
      blocks.add((Block)ObjectRegistry.DRAWER.get());
      blocks.add((Block)ObjectRegistry.WALL_CABINET.get());
      return blocks;
   }

   private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(String name, Supplier<T> type) {
      return BLOCK_ENTITY_TYPES.register(name, type);
   }

   public static void init() {
      BLOCK_ENTITY_TYPES.register();
   }
}
