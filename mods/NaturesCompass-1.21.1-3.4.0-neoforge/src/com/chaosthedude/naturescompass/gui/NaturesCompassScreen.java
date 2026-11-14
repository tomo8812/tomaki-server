package com.chaosthedude.naturescompass.gui;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.chaosthedude.naturescompass.items.NaturesCompassItem;
import com.chaosthedude.naturescompass.network.SearchForNextPacket;
import com.chaosthedude.naturescompass.network.SearchPacket;
import com.chaosthedude.naturescompass.network.TeleportPacket;
import com.chaosthedude.naturescompass.sorting.ISorting;
import com.chaosthedude.naturescompass.sorting.NameSorting;
import com.chaosthedude.naturescompass.util.BiomeUtils;
import com.chaosthedude.naturescompass.util.CompassState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class NaturesCompassScreen extends Screen {
   public Level level;
   private Player player;
   private List<Biome> allowedBiomes;
   private List<Biome> biomesMatchingSearch;
   private ResourceLocation foundBiomeKey;
   private ItemStack stack;
   private NaturesCompassItem natureCompass;
   private Button searchForBiomeButton;
   private Button searchForNextButton;
   private Button teleportButton;
   private Button cancelButton;
   private Button sortByButton;
   private TransparentTextField searchTextField;
   private BiomeSearchList selectionList;
   private ISorting<?> sortingCategory;

   public NaturesCompassScreen(Level level, Player player, ItemStack stack, NaturesCompassItem natureCompass, List<ResourceLocation> allowedBiomes) {
      super(Component.translatable("string.naturescompass.selectBiome"));
      this.level = level;
      this.player = player;
      this.stack = stack;
      this.natureCompass = natureCompass;
      this.allowedBiomes = new ArrayList<>();
      this.loadAllowedBiomes(allowedBiomes);
      this.sortingCategory = new NameSorting();
      this.biomesMatchingSearch = new ArrayList<>(this.allowedBiomes);
      if (natureCompass.getState(stack) == CompassState.FOUND) {
         String foundBiomeKeyStr = stack.has(NaturesCompass.BIOME_ID) ? (String)stack.get(NaturesCompass.BIOME_ID) : null;
         if (foundBiomeKeyStr != null) {
            this.foundBiomeKey = ResourceLocation.parse(foundBiomeKeyStr);
         }
      }
   }

   public boolean mouseScrolled(double par1, double par2, double par3, double par4) {
      return this.selectionList.mouseScrolled(par1, par2, par3, par4);
   }

   protected void init() {
      this.setupWidgets();
   }

   public void tick() {
      ResourceLocation selectedId = this.selectionList != null && this.selectionList.hasSelection()
         ? ((BiomeSearchEntry)this.selectionList.getSelected()).getBiomeId()
         : null;
      this.searchForNextButton.active = this.teleportButton.active = selectedId != null && selectedId.equals(this.foundBiomeKey);
      this.searchForBiomeButton.active = this.selectionList.hasSelection();
      if (NaturesCompass.synced) {
         this.teleportButton.visible = NaturesCompass.canTeleport;
         this.searchForNextButton.visible = NaturesCompass.maxNextSearches > 0;
         if (this.searchForNextButton.visible) {
            this.sortByButton.setPosition(10, 100);
         } else {
            this.sortByButton.setPosition(10, 65);
         }

         this.removeWidget(this.selectionList);
         this.loadAllowedBiomes(NaturesCompass.allowedBiomes);
         this.biomesMatchingSearch = new ArrayList<>(this.allowedBiomes);
         this.selectionList = new BiomeSearchList(this, this.minecraft, this.player, this.foundBiomeKey, 130, 40, this.width - 140, this.height - 50, 50);
         this.addRenderableWidget(this.selectionList);
         NaturesCompass.synced = false;
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      guiGraphics.drawCenteredString(this.font, I18n.get("string.naturescompass.selectBiome", new Object[0]), 65, 15, 16777215);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      boolean ret = super.keyPressed(keyCode, scanCode, modifiers);
      if (this.searchTextField.isFocused()) {
         this.processSearchTerm();
         return true;
      } else {
         return ret;
      }
   }

   public boolean charTyped(char typedChar, int keyCode) {
      boolean ret = super.charTyped(typedChar, keyCode);
      if (this.searchTextField.isFocused()) {
         this.processSearchTerm();
         return true;
      } else {
         return ret;
      }
   }

   public void searchForBiome(ResourceLocation biomeId) {
      if (biomeId != null) {
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         PacketDistributor.sendToServer(new SearchPacket(biomeId, this.player.blockPosition()), new CustomPacketPayload[0]);
      }

      this.minecraft.setScreen(null);
   }

   public void searchForNextBiome() {
      PacketDistributor.sendToServer(new SearchForNextPacket(), new CustomPacketPayload[0]);
      this.minecraft.setScreen(null);
   }

   public void teleport() {
      PacketDistributor.sendToServer(new TeleportPacket(), new CustomPacketPayload[0]);
      this.minecraft.setScreen(null);
   }

   public ISorting<?> getSortingCategory() {
      return this.sortingCategory;
   }

   public void processSearchTerm() {
      this.biomesMatchingSearch = new ArrayList<>();
      String searchTerm = this.searchTextField.getValue().toLowerCase();

      for (Biome biome : this.allowedBiomes) {
         if (searchTerm.startsWith("$")) {
            if (BiomeUtils.getBiomeTags(this.level, biome).toLowerCase().contains(searchTerm.substring(1))) {
               this.biomesMatchingSearch.add(biome);
            }
         } else if (searchTerm.startsWith("@")) {
            if (BiomeUtils.getBiomeSource(this.level, biome).toLowerCase().contains(searchTerm.substring(1))) {
               this.biomesMatchingSearch.add(biome);
            }
         } else if (BiomeUtils.getBiomeNameForDisplay(this.level, biome).toLowerCase().contains(searchTerm)) {
            this.biomesMatchingSearch.add(biome);
         }
      }

      this.selectionList.refreshList(true);
   }

   public List<Biome> sortBiomes() {
      List<Biome> biomes = this.biomesMatchingSearch;
      Collections.sort(biomes, new NameSorting());
      Collections.sort(biomes, (Comparator<? super Biome>)this.sortingCategory);
      return biomes;
   }

   private void setupWidgets() {
      this.clearWidgets();
      this.searchForBiomeButton = (Button)this.addRenderableWidget(
         new TransparentButton(10, 40, 110, 20, Component.translatable("string.naturescompass.search"), onPress -> {
            if (this.selectionList.hasSelection()) {
               this.searchForBiome(((BiomeSearchEntry)this.selectionList.getSelected()).getBiomeId());
            }
         })
      );
      this.searchForBiomeButton.active = false;
      this.searchForNextButton = (Button)this.addRenderableWidget(
         new TransparentButton(10, 65, 110, 20, Component.translatable("string.naturescompass.searchForNext"), onPress -> this.searchForNextBiome())
      );
      this.searchForNextButton.visible = NaturesCompass.maxNextSearches > 0;
      this.searchForNextButton.active = false;
      this.sortByButton = (Button)this.addRenderableWidget(
         new TransparentButton(
            10,
            100,
            110,
            20,
            Component.literal(I18n.get("string.naturescompass.sortBy", new Object[0]) + ": " + this.sortingCategory.getLocalizedName()),
            onPress -> {
               this.sortingCategory = this.sortingCategory.next();
               this.sortByButton
                  .setMessage(Component.literal(I18n.get("string.naturescompass.sortBy", new Object[0]) + ": " + this.sortingCategory.getLocalizedName()));
               this.selectionList.refreshList(true);
            }
         )
      );
      if (!this.searchForNextButton.visible) {
         this.sortByButton.setPosition(10, 65);
      }

      this.teleportButton = (Button)this.addRenderableWidget(
         new TransparentButton(this.width - 120, 10, 110, 20, Component.translatable("string.naturescompass.teleport"), onPress -> this.teleport())
      );
      this.teleportButton.visible = NaturesCompass.canTeleport;
      this.teleportButton.active = false;
      this.cancelButton = (Button)this.addRenderableWidget(
         new TransparentButton(10, this.height - 30, 110, 20, Component.translatable("gui.cancel"), onPress -> this.minecraft.setScreen(null))
      );
      this.searchTextField = (TransparentTextField)this.addRenderableWidget(
         new TransparentTextField(this.font, 130, 10, 140, 20, Component.translatable("string.naturescompass.search"))
      );
      this.selectionList = new BiomeSearchList(this, this.minecraft, this.player, this.foundBiomeKey, 130, 40, this.width - 140, this.height - 50, 50);
      this.addRenderableWidget(this.selectionList);
   }

   private void loadAllowedBiomes(List<ResourceLocation> allowedBiomeKeys) {
      this.allowedBiomes = new ArrayList<>();

      for (ResourceLocation biomeKey : allowedBiomeKeys) {
         Optional<Biome> optionalBiome = BiomeUtils.getBiomeForKey(this.level, biomeKey);
         if (optionalBiome.isPresent()) {
            this.allowedBiomes.add(optionalBiome.get());
         }
      }
   }
}
