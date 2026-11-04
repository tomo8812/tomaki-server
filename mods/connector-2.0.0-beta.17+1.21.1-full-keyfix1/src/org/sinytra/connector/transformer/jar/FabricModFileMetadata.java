package org.sinytra.connector.transformer.jar;

import java.util.Collection;
import java.util.Set;
import java.util.jar.Attributes;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;

public record FabricModFileMetadata(
   LoaderModMetadata modMetadata,
   Collection<String> visibleMixinConfigs,
   Collection<String> mixinConfigs,
   Set<String> refmaps,
   Set<String> mixinPackages,
   Set<String> mixinClasses,
   Attributes manifestAttributes,
   boolean containsAT,
   boolean generated
) {
}
