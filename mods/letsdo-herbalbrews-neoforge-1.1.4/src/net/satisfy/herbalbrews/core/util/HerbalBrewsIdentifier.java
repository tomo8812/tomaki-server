package net.satisfy.herbalbrews.core.util;

import net.minecraft.resources.ResourceLocation;

public class HerbalBrewsIdentifier {
   public static ResourceLocation identifier(String path) {
      return ResourceLocation.fromNamespaceAndPath("herbalbrews", path);
   }
}
