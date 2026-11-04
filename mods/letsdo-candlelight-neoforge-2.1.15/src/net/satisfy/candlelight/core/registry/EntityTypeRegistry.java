package net.satisfy.candlelight.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.core.block.entity.CandlelightStoveBlockEntity;
import net.satisfy.candlelight.core.block.entity.CookingPanBlockEntity;
import net.satisfy.candlelight.core.block.entity.DinnerBellBlockEntity;
import net.satisfy.candlelight.core.block.entity.LargeCookingPotBlockEntity;
import net.satisfy.candlelight.core.block.entity.SideBoardBlockEntity;
import net.satisfy.candlelight.core.block.entity.SideTableBlockEntity;
import net.satisfy.candlelight.core.block.entity.TableSetBlockEntity;
import net.satisfy.candlelight.core.block.entity.TableSignBlockEntity;
import net.satisfy.candlelight.core.block.entity.TypewriterEntity;
import net.satisfy.foundation.banner.CompletionistBannerEntity;
import net.satisfy.foundation.block.CabinetBlockEntity;
import net.satisfy.foundation.block.WallDecorationBlockEntity;
import net.satisfy.foundation.food.IngredientEffectFoodBlockEntity;
import net.satisfy.foundation.storage.StorageBlockEntity;

public class EntityTypeRegistry {
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create("candlelight", Registries.BLOCK_ENTITY_TYPE);
   private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create("candlelight", Registries.ENTITY_TYPE);
   public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> STORAGE_BLOCK_ENTITY = registerBlockEntity(
      "storage",
      () -> Builder.of(
            (pos, state) -> new StorageBlockEntity((BlockEntityType)STORAGE_BLOCK_ENTITY.get(), pos, state),
            StorageTypeRegistry.registerBlocks(new HashSet<>()).toArray(new Block[0])
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<SideBoardBlockEntity>> SIDEBOARD_BLOCK_ENTITY = registerBlockEntity(
      "sideboard", () -> Builder.of(SideBoardBlockEntity::new, new Block[]{(Block)ObjectRegistry.SIDEBOARD.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<LargeCookingPotBlockEntity>> LARGE_COOKING_POT_BLOCK_ENTITY = registerBlockEntity(
      "large_cooking_pot", () -> Builder.of(LargeCookingPotBlockEntity::new, new Block[]{(Block)ObjectRegistry.COOKING_POT.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<TypewriterEntity>> TYPE_WRITER_BLOCK_ENTITY = registerBlockEntity(
      "type_writer",
      () -> Builder.of(TypewriterEntity::new, new Block[]{(Block)ObjectRegistry.TYPEWRITER_IRON.get(), (Block)ObjectRegistry.TYPEWRITER_GOLD.get()})
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CookingPanBlockEntity>> COOKING_PAN_BLOCK_ENTITY = registerBlockEntity(
      "cooking_pan", () -> Builder.of(CookingPanBlockEntity::new, new Block[]{(Block)ObjectRegistry.COOKING_PAN.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<DinnerBellBlockEntity>> DINNER_BELL_BLOCK_ENTITY = registerBlockEntity(
      "dinner_bell", () -> Builder.of(DinnerBellBlockEntity::new, new Block[]{(Block)ObjectRegistry.DINNER_BELL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CandlelightStoveBlockEntity>> STOVE_BLOCK_ENTITY = registerBlockEntity(
      "stove_block",
      () -> Builder.of(
            CandlelightStoveBlockEntity::new,
            new Block[]{
               (Block)ObjectRegistry.COBBLESTONE_STOVE.get(),
               (Block)ObjectRegistry.MUD_STOVE.get(),
               (Block)ObjectRegistry.GRANITE_STOVE.get(),
               (Block)ObjectRegistry.SANDSTONE_STOVE.get(),
               (Block)ObjectRegistry.STONE_BRICKS_STOVE.get(),
               (Block)ObjectRegistry.RED_NETHER_BRICKS_STOVE.get(),
               (Block)ObjectRegistry.DEEPSLATE_STOVE.get(),
               (Block)ObjectRegistry.QUARTZ_STOVE.get(),
               (Block)ObjectRegistry.END_STOVE.get(),
               (Block)ObjectRegistry.BASALT_STOVE.get(),
               (Block)ObjectRegistry.BAMBOO_STOVE.get()
            }
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
   public static final RegistrySupplier<BlockEntityType<CompletionistBannerEntity>> CANDLELIGHT_BANNER_ENTITY = registerBlockEntity(
      "candlelight_banner_entity",
      () -> Builder.of(
            CompletionistBannerEntity::new, new Block[]{(Block)ObjectRegistry.CANDLELIGHT_BANNER.get(), (Block)ObjectRegistry.CANDLELIGHT_WALL_BANNER.get()}
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> TABLE_SET_BLOCK_ENTITY = registerBlockEntity(
      "table_set", () -> Builder.of(TableSetBlockEntity::new, new Block[]{(Block)ObjectRegistry.TABLE_SET.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<IngredientEffectFoodBlockEntity>> EFFECT_FOOD_BLOCK_ENTITY = registerBlockEntity(
      "effect_food_block",
      () -> Builder.of(
            (pos, state) -> new IngredientEffectFoodBlockEntity((BlockEntityType)EFFECT_FOOD_BLOCK_ENTITY.get(), pos, state),
            new Block[]{
               (Block)ObjectRegistry.LASAGNE_BLOCK.get(),
               (Block)ObjectRegistry.TOMATO_MOZZARELLA_BLOCK.get(),
               (Block)ObjectRegistry.PORK_RIBS_BLOCK.get(),
               (Block)ObjectRegistry.FRESH_GARDEN_SALAD_BLOCK.get(),
               (Block)ObjectRegistry.BEEF_WELLINGTON_BLOCK.get()
            }
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<SideTableBlockEntity>> SIDE_TABLE_BLOCK_ENTITY = registerBlockEntity(
      "side_table", () -> Builder.of(SideTableBlockEntity::new, new Block[]{(Block)ObjectRegistry.SIDE_TABLE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<TableSignBlockEntity>> TABLE_SIGN_BLOCK_ENTITY = registerBlockEntity(
      "table_sign", () -> Builder.of(TableSignBlockEntity::new, new Block[]{(Block)ObjectRegistry.TABLE_SIGN.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<WallDecorationBlockEntity>> WALL_DECORATION = registerBlockEntity(
      "wall_decoration",
      () -> Builder.of(
            (pos, state) -> new WallDecorationBlockEntity((BlockEntityType)WALL_DECORATION.get(), pos, state), new Block[]{(Block)ObjectRegistry.HEART.get()}
         )
         .build(null)
   );

   private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(String path, Supplier<T> type) {
      return BLOCK_ENTITY_TYPES.register(Candlelight.identifier(path), type);
   }

   public static void init() {
      ENTITY_TYPES.register();
      BLOCK_ENTITY_TYPES.register();
   }

   private static Set<Block> addCabinet(Set<Block> blocks) {
      blocks.add((Block)ObjectRegistry.DRAWER.get());
      blocks.add((Block)ObjectRegistry.OAK_DRAWER.get());
      blocks.add((Block)ObjectRegistry.BIRCH_DRAWER.get());
      blocks.add((Block)ObjectRegistry.SPRUCE_DRAWER.get());
      blocks.add((Block)ObjectRegistry.DARK_OAK_DRAWER.get());
      blocks.add((Block)ObjectRegistry.ACACIA_DRAWER.get());
      blocks.add((Block)ObjectRegistry.JUNGLE_DRAWER.get());
      blocks.add((Block)ObjectRegistry.MANGROVE_DRAWER.get());
      blocks.add((Block)ObjectRegistry.WARPED_DRAWER.get());
      blocks.add((Block)ObjectRegistry.CRIMSON_DRAWER.get());
      blocks.add((Block)ObjectRegistry.CHERRY_DRAWER.get());
      blocks.add((Block)ObjectRegistry.BAMBOO_DRAWER.get());
      blocks.add((Block)ObjectRegistry.CABINET.get());
      blocks.add((Block)ObjectRegistry.OAK_CABINET.get());
      blocks.add((Block)ObjectRegistry.BIRCH_CABINET.get());
      blocks.add((Block)ObjectRegistry.SPRUCE_CABINET.get());
      blocks.add((Block)ObjectRegistry.DARK_OAK_CABINET.get());
      blocks.add((Block)ObjectRegistry.ACACIA_CABINET.get());
      blocks.add((Block)ObjectRegistry.JUNGLE_CABINET.get());
      blocks.add((Block)ObjectRegistry.MANGROVE_CABINET.get());
      blocks.add((Block)ObjectRegistry.WARPED_CABINET.get());
      blocks.add((Block)ObjectRegistry.CRIMSON_CABINET.get());
      blocks.add((Block)ObjectRegistry.CHERRY_CABINET.get());
      blocks.add((Block)ObjectRegistry.BAMBOO_CABINET.get());
      return blocks;
   }
}
