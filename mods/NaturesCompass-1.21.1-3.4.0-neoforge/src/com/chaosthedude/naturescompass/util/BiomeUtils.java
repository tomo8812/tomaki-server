package com.chaosthedude.naturescompass.util;

import com.chaosthedude.naturescompass.config.ConfigHandler;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.apache.commons.lang3.text.WordUtils;

public class BiomeUtils {
   public static Optional<? extends Registry<Biome>> getBiomeRegistry(Level level) {
      return level.registryAccess().registry(Registries.BIOME);
   }

   public static Optional<ResourceLocation> getKeyForBiome(Level level, Biome biome) {
      return getBiomeRegistry(level).isPresent() ? Optional.of(getBiomeRegistry(level).get().getKey(biome)) : Optional.empty();
   }

   public static Optional<Biome> getBiomeForKey(Level level, ResourceLocation key) {
      return getBiomeRegistry(level).isPresent() ? getBiomeRegistry(level).get().getOptional(key) : Optional.empty();
   }

   public static List<ResourceLocation> getAllowedBiomeKeys(Level level) {
      List<ResourceLocation> biomeKeys = new ArrayList<>();
      if (getBiomeRegistry(level).isPresent()) {
         for (Entry<ResourceKey<Biome>, Biome> entry : getBiomeRegistry(level).get().entrySet()) {
            Biome biome = entry.getValue();
            if (biome != null) {
               Optional<ResourceLocation> optionalBiomeKey = getKeyForBiome(level, biome);
               if (biome != null
                  && optionalBiomeKey.isPresent()
                  && !biomeKeyIsBlacklisted(level, optionalBiomeKey.get())
                  && !biomeKeyIsHidden(level, optionalBiomeKey.get())) {
                  biomeKeys.add(optionalBiomeKey.get());
               }
            }
         }
      }

      return biomeKeys;
   }

   public static int getXpLevelsForBiome(ResourceLocation biomeKey) {
      int xpLevels = (Integer)ConfigHandler.GENERAL.defaultXpLevels.get();
      Map<String, Integer> xpLevelOverrides = parseXpLevelOverridesConfig();

      for (String biomeRegex : xpLevelOverrides.keySet()) {
         if (biomeKey.toString().matches(convertToRegex(biomeRegex))) {
            xpLevels = xpLevelOverrides.get(biomeRegex);
            if (xpLevels > 3) {
               xpLevels = 3;
            }
            break;
         }
      }

      return xpLevels;
   }

   public static Map<String, Integer> parseXpLevelOverridesConfig() {
      List<String> xpLevelOverrides = (List<String>)ConfigHandler.GENERAL.perBiomeXpLevels.get();
      Map<String, Integer> parsedOverrides = new HashMap<>();

      for (String override : xpLevelOverrides) {
         String[] split = override.split(",");
         if (split.length == 2) {
            String biomeRegex = split[0];
            String xpLevelsStr = split[1];

            try {
               int xpLevels = Integer.valueOf(xpLevelsStr);
               parsedOverrides.put(biomeRegex, xpLevels);
            } catch (NumberFormatException e) {
            }
         }
      }

      return parsedOverrides;
   }

   public static Map<ResourceLocation, Integer> getXpLevelsForAllowedBiomes(List<ResourceLocation> allowedBiomes) {
      Map<ResourceLocation, Integer> xpLevels = new HashMap<>();

      for (ResourceLocation biomeKey : allowedBiomes) {
         int levels = getXpLevelsForBiome(biomeKey);
         xpLevels.put(biomeKey, levels);
      }

      return xpLevels;
   }

   public static List<ResourceLocation> getGeneratingDimensionKeys(ServerLevel serverLevel, Biome biome) {
      List<ResourceLocation> dimensions = new ArrayList<>();
      Registry<Biome> biomeRegistry = (Registry<Biome>)getBiomeRegistry(serverLevel).get();

      for (ServerLevel level : serverLevel.getServer().getAllLevels()) {
         Set<Holder<Biome>> biomeSet = level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes();
         Holder<Biome> biomeHolder = (Holder<Biome>)biomeRegistry.getHolder((ResourceKey)biomeRegistry.getResourceKey(biome).get()).get();
         if (biomeSet.contains(biomeHolder)) {
            dimensions.add(level.dimension().location());
         }
      }

      return dimensions;
   }

