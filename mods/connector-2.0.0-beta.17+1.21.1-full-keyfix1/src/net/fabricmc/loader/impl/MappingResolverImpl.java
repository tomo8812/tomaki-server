package net.fabricmc.loader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import net.fabricmc.loader.api.MappingResolver;
import net.neoforged.fml.loading.FMLEnvironment;
import reloc.net.minecraftforge.srgutils.IMappingFile;
import reloc.net.minecraftforge.srgutils.INamedMappingFile;

public class MappingResolverImpl implements MappingResolver {
   private static final String MAPPINGS_RESOURCE = "/mappings.tsrg";
   private static final String FML_NAMESPACE = "mojang";
   private final INamedMappingFile mappings;

   public MappingResolverImpl() {
      URL path = this.getClass().getResource("/mappings.tsrg");
      if (path == null && !FMLEnvironment.production) {
         throw new RuntimeException("Missing mappings file");
      }

      try (InputStream is = path.openStream()) {
         this.mappings = INamedMappingFile.load(is);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public IMappingFile getCurrentMap(String from) {
      return this.getMap(from, "mojang");
   }

   public IMappingFile getMap(String from, String to) {
      return this.mappings.getMap(from, to);
   }

   public String mapDescriptor(String namespace, String descriptor) {
      return this.getCurrentMap(namespace).remapDescriptor(descriptor);
   }

   @Override
   public Collection<String> getNamespaces() {
      return this.mappings.getNames();
   }

   @Override
   public String getCurrentRuntimeNamespace() {
      return "mojang";
   }

   @Override
   public String mapClassName(String namespace, String className) {
      return toBinaryName(this.getCurrentMap(namespace).remapClass(toInternalName(className)));
   }

   @Override
   public String unmapClassName(String targetNamespace, String className) {
      return toBinaryName(this.getMap("mojang", targetNamespace).remapClass(toInternalName(className)));
   }

   @Override
   public String mapFieldName(String namespace, String owner, String name, String descriptor) {
      return Optional.ofNullable(this.getCurrentMap(namespace).getClass(toInternalName(owner))).map(cls -> cls.remapField(name)).orElse(name);
   }

   @Override
   public String mapMethodName(String namespace, String owner, String name, String descriptor) {
      return Optional.ofNullable(this.getCurrentMap(namespace).getClass(toInternalName(owner))).map(cls -> cls.remapMethod(name, descriptor)).orElse(name);
   }

   private static String toBinaryName(String className) {
      return className.replace('/', '.');
   }

   private static String toInternalName(String className) {
      return className.replace('.', '/');
   }
}
