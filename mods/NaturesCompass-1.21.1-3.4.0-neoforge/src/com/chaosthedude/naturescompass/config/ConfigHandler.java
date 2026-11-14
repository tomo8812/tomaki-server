package com.chaosthedude.naturescompass.config;

import com.chaosthedude.naturescompass.client.OverlaySide;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class ConfigHandler {
   private static final Builder GENERAL_BUILDER = new Builder();
   private static final Builder CLIENT_BUILDER = new Builder();
   public static final ConfigHandler.General GENERAL = new ConfigHandler.General(GENERAL_BUILDER);
   public static final ConfigHandler.Client CLIENT = new ConfigHandler.Client(CLIENT_BUILDER);
   public static final ModConfigSpec GENERAL_SPEC = GENERAL_BUILDER.build();
   public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

   public static class Client {
      public final BooleanValue displayWithChatOpen;
      public final BooleanValue fixBiomeNames;
      public final EnumValue<OverlaySide> overlaySide;
      public final IntValue overlayLineOffset;

      Client(Builder builder) {
         builder.push("Client");
         String desc = "Displays Nature's Compass information even while chat is open.";
         this.displayWithChatOpen = builder.comment(desc).define("displayWithChatOpen", true);
         desc = "Fixes biome names by adding missing spaces. Ex: ForestHills becomes Forest Hills";
         this.fixBiomeNames = builder.comment(desc).define("fixBiomeNames", true);
         desc = "The line offset for information rendered on the HUD.";
         this.overlayLineOffset = builder.comment(desc).defineInRange("overlayLineOffset", 1, 0, 50);
         desc = "The side for information rendered on the HUD. Ex: LEFT, RIGHT";
         this.overlaySide = builder.comment(desc).defineEnum("overlaySide", OverlaySide.LEFT);
         builder.pop();
      }
   }

   public static class General {
      public final BooleanValue allowTeleport;
      public final IntValue maxNextSearches;
      public final BooleanValue displayCoordinates;
      public final IntValue radiusModifier;
      public final IntValue sampleSpaceModifier;
      public final ConfigValue<List<String>> biomeBlacklist;
      public final IntValue defaultXpLevels;
      public final ConfigValue<List<String>> perBiomeXpLevels;
      public final IntValue maxSamples;
      public final IntValue compassDurability;

      General(Builder builder) {
         builder.push("General");
         String desc = "Allows a player to teleport to a located biome when in creative mode, opped, or in cheat mode.";
         this.allowTeleport = builder.comment(desc).define("allowTeleport", true);
         desc = "The maximum number of times a player can search for the next instance of a located biome. Set to 0 to disable searching for additional biome instances and make the compass always locate the nearest biome.";
         this.maxNextSearches = builder.comment(desc).defineInRange("maxNextSearches", 25, 0, 10000);
         desc = "Allows players to view the precise coordinates and distance of a located structure on the HUD, rather than relying on the direction the compass is pointing.";
         this.displayCoordinates = builder.comment(desc).define("displayCoordinates", true);
         desc = "biomeSize * radiusModifier = maxSearchRadius. Raising this value will increase search accuracy but will potentially make the process more resource .";
         this.radiusModifier = builder.comment(desc).defineInRange("radiusModifier", 2500, 0, 1000000);
         desc = "biomeSize * sampleSpaceModifier = sampleSpace. Lowering this value will increase search accuracy but will make the process more resource intensive.";
         this.sampleSpaceModifier = builder.comment(desc).defineInRange("sampleSpaceModifier", 16, 0, 1000000);
         desc = "A list of biomes that the compass will not be able to search for, specified by resource location. The wildcard character * can be used to match any number of characters, and ? can be used to match one character. Ex: [\"minecraft:savanna\", \"minecraft:desert\", \"minecraft:*ocean*\"]";
         this.biomeBlacklist = builder.comment(desc).define("biomeBlacklist", new ArrayList());
         desc = "The default number of XP levels consumed when searching for a biome. Individual biomes can be configured via perBiomeXpLevels.";
         this.defaultXpLevels = builder.comment(desc).defineInRange("defaultXpLevels", 0, 0, 3);
         desc = "A list of per-biome XP level costs that override defaultXpLevels, specified as comma-separated \"biome_id,num_levels\" pairs. Biomes not listed here use defaultXpLevels. Max of 3 levels. The wildcard character * can be used to match any number of characters, and ? can be used to match one character. Ex: [\"minecraft:deep_dark,3\", \"minecraft:end*,2\", \"minecraft:*caves,3\"]";
         this.perBiomeXpLevels = builder.comment(desc).define("perBiomeXpLevels", new ArrayList());
         desc = "The maximum number of samples to be taken when searching for a biome.";
         this.maxSamples = builder.comment(desc).defineInRange("maxSamples", 50000, 0, 1000000);
         desc = "The number of successful biome searches before the compass breaks and must be repaired. Set to 0 to disable durability.";
         this.compassDurability = builder.comment(desc).defineInRange("compassDurability", 0, 0, 10000);
         builder.pop();
      }
   }
}
