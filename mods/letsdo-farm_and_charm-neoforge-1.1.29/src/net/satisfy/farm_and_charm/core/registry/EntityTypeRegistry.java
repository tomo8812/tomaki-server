package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.HashSet;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.CookingPotBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.CraftingBowlBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.CuttingBoardBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.FeedingTroughBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.MincerBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.RoasterBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.ScarecrowBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.SiloBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.StoveBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.SturdyLadderBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.TimberWellBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.WaterSprinklerBlockEntity;
import net.satisfy.farm_and_charm.core.entity.PlowCartEntity;
import net.satisfy.farm_and_charm.core.entity.RottenTomatoEntity;
import net.satisfy.farm_and_charm.core.entity.SeederCartEntity;
import net.satisfy.farm_and_charm.core.entity.SupplyCartEntity;
import net.satisfy.foundation.block.mimic.MimicBlockEntity;
import net.satisfy.foundation.storage.StorageBlockEntity;

public class EntityTypeRegistry {
   private static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create("farm_and_charm", Registries.BLOCK_ENTITY_TYPE)
      .getRegistrar();
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create("farm_and_charm", Registries.ENTITY_TYPE);
   public static final RegistrySupplier<BlockEntityType<TimberWellBlockEntity>> TIMBER_WELL_BLOCK_ENTITY = registerBlockEntity(
      "timber_well", () -> Builder.of(TimberWellBlockEntity::new, new Block[]{(Block)ObjectRegistry.TIMBER_WELL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> STORAGE_ENTITY = registerBlockEntity(
      "storage",
      () -> Builder.of(
            (pos, state) -> new StorageBlockEntity((BlockEntityType)STORAGE_ENTITY.get(), pos, state),
            StorageTypeRegistry.registerBlocks(new HashSet<>()).toArray(new Block[0])
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<RoasterBlockEntity>> ROASTER_BLOCK_ENTITY = registerBlockEntity(
      "roaster", () -> Builder.of(RoasterBlockEntity::new, new Block[]{(Block)ObjectRegistry.ROASTER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CraftingBowlBlockEntity>> CRAFTING_BOWL_BLOCK_ENTITY = registerBlockEntity(
      "crafting_bowl", () -> Builder.of(CraftingBowlBlockEntity::new, new Block[]{(Block)ObjectRegistry.CRAFTING_BOWL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CookingPotBlockEntity>> COOKING_POT_BLOCK_ENTITY = registerBlockEntity(
      "cooking_pot", () -> Builder.of(CookingPotBlockEntity::new, new Block[]{(Block)ObjectRegistry.COOKING_POT.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<StoveBlockEntity>> STOVE_BLOCK_ENTITY = registerBlockEntity(
      "stove_block", () -> Builder.of(StoveBlockEntity::new, new Block[]{(Block)ObjectRegistry.STOVE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<WaterSprinklerBlockEntity>> SPRINKLER_BLOCK_ENTITY = registerBlockEntity(
      "water_sprinkler", () -> Builder.of(WaterSprinklerBlockEntity::new, new Block[]{(Block)ObjectRegistry.WATER_SPRINKLER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<SiloBlockEntity>> SILO_BLOCK_ENTITY = registerBlockEntity(
      "silo", () -> Builder.of(SiloBlockEntity::new, new Block[]{(Block)ObjectRegistry.SILO_WOOD.get(), (Block)ObjectRegistry.SILO_COPPER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<FeedingTroughBlockEntity>> FEEDING_TROUGH_BLOCK_ENTITY = registerBlockEntity(
      "feeding_trough", () -> Builder.of(FeedingTroughBlockEntity::new, new Block[]{(Block)ObjectRegistry.FEEDING_TROUGH.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<SturdyLadderBlockEntity>> STURDY_LADDER_BLOCK_ENTITY = registerBlockEntity(
      "sturdy_ladder", () -> Builder.of(SturdyLadderBlockEntity::new, new Block[]{(Block)ObjectRegistry.STURDY_LADDER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<ScarecrowBlockEntity>> SCARECROW_BLOCK_ENTITY = registerBlockEntity(
      "scarecrow", () -> Builder.of(ScarecrowBlockEntity::new, new Block[]{(Block)ObjectRegistry.SCARECROW.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CuttingBoardBlockEntity>> CUTTING_BOARD_BLOCK_ENTITY = registerBlockEntity(
      "cutting_board", () -> Builder.of(CuttingBoardBlockEntity::new, new Block[]{(Block)ObjectRegistry.CUTTING_BOARD.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<MincerBlockEntity>> MINCER_BLOCK_ENTITY = registerBlockEntity(
      "mincer", () -> Builder.of(MincerBlockEntity::new, new Block[]{(Block)ObjectRegistry.MINCER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<PetBowlBlockEntity>> PET_BOWL_BLOCK_ENTITY = registerBlockEntity(
      "pet_bowl", () -> Builder.of(PetBowlBlockEntity::new, new Block[]{(Block)ObjectRegistry.PET_BOWL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<ChickenCoopBlockEntity>> CHICKEN_COOP_BLOCK_ENTITY = registerBlockEntity(
      "chicken_coop", () -> Builder.of(ChickenCoopBlockEntity::new, new Block[]{(Block)ObjectRegistry.CHICKEN_COOP.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<MimicBlockEntity>> ROPE_KNOT_BLOCK_ENTITY = registerBlockEntity(
      "rope_knot",
      () -> Builder.of(
            (pos, state) -> new MimicBlockEntity((BlockEntityType)ROPE_KNOT_BLOCK_ENTITY.get(), pos, state), new Block[]{(Block)ObjectRegistry.ROPE_KNOT.get()}
         )
         .build(null)
   );
   public static final RegistrySupplier<EntityType<RottenTomatoEntity>> ROTTEN_TOMATO = registerEntityType(
      "rotten_tomato",
      () -> net.minecraft.world.entity.EntityType.Builder.of(RottenTomatoEntity::new, MobCategory.MISC)
         .sized(0.25F, 0.25F)
         .build(FarmAndCharm.identifier("rotten_tomato").toString())
   );
   public static final RegistrySupplier<EntityType<SupplyCartEntity>> SUPPLY_CART = registerEntityType(
      "cart",
      () -> net.minecraft.world.entity.EntityType.Builder.of(SupplyCartEntity::new, MobCategory.MISC)
         .sized(1.875F, 0.875F)
         .clientTrackingRange(10)
         .build(FarmAndCharm.identifier("supply_cart").toString())
   );
   public static final RegistrySupplier<EntityType<PlowCartEntity>> PLOW = registerEntityType(
      "plow",
      () -> net.minecraft.world.entity.EntityType.Builder.of(PlowCartEntity::new, MobCategory.MISC)
         .sized(1.875F, 0.875F)
         .clientTrackingRange(10)
         .build(FarmAndCharm.identifier("plow").toString())
   );
   public static final RegistrySupplier<EntityType<SeederCartEntity>> SEEDER = registerEntityType(
      "seeder",
      () -> net.minecraft.world.entity.EntityType.Builder.of(SeederCartEntity::new, MobCategory.MISC)
         .sized(1.875F, 0.875F)
         .clientTrackingRange(10)
         .build(FarmAndCharm.identifier("seeder").toString())
   );

   private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(String path, Supplier<T> type) {
      return BLOCK_ENTITY_TYPES.register(FarmAndCharm.identifier(path), type);
   }

   private static <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(String path, Supplier<T> type) {
      return ENTITY_TYPES.register(FarmAndCharm.identifier(path), type);
   }

   public static void init() {
      ENTITY_TYPES.register();
   }
}
