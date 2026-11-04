package net.satisfy.candlelight.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.core.block.BambooStoveBlock;
import net.satisfy.candlelight.core.block.CookingPanBlock;
import net.satisfy.candlelight.core.block.DinnerBellBlock;
import net.satisfy.candlelight.core.block.EffectFoodBlock;
import net.satisfy.candlelight.core.block.EffectFoodTrayBlock;
import net.satisfy.candlelight.core.block.JewelryBoxBlock;
import net.satisfy.candlelight.core.block.LargeCookingPotBlock;
import net.satisfy.candlelight.core.block.SideBoardBlock;
import net.satisfy.candlelight.core.block.SideTableBlock;
import net.satisfy.candlelight.core.block.SmallPaintingBlock;
import net.satisfy.candlelight.core.block.SofaBlock;
import net.satisfy.candlelight.core.block.StoveBlock;
import net.satisfy.candlelight.core.block.TableSetBlock;
import net.satisfy.candlelight.core.block.TableSignBlock;
import net.satisfy.candlelight.core.block.TypewriterBlock;
import net.satisfy.candlelight.core.item.ClosedLetterItem;
import net.satisfy.candlelight.core.item.CookingPanItem;
import net.satisfy.candlelight.core.item.LetterItem;
import net.satisfy.candlelight.core.item.NapkinItem;
import net.satisfy.candlelight.core.item.RingItem;
import net.satisfy.candlelight.core.item.TableSetBlockItem;
import net.satisfy.candlelight.core.item.TableSignItem;
import net.satisfy.candlelight.core.item.WineGlassItem;
import net.satisfy.candlelight.core.item.WriteablePaperItem;
import net.satisfy.candlelight.core.item.WrittenPaperItem;
import net.satisfy.candlelight.core.util.CandlelightFoods;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.farm_and_charm.core.registry.ArmorMaterialRegistry;
import net.satisfy.foundation.armor.DyeableArmorItem;
import net.satisfy.foundation.armor.TexturedArmorItem;
import net.satisfy.foundation.banner.BannerSettings;
import net.satisfy.foundation.banner.CompletionistBannerBlock;
import net.satisfy.foundation.banner.CompletionistWallBannerBlock;
import net.satisfy.foundation.block.BigTableBlock;
import net.satisfy.foundation.block.BonemealableFlowerBlock;
import net.satisfy.foundation.block.CabinetBlock;
import net.satisfy.foundation.block.ChairBlock;
import net.satisfy.foundation.block.ClothColor;
import net.satisfy.foundation.block.EatableBoxBlock;
import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.foundation.block.LampBlock;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.satisfy.foundation.block.SinkBlock;
import net.satisfy.foundation.block.StackableBlock;
import net.satisfy.foundation.block.TableBlock;
import net.satisfy.foundation.block.WallDecorationBlock;
import net.satisfy.foundation.food.EffectFoodItem;
import net.satisfy.foundation.food.IngredientEffectFoodItem;
import net.satisfy.foundation.food.PlaceableEffectFoodItem;
import net.satisfy.foundation.food.PlaceableIngredientEffectFoodItem;
import net.satisfy.foundation.storage.WallShelfBlock;
import net.satisfy.foundation.util.RegistryUtil;

