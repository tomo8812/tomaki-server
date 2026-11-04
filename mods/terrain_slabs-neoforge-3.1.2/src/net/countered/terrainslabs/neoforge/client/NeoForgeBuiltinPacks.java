package net.countered.terrainslabs.neoforge.client;

import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

public class NeoForgeBuiltinPacks {
   @SubscribeEvent
   public static void addPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES) {
         event.addRepositorySource(consumer -> {
            Pack pack = createBuiltinPack();
            if (pack != null) {
               consumer.accept(pack);
            }
         });
      }
   }

   private static Pack createBuiltinPack() {
      String id = "terrain_slabs:better_grass_slabs";
      IModFile modFile = ModList.get().getModFileById("terrain_slabs").getFile();
      final Path packPath = modFile.findResource(new String[]{"resourcepacks/better_grass_slabs"});
      if (packPath == null) {
         return null;
      }

      PackLocationInfo location = new PackLocationInfo(id, Component.literal("Better Grass Slabs"), PackSource.BUILT_IN, Optional.empty());
      ResourcesSupplier supplier = new ResourcesSupplier() {
         public PackResources openPrimary(PackLocationInfo location) {
            return new PathPackResources(location, packPath);
         }

         public PackResources openFull(PackLocationInfo location, Metadata metadata) {
            return this.openPrimary(location);
         }
      };
      PackSelectionConfig config = new PackSelectionConfig(false, Position.TOP, false);
      return Pack.readMetaAndCreate(location, supplier, PackType.CLIENT_RESOURCES, config);
   }
}