   public static ListMultimap<ResourceLocation, ResourceLocation> getGeneratingDimensionsForAllowedBiomes(ServerLevel serverLevel) {
      ListMultimap<ResourceLocation, ResourceLocation> dimensionsForAllowedStructures = ArrayListMultimap.create();

      for (ResourceLocation biomeKey : getAllowedBiomeKeys(serverLevel)) {
         Optional<Biome> optionalBiome = getBiomeForKey(serverLevel, biomeKey);
         if (optionalBiome.isPresent()) {
            dimensionsForAllowedStructures.putAll(biomeKey, getGeneratingDimensionKeys(serverLevel, optionalBiome.get()));
         }
      }

      return dimensionsForAllowedStructures;
   }

   public static int getBiomeSize(Level world) {
      return 4;
   }

   public static int getDistanceToBiome(Player player, int biomeX, int biomeZ) {
      return getDistanceToBiome(player.blockPosition(), biomeX, biomeZ);
   }

   public static int getDistanceToBiome(BlockPos startPos, int biomeX, int biomeZ) {
      return (int)Mth.sqrt((float)startPos.distSqr(new BlockPos(biomeX, startPos.getY(), biomeZ)));
   }

   @OnlyIn(Dist.CLIENT)
   public static String getBiomeTags(Level level, Biome biome) {
      List<String> tagPathsToIgnore = List.of("is_overworld");
      Set<String> biomeCategories = new TreeSet<>();
      if (getBiomeRegistry(level).isPresent()) {
         Registry<Biome> biomeRegistry = (Registry<Biome>)getBiomeRegistry(level).get();
         if (biomeRegistry.getResourceKey(biome).isPresent() && biomeRegistry.getHolder((ResourceKey)biomeRegistry.getResourceKey(biome).get()).isPresent()) {
            Holder<Biome> biomeHolder = (Holder<Biome>)biomeRegistry.getHolder((ResourceKey)biomeRegistry.getResourceKey(biome).get()).get();

            for (TagKey<Biome> tag : biomeHolder.tags().filter(tagx -> tagx.location().getPath().startsWith("is_")).collect(Collectors.toList())) {
               if (!tagPathsToIgnore.contains(tag.location().getPath())) {
                  String fixedPath = tag.location().getPath().replaceFirst("is_", "");
                  if (fixedPath.contains("/")) {
                     fixedPath = fixedPath.substring(0, fixedPath.indexOf("/"));
                  }

                  String biomeKey = Util.makeDescriptionId("biome", ResourceLocation.fromNamespaceAndPath(tag.location().getNamespace(), fixedPath));
                  String translatedBiomeKey = I18n.get(biomeKey, new Object[0]);
                  if (!biomeKey.equals(translatedBiomeKey)) {
                     return translatedBiomeKey;
                  }

                  String categoryKey = Util.makeDescriptionId("category", ResourceLocation.fromNamespaceAndPath(tag.location().getNamespace(), fixedPath));
                  String translatedCategoryKey = I18n.get(categoryKey, new Object[0]);
                  if (!categoryKey.equals(translatedCategoryKey)) {
                     return translatedCategoryKey;
                  }

                  biomeCategories.add(WordUtils.capitalize(fixedPath.replace('_', ' ')));
               }
            }
         }
      }

      if (biomeCategories.isEmpty()) {
         biomeCategories.add(I18n.get("string.naturescompass.none", new Object[0]));
      }

      return String.join(", ", biomeCategories);
   }

   @OnlyIn(Dist.CLIENT)
   public static String getBiomeNameForDisplay(Level level, ResourceLocation biome) {
      return getBiomeForKey(level, biome).isPresent() ? getBiomeNameForDisplay(level, getBiomeForKey(level, biome).get()) : "";
   }

