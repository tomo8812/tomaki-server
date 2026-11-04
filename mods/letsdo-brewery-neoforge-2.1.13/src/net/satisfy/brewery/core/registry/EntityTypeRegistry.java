package net.satisfy.brewery.core.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.block.entity.BeerMugBlockEntity;
import net.satisfy.brewery.core.block.entity.BigBarrelBlockEntity;
import net.satisfy.brewery.core.block.entity.BrewTimerBlockEntity;
import net.satisfy.brewery.core.block.entity.BrewWhistleBlockEntity;
import net.satisfy.brewery.core.block.entity.BrewstationBlockEntity;
import net.satisfy.brewery.core.entity.BeerElementalAttackEntity;
import net.satisfy.brewery.core.entity.BeerElementalEntity;
import net.satisfy.brewery.core.entity.DarkBrewEntity;
import net.satisfy.foundation.banner.CompletionistBannerEntity;
import net.satisfy.foundation.block.CabinetBlockEntity;
import net.satisfy.foundation.block.WallDecorationBlockEntity;
import net.satisfy.foundation.storage.StorageBlockEntity;

public class EntityTypeRegistry {
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create("brewery", Registries.BLOCK_ENTITY_TYPE);
   private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create("brewery", Registries.ENTITY_TYPE);
   public static final RegistrySupplier<EntityType<DarkBrewEntity>> DARK_BREW = registerEntityType(
      "dark_brew", () -> Builder.of(DarkBrewEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(Brewery.identifier("dark_brew").toString())
   );
   public static final RegistrySupplier<EntityType<BeerElementalEntity>> BEER_ELEMENTAL = registerEntityType(
      "beer_elemental",
      () -> Builder.of(BeerElementalEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.6F)
         .clientTrackingRange(80)
         .updateInterval(3)
         .build(Brewery.identifier("beer_elemental").toString())
   );
   public static final RegistrySupplier<EntityType<BeerElementalAttackEntity>> BEER_ELEMENTAL_ATTACK = registerEntityType(
      "beer_elemental_attack",
      () -> Builder.of(BeerElementalAttackEntity::new, MobCategory.MISC)
         .sized(0.3125F, 0.3125F)
         .clientTrackingRange(4)
         .updateInterval(10)
         .build(Brewery.identifier("beer_elemental_attack").toString())
   );
   public static final RegistrySupplier<BlockEntityType<BrewstationBlockEntity>> BREWINGSTATION_BLOCK_ENTITY = registerBlockEntity(
      "brewingstation",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
            BrewstationBlockEntity::new,
            new Block[]{
               (Block)ObjectRegistry.WOODEN_BREWINGSTATION.get(),
               (Block)ObjectRegistry.COPPER_BREWINGSTATION.get(),
               (Block)ObjectRegistry.NETHERITE_BREWINGSTATION.get()
            }
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<BrewTimerBlockEntity>> BREW_TIMER_BLOCK_ENTITY = registerBlockEntity(
      "brew_timer",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(BrewTimerBlockEntity::new, new Block[]{(Block)ObjectRegistry.BREW_TIMER.get()})
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<BrewWhistleBlockEntity>> BREW_WHISTLE_BLOCK_ENTITY = registerBlockEntity(
      "brew_whistle",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
            BrewWhistleBlockEntity::new, new Block[]{(Block)ObjectRegistry.BREW_WHISTLE.get()}
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<BigBarrelBlockEntity>> BIG_BARREL_BLOCK_ENTITY = registerBlockEntity(
      "big_barrel",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(BigBarrelBlockEntity::new, new Block[]{(Block)ObjectRegistry.BARREL_MAIN.get()})
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<BeerMugBlockEntity>> BEER_MUG_BLOCK_ENTITY = registerBlockEntity(
      "beer_mug",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(BeerMugBlockEntity::new, new Block[]{(Block)ObjectRegistry.BEER_MUG.get()})
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> STORAGE_ENTITY = registerBlockEntity(
      "storage",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
            (pos, state) -> new StorageBlockEntity((BlockEntityType)STORAGE_ENTITY.get(), pos, state),
            StorageTypeRegistry.registerBlocks(new HashSet<>()).toArray(new Block[0])
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CabinetBlockEntity>> CABINET_BLOCK_ENTITY = registerBlockEntity(
      "cabinet",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
            (pos, state) -> new CabinetBlockEntity((BlockEntityType)CABINET_BLOCK_ENTITY.get(), pos, state), addCabinet(new HashSet<>()).toArray(new Block[0])
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CompletionistBannerEntity>> BREWERY_BANNER = registerBlockEntity(
      "brewery_banner",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
            CompletionistBannerEntity::new, new Block[]{(Block)ObjectRegistry.BREWERY_BANNER.get(), (Block)ObjectRegistry.BREWERY_WALL_BANNER.get()}
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<WallDecorationBlockEntity>> WALL_DECORATION = registerBlockEntity(
      "wall_decoration",
      () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
            (pos, state) -> new WallDecorationBlockEntity((BlockEntityType)WALL_DECORATION.get(), pos, state),
            new Block[]{(Block)ObjectRegistry.GINGERBREAD.get()}
         )
         .build(null)
   );

   public static Set<Block> addCabinet(Set<Block> blocks) {
      blocks.add((Block)ObjectRegistry.CABINET.get());
      blocks.add((Block)ObjectRegistry.DRAWER.get());
      blocks.add((Block)ObjectRegistry.SIDEBOARD.get());
      blocks.add((Block)ObjectRegistry.WALL_CABINET.get());
      return blocks;
   }

   public static void registerBeerElemental(Supplier<? extends EntityType<? extends Monster>> typeSupplier) {
      EntityAttributeRegistry.register(typeSupplier, BeerElementalEntity::createAttributes);
   }

   private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(String path, Supplier<T> type) {
      return BLOCK_ENTITY_TYPES.register(Brewery.identifier(path), type);
   }

   private static <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(String path, Supplier<T> type) {
      return ENTITY_TYPES.register(Brewery.identifier(path), type);
   }

   public static void init() {
      ENTITY_TYPES.register();
      BLOCK_ENTITY_TYPES.register();
      registerBeerElemental(BEER_ELEMENTAL);
   }
}
