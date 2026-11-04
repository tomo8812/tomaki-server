package net.satisfy.herbalbrews.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.Foods;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.satisfy.herbalbrews.core.blocks.BonemealableFlowerBlock;
import net.satisfy.herbalbrews.core.blocks.CauldronBlock;
import net.satisfy.herbalbrews.core.blocks.CoffeeCropBlock;
import net.satisfy.herbalbrews.core.blocks.CompletionistBannerBlock;
import net.satisfy.herbalbrews.core.blocks.CompletionistWallBannerBlock;
import net.satisfy.herbalbrews.core.blocks.JugBlock;
import net.satisfy.herbalbrews.core.blocks.RooibosCropBlock;
import net.satisfy.herbalbrews.core.blocks.StoveBlock;
import net.satisfy.herbalbrews.core.blocks.TeaCropBlock;
import net.satisfy.herbalbrews.core.blocks.TeaCupBlock;
import net.satisfy.herbalbrews.core.blocks.TeaKettleBlock;
import net.satisfy.herbalbrews.core.blocks.TeaLeafBlock;
import net.satisfy.herbalbrews.core.blocks.YerbaMateCropBlock;
import net.satisfy.herbalbrews.core.items.DrinkBlockItem;
import net.satisfy.herbalbrews.core.items.FlaskItem;
import net.satisfy.herbalbrews.core.items.HatItem;
import net.satisfy.herbalbrews.core.items.JugItem;
import net.satisfy.herbalbrews.core.util.HerbalBrewsIdentifier;
import net.satisfy.herbalbrews.core.util.HerbalBrewsUtil;

