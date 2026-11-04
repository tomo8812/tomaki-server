package net.caffeinemc.mods.lithium.common.services;

public interface PlatformMappingInformation {
   PlatformMappingInformation INSTANCE = Services.load(PlatformMappingInformation.class);

   String mapMethodName(String var1, String var2, String var3, String var4, String var5);
}
