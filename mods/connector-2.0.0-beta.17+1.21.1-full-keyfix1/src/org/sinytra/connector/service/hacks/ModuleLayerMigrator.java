package org.sinytra.connector.service.hacks;

import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.SecureJar.ModuleDataProvider;
import cpw.mods.jarhandling.SecureJar.Status;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import cpw.mods.modlauncher.api.IModuleLayerManager.Layer;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.lang.module.ModuleDescriptor.Builder;
import java.net.URI;
import java.nio.file.Path;
import java.security.CodeSigner;
import java.util.Optional;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.sinytra.connector.util.ConnectorUtil;
import org.slf4j.Logger;

public class ModuleLayerMigrator {
   private static final Class<?> JAR_MODULE_REF_CLASS = (Class<?>)LambdaExceptionUtils.uncheck(
      () -> Class.forName("cpw.mods.cl.JarModuleFinder$JarModuleReference")
   );
   private static final VarHandle REF_MODULE_PROVIDER_FIELD = (VarHandle)LambdaExceptionUtils.uncheck(
      () -> ConnectorUtil.TRUSTED_LOOKUP.findVarHandle(JAR_MODULE_REF_CLASS, "jar", ModuleDataProvider.class)
   );
   private static final VarHandle DESCRIPTOR_PACKAGES_FIELD = (VarHandle)LambdaExceptionUtils.uncheck(
      () -> ConnectorUtil.TRUSTED_LOOKUP.findVarHandle(ModuleDescriptor.class, "packages", Set.class)
   );
   private static final MethodHandle IMPL_ADD_READS = (MethodHandle)LambdaExceptionUtils.uncheck(
      () -> ConnectorUtil.TRUSTED_LOOKUP.findVirtual(Module.class, "implAddReads", MethodType.methodType(void.class, Module.class))
   );
   private static final Logger LOGGER = LogUtils.getLogger();

   public static SecureJar moveModule(String moduleName) {
      try {
         LOGGER.debug("Attempting to make module {} transformable", moduleName);
         ModuleLayer layer = (ModuleLayer)((IModuleLayerManager)Launcher.INSTANCE.findLayerManager().orElseThrow()).getLayer(Layer.BOOT).orElseThrow();
         ResolvedModule module = layer.configuration()
            .findModule(moduleName)
            .orElseThrow(() -> new RuntimeException("Module %s not found".formatted(moduleName)));
         Module actualModule = layer.findModule(moduleName).orElseThrow(() -> new RuntimeException("Module %s not found".formatted(moduleName)));
         ModuleReference reference = module.reference();
         if (!JAR_MODULE_REF_CLASS.isInstance(reference)) {
            throw new RuntimeException("Module %s does not contain a jar module reference".formatted(moduleName));
         }

         ModuleDataProvider originalProvider = (ModuleDataProvider)REF_MODULE_PROVIDER_FIELD.get((ModuleReference)reference);
         ModuleDataProvider wrappedProvider = new ModuleLayerMigrator.EmptyModuleDataProvider(originalProvider.name());
         REF_MODULE_PROVIDER_FIELD.set((ModuleReference)reference, (ModuleDataProvider)wrappedProvider);
         ModuleDataProvider provider = new ModuleLayerMigrator.ModuleDataProviderWrapper(originalProvider, "connector$" + moduleName);
         ModuleDescriptor desc = actualModule.getDescriptor();
         DESCRIPTOR_PACKAGES_FIELD.set((ModuleDescriptor)desc, (Set)Set.of());
         LOGGER.info("Successfully made module {} transformable", moduleName);
         return new ModuleLayerMigrator.SimpleSecureJar(provider);
      } catch (Throwable t) {
         LOGGER.error("Error making module {} transformable", moduleName, t);
         return null;
      }
   }

   public static void addReads(Set<Module> sources) {
      try {
         Module ourModule = ModuleLayerMigrator.class.getModule();

         for (Module source : sources) {
            IMPL_ADD_READS.invoke((Module)source, (Module)ourModule);
         }
      } catch (Throwable t) {
         LOGGER.error("Error adding reads to modules", t);
      }
   }

   private static class EmptyModuleDataProvider implements ModuleDataProvider {
      private final String name;
      private ModuleDescriptor descriptor;

      public EmptyModuleDataProvider(String name) {
         this.name = name;
      }

      public ModuleDescriptor descriptor() {
         if (this.descriptor == null) {
            this.descriptor = ModuleDescriptor.newAutomaticModule(this.name()).build();
         }

         return this.descriptor;
      }

      public String name() {
         return this.name;
      }

      public URI uri() {
         return (URI)LambdaExceptionUtils.uncheck(() -> new URI("file:///~nonexistent"));
      }

      public Optional<URI> findFile(String name) {
         return Optional.empty();
      }

      public Optional<InputStream> open(String name) {
         return Optional.empty();
      }

      public Manifest getManifest() {
         return new Manifest();
      }

      public CodeSigner[] verifyAndGetSigners(String cname, byte[] bytes) {
         return new CodeSigner[0];
      }
   }

   private static class ModuleDataProviderWrapper implements ModuleDataProvider {
      private final ModuleDataProvider provider;
      private final String name;
      private final ModuleDescriptor descriptor;

      public ModuleDataProviderWrapper(ModuleDataProvider provider, String name) {
         this.provider = provider;
         this.name = name;
         ModuleDescriptor desc = this.provider.descriptor();
         Builder builder = ModuleDescriptor.newModule(this.name, desc.modifiers());
         builder.packages(desc.packages());
         if (!desc.isAutomatic()) {
            desc.version().ifPresent(builder::version);
            desc.requires().forEach(builder::requires);
            desc.exports().forEach(builder::exports);
            desc.opens().forEach(builder::opens);
            desc.uses().forEach(builder::uses);
            desc.provides().forEach(builder::provides);
         }

         this.descriptor = builder.build();
      }

      public String name() {
         return this.name;
      }

      public URI uri() {
         return this.provider.uri();
      }

      public ModuleDescriptor descriptor() {
         return this.descriptor;
      }

      public Optional<URI> findFile(String name) {
         return this.provider.findFile(name);
      }

      public Optional<InputStream> open(String name) {
         return this.provider.open(name);
      }

      public Manifest getManifest() {
         return this.provider.getManifest();
      }

      public CodeSigner[] verifyAndGetSigners(String cname, byte[] bytes) {
         return this.provider.verifyAndGetSigners(cname, bytes);
      }
   }

   private record SimpleSecureJar(ModuleDataProvider moduleDataProvider) implements SecureJar {
      public Path getPrimaryPath() {
         return Path.of(this.moduleDataProvider().uri());
      }

      public CodeSigner[] getManifestSigners() {
         return new CodeSigner[0];
      }

      public Status verifyPath(Path path) {
         return Status.NONE;
      }

      public Status getFileStatus(String name) {
         return Status.NONE;
      }

      public Attributes getTrustedManifestEntries(String name) {
         return new Attributes();
      }

      public boolean hasSecurityData() {
         return false;
      }

      public void close() {
      }

      public String name() {
         return this.moduleDataProvider().name();
      }

      public Path getPath(String first, String... rest) {
         return this.getPrimaryPath();
      }

      public Path getRootPath() {
         return this.getPrimaryPath();
      }
   }
}
