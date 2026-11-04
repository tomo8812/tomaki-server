package org.sinytra.connector.service;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.SecureJar.ModuleDataProvider;
import cpw.mods.jarhandling.SecureJar.Status;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.net.URI;
import java.nio.file.Path;
import java.security.CodeSigner;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class DummyVirtualJar implements SecureJar {
   private final ModuleDataProvider moduleDataProvider;

   public DummyVirtualJar(String name, Set<String> packages, Supplier<Manifest> manifest, Function<String, Optional<InputStream>> fileLookup) {
      this(name, name, packages, manifest, fileLookup);
   }

   public DummyVirtualJar(String name, String moduleName, Set<String> packages, Supplier<Manifest> manifest, Function<String, Optional<InputStream>> fileLookup) {
      this.moduleDataProvider = new DummyVirtualJar.VirtualModuleDataProvider(name, moduleName, packages, manifest, fileLookup);
   }

   public ModuleDataProvider moduleDataProvider() {
      return this.moduleDataProvider;
   }

   public Path getPrimaryPath() {
      return Path.of(this.moduleDataProvider().uri());
   }

   public CodeSigner[] getManifestSigners() {
      return null;
   }

   public Status verifyPath(Path path) {
      return Status.NONE;
   }

   public Status getFileStatus(String name) {
      return Status.NONE;
   }

   public Attributes getTrustedManifestEntries(String name) {
      return null;
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

   public static class VirtualModuleDataProvider implements ModuleDataProvider {
      private final String name;
      private final String moduleName;
      private final Set<String> packages;
      private final Supplier<Manifest> manifestFactory;
      private final Function<String, Optional<InputStream>> fileLookup;
      private ModuleDescriptor descriptor;
      private Manifest manifest;

      public VirtualModuleDataProvider(
         String name, String moduleName, Set<String> packages, Supplier<Manifest> manifest, Function<String, Optional<InputStream>> fileLookup
      ) {
         this.name = name;
         this.moduleName = moduleName;
         this.packages = packages;
         this.manifestFactory = manifest;
         this.fileLookup = fileLookup;
      }

      public String name() {
         return this.name;
      }

      public ModuleDescriptor descriptor() {
         if (this.descriptor == null) {
            this.descriptor = ModuleDescriptor.newAutomaticModule(this.moduleName).packages(this.packages).build();
         }

         return this.descriptor;
      }

      public URI uri() {
         return (URI)LambdaExceptionUtils.uncheck(() -> new URI("file:///~nonexistent"));
      }

      public Optional<URI> findFile(String name) {
         return Optional.empty();
      }

      public Optional<InputStream> open(String name) {
         return this.fileLookup.apply(name);
      }

      public Manifest getManifest() {
         if (this.manifest == null) {
            this.manifest = this.manifestFactory.get();
         }

         return this.manifest;
      }

      public CodeSigner[] verifyAndGetSigners(String cname, byte[] bytes) {
         return null;
      }
   }
}