public class ObjectRegistry {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create("herbalbrews", Registries.ITEM);
   public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create("herbalbrews", Registries.BLOCK);
   public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();
   public static final RegistrySupplier<Block> STOVE = registerWithItem(
      "stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)
            .lightLevel(state -> state.getValue(StoveBlock.LIT) ? 13 : 0)
            .randomTicks()
      )
   );
   public static final RegistrySupplier<Block> TEA_LEAF_CRATE = registerWithItem(
      "tea_leaf_crate", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL))
   );
   public static final RegistrySupplier<Block> GREEN_TEA_LEAF_BLOCK = registerWithItem(
      "green_tea_leaf_block", () -> new TeaLeafBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LEAVES))
   );
   public static final RegistrySupplier<Block> DRIED_GREEN_TEA_LEAF_BLOCK = registerWithItem(
      "dried_green_tea_leaf_block", () -> new TeaLeafBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LEAVES))
   );
   public static final RegistrySupplier<Block> DRIED_OUT_GREEN_TEA_LEAF_BLOCK = registerWithItem(
      "dried_out_green_tea_leaf_block", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LEAVES))
   );
   public static final RegistrySupplier<Block> BLACK_TEA_LEAF_BLOCK = registerWithItem(
      "black_tea_leaf_block", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LEAVES))
   );
   public static final RegistrySupplier<Block> MIXED_TEA_LEAF_BLOCK = registerWithItem(
      "mixed_tea_leaf_block", () -> new TeaLeafBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LEAVES))
   );
   public static final RegistrySupplier<Block> OOLONG_TEA_LEAF_BLOCK = registerWithItem(
      "oolong_tea_leaf_block", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LEAVES))
   );
   public static final RegistrySupplier<Block> WILD_COFFEE_PLANT = registerWithItem(
      "wild_coffee_plant",
      () -> new FlowerBlock(MobEffects.HEAL, 1.0F, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION))
   );
   public static final RegistrySupplier<Block> COFFEE_PLANT = registerWithoutItem(
      "coffee_plant", () -> new CoffeeCropBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH))
   );
   public static final RegistrySupplier<Block> WILD_ROOIBOS_PLANT = registerWithItem(
      "wild_rooibos_plant",
      () -> new FlowerBlock(MobEffects.HEAL, 1.0F, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION))
   );
   public static final RegistrySupplier<Block> ROOIBOS_PLANT = registerWithoutItem(
      "rooibos_plant", () -> new RooibosCropBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH))
   );
   public static final RegistrySupplier<Block> WILD_YERBA_MATE_PLANT = registerWithItem(
      "wild_yerba_mate_plant",
      () -> new FlowerBlock(MobEffects.HEAL, 1.0F, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION))
   );
   public static final RegistrySupplier<Block> YERBA_MATE_PLANT = registerWithoutItem(
      "yerba_mate_plant", () -> new YerbaMateCropBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH))
   );
   public static final RegistrySupplier<Block> TEA_PLANT = registerWithoutItem(
      "tea_plant", () -> new TeaCropBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH))
   );
   public static final RegistrySupplier<Block> HIBISCUS = registerWithItem(
      "hibiscus",
      () -> new BonemealableFlowerBlock(MobEffects.HEAL, 1.0F, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION))
   );
   public static final RegistrySupplier<Block> LAVENDER = registerWithItem(
      "lavender",
      () -> new BonemealableFlowerBlock(MobEffects.HEAL, 1.0F, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION))
   );
   public static final RegistrySupplier<Block> JUG = registerWithoutItem(
      "jug", () -> new JugBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS))
   );
   public static final RegistrySupplier<Item> JUG_ITEM = registerItem("jug", () -> new JugItem((Block)JUG.get(), getSettings()));
   public static final RegistrySupplier<Block> COPPER_TEA_KETTLE = registerWithItem(
      "copper_tea_kettle", () -> new TeaKettleBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK))
   );
   public static final RegistrySupplier<Block> TEA_KETTLE = registerWithItem(
      "tea_kettle", () -> new TeaKettleBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK))
   );
   public static final RegistrySupplier<Block> CAULDRON = registerWithItem(
      "cauldron",
      () -> new CauldronBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).lightLevel(blockState -> 11))
   );
   public static final RegistrySupplier<Item> FLASK = registerItem("flask", () -> new FlaskItem(getSettings()));
   public static final RegistrySupplier<Item> TEA_BLOSSOM = registerItem(
      "tea_blossom",
      () -> new BlockItem((Block)TEA_PLANT.get(), getSettings().food(new Builder().nutrition(1).saturationModifier(0.1F).fast().alwaysEdible().build()))
   );
   public static final RegistrySupplier<Item> GREEN_TEA_LEAF = registerItem("green_tea_leaf", () -> new Item(getSettings()));
   public static final RegistrySupplier<Item> YERBA_MATE_LEAF = registerItem(
      "yerba_mate_leaf",
      () -> new BlockItem((Block)YERBA_MATE_PLANT.get(), getSettings().food(new Builder().nutrition(1).saturationModifier(0.1F).fast().alwaysEdible().build()))
   );
   public static final RegistrySupplier<Item> ROOIBOS_LEAF = registerItem(
      "rooibos_leaf",
      () -> new BlockItem((Block)ROOIBOS_PLANT.get(), getSettings().food(new Builder().nutrition(1).saturationModifier(0.1F).fast().alwaysEdible().build()))
   );
   public static final RegistrySupplier<Item> LAVENDER_BLOSSOM = registerItem("lavender_blossom", () -> new Item(getSettings()));
   public static final RegistrySupplier<Item> DRIED_GREEN_TEA = registerItem(
      "dried_green_tea", () -> new Item(getSettings().food(new Builder().nutrition(1).saturationModifier(0.1F).fast().alwaysEdible().build()))
   );
   public static final RegistrySupplier<Item> DRIED_BLACK_TEA = registerItem(
      "dried_black_tea", () -> new Item(getSettings().food(new Builder().nutrition(1).saturationModifier(0.1F).fast().alwaysEdible().build()))
   );
   public static final RegistrySupplier<Item> DRIED_OOLONG_TEA = registerItem(
      "dried_oolong_tea", () -> new Item(getSettings().food(new Builder().nutrition(1).saturationModifier(0.1F).fast().alwaysEdible().build()))
   );
   public static final RegistrySupplier<Item> COFFEE_BEANS = registerItem(
      "coffee_beans",
      () -> new BlockItem((Block)COFFEE_PLANT.get(), getSettings().food(new Builder().nutrition(1).saturationModifier(0.1F).fast().alwaysEdible().build()))
   );
   public static final RegistrySupplier<Block> GREEN_TEA_BLOCK = registerWithoutItem("green_tea_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Block> BLACK_TEA_BLOCK = registerWithoutItem("black_tea_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Block> LAVENDER_TEA_BLOCK = registerWithoutItem("lavender_tea_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Block> YERBA_MATE_TEA_BLOCK = registerWithoutItem("yerba_mate_tea_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Block> OOLONG_TEA_BLOCK = registerWithoutItem("oolong_tea_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Block> ROOIBOS_TEA_BLOCK = registerWithoutItem("rooibos_tea_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Block> HIBISCUS_TEA_BLOCK = registerWithoutItem("hibiscus_tea_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Block> MILK_COFFEE_BLOCK = registerWithoutItem("milk_coffee_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Block> COFFEE_BLOCK = registerWithoutItem("coffee_block", () -> new TeaCupBlock(getTeaSettings()));
   public static final RegistrySupplier<Item> GREEN_TEA = registerItem(
      "green_tea",
      () -> new DrinkBlockItem(
         (Block)GREEN_TEA_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> BLACK_TEA = registerItem(
      "black_tea",
      () -> new DrinkBlockItem(
         (Block)BLACK_TEA_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> LAVENDER_TEA = registerItem(
      "lavender_tea",
      () -> new DrinkBlockItem(
         (Block)LAVENDER_TEA_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> YERBA_MATE_TEA = registerItem(
      "yerba_mate_tea",
      () -> new DrinkBlockItem(
         (Block)YERBA_MATE_TEA_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> OOLONG_TEA = registerItem(
      "oolong_tea",
      () -> new DrinkBlockItem(
         (Block)OOLONG_TEA_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> ROOIBOS_TEA = registerItem(
      "rooibos_tea",
      () -> new DrinkBlockItem(
         (Block)ROOIBOS_TEA_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> HIBISCUS_TEA = registerItem(
      "hibiscus_tea",
      () -> new DrinkBlockItem(
         (Block)HIBISCUS_TEA_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> MILK_COFFEE = registerItem(
      "milk_coffee",
      () -> new DrinkBlockItem(
         (Block)MILK_COFFEE_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> COFFEE = registerItem(
      "coffee",
      () -> new DrinkBlockItem(
         (Block)COFFEE_BLOCK.get(),
         getSettings()
            .food(new Builder().nutrition(Foods.SWEET_BERRIES.nutrition()).saturationModifier(Foods.SWEET_BERRIES.saturation()).alwaysEdible().build())
      )
   );
   public static final RegistrySupplier<Item> WITCH_HAT = registerItem(
      "witch_hat",
      () -> new HatItem(
         ArmorMaterials.LEATHER, Type.HELMET, new Properties().rarity(Rarity.EPIC), HerbalBrewsIdentifier.identifier("textures/models/armor/witch_hat.png")
      )
   );
   public static final RegistrySupplier<Item> TOP_HAT = registerItem(
      "top_hat",
      () -> new HatItem(
         ArmorMaterials.LEATHER, Type.HELMET, new Properties().rarity(Rarity.EPIC), HerbalBrewsIdentifier.identifier("textures/models/armor/top_hat.png")
      )
   );
   public static final RegistrySupplier<Block> HERBALBREWS_BANNER = registerWithItem(
      "herbalbrews_banner",
      () -> new CompletionistBannerBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .strength(1.0F)
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> HERBALBREWS_WALL_BANNER = registerWithoutItem(
      "herbalbrews_wall_banner",
      () -> new CompletionistWallBannerBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .strength(1.0F)
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> POTTED_LAVENDER = registerWithoutItem(
      "potted_lavender",
      () -> new FlowerPotBlock((Block)LAVENDER.get(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT))
   );
   public static final RegistrySupplier<Block> POTTED_HIBISCUS = registerWithoutItem(
      "potted_hibiscus",
      () -> new FlowerPotBlock((Block)HIBISCUS.get(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT))
   );
   public static final RegistrySupplier<Block> POTTED_WILD_ROOIBOS = registerWithoutItem(
      "potted_wild_rooibos",
      () -> new FlowerPotBlock((Block)WILD_ROOIBOS_PLANT.get(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT))
   );
   public static final RegistrySupplier<Block> POTTED_WILD_COFFEE = registerWithoutItem(
      "potted_wild_coffee",
      () -> new FlowerPotBlock((Block)WILD_COFFEE_PLANT.get(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT))
   );
   public static final RegistrySupplier<Block> POTTED_WILD_YERBA_MATE = registerWithoutItem(
      "potted_wild_yerba_mate",
      () -> new FlowerPotBlock(
         (Block)WILD_YERBA_MATE_PLANT.get(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)
      )
   );
   public static final RegistrySupplier<Item> HERBAL_INFUSION = registerItem("herbal_infusion", () -> new Item(getSettings()));

   public static void init() {
      ITEMS.register();
      BLOCKS.register();
   }

   private static Properties getSettings(Consumer<Properties> consumer) {
      Properties settings = new Properties();
      consumer.accept(settings);
      return settings;
   }

   static Properties getSettings() {
      return getSettings(settings -> {});
   }

   public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
      return HerbalBrewsUtil.registerWithItem(BLOCKS, BLOCK_REGISTRAR, ITEMS, ITEM_REGISTRAR, HerbalBrewsIdentifier.identifier(name), block);
   }

   public static <T extends Block> RegistrySupplier<T> registerWithoutItem(String path, Supplier<T> block) {
      return HerbalBrewsUtil.registerWithoutItem(BLOCKS, BLOCK_REGISTRAR, HerbalBrewsIdentifier.identifier(path), block);
   }

   public static <T extends Item> RegistrySupplier<T> registerItem(String path, Supplier<T> itemSupplier) {
      return HerbalBrewsUtil.registerItem(ITEMS, ITEM_REGISTRAR, HerbalBrewsIdentifier.identifier(path), itemSupplier);
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties getTeaSettings() {
      return net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion().instabreak();
   }
}