   @OnlyIn(Dist.CLIENT)
   public static String getBiomeNameForDisplay(Level level, Biome biome) {
      if (biome != null) {
         if ((Boolean)ConfigHandler.CLIENT.fixBiomeNames.get()) {
            String original = getBiomeName(level, biome);
            String fixed = "";
            char pre = ' ';

            for (int i = 0; i < original.length(); i++) {
               char c = original.charAt(i);
               if (Character.isUpperCase(c) && Character.isLowerCase(pre) && Character.isAlphabetic(pre)) {
                  fixed = fixed + " ";
               }

               fixed = fixed + c;
               pre = c;
            }

            return fixed;
         } else {
            return getBiomeName(level, biome);
         }
      } else {
         return "";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static String getBiomeName(Level level, Biome biome) {
      return getKeyForBiome(level, biome).isPresent() ? I18n.get(Util.makeDescriptionId("biome", getKeyForBiome(level, biome).get()), new Object[0]) : "";
   }

   @OnlyIn(Dist.CLIENT)
   public static String getBiomeName(Level level, ResourceLocation key) {
      return getBiomeForKey(level, key).isPresent() ? getBiomeName(level, getBiomeForKey(level, key).get()) : "";
   }

   @OnlyIn(Dist.CLIENT)
   public static String getBiomeSource(Level level, Biome biome) {
      if (getKeyForBiome(level, biome).isEmpty()) {
         return "";
      }

      String modid = getKeyForBiome(level, biome).get().getNamespace();
      if (modid.equals("minecraft")) {
         return "Minecraft";
      }

      Optional<? extends ModContainer> sourceContainer = ModList.get().getModContainerById(modid);
      return sourceContainer.isPresent() ? sourceContainer.get().getModInfo().getDisplayName() : modid;
   }

   @OnlyIn(Dist.CLIENT)
   private static String getDimensionName(ResourceLocation dimensionKey) {
      String name = I18n.get(Util.makeDescriptionId("dimension", dimensionKey), new Object[0]);
      if (name.equals(Util.makeDescriptionId("dimension", dimensionKey))) {
         name = dimensionKey.toString();
         if (name.contains(":")) {
            name = name.substring(name.indexOf(":") + 1);
         }

         name = WordUtils.capitalize(name.replace('_', ' '));
      }

      return name;
   }

   @OnlyIn(Dist.CLIENT)
   public static String dimensionKeysToString(List<ResourceLocation> dimensions) {
      Set<String> dimensionNames = new HashSet<>();
      dimensions.forEach(key -> dimensionNames.add(getDimensionName(key)));
      return String.join(", ", dimensionNames);
   }

   public static boolean biomeKeyIsBlacklisted(Level level, ResourceLocation biomeKey) {
      for (String key : (List)ConfigHandler.GENERAL.biomeBlacklist.get()) {
         if (biomeKey.toString().matches(convertToRegex(key))) {
            return true;
         }
      }

      return false;
   }

   public static boolean biomeKeyIsHidden(Level level, ResourceLocation biomeKey) {
      if (getBiomeRegistry(level).isPresent() && getBiomeForKey(level, biomeKey).isPresent()) {
         Registry<Biome> biomeRegistry = (Registry<Biome>)getBiomeRegistry(level).get();
         Biome biome = getBiomeForKey(level, biomeKey).get();
         if (biomeRegistry.getResourceKey(biome).isPresent() && biomeRegistry.getHolder((ResourceKey)biomeRegistry.getResourceKey(biome).get()).isPresent()) {
            Holder<Biome> biomeHolder = (Holder<Biome>)biomeRegistry.getHolder((ResourceKey)biomeRegistry.getResourceKey(biome).get()).get();
            return biomeHolder.tags().anyMatch(tag -> tag.location().toString().equals("c:hidden_from_locator_selection"));
         }
      }

      return false;
   }

   private static String convertToRegex(String glob) {
      String regex = "^";

      for (char i = 0; i < glob.length(); i++) {
         char c = glob.charAt(i);
         if (c == '*') {
            regex = regex + ".*";
         } else if (c == '?') {
            regex = regex + ".";
         } else if (c == '.') {
            regex = regex + "\\.";
         } else {
            regex = regex + c;
         }
      }

      return regex + "$";
   }
}
