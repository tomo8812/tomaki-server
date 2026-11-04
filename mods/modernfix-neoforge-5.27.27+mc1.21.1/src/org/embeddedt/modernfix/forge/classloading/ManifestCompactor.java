package org.embeddedt.modernfix.forge.classloading;

import cpw.mods.jarhandling.impl.Jar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;

public class ManifestCompactor {
   public static void compactManifests() {
      for (ModFileInfo mfi : LoadingModList.get().getModFiles()) {
         if (mfi.getFile().getSecureJar() instanceof Jar jar
            && (jar.getManifestSigners() == null || !mfi.getMods().stream().noneMatch(m -> m.getModId().equals("minecraft")))) {
            Manifest manifest = jar.moduleDataProvider().getManifest();
            if (manifest != null) {
               Map<String, Attributes> entries = manifest.getEntries();
               HashSet<String> entryKeys = new HashSet<>(entries.keySet());
               Set<Name> digests = Set.of(new Name("SHA-256-Digest"), new Name("SHA-384-Digest"));
               entryKeys.forEach(
                  key -> entries.compute(
                     key, (k, attrs) -> (Attributes)(attrs != null && attrs.keySet().stream().anyMatch(n -> n != null && !digests.contains(n)) ? attrs : null)
                  )
               );
            }
         }
      }
   }
}
