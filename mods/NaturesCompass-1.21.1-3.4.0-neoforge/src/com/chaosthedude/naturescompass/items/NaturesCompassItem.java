package com.chaosthedude.naturescompass.items;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.chaosthedude.naturescompass.config.ConfigHandler;
import com.chaosthedude.naturescompass.gui.GuiWrapper;
import com.chaosthedude.naturescompass.network.SyncPacket;
import com.chaosthedude.naturescompass.util.BiomeSearchWorker;
import com.chaosthedude.naturescompass.util.BiomeUtils;
import com.chaosthedude.naturescompass.util.CompassState;
import com.chaosthedude.naturescompass.util.ItemUtils;
import com.chaosthedude.naturescompass.util.PlayerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class NaturesCompassItem extends Item {
   public static final String NAME = "naturescompass";
   private BiomeSearchWorker worker;

   public NaturesCompassItem() {
      super(new Properties().stacksTo(1));
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      if (!player.isCrouching()) {
         if (this.isBroken(player.getItemInHand(hand))) {
            return new InteractionResultHolder(InteractionResult.PASS, player.getItemInHand(hand));
         }

         if (level.isClientSide()) {
            ItemStack stack = ItemUtils.getHeldNatureCompass(player);
            GuiWrapper.openGUI(level, player, stack);
         } else {
            ServerLevel serverLevel = (ServerLevel)level;
            ServerPlayer serverPlayer = (ServerPlayer)player;
            boolean canTeleport = (Boolean)ConfigHandler.GENERAL.allowTeleport.get() && PlayerUtils.canTeleport(serverPlayer.getServer(), player);
            int maxNextSearches = (Integer)ConfigHandler.GENERAL.maxNextSearches.get();
            boolean hasInfiniteXp = player.hasInfiniteMaterials();
            List<ResourceLocation> allowedBiomeKeys = BiomeUtils.getAllowedBiomeKeys(level);
            Map<ResourceLocation, Integer> xpLevels = BiomeUtils.getXpLevelsForAllowedBiomes(allowedBiomeKeys);
            PacketDistributor.sendToPlayer(
               serverPlayer,
               new SyncPacket(
                  canTeleport, maxNextSearches, hasInfiniteXp, allowedBiomeKeys, xpLevels, BiomeUtils.getGeneratingDimensionsForAllowedBiomes(serverLevel)
               ),
               new CustomPacketPayload[0]
            );
         }
      } else {
         if (this.worker != null) {
            this.worker.stop();
            this.worker = null;
         }

         this.setState(player.getItemInHand(hand), null, CompassState.INACTIVE, player);
      }

      return new InteractionResultHolder(InteractionResult.PASS, player.getItemInHand(hand));
   }

   public boolean isBarVisible(ItemStack stack) {
      int max = (Integer)ConfigHandler.GENERAL.compassDurability.get();
      if (max > 0) {
         int damage = (Integer)stack.getOrDefault(NaturesCompass.DAMAGE, 0);
         return damage > 0;
      } else {
         return false;
      }
   }

   public int getBarWidth(ItemStack stack) {
      int max = (Integer)ConfigHandler.GENERAL.compassDurability.get();
      if (max > 0) {
         int damage = (Integer)stack.getOrDefault(NaturesCompass.DAMAGE, 0);
         return Math.round(Math.max(0.0F, Math.min(13.0F, 13.0F * (1.0F - (float)damage / max))));
      } else {
         return 13;
      }
   }

   public int getBarColor(ItemStack stack) {
      int max = (Integer)ConfigHandler.GENERAL.compassDurability.get();
      int damage = (Integer)stack.getOrDefault(NaturesCompass.DAMAGE, 0);
      float f = max > 0 ? (float)damage / max : 0.0F;
      return Mth.hsvToRgb(Math.max(0.0F, (1.0F - f) / 3.0F), 1.0F, 1.0F);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return this.getState(oldStack) == this.getState(newStack) ? false : super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
   }

   public void searchForBiome(ServerLevel level, Player player, ResourceLocation biomeKey, BlockPos pos, ItemStack stack) {
      if (!this.isBroken(stack)) {
         this.setSearching(stack, biomeKey, player);
         stack.set(NaturesCompass.SAMPLES, 0);
         stack.set(NaturesCompass.SEARCH_RADIUS, 0);
         stack.remove(NaturesCompass.PREV_POS);
         if (this.worker != null) {
            this.worker.stop();
         }

         List<BlockPos> prevPos = new ArrayList<>();
         this.worker = new BiomeSearchWorker(level, player, stack, biomeKey, pos, prevPos);
         this.worker.start();
         int xpLevels = BiomeUtils.getXpLevelsForBiome(biomeKey);
         if (!player.hasInfiniteMaterials() && xpLevels > 0) {
            player.giveExperienceLevels(-xpLevels);
         }
      }
   }

   public void searchForNextBiome(ServerLevel level, Player player, BlockPos pos, ItemStack stack) {
      if (!this.isBroken(stack)) {
         List<BlockPos> prevPos = (List<BlockPos>)stack.getOrDefault(NaturesCompass.PREV_POS, null);
         String biomeKeyStr = stack.has(NaturesCompass.BIOME_ID) ? (String)stack.get(NaturesCompass.BIOME_ID) : null;
         if (prevPos != null && biomeKeyStr != null) {
            ResourceLocation biomeKey = ResourceLocation.parse(biomeKeyStr);
            this.setSearching(stack, biomeKey, player);
            stack.set(NaturesCompass.SAMPLES, 0);
            stack.set(NaturesCompass.SEARCH_RADIUS, 0);
            if (this.worker != null) {
               this.worker.stop();
            }

            this.worker = new BiomeSearchWorker(level, player, stack, biomeKey, pos, prevPos);
            this.worker.start();
            int xpLevels = BiomeUtils.getXpLevelsForBiome(biomeKey);
            if (!player.hasInfiniteMaterials() && xpLevels > 0) {
               player.giveExperienceLevels(-xpLevels);
            }
         }
      }
   }

   public void succeed(ItemStack stack, Player player, int x, int z, List<BlockPos> prevPos, int samples, boolean displayCoordinates) {
      this.setFound(stack, x, z, samples, player);
      this.setDisplayCoordinates(stack, displayCoordinates);
      stack.set(NaturesCompass.PREV_POS, prevPos);
      this.damageCompass(stack);
      this.worker = null;
   }

   public void fail(ItemStack stack, Player player, int radius, int samples) {
      this.setNotFound(stack, player, radius, samples);
      this.worker = null;
   }

   private void damageCompass(ItemStack stack) {
      int max = (Integer)ConfigHandler.GENERAL.compassDurability.get();
      if (max > 0) {
         int damage = (Integer)stack.getOrDefault(NaturesCompass.DAMAGE, 0) + 1;
         stack.set(NaturesCompass.DAMAGE, damage);
      }
   }

   public boolean isBroken(ItemStack stack) {
      int max = (Integer)ConfigHandler.GENERAL.compassDurability.get();
      return max > 0 && (Integer)stack.getOrDefault(NaturesCompass.DAMAGE, 0) >= max;
   }

   public boolean isActive(ItemStack stack) {
      return stack.getItem() == NaturesCompass.naturesCompass ? this.getState(stack) != CompassState.INACTIVE : false;
   }

   public void setSearching(ItemStack stack, ResourceLocation biomeKey, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.BIOME_ID, biomeKey.toString());
         stack.set(NaturesCompass.COMPASS_STATE, CompassState.SEARCHING.getID());
      }
   }

   public void setFound(ItemStack stack, int x, int z, int samples, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.COMPASS_STATE, CompassState.FOUND.getID());
         stack.set(NaturesCompass.FOUND_X, x);
         stack.set(NaturesCompass.FOUND_Z, z);
         stack.set(NaturesCompass.SAMPLES, samples);
      }
   }

   public void setNotFound(ItemStack stack, Player player, int searchRadius, int samples) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.COMPASS_STATE, CompassState.NOT_FOUND.getID());
         stack.set(NaturesCompass.SEARCH_RADIUS, searchRadius);
         stack.set(NaturesCompass.SAMPLES, samples);
      }
   }

   public void setInactive(ItemStack stack, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.COMPASS_STATE, CompassState.INACTIVE.getID());
      }
   }

   public void setState(ItemStack stack, BlockPos pos, CompassState state, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.COMPASS_STATE, state.getID());
      }
   }

   public void setFoundBiomeX(ItemStack stack, int x, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.FOUND_X, x);
      }
   }

   public void setFoundBiomeZ(ItemStack stack, int z, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.FOUND_Z, z);
      }
   }

   public void setBiomeKey(ItemStack stack, ResourceLocation biomeKey, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.BIOME_ID, biomeKey.toString());
      }
   }

   public void setSearchRadius(ItemStack stack, int searchRadius, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.SEARCH_RADIUS, searchRadius);
      }
   }

   public void setSamples(ItemStack stack, int samples, Player player) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.SAMPLES, samples);
      }
   }

   public CompassState getState(ItemStack stack) {
      return stack.getItem() == NaturesCompass.naturesCompass && stack.has(NaturesCompass.COMPASS_STATE)
         ? CompassState.fromID((Integer)stack.get(NaturesCompass.COMPASS_STATE))
         : null;
   }

   public void setDisplayCoordinates(ItemStack stack, boolean displayPosition) {
      if (stack.getItem() == NaturesCompass.naturesCompass) {
         stack.set(NaturesCompass.DISPLAY_COORDS, displayPosition);
      }
   }

   public int getFoundBiomeX(ItemStack stack) {
      return stack.getItem() == NaturesCompass.naturesCompass && stack.has(NaturesCompass.FOUND_X) ? (Integer)stack.get(NaturesCompass.FOUND_X) : 0;
   }

   public int getFoundBiomeZ(ItemStack stack) {
      return stack.getItem() == NaturesCompass.naturesCompass && stack.has(NaturesCompass.FOUND_Z) ? (Integer)stack.get(NaturesCompass.FOUND_Z) : 0;
   }

   public ResourceLocation getBiomeKey(ItemStack stack) {
      return stack.getItem() == NaturesCompass.naturesCompass && stack.has(NaturesCompass.BIOME_ID)
         ? ResourceLocation.parse((String)stack.get(NaturesCompass.BIOME_ID))
         : ResourceLocation.fromNamespaceAndPath("", "");
   }

   public int getSearchRadius(ItemStack stack) {
      return stack.getItem() == NaturesCompass.naturesCompass && stack.has(NaturesCompass.SEARCH_RADIUS)
         ? (Integer)stack.get(NaturesCompass.SEARCH_RADIUS)
         : -1;
   }

   public int getSamples(ItemStack stack) {
      return stack.getItem() == NaturesCompass.naturesCompass && stack.has(NaturesCompass.SAMPLES) ? (Integer)stack.get(NaturesCompass.SAMPLES) : -1;
   }

   public int getDistanceToBiome(Player player, ItemStack stack) {
      return BiomeUtils.getDistanceToBiome(player, this.getFoundBiomeX(stack), this.getFoundBiomeZ(stack));
   }

   public boolean shouldDisplayCoordinates(ItemStack stack) {
      return stack.getItem() == NaturesCompass.naturesCompass && stack.has(NaturesCompass.DISPLAY_COORDS)
         ? (Boolean)stack.get(NaturesCompass.DISPLAY_COORDS)
         : true;
   }
}
