package net.fabricmc.loader.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.fabricmc.loader.impl.discovery.BuiltinMetadataWrapper;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;
import net.fabricmc.loader.impl.metadata.ModOriginImpl;
import net.neoforged.neoforgespi.language.IModInfo;

public class ModContainerImpl extends net.fabricmc.loader.ModContainer {
   private final IModInfo modInfo;
   private final LoaderModMetadata metadata;
   private final ModOrigin origin;
   private final Collection<String> childModIds;

   public ModContainerImpl(IModInfo modInfo) {
      this(
         modInfo,
         (LoaderModMetadata)Optional.ofNullable((LoaderModMetadata)modInfo.getOwningFile().getFileProperties().get("metadata"))
            .orElseGet(() -> new BuiltinMetadataWrapper(new FMLModMetadata(modInfo)))
      );
   }

   public ModContainerImpl(IModInfo modInfo, LoaderModMetadata metadata) {
      this.modInfo = modInfo;
      this.metadata = metadata;
      this.origin = new ModOriginImpl(List.of(this.modInfo.getOwningFile().getFile().getFilePath()));
      this.childModIds = modInfo.getOwningFile()
         .getMods()
         .stream()
         .filter(other -> other != modInfo)
         .<String>map(IModInfo::getDisplayName)
         .collect(Collectors.toSet());
   }

   public LoaderModMetadata getMetadata() {
      return this.metadata;
   }

   @Override
   public ModOrigin getOrigin() {
      return this.origin;
   }

   @Override
   public List<Path> getCodeSourcePaths() {
      return this.getRootPaths();
   }

   @Override
   public Path getRootPath() {
      return this.modInfo.getOwningFile().getFile().findResource(new String[]{"/"});
   }

   @Override
   public List<Path> getRootPaths() {
      return List.of(this.getRootPath());
   }

   @Override
   public Path getPath(String file) {
      return this.modInfo.getOwningFile().getFile().findResource(new String[]{file});
   }

   @Override
   public Optional<ModContainer> getContainingMod() {
      return Optional.empty();
   }

   @Override
   public Collection<ModContainer> getContainedMods() {
      if (this.childModIds.isEmpty()) {
         return Collections.emptyList();
      }

      List<ModContainer> ret = new ArrayList<>(this.childModIds.size());

      for (String id : this.childModIds) {
         FabricLoaderImpl.INSTANCE.getModContainer(id).ifPresent(ret::add);
      }

      return ret;
   }

   @Deprecated
   public LoaderModMetadata getInfo() {
      return this.getMetadata();
   }

   @Override
   public String toString() {
      return String.format("%s %s", this.modInfo.getModId(), this.modInfo.getVersion());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Deprecated
   @Override
   public net.fabricmc.loader.metadata.LoaderModMetadata getInfo() {
      return this.getInfo();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public ModMetadata getMetadata() {
      return this.getMetadata();
   }
}
