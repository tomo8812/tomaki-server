package com.chaosthedude.naturescompass;

import com.chaosthedude.naturescompass.config.ConfigHandler;
import com.chaosthedude.naturescompass.items.NaturesCompassItem;
import com.chaosthedude.naturescompass.network.SearchForNextPacket;
import com.chaosthedude.naturescompass.network.SearchPacket;
import com.chaosthedude.naturescompass.network.SyncPacket;
import com.chaosthedude.naturescompass.network.TeleportPacket;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent.Nodes;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("naturescompass")
public class NaturesCompass {
   public static final String MODID = "naturescompass";
   public static final PermissionNode<Boolean> TELEPORT_PERMISSION = new PermissionNode(
      "naturescompass", "naturescompass.teleport", PermissionTypes.BOOLEAN, (player, playerUUID, context) -> false, new PermissionDynamicContextKey[0]
   );
   public static final Logger LOGGER = LogManager.getLogger("naturescompass");
   public static NaturesCompassItem naturesCompass;
   public static final DataComponentType<String> BIOME_ID = DataComponentType.builder()
      .persistent(Codec.STRING)
      .networkSynchronized(ByteBufCodecs.STRING_UTF8)
      .build();
   public static final DataComponentType<Integer> COMPASS_STATE = DataComponentType.builder()
      .persistent(Codec.INT)
      .networkSynchronized(ByteBufCodecs.VAR_INT)
      .build();
   public static final DataComponentType<Integer> FOUND_X = DataComponentType.builder()
      .persistent(Codec.INT)
      .networkSynchronized(ByteBufCodecs.VAR_INT)
      .build();
   public static final DataComponentType<Integer> FOUND_Z = DataComponentType.builder()
      .persistent(Codec.INT)
      .networkSynchronized(ByteBufCodecs.VAR_INT)
      .build();
   public static final DataComponentType<Integer> SEARCH_RADIUS = DataComponentType.builder()
      .persistent(Codec.INT)
      .networkSynchronized(ByteBufCodecs.VAR_INT)
      .build();
   public static final DataComponentType<Integer> SAMPLES = DataComponentType.builder()
      .persistent(Codec.INT)
      .networkSynchronized(ByteBufCodecs.VAR_INT)
      .build();
   public static final DataComponentType<Boolean> DISPLAY_COORDS = DataComponentType.builder()
      .persistent(Codec.BOOL)
      .networkSynchronized(ByteBufCodecs.BOOL)
      .build();
   public static final DataComponentType<List<BlockPos>> PREV_POS = DataComponentType.builder()
      .persistent(BlockPos.CODEC.listOf().xmap(ArrayList::new, list -> list))
      .networkSynchronized(ByteBufCodecs.collection(ArrayList::new, BlockPos.STREAM_CODEC))
      .build();
   public static final DataComponentType<Integer> DAMAGE = DataComponentType.builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
   public static boolean synced;
   public static boolean canTeleport;
   public static int maxNextSearches;
   public static boolean infiniteXp;
   public static List<ResourceLocation> allowedBiomes;
   public static Map<ResourceLocation, Integer> xpLevelsForAllowedBiomes;
   public static ListMultimap<ResourceLocation, ResourceLocation> dimensionKeysForAllowedBiomeKeys;
   public static NaturesCompass instance;

   public NaturesCompass(ModContainer modContainer) {
      instance = this;
      modContainer.getEventBus().addListener(this::preInit);
      modContainer.getEventBus().addListener(this::buildCreativeTabContents);
      modContainer.getEventBus().addListener(this::registerPayloads);
      modContainer.registerConfig(Type.COMMON, ConfigHandler.GENERAL_SPEC);
      modContainer.registerConfig(Type.CLIENT, ConfigHandler.CLIENT_SPEC);
      NeoForge.EVENT_BUS.register(this);
   }

   private void preInit(FMLCommonSetupEvent event) {
      allowedBiomes = new ArrayList<>();
      xpLevelsForAllowedBiomes = new HashMap<>();
      dimensionKeysForAllowedBiomeKeys = ArrayListMultimap.create();
   }

   private void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
         event.accept(new ItemStack(naturesCompass));
      }
   }

   private void registerPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("naturescompass");
      registrar.playToServer(SearchPacket.TYPE, SearchPacket.CODEC, SearchPacket::handle);
      registrar.playToServer(SearchForNextPacket.TYPE, SearchForNextPacket.CODEC, SearchForNextPacket::handle);
      registrar.playToServer(TeleportPacket.TYPE, TeleportPacket.CODEC, TeleportPacket::handle);
      registrar.playToClient(SyncPacket.TYPE, SyncPacket.CODEC, SyncPacket::handle);
   }

   @SubscribeEvent
   public void registerNodes(Nodes event) {
      event.addNodes(new PermissionNode[]{TELEPORT_PERMISSION});
   }
}
