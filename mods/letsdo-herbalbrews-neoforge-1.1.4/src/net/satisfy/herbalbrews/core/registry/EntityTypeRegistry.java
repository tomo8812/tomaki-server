package net.satisfy.herbalbrews.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.satisfy.herbalbrews.core.blocks.entity.CauldronBlockEntity;
import net.satisfy.herbalbrews.core.blocks.entity.CompletionistBannerEntity;
import net.satisfy.herbalbrews.core.blocks.entity.DrinkBlockEntity;
import net.satisfy.herbalbrews.core.blocks.entity.JugBlockEntity;
import net.satisfy.herbalbrews.core.blocks.entity.StoveBlockEntity;
import net.satisfy.herbalbrews.core.blocks.entity.TeaKettleBlockEntity;
import net.satisfy.herbalbrews.core.blocks.entity.TeaLeafBlockEntity;
import net.satisfy.herbalbrews.core.util.HerbalBrewsIdentifier;

public class EntityTypeRegistry {
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create("herbalbrews", Registries.BLOCK_ENTITY_TYPE);
   public static final RegistrySupplier<BlockEntityType<TeaKettleBlockEntity>> TEA_KETTLE_BLOCK_ENTITY = registerBlockEntity(
      "tea_kettle",
      () -> Builder.of(TeaKettleBlockEntity::new, new Block[]{(Block)ObjectRegistry.TEA_KETTLE.get(), (Block)ObjectRegistry.COPPER_TEA_KETTLE.get()})
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CauldronBlockEntity>> CAULDRON_BLOCK_ENTITY = registerBlockEntity(
      "cauldron", () -> Builder.of(CauldronBlockEntity::new, new Block[]{(Block)ObjectRegistry.CAULDRON.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<TeaLeafBlockEntity>> TEA_LEAF_BLOCK_ENTITY = registerBlockEntity(
      "tea_leaf",
      () -> Builder.of(
            TeaLeafBlockEntity::new,
            new Block[]{
               (Block)ObjectRegistry.GREEN_TEA_LEAF_BLOCK.get(),
               (Block)ObjectRegistry.MIXED_TEA_LEAF_BLOCK.get(),
               (Block)ObjectRegistry.DRIED_GREEN_TEA_LEAF_BLOCK.get()
            }
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CompletionistBannerEntity>> HERBALBREWS_BANNER = registerBlockEntity(
      "herbalbrews_banner",
      () -> Builder.of(
            CompletionistBannerEntity::new, new Block[]{(Block)ObjectRegistry.HERBALBREWS_BANNER.get(), (Block)ObjectRegistry.HERBALBREWS_WALL_BANNER.get()}
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<StoveBlockEntity>> STOVE_BLOCK_ENTITY = registerBlockEntity(
      "stove_block_entity", () -> Builder.of(StoveBlockEntity::new, new Block[]{(Block)ObjectRegistry.STOVE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<JugBlockEntity>> JUG_BLOCK_ENTITY = registerBlockEntity(
      "jug_block_entity", () -> Builder.of(JugBlockEntity::new, new Block[]{(Block)ObjectRegistry.JUG.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<DrinkBlockEntity>> DRINK_BLOCK_ENTITY = registerBlockEntity(
      "drink_block_entity",
      () -> Builder.of(
            DrinkBlockEntity::new,
            new Block[]{
               (Block)ObjectRegistry.GREEN_TEA_BLOCK.get(),
               (Block)ObjectRegistry.COFFEE_BLOCK.get(),
               (Block)ObjectRegistry.BLACK_TEA_BLOCK.get(),
               (Block)ObjectRegistry.LAVENDER_TEA_BLOCK.get(),
               (Block)ObjectRegistry.YERBA_MATE_TEA_BLOCK.get(),
               (Block)ObjectRegistry.OOLONG_TEA_BLOCK.get(),
               (Block)ObjectRegistry.ROOIBOS_TEA_BLOCK.get(),
               (Block)ObjectRegistry.HIBISCUS_TEA_BLOCK.get(),
               (Block)ObjectRegistry.MILK_COFFEE_BLOCK.get()
            }
         )
         .build(null)
   );

   private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(String name, Supplier<T> type) {
      return BLOCK_ENTITY_TYPES.register(HerbalBrewsIdentifier.identifier(name), type);
   }

   public static void init() {
      BLOCK_ENTITY_TYPES.register();
   }
}