public class ObjectRegistry {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create("candlelight", Registries.ITEM);
   public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create("candlelight", Registries.BLOCK);
   public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();
   public static final RegistrySupplier<Item> TOMATO_SOUP = registerItem(
      "tomato_soup", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.TOMATO_SOUP), 1)
   );
   public static final RegistrySupplier<Item> MUSHROOM_SOUP = registerItem(
      "mushroom_soup", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.MUSHROOM_SOUP), 1)
   );
   public static final RegistrySupplier<Item> PASTA_WITH_MOZZARELLA = registerItem(
      "pasta_with_mozzarella", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.PASTA_WITH_MOZZARELLA), 2)
   );
   public static final RegistrySupplier<Item> BOLOGNESE = registerItem(
      "bolognese", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.BOLOGNESE), 1)
   );
   public static final RegistrySupplier<Item> BEEF_WITH_MUSHROOM_IN_WINE_AND_POTATOES = registerItem(
      "beef_with_mushroom_in_wine_and_potatoes",
      () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.BEEF_WITH_MUSHROOM_IN_WINE_AND_POTATOES), 2)
   );
   public static final RegistrySupplier<Item> PASTA_WITH_BOLOGNESE = registerItem(
      "pasta_with_bolognese", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.PASTA_WITH_BOLOGNESE), 2)
   );
   public static final RegistrySupplier<Item> ROASTBEEF_WITH_GLAZED_CARROTS = registerItem(
      "roastbeef_with_glazed_carrots", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.ROASTBEEF_WITH_GLAZED_CARROTS), 2)
   );
   public static final RegistrySupplier<Item> ROASTED_LAMB_WITH_LETTUCE = registerItem(
      "roasted_lamb_with_lettuce", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.ROASTED_LAMB_WITH_LETTUCE), 2)
   );
   public static final RegistrySupplier<Item> FILLET_STEAK = registerItem(
      "fillet_steak", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.FILLET_STEAK), 2)
   );
   public static final RegistrySupplier<Item> TROPICAL_FISH_SUPREME = registerItem(
      "tropical_fish_supreme", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.TROPICAL_FISH_SUPREME), 1)
   );
   public static final RegistrySupplier<Item> CHICKEN_ALFREDO = registerItem(
      "chicken_alfredo", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.CHICKEN_ALFREDO), 2)
   );
   public static final RegistrySupplier<Item> SALMON_ON_WHITE_WINE = registerItem(
      "salmon_on_white_wine", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.SALMON_ON_WHITE_WINE), 2)
   );
   public static final RegistrySupplier<Item> CHICKEN_WITH_VEGETABLES = registerItem(
      "chicken_with_vegetables", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.CHICKEN_WITH_VEGETABLES), 2)
   );
   public static final RegistrySupplier<Block> FRESH_GARDEN_SALAD_BLOCK = registerWithoutItem(
      "fresh_garden_salad_block",
      () -> new EffectFoodTrayBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE),
         4,
         new Builder().nutrition(PlatformHelper.freshGardenSaladNutrition()).saturationModifier((float)PlatformHelper.freshGardenSaladSaturation()).build()
      )
   );
   public static final RegistrySupplier<Block> LASAGNE_BLOCK = registerWithoutItem(
      "lasagne_block",
      () -> new EffectFoodBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE), 3, CandlelightFoods.LASAGNE)
   );
   public static final RegistrySupplier<Block> BEEF_WELLINGTON_BLOCK = registerWithoutItem(
      "beef_wellington_block",
      () -> new EffectFoodBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE), 2, CandlelightFoods.BEEF_WELLINGTON)
   );
   public static final RegistrySupplier<Block> PORK_RIBS_BLOCK = registerWithoutItem(
      "pork_ribs_block",
      () -> new EffectFoodBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE), 2, CandlelightFoods.PORK_RIBS)
   );
   public static final RegistrySupplier<Block> TOMATO_MOZZARELLA_BLOCK = registerWithoutItem(
      "tomato_mozzarella_block",
      () -> new EffectFoodTrayBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE),
         4,
         new Builder()
            .nutrition(PlatformHelper.tomatoMozzarellaSaladNutrition())
            .saturationModifier((float)PlatformHelper.tomatoMozzarellaSaladSaturation())
            .build()
      )
   );
   public static final RegistrySupplier<Item> PORK_RIBS = registerItem(
      "pork_ribs", () -> new PlaceableIngredientEffectFoodItem((Block)PORK_RIBS_BLOCK.get(), getSettings().food(CandlelightFoods.PORK_RIBS), 2)
   );
   public static final RegistrySupplier<Item> LASAGNE = registerItem(
      "lasagne", () -> new PlaceableIngredientEffectFoodItem((Block)LASAGNE_BLOCK.get(), getSettings().food(CandlelightFoods.LASAGNE), 3)
   );
   public static final RegistrySupplier<Item> BEEF_WELLINGTON = registerItem(
      "beef_wellington",
      () -> new PlaceableIngredientEffectFoodItem((Block)BEEF_WELLINGTON_BLOCK.get(), getSettings().food(CandlelightFoods.BEEF_WELLINGTON), 2)
   );
   public static final RegistrySupplier<Item> CLOCHE = registerItem("cloche", () -> new Item(getSettings()));
   public static final RegistrySupplier<Item> NAPKIN = registerItem("napkin", () -> new NapkinItem(getSettings()));
   public static final RegistrySupplier<Item> MOZZARELLA = registerItem("mozzarella", () -> new Item(getSettings().food(CandlelightFoods.MOZZARELLA)));
   public static final RegistrySupplier<Item> POTROAST = registerItem(
      "potroast", () -> new IngredientEffectFoodItem(getSettings().food(CandlelightFoods.POTROAST), 2)
   );
   public static final RegistrySupplier<Item> BEETROOT_SALAD = registerItem(
      "beetroot_salad",
      () -> new EffectFoodItem(
         getFoodItemSettings(
            PlatformHelper.beetrootSaladNutrition(),
            (float)PlatformHelper.beetrootSaladSaturation(),
            (MobEffect)MobEffectRegistry.REFRESHED.get(),
            PlatformHelper.beetrootSaladEffectDuration()
         ),
         PlatformHelper.beetrootSaladEffectDuration(),
         true
      )
   );
   public static final RegistrySupplier<Item> CHICKEN_TERIYAKI = registerItem(
      "chicken_teriyaki",
      () -> new EffectFoodItem(
         getFoodItemSettings(
            PlatformHelper.chickenTeriyakiNutrition(),
            (float)PlatformHelper.chickenTeriyakiSaturation(),
            (MobEffect)MobEffectRegistry.WELL_SERVED.get(),
            PlatformHelper.chickenTeriyakiEffectDuration()
         ),
         PlatformHelper.chickenTeriyakiEffectDuration(),
         true
      )
   );
   public static final RegistrySupplier<Item> SALAD = registerItem(
      "salad",
      () -> new EffectFoodItem(
         getFoodItemSettings(
            PlatformHelper.saladNutrition(),
            (float)PlatformHelper.saladSaturation(),
            (MobEffect)MobEffectRegistry.REFRESHED.get(),
            PlatformHelper.saladEffectDuration()
         ),
         PlatformHelper.saladEffectDuration(),
         true
      )
   );
   public static final RegistrySupplier<Item> BEEF_TARTARE = registerItem(
      "beef_tartare",
      () -> new EffectFoodItem(
         getFoodItemSettings(
            PlatformHelper.beefTartareNutrition(),
            (float)PlatformHelper.beefTartareSaturation(),
            (MobEffect)MobEffectRegistry.WELL_SERVED.get(),
            PlatformHelper.beefTartareEffectDuration()
         ),
         PlatformHelper.beefTartareEffectDuration(),
         true
      )
   );
   public static final RegistrySupplier<Item> PASTA_WITH_LETTUCE = registerItem(
      "pasta_with_lettuce",
      () -> new EffectFoodItem(
         getFoodItemSettings(
            PlatformHelper.pastaWithLettuceNutrition(),
            (float)PlatformHelper.pastaWithLettuceSaturation(),
            (MobEffect)MobEffectRegistry.REFRESHED.get(),
            PlatformHelper.pastaWithLettuceEffectDuration()
         ),
         PlatformHelper.pastaWithLettuceEffectDuration(),
         true
      )
   );
   public static final RegistrySupplier<Item> OMELET = registerItem(
      "omelet",
      () -> new EffectFoodItem(
         getFoodItemSettings(
            PlatformHelper.omeletNutrition(),
            (float)PlatformHelper.omeletSaturation(),
            (MobEffect)MobEffectRegistry.WELL_SERVED.get(),
            PlatformHelper.omeletEffectDuration()
         ),
         PlatformHelper.omeletEffectDuration(),
         true
      )
   );
   public static final RegistrySupplier<Item> HARVEST_PLATE = registerItem(
      "harvest_plate",
      () -> new EffectFoodItem(
         getFoodItemSettings(
            PlatformHelper.harvestPlateNutrition(),
            (float)PlatformHelper.harvestPlateSaturation(),
            (MobEffect)MobEffectRegistry.REFRESHED.get(),
            PlatformHelper.harvestPlateEffectDuration()
         ),
         PlatformHelper.harvestPlateEffectDuration(),
         true
      )
   );
   public static final RegistrySupplier<Item> CHOCOLATE_MOUSSE = registerItem(
      "chocolate_mousse",
      () -> new EffectFoodItem(
         getFoodItemSettings(
            PlatformHelper.chocolateMousseNutrition(),
            (float)PlatformHelper.chocolateMousseSaturation(),
            (MobEffect)MobEffectRegistry.WELL_SERVED.get(),
            PlatformHelper.chocolateMousseEffectDuration()
         ),
         PlatformHelper.chocolateMousseEffectDuration(),
         true
      )
   );
   public static final RegistrySupplier<Item> GOLD_RING = registerItem("gold_ring", () -> new RingItem(getSettings().rarity(Rarity.EPIC).stacksTo(1)));
   public static final RegistrySupplier<Item> COOKING_HAT = registerItem(
      "cooking_hat",
      () -> new TexturedArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/cooking_hat.png")),
         Type.HELMET,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/cooking_hat.png")
      )
   );
   public static final RegistrySupplier<Item> CHEFS_JACKET = registerItem(
      "chefs_jacket",
      () -> new TexturedArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/cook.png")),
         Type.CHESTPLATE,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/cook.png")
      )
   );
   public static final RegistrySupplier<Item> CHEFS_PANTS = registerItem(
      "chefs_pants",
      () -> new TexturedArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/cook.png")),
         Type.LEGGINGS,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/cook.png")
      )
   );
   public static final RegistrySupplier<Item> CHEFS_BOOTS = registerItem(
      "chefs_boots",
      () -> new TexturedArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/cook.png")),
         Type.BOOTS,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/cook.png")
      )
   );
   public static final RegistrySupplier<Item> FLOWER_CROWN = registerItem(
      "flower_crown",
      () -> new TexturedArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/flower_crown.png")),
         Type.HELMET,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/flower_crown.png")
      )
   );
   public static final RegistrySupplier<Item> DRESS = registerItem(
      "dress",
      () -> new DyeableArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/dress.png")),
         Type.CHESTPLATE,
         16744576,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/dress.png")
      )
   );
   public static final RegistrySupplier<Item> SHIRT = registerItem(
      "shirt",
      () -> new TexturedArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/shirt.png")),
         Type.CHESTPLATE,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/shirt.png")
      )
   );
   public static final RegistrySupplier<Item> FORMAL_SHIRT = registerItem(
      "formal_shirt",
      () -> new TexturedArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/formal_shirt.png")),
         Type.CHESTPLATE,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/formal_shirt.png")
      )
   );
   public static final RegistrySupplier<Item> TROUSERS_AND_VEST = registerItem(
      "trousers_and_vest",
      () -> new DyeableArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/suit.png")),
         Type.LEGGINGS,
         3355545,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/suit.png")
      )
   );
   public static final RegistrySupplier<Item> NECKTIE = registerItem(
      "necktie",
      () -> new TexturedArmorItem(
         ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, Candlelight.identifier("textures/models/armor/tie.png")),
         Type.HELMET,
         getSettings().rarity(Rarity.RARE).stacksTo(1),
         Candlelight.identifier("textures/models/armor/tie.png")
      )
   );
   public static final RegistrySupplier<Item> NOTE_PAPER_WRITEABLE = registerItem(
      "note_paper_writeable", () -> new WriteablePaperItem(getSettings().stacksTo(1))
   );
   public static final RegistrySupplier<Item> NOTE_PAPER_WRITTEN = registerItem("note_paper_written", () -> new WrittenPaperItem(getSettingsWithoutTab()));
   public static final RegistrySupplier<Item> LETTER_OPEN = registerItem("letter_open", () -> new LetterItem(getSettings()));
   public static final RegistrySupplier<Item> LETTER_CLOSED = registerItem("letter_closed", () -> new ClosedLetterItem(getSettingsWithoutTab().stacksTo(1)));
   public static final RegistrySupplier<Item> LOVE_LETTER_OPEN = registerItem("love_letter_open", () -> new LetterItem(getSettings()));
   public static final RegistrySupplier<Item> LOVE_LETTER_CLOSED = registerItem("love_letter", () -> new ClosedLetterItem(getSettingsWithoutTab().stacksTo(1)));
   private static final BannerSettings BANNER_SETTINGS = new BannerSettings(
      () -> (BlockEntityType)EntityTypeRegistry.CANDLELIGHT_BANNER_ENTITY.get(),
      () -> (Block)ObjectRegistry.CANDLELIGHT_WALL_BANNER.get(),
      Candlelight.identifier("textures/banner/candlelight_banner.png"),
      "tooltip.candlelight.banner",
      MobEffects.FIRE_RESISTANCE,
      () -> PlatformHelper.bannerGiveEffect() ? PlatformHelper.bannerEffectRadius() : 0,
      () -> PlatformHelper.bannerEffectAmplifier()
   );
   public static final RegistrySupplier<Block> CANDLELIGHT_BANNER = registerWithItem(
      "candlelight_banner",
      () -> new CompletionistBannerBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .strength(1.0F)
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .sound(SoundType.WOOD),
         BANNER_SETTINGS
      )
   );
   public static final RegistrySupplier<Block> CANDLELIGHT_WALL_BANNER = registerWithoutItem(
      "candlelight_wall_banner",
      () -> new CompletionistWallBannerBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .strength(1.0F)
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .sound(SoundType.WOOD),
         BANNER_SETTINGS
      )
   );
   public static final RegistrySupplier<Block> FLOORBOARD = registerWithItem(
      "floorboard", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> DRAWER = registerDrawer("drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> CABINET = registerCabinet("cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> SIDEBOARD = registerWithItem(
      "sideboard",
      () -> new SideBoardBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.5F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> CHAIR = registerWithItem(
      "chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> SOFA = registerWithItem(
      "sofa",
      () -> new SofaBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> TABLE = registerWithItem(
      "table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS), ClothColor.WHITE)
   );
   private static final VoxelShape SHELF_SHAPE = Shapes.or(Shapes.box(0.0, 0.1875, 0.625, 1.0, 0.3125, 1.0), Shapes.box(0.0, 0.125, 0.9375, 1.0, 0.1875, 1.0));
   private static final VoxelShape LAMP_SHAPE = Shapes.or(
      Shapes.box(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875),
      new VoxelShape[]{Shapes.box(0.1875, 0.5, 0.1875, 0.8125, 1.0, 0.8125), Shapes.box(0.4375, 0.3125, 0.4375, 0.5625, 0.9375, 0.5625)}
   );
   private static final VoxelShape HANGING_LAMP_SHAPE = Shapes.or(
      Shapes.box(0.3125, 0.875, 0.3125, 0.6875, 1.0, 0.6875),
      new VoxelShape[]{Shapes.box(0.4375, 0.625, 0.4375, 0.5625, 0.875, 0.5625), Shapes.box(0.1875, 0.125, 0.1875, 0.8125, 0.625, 0.8125)}
   );
   public static final RegistrySupplier<Block> LAMP = registerWithItem(
      "lamp",
      () -> new LampBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(LampBlock.lightWhenOn(15)).sound(SoundType.WOOD),
         LAMP_SHAPE,
         HANGING_LAMP_SHAPE
      )
   );
   public static final RegistrySupplier<Block> SIDE_TABLE = registerWithItem(
      "side_table", () -> new SideTableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> DINNER_BELL = registerWithItem(
      "dinner_bell", () -> new DinnerBellBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion())
   );
   public static final RegistrySupplier<Block> COOKING_POT = registerWithItem(
      "cooking_pot",
      () -> new LargeCookingPotBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion())
   );
   public static final RegistrySupplier<Block> COOKING_PAN = registerWithoutItem(
      "cooking_pan", () -> new CookingPanBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK))
   );
   public static final RegistrySupplier<Item> COOKING_PAN_ITEM = registerItem(
      "cooking_pan", () -> new CookingPanItem((Block)COOKING_PAN.get(), getSettings().attributes(CookingPanItem.createAttributes()))
   );
   public static final RegistrySupplier<Block> TABLE_SET = registerWithoutItem(
      "table_set", () -> new TableSetBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT))
   );
   public static final RegistrySupplier<Item> PLATE = registerItem(
      "plate", () -> new TableSetBlockItem((Block)TABLE_SET.get(), getSettings(), TableSetBlock.PlateType.PLATE)
   );
   public static final RegistrySupplier<Item> BOWL = registerItem(
      "bowl", () -> new TableSetBlockItem((Block)TABLE_SET.get(), getSettings(), TableSetBlock.PlateType.BOWL)
   );
   public static final RegistrySupplier<Block> GLASS_BLOCK = registerWithoutItem(
      "glass", () -> new StackableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noTerrainParticles(), 4)
   );
   public static final RegistrySupplier<Item> GLASS = registerItem("glass", () -> new BlockItem((Block)GLASS_BLOCK.get(), getSettings()));
   public static final RegistrySupplier<Block> WINE_GLASS_BLOCK = registerWithoutItem(
      "wine_glass", () -> new StackableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noTerrainParticles(), 4)
   );
   public static final RegistrySupplier<Item> WINE_GLASS = registerItem("wine_glass", () -> new WineGlassItem((Block)WINE_GLASS_BLOCK.get(), getSettings()));
   public static final RegistrySupplier<Item> FRESH_GARDEN_SALAD = registerItem(
      "fresh_garden_salad",
      () -> new PlaceableEffectFoodItem(
         (Block)FRESH_GARDEN_SALAD_BLOCK.get(),
         getFoodItemSettings(
            PlatformHelper.freshGardenSaladNutrition(),
            (float)PlatformHelper.freshGardenSaladSaturation(),
            (MobEffect)MobEffectRegistry.WELL_SERVED.get(),
            PlatformHelper.freshGardenSaladEffectDuration()
         )
      )
   );
   public static final RegistrySupplier<Item> TOMATO_MOZZARELLA_SALAD = registerItem(
      "tomato_mozzarella_salad",
      () -> new PlaceableEffectFoodItem(
         (Block)TOMATO_MOZZARELLA_BLOCK.get(),
         getFoodItemSettings(
            PlatformHelper.tomatoMozzarellaSaladNutrition(),
            (float)PlatformHelper.tomatoMozzarellaSaladSaturation(),
            (MobEffect)MobEffectRegistry.WELL_SERVED.get(),
            PlatformHelper.tomatoMozzarellaSaladEffectDuration()
         )
      )
   );
   public static final RegistrySupplier<Block> TABLE_SIGN = registerWithoutItem(
      "table_sign", () -> new TableSignBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT))
   );
   public static final RegistrySupplier<Item> TABLE_SIGN_ITEM = registerItem("table_sign", () -> new TableSignItem((Block)TABLE_SIGN.get(), getSettings()));
   public static final RegistrySupplier<Block> PAINTING = registerWithItem(
      "painting", () -> new SmallPaintingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).noCollission())
   );
   public static final RegistrySupplier<Block> HEART = registerWithItem(
      "heart",
      () -> new WallDecorationBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).noCollission(),
         EntityTypeRegistry.WALL_DECORATION,
         Candlelight.identifier("textures/item/heart.png"),
         12
      )
   );
   public static final RegistrySupplier<Block> ROSE = registerWithItem(
      "rose", () -> new BonemealableFlowerBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION))
   );
   public static final RegistrySupplier<Block> POTTED_ROSE = registerWithoutItem(
      "potted_rose",
      () -> new FlowerPotBlock((Block)ROSE.get(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY))
   );
   public static final RegistrySupplier<Block> JEWELRY_BOX = registerWithItem(
      "jewelry_box", () -> new JewelryBoxBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT))
   );
   public static final RegistrySupplier<Block> CHOCOLATE_BOX = registerWithItem(
      "chocolate_box", () -> new EatableBoxBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE))
   );
   public static final RegistrySupplier<Block> TYPEWRITER_IRON = registerWithItem(
      "typewriter_iron",
      () -> new TypewriterBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> TYPEWRITER_GOLD = registerWithItem(
      "typewriter_gold",
      () -> new TypewriterBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> NOTE_PAPER_BLOCK = registerWithoutItem(
      "note_paper", () -> new StackableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DRIED_KELP_BLOCK), 8)
   );
   public static final RegistrySupplier<Item> NOTE_PAPER = registerItem("note_paper", () -> new BlockItem((Block)NOTE_PAPER_BLOCK.get(), getSettings()));
   public static final RegistrySupplier<Block> COBBLESTONE_STOVE = registerWithItem(
      "cobblestone_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> COBBLESTONE_KITCHEN_SINK = registerWithItem(
      "cobblestone_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> COBBLESTONE_COUNTER = registerWithItem(
      "cobblestone_counter",
      () -> new LineConnectingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> OAK_CABINET = registerCabinet("oak_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> OAK_DRAWER = registerDrawer("oak_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> OAK_TABLE = registerWithItem(
      "oak_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> OAK_CHAIR = registerWithItem(
      "oak_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> OAK_SHELF = registerWithItem(
      "oak_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> OAK_BIG_TABLE = registerWithItem(
      "oak_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> SANDSTONE_STOVE = registerWithItem(
      "sandstone_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> SANDSTONE_KITCHEN_SINK = registerWithItem(
      "sandstone_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> SANDSTONE_COUNTER = registerWithItem(
      "sandstone_counter",
      () -> new LineConnectingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> BIRCH_CABINET = registerCabinet("birch_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> BIRCH_DRAWER = registerDrawer("birch_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> BIRCH_TABLE = registerWithItem(
      "birch_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> BIRCH_CHAIR = registerWithItem(
      "birch_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> BIRCH_SHELF = registerWithItem(
      "birch_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> BIRCH_BIG_TABLE = registerWithItem(
      "birch_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> STONE_BRICKS_STOVE = registerWithItem(
      "stone_bricks_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> STONE_BRICKS_KITCHEN_SINK = registerWithItem(
      "stone_bricks_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> STONE_BRICKS_COUNTER = registerWithItem(
      "stone_bricks_counter",
      () -> new LineConnectingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> SPRUCE_CABINET = registerCabinet("spruce_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> SPRUCE_DRAWER = registerDrawer("spruce_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> SPRUCE_TABLE = registerWithItem(
      "spruce_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> SPRUCE_CHAIR = registerWithItem(
      "spruce_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> SPRUCE_SHELF = registerWithItem(
      "spruce_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> SPRUCE_BIG_TABLE = registerWithItem(
      "spruce_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> DEEPSLATE_STOVE = registerWithItem(
      "deepslate_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> DEEPSLATE_KITCHEN_SINK = registerWithItem(
      "deepslate_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> DEEPSLATE_COUNTER = registerWithItem(
      "deepslate_counter", () -> new FacingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> DARK_OAK_CABINET = registerCabinet("dark_oak_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> DARK_OAK_DRAWER = registerDrawer("dark_oak_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> DARK_OAK_TABLE = registerWithItem(
      "dark_oak_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS))
   );
   public static final RegistrySupplier<Block> DARK_OAK_CHAIR = registerWithItem(
      "dark_oak_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> DARK_OAK_SHELF = registerWithItem(
      "dark_oak_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> DARK_OAK_BIG_TABLE = registerWithItem(
      "dark_oak_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> GRANITE_STOVE = registerWithItem(
      "granite_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> GRANITE_KITCHEN_SINK = registerWithItem(
      "granite_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> GRANITE_COUNTER = registerWithItem(
      "granite_counter", () -> new LineConnectingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> ACACIA_CABINET = registerCabinet("acacia_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> ACACIA_DRAWER = registerDrawer("acacia_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> ACACIA_TABLE = registerWithItem(
      "acacia_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS))
   );
   public static final RegistrySupplier<Block> ACACIA_CHAIR = registerWithItem(
      "acacia_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> ACACIA_SHELF = registerWithItem(
      "acacia_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> ACACIA_BIG_TABLE = registerWithItem(
      "acacia_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> END_STOVE = registerWithItem(
      "end_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> END_KITCHEN_SINK = registerWithItem(
      "end_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> END_COUNTER = registerWithItem(
      "end_counter", () -> new LineConnectingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> JUNGLE_CABINET = registerCabinet("jungle_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> JUNGLE_DRAWER = registerDrawer("jungle_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> JUNGLE_TABLE = registerWithItem(
      "jungle_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> JUNGLE_CHAIR = registerWithItem(
      "jungle_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> JUNGLE_SHELF = registerWithItem(
      "jungle_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> JUNGLE_BIG_TABLE = registerWithItem(
      "jungle_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> MUD_STOVE = registerWithItem(
      "mud_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> MUD_KITCHEN_SINK = registerWithItem(
      "mud_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> MUD_COUNTER = registerWithItem(
      "mud_counter", () -> new LineConnectingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> MANGROVE_CABINET = registerCabinet("mangrove_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> MANGROVE_DRAWER = registerDrawer("mangrove_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> MANGROVE_TABLE = registerWithItem(
      "mangrove_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> MANGROVE_CHAIR = registerWithItem(
      "mangrove_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> MANGROVE_SHELF = registerWithItem(
      "mangrove_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> MANGROVE_BIG_TABLE = registerWithItem(
      "mangrove_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> QUARTZ_STOVE = registerWithItem(
      "quartz_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> QUARTZ_KITCHEN_SINK = registerWithItem(
      "quartz_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> QUARTZ_COUNTER = registerWithItem(
      "quartz_counter", () -> new FacingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> WARPED_CABINET = registerCabinet("warped_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> WARPED_DRAWER = registerDrawer("warped_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> WARPED_TABLE = registerWithItem(
      "warped_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> WARPED_CHAIR = registerWithItem(
      "warped_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> WARPED_SHELF = registerWithItem(
      "warped_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> WARPED_BIG_TABLE = registerWithItem(
      "warped_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> RED_NETHER_BRICKS_STOVE = registerWithItem(
      "red_nether_bricks_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> RED_NETHER_BRICKS_KITCHEN_SINK = registerWithItem(
      "red_nether_bricks_kitchen_sink",
      () -> new SinkBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> RED_NETHER_BRICKS_COUNTER = registerWithItem(
      "red_nether_bricks_counter",
      () -> new LineConnectingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion())
   );
   public static final RegistrySupplier<Block> CRIMSON_CABINET = registerCabinet("crimson_cabinet", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> CRIMSON_DRAWER = registerDrawer("crimson_drawer", Blocks.OAK_PLANKS);
   public static final RegistrySupplier<Block> CRIMSON_TABLE = registerWithItem(
      "crimson_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final RegistrySupplier<Block> CRIMSON_CHAIR = registerWithItem(
      "crimson_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> CRIMSON_SHELF = registerWithItem(
      "crimson_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> CRIMSON_BIG_TABLE = registerWithItem(
      "crimson_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0F, 2.0F).pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> BASALT_STOVE = registerWithItem(
      "basalt_stove",
      () -> new StoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 12 : 0)
      )
   );
   public static final RegistrySupplier<Block> BASALT_KITCHEN_SINK = registerWithItem(
      "basalt_kitchen_sink", () -> new SinkBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT).noOcclusion())
   );
   public static final RegistrySupplier<Block> BASALT_COUNTER = registerWithItem(
      "basalt_counter", () -> new LineConnectingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT).noOcclusion())
   );
   public static final RegistrySupplier<Block> CHERRY_CABINET = registerCabinet("cherry_cabinet", Blocks.CHERRY_PLANKS);
   public static final RegistrySupplier<Block> CHERRY_DRAWER = registerDrawer("cherry_drawer", Blocks.CHERRY_PLANKS);
   public static final RegistrySupplier<Block> CHERRY_TABLE = registerWithItem(
      "cherry_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS))
   );
   public static final RegistrySupplier<Block> CHERRY_CHAIR = registerWithItem(
      "cherry_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> CHERRY_SHELF = registerWithItem(
      "cherry_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS)
            .strength(2.0F, 3.0F)
            .sound(SoundType.WOOD)
            .noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> CHERRY_BIG_TABLE = registerWithItem(
      "cherry_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS)
            .strength(2.0F, 2.0F)
            .pushReaction(PushReaction.IGNORE)
      )
   );
   public static final RegistrySupplier<Block> BAMBOO_STOVE = registerWithItem(
      "bamboo_stove",
      () -> new BambooStoveBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS).lightLevel(s -> s.getValue(StoveBlock.LIT) ? 10 : 0)
      )
   );
   public static final RegistrySupplier<Block> BAMBOO_KITCHEN_SINK = registerWithItem(
      "bamboo_kitchen_sink",
      () -> new SinkBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS).noOcclusion())
   );
   public static final RegistrySupplier<Block> BAMBOO_COUNTER = registerWithItem(
      "bamboo_counter", () -> new FacingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS).noOcclusion())
   );
   public static final RegistrySupplier<Block> BAMBOO_CABINET = registerCabinet("bamboo_cabinet", Blocks.BAMBOO_PLANKS);
   public static final RegistrySupplier<Block> BAMBOO_DRAWER = registerDrawer("bamboo_drawer", Blocks.BAMBOO_PLANKS);
   public static final RegistrySupplier<Block> BAMBOO_TABLE = registerWithItem(
      "bamboo_table", () -> new TableBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS))
   );
   public static final RegistrySupplier<Block> BAMBOO_CHAIR = registerWithItem(
      "bamboo_chair",
      () -> new ChairBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)
      )
   );
   public static final RegistrySupplier<Block> BAMBOO_SHELF = registerWithItem(
      "bamboo_shelf",
      () -> shelf(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS)
            .strength(2.0F, 3.0F)
            .sound(SoundType.WOOD)
            .noOcclusion()
      )
   );
   public static final RegistrySupplier<Block> BAMBOO_BIG_TABLE = registerWithItem(
      "bamboo_big_table",
      () -> new BigTableBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS)
            .strength(2.0F, 2.0F)
            .pushReaction(PushReaction.IGNORE)
      )
   );

   public static void init() {
      ITEMS.register();
      BLOCKS.register();
   }

   private static WallShelfBlock shelf(net.minecraft.world.level.block.state.BlockBehaviour.Properties properties) {
      return new WallShelfBlock(
         properties, EntityTypeRegistry.STORAGE_BLOCK_ENTITY, StorageTypeRegistry.SHELF, stack -> stack.getItem() instanceof BlockItem, SHELF_SHAPE
      );
   }

   private static Properties getSettings(Consumer<Properties> consumer) {
      Properties settings = new Properties();
      consumer.accept(settings);
      return settings;
   }

   private static Properties getSettingsWithoutTab(Consumer<Properties> consumer) {
      Properties settings = new Properties();
      consumer.accept(settings);
      return settings;
   }

   private static Properties getSettings() {
      return getSettings(settings -> {});
   }

   private static Properties getSettingsWithoutTab() {
      return getSettingsWithoutTab(settings -> {});
   }

   private static Properties getFoodItemSettings(int nutrition, float saturationModifier, MobEffect effect, int duration) {
      return getFoodItemSettings(nutrition, saturationModifier, effect, duration, true, false);
   }

   private static Properties getFoodItemSettings(int nutrition, float saturationModifier, MobEffect effect, int duration, boolean alwaysEat, boolean fast) {
      return getSettings().food(createFood(nutrition, saturationModifier, effect, duration, alwaysEat, fast));
   }

   private static FoodProperties createFood(int nutrition, float saturationModifier, MobEffect effect, int duration, boolean alwaysEat, boolean fast) {
      Builder food = new Builder().nutrition(nutrition).saturationModifier(saturationModifier);
      if (alwaysEat) {
         food.alwaysEdible();
      }

      if (fast) {
         food.fast();
      }

      if (effect != null) {
         food.effect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), duration, 0, false, false), 1.0F);
      }

      return food.build();
   }

   private static RegistrySupplier<Block> registerCabinet(String name, Block planks) {
      return registerWithItem(
         name,
         () -> new CabinetBlock(
            net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(planks).strength(2.0F, 3.0F).sound(SoundType.WOOD),
            EntityTypeRegistry.CABINET_BLOCK_ENTITY,
            (SoundEvent)SoundEventRegistry.CABINET_OPEN.get(),
            (SoundEvent)SoundEventRegistry.CABINET_CLOSE.get()
         )
      );
   }

   private static RegistrySupplier<Block> registerDrawer(String name, Block planks) {
      return registerWithItem(
         name,
         () -> new CabinetBlock(
            net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(planks).strength(2.0F, 3.0F).sound(SoundType.WOOD),
            EntityTypeRegistry.CABINET_BLOCK_ENTITY,
            (SoundEvent)SoundEventRegistry.DRAWER_OPEN.get(),
            (SoundEvent)SoundEventRegistry.DRAWER_CLOSE.get()
         )
      );
   }

   public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
      return RegistryUtil.registerWithItem(BLOCKS, BLOCK_REGISTRAR, ITEMS, ITEM_REGISTRAR, Candlelight.identifier(name), block);
   }

   public static <T extends Block> RegistrySupplier<T> registerWithoutItem(String path, Supplier<T> block) {
      return RegistryUtil.registerWithoutItem(BLOCKS, BLOCK_REGISTRAR, Candlelight.identifier(path), block);
   }

   public static <T extends Item> RegistrySupplier<T> registerItem(String path, Supplier<T> itemSupplier) {
      return RegistryUtil.registerItem(ITEMS, ITEM_REGISTRAR, Candlelight.identifier(path), itemSupplier);
   }
}
